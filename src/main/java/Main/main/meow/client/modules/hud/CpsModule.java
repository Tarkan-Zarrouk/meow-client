package Main.main.meow.client.modules.hud;

import Main.main.meow.client.modules.client.ClientModule;
import Main.main.meow.client.modules.category.Category;

public final class CpsModule extends ClientModule {
    public CpsModule() { super("CPS", Category.HUD, true, "Displays your clicks per second based on recent attack or interaction clicks."); }
}
