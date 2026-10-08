package Main.main.meow.client.modules.hud;

import Main.main.meow.client.modules.client.ClientModule;
import Main.main.meow.client.modules.category.Category;

public final class ClockModule extends ClientModule {
    public ClockModule() { super("Clock", Category.HUD, true, "Displays the current local and world time on the HUD."); }
}
