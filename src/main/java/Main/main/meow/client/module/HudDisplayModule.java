package Main.main.meow.client.module;

import Main.main.meow.client.module.modules.ClientModule;
import Main.main.meow.client.ui.clickgui.Category;
import net.minecraft.client.Minecraft;

public abstract class HudDisplayModule extends ClientModule {
    protected HudDisplayModule(String name, Category category, boolean enabled, String description) {
        super(name, category, enabled, description);
    }

    public abstract int panelWidth(Minecraft client);

    public abstract int panelHeight(Minecraft client);
}
