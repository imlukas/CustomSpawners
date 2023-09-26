package dev.imlukas.ultraspawners.menu;

import dev.imlukas.ultraspawners.UltraSpawnersPlugin;
import dev.imlukas.ultraspawners.data.SpawnerData;
import dev.imlukas.ultraspawners.impl.InstancedSpawner;
import dev.imlukas.ultraspawners.utils.InventoryUpdate;
import dev.imlukas.ultraspawners.utils.NumberUtil;
import dev.imlukas.ultraspawners.utils.menu.base.ConfigurableMenu;
import dev.imlukas.ultraspawners.utils.menu.button.Button;
import dev.imlukas.ultraspawners.utils.menu.configuration.ConfigurationApplicator;
import dev.imlukas.ultraspawners.utils.menu.layer.BaseLayer;
import dev.imlukas.ultraspawners.utils.menu.registry.communication.UpdatableMenu;
import dev.imlukas.ultraspawners.utils.schedulerutil.ScheduledTask;
import dev.imlukas.ultraspawners.utils.schedulerutil.builders.ScheduleBuilder;
import dev.imlukas.ultraspawners.utils.text.Placeholder;
import dev.imlukas.ultraspawners.utils.text.TextUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

public class GenericSpawnerMenu extends UpdatableMenu {

    private final UltraSpawnersPlugin plugin;
    private final InstancedSpawner spawner;
    private final SpawnerData data;

    private ConfigurableMenu menu;
    private ConfigurationApplicator applicator;
    private BaseLayer baseLayer;

    private ScheduledTask refreshTask;
    private List<Placeholder<Player>> menuPlaceholders;

    public GenericSpawnerMenu(UltraSpawnersPlugin plugin, Player viewer, InstancedSpawner spawner) {
        super(plugin, viewer);
        this.plugin = plugin;
        this.spawner = spawner;
        this.data = spawner.getSpawnerData();
        setup();
    }

    @Override
    public void refresh() {
        if (getViewer() == null) {
            return;
        }

        String name = data.getName();
        String storagePercent = String.format("%.1f", data.getStoragePercent());

        menuPlaceholders = List.of(
                new Placeholder<>("spawner-name", name),
                new Placeholder<>("spawner-name-plural", name + "s"),
                new Placeholder<>("storage-amount", String.format("%.0f", data.getStorage())),
                new Placeholder<>("storage-max", String.format("%.0f", data.getMaxStorage())),
                new Placeholder<>("storage-percent", storagePercent + "%"),
                new Placeholder<>("item-type", TextUtils.enumToText(data.getGeneratedItem().getType())),
                new Placeholder<>("stack-amount", data.getStackSize()),
                new Placeholder<>("xp-amount", NumberUtil.formatDouble(data.getStoredXp())),
                new Placeholder<>("sold-amount", NumberUtil.formatDouble(data.getSellPrice() * data.getStorage()))
        );

        baseLayer.setItemPlaceholders(menuPlaceholders);
        menu.forceUpdate();
    }

    @Override
    public void setup() {
        menu = createMenu();
        menu.onClose(() -> {
            if (refreshTask != null) {
                refreshTask.cancel();
            }
        });

        applicator = getApplicator();
        baseLayer = new BaseLayer(menu);

        applicator.registerButton(baseLayer, "s", () -> {
            refreshTask.cancel();
            new SpawnerStorageMenu(plugin, getViewer(), spawner).open();
        });

        // Collect money and XP
        Button displayItem = applicator.registerButton(baseLayer, "t", () -> {
            Player player = getViewer();

            double amount = spawner.getSpawnerData().getStorage();
            double sellPrice = spawner.getSpawnerData().getSellPrice() * amount;
            double xp = spawner.getSpawnerData().getStoredXp();

            if (data.getStoragePercent() <= 0.1) {
                return;
            }

            spawner.getSpawnerData().setStorage(0);
            spawner.getSpawnerData().setStoredXp(0);

            plugin.getEconomy().depositPlayer(player, sellPrice);
            getViewer().giveExp((int) xp);
            plugin.getMessages().sendMessage(player, "spawner.collected", menuPlaceholders);
            plugin.getMessages().sendActionbar(player, "spawner.collected-actionbar", menuPlaceholders);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            close();
        });

        displayItem.setDisplayItem(data.getDisplayItem());

        // Collect XP
        applicator.registerButton(baseLayer, "x", () -> {
            Player player = getViewer();
            double xp = spawner.getSpawnerData().getStoredXp();

            if (xp == 0) {
                return;
            }

            spawner.getSpawnerData().setStoredXp(0);
            player.giveExp((int) xp);
            plugin.getMessages().sendMessage(player, "spawner.collected-xp", menuPlaceholders);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            close();
        });

        menu.addRenderable(baseLayer);
        menu.forceUpdate();
        setupRefreshTask();
        refresh();
    }

    @Override
    public void open() {
        super.open();
        InventoryUpdate.updateInventory(getViewer(), spawner.getSpawnerData().getStackSize() + " " + spawner.getSpawnerData().getName());
    }

    @Override
    public String getIdentifier() {
        return "generic-spawner-menu";
    }

    @Override
    public ConfigurableMenu getMenu() {
        return menu;
    }

    public void setupRefreshTask() {
        refreshTask = new ScheduleBuilder(plugin).every(10).ticks().run(this::refresh).sync().start();
    }

}
