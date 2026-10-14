package Main.main.meow.client.module.modules.hud;

import Main.main.meow.client.module.HudDisplayModule;
import Main.main.meow.client.ui.hud.QolHudRenderer;
import Main.main.meow.client.ui.clickgui.Category;
import Main.main.meow.client.setting.modulesettings.BooleanSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import org.apache.commons.lang3.StringUtils;

public final class BiomeModule extends HudDisplayModule {
    private static final BooleanSetting HAVE_BACKGROUND = new BooleanSetting("Panel", "Displays the background", false);
    public BiomeModule() {
        super("Biome", Category.HUD, false, "Displays the biome at your current position.");
        addSetting(HAVE_BACKGROUND);
    }
    public static void drawInfo(GuiGraphicsExtractor g, Minecraft client, LocalPlayer player, int x, int y) {
        if (client.player == null)
            return;
        String biome;
        if(client.level != null ){ // checks if player not on server, crashed once before bc of not having this lol
            biome = getBiome(client, player);
        } else {
            biome = "Unknown?"; // it really shouldn't return this but better than "null" lol
        }
        if(HAVE_BACKGROUND.enabled()) {
            QolHudRenderer.panel(g,x,y, QolHudRenderer.getTextWidth(client, biome) + 4, QolHudRenderer.getTextHeight(client) + 4);
            QolHudRenderer.text(g, client, "Biome: " + biome, x + 2, y + 2, QolHudRenderer.getTEXT());
        } else {
            QolHudRenderer.text(g, client, "Biome: " + biome, x + 2, y + 2, QolHudRenderer.getTEXT());
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
        return QolHudRenderer.getTextWidth(client, getBiome(client, client.player) + (HAVE_BACKGROUND.enabled()));
    }

    @Override
    public int panelHeight(Minecraft client) {
        if(client.player == null)
            return 0;
        return QolHudRenderer.getTextHeight(client);
    }
}
