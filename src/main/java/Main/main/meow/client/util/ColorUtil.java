package Main.main.meow.client.util;

public class ColorUtil {
    // ((color >>> 16) & 0xff) / 255f, ((color >>> 8) & 0xff) / 255f, (color & 0xff) / 255f, 0.5f

    public static float getRed(int color) {
        return ((color >>> 16) & 0xff) / 255f;
    }

    public static float getGreen(int color) {
        return ((color >>> 8) & 0xff) / 255f;
    }
    public static float getBlue(int color) {
        return (color & 0xff) / 255f;
    }
    public static float getAlpha(int color) {
        return ((color >>> 24) & 0xff) / 255f;
    }
}
