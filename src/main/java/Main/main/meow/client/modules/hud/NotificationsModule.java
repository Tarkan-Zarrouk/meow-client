package Main.main.meow.client.modules.hud;

import Main.main.meow.client.modules.client.ClientModule;
import Main.main.meow.client.modules.category.Category;

public final class NotificationsModule extends ClientModule {
    public enum Mode {
        CHAT,
        ALERT
    }

    private Mode mode = Mode.CHAT;
    private boolean showTimer = true;

    public NotificationsModule() {
        super("Notifications", Category.HUD, false, "Shows module toggle notifications in chat or as animated top-right alerts.");
    }

    public Mode mode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public boolean showTimer() {
        return showTimer;
    }

    public void setShowTimer(boolean showTimer) {
        this.showTimer = showTimer;
    }
}
