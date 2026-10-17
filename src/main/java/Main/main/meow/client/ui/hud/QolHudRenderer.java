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
    private static final int PANEL = 0xb9161d2a, TEXT = 0xfff2f0f4, WARNING = 0xffff9c52;


    public static void render(GuiGraphicsExtractor graphics, Minecraft client) {
        LocalPlayer player = client.player;
        if (player == null || client.level == null) {
            return;
        }
        MeowClient.HUD_COMPONENTS.render(graphics, client, false);
        NotificationManager.render(graphics, client);
    }

    public static int getPanel() {
        return PANEL;
    }

    public static int getTEXT() {
        return TEXT;
    }

    public static int getTextWidth(Minecraft client, String text) {
        if (client == null || text == null) {
            return text == null ? 0 : text.length() * 6;
        }
        return client.font.width(styled(text));
    }
    public static int getTextHeight(Minecraft client) {
        if (client == null) {
            return 9;
        }
        return client.font.lineHeight;
    }
    public static void drawItemDurability(GuiGraphicsExtractor g, Minecraft client, LocalPlayer player, int x, int y) {
        ItemStack stack = player.getMainHandItem();
        int damage = stack.getDamageValue();
        int maxDamage = stack.getMaxDamage();
        int percent = Math.round((1f - (float) damage / maxDamage) * 100f);
        panel(g, x, y, 210, 30);
        g.item(stack, x + 6, y + 6);
        if (percent < 20) {
            text(g, client, stack.getHoverName().getString() + " " + percent + "%", x + 28, y + 10, WARNING);
        } else {
            text(g, client, stack.getHoverName().getString() + " " + percent + "%", x + 28, y + 10, TEXT);
        }
    }


    public static Component styled(String value) {
        return Component.literal(value).withStyle(style -> style.withFont(HudGuiStyler.getFont()));
    }

    public static void text(GuiGraphicsExtractor graphics, Minecraft client, String value, int x, int y, int color) {
        graphics.text(client.font, styled(value), x, y, color, true);
    }
    public static void panel(GuiGraphicsExtractor g, int x, int y, int width, int height) {
        g.fill(x, y, x + width, y + height, PANEL);
    }
    public static void panel(GuiGraphicsExtractor g, int x, int y, int width, int height, int color) {
        g.fill(x, y, x + width, y + height, color);
    }

    public static boolean enabled(String name) {
        Module module = MeowClient.MODULE_MANAGER.byName(name);
        return module != null && module.isEnabled();
    }
}
