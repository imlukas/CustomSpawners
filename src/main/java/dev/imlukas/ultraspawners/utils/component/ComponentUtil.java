package dev.imlukas.ultraspawners.utils.component;

import dev.imlukas.ultraspawners.utils.text.TextUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

public class ComponentUtil {

    public static TextComponent create(String mainText) {
        return Component.text(TextUtils.color(mainText));
    }

    public static TextComponent create(String mainText, String... texts) {
        TextComponent.Builder builder = create(mainText).toBuilder();
        for (String text : texts) {
            builder.append(create(text));
        }
        return builder.build();
    }

    public static TextComponent create(String mainText, HoverEvent<Component> hoverEvent, String... toAppend) {
        TextComponent text = create(mainText, toAppend);

        return text.hoverEvent(hoverEvent);
    }

    public static TextComponent create(String mainText, ClickEvent clickEvent, String... toAppend) {
        TextComponent text = create(mainText, toAppend);

        return text.clickEvent(clickEvent);
    }

    public static TextComponent create(String mainText, HoverEvent<Component> hoverEvent, ClickEvent clickEvent, String... toAppend) {
        TextComponent text = create(mainText, toAppend);

        return text.hoverEvent(hoverEvent).clickEvent(clickEvent);
    }
}
