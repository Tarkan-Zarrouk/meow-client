package Main.main.meow.client.ui.clickgui.view.setting.modulesettings.colorpicker;

import Main.main.meow.client.module.modules.client.clickgui.ClickGuiModule;
import Main.main.meow.client.util.RenderUtil;
import Main.main.meow.client.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

import java.awt.Color;

public final class ColorPicker implements ColorConsumer {

    private static final int SQUARE_INSET = 54;
    private final int width;
    private final int height;
    private final ColorConsumer consumer;
    private int x;
    private int y;
    private float hue;
    private float saturation;
    private float value;
    private int alpha = 255;
    private boolean draggingSquare;
    private boolean draggingHue;
    private boolean draggingAlpha;

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
    public int color() { return (Color.HSBtoRGB(hue, saturation, value) & 0x00ffffff) | (alpha << 24); }

    public void setColor(int color) {
        alpha = color >>> 24;
        float[] hsv = Color.RGBtoHSB(color >> 16 & 255, color >> 8 & 255, color & 255, null);
        hue = hsv[0];
        saturation = hsv[1];
        value = hsv[2];
    }

    public void render(GuiGraphicsExtractor graphics) {
        int squareHeight = height - SQUARE_INSET;
        int hueY = y + squareHeight + 14;
        int hueWidth = width - 16;
        int base = Color.HSBtoRGB(hue, 1f, 1f) | 0xff000000;
        RenderUtil.drawPanel(graphics, x, y, width, height, 0xee20232a);
        RenderUtil.drawGradientPanel(graphics, x, y, width, squareHeight, 0xffffffff, base);
        RenderUtil.drawGradientPanel(graphics, x, y, width, squareHeight, 0x00000000, 0xff000000);
        RenderUtil.drawCircle(graphics,x + Math.round(saturation * width), y + Math.round((1f - value) * squareHeight), 6, 0xffffffff);
        drawColorSlider(graphics, hueWidth, hueY);
        drawAlphaSlider(graphics, hueWidth, y + height - 24);
        drawCopyTextButtonHelper(graphics);
        drawColorPreviewHelper(graphics);
    }
    private void drawColorSlider(GuiGraphicsExtractor graphics, int hueWidth, int hueY) {
        int hueSegments = 32;
        for (int segment = 0; segment < hueSegments; segment++) {
            int segmentStart = x + 8 + segment * hueWidth / hueSegments;
            int segmentEnd = x + 8 + (segment + 1) * hueWidth / hueSegments;
            int color = Color.HSBtoRGB(segment / (float) hueSegments, 1f, 1f) | 0xff000000;
            graphics.fill(segmentStart, hueY, segmentEnd, hueY + 8, color);
        }
        RenderUtil.drawCircle(graphics,x + 8 + Math.round(hue * hueWidth), hueY + 4, 6, 0xffffffff);
    }

    private void drawAlphaSlider(GuiGraphicsExtractor graphics, int sliderWidth, int sliderY) {
        int left = x + 8;
        int right = x + sliderWidth - 50;
        RenderUtil.drawCircle(graphics, left + Math.round((1f - alpha / 255f) * sliderWidth), sliderY + 4, 6, 0xffffffff);
        RenderUtil.roundedFill(graphics,  left, sliderY + 4, width - 17, ClickGuiModule.getAlphaChannelHeight(),  ClickGuiModule.getAlphaChannelBackgroundColor(), ClickGuiModule.getTopLeftAlphaChannelRound(), ClickGuiModule.getTopRightAlphaChannelRound(), ClickGuiModule.getBottomLeftAlphaChannelRound(), ClickGuiModule.getBottomRightAlphaChannelRound(), ClickGuiModule.getAlphaChannelBorderRadius());
        RenderUtil.drawText(graphics, Minecraft.getInstance().font, Component.literal("Opaque"), left - 4, sliderY + 12, ClickGuiModule.getCopyTextColorSetting(), false);
        RenderUtil.drawText(graphics, Minecraft.getInstance().font, Component.literal("Transparent"), right, sliderY + 12, ClickGuiModule.getCopyTextColorSetting(), false);
        RenderUtil.drawText(graphics, Minecraft.getInstance().font, Component.literal(String.format("%d%%", Math.round(alpha / 255f * 100))), x + (sliderWidth / 2), sliderY + 12, ClickGuiModule.getCopyTextColorSetting(), false);
    }

    private void drawCopyTextButtonHelper(GuiGraphicsExtractor graphics) {
        Component copyText = Component.literal("Copy");
        int xPos = width / 2 - 5;
        int yPos = height + 12;
        RenderUtil.drawPanel(graphics, x +xPos, y + yPos, TextUtil.getTextWidth(Minecraft.getInstance(), copyText.getString()) + 5, TextUtil.getTextHeight(Minecraft.getInstance()) + 5, ClickGuiModule.getCopyButtonColorSetting());
        RenderUtil.drawText(graphics, Minecraft.getInstance().font, Component.literal("Copy"), x + xPos + 2, y + yPos + 3, ClickGuiModule.getCopyTextColorSetting(), ClickGuiModule.getCopyButtonDropShadow());
    }

    private void drawColorPreviewHelper(GuiGraphicsExtractor graphics) {
        RenderUtil.drawText(graphics, Minecraft.getInstance(), Component.literal("Color: ").append(Component.literal(String.format("#%08X", color())).withStyle(style -> style.withColor(color()))), x, y - 9, RenderUtil.getGuiTextColor());
        RenderUtil.drawPanel(graphics, x + 90, y - 10, 10, 10, color());
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
        if (inAlpha(mouseX, mouseY)) {
            draggingAlpha = true;
            updateAlpha(mouseX);
            return true;
        }
        if(inCopy(mouseX, mouseY)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(String.format("#%08X", color()));
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
        if (draggingAlpha) {
            updateAlpha(mouseX);
            return true;
        }
        return false;
    }

    public void mouseReleased() {
        draggingSquare = false;
        draggingHue = false;
        draggingAlpha = false;
    }

    private void updateSquare(double mouseX, double mouseY) {
        saturation = clamp((float) ((mouseX - x) / width));
        value = 1f - clamp((float) ((mouseY - y) / (height - SQUARE_INSET)));
        consumer.accept(color());
    }

    private void updateHue(double mouseX) {
        hue = clamp((float) ((mouseX - x - 8) / (width - 16)));
        consumer.accept(color());
    }

    private void updateAlpha(double mouseX) {
        alpha = Math.round((1f - clamp((float) ((mouseX - x - 8) / (width - 16)))) * 255);
        consumer.accept(color());
    }

    private boolean inAlpha(double mouseX, double mouseY) {
        return mouseX >= x + 8 && mouseX <= x + width - 8
                && mouseY >= y + height - 24 && mouseY <= y + height - 14;
    }

    private boolean inSquare(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY < y + height - SQUARE_INSET;
    }

    private boolean inHue(double mouseX, double mouseY) {
        return mouseX >= x + 8 && mouseX <= x + width - 8
                && mouseY >= y + height - 40 && mouseY <= y + height - 30;
    }

    private boolean inCopy(double mouseX, double mouseY) {
        int xPos = width / 2 - 5;
        int yPos = height + 12;
        return mouseX >= x + xPos && mouseX <= x + xPos + TextUtil.getTextWidth(Minecraft.getInstance(), "Copy") + 5
                && mouseY >= y + yPos && mouseY <= y + yPos + TextUtil.getTextHeight(Minecraft.getInstance()) + 5;
    }

    private static float clamp(float value) {
        return Math.clamp(value, 0f, 1f);
    }

    @Override
    public void accept(int color) {
        this.setColor(color);
    }
}
