package Main.main.meow.client.modules.hud;

import Main.main.meow.client.modules.client.ClientModule;
import Main.main.meow.client.modules.category.Category;

public final class TotemCounterModule extends ClientModule {
    public TotemCounterModule() {
        super("Totem Counter", Category.HUD, true, "Displays the number of Totems of Undying in your inventory.");
    }
}
