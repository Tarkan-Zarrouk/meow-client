package Main.main.meow.client.modules.hud;

import Main.main.meow.client.modules.client.ClientModule;
import Main.main.meow.client.modules.category.Category;
import net.minecraft.client.Minecraft;

public abstract class HudDisplayModule extends ClientModule {
    protected HudDisplayModule(String name, Category category, boolean enabled, String description) {
        super(name, category, enabled, description);
    }

    public abstract int panelWidth(Minecraft client);

    public abstract int panelHeight(Minecraft client);
}
