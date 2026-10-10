package Main.main.meow.client.modules.player;

import Main.main.meow.client.modules.hud.HudDisplayModule;
import Main.main.meow.client.modules.category.Category;
import net.minecraft.client.Minecraft;

public final class ArmorStatusModule extends HudDisplayModule {
    public ArmorStatusModule() {
        super("Armor Status", Category.PLAYER, true, "Shows your equipped armor and its remaining durability on the HUD.");
    }

    @Override
    public int panelWidth(Minecraft client) {
        return 48;
    }

    @Override
    public int panelHeight(Minecraft client) {
        return 112;
    }
}
