package Main.main.meow.client.ui.clickgui.view.setting.modulesettings.colorpicker;

import Main.main.meow.client.ui.clickgui.view.setting.Setting;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public final class ColorSetting extends Setting<Integer> {
    private static final int PICKER_WIDTH = 260;
    private static final int PICKER_HEIGHT = 190;

    private final int defaultColor;

    public ColorSetting(String name, String description, int defaultColor) {
        super(name, description, defaultColor);
        this.defaultColor = defaultColor;
    }

    public int color() {
        return value();
    }

    // picker that writes every change straight back into this setting, onChange runs after each update
    public ColorPicker createPicker(Runnable onChange) {
        return new ColorPicker(PICKER_WIDTH, PICKER_HEIGHT, color(), color -> {
            setValue(color);
            onChange.run();
        });
    }

    @Override
    public String label() {
        return name() +  ": " + String.format("#%08X", value());
    }

    @Override
    public void click() {
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(value());
    }

    @Override
    public void fromJson(JsonElement element) {
        if (element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            int stored = element.getAsInt();
            // older configs stored small palette indexes instead of ARGB colors
            if (stored >= 0 && stored < 0x100) {
                stored = defaultColor;
            }
            setValue(stored);
        }
    }
}
