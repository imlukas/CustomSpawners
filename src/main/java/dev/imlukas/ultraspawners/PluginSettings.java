package dev.imlukas.ultraspawners;

import dev.imlukas.ultraspawners.utils.PDCUtils.PDCWrapper;
import dev.imlukas.ultraspawners.utils.item.ItemBuilder;
import dev.imlukas.ultraspawners.utils.item.ItemUtil;
import dev.imlukas.ultraspawners.utils.text.Placeholder;
import dev.imlukas.ultraspawners.utils.time.Time;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

@Data
public class PluginSettings {

    private final int spawnerRange;
    private final Time boosterDuration;
    private final double boosterMultiplier;
    private final ItemStack boosterItem;
    private final Material pickaxeType;
    private final boolean canPickupAtZero;

    public PluginSettings(UltraSpawnersPlugin plugin) {
        FileConfiguration config = plugin.getConfig();
        this.spawnerRange = config.getInt("spawner-range");
        this.boosterDuration = Time.parseTime(config.getString("booster-duration"));
        this.boosterMultiplier = config.getDouble("booster-multiplier");

        this.boosterItem = ItemBuilder.fromSection(config.getConfigurationSection("booster-item"));

        PDCWrapper.modifyItem(plugin, boosterItem, wrapper -> {
            wrapper.setBoolean("booster", true);
        });

        ItemUtil.replaceLore(boosterItem, s -> s.replace("%duration%", boosterDuration.toString()));

        this.pickaxeType = Material.getMaterial(config.getString("pickaxe-type"));
        this.canPickupAtZero = config.getBoolean("can-pickup-at-zero");
    }

    public boolean canPickupAtZero() {
        return canPickupAtZero;
    }

    public ItemStack getBoosterItem() {
        return boosterItem.clone();
    }
}
