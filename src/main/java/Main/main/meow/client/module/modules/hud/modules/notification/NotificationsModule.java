package Main.main.meow.client.module.modules.hud.modules.notification;

import Main.main.meow.client.module.modules.ClientModule;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.NumberSetting;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.colorpicker.ColorSetting;
import Main.main.meow.client.ui.clickgui.model.Category;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.BooleanSetting;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.EnumSetting;
import Main.main.meow.client.util.RenderUtil2D;

public final class NotificationsModule extends ClientModule {
    private static final EnumSetting<Mode> MODE = new EnumSetting<>("Type", "Switches between pink [MeowMod] chat messages and animated top-right alerts.", Mode.CHAT);
    private static final BooleanSetting SHOW_BACKGROUND = new BooleanSetting("Show Background", "Toggle the background of your panel", true);
    private static final ColorSetting BACKGROUND_COLOR = new ColorSetting("Background Color", "Change the color of the background", 0xff202632);
    private static final ColorSetting TEXT_COLOR = new ColorSetting("Text Color", "Change the color of the text", RenderUtil2D.getGuiTextColor());
    private static final ColorSetting TIMER_COLOR = new ColorSetting("Timer Setting", "Change the color of the timer", 0xffc52dbd);
    private static final BooleanSetting SHOW_TIMER = new BooleanSetting("Timer", "Shows or hides the magenta timer bar that drains until the alert is cleared.", true);
    private static final NumberSetting BORDER_RADIUS = new NumberSetting("Border Radius", "Change the roundedness of the notifcation popup", 16, 5, 20);
    private static final EnumSetting<NotificationPosition> POSITION_NOTIFICATION = new EnumSetting<>("Notification Position", "Change the position of the notification", NotificationPosition.TOP_RIGHT);
    private static final NumberSetting ENTER_TIME = new NumberSetting("Notification Enter Time", "Change the time it takes for a notification to appear (ms)", 240, 100, 1000);
    private static final NumberSetting DISPLAY_TIME = new NumberSetting("Notification Display Time", "Change the time it takes for a notification to appear on screen", 2600, 1000, 5000);
    private static final NumberSetting EXIT_TIME = new NumberSetting("Notification Exit Time", "The time it takes to leave the screen", 240, 200, 1000);
    private static final BooleanSetting TOP_LEFT_ROUND = new BooleanSetting("Top Left Rounded", "Toggle the top left corner of the notification to be rounded or not", true);
    private static final BooleanSetting TOP_RIGHT_ROUND = new BooleanSetting("Top Right Rounded", "Toggle the top right corner of the notification to be rounded or not", true);
    private static final BooleanSetting BOTTOM_LEFT_ROUND = new BooleanSetting("Bottom Left Rounded", "Toggle the bottom left corner of the notification to be rounded or not", true);
    private static final BooleanSetting BOTTOM_RIGHT_ROUND = new BooleanSetting("Bottom Right Rounded", "Toggle the bottom right corner of the notification to be rounded or not", true);
    private static final BooleanSetting TIMER_TOP_LEFT_ROUND = new BooleanSetting("Timer Top Left Rounded", "Toggle the top left corner of the timer to be rounded or not", true);
    private static final BooleanSetting TIMER_TOP_RIGHT_ROUND = new BooleanSetting("Timer Top Right Rounded", "Toggle the top right corner of the timer to be rounded or not", true);
    private static final BooleanSetting TIMER_BOTTOM_LEFT_ROUND = new BooleanSetting("Timer Bottom Left Rounded", "Toggle the bottom left corner of the timer to be rounded or not", true);
    private static final BooleanSetting TIMER_BOTTOM_RIGHT_ROUND = new BooleanSetting("Timer Bottom Right Rounded", "Toggle the bottom right corner of the timer to be rounded or not", true);
    private static final NumberSetting SLIDE_TIME_VAL = new NumberSetting("Slide In / Out Time", "Modify the speed at which enters and exits", 18, 10, 100);
    private static final NumberSetting NOTIFICATION_HEIGHT = new NumberSetting("Height of Notification", "Does this really need an explanation?", 32, 20, 50);
    private static final NumberSetting NOTIFICATION_WIDTH = new NumberSetting("Width of Notification", "See tooltip above", 28, 10, 50);
    private static final BooleanSetting SHOW_STATUS_BAR = new BooleanSetting("Show Status Bar", "Show the status bar on the left side of alerts.", true);
    private static final ColorSetting STATUS_NORMAL_COLOR = new ColorSetting("Normal Status Color", "Color of the status bar for non-emergency alerts.", 0xff00ff00);
    private static final ColorSetting STATUS_EMERGENCY_COLOR = new ColorSetting("Emergency Status Color", "Color of the status bar for emergency alerts.", 0xffff0000);
    private static final NumberSetting STATUS_BAR_WIDTH = new NumberSetting("Status Bar Width", "Width of the full-height status bar.", 3, 1, 16);
    private static final NumberSetting STATUS_BAR_RADIUS = new NumberSetting("Status Bar Radius", "Corner radius of the status bar, limited by its width.", 0, 0, 8);
    private static final BooleanSetting STATUS_TOP_LEFT_ROUND = new BooleanSetting("Status Top Left Rounded", "Round the top left corner of the status bar.", true);
    private static final BooleanSetting STATUS_TOP_RIGHT_ROUND = new BooleanSetting("Status Top Right Rounded", "Round the top right corner of the status bar.", true);
    private static final BooleanSetting STATUS_BOTTOM_LEFT_ROUND = new BooleanSetting("Status Bottom Left Rounded", "Round the bottom left corner of the status bar.", true);
    private static final BooleanSetting STATUS_BOTTOM_RIGHT_ROUND = new BooleanSetting("Status Bottom Right Rounded", "Round the bottom right corner of the status bar.", true);
    public NotificationsModule() {
        super("Notifications", Category.HUD, false, "Shows module toggle notifications in chat or as animated top-right alerts.");
        super.addSetting(
                MODE,
                SHOW_TIMER,
                BACKGROUND_COLOR, TEXT_COLOR, TIMER_COLOR,
                SHOW_STATUS_BAR, STATUS_NORMAL_COLOR, STATUS_EMERGENCY_COLOR, STATUS_BAR_WIDTH, STATUS_BAR_RADIUS,
                STATUS_TOP_LEFT_ROUND, STATUS_TOP_RIGHT_ROUND, STATUS_BOTTOM_LEFT_ROUND, STATUS_BOTTOM_RIGHT_ROUND,
                BORDER_RADIUS,
                ENTER_TIME, DISPLAY_TIME, EXIT_TIME,
                POSITION_NOTIFICATION,
                SHOW_BACKGROUND,
                TOP_LEFT_ROUND, TOP_RIGHT_ROUND, BOTTOM_LEFT_ROUND, BOTTOM_RIGHT_ROUND, TIMER_TOP_LEFT_ROUND, TIMER_TOP_RIGHT_ROUND, TIMER_BOTTOM_LEFT_ROUND, TIMER_BOTTOM_RIGHT_ROUND,
                SLIDE_TIME_VAL,
                NOTIFICATION_HEIGHT,
                NOTIFICATION_WIDTH
        );
    }
    public static int getNotificationHeight() {
        return NOTIFICATION_HEIGHT.value();
    }
    public static boolean getShowStatusBar() {
        return SHOW_STATUS_BAR.enabled();
    }
    public static int getStatusBarColor(boolean emergency) {
        if (emergency) return STATUS_EMERGENCY_COLOR.color();
        return STATUS_NORMAL_COLOR.color();
    }
    public static int getStatusBarWidth() {
        return STATUS_BAR_WIDTH.value();
    }
    public static int getStatusBarRadius() {
        return STATUS_BAR_RADIUS.value();
    }
    public static boolean getStatusTopLeftRound() {
        return STATUS_TOP_LEFT_ROUND.enabled();
    }
    public static boolean getStatusTopRightRound() {
        return STATUS_TOP_RIGHT_ROUND.enabled();
    }
    public static boolean getStatusBottomLeftRound() {
        return STATUS_BOTTOM_LEFT_ROUND.enabled();
    }
    public static boolean getStatusBottomRightRound() {
        return STATUS_BOTTOM_RIGHT_ROUND.enabled();
    }
    public static int getNotificationWidth() {
        return NOTIFICATION_WIDTH.value();
    }
    public static int getSlideTimeVal() {
        return SLIDE_TIME_VAL.value();
    }
    public static boolean getTimerTopLeftRound() {
        return TIMER_TOP_LEFT_ROUND.enabled();
    }

    public static boolean getTimerTopRightRound() {
        return TIMER_TOP_RIGHT_ROUND.enabled();
    }

    public static boolean getTimerBottomLeftRound() {
        return TIMER_BOTTOM_LEFT_ROUND.enabled();
    }

    public static boolean getTimerBottomRightRound() {
        return TIMER_BOTTOM_RIGHT_ROUND.enabled();
    }

    public static boolean getShowBackground() {
        return SHOW_BACKGROUND.enabled();
    }

    public static boolean getTopLeftRound() {
        return TOP_LEFT_ROUND.enabled();
    }

    public static boolean getTopRightRound() {
        return TOP_RIGHT_ROUND.enabled();
    }

    public static boolean getBottomLeftRound() {
        return BOTTOM_LEFT_ROUND.enabled();
    }

    public static boolean getBottomRightRound() {
        return BOTTOM_RIGHT_ROUND.enabled();
    }

    public static int getEnterTime() {
        return ENTER_TIME.value();
    }
    public static int getDisplayTime() {
        return DISPLAY_TIME.value();
    }
    public static int getExitTime() {
        return EXIT_TIME.value();
    }
    public static NotificationPosition getNotificationPosition() {
        return POSITION_NOTIFICATION.value();
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
