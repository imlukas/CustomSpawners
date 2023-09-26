package dev.imlukas.ultraspawners.utils.menu.element;

import dev.imlukas.ultraspawners.utils.text.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface MenuElement {

    ItemStack getDisplayItem();

    void handle(InventoryClickEvent event);

    MenuElement copy();

    default Collection<Placeholder<Player>> getItemPlaceholders() {
        return Collections.emptyList();
    }

    default MenuElement setItemPlaceholders(Placeholder<Player>... placeholders) {
        return setItemPlaceholders(List.of(placeholders));
    }

    default MenuElement setItemPlaceholders(Collection<Placeholder<Player>> placeholders) {
        return this;
    }

}
