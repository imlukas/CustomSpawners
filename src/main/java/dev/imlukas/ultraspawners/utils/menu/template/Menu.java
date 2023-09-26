package dev.imlukas.ultraspawners.utils.menu.template;

import dev.imlukas.ultraspawners.UltraSpawnersPlugin;
import dev.imlukas.ultraspawners.utils.menu.base.ConfigurableMenu;
import dev.imlukas.ultraspawners.utils.menu.concurrency.MainThreadExecutor;
import dev.imlukas.ultraspawners.utils.menu.configuration.ConfigurationApplicator;
import dev.imlukas.ultraspawners.utils.menu.registry.MenuRegistry;
import dev.imlukas.ultraspawners.utils.menu.registry.meta.HiddenMenuTracker;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Consumer;

public abstract class Menu {

    @Getter
    private final UltraSpawnersPlugin plugin;
    @Getter
    private final MenuRegistry menuRegistry;
    @Getter
    private final HiddenMenuTracker hiddenMenuTracker;
    @Getter
    private final UUID viewerId;
    private Runnable onClose;

    public Menu(UltraSpawnersPlugin plugin, Player viewer) {
        this.plugin = plugin;
        this.menuRegistry = plugin.getMenuRegistry();
        this.hiddenMenuTracker = plugin.getMenuRegistry().getHiddenMenuTracker();
        this.viewerId = viewer.getUniqueId();
    }

    /**
     * Handles creation of the menu, definition of variables and static button creation.
     */
    public abstract void setup();

    public abstract String getIdentifier();

    public abstract ConfigurableMenu getMenu();

    public ConfigurationApplicator getApplicator() {
        return getMenu().getApplicator();
    }

    public Player getViewer() {
        return Bukkit.getPlayer(viewerId);
    }

    public Menu onClose(Runnable onClose) {
        getMenu().onClose(onClose);
        return this;
    }

    public void holdForInput(Consumer<String> action) {
        hiddenMenuTracker.holdForInput(getMenu(), action, true);
    }

    public void holdForInput(Consumer<String> action, boolean reOpen) {
        hiddenMenuTracker.holdForInput(getMenu(), action, reOpen);
    }

    public ConfigurableMenu createMenu() {
        return (ConfigurableMenu) menuRegistry.create(getIdentifier(), getViewer());
    }

    public void close() {
        Player viewer = getViewer();

        if (viewer.getOpenInventory().getTopInventory().equals(getMenu().getInventory())) {
            if (Bukkit.isPrimaryThread()) {
                viewer.closeInventory();
            } else {
                MainThreadExecutor.INSTANCE.execute(viewer::closeInventory); // fuck you bukkit
            }
        }
    }

    public void open() {
        getMenu().open();
    }
}
