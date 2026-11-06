package Main.main.meow.client.module.modules.client.clickgui;

import Main.main.meow.client.module.modules.ClientModule;
import Main.main.meow.client.ui.clickgui.view.ClickGuiStyler;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.EnumSetting;
import Main.main.meow.client.ui.clickgui.model.Category;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.BooleanSetting;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.colorpicker.ColorSetting;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.NumberSetting;
import Main.main.meow.client.ui.clickgui.model.ClickGuiLayout;
import Main.main.meow.client.util.RenderUtil2D;
import org.lwjgl.glfw.GLFW;

public final class ClickGuiModule extends ClientModule {
    private static final String NAME = "ClickGui";
    private static final Category CATEGORY = Category.CLIENT;
    private static final boolean ENABLED = true;
    private static final String DESCRIPTION = "Opens this ClickGUI and lets you customize its colors and keybind.";
    private static final ColorSetting SETTINGS_TEXT_COLOR = new ColorSetting("Setting Color", "Opens a picker that changes the text color", ClickGuiStyler.MUTED);
    private static final ColorSetting MODULE_TEXT_COLOR = new ColorSetting("Module text color", "Opens a picker that changes the text color", RenderUtil2D.getGuiTextColor());
    private static final ColorSetting CATEGORY_TEXT_COLOR = new ColorSetting("Category text color", "Opens a picker that changes the text color", RenderUtil2D.getGuiTextColor());
    private static final ColorSetting CATEGORY_COLOR = new ColorSetting("Category colour", "Opens a picker that changes the color of every category header in real time.", 0xffc52dbd);
    private static final ColorSetting MODULE_COLOR = new ColorSetting("Module colour", "Opens a picker that changes the color of every module row in real time.", 0xffc52dbd);
    private static final ColorSetting TOOL_TIP_BACKGROUND_COLOR = new ColorSetting("Tooltip Color", "Change the background color of the tooltip", RenderUtil2D.getGuiColorPanel());
    private static final ColorSetting TOOL_TIP_TEXT_BACKGROUND_COLOR = new ColorSetting("Tool Text Color", "Change the color of the tooltip text", RenderUtil2D.getGuiTextColor());
    private static final EnumSetting<Font> FONT = new EnumSetting<>("Font", "ClickGui and HUDUI switcher between Inter, built-in, and the backup font", Font.MINECRAFT);
    private static final BooleanSetting SHOW_SHADOW = new BooleanSetting("Font Shadow", "Toggle between shadow on text font", false);
    private static final BooleanSetting SHOW_OUTLINE = new BooleanSetting("Toggle ToolTip Border", "Toggle ToolTip border", false);
    private static final ColorSetting TOOLTIP_BORDER_COLOR = new ColorSetting("Tooltip Border Color", "Change the color of the tooltip border", 0xffffffff);
    private static final NumberSetting FONT_SIZE = new NumberSetting("Font size", "Drag across this slider to change the active font size in real time.", 11, 5, 20);
    private static final NumberSetting CLICK_GUI_BORDER_RADIUS = new NumberSetting("Border Radius", "Change the roundedness of the borders", 13, 1,13);
    private static final NumberSetting CLICK_GUI_HEADER_RADIUS = new NumberSetting("Header Radius","Change the header radius", 13, 1, 13);
    private static final BooleanSetting SHOW_TOOL_TIP = new BooleanSetting("Draw Tooltips", "Show the tooltips for each module and their respective setting", true);
    private static final NumberSetting TOOLTIP_BORDER_RADIUS = new NumberSetting("Tool tip border radius", "Change the roundedness of the border radius", 5, 0, 15);
    private static final BooleanSetting BLUR_BACKGROUND = new BooleanSetting("Blur Background", "Blur the background", true);
    private static final NumberSetting COLUMN_GAP = new NumberSetting("Column Gap", "Change the gap of the columns", ClickGuiLayout.getColumnGap(), 1, 20);
    private static final NumberSetting MODULE_WIDTH = new NumberSetting("Module Width", "Change the width of the module", 120, 100, 170);
    private static final NumberSetting MODULE_HEIGHT = new NumberSetting("Module height", "Change the height of each module collectively", ClickGuiLayout.getRowHeight(), 10, 30);
    private static final BooleanSetting SHOW_HUD_INFO_TEXT = new BooleanSetting("Show HUD info text", "The text on the bottom left", true);
    private static final NumberSetting KNOB_BORDER_RADIUS = new NumberSetting("Knob Border Radius", "How round the knob slider is", 3, 0, 3);
    private static final NumberSetting KNOB_WIDTH = new NumberSetting("Knob Width", "Knob width changer", 5, 5, 10);
    private static final NumberSetting KNOB_HEIGHT = new NumberSetting("Knob Height", "Knob height changer", 4, 4, 6);
    private static final NumberSetting SCROLL_SPEED = new NumberSetting("Scroll Speed", "Change the scroll speed of the clickgui", 10, 1, 20);
    private static final NumberSetting ANIMATION_SPEED = new NumberSetting("Animation Speed", "Speed of the entry", 180, 1, 1000);
    private static final BooleanSetting TOGGLE_CATEGORY_LINE = new BooleanSetting("Toggle Category White Line", "Toggle the white line on the category header", true);
    private static final NumberSetting CATEGORY_LINE_WIDTH = new NumberSetting("Category Line Width", "Change the length of the white line on the category header", 170, 10, 170);
    private static final ColorSetting CATEGORY_LINE_COLOR = new ColorSetting("Category Line Color", "Change the color of the line on the category header", RenderUtil2D.getGuiTextColor());
    private static final ColorSetting COLOR_SETTING_BUTTON_COLOR = new ColorSetting("Color Copy Button Color", "Change the color of the copy button", RenderUtil2D.getGuiColorPanel());
    private static final ColorSetting COLOR_SETTING_TEXT_COLOR = new ColorSetting("Color Copy Text Color", "Change the color of the copy text", RenderUtil2D.getGuiTextColor());
    private static final BooleanSetting COLOR_SETTING_TEXT_DROP_SHADOW = new BooleanSetting("Copy Text Drop Shadow", "Add / Remove drop shadow to copy text button", false);
    private static final ColorSetting ALPHA_CHANNEL_BACKGROUND_COLOR = new ColorSetting("Alpha Channel Background Color", "Alpha slider background changer", RenderUtil2D.getGuiTextColor());
    private static final NumberSetting ALPHA_CHANNEL_BORDER_RADIUS = new NumberSetting("Alpha Channel Radius", "Roundedness of borders on alpha channel", 1, 1, 3);
    private static final NumberSetting ALPHA_CHANNEL_HEIGHT = new NumberSetting("Alpha Channel Height", "Height changer", 2, 1, 10);
    private static final BooleanSetting TOP_LEFT_ALPHA_CHANNEL_ROUND = new BooleanSetting("Top Left Alpha Channel Round", "Toggle the top left corner of the alpha channel", true);
    private static final BooleanSetting TOP_RIGHT_ALPHA_CHANNEL_ROUND = new BooleanSetting("Top Right Alpha Channel Round", "Toggle the top right corner of the alpha channel", true);
    private static final BooleanSetting BOTTOM_LEFT_ALPHA_CHANNEL_ROUND = new BooleanSetting("Bottom Left Alpha Channel Round", "Toggle the bottom left corner of the alpha channel", true);
    private static final BooleanSetting BOTTOM_RIGHT_ALPHA_CHANNEL_ROUND = new BooleanSetting("Bottom Right Alpha Channel Round", "Toggle the bottom right corner of the alpha channel", true);
    private static final ColorSetting ALPHA_CHANNEL_SLIDER_BUTTON_COLOR = new ColorSetting("Alpha Channel Button Color", "Color of alpha channel slider button", 0xffffffff);
    private static final NumberSetting ALPHA_CHANNEL_SLIDER_BUTTON_BORDER_RADIUS = new NumberSetting("Alpha Button Radius", "Roundedness of alpha button", 6, 1, 10);
    private static final BooleanSetting CATEGORY_TOP_LEFT_ROUND = new BooleanSetting("Category Top Left Round", "Toggle the top left corner of the category header", true);
    private static final BooleanSetting CATEGORY_TOP_RIGHT_ROUND = new BooleanSetting("Category Top Right Round", "Toggle the top right corner of the category header", true);
    private static final BooleanSetting CATEGORY_BOTTOM_LEFT_ROUND = new BooleanSetting("Category Bottom Left Round", "Toggle the bottom left corner of the category header", true);
    private static final BooleanSetting CATEGORY_BOTTOM_RIGHT_ROUND = new BooleanSetting("Category Bottom Right Round", "Toggle the bottom right corner of the category header", true);
    private static final NumberSetting MODULE_GAP_HEIGHT = new NumberSetting("Module Gap Height", "Change the gap between each module row", 1, 0, 10);
    private static final BooleanSetting TOGGLE_BOTTOM_ROUNDED_RECT = new BooleanSetting("Rounded Rect Bottom", "The semi-ellipse on the bottom of each column", true);
    private static final ColorSetting COLOR_BOTTOM_ROUNDED_RECT = new ColorSetting("Color Bottom", "Bottom rect color", RenderUtil2D.getGuiColorPanel());
    private static final NumberSetting BOTTOM_ROUNDED_RECT_HEIGHT = new NumberSetting("Rounded Bottom Rect Height", "Does this really need an explanation", 25, 1, 50);
    private static final BooleanSetting OUTLINE_TOGGLE = new BooleanSetting("Toggle Outline", "Outline of the modules on the clickgui", true);
    private static final ColorSetting OUTLINE_COLOR = new ColorSetting("Outline Color", "Change the color of the outline", 0xFFFFFFFF);
    public ClickGuiModule() {
        super(NAME, CATEGORY, ENABLED, DESCRIPTION);
        super.addSetting(
                CATEGORY_COLOR, MODULE_COLOR, TOOL_TIP_BACKGROUND_COLOR, TOOL_TIP_TEXT_BACKGROUND_COLOR, TOOLTIP_BORDER_COLOR, CATEGORY_TEXT_COLOR, MODULE_TEXT_COLOR, SETTINGS_TEXT_COLOR, CATEGORY_LINE_COLOR, COLOR_SETTING_BUTTON_COLOR, COLOR_SETTING_TEXT_COLOR, ALPHA_CHANNEL_BACKGROUND_COLOR, ALPHA_CHANNEL_SLIDER_BUTTON_COLOR, COLOR_BOTTOM_ROUNDED_RECT, OUTLINE_COLOR,
                FONT, SHOW_SHADOW, FONT_SIZE, COLOR_SETTING_TEXT_DROP_SHADOW,
                SHOW_OUTLINE, SHOW_TOOL_TIP,
                TOOLTIP_BORDER_RADIUS, CLICK_GUI_BORDER_RADIUS, CLICK_GUI_HEADER_RADIUS, KNOB_BORDER_RADIUS, ALPHA_CHANNEL_BORDER_RADIUS, ALPHA_CHANNEL_SLIDER_BUTTON_BORDER_RADIUS,
                BLUR_BACKGROUND,
                MODULE_WIDTH, MODULE_HEIGHT, COLUMN_GAP, MODULE_GAP_HEIGHT,
                SHOW_HUD_INFO_TEXT,
                KNOB_WIDTH, KNOB_HEIGHT,
                SCROLL_SPEED, ANIMATION_SPEED,
                TOGGLE_CATEGORY_LINE, CATEGORY_LINE_WIDTH,
                ALPHA_CHANNEL_HEIGHT,
                TOP_LEFT_ALPHA_CHANNEL_ROUND, TOP_RIGHT_ALPHA_CHANNEL_ROUND, BOTTOM_LEFT_ALPHA_CHANNEL_ROUND, BOTTOM_RIGHT_ALPHA_CHANNEL_ROUND, CATEGORY_TOP_LEFT_ROUND, CATEGORY_TOP_RIGHT_ROUND, CATEGORY_BOTTOM_LEFT_ROUND, CATEGORY_BOTTOM_RIGHT_ROUND,
                TOGGLE_BOTTOM_ROUNDED_RECT, BOTTOM_ROUNDED_RECT_HEIGHT,
                OUTLINE_TOGGLE
        );
        super.setKeybind(GLFW.GLFW_KEY_RIGHT_SHIFT);
    }
    public static int getOutlineColor() {
        return OUTLINE_COLOR.color();
    }
    public static boolean getOutlineToggle() {
        return OUTLINE_TOGGLE.value();
    }
    public static int getFooterHeight() {
        return BOTTOM_ROUNDED_RECT_HEIGHT.value();
    }
    public static int getColorBottomRoundedRect() {
        return COLOR_BOTTOM_ROUNDED_RECT.color();
    }

    public static boolean getToggleBottomRoundedRect() {
        return TOGGLE_BOTTOM_ROUNDED_RECT.value();
    }
    public static int getModuleGapHeight() {
        return MODULE_GAP_HEIGHT.value();
    }
    public static boolean getCategoryTopLeftRound() {
        return CATEGORY_TOP_LEFT_ROUND.value();
    }
    public static boolean getCategoryTopRightRound() {
        return CATEGORY_TOP_RIGHT_ROUND.value();
    }
    public static boolean getCategoryBottomLeftRound() {
        return CATEGORY_BOTTOM_LEFT_ROUND.value();
    }
    public static boolean getCategoryBottomRightRound() {
        return CATEGORY_BOTTOM_RIGHT_ROUND.value();
    }

    public static int getAlphaChannelButtonRadius() {
        return ALPHA_CHANNEL_SLIDER_BUTTON_BORDER_RADIUS.value();
    }
    public static int getAlphaChannelButtonColor() {
        return ALPHA_CHANNEL_SLIDER_BUTTON_COLOR.color();
    }
    public static boolean getTopLeftAlphaChannelRound() {
        return TOP_LEFT_ALPHA_CHANNEL_ROUND.value();
    }
    public static boolean getTopRightAlphaChannelRound() {
        return TOP_RIGHT_ALPHA_CHANNEL_ROUND.value();
    }
    public static boolean getBottomLeftAlphaChannelRound() {
        return BOTTOM_LEFT_ALPHA_CHANNEL_ROUND.value();
    }
    public static boolean getBottomRightAlphaChannelRound() {
        return BOTTOM_RIGHT_ALPHA_CHANNEL_ROUND.value();
    }

    public static int getAlphaChannelHeight() {
        return ALPHA_CHANNEL_HEIGHT.value();
    }
    public static int getAlphaChannelBackgroundColor() {
        return ALPHA_CHANNEL_BACKGROUND_COLOR.color();
    }

    public static int getAlphaChannelBorderRadius() {
        return ALPHA_CHANNEL_BORDER_RADIUS.value();
    }

    public static boolean getCopyButtonDropShadow() {
        return COLOR_SETTING_TEXT_DROP_SHADOW.value();
    }

    public static int getCopyButtonColorSetting() {
        return COLOR_SETTING_BUTTON_COLOR.color();
    }
    public static int getCopyTextColorSetting() {
        return COLOR_SETTING_TEXT_COLOR.color();
    }

    public static int getCategoryLineColor() {
        return CATEGORY_LINE_COLOR.color();
    }

    public static int getCategoryLineWidth() {
        return CATEGORY_LINE_WIDTH.value();
    }

    public static boolean getToggleCategoryLine() {
        return TOGGLE_CATEGORY_LINE.value();
    }

    public static float getAnimationSpeed() {
        return (float) ANIMATION_SPEED.value();
    }

    public static int getScrollSpeed() {
        return SCROLL_SPEED.value();
    }

    public static int getKnobHeight() {
        return KNOB_HEIGHT.value();
    }

    public static int getKnobWidth() {
        return KNOB_WIDTH.value();
    }

    public static int getKnobBorderRadius() {
        return KNOB_BORDER_RADIUS.value();
    }

    public static boolean getShowHudInfoText() {
        return SHOW_HUD_INFO_TEXT.value();
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

}
