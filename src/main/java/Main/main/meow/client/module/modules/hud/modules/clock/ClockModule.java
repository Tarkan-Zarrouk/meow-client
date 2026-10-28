package Main.main.meow.client.module.modules.hud.modules.clock;

import Main.main.meow.client.module.modules.hud.HudDisplayModule;
import Main.main.meow.client.setting.modulesettings.BooleanSetting;
import Main.main.meow.client.setting.modulesettings.EnumSetting;
import Main.main.meow.client.setting.modulesettings.colorpicker.ColorSetting;
import Main.main.meow.client.ui.clickgui.Category;
import Main.main.meow.client.util.RenderUtil;
import Main.main.meow.client.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


public final class ClockModule extends HudDisplayModule {
    private static int panelWidth, panelHeight;
    private static final EnumSetting<Modes> CLOCK_FORMAT = new EnumSetting<>("Standard", "Switches between 24h clock and 12 hour clock", Modes.MILITARY);
    private static final BooleanSetting SHOW_BACKGROUND = new BooleanSetting("Show Background", "Toggle the background of your panel", true);
    private static final ColorSetting BACKGROUND_COLOR = new ColorSetting("Background Colour", "Colour of background", RenderUtil.getGuiColorPanel());
    private static final ColorSetting TEXT_COLOR = new ColorSetting("Text Color", "Colour of text", RenderUtil.getGuiTextColor());
    public ClockModule() {
        super("Clock", Category.HUD, false, "Displays the current local and world time on the HUD.");
        super.addSetting(SHOW_BACKGROUND, CLOCK_FORMAT, BACKGROUND_COLOR, TEXT_COLOR);
    }
    @Override
    public void drawInfo(GuiGraphicsExtractor g, Minecraft client, LocalPlayer player, int x, int y) {
        LocalTime time = LocalTime.now();
        String clock = switch(CLOCK_FORMAT.value()) {
            case MILITARY -> time.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            case CIVIL -> time.format(DateTimeFormatter.ofPattern("h:mm:ss"));
        };
        panelWidth = TextUtil.getTextWidth(client, clock);
        panelHeight = TextUtil.getTextHeight(client);
        if (SHOW_BACKGROUND.value()) {
            RenderUtil.drawPanel(g, x -2, y - 2, panelWidth + 4, panelHeight + 4, BACKGROUND_COLOR.color());
        }
        RenderUtil.drawText(g, client, clock, x, y, BACKGROUND_COLOR.color());
    }

    @Override
    public int panelWidth(Minecraft client) {
        return panelWidth;
    }

    @Override
    public int panelHeight(Minecraft client) {
        return panelHeight;
    }
}
