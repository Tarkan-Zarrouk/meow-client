package Main.main.meow.client.modules.visuals;

import Main.main.meow.client.hud.QolHudRenderer;
import Main.main.meow.client.modules.hud.HudDisplayModule;
import Main.main.meow.client.modules.category.Category;
import Main.main.meow.client.modules.setting.ColorSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;

import static Main.main.meow.client.hud.QolHudRenderer.getPanel;

public final class KeystrokesModule extends HudDisplayModule {
    private static long lastKeyTime = System.nanoTime();
    private static final ColorSetting COLOR_OF_BACKGROUND = new ColorSetting("Color of Background", "The background of the each key", QolHudRenderer.getPanel());
    private static final float[] KEY_LEVELS = new float[7];
    public KeystrokesModule() {
        super("Keystrokes", Category.VISUALS, true, "Shows keyboard movement inputs and smoothly animates pressed keys on the HUD.");
        addSetting(COLOR_OF_BACKGROUND);
    }

    public static void drawInfo(GuiGraphicsExtractor g, Minecraft client, LocalPlayer player, int x, int y, int colourFromUser) {
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
        g.fill(x, y, x + 122, y + 122, getPanel());
        key(g, client, "W", x + 41, y + 8, 40, 28, KEY_LEVELS[0], colourFromUser);
        key(g, client, "A", x + 7, y + 40, 40, 28, KEY_LEVELS[1], colourFromUser);
        key(g, client, "S", x + 41, y + 40, 40, 28, KEY_LEVELS[2], colourFromUser);
        key(g, client, "D", x + 75, y + 40, 40, 28, KEY_LEVELS[3], colourFromUser);
        key(g, client, "SPACE", x + 7, y + 72, 108, 22, KEY_LEVELS[4], colourFromUser);
        key(g, client, "LMB", x + 7, y + 96, 52, 22, KEY_LEVELS[5], colourFromUser);
        key(g, client, "RMB", x + 63, y + 96, 52, 22, KEY_LEVELS[6], colourFromUser);
    }

    public int getBackgroundColour() {
        return COLOR_OF_BACKGROUND.color();
    }

    public static void key(GuiGraphicsExtractor g, Minecraft client, String label, int x, int y, int width, int height, float level, int colorFromUser) {
        int color = blend(colorFromUser, 0xff8d3e9a, level);
        g.fill(x, y, x + width, y + height, color);
        int textWidth = client.font.width(label);
        g.text(client.font, label, x + (width - textWidth) / 2, y + (height - 8) / 2, blend(QolHudRenderer.getTEXT(), 0xffffffff, level));
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
