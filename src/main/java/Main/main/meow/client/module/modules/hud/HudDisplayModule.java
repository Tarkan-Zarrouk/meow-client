package Main.main.meow.client.module.modules.hud;

import Main.main.meow.client.module.Module;
import Main.main.meow.client.ui.clickgui.model.Category;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;

public abstract class HudDisplayModule extends Module {
    protected HudDisplayModule(String name, Category category, boolean enabled, String description) {
        super(name, category, enabled, description);
    }

    public abstract void drawInfo(GuiGraphicsExtractor g, Minecraft client, LocalPlayer player, int x, int y);

    public abstract int panelWidth(Minecraft client);

    public abstract int panelHeight(Minecraft client);
}
