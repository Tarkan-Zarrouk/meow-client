package Main.main.meow.client.module.modules.hud.modules.info;

import Main.main.meow.client.module.modules.hud.HudDisplayModule;
import Main.main.meow.client.setting.modulesettings.BooleanSetting;
import Main.main.meow.client.ui.clickgui.Category;
import Main.main.meow.client.setting.modulesettings.colorpicker.ColorSetting;
import Main.main.meow.client.setting.modulesettings.EnumSetting;
import Main.main.meow.client.util.RenderUtil;
import Main.main.meow.client.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;

import java.util.Objects;


public final class InfoModule extends HudDisplayModule {
    private static double tps = 20.0;
    private static final EnumSetting<Modes> INFO_CYCLE = new EnumSetting<>("Info Cycle", "Toggle between Showing fps and tps and ping, showing fps and ping, showing fps and showing", Modes.SHOW_FPS_PING_TPS);
    private static final BooleanSetting SHOW_BACKGROUND = new BooleanSetting("Show Background", "Toggle the background of your panel", true);
    private static final ColorSetting BACKGROUND_COLOR = new ColorSetting("Background Colour", "Change the colour of the background", RenderUtil.getGuiTextColor());
    private static final ColorSetting TEXT_COLOR = new ColorSetting("Text Colour", "Change the colour of your text", RenderUtil.getGuiTextColor());
    public InfoModule() {
        super("Info", Category.HUD, false, "Displays information about the client on the HUD.");
        super.addSetting(SHOW_BACKGROUND, INFO_CYCLE, TEXT_COLOR, BACKGROUND_COLOR);
    }

    @Override
    public void drawInfo(GuiGraphicsExtractor g, Minecraft client, LocalPlayer player, int x, int y) {
        int fps = client.getFps();
        int ping = 0;
        if(client.getConnection() != null) {
            ping = Objects.requireNonNull(client.getConnection().getPlayerInfo(player.getUUID())).getLatency(); // i found no way around using the Objects collection
        }
        long now = System.nanoTime();
        long lastTimeUpdate = -1;
        if(lastTimeUpdate != -1) {
            double elapsedTime = (now - lastTimeUpdate) / 1000000000.0;
            tps = 20.0 / elapsedTime;
        }
        now = lastTimeUpdate;
        String text = switch(INFO_CYCLE.value()) {
            case SHOW_FPS -> "FPS: " + fps;
            case SHOW_TPS -> "TPS: " + tps;
            case SHOW_PING -> "Ping: " + ping;
            case SHOW_FPS_PING -> "FPS: " + fps + " Ping: " + ping;
            case SHOW_FPS_TPS -> "FPS: " + fps + " TPS: " + tps;
            case SHOW_PING_TPS -> "Ping: " + ping + " TPS: " + tps;
            case SHOW_FPS_PING_TPS -> "FPS: " + fps + " PING: " + ping + " TPS: " + tps;
        };
        if (SHOW_BACKGROUND.value()) {
            RenderUtil.drawPanel(g, x, y, TextUtil.getTextWidth(client, text) + 4, TextUtil.getTextHeight(client) + 4, BACKGROUND_COLOR.color());
        }
        RenderUtil.drawText(g, client, text, x + 2, y + 2, TEXT_COLOR.color());
    }

    @Override
    public int panelWidth(net.minecraft.client.Minecraft client) {
        return 100; // varying text widths, just assume 100 because im not doing allat lol
    }

    @Override
    public int panelHeight(net.minecraft.client.Minecraft client) {
        return 8;
    }
}
