package Main.main.meow.client.modules.hud;

import Main.main.meow.client.modules.category.Category;
import net.minecraft.client.Minecraft;

public class InventoryViewer extends HudDisplayModule {
    public InventoryViewer() {
        super("Inventory Viewer", Category.HUD, false, "Shows the content of your inventory updating in real time");
    }

    @Override
    public int panelWidth(Minecraft client) {
        return 162;
    }

    @Override
    public int panelHeight(Minecraft client) {
        return 54;
    }
}
