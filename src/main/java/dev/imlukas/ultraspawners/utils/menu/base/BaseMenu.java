package dev.imlukas.ultraspawners.utils.menu.base;

import dev.imlukas.ultraspawners.utils.item.ItemUtil;
import dev.imlukas.ultraspawners.utils.menu.concurrency.MainThreadExecutor;
import dev.imlukas.ultraspawners.utils.menu.element.MenuElement;
import dev.imlukas.ultraspawners.utils.menu.element.Renderable;
import dev.imlukas.ultraspawners.utils.text.Placeholder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class BaseMenu implements InventoryHolder {

    private final UUID destinationPlayerId;
    private final List<Renderable> renderables = new ArrayList<>();
    private final Map<Integer, MenuElement> elements = new HashMap<>();
    private final Inventory inventory;
    private final List<Runnable> closeTasks = new ArrayList<>();

    @Getter
    private boolean allowRemoveItems = false;
    private boolean allowInputItems = false;


    public BaseMenu(UUID playerId, String title, int rows) {
        this.inventory = Bukkit.createInventory(this, rows * 9, title);
        this.destinationPlayerId = playerId;
    }


    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void open() {
        if (!Bukkit.isPrimaryThread()) {
            MainThreadExecutor.INSTANCE.execute(this::open);
            return;
        }

        Player player = getPlayer();

        if (player == null) {
            return;
        }
        forceUpdate();
        player.openInventory(inventory);
    }

    public void setItemPlaceholders(Placeholder<Player>... placeholders) {
        setItemPlaceholders(List.of(placeholders));
    }

    public void setItemPlaceholders(Collection<Placeholder<Player>> placeholders) {
        for (Renderable renderable : renderables) {
            renderable.setItemPlaceholders(placeholders);
        }

        elements.values().forEach(element -> element.setItemPlaceholders(placeholders));
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(destinationPlayerId);
    }

    public void addRenderable(Renderable... renderable) {
        renderables.addAll(List.of(renderable));
    }

    public void forceUpdate() {
        Player player = getPlayer();

        if (player == null) {
            return;
        }

        for (Renderable renderable : renderables) {
            if (renderable.isActive()) {
                renderable.forceUpdate();
            }
        }

        for (Map.Entry<Integer, MenuElement> entry : elements.entrySet()) {
            int slot = entry.getKey();
            MenuElement element = entry.getValue();

            ItemStack item = element.getDisplayItem().clone();

            ItemUtil.replacePlaceholder(item, player, element.getItemPlaceholders());
            inventory.setItem(slot, item);
        }
    }

    public void setElement(int slot, MenuElement element) {
        elements.put(slot, element);
    }

    public void onClose(Runnable task) {
        closeTasks.add(task);
    }

    public boolean canRemoveItems() {
        return allowRemoveItems;
    }

    public boolean canInputItems() {
        return allowInputItems;
    }

    public void setAllowRemoveItems(boolean allowRemoveItems) {
        this.allowRemoveItems = allowRemoveItems;
    }

    public void setAllowInputItems(boolean allowInputItems) {
        this.allowInputItems = allowInputItems;
    }

    public void handleDrag(InventoryDragEvent event) {
        ItemStack cursor = event.getOldCursor();

        if (!cursor.getType().isAir() && !canInputItems()) {
            event.setCancelled(true);
        }
    }

    public void handleClick(InventoryClickEvent event) {
        ItemStack cursor = event.getCursor();
        ItemStack current = event.getCurrentItem();

        if (current != null && event.isShiftClick()) {
            event.setCancelled(true);
        }

        if (!cursor.getType().isAir() && !canInputItems()) {
            event.setCancelled(true);
        }

        int slot = event.getRawSlot();

        if (slot < 0 || slot >= inventory.getSize()) {
            return;
        }

        MenuElement element = elements.get(slot);

        if (element == null) {
            return;
        }

        element.handle(event);

        if (!allowRemoveItems) {
            event.setCancelled(true);
        }
    }

    public void handleClose() {
        for (Runnable task : closeTasks) {
            task.run();
        }
    }
}
