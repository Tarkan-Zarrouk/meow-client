package Main.main.meow.client.clickgui;

import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.awt.Color;

/**
 * Reusable HSV color picker with a saturation/value square and hue slider.
 */
public final class ColorPicker {
    @FunctionalInterface
    public interface ColorConsumer {
        void accept(int color);
    }

    private final int width;
    private final int height;
    private final ColorConsumer consumer;
    private int x;
    private int y;
    private float hue;
    private float saturation;
    private float value;
    private boolean draggingSquare;
    private boolean draggingHue;

    public ColorPicker(int width, int height, int initialColor, ColorConsumer consumer) {
        this.width = width;
        this.height = height;
        this.consumer = consumer;
        setColor(initialColor);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int width() { return width; }
    public int height() { return height; }
    public int color() { return Color.HSBtoRGB(hue, saturation, value) | 0xff000000; }

    public void setColor(int color) {
        float[] hsv = Color.RGBtoHSB(color >> 16 & 255, color >> 8 & 255, color & 255, null);
        hue = hsv[0];
        saturation = hsv[1];
        value = hsv[2];
    }

    public void render(GuiGraphicsExtractor graphics) {
        int squareHeight = height - 38;
        int hueY = y + squareHeight + 14;
        int hueWidth = width - 16;
        int base = Color.HSBtoRGB(hue, 1f, 1f) | 0xff000000;

        graphics.fill(x, y, x + width, y + height, 0xee20232a);
        graphics.fillGradient(x, y, x + width, y + squareHeight, 0xffffffff, base);
        graphics.fillGradient(x, y, x + width, y + squareHeight,
                0x00000000, 0xff000000);
        int hueSegments = 32;
        int segmentWidth = Math.max(1, hueWidth / hueSegments);
        for (int segment = 0; segment < hueSegments; segment++) {
            int segmentStart = x + 8 + segment * hueWidth / hueSegments;
            int segmentEnd = x + 8 + (segment + 1) * hueWidth / hueSegments;
            int color = Color.HSBtoRGB(segment / (float) hueSegments, 1f, 1f) | 0xff000000;
            graphics.fill(segmentStart, hueY, segmentEnd, hueY + 8, color);
        }
        circle(graphics, x + Math.round(saturation * width), y + Math.round((1f - value) * squareHeight),
                6, 0xffffffff);
        circle(graphics, x + 8 + Math.round(hue * hueWidth), hueY + 4, 6, 0xffffffff);
        graphics.fill(x + width - 28, y + height - 16, x + width - 8, y + height - 6, base);
    }

    public boolean mouseClicked(double mouseX, double mouseY) {
        if (inSquare(mouseX, mouseY)) {
            draggingSquare = true;
            updateSquare(mouseX, mouseY);
            return true;
        }
        if (inHue(mouseX, mouseY)) {
            draggingHue = true;
            updateHue(mouseX);
            return true;
        }
        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY) {
        if (draggingSquare) {
            updateSquare(mouseX, mouseY);
            return true;
        }
        if (draggingHue) {
            updateHue(mouseX);
            return true;
        }
        return false;
    }

    public void mouseReleased() {
        draggingSquare = false;
        draggingHue = false;
    }

    private void updateSquare(double mouseX, double mouseY) {
        saturation = clamp((float) ((mouseX - x) / width));
        value = 1f - clamp((float) ((mouseY - y) / (height - 38)));
        consumer.accept(color());
    }

    private void updateHue(double mouseX) {
        hue = clamp((float) ((mouseX - x - 8) / (width - 16)));
        consumer.accept(color());
    }

    private boolean inSquare(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY < y + height - 38;
    }

    private boolean inHue(double mouseX, double mouseY) {
        return mouseX >= x + 8 && mouseX <= x + width - 8
                && mouseY >= y + height - 24 && mouseY <= y + height - 14;
    }

    private static float clamp(float value) {
        return Math.max(0f, Math.min(1f, value));
    }

    private static void circle(GuiGraphicsExtractor graphics, int centerX, int centerY, int radius, int color) {
        for (int row = -radius; row <= radius; row++) {
            int half = (int) Math.sqrt(radius * radius - row * row);
            graphics.fill(centerX - half, centerY + row, centerX + half + 1, centerY + row + 1, color);
        }
    }
}
