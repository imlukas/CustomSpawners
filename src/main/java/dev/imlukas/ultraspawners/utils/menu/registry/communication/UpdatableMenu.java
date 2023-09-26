package dev.imlukas.ultraspawners.utils.menu.registry.communication;

import dev.imlukas.ultraspawners.UltraSpawnersPlugin;
import dev.imlukas.ultraspawners.utils.menu.template.Menu;
import org.bukkit.entity.Player;

public abstract class UpdatableMenu extends Menu {

    public UpdatableMenu(UltraSpawnersPlugin plugin, Player viewer) {
        super(plugin, viewer);
    }

    /**
     * Handles refreshing placeholders and updating buttons and other elements accordingly.
     */
    public abstract void refresh();

    @Override
    public void open() {
        refresh();
        super.open();
    }
}
