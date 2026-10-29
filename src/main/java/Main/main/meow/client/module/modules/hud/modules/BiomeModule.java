package Main.main.meow.client.module.modules.hud.modules;

import Main.main.meow.client.module.modules.hud.HudDisplayModule;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.BooleanSetting;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.colorpicker.ColorSetting;
import Main.main.meow.client.ui.clickgui.model.Category;
import Main.main.meow.client.util.RenderUtil;
import Main.main.meow.client.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import org.apache.commons.lang3.StringUtils;


public final class BiomeModule extends HudDisplayModule {
    private static final BooleanSetting SHOW_BACKGROUND = new BooleanSetting("Show Background", "Toggle the background of your panel", true);
    private static final ColorSetting BACKGROUND_COLOR = new ColorSetting("Background Color", "Set the background color", RenderUtil.getGuiColorPanel());
    private static final ColorSetting TEXT_COLOR = new ColorSetting("Text Color", "Change the color of the text", RenderUtil.getGuiTextColor());
    public BiomeModule() {
        super("Biome", Category.HUD, false, "Displays the biome at your current position.");
        super.addSetting(SHOW_BACKGROUND, BACKGROUND_COLOR, TEXT_COLOR);
    }

    @Override
    public void drawInfo(GuiGraphicsExtractor g, Minecraft client, LocalPlayer player, int x, int y) {
        if (client.player == null)
            return;
        String biome;
        if(client.level != null ){
            biome = getBiome(client, player);
        } else {
            biome = "Unknown?";
        }
        if (SHOW_BACKGROUND.value()) {
            RenderUtil.drawPanel(g, x, y, TextUtil.getTextWidth(client, biome) + TextUtil.getTextWidth(client, "Biome :") + 4, TextUtil.getTextHeight(client) + 4, BACKGROUND_COLOR.color());
        }
        RenderUtil.drawText(g, client, "Biome: " + biome, x + 2, y + 2, TEXT_COLOR.color());
    }


    public static String getBiome(Minecraft client, LocalPlayer player) {
        if(client.level == null) return "Unknown";
        return StringUtils.capitalize(client.level.getBiome(player.blockPosition()).unwrapKey().map(key -> key.identifier().getPath().replace('_', ' ')).orElse("unknown"));
    }

    @Override
    public int panelWidth(Minecraft client) {
        if(client.player == null)
            return 0;
        return TextUtil.getTextWidth(client, getBiome(client, client.player)) + TextUtil.getTextWidth(client, "Biome :") + 4;
    }

    @Override
    public int panelHeight(Minecraft client) {
        if(client.player == null)
            return 0;
        return TextUtil.getTextHeight(client);
    }
}
