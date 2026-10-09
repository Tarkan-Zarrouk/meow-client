package Main.main.meow.client.modules.visuals;

import Main.main.meow.client.modules.client.ClientModule;
import Main.main.meow.client.modules.category.Category;

public final class KeystrokesModule extends ClientModule {
    public KeystrokesModule() {
        super("Keystrokes", Category.VISUALS, true, "Shows keyboard movement inputs and smoothly animates pressed keys on the HUD.");
    }
}
