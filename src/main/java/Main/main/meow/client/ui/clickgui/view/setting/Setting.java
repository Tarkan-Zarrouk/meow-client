package Main.main.meow.client.ui.clickgui.view.setting;

import com.google.gson.JsonElement;

public abstract class Setting<T> {
    private final String name;
    private final String description;
    private T value;

    protected Setting(String name, String description, T defaultValue) {
        this.name = name;
        this.description = description;
        this.value = defaultValue;
    }

    public final String name() {
        return name;
    }

    public final String description() {
        return description;
    }

    public final T value() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public abstract String label();

    // left click in the ClickGUI
    public abstract void click();

    public abstract JsonElement toJson();

    // must ignore malformed values and keep the current one
    public abstract void fromJson(JsonElement element);
}
