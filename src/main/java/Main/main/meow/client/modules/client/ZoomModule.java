package Main.main.meow.client.modules.client;

import Main.main.meow.client.modules.category.Category;
import Main.main.meow.client.modules.setting.KeybindSetting;
import org.lwjgl.glfw.GLFW;

public final class ZoomModule extends ClientModule {
    private final KeybindSetting zoomKey = addSetting(new KeybindSetting("Zoom key",
            "Hold this key to smoothly zoom to a 30 degree field of view. Press Delete to clear it.",
            GLFW.GLFW_KEY_C));

    public ZoomModule() {
        super("Zoom", Category.CLIENT, false, "Hold the zoom key to smoothly zoom the camera like OptiFine.");
    }

    public boolean isZooming(long window) {
        return isEnabled() && zoomKey.isBound()
                && GLFW.glfwGetKey(window, zoomKey.key()) == GLFW.GLFW_PRESS;
    }


}
