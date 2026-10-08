package Main.main.meow.client.modules.client;

import Main.main.meow.client.modules.category.Category;
import org.lwjgl.glfw.GLFW;

public final class ZoomModule extends ClientModule {
    private int zoomKeybind = GLFW.GLFW_KEY_C;

    public ZoomModule() {
        super("Zoom", Category.CLIENT, false,
                "Hold the zoom key to smoothly zoom the camera like OptiFine.");
    }

    public int zoomKeybind() {
        return zoomKeybind;
    }

    public void setZoomKeybind(int keybind) {
        zoomKeybind = keybind;
    }

    public boolean isZooming(long window) {
        return isEnabled() && zoomKeybind != GLFW.GLFW_KEY_UNKNOWN
                && GLFW.glfwGetKey(window, zoomKeybind) == GLFW.GLFW_PRESS;
    }
}
