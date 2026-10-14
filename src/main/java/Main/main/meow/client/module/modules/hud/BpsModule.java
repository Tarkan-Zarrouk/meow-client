package Main.main.meow.client.module.modules.hud;

import Main.main.meow.client.MeowClient;
import Main.main.meow.client.module.HudDisplayModule;
import Main.main.meow.client.module.modules.ClientModule;
import Main.main.meow.client.setting.modulesettings.BooleanSetting;
import Main.main.meow.client.setting.modulesettings.EnumSetting;
import Main.main.meow.client.setting.modulesettings.colorpicker.ColorSetting;
import Main.main.meow.client.ui.clickgui.Category;
import Main.main.meow.client.ui.hud.QolHudRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;

public final class BpsModule extends HudDisplayModule {
    private static Vec3 lastPosition = null;
    private static double currentBps = 0.0;
    private enum Modes {
        MPH,
        KMH,
        BPS
    }
    private static final BooleanSetting SHOW_BACKGROUND = new BooleanSetting("Toggle Background", "Show a background", true);
    private static final ColorSetting BACKGROUND_COLOUR = new ColorSetting("Background Color", "Change the background colour", QolHudRenderer.getPanel());
    private static final EnumSetting<Modes> UNITS_OF_MEASUREMENT = new EnumSetting<>("Units", "Swap between MPH, KMH, BPS", Modes.BPS);

    public BpsModule() {
        super("BPS", Category.HUD, false, "Displays your horizontal movement speed in blocks per second.");
        addSetting(SHOW_BACKGROUND, BACKGROUND_COLOUR, UNITS_OF_MEASUREMENT);
    }

    public static void drawInfo(GuiGraphicsExtractor g, Minecraft client, LocalPlayer player, int x, int y) {
        double speed = switch(UNITS_OF_MEASUREMENT.value()) {
            case MPH -> getCurrentMph();
            case KMH -> getCurrentKmh();
            case BPS -> currentBps;
        };
        String text = String.format("%.2f %s", speed, UNITS_OF_MEASUREMENT.value().name());
        int textWidth = QolHudRenderer.getTextWidth(client, text);
        int width = textWidth + 4;
        int height = 18;
        if (SHOW_BACKGROUND.enabled()) {
            QolHudRenderer.panel(g, x, y, width, height, BACKGROUND_COLOUR.color());
        }
        QolHudRenderer.text(g, client, text, x + 2, y + 5, QolHudRenderer.getTEXT());
    }

    public static double getCurrentMph() {
        // assume one block width is the size of a meter
        return currentBps * 2.237;
    }

    public static double getCurrentKmh() {
        // again, assuming one block is the size of a meter,
        return currentBps * 3.6;
    }


    public static void getBps(Minecraft client) {
        LocalPlayer player = client.player;
        Vec3 latestPlayerPosition = null;
        if(player != null) {
            latestPlayerPosition = player.position();
            if(lastPosition != null) {
                currentBps = lastPosition.distanceTo(latestPlayerPosition) * 20;
            } else {
                currentBps = 0.0;
            }
        }
        lastPosition = latestPlayerPosition;
    }



    @Override
    public int panelWidth(Minecraft client) {
        return 0;
    }

    @Override
    public int panelHeight(Minecraft client) {
        return 0;
    }
}
