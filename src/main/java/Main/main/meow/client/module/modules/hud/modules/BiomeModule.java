package Main.main.meow.client.module.modules.hud.modules;

import Main.main.meow.client.module.modules.hud.HudDisplayModule;
import Main.main.meow.client.setting.modulesettings.colorpicker.ColorSetting;
import Main.main.meow.client.ui.clickgui.Category;
import Main.main.meow.client.setting.modulesettings.BooleanSetting;
import Main.main.meow.client.util.RenderUtil;
import Main.main.meow.client.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import org.apache.commons.lang3.StringUtils;

public final class BiomeModule extends HudDisplayModule {
    private static final BooleanSetting HAVE_BACKGROUND = new BooleanSetting("Panel", "Displays the background", false);
    private static final ColorSetting BACKGROUND_COLOR = new ColorSetting("Background Color", "Set the background color", RenderUtil.getGuiColorPanel());
    private static final ColorSetting TEXT_COLOR = new ColorSetting("Text Color", "Change the color of the text", RenderUtil.getGuiTextColor());
    public BiomeModule() {
        super("Biome", Category.HUD, false, "Displays the biome at your current position.");
        addSetting(HAVE_BACKGROUND, BACKGROUND_COLOR, TEXT_COLOR);
    }

    @Override
    public void drawInfo(GuiGraphicsExtractor g, Minecraft client, LocalPlayer player, int x, int y) {
        if (client.player == null)
            return;
        String biome;
        if(client.level != null ){ // checks if player not on server, crashed once before bc of not having this lol
            biome = getBiome(client, player);
        } else {
            biome = "Unknown?"; // it really shouldn't return this but better than "null" lol
        }
        if(HAVE_BACKGROUND.enabled()) {
            RenderUtil.drawPanel(g, x, y, TextUtil.getTextWidth(client, biome) + 4, TextUtil.getTextHeight(client) + 4, BACKGROUND_COLOR.color());
            RenderUtil.drawText(g, client, "Biome: " + biome, x + 2, y + 2, TEXT_COLOR.color());
        } else {
            RenderUtil.drawText(g, client, "Biome: " + biome, x + 2, y + 2, RenderUtil.getGuiTextColor());
        }
    }


    public static String getBiome(Minecraft client, LocalPlayer player) {
        if(client.level == null) return "Unknown";
        return StringUtils.capitalize(client.level.getBiome(player.blockPosition()).unwrapKey().map(key -> key.identifier().getPath().replace('_', ' ')).orElse("unknown"));
    }

    @Override
    public int panelWidth(Minecraft client) {
        if(client.player == null)
            return 0;
        return TextUtil.getTextWidth(client, getBiome(client, client.player) + (HAVE_BACKGROUND.enabled()));
    }

    @Override
    public int panelHeight(Minecraft client) {
        if(client.player == null)
            return 0;
        return TextUtil.getTextHeight(client);
    }
}
