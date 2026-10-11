package Main.main.meow.client.module.modules.player;

import Main.main.meow.client.module.HudDisplayModule;
import Main.main.meow.client.ui.clickgui.Category;
import net.minecraft.client.Minecraft;

public final class ItemDurabilityModule extends HudDisplayModule {
    public ItemDurabilityModule() {
        super("Item Durability", Category.PLAYER, true, "Displays the remaining durability of the item you are holding.");
    }

    @Override
    public int panelWidth(Minecraft client) {
        return 210;
    }

    @Override
    public int panelHeight(Minecraft client) {
        return 30;
    }
}
