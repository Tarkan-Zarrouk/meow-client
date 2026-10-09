package Main.main.meow.client.modules.player;

import Main.main.meow.client.modules.client.ClientModule;
import Main.main.meow.client.modules.category.Category;

public final class ArmorDurabilityModule extends ClientModule {
    public ArmorDurabilityModule() {
        super("Armor Durability", Category.PLAYER, true, "Displays detailed durability values for equipped armor pieces.");
    }
}
