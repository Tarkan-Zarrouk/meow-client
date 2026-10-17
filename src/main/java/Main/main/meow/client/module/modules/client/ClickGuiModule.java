package Main.main.meow.client.module.modules.client;

import Main.main.meow.client.module.modules.ClientModule;
import Main.main.meow.client.ui.clickgui.Category;
import Main.main.meow.client.setting.modulesettings.BooleanSetting;
import Main.main.meow.client.setting.modulesettings.colorpicker.ColorSetting;
import Main.main.meow.client.setting.NumberSetting;
import Main.main.meow.client.ui.hud.QolHudRenderer;
import org.lwjgl.glfw.GLFW;

public final class ClickGuiModule extends ClientModule {
    private static final ColorSetting CATEGORY_COLOR = new ColorSetting("Category colour", "Opens a picker that changes the color of every category header in real time.", 0xffc52dbd);
    private static final ColorSetting MODULE_COLOR = new ColorSetting("Module colour", "Opens a picker that changes the color of every module row in real time.", 0xffc52dbd);
    private static final ColorSetting TOOL_TIP_BACKGROUND_COLOR = new ColorSetting("Tooltip Color", "Change the background color of the tooltip", QolHudRenderer.getPanel());
    private static final ColorSetting TOOL_TIP_TEXT_BACKGROUND_COLOR = new ColorSetting("Tool Text Color", "Change the color of the tooltip text", QolHudRenderer.getTEXT());
    private static final BooleanSetting FONT = new BooleanSetting("Font", "Switches the ClickGUI between the custom Inter font and Minecraft's default font.", false, "Font: Inter", "Font: Minecraft");
    private static final BooleanSetting SHOW_SHADOW = new BooleanSetting("Font Shadow", "Toggle between shadow on text font", false);
    private static final BooleanSetting SHOW_OUTLINE = new BooleanSetting("Toggle ToolTip Border", "Toggle ToolTip border", false);
    private static final ColorSetting TOOLTIP_BORDER_COLOR = new ColorSetting("Tooltip Border Color", "Change the color of the tooltip border", 0xffffffff);
    private static final NumberSetting FONT_SIZE = new NumberSetting("Font size", "Drag across this slider to change the active font size in real time.", 11, 5, 20);
    public ClickGuiModule() {
        super("ClickGUI", Category.CLIENT, true, "Opens this ClickGUI and lets you customize its colors and keybind.");
        addSetting(CATEGORY_COLOR, MODULE_COLOR, TOOL_TIP_BACKGROUND_COLOR, TOOL_TIP_TEXT_BACKGROUND_COLOR, FONT, SHOW_SHADOW, SHOW_OUTLINE, TOOLTIP_BORDER_COLOR, FONT_SIZE);
        setKeybind(GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    public static int tooltipBorderColor() {
        return TOOLTIP_BORDER_COLOR.color();
    }

    public static boolean showOutline() {
        return SHOW_OUTLINE.value();
    }

    public static boolean getShadowBoolean() {
        return SHOW_SHADOW.value();
    }

    public static int categoryColor() {
        return CATEGORY_COLOR.color();
    }

    public static int getToolTipTextColor() {
        return TOOL_TIP_TEXT_BACKGROUND_COLOR.color();
    }

    public static int getToolTipBackgroundColor() {
        return TOOL_TIP_BACKGROUND_COLOR.color();
    }

    public static int moduleColor() {
        return MODULE_COLOR.color();
    }

    public static boolean interFont() {
        return FONT.enabled();
    }

    public static int fontSize() {
        return FONT_SIZE.number();
    }


}
