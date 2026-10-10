package Main.main.meow.client.modules.hud;

import Main.main.meow.client.hud.QolHudRenderer;
import Main.main.meow.client.modules.category.Category;
import Main.main.meow.client.modules.setting.BooleanSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;

public final class CoordinatesModule extends HudDisplayModule {
    private static final BooleanSetting HAVE_BACKGROUND = new BooleanSetting("Panel", "Displays the background", false);
    public CoordinatesModule() {
        super("Coordinates", Category.HUD, false, "Displays your current X, Y, and Z world coordinates.");
        addSetting(HAVE_BACKGROUND);
    }

    @Override
    public int panelWidth(Minecraft client) {
        if (client.player == null) {
            return 0;
        }
        return QolHudRenderer.getTextWidth(client, position(client.player)) + (HAVE_BACKGROUND.enabled() ? 4 : 0);
    }

    @Override
    public int panelHeight(Minecraft client) {
        if (client.player == null) {
            return 0;
        }
        return QolHudRenderer.getTextHeight(client, position(client.player)) + (HAVE_BACKGROUND.enabled() ? 4 : 0);
    }

    private static String position(LocalPlayer player) {
        BlockPos pos = player.blockPosition();
        return "X: " + pos.getX() + " Y: " + pos.getY() + " Z: " + pos.getZ();
    }

    public static void drawInfo(GuiGraphicsExtractor g, Minecraft client, LocalPlayer player, int x, int y) {
        if (client.player == null) {
            return;
        }
        String position = position(player);
        if (HAVE_BACKGROUND.enabled()) {
            QolHudRenderer.panel(g, x, y, QolHudRenderer.getTextWidth(client, position) + 4, QolHudRenderer.getTextHeight(client, position) + 4);
        }
        QolHudRenderer.text(g, client, position, x + (HAVE_BACKGROUND.enabled() ? 2 : 0), y + (HAVE_BACKGROUND.enabled() ? 2 : 0), QolHudRenderer.getTEXT());
    }


}
