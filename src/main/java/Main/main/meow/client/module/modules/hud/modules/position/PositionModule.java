package Main.main.meow.client.module.modules.hud.modules.position;

import Main.main.meow.client.module.modules.hud.HudDisplayModule;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.BooleanSetting;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.colorpicker.ColorSetting;
import Main.main.meow.client.ui.clickgui.model.Category;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.EnumSetting;
import Main.main.meow.client.util.RenderUtil;
import Main.main.meow.client.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import org.apache.commons.lang3.StringUtils;


public final class PositionModule extends HudDisplayModule {
    private static final BooleanSetting SHOW_BACKGROUND = new BooleanSetting("Show Background", "Toggle the background of your panel", true);
    private static final ColorSetting BACKGROUND_COLOR = new ColorSetting("Background Color", "Set background color", RenderUtil.getGuiColorPanel());
    private static final ColorSetting TEXT_COLOR = new ColorSetting("Text Color", "Change text color", RenderUtil.getGuiTextColor());
    private static final EnumSetting<Modes> DIRECTION_TYPE = new EnumSetting<>("Modes", "Cycles between direction and coordinates, coordinates, or just direction", Modes.DIRECTIONCOORDINATES);
    public PositionModule() {
        super("PositionModule", Category.HUD, false, "Displays your current X, Y, and Z world coordinates.");
        super.addSetting(SHOW_BACKGROUND, DIRECTION_TYPE, BACKGROUND_COLOR, TEXT_COLOR);
    }

    @Override
    public int panelWidth(Minecraft client) {
        if (client.player == null) {
            return 0;
        }
        return TextUtil.getTextWidth(client, position(client.player)) + 4;
    }

    @Override
    public int panelHeight(Minecraft client) {
        if (client.player == null) {
            return 0;
        }
        return TextUtil.getTextHeight(client) + 4;
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
        if (SHOW_BACKGROUND.value()) {
            RenderUtil.drawPanel(g, x, y, TextUtil.getTextWidth(client, text) + 4, TextUtil.getTextHeight(client) + 4, BACKGROUND_COLOR.color());
        }
        RenderUtil.drawText(g, client, text, x + 3, y + 3, TEXT_COLOR.color());
    }
}
