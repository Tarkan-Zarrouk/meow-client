package Main.main.meow.client.modules.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public final class EnumSetting<E extends Enum<E>> extends Setting<E> {
    private final E[] options;

    public EnumSetting(String name, String description, E defaultValue) {
        super(name, description, defaultValue);
        this.options = defaultValue.getDeclaringClass().getEnumConstants();
    }

    @Override
    public String label() {
        String text = value().name();
        return name() + ": " + text.charAt(0) + text.substring(1).toLowerCase();
    }

    // cycles to the next option
    @Override
    public void click() {
        setValue(options[(value().ordinal() + 1) % options.length]);
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(value().name());
    }

    @Override
    public void fromJson(JsonElement element) {
        if (element == null || !element.isJsonPrimitive()) {
            return;
        }
        for (E option : options) {
            if (option.name().equals(element.getAsString())) {
                setValue(option);
                return;
            }
        }
    }
}
