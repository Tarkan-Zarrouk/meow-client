package Main.main.meow.client.modules.client;

import Main.main.meow.client.clickgui.settings.ClickGuiSettings;
import Main.main.meow.client.modules.category.Category;
import Main.main.meow.client.modules.setting.BooleanSetting;
import Main.main.meow.client.modules.setting.ColorSetting;
import Main.main.meow.client.modules.setting.NumberSetting;
import org.lwjgl.glfw.GLFW;

public final class ClickGuiModule extends ClientModule {
    public static final ColorSetting CATEGORY_COLOR = new ColorSetting("Category colour", "Opens a picker that changes the color of every category header in real time.", 0xffc52dbd);
    public static final ColorSetting MODULE_COLOR = new ColorSetting("Module colour", "Opens a picker that changes the color of every module row in real time.", 0xffc52dbd);
    public static final BooleanSetting FONT = new BooleanSetting("Font", "Switches the ClickGUI between the custom Inter font and Minecraft's default font.", false, "Font: Inter", "Font: Minecraft");
    public static final NumberSetting FONT_SIZE = new NumberSetting("Font size", "Drag across this slider to change the active font size in real time.", 11, 5, 20);
    public ClickGuiModule() {
        super("ClickGUI", Category.CLIENT, true, "Opens this ClickGUI and lets you customize its colors and keybind.");
        addSetting(CATEGORY_COLOR);
        addSetting(MODULE_COLOR);
        addSetting(FONT);
        addSetting(FONT_SIZE);

        setKeybind(GLFW.GLFW_KEY_RIGHT_SHIFT);
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
