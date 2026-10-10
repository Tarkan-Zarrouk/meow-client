package Main.main.meow.client.modules.hud;

import Main.main.meow.client.modules.category.Category;
import Main.main.meow.client.modules.client.ClientModule;

public class InventoryViewer extends ClientModule {
    public InventoryViewer() {
        super("Inventory Viewer", Category.HUD, false, "Shows the content of your inventory updating in real time");
    }
}
