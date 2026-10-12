package Main.main.meow.client.module.modules.hud;

import Main.main.meow.client.module.HudDisplayModule;
import Main.main.meow.client.ui.clickgui.Category;
import Main.main.meow.client.ui.hud.QolHudRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class PotionEffectsModule extends HudDisplayModule {
    public PotionEffectsModule() {
        super("Potion Effects", Category.HUD, true, "Displays active potion effects with their icons, levels, and remaining time.");
    }

    public static void drawEffects(GuiGraphicsExtractor g, Minecraft client, LocalPlayer player, int x, int y) {
        List<MobEffectInstance> effects = new ArrayList<>(player.getActiveEffects()); // it took a while to figure out how to get the effects, but this works ig
        QolHudRenderer.panel(g, x, y, 210, effects.size() * 20 + 12);
        if (effects.isEmpty()) {
            QolHudRenderer.text(g, client, "No active effects", x + 27, y+2, QolHudRenderer.getTEXT());
        }
        for (int i = 0; i < effects.size(); i++) {
            MobEffectInstance effect = effects.get(i);
            int effectX = x + 6;
            int effectY = y + 4 + i * 20;
            int effectWidth = 16;
            int effectHeight = 16;
            g.blitSprite(RenderPipelines.GUI_TEXTURED, Gui.getMobEffectSprite(effect.getEffect()), effectX, effectY, effectWidth, effectHeight);
            int textX = x + 27;
            int textY = y + 7 + i * 20;
            QolHudRenderer.text(g, client, effect.getEffect().value().getDisplayName().getString() + " " + formatDuration(effect.getDuration()), textX, textY, QolHudRenderer.getTEXT());
        }
    }

    private static String formatDuration(int ticks) {
        if(ticks == MobEffectInstance.INFINITE_DURATION)
            return "inf";
        return (ticks / 20) / 60 + ":" + String.format(Locale.ROOT, "%02d", (ticks / 20 ) % 60);
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
