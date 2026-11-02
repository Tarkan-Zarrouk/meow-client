package Main.main.meow.client.module.modules.hud.modules.enabledmodules;

import Main.main.meow.client.MeowClient;
import Main.main.meow.client.module.modules.hud.HudDisplayModule;
import Main.main.meow.client.ui.clickgui.model.Category;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.BooleanSetting;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.EnumSetting;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.NumberSetting;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.colorpicker.ColorSetting;
import Main.main.meow.client.util.RenderUtil;
import Main.main.meow.client.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;

import java.util.*;

public class EnabledModules extends HudDisplayModule {
    private static final String NAME = "EnabledModules";
    private static final Category CATEGORY = Category.HUD;
    private static final boolean ENABLED = false;
    private static final String DESCRIPTION = "Shows a list of modules currently enabled";
    private static final EnumSetting<Main.main.meow.client.module.modules.hud.modules.enabledmodules.Modes> ORDERING = new EnumSetting<>("Order", "Swap between unordered, ascending, descending order", Modes.UNORDERED);
    private static final ColorSetting TEXT_COLOR = new ColorSetting("Text Color", "Change the color of the text", RenderUtil.getGuiTextColor());
    private static final BooleanSetting BACKGROUND_PANEL = new BooleanSetting("Show Panel", "Toggle on and off the panel", true);
    private static final ColorSetting PANEL_COLOR = new ColorSetting("Panel Color", "Background color", RenderUtil.getGuiColorPanel());
    private static final NumberSetting BORDER_RADIUS = new NumberSetting("Panel Radius", "Change the radius of the panel", 4, 1, 4);
    private static final BooleanSetting TOP_LEFT_ROUNDED = new BooleanSetting("Top Left Round", "Top left round", true);
    private static final BooleanSetting TOP_RIGHT_ROUNDED = new BooleanSetting("Top Right Round", "Top right round", true);
    private static final BooleanSetting BOTTOM_LEFT_ROUNDED = new BooleanSetting("Bottom Left Round", "Bottom left round", true);
    private static final BooleanSetting BOTTOM_RIGHT_ROUNDED = new BooleanSetting("Bottom Right Round", "Bottom right round", true);
    public EnabledModules() {
        super(NAME, CATEGORY, ENABLED, DESCRIPTION);
        super.addSetting(
                ORDERING,
                BACKGROUND_PANEL,
                TEXT_COLOR, PANEL_COLOR,
                BORDER_RADIUS,
                TOP_LEFT_ROUNDED, TOP_RIGHT_ROUNDED, BOTTOM_LEFT_ROUNDED, BOTTOM_RIGHT_ROUNDED
        );
    }


    @Override
    public void drawInfo(GuiGraphicsExtractor graphics, Minecraft client, LocalPlayer player, int x, int y) {
        drawList(graphics, client, orderedNames(), x, y, TEXT_COLOR.color(), BACKGROUND_PANEL.value(), PANEL_COLOR.color(), true);
    }

    private List<String> orderedNames() {
        List<String> names = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : MeowClient.getModuleManager().getModuleListString().entrySet()) {
            if (entry.getValue()) {
                names.add(entry.getKey());
            }
        }
        switch (ORDERING.value()) {
            case UNORDERED -> names.sort(String.CASE_INSENSITIVE_ORDER);
            case ASCENDING -> names.sort(Comparator.comparingInt(String::length));
            case DESCENDING -> names.sort(Comparator.comparingInt(String::length).reversed());
        }
        return names;
    }

    private void drawList(GuiGraphicsExtractor graphics, Minecraft client, List<String> list, int x, int y, int textColor, boolean showBackground, int backgroundColor, boolean dropShadow) {
        int offsetY = 0;
        if (showBackground) {
            RenderUtil.roundedFill(graphics, x -2, y - 2, panelWidth(client) + 8, panelHeight(client) + 8, backgroundColor, TOP_LEFT_ROUNDED.value(), TOP_RIGHT_ROUNDED.value(), BOTTOM_LEFT_ROUNDED.value(), BOTTOM_RIGHT_ROUNDED.value(), BORDER_RADIUS.value());
        }
        for (String moduleName : list) {
            RenderUtil.drawText(graphics, client.font, TextUtil.setFont(moduleName), x, y + offsetY, textColor, dropShadow);
            offsetY += client.font.lineHeight + 1;
        }
    }

    @Override
    public int panelWidth(Minecraft client) {
        if (client.font == null) return 0;
        int widest = 0;
        for (String name : orderedNames()) {
            widest = Math.max(widest, TextUtil.getTextWidth(client, name));
        }
        return Math.max(widest, 40);
    }

    @Override
    public int panelHeight(Minecraft client) {
        if (client.font == null) return 0;
        return Math.max(orderedNames().size() * (client.font.lineHeight + 1), client.font.lineHeight);
    }
}
