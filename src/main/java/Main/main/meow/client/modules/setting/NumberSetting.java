package Main.main.meow.client.modules.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/**
 * Whole-number setting shown as a slider in the ClickGUI.
 */
public final class NumberSetting extends Setting<Integer> {
    private final int min;
    private final int max;

    public NumberSetting(String name, String description, int defaultValue, int min, int max) {
        super(name, description, Math.max(min, Math.min(max, defaultValue)));
        this.min = min;
        this.max = max;
    }

    public int number() {
        return value();
    }

    public int min() {
        return min;
    }

    public int max() {
        return max;
    }

    // 0..1 position of the current value on the slider
    public float progress() {
        return (value() - min) / (float) (max - min);
    }

    public void setProgress(double progress) {
        setValue(min + (int) Math.round(Math.max(0d, Math.min(1d, progress)) * (max - min)));
    }

    @Override
    public void setValue(Integer value) {
        super.setValue(Math.max(min, Math.min(max, value)));
    }

    @Override
    public String label() {
        return name() + ": " + value();
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
            setValue(element.getAsInt());
        }
    }
}