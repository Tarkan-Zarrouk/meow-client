package Main.main.meow.client.modules.hud;

import Main.main.meow.client.modules.client.ClientModule;
import Main.main.meow.client.modules.category.Category;

public final class CoordinatesModule extends ClientModule {
    public CoordinatesModule() { super("Coordinates", Category.HUD, true, "Displays your current X, Y, and Z world coordinates."); }
}
