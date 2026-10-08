package Main.main.meow.client.modules.hud;

import Main.main.meow.client.modules.client.ClientModule;
import Main.main.meow.client.modules.category.Category;

public final class ArrowCounterModule extends ClientModule {
    public ArrowCounterModule() { super("Arrow Counter", Category.HUD, true, "Displays the number of arrows in your inventory."); }
}
