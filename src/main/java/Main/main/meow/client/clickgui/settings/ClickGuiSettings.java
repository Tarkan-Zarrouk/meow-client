package Main.main.meow.client.clickgui.settings;

public final class ClickGuiSettings {
    private static int categoryColor = 0xffc52dbd;
    private static int moduleColor = 0xffc52dbd;
    private static boolean odinFont;
    private static int fontSize = 11;

    public static int categoryColor() {
        return categoryColor;
    }

    public static int moduleColor() {
        return moduleColor;
    }

    public static boolean odinFont() {
        return odinFont;
    }

    public static void setOdinFont(boolean enabled) {
        odinFont = enabled;
    }

    public static int fontSize() {
        return fontSize;
    }

    public static void setFontSize(int size) {
        fontSize = Math.max(5, Math.min(20, size));
    }

    public static void setCategoryColor(int color) {
        categoryColor = color | 0xff000000;
    }

    public static void setModuleColor(int color) {
        moduleColor = color | 0xff000000;
    }

    public static int categoryColorValue() {
        return categoryColor;
    }

    public static int moduleColorValue() {
        return moduleColor;
    }
}
