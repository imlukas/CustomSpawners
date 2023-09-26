package dev.imlukas.ultraspawners.utils.menu.registry;

import dev.imlukas.ultraspawners.UltraSpawnersPlugin;
import dev.imlukas.ultraspawners.utils.menu.base.BaseMenu;
import dev.imlukas.ultraspawners.utils.menu.base.ConfigurableMenu;
import dev.imlukas.ultraspawners.utils.menu.configuration.ConfigurationApplicator;
import dev.imlukas.ultraspawners.utils.menu.layer.BaseLayer;
import dev.imlukas.ultraspawners.utils.menu.listener.MenuListener;
import dev.imlukas.ultraspawners.utils.menu.registry.communication.UpdatableMenuRegistry;
import dev.imlukas.ultraspawners.utils.menu.registry.meta.HiddenMenuTracker;
import dev.imlukas.ultraspawners.utils.storage.YMLBase;
import dev.imlukas.ultraspawners.utils.text.TextUtils;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

@Getter
public class MenuRegistry {

    private final Map<String, Function<Player, BaseMenu>> menuInitializers = new ConcurrentHashMap<>();
    private final UltraSpawnersPlugin plugin;
    private final HiddenMenuTracker hiddenMenuTracker = new HiddenMenuTracker();
    private final UpdatableMenuRegistry updatableMenuRegistry = new UpdatableMenuRegistry();

    public MenuRegistry(UltraSpawnersPlugin plugin) {
        this.plugin = plugin;

        MenuListener.register(this);
        load(new File(plugin.getDataFolder(), "menu"));
    }

    private void load(File folder) {
        if (!folder.exists()) {
            folder.mkdirs();
        }

        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                load(file);
                continue;
            }

            if (!file.getName().endsWith(".yml")) {
                continue;
            }

            boolean existsOnSource = plugin.getResource(file.getName()) != null;
            YMLBase config = new YMLBase(plugin, file, existsOnSource);

            registerConfigurable(config);
        }
    }

    public void register(String name, Function<Player, BaseMenu> initializer) {
        menuInitializers.put(name, initializer);
    }

    public void registerConfigurable(YMLBase base) {
        String name = base.getFile().getName().replace(".yml", "");

        int rows = base.getConfiguration().getInt("rows", -1);

        if (rows == -1) {
            // get from layout
            List<String> layout = base.getConfiguration().getStringList("layout");

            if (layout.isEmpty()) {
                throw new IllegalArgumentException("No rows specified for menu " + name);
            }

            rows = layout.size();
        }

        String title = TextUtils.color(base.getConfiguration().getString("title", name));

        int finalRows = rows;
        register(name, player -> {
            ConfigurationApplicator applicator = new ConfigurationApplicator(base.getConfiguration());

            BaseMenu menu = new ConfigurableMenu(player.getUniqueId(), title, finalRows, applicator);
            BaseLayer layer = new BaseLayer(menu);

            applicator.applyConfiguration(layer);
            layer.forceUpdate();

            return menu;
        });
    }

    public Function<Player, BaseMenu> getInitializer(String name) {
        return menuInitializers.get(name);
    }

    public BaseMenu create(String name, Player player) {
        Function<Player, BaseMenu> initializer = getInitializer(name);

        if (initializer == null) {
            plugin.getLogger().warning("No menu initializer found for " + name);
            return null;
        }

        return initializer.apply(player);
    }

    public void registerPostInitTask(String name, Consumer<BaseMenu> consumer) {
        Function<Player, BaseMenu> initializer = getInitializer(name);

        register(name, player -> {
            BaseMenu menu = initializer.apply(player);

            consumer.accept(menu);
            return menu;
        });
    }

    public List<String> getMenuNames() {
        return new ArrayList<>(menuInitializers.keySet());
    }

    public void reload() {
        menuInitializers.clear();
        load(new File(plugin.getDataFolder(), "menu"));
    }

}
