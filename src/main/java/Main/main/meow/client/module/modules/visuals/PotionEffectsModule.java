package Main.main.meow.client.module.modules.visuals;

import Main.main.meow.client.module.modules.hud.HudDisplayModule;
import Main.main.meow.client.ui.clickgui.Category;
import net.minecraft.client.Minecraft;

public final class PotionEffectsModule extends HudDisplayModule {
    public PotionEffectsModule() {
        super("Potion Effects", Category.VISUALS, true, "Displays active potion effects with their icons, levels, and remaining time.");
    }

    @Override
    public int panelWidth(Minecraft client) {
        return 210;
    }

    @Override
    public int panelHeight(Minecraft client) {
        if (client.player == null || client.player.getActiveEffects().isEmpty()) {
            return 12;
        }
        return client.player.getActiveEffects().size() * 20 + 12;
    }
}
