package Main.main.meow.client.util;

import Main.main.meow.client.module.modules.client.clickgui.ClickGuiModule;
import Main.main.meow.client.ui.clickgui.view.ClickGuiStyler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

public class TextUtil {
    /**
     * Calculates the width of the text
     * @param client Minecraft instance
     * @param textString The string to calculate
     * @return Number of pixels the text can be contained in
     */
    public static int getTextWidth(Minecraft client, String textString) {
        if(client == null) {
            return 0; // null, therefore no text
        }
        return client.font.width(textString);
    }


    /**
     * Height of an unmodified text string
     * @param client Minecraft instance
     * @return The height of a generic unmodified string, 9 pixels is standard.
     */
    public static int getTextHeight(Minecraft client) {
        if(client == null) {
            return 0;
        }
        return client.font.lineHeight; // standard text in minecraft maintains a height of 9 pixels
    }

    /**
     * Component font modifier
     * @param value Desired String to modify font
     * @return MutableComponent custom font
     */
    public static Component setFont(String value) {
        return Component.literal(value).withStyle(style -> style.withFont(ClickGuiStyler.getFont()));
    }
}
