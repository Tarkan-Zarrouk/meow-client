package Main.main.meow.client.modules.player;

import Main.main.meow.client.modules.client.ClientModule;
import Main.main.meow.client.modules.category.Category;

public final class ItemDurabilityModule extends ClientModule {
    public ItemDurabilityModule() {
        super("Item Durability", Category.PLAYER, true, "Displays the remaining durability of the item you are holding.");
    }
}
