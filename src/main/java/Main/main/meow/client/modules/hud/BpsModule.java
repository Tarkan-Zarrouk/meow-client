package Main.main.meow.client.modules.hud;

import Main.main.meow.client.modules.client.ClientModule;
import Main.main.meow.client.modules.category.Category;

public final class BpsModule extends ClientModule {
    public BpsModule() { super("BPS", Category.HUD, true, "Displays your horizontal movement speed in blocks per second."); }
}
