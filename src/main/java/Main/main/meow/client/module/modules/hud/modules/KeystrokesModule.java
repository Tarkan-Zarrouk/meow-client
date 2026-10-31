package Main.main.meow.client.module.modules.hud.modules;

import Main.main.meow.client.module.modules.hud.HudDisplayModule;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.BooleanSetting;
import Main.main.meow.client.ui.clickgui.model.Category;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.colorpicker.ColorSetting;
import Main.main.meow.client.util.RenderUtil;
import Main.main.meow.client.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;


public final class KeystrokesModule extends HudDisplayModule {
    private static long lastKeyTime = System.nanoTime();
    private static final ColorSetting COLOR_OF_KEYS = new ColorSetting("Color of Background", "The background of the each key", RenderUtil.getGuiColorPanel());
    private static final ColorSetting TEXT_COLOR = new ColorSetting("Text Color", "Change the text color", RenderUtil.getGuiTextColor());
    private static final BooleanSetting SHOW_BACKGROUND = new BooleanSetting("Show Background", "Toggle the background of your panel", true);
    private static final ColorSetting BACKGROUND_COLOR = new ColorSetting("Color of Background", "The background of the keystrokes background", RenderUtil.getGuiColorPanel());
    private static final ColorSetting COLOR_OF_KEY_ONE = new ColorSetting("Key 1 Color", "Background color of Key 1", RenderUtil.getGuiColorPanel());
    private static final ColorSetting COLOR_OF_KEY_TWO = new ColorSetting("Key 2 Color", "Background color of Key 2", RenderUtil.getGuiColorPanel());
    private static final ColorSetting COLOR_OF_KEY_THREE = new ColorSetting("Key 3 Color", "Background color of Key 3", RenderUtil.getGuiColorPanel());
    private static final ColorSetting COLOR_OF_KEY_FOUR = new ColorSetting("Key 4 Color", "Background color of Key 4", RenderUtil.getGuiColorPanel());
    private static final ColorSetting COLOR_OF_KEY_FIVE = new ColorSetting("Key 5 Color", "Background color of Key 5", RenderUtil.getGuiColorPanel());
    private static final ColorSetting COLOR_OF_KEY_SIX = new ColorSetting("Key 6 Color", "Background color of Key 6", RenderUtil.getGuiColorPanel());
    private static final ColorSetting COLOR_OF_KEY_SEVEN = new ColorSetting("Key 7 Color", "Background color of Key 7", RenderUtil.getGuiColorPanel());
    private static final float[] KEY_LEVELS = new float[7];
    public KeystrokesModule() {
        super("Keystrokes", Category.HUD, false, "Shows keyboard movement inputs and smoothly animates pressed keys on the HUD.");
        super.addSetting(SHOW_BACKGROUND, COLOR_OF_KEYS, BACKGROUND_COLOR, COLOR_OF_KEY_ONE, COLOR_OF_KEY_TWO, COLOR_OF_KEY_THREE, COLOR_OF_KEY_FOUR, COLOR_OF_KEY_FIVE, COLOR_OF_KEY_SIX, COLOR_OF_KEY_SEVEN);
    }
    @Override
    public void drawInfo(GuiGraphicsExtractor g, Minecraft client, LocalPlayer player, int x, int y) {
        // this entire method is ai, i lowkey have no idea how i could do this without it being buggy lol
        long now = System.nanoTime();
        float delta = Math.min(0.05f, (now - lastKeyTime) / 1000000000f);
        lastKeyTime = now;
        // whether the keys are perssed or not
        boolean[] pressed = {player.input.keyPresses.forward(), player.input.keyPresses.left(), player.input.keyPresses.backward(), player.input.keyPresses.right(), player.input.keyPresses.jump(), client.mouseHandler.isLeftPressed(), client.mouseHandler.isRightPressed()};
        for (int i = 0; i < KEY_LEVELS.length; i++) {
            float target;
            if (pressed[i]) {
                target = 1f;
            } else {
                target = 0f;
            }
            KEY_LEVELS[i] += (target - KEY_LEVELS[i]) * (1f - (float) Math.exp(-delta * 18f)); // genuinely what is this :sob:
        }
        if (SHOW_BACKGROUND.value()) {
            RenderUtil.drawPanel(g, x, y, 122, 122, BACKGROUND_COLOR.color());
        }
        key(g, client, "W", x + 41, y + 8, 40, 28, KEY_LEVELS[0], COLOR_OF_KEY_ONE.color());
        key(g, client, "A", x + 7, y + 40, 40, 28, KEY_LEVELS[1], COLOR_OF_KEY_TWO.color());
        key(g, client, "S", x + 41, y + 40, 40, 28, KEY_LEVELS[2], COLOR_OF_KEY_THREE.color());
        key(g, client, "D", x + 75, y + 40, 40, 28, KEY_LEVELS[3], COLOR_OF_KEY_FOUR.color());
        key(g, client, "SPACE", x + 7, y + 72, 108, 22, KEY_LEVELS[4], COLOR_OF_KEY_FIVE.color());
        key(g, client, "LMB", x + 7, y + 96, 52, 22, KEY_LEVELS[5], COLOR_OF_KEY_SIX.color());
        key(g, client, "RMB", x + 63, y + 96, 52, 22, KEY_LEVELS[6], COLOR_OF_KEY_SEVEN.color());
    }


    public static void key(GuiGraphicsExtractor g, Minecraft client, String label, int x, int y, int width, int height, float level, int keyColor) {
        int color = blend(keyColor, COLOR_OF_KEYS.color(), level);
        g.fill(x, y, x + width, y + height, color);
        RenderUtil.drawText(g, client, label, x + width / 2 - TextUtil.getTextWidth(client, label) / 2, y + height / 2 - TextUtil.getTextHeight(client) / 2, keyColor);
    }
    public static int blend(int from, int to, float amount) {
        // i had to look up whatever the hell this does, but it basically blends two colors together based on the amount (0-1)
        int r = (int) (((from >> 16 & 255) * (1 - amount)) + ((to >> 16 & 255) * amount));
        int g = (int) (((from >> 8 & 255) * (1 - amount)) + ((to >> 8 & 255) * amount));
        int b = (int) (((from & 255) * (1 - amount)) + ((to & 255) * amount));
        return 0xff000000 | r << 16 | g << 8 | b;
    }

    @Override
    public int panelWidth(Minecraft client) {
        return 122;
    }

    @Override
    public int panelHeight(Minecraft client) {
        return 122;
    }
}
