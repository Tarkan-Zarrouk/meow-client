package Main.main.meow.client.module.modules.hud;

import Main.main.meow.client.module.HudDisplayModule;
import Main.main.meow.client.ui.hud.QolHudRenderer;
import Main.main.meow.client.ui.clickgui.Category;
import Main.main.meow.client.setting.modulesettings.BooleanSetting;
import Main.main.meow.client.setting.modulesettings.EnumSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import org.apache.commons.lang3.StringUtils;

public final class PositionModule extends HudDisplayModule {
    public enum Modes {
        DIRECTIONCOORDINATES,
        COORDINATES,
        DIRECTION
    }
    private static final BooleanSetting HAVE_BACKGROUND = new BooleanSetting("Panel", "Displays the background", false);
    private static final EnumSetting<Modes> DIRECTION_TYPE = new EnumSetting<>("Modes", "Cycles between direction and coordinates, coordinates, or just direction", Modes.DIRECTIONCOORDINATES);
    public PositionModule() {
        super("PositionModule", Category.HUD, false, "Displays your current X, Y, and Z world coordinates.");
        addSetting(HAVE_BACKGROUND, DIRECTION_TYPE);
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

    private static String direction(LocalPlayer player) {
        return StringUtils.capitalize(player.getDirection().getName());
    }

    public static void drawInfo(GuiGraphicsExtractor g, Minecraft client, LocalPlayer player, int x, int y) {
        if (client.player == null) {
            return;
        }
        String text = switch(DIRECTION_TYPE.value()) {
            case Modes.DIRECTIONCOORDINATES -> "Direction: " + direction(player) + " " + position(player);
            case Modes.DIRECTION -> "Direction: " + direction(player);
            case Modes.COORDINATES -> position(player);
        };
        if (HAVE_BACKGROUND.enabled()) {
            QolHudRenderer.panel(g, x, y, QolHudRenderer.getTextWidth(client, text) + 4, QolHudRenderer.getTextHeight(client, text) + 4);
        }
        QolHudRenderer.text(g, client, text, x + (HAVE_BACKGROUND.enabled() ? 2 : 0), y + (HAVE_BACKGROUND.enabled() ? 2 : 0), QolHudRenderer.getTEXT());
    }

}
