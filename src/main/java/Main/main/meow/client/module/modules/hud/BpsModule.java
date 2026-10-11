package Main.main.meow.client.module.modules.hud;

import Main.main.meow.client.module.modules.ClientModule;
import Main.main.meow.client.ui.clickgui.Category;

public final class BpsModule extends ClientModule {
    public BpsModule() {
        super("BPS", Category.HUD, false, "Displays your horizontal movement speed in blocks per second.");
    }


}
