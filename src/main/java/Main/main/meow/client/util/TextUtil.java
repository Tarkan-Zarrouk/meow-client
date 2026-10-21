package Main.main.meow.client.util;

import Main.main.meow.client.ui.hud.hudgui.HudGuiStyler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class TextUtil {
    public static int getTextWidth(Minecraft client, String textString) {
        if(client == null) {
            return 0; // null, therefore no text
        }
        return client.font.width(textString);
    }
    public static int getTextHeight(Minecraft client) {
        if(client == null) {
            return 0;
        }
        return client.font.lineHeight; // standard text in minecraft maintains a height of 9 pixels
    }
    public static Component setFont(String value) {
        return Component.literal(value).withStyle(style -> style.withFont(HudGuiStyler.getFont()));
    }
}
