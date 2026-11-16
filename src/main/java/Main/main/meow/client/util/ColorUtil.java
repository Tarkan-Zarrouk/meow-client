package Main.main.meow.client.util;

// comp 2401 taught bitwise operations, ty connor hillen <3
public class ColorUtil {
    public static final int ROW = 0xe62c313b, ROW_HOVER = 0xf03a404b, SETTING_ROW = 0xff171a22, MUTED = 0xffc9c8cc, BORDER = 0xffffffff;

    /**
     * Default row color value
     * @return Hexadecimal value pertaining to the defaulted color
     */
    public static int getRowColor() {
        return ROW;
    }

    /**
     * Default row hover value
     * @return Hexadecimal value pertaining to the defaulted hover color value (this is based on the getRowColor hexadecimal value at a lighter opacity)
     */
    public static int getRowHoverColor() {
        return ROW_HOVER;
    }

    /**
     * Default setting row background color
     * @return Hexadecimal value pertaining to the defaulted setting row color
     */
    public static int getSettingRowColor() {
        return SETTING_ROW;
    }

    /**
     * A 'muted' look
     * @return Low saturation color that gives a 'muted' look
     */
    public static int getMutedColor() {
        return MUTED;
    }

    /**
     * White.
     * @return Hexadecimal equivalent to opaque white.
     */
    public static int getBorderColor() {
        return BORDER;
    }

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
