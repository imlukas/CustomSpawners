package dev.imlukas.ultraspawners;

import dev.imlukas.ultraspawners.utils.PDCUtils.PDCWrapper;
import dev.imlukas.ultraspawners.utils.item.ItemBuilder;
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

    public PluginSettings(UltraSpawnersPlugin plugin) {
        FileConfiguration config = plugin.getConfig();
        this.spawnerRange = config.getInt("spawner-range");
        this.boosterDuration = Time.parseTime(config.getString("booster-duration"));
        this.boosterMultiplier = config.getDouble("booster-multiplier");

        this.boosterItem = ItemBuilder.fromSection(config.getConfigurationSection("booster-item"));

        PDCWrapper.modifyItem(plugin, boosterItem, wrapper -> {
            wrapper.setBoolean("booster", true);
        });

        ItemMeta meta = boosterItem.getItemMeta();
        List<String> lore = meta.getLore();
        lore.replaceAll(line -> line.replace("%duration%", boosterDuration.toString()));
        meta.setLore(lore);
        boosterItem.setItemMeta(meta);

        this.pickaxeType = Material.getMaterial(config.getString("pickaxe-type"));
    }

    public ItemStack getBoosterItem() {
        return boosterItem.clone();
    }
}
