package Main.main.meow.client.ui.hud;

import Main.main.meow.client.MeowClient;
import Main.main.meow.client.module.Module;

import Main.main.meow.client.manager.NotificationManager;
import Main.main.meow.client.module.modules.client.ClickGuiModule;
import Main.main.meow.client.ui.hud.hudgui.HudGuiStyler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.*;

public final class QolHudRenderer {
    public static void render(GuiGraphicsExtractor graphics, Minecraft client) {
        LocalPlayer player = client.player;
        if (player == null || client.level == null) {
            return;
        }
        MeowClient.HUD_COMPONENTS.render(graphics, client, false);
        NotificationManager.render(graphics, client);
    }

    public static boolean enabled(String name) {
        Module module = MeowClient.MODULE_MANAGER.byName(name);
        return module != null && module.isEnabled();
    }
}
