package Main.main.meow.client.modules.hud;

import Main.main.meow.client.modules.client.ClientModule;
import Main.main.meow.client.modules.category.Category;

public final class DirectionModule extends ClientModule {
    public DirectionModule() { super("Direction", Category.HUD, true, "Displays the compass direction your player is facing."); }
}
