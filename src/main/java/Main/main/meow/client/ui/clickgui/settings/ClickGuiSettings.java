package Main.main.meow.client.ui.clickgui.settings;

import Main.main.meow.client.setting.modulesettings.BooleanSetting;
import Main.main.meow.client.setting.modulesettings.colorpicker.ColorSetting;
import Main.main.meow.client.setting.NumberSetting;

/**
 * The ClickGUI's own settings. ClickGuiModule registers these, so the GUI and config treat them like any other setting.
 */
public final class ClickGuiSettings {
    public static final ColorSetting CATEGORY_COLOR = new ColorSetting("Category colour", "Opens a picker that changes the color of every category header in real time.", 0xffc52dbd);
    public static final ColorSetting MODULE_COLOR = new ColorSetting("Module colour", "Opens a picker that changes the color of every module row in real time.", 0xffc52dbd);
    public static final BooleanSetting FONT = new BooleanSetting("Font", "Switches the ClickGUI between the custom Inter font and Minecraft's default font.", false, "Font: Inter", "Font: Minecraft");
    public static final NumberSetting FONT_SIZE = new NumberSetting("Font size", "Drag across this slider to change the active font size in real time.", 11, 5, 20);
    private ClickGuiSettings() {
    }

    public static int categoryColor() {
        return CATEGORY_COLOR.color();
    }

    public static int moduleColor() {
        return MODULE_COLOR.color();
    }

    public static boolean odinFont() {
        return FONT.enabled();
    }

    public static int fontSize() {
        return FONT_SIZE.number();
    }
}
