package dev.imlukas.ultraspawners.data;

import dev.imlukas.ultraspawners.UltraSpawnersPlugin;
import dev.imlukas.ultraspawners.utils.PDCUtils.PDCWrapper;
import dev.imlukas.ultraspawners.utils.item.ItemBuilder;
import dev.imlukas.ultraspawners.utils.time.Time;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

@Data
public class SpawnerData {

    private final UltraSpawnersPlugin plugin;
    private final String identifier;
    private final String name;
    private final Time timePerCycle;

    private final ItemStack displayItem;
    private final ItemStack generatedItem;
    private final ItemStack blockItem;

    private final String entityType;

    private final double sellPrice;
    private final double amountGenerated;

    private double initialMaxStorage;
    private double maxStorage;
    private double storage = 0;
    private double storedXp = 0;
    private int stackSize = 1;

    private boolean boosted = false;
    private boolean isActive = false;

    public SpawnerData(UltraSpawnersPlugin plugin, ConfigurationSection spawnerSection) {
        this.plugin = plugin;
        this.identifier = spawnerSection.getName();
        this.name = spawnerSection.getString("name");
        this.displayItem = ItemBuilder.fromSection(spawnerSection.getConfigurationSection("display-item"));
        this.generatedItem = new ItemStack(Material.getMaterial(spawnerSection.getString("item")));
        this.blockItem = ItemBuilder.fromSection(spawnerSection.getConfigurationSection("block"));

        this.entityType = spawnerSection.getString("mob-type");

        PDCWrapper.modifyItem(plugin, blockItem, wrapper -> wrapper.setString("spawner", identifier));

        this.sellPrice = spawnerSection.getInt("sell-price");

        ConfigurationSection rateSection = spawnerSection.getConfigurationSection("rate");
        this.amountGenerated = rateSection.getInt("amount");
        this.timePerCycle = Time.parseTime(rateSection.getString("time"));

        this.maxStorage = spawnerSection.getInt("max-storage");
        this.initialMaxStorage = maxStorage;

    }

    public ItemStack getGeneratedItem() {
        return generatedItem.clone();
    }

    public ItemStack getDisplayItem() {
        return displayItem.clone();
    }

    public ItemStack getBlockItem() {
        return blockItem.clone();
    }

    public double getStoragePercent() {
        return storage / maxStorage * 100;
    }

    public void addStorage() {
        if (storage > maxStorage) {
            return;
        }

        double finalAmount = amountGenerated * stackSize;
        double newStorage = storage;

        if (isBoosted()) {
            newStorage += (finalAmount * plugin.getPluginSettings().getBoosterMultiplier());
        } else {
            newStorage += finalAmount;
        }

        if (newStorage > maxStorage) {
            storage = (int) maxStorage;
            return;
        }

        storage = newStorage;
    }

    public void addXp(int amount) {
        if (isBoosted()) {
            storedXp += amount * 2;
            return;
        }

        storedXp += amount;
    }

    public void addStack(int amount) {
        stackSize += amount;
        maxStorage = initialMaxStorage * this.stackSize;

        if (storage > maxStorage) {
            storage = maxStorage;
        }

    }

    public void setStackSize(int stackSize) {
        this.stackSize = stackSize;
        maxStorage = initialMaxStorage * this.stackSize;

        if (storage > maxStorage) {
            storage = maxStorage;
        }

    }
}
