package Main.main.meow.client.module.modules.hud.modules.notification;

import Main.main.meow.client.module.modules.ClientModule;
import Main.main.meow.client.setting.NumberSetting;
import Main.main.meow.client.setting.modulesettings.colorpicker.ColorSetting;
import Main.main.meow.client.ui.clickgui.Category;
import Main.main.meow.client.setting.modulesettings.BooleanSetting;
import Main.main.meow.client.setting.modulesettings.EnumSetting;
import Main.main.meow.client.util.RenderUtil;

public final class NotificationsModule extends ClientModule {
    private static final EnumSetting<Mode> MODE = new EnumSetting<>("Type", "Switches between pink [MeowMod] chat messages and animated top-right alerts.", Mode.CHAT);
    private static final BooleanSetting SHOW_BACKGROUND = new BooleanSetting("Show Background", "Toggle the background of your panel", true);
    private static final ColorSetting BACKGROUND_COLOR = new ColorSetting("Background Color", "Change the color of the background", 0xff202632);
    private static final ColorSetting TEXT_COLOR = new ColorSetting("Text Color", "Change the color of the text", RenderUtil.getGuiTextColor());
    private static final ColorSetting TIMER_COLOR = new ColorSetting("Timer Setting", "Change the color of the timer", 0xffc52dbd);
    private static final BooleanSetting SHOW_TIMER = new BooleanSetting("Timer", "Shows or hides the magenta timer bar that drains until the alert is cleared.", true, "Show Timer", "Hide Timer");
    private static final NumberSetting BORDER_RADIUS = new NumberSetting("Border Radius", "Change the roundedness of the notifcation popup", 16, 5, 20);

    public NotificationsModule() {
        super("Notifications", Category.HUD, false, "Shows module toggle notifications in chat or as animated top-right alerts.");
        super.addSetting(MODE, SHOW_TIMER, BACKGROUND_COLOR, TEXT_COLOR, TIMER_COLOR, BORDER_RADIUS);

    }

    public static int getBorderRadiusValue() {
        return BORDER_RADIUS.value();
    }

    public static int getTimerColor() {
        return TIMER_COLOR.color();
    }

    public static int getBackgroundColor() {
        return BACKGROUND_COLOR.color();
    }

    public static int getTextColor() {
        return TEXT_COLOR.color();
    }

    public Mode mode() {
        return MODE.value();
    }

    public boolean showTimer() {
        return SHOW_TIMER.enabled();
    }
}
