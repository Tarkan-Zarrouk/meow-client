package Main.main.meow.client.modules.hud;

import Main.main.meow.client.modules.client.ClientModule;
import Main.main.meow.client.modules.category.Category;

public final class BiomeModule extends ClientModule {
    public BiomeModule() { super("Biome", Category.HUD, true, "Displays the biome at your current position."); }
}
