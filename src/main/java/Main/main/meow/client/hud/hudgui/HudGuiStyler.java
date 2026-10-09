package Main.main.meow.client.hud.hudgui;

import Main.main.meow.client.clickgui.settings.ClickGuiSettings;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public final class HudGuiStyler {
    // this is pretty to look at, ai generated again but im not an artist so i cant do better
    public static final int ROW = 0xe62c313b, ROW_HOVER = 0xf03a404b, SETTING_ROW = 0xff171a22, TEXT = 0xfff7f4f7, CATEGORY_TEXT = 0xff000000, MUTED = 0xffc9c8cc, BORDER = 0xffffffff, CORNER_RADIUS = 16;

    private static final FontDescription ODIN_FONT = new FontDescription.Resource(Identifier.fromNamespaceAndPath("meow", "odin"));

    private final Font font;

    public HudGuiStyler(Font font) {
        this.font = font;
    }

    public Component styled(String value) {
        MutableComponent component = Component.literal(value);
        if (ClickGuiSettings.odinFont()) {
            return component.withStyle(style -> style.withFont(ODIN_FONT));
        } else {
            return component;
        }
    }

    public float textScale() {
        return ClickGuiSettings.fontSize() / 11f;
    }

    public int textWidth(Component value) {
        return Math.round(font.width(value) * textScale());
    }

    public int textLineHeight() {
        return Math.round(font.lineHeight * textScale());
    }

    public void drawText(GuiGraphicsExtractor graphics, Component value, int x, int y, int color, boolean shadow) {
        float scale = textScale();
        graphics.pose().pushMatrix();
        graphics.pose().translate(x, y);
        graphics.pose().scale(scale, scale);
        graphics.text(font, value, 0, 0, color, shadow && !ClickGuiSettings.odinFont());
        graphics.pose().popMatrix();
    }

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

    public List<String> wrapTooltip(String description, int maxWidth) {
        List<String> lines = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (String word : description.split(" ")) {
            // use conditional if rather than ternary operator for candidate
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

    public int centeredX(int x, int containerWidth, String value) {
        return x + Math.max(4, (containerWidth - textWidth(styled(value))) / 2);
    }

    public int centeredY(int y, int containerHeight) {
        return y + Math.max(0, (containerHeight - textLineHeight()) / 2);
    }
    // wouldn't you know! circles are hard to draw in minecraft, so we have to do this instead, thanks random minecraft clients code i read on github
    public static void roundedFill(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color, boolean topLeft, boolean topRight, boolean bottomLeft, boolean bottomRight) {
        int radius = Math.min(CORNER_RADIUS, width / 2);
        for (int row = 0; row < height; row++) {
            int leftInset = 0;
            int rightInset = 0;
            if (topLeft && row < radius) {
                leftInset = roundedInset(radius, row);
            }
            if (topRight && row < radius) {
                rightInset = roundedInset(radius, row);
            }
            if (bottomLeft && row >= height - radius) {
                leftInset = roundedInset(radius, height - row - 1);
            }
            if (bottomRight && row >= height - radius) {
                rightInset = roundedInset(radius, height - row - 1);
            }
            if (leftInset > 0 || rightInset > 0) {
                graphics.fill(x + leftInset, y + row, x + width - rightInset, y + row + 1, color);
            } else {
                graphics.fill(x, y + row, x + width, y + row + 1, color);
            }
        }
    }

    private static int roundedInset(int radius, int distance) {
        double circle = radius - 0.5;
        double offset = distance - circle;
        return Math.max(0, (int) Math.ceil(circle - Math.sqrt(Math.max(0, circle * circle - offset * offset)))); // read a minecraft clients code for this, i have no idea how this works but it does and it looks good so im not complaining
    }
    // thanks to the internet for this function, i have no idea how it works but it does
    public static float easeIn(float value) {
        return (float) Math.pow(value, 2);
    }

    public static int withAlpha(int color, int alpha) {
        return (color & 0x00ffffff) | (alpha << 24); // bitwise operations to set the alpha channel of the color while preserving the RGB values (thanks comp 2401)
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    public static String hex(int color) {
        return String.format("#%06X", color & 0xffffff); // bitwise operation to get the RGB values of the color while ignoring the alpha channel (thanks comp 2401)
    }
}
