package Main.main.meow.client.module.modules.hud.position;

import Main.main.meow.client.module.HudDisplayModule;
import Main.main.meow.client.setting.modulesettings.colorpicker.ColorSetting;
import Main.main.meow.client.ui.clickgui.Category;
import Main.main.meow.client.setting.modulesettings.BooleanSetting;
import Main.main.meow.client.setting.modulesettings.EnumSetting;
import Main.main.meow.client.util.RenderUtil;
import Main.main.meow.client.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import org.apache.commons.lang3.StringUtils;

public final class PositionModule extends HudDisplayModule {
    private static final BooleanSetting HAVE_BACKGROUND = new BooleanSetting("Panel", "Displays the background", false);
    private static final ColorSetting BACKGROUND_COLOR = new ColorSetting("Background Color", "Set background color", RenderUtil.getGuiColorPanel());
    private static final ColorSetting TEXT_COLOR = new ColorSetting("Text Color", "Change text color", RenderUtil.getGuiTextColor());
    private static final EnumSetting<Modes> DIRECTION_TYPE = new EnumSetting<>("Modes", "Cycles between direction and coordinates, coordinates, or just direction", Modes.DIRECTIONCOORDINATES);
    public PositionModule() {
        super("PositionModule", Category.HUD, false, "Displays your current X, Y, and Z world coordinates.");
        addSetting(HAVE_BACKGROUND, BACKGROUND_COLOR, TEXT_COLOR, DIRECTION_TYPE);
    }

    @Override
    public int panelWidth(Minecraft client) {
        if (client.player == null) {
            return 0;
        }
        return TextUtil.getTextWidth(client, position(client.player)) + (HAVE_BACKGROUND.enabled() ? 4 : 0);
    }

    @Override
    public int panelHeight(Minecraft client) {
        if (client.player == null) {
            return 0;
        }
        return TextUtil.getTextHeight(client) + (HAVE_BACKGROUND.enabled() ? 4 : 0);
    }

    private static String position(LocalPlayer player) {
        BlockPos pos = player.blockPosition();
        return "X: " + pos.getX() + " Y: " + pos.getY() + " Z: " + pos.getZ();
    }

    private static String direction(LocalPlayer player) {
        return StringUtils.capitalize(player.getDirection().getName());
    }
    @Override
    public void drawInfo(GuiGraphicsExtractor g, Minecraft client, LocalPlayer player, int x, int y) {
        if (client.player == null) {
            return;
        }
        String text = switch(DIRECTION_TYPE.value()) {
            case Modes.DIRECTIONCOORDINATES -> "Direction: " + direction(player) + " " + position(player);
            case Modes.DIRECTION -> "Direction: " + direction(player);
            case Modes.COORDINATES -> position(player);
        };
        if (HAVE_BACKGROUND.enabled()) {
            RenderUtil.drawPanel(g, x, y, TextUtil.getTextWidth(client, text) + 4, TextUtil.getTextHeight(client) + 4);
        }
        RenderUtil.drawText(g, client, text, x + (HAVE_BACKGROUND.enabled() ? 2 : 0), y + (HAVE_BACKGROUND.enabled() ? 2 : 0), RenderUtil.getGuiTextColor());
    }
    public int getColor() {
        return 1;
    }
}
