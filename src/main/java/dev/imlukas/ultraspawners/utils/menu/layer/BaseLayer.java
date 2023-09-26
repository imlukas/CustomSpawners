package dev.imlukas.ultraspawners.utils.menu.layer;

import dev.imlukas.ultraspawners.utils.menu.base.BaseMenu;
import dev.imlukas.ultraspawners.utils.menu.element.MenuElement;
import dev.imlukas.ultraspawners.utils.menu.element.Renderable;
import dev.imlukas.ultraspawners.utils.menu.selection.Selection;
import dev.imlukas.ultraspawners.utils.text.Placeholder;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BaseLayer extends Renderable {

    private final Map<Integer, MenuElement> slots = new HashMap<>();

    public BaseLayer(BaseMenu menu) {
        super(menu);
    }

    @Override
    public void forceUpdate() {
        for (Map.Entry<Integer, MenuElement> entry : slots.entrySet()) {
            menu.setElement(entry.getKey(), entry.getValue());
        }
    }

    public void applySelection(Selection selection, MenuElement element) {
        for (int slot : selection.getSlots()) {
            slots.put(slot, element.copy());
        }
    }

    public void applyRawSelection(Selection selection, MenuElement element) {
        for (int slot : selection.getSlots()) {
            slots.put(slot, element);
        }
    }

    public void setItemPlaceholders(Collection<Placeholder<Player>> placeholders) {
        for (MenuElement element : slots.values()) {
            element.setItemPlaceholders(placeholders);
        }
    }
}
