package dev.imlukas.ultraspawners.menu;

import dev.imlukas.ultraspawners.UltraSpawnersPlugin;
import dev.imlukas.ultraspawners.impl.InstancedSpawner;
import dev.imlukas.ultraspawners.utils.menu.base.ConfigurableMenu;
import dev.imlukas.ultraspawners.utils.menu.button.Button;
import dev.imlukas.ultraspawners.utils.menu.configuration.ConfigurationApplicator;
import dev.imlukas.ultraspawners.utils.menu.layer.BaseLayer;
import dev.imlukas.ultraspawners.utils.menu.layer.PaginableLayer;
import dev.imlukas.ultraspawners.utils.menu.pagination.PaginableArea;
import dev.imlukas.ultraspawners.utils.menu.registry.communication.UpdatableMenu;
import dev.imlukas.ultraspawners.utils.schedulerutil.ScheduledTask;
import dev.imlukas.ultraspawners.utils.schedulerutil.builders.ScheduleBuilder;
import dev.imlukas.ultraspawners.utils.text.Placeholder;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class SpawnerStorageMenu extends UpdatableMenu {

    private final UltraSpawnersPlugin plugin;
    private final InstancedSpawner spawner;

    private ConfigurableMenu menu;
    private PaginableArea area;

    private ScheduledTask refreshTask;

    public SpawnerStorageMenu(UltraSpawnersPlugin plugin, Player viewer, InstancedSpawner spawner) {
        super(plugin, viewer);
        this.plugin = plugin;
        this.spawner = spawner;
        setup();
    }

    @Override
    public void refresh() {
        area.clear();
        double storage = spawner.getSpawnerData().getStorage();
        double stackAmount = (float) spawner.getSpawnerData().getStorage() / 64;
        ItemStack item = spawner.getSpawnerData().getGeneratedItem();


        double iteration = stackAmount < 1 ? 1 : Math.ceil(stackAmount);
        for (int i = 0; i < iteration; i++) {
            ItemStack newItem = item.clone();
            double newStorage = storage - newItem.getMaxStackSize();

            if (newStorage < 0) {
                newItem.setAmount((int) storage);
            } else {
                newItem.setAmount(newItem.getMaxStackSize());
            }

            storage = newStorage;
            Button itemButton = getButton(newItem);

            area.addElement(itemButton);
        }

        menu.forceUpdate();
    }

    @NotNull
    private Button getButton(ItemStack newItem) {
        Button itemButton = new Button(newItem);

        itemButton.setLeftClickAction(() -> {
            Map<Integer, ItemStack> items = getViewer().getInventory().addItem(newItem);

            for (ItemStack value : items.values()) {
                getViewer().getWorld().dropItem(getViewer().getLocation(), value);
            }

            spawner.getSpawnerData().setStorage(spawner.getSpawnerData().getStorage() - newItem.getAmount());
            area.removeElement(itemButton);
            menu.forceUpdate();
        });
        return itemButton;
    }

    @Override
    public void setup() {
        ConfigurationApplicator applicator;
        BaseLayer layer;
        menu = createMenu();
        applicator = getApplicator();

        menu.onClose(() -> {
            if (refreshTask != null) {
                refreshTask.cancel();
            }
        });

        PaginableLayer paginableLayer = new PaginableLayer(menu);
        area = new PaginableArea(applicator.getMask().selection("."));
        paginableLayer.addArea(area);
        layer = new BaseLayer(menu);
        menu.addRenderable(paginableLayer, layer);

        applicator.registerButton(layer, "c", () -> {
            if (refreshTask != null) {
                refreshTask.cancel();
            }

            close();

            ScheduleBuilder.runIn1Tick(plugin, () -> {
                new GenericSpawnerMenu(plugin, getViewer(), spawner).open();
            }).sync().start();
        });

        applicator.registerButton(layer, "n", paginableLayer::nextPage);
        applicator.registerButton(layer, "p", paginableLayer::previousPage);
        applicator.registerButton(layer, "s", () -> {
            Player player = getViewer();
            double amount = spawner.getSpawnerData().getStorage();
            double sellPrice = spawner.getSpawnerData().getSellPrice() * amount;
            Placeholder<Player> sellPricePlaceholder = new Placeholder<>("sold-amount", String.valueOf(sellPrice));

            if (amount == 0) {
                return;
            }

            spawner.getSpawnerData().setStorage(0);

            plugin.getEconomy().depositPlayer(player, sellPrice);
            plugin.getMessages().sendMessage(player, "spawner.collected", sellPricePlaceholder);
            plugin.getMessages().sendActionbar(player, "spawner.collected-actionbar", sellPricePlaceholder);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            refresh();
        });

        refresh();
    }

    @Override
    public String getIdentifier() {
        return "storage-menu";
    }

    @Override
    public ConfigurableMenu getMenu() {
        return menu;
    }

    @Override
    public void open() {
        super.open();
        setupRefreshTask();
    }

    public void setupRefreshTask() {
        refreshTask = new ScheduleBuilder(plugin).every(10).ticks().run(this::refresh).sync().start();
    }
}
