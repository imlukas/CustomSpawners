package dev.imlukas.ultraspawners.utils.menu.base;

import dev.imlukas.ultraspawners.utils.menu.configuration.ConfigurationApplicator;
import dev.imlukas.ultraspawners.utils.menu.element.MenuElement;
import dev.imlukas.ultraspawners.utils.menu.mask.PatternMask;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

@Getter
public class ConfigurableMenu extends BaseMenu {

    private final ConfigurationApplicator applicator;


    public ConfigurableMenu(UUID playerId, String title, int rows, ConfigurationApplicator applicator) {
        super(playerId, title, rows);
        this.applicator = applicator;
    }

    public ItemStack getItem(String key) {
        return getApplicator().getItem(key);
    }

    public MenuElement getDecorationItem(String key) {
        return getApplicator().getDecorationItem(key);
    }

    public PatternMask getMask() {
        return getApplicator().getMask();
    }

    public List<String> getDescription() {
        return getApplicator().getDescription();
    }

    public FileConfiguration getConfig() {
        return getApplicator().getConfig();
    }


}
