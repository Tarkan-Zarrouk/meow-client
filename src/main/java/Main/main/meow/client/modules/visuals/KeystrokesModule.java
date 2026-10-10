package Main.main.meow.client.modules.visuals;

import Main.main.meow.client.modules.hud.HudDisplayModule;
import Main.main.meow.client.modules.category.Category;
import net.minecraft.client.Minecraft;

public final class KeystrokesModule extends HudDisplayModule {
    public KeystrokesModule() {
        super("Keystrokes", Category.VISUALS, true, "Shows keyboard movement inputs and smoothly animates pressed keys on the HUD.");
    }

    @Override
    public int panelWidth(Minecraft client) {
        return 122;
    }

    @Override
    public int panelHeight(Minecraft client) {
        return 102;
    }
}
