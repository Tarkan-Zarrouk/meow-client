package Main.main.meow.client.module.modules.hud.modules;

import Main.main.meow.client.module.modules.hud.HudDisplayModule;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.BooleanSetting;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.colorpicker.ColorSetting;
import Main.main.meow.client.ui.clickgui.model.Category;
import Main.main.meow.client.util.RenderUtil;
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
    private static final BooleanSetting SHOW_BACKGROUND = new BooleanSetting("Show Background", "Toggle the background of your panel", true);
    private static final ColorSetting BACKGROUND_COLOR = new ColorSetting("Background color", "Change the background color", RenderUtil.getGuiColorPanel());
    private static final ColorSetting TEXT_COLOR = new ColorSetting("Text color", "Change the color of the text", RenderUtil.getGuiTextColor());
    public PotionEffectsModule() {
        super("Potion Effects", Category.HUD, true, "Displays active potion effects with their icons, levels, and remaining time.");
        super.addSetting(SHOW_BACKGROUND, BACKGROUND_COLOR, TEXT_COLOR);
    }

    @Override
    public void drawInfo(GuiGraphicsExtractor g, Minecraft client, LocalPlayer player, int x, int y) {
        List<MobEffectInstance> effects = new ArrayList<>(player.getActiveEffects()); // it took a while to figure out how to get the effects, but this works ig
        if (SHOW_BACKGROUND.value()) {
            RenderUtil.drawPanel(g, x, y, 210, effects.size() * 20 + 12, BACKGROUND_COLOR.color());
        }
        if (effects.isEmpty()) {
            RenderUtil.drawText(g, client, "No active effects", x + 27, y+2, TEXT_COLOR.color());
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
            RenderUtil.drawText(g, client, effect.getEffect().value().getDisplayName().getString() + " " + formatDuration(effect.getDuration()), textX, textY, TEXT_COLOR.color());
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
