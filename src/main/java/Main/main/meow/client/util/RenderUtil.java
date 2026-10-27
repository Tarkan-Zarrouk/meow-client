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

    /**
     * Draws text on the screen
     * @param graphics Graphics renderer class
     * @param client Minecraft instance
     * @param value The string to draw
     * @param x The x position
     * @param y The y position
     * @param color Text color
     */
    public static void drawText(GuiGraphicsExtractor graphics, Minecraft client, String value, int x, int y, int color) {
        graphics.text(client.font, TextUtil.setFont(value), x, y, color);
    }

    /**
     * Draws text on the screen
     * @param graphics Graphics renderer class
     * @param client Minecraft instance
     * @param value The string to draw
     * @param x The x position
     * @param y The y position
     */
    public static void drawText(GuiGraphicsExtractor graphics, Minecraft client, String value, int x, int y) {
        graphics.text(client.font, TextUtil.setFont(value), x, y, getGuiTextColor());
    }

    /**
     * Draw a quadrilateral on the screen
     * @param graphics Graphics renderer class
     * @param x The x position
     * @param y The y position
     * @param width The width
     * @param height The height
     * @param color Text color
     */
    public static void drawPanel(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color) {
        graphics.fill(x, y, x + width, y + height, color);
    }

    /**
     * Draw a quadrilateral on the screen
     * @param graphics Graphics renderer class
     * @param x The x position
     * @param y The y position
     * @param width The width
     * @param height The height
     */
    public static void drawPanel(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + height, getGuiColorPanel());
    }
    public static void roundedFill(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color, boolean topLeft, boolean topRight, boolean bottomLeft, boolean bottomRight, int radius) {
        int borderRadius = Math.min(radius, width / 2);
        for (int row = 0; row < height; row++) {
            int leftInset = 0;
            int rightInset = 0;
            if (topLeft && row < borderRadius) {
                leftInset = roundedInset(borderRadius, row);
            }
            if (topRight && row < borderRadius) {
                rightInset = roundedInset(borderRadius, row);
            }
            if (bottomLeft && row >= height - borderRadius) {
                leftInset = roundedInset(borderRadius, height - row - 1);
            }
            if (bottomRight && row >= height - borderRadius) {
                rightInset = roundedInset(borderRadius, height - row - 1);
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
//    /**
//     * Calculates the amount of pixels to insert for a rounded corner based on the radius and distance from the corner
//     * @param radius Radius of the rounded corner
//     * @param distance Distance from the corner
//     * @return The amount of pixels to inset for the rounded corner
//     */
//    public static int roundedInset(int radius, int distance) {
//        double circle = radius - 0.5;
//        double offset = distance - circle;
//        return Math.max(0, (int) Math.ceil(circle - Math.sqrt(Math.max(0, circle * circle - offset * offset)))); // read a minecraft clients code for this, i have no idea how this works but it does and it looks good so im not complaining
//    }
//
//    /**
//     * Draws a rounded shape
//     * @param graphics Graphics renderer class
//     * @param x The x position
//     * @param y The y position
//     * @param width The width
//     * @param height The height
//     * @param color The color
//     * @param radius Distance to the midpoint of the shape
//     */
//    public static void roundedFill(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color, int radius) {
//        radius = Math.min(radius, Math.min(width / 2, height / 2));
//        for (int row = 0; row < height; row++) {
//            int inset = 0;
//            if (row < radius) {
//                inset = roundedInset(radius, row);
//            } else if (row >= height - radius) {
//                inset = roundedInset(radius, height - row - 1); // bottom corner
//            }
//            graphics.fill(x + inset, y + row, x + width - inset, y + row + 1, color); // horizontal line current riow
//        }
//    }
}
