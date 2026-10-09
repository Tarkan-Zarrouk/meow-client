package Main.main.meow.client.modules.client;

import Main.main.meow.client.MeowClient;
import net.minecraft.client.Minecraft;

public final class ZoomController {
    private static final float ZOOM_FOV = 30f;
    private static float progress;
    private static long lastUpdate = System.nanoTime();

    private ZoomController() {
    }

    public static void tick(long window) {
        update(window);
    }

    private static void update(long window) {
        // had to use ai for this method, i literally have no idea how to do this without it, i tried for 2 hours and gave up
        ZoomModule zoom = (ZoomModule) MeowClient.MODULE_MANAGER.byName("Zoom");
        boolean active = zoom != null && zoom.isZooming(window);
        long now = System.nanoTime();
        float delta = Math.min(0.1f, (now - lastUpdate) / 1_000_000_000f);
        lastUpdate = now;
        float target = active ? 1f : 0f;
        float smoothing = 1f - (float) Math.exp(-delta * 14f);
        progress += (target - progress) * smoothing;
        if (Math.abs(target - progress) < 0.001f) progress = target;
    }

    public static float apply(float baseFov) {
        Minecraft client = Minecraft.getInstance();
        update(client.getWindow().handle());
        float eased = progress * progress * (3f - 2f * progress);
        return baseFov + (ZOOM_FOV - baseFov) * eased;
    }
}
