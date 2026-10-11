package Main.main.meow.client.module.modules.player;

import Main.main.meow.client.module.modules.client.ClientModule;
import Main.main.meow.client.ui.clickgui.Category;

public final class ArmorDurabilityModule extends ClientModule {
    public ArmorDurabilityModule() {
        super("Armor Durability", Category.PLAYER, true, "Displays detailed durability values for equipped armor pieces.");
    }


}
