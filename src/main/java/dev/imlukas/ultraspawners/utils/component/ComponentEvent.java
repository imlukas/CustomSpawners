package dev.imlukas.ultraspawners.utils.component;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

public class ComponentEvent {

    public static class Hover {

        public static HoverEvent<Component> showText(String text) {
            return HoverEvent.showText(ComponentUtil.create(text));
        }

        public static HoverEvent<Component> showText(Component text) {
            return HoverEvent.showText(text);
        }
    }

    public static class Click {

        public static ClickEvent runCommand(String command) {
            return ClickEvent.runCommand(command);
        }

        public static ClickEvent openURL(String url) {
            return ClickEvent.openUrl(url);
        }

        public static ClickEvent copyToClipboard(String textToCopy) {
            return ClickEvent.copyToClipboard(textToCopy);
        }
    }

}
