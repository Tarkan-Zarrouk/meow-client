package Main.main.meow.client.modules.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public final class BooleanSetting extends Setting<Boolean> {
    private final String onLabel;
    private final String offLabel;

    public BooleanSetting(String name, String description, boolean defaultValue) {
        this(name, description, defaultValue, name + ": On", name + ": Off");
    }

    public BooleanSetting(String name, String description, boolean defaultValue, String onLabel, String offLabel) {
        super(name, description, defaultValue);
        this.onLabel = onLabel;
        this.offLabel = offLabel;
    }

    public boolean enabled() {
        return value();
    }

    @Override
    public String label() {
        if (value()) {
            return onLabel;
        }
        return offLabel;
    }

    @Override
    public void click() {
        setValue(!value());
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(value());
    }

    @Override
    public void fromJson(JsonElement element) {
        if (element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean()) {
            setValue(element.getAsBoolean());
        }
    }
}
