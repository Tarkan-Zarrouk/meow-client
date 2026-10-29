package Main.main.meow.client.module.modules.client.clickgui;

import Main.main.meow.client.module.modules.ClientModule;
import Main.main.meow.client.ui.clickgui.view.ClickGuiStyler;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.EnumSetting;
import Main.main.meow.client.ui.clickgui.model.Category;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.BooleanSetting;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.colorpicker.ColorSetting;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.NumberSetting;
import Main.main.meow.client.ui.clickgui.model.ClickGuiLayout;
import Main.main.meow.client.util.RenderUtil;
import Main.main.meow.client.util.TextUtil;
import org.lwjgl.glfw.GLFW;

public final class ClickGuiModule extends ClientModule {
    private static String NAME = "ClickGui";
    private static Category CATEGORY = Category.CLIENT;
    private static boolean ENABLED = true;
    private static String DESCRIPTION = "Opens this ClickGUI and lets you customize its colors and keybind.";
    private static final ColorSetting SETTINGS_TEXT_COLOR = new ColorSetting("Setting Color", "Opens a picker that changes the text color", ClickGuiStyler.MUTED);
    private static final ColorSetting MODULE_TEXT_COLOR = new ColorSetting("Module text color", "Opens a picker that changes the text color", RenderUtil.getGuiTextColor());
    private static final ColorSetting CATEGORY_TEXT_COLOR = new ColorSetting("Category text color", "Opens a picker that changes the text color", RenderUtil.getGuiTextColor());
    private static final ColorSetting CATEGORY_COLOR = new ColorSetting("Category colour", "Opens a picker that changes the color of every category header in real time.", 0xffc52dbd);
    private static final ColorSetting MODULE_COLOR = new ColorSetting("Module colour", "Opens a picker that changes the color of every module row in real time.", 0xffc52dbd);
    private static final ColorSetting TOOL_TIP_BACKGROUND_COLOR = new ColorSetting("Tooltip Color", "Change the background color of the tooltip", RenderUtil.getGuiColorPanel());
    private static final ColorSetting TOOL_TIP_TEXT_BACKGROUND_COLOR = new ColorSetting("Tool Text Color", "Change the color of the tooltip text", RenderUtil.getGuiTextColor());
    private static final EnumSetting<Font> FONT = new EnumSetting<>("Font", "ClickGui and HUDUI switcher between Inter, built-in, and the backup font", Font.MINECRAFT);
    private static final BooleanSetting SHOW_SHADOW = new BooleanSetting("Font Shadow", "Toggle between shadow on text font", false);
    private static final BooleanSetting SHOW_OUTLINE = new BooleanSetting("Toggle ToolTip Border", "Toggle ToolTip border", false);
    private static final ColorSetting TOOLTIP_BORDER_COLOR = new ColorSetting("Tooltip Border Color", "Change the color of the tooltip border", 0xffffffff);
    private static final NumberSetting FONT_SIZE = new NumberSetting("Font size", "Drag across this slider to change the active font size in real time.", 11, 5, 20);
    private static final NumberSetting CLICK_GUI_BORDER_RADIUS = new NumberSetting("Border Radius", "Change the roundedness of the borders", 16, 0,16);
    private static final NumberSetting CLICK_GUI_HEADER_RADIUS = new NumberSetting("Header Radius","Change the header radius", 16, 0, 16);
    private static final BooleanSetting SHOW_TOOL_TIP = new BooleanSetting("Draw Tooltips", "Show the tooltips for each module and their respective setting", true);
    private static final NumberSetting TOOLTIP_BORDER_RADIUS = new NumberSetting("Tool tip border radius", "Change the roundedness of the border radisu", 5, 0, 15);
    private static final BooleanSetting BLUR_BACKGROUND = new BooleanSetting("Blur Background", "Blur the background", true);
    private static final NumberSetting COLUMN_GAP = new NumberSetting("Column Gap", "Change the gap of the columns", ClickGuiLayout.getColumnGap(), 1, 20);
    private static final NumberSetting MODULE_WIDTH = new NumberSetting("Module Width", "Change the width of the module", 120, 100, 170);
    private static final NumberSetting MODULE_HEIGHT = new NumberSetting("Module height", "Change the height of each module collectively", ClickGuiLayout.getRowHeight(), 10, 30);


    public ClickGuiModule() {
        super(NAME, CATEGORY, ENABLED, DESCRIPTION);
        super.addSetting(
                CATEGORY_COLOR, MODULE_COLOR, TOOL_TIP_BACKGROUND_COLOR, TOOL_TIP_TEXT_BACKGROUND_COLOR, TOOLTIP_BORDER_COLOR, CATEGORY_TEXT_COLOR, MODULE_TEXT_COLOR, SETTINGS_TEXT_COLOR,
                FONT, SHOW_SHADOW, FONT_SIZE,
                SHOW_OUTLINE,
                SHOW_TOOL_TIP,
                TOOLTIP_BORDER_RADIUS, CLICK_GUI_BORDER_RADIUS, CLICK_GUI_HEADER_RADIUS,
                BLUR_BACKGROUND,
                MODULE_WIDTH, MODULE_HEIGHT, COLUMN_GAP
        );
        super.setKeybind(GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    public static int getSettingTextColor() {
        return SETTINGS_TEXT_COLOR.color();
    }

    public static int getModuleTextColor() {
        return MODULE_TEXT_COLOR.color();
    }

    public static int getCategoryTextColor() {
        return CATEGORY_TEXT_COLOR.color();
    }

    public static int getColumnGap() {
        return COLUMN_GAP.value();
    }
    public static int getModuleWidth() {
        return MODULE_WIDTH.value();
    }
    public static int getModuleHeight() {
        return MODULE_HEIGHT.value();
    }

    public static boolean getBlurBackgroundValue() {
        return BLUR_BACKGROUND.value();
    }
    public static int getToolTipBorderRadius() {
        return TOOLTIP_BORDER_RADIUS.value();
    }
    public static boolean getToolTipShowVal() {
        return SHOW_TOOL_TIP.enabled();
    }
    public static int getClickGuiHeaderBorderRadius() {
        return CLICK_GUI_HEADER_RADIUS.value();
    }
    public static int getClickGuiBorderRadius() {
        return CLICK_GUI_BORDER_RADIUS.value();
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

    public static Font font() {
        return FONT.value();
    }

    public static int fontSize() {
        return FONT_SIZE.number();
    }


    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Category getCategory() {
        return CATEGORY;
    }

    @Override
    public boolean getEnabled() {
        return ENABLED;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public void setName(String name) {
        NAME = name;
    }

    @Override
    public void setCategory(Category category) {
        CATEGORY = category;
    }

    @Override
    public void setEnabledVal(boolean enabled) {
        ENABLED = enabled;
    }

    @Override
    public void setDescription(String description) {
        DESCRIPTION = description;
    }
}
