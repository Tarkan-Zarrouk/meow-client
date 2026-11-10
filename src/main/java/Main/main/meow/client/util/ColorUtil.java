package Main.main.meow.client.util;

// comp 2401 taught bitwise operations, ty connor hillen <3
public class ColorUtil {

    /**
     * pack into 32bit ARGB integer
     * @param r red component
     * @param g green component
     * @param b blue component
     * @param a alpha component
     * @return 32bit ARGB integer
     */
    public static int toARGB(int r, int g, int b, int a) {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /**
     * get red component from 32bit ARGB integer
     * @param color 32bit ARGB integer
     * @return red component in [0, 1] range
     */
    public static int getRed(int color) {
        return ((color >>> 16) & 0xff);
    }

    /**
     * get green component from 32bit ARGB integer
     * @param color 32bit ARGB integer
     * @return green component in [0, 1] range
     */
    public static int getGreen(int color) {
        return ((color >>> 8) & 0xff);
    }

    /**
     * get blue component from 32bit ARGB integer
     * @param color 32bit ARGB integer
     * @return blue component in [0, 1] range
     */
    public static int getBlue(int color) {
        return (color & 0xff);
    }

    /**
     * get alpha component from 32bit ARGB integer
     * @param color 32bit ARGB integer
     * @return alpha component in [0, 1] range
     */
    public static int getAlpha(int color) {
        return ((color >>> 24) & 0xff);
    }
}
