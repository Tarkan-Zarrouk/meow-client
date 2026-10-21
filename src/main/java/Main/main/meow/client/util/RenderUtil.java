package Main.main.meow.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class RenderUtil {
    private static final int GUI_PANEL_COLOR = 0xb9161d2a;
    private static final int GUI_TEXT_COLOR = 0xfff2f0f4;
    public static int getGuiColorPanel() {
        return GUI_PANEL_COLOR;
    }
    public static int getGuiTextColor() {
        return GUI_TEXT_COLOR;
    }
    public static void drawText(GuiGraphicsExtractor graphics, Minecraft client, String value, int x, int y, int color) {
        graphics.text(client.font, TextUtil.setFont(value), x, y, color);
    }
    public static void drawText(GuiGraphicsExtractor graphics, Minecraft client, String value, int x, int y) {
        graphics.text(client.font, TextUtil.setFont(value), x, y, getGuiTextColor());
    }
    public static void drawPanel(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color) {
        graphics.fill(x, y, x + width, y + height, color);
    }
    public static void drawPanel(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + height, getGuiColorPanel());
    }
    public static int roundedInset(int radius, int distance) {
        double circle = radius - 0.5;
        double offset = distance - circle;
        return Math.max(0, (int) Math.ceil(circle - Math.sqrt(Math.max(0, circle * circle - offset * offset)))); // read a minecraft clients code for this, i have no idea how this works but it does and it looks good so im not complaining
    }
    public static void roundedFill(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color, int radius) {
        radius = Math.min(radius, Math.min(width / 2, height / 2));
        for (int row = 0; row < height; row++) {
            int inset = 0;
            if (row < radius) {
                inset = roundedInset(radius, row);
            } else if (row >= height - radius) {
                inset = roundedInset(radius, height - row - 1); // bottom corner
            }
            graphics.fill(x + inset, y + row, x + width - inset, y + row + 1, color); // horizontal line current riow
        }
    }
}
