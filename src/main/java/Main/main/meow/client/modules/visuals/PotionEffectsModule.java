package Main.main.meow.client.modules.visuals;

import Main.main.meow.client.modules.client.ClientModule;
import Main.main.meow.client.modules.category.Category;

public final class PotionEffectsModule extends ClientModule {
    public PotionEffectsModule() { super("Potion Effects", Category.VISUALS, true, "Displays active potion effects with their icons, levels, and remaining time."); }
}
