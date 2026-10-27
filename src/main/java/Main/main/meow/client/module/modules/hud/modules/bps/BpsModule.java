package Main.main.meow.client.module.modules.hud.modules.bps;

import Main.main.meow.client.module.modules.hud.HudDisplayModule;
import Main.main.meow.client.module.modules.hud.annotation.RequireHudModuleFields;
import Main.main.meow.client.setting.modulesettings.EnumSetting;
import Main.main.meow.client.setting.modulesettings.colorpicker.ColorSetting;
import Main.main.meow.client.ui.clickgui.Category;
import Main.main.meow.client.util.RenderUtil;
import Main.main.meow.client.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;

@RequireHudModuleFields
public final class BpsModule extends HudDisplayModule {
    private static int panelWidthValue = 0;
    private static int panelHeightValue = 0;
    private static Vec3 lastPosition = null;
    private static double currentBps = 0.0;
    private static final ColorSetting BACKGROUND_COLOR = new ColorSetting("Background Color", "Change the background colour", RenderUtil.getGuiColorPanel());
    private static final ColorSetting TEXT_COLOR = new ColorSetting("Text color", "Change the text color", RenderUtil.getGuiTextColor());
    private static final EnumSetting<Mode> UNITS_OF_MEASUREMENT = new EnumSetting<>("Units", "Swap between MPH, KMH, BPS", Mode.BPS);

    public BpsModule() {
        super("BPS", Category.HUD, false, "Displays your horizontal movement speed in blocks per second.");
        super.addSetting(BACKGROUND_COLOR, TEXT_COLOR, UNITS_OF_MEASUREMENT);
    }
    @Override
    public void drawInfo(GuiGraphicsExtractor g, Minecraft client, LocalPlayer player, int x, int y) {
        double speed = switch(UNITS_OF_MEASUREMENT.value()) {
            case MPH -> getCurrentMph();
            case KMH -> getCurrentKmh();
            case BPS -> currentBps;
        };
        String text = String.format("%.2f %s", speed, UNITS_OF_MEASUREMENT.value().name());
        int textWidth = TextUtil.getTextWidth(client, text);
        int width = textWidth + 4;
        int height = 18;
        panelWidthValue = width;
        panelHeightValue = height;
        RenderUtil.drawPanel(g, x, y, width, height, BACKGROUND_COLOR.color());
        RenderUtil.drawText(g, client, text, x + 2, y + 5, TEXT_COLOR.color());
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
        return panelWidthValue;
    }

    @Override
    public int panelHeight(Minecraft client) {
        return panelHeightValue;
    }
}
