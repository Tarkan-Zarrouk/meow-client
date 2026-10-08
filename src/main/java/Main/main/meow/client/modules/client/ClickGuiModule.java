package Main.main.meow.client.modules.client;

import Main.main.meow.client.modules.category.Category;
import org.lwjgl.glfw.GLFW;

public final class ClickGuiModule extends ClientModule {
    public ClickGuiModule() {
        super("ClickGUI", Category.CLIENT, true,
                "Opens this ClickGUI and lets you customize its colors and keybind.");
        setKeybind(GLFW.GLFW_KEY_RIGHT_SHIFT);
    }
}
