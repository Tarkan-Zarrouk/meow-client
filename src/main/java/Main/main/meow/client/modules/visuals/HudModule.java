package Main.main.meow.client.modules.visuals;

import Main.main.meow.client.modules.client.ClientModule;
import Main.main.meow.client.modules.category.Category;

public final class HudModule extends ClientModule {
    public HudModule() { super("HUD", Category.VISUALS, true, "Displays general player information such as FPS, ping, coordinates, direction, and biome."); }
}
