package Main.main.meow.client.util;

import Main.main.meow.client.module.modules.client.clickgui.ClickGuiModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;

public class TextUtil {
    private final Font FONT = Minecraft.getInstance().font;
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
    public static Component setComponentFont(String value) {
        return Component.literal(value).withStyle(style -> style.withFont(FontUtil.getFontDescription()));
    }


    /**
     * Font size on a 1-11 scale
     * @return 1-11 font size
     */
    public float textScale() {
        return ClickGuiModule.fontSize() / 11f;
    }

    /**
     * Width of text
     * @param value text to compute width
     * @return stringWidth * (1 to 11 font scaled)
     */
    public int textWidth(Component value) {
        return Math.round(FONT.width(value) * textScale());
    }

    /**
     * Height of standard minecraft text
     * @return FixedHeight (9 pixels) * ClickGuiFontSize(from 1 to 11 scale)
     */
    public int textLineHeight() {
        return Math.round(FONT.lineHeight * textScale());
    }

    /**
     * Converts a string into a component with the current font applied
     * @param value Desired String to modify font
     * @return MutableComponent custom font
     */
    public MutableComponent styled(String value) {
        return Component.literal(value).withStyle(style -> style.withFont(FontUtil.getFontDescription()));
    }

    /**
     * Eliminates text exceeding bounds
     * @param value text
     * @param maxWidth max bound
     * @return normal text + truncated text replacement ".."
     */
    public String truncate(String value, int maxWidth) {
        if (textWidth(styled(value)) <= maxWidth) return value;
        String suffix = "..";
        StringBuilder result = new StringBuilder();
        for (char character : value.toCharArray()) {
            if (textWidth(styled(result.toString() + character + suffix)) > maxWidth)
                break;
            result.append(character);
        }
        return result + suffix;
    }

    /**
     * Adjust tooltip width (styling purpose)
     * @param description tooltip string
     * @param maxWidth max bound
     * @return List of the strings split at every bound max
     */
    public List<String> wrapTooltip(String description, int maxWidth) {
        List<String> lines = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (String word : description.split(" ")) {
            String candidate;
            if (current.isEmpty()) {
                candidate = word;
            } else {
                candidate = current + " " + word;
            }
            if (!current.isEmpty() && textWidth(styled(candidate)) > maxWidth) {
                lines.add(current.toString());
                current.setLength(0);
            }
            if(current.isEmpty()) {
                current.append(word);
            } else {
                current.append(" ").append(word);
            }
        }
        if (!current.isEmpty()) lines.add(current.toString());
        return lines;
    }

    /**
     * Centers x based on parent width
     * @param x current x position
     * @param containerWidth parent width
     * @param value the string to center
     * @return centers based on the width of the parent's container width
     */
    public int centeredX(int x, int containerWidth, String value) {
        return x + Math.max(4, (containerWidth - textWidth(styled(value))) / 2);
    }

    /**
     * Centers y based on parent height
     * @param y current y position
     * @param containerHeight parent height
     * @return centers based on the height of the parent's container height
     */
    public int centeredY(int y, int containerHeight) {
        return y + Math.max(0, (containerHeight - textLineHeight()) / 2);
    }

    /**
     * Easing function for animations
     * @param value value between 0 and 1
     * @return eased value between 0 and 1
     */
    public static float easeIn(float value) {
        return (float) Math.pow(value, 2);
    }
}
