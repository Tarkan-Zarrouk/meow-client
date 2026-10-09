package Main.main.meow.client.modules.player;

import Main.main.meow.client.modules.client.ClientModule;
import Main.main.meow.client.modules.category.Category;

public final class InventoryMoverModule extends ClientModule {
    public InventoryMoverModule() {
        super("Inventory Mover", Category.PLAYER, true, "Allows movement and inventory-related controls while a screen is open.");
    }
}
