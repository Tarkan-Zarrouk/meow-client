package Main.main.meow.client.modules.hud;

import Main.main.meow.client.modules.client.ClientModule;
import Main.main.meow.client.modules.category.Category;
import Main.main.meow.client.modules.setting.BooleanSetting;
import Main.main.meow.client.modules.setting.EnumSetting;

public final class NotificationsModule extends ClientModule {


    public enum Mode {
        CHAT,
        ALERT
    }
    private final EnumSetting<Mode> mode = addSetting(new EnumSetting<>("Type", "Switches between pink [MeowMod] chat messages and animated top-right alerts.", Mode.CHAT));
    private final BooleanSetting showTimer = addSetting(new BooleanSetting("Timer", "Shows or hides the magenta timer bar that drains until the alert is cleared.", true, "Show Timer", "Hide Timer"));

    public NotificationsModule() {
        super("Notifications", Category.HUD, false, "Shows module toggle notifications in chat or as animated top-right alerts.");

    }

    public Mode mode() {
        return mode.value();
    }

    public boolean showTimer() {
        return showTimer.enabled();
    }
}
