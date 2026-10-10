package Main.main.meow.client.modules;

import Main.main.meow.client.modules.category.Category;
import Main.main.meow.client.modules.setting.Setting;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_UNKNOWN;

/**
 * Base type for client features. Modules can be registered in ModuleManager when they are ready.
 */
public abstract class Module {
    private final String name;
    private final String description;
    private final Category category;
    private boolean enabled;
    private int keybind = GLFW_KEY_UNKNOWN;
    private final List<Setting<?>> settings = new ArrayList<>();
    private Runnable changeListener = () -> {};
    private Consumer<Module> enabledChangeListener = module -> {};

    protected Module(String name, Category category) {
        this(name, category, "");
    }

    protected Module(String name, Category category, String description) {
        this.name = name;
        this.category = category;
        this.description = description;
    }

    public final String name() {
        return name;
    }

    public final Category category() {
        return category;
    }

    public final String description() {
        return description;
    }

    public final boolean isEnabled() {
        return enabled;
    }

    public final List<Setting<?>> settings() {
        return Collections.unmodifiableList(settings);
    }

    public final <S extends Setting<?>> S addSetting(S setting) {
        settings.add(setting);
        return setting;
    }

    public final void removeSetting(Setting<?> setting) {
        settings.remove(setting);
    }

    public final int keybind() {
        return keybind;
    }

    public final void setKeybind(int keybind) {
        this.keybind = keybind;
        changeListener.run();
    }

    public final void toggle() {
        setEnabled(!enabled);
    }

    public final void setEnabled(boolean enabled) {
        if (this.enabled == enabled) {
            return;
        }
        this.enabled = enabled;
        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
        enabledChangeListener.accept(this);
        changeListener.run();
    }

    final void onChange(Runnable listener) {
        changeListener = listener;
    }

    final void onEnabledChange(Consumer<Module> listener) {
        enabledChangeListener = listener;
    }

    protected void onEnable() {
    }

    protected void onDisable() {
    }
}
