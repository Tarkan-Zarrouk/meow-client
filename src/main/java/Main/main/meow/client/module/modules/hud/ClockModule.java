package Main.main.meow.client.module.modules.hud;

import Main.main.meow.client.module.HudDisplayModule;
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
    private enum Modes {
        MILITARY,
        CIVIL,
    }
    private static int panelWidth, panelHeight;
    private static final EnumSetting<Modes> CLOCK_FORMAT = new EnumSetting<>("Standard", "Switches between 24h clock and 12 hour clock", Modes.MILITARY);
    private static final BooleanSetting HAVE_BACKGROUND = new BooleanSetting("Show background", "Show the background colour", true);
    private static final ColorSetting BACKGROUND_COLOUR = new ColorSetting("Background Colour", "Colour of background", RenderUtil.getGuiColorPanel());
    public ClockModule() {
        super("Clock", Category.HUD, false, "Displays the current local and world time on the HUD.");
        addSetting(CLOCK_FORMAT, HAVE_BACKGROUND, BACKGROUND_COLOUR);
    }
    public static void drawInfo(GuiGraphicsExtractor g, Minecraft client, LocalPlayer player, int x, int y, int colour) {
        LocalTime time = LocalTime.now();
        String clock = switch(CLOCK_FORMAT.value()) {
            case MILITARY -> time.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            case CIVIL -> time.format(DateTimeFormatter.ofPattern("h:mm:ss"));
        };
        panelWidth = TextUtil.getTextWidth(client, clock);
        panelHeight = TextUtil.getTextHeight(client);
        if(HAVE_BACKGROUND.enabled()) {
            RenderUtil.drawPanel(g, x, y, panelWidth + 4, panelHeight + 4, colour);
        }

        RenderUtil.drawText(g, client, clock, x, y, RenderUtil.getGuiTextColor());
    }
    public int getBackgroundColour() {
        return BACKGROUND_COLOUR.color();
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
