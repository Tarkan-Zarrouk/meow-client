package Main.main.meow.client.module.modules.client;

import Main.main.meow.client.MeowClient;
import Main.main.meow.client.ui.clickgui.Category;
import Main.main.meow.client.setting.modulesettings.KeybindSetting;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public final class ZoomModule extends ClientModule {
    private static final float ZOOM_FOV = 30f;
    private static float progress;
    private static long lastUpdate = System.nanoTime();
    private final KeybindSetting zoomKey = new KeybindSetting("Zoom key", "Hold this key to smoothly zoom to a 30 degree field of view. Press Delete to clear it.", GLFW.GLFW_KEY_C);

    public ZoomModule() {
        super("Zoom", Category.CLIENT, false, "Hold the zoom key to smoothly zoom the camera like OptiFine.");
        addSetting(zoomKey);
    }

    private static void update(long window) {
        // had to use ai for this method, i literally have no idea how to do this without it, i tried for 2 hours and gave up
        ZoomModule zoom = (ZoomModule) MeowClient.MODULE_MANAGER.byName("Zoom");
        boolean active = zoom != null && zoom.isZooming(window);
        long now = System.nanoTime();
        float delta = Math.min(0.1f, (now - lastUpdate) / 1_000_000_000f);
        lastUpdate = now;
        float target;
        if (active) {
            target = 1f;
        } else {
            target = 0f;
        }
        float smoothing = 1f - (float) Math.exp(-delta * 14f); // what are we doing vro :wilted_rose:
        progress += (target - progress) * smoothing;
        if (Math.abs(target - progress) < 0.001f) progress = target;
    }

    public static float apply(float baseFov) {
        Minecraft client = Minecraft.getInstance();
        update(client.getWindow().handle());
        float eased = progress * progress * (3f - 2f * progress);
        return baseFov + (ZOOM_FOV - baseFov) * eased;
    }

    public boolean isZooming(long window) {
        return isEnabled() && zoomKey.isBound() && GLFW.glfwGetKey(window, zoomKey.key()) == GLFW.GLFW_PRESS;
    }


}
