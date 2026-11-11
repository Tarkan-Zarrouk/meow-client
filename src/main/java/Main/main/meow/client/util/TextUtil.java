package Main.main.meow.client.util;

import net.minecraft.client.Minecraft;
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
     * Filters hexadecimal color code into a usable color code
     * @return color of the form 0xAARRGGBB, or -1 if invalid
     */
    public static int getColorFromClipboard() {
        String clipboard = Minecraft.getInstance().keyboardHandler.getClipboard();
        String hexString = clipboard.replaceAll("[^a-fA-F0-9]", "");

        if (hexString.length() != 8) {
            return -1;
        }
        try {
            return Integer.parseUnsignedInt(hexString, 16);
        } catch (NumberFormatException e) {
            return e.hashCode();
        }
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
        return Component.literal(value).withStyle(style -> style.withFont(FontUtil.getFont()));
    }
}
