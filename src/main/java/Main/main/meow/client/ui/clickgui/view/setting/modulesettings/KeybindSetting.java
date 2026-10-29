package Main.main.meow.client.ui.clickgui.view.setting.modulesettings;

import Main.main.meow.client.ui.clickgui.view.setting.Setting;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.lwjgl.glfw.GLFW;

/**
 * Stores a GLFW key code. The ClickGUI handles clicks on it by waiting for the next key press.
 */
public final class KeybindSetting extends Setting<Integer> {
    private final String displayName;

    public KeybindSetting(String name, String description, int defaultKey) {
        super(name, description, defaultKey);
        this.displayName = name;
    }

    public int key() {
        return value();
    }

    public boolean isBound() {
        return value() != GLFW.GLFW_KEY_UNKNOWN;
    }

    @Override
    public String label() {
        return displayName + ": ";
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
