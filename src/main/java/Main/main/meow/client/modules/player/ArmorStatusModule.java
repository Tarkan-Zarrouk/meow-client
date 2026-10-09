package Main.main.meow.client.modules.player;

import Main.main.meow.client.modules.client.ClientModule;
import Main.main.meow.client.modules.category.Category;

public final class ArmorStatusModule extends ClientModule {
    public ArmorStatusModule() {
        super("Armor Status", Category.PLAYER, true, "Shows your equipped armor and its remaining durability on the HUD.");
    }
}
