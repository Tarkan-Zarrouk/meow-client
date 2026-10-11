package Main.main.meow.client.module.modules.hud;

import Main.main.meow.client.module.modules.client.ClientModule;
import Main.main.meow.client.ui.clickgui.Category;

public final class ClockModule extends ClientModule {
    public ClockModule() {
        super("Clock", Category.HUD, false, "Displays the current local and world time on the HUD.");
    }
}
