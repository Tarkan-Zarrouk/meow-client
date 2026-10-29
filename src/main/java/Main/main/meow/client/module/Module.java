package Main.main.meow.client.module;

import Main.main.meow.client.ui.clickgui.model.Category;
import Main.main.meow.client.ui.clickgui.view.setting.Setting;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_UNKNOWN;

/**
 * Base type for client features. Modules can be registered in ModuleManager when they are ready.
 */
public abstract class Module {
    private String name;
    private String description;
    private Category category;
    private boolean enabled;
    private int keybind = GLFW_KEY_UNKNOWN;
    private final List<Setting<?>> settings = new ArrayList<>();
    private Runnable changeListener = () -> {};
    private Consumer<Module> enabledChangeListener = module -> {};

    public Module(String name, Category category, boolean enabled, String description) {
        this.name = name;
        this.category = category;
        this.enabled = enabled;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setEnabledVal(boolean enabled) {
        this.enabled = enabled;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public final List<Setting<?>> settings() {
        return Collections.unmodifiableList(settings);
    }
    @SafeVarargs // supresses the warning about generic array creation (i.e., generic array creation happens when you use varargs with generics, which is not type-safe (StackOverflow lol))
    public final <S extends Setting<?>> S addSetting(S... setting) {
        settings.addAll(Arrays.asList(setting));
        return setting[0];
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
        setEnabled(!getEnabled());
    }

    public final void setEnabled(boolean enabled) {
        if (getEnabled() == enabled) {
            return;
        }
        setEnabledVal(enabled);
        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
        enabledChangeListener.accept(this);
        changeListener.run();
    }

    public final void onChange(Runnable listener) {
        changeListener = listener;
    }

    public final void onEnabledChange(Consumer<Module> listener) {
        enabledChangeListener = listener;
    }

    protected void onEnable() {
    }

    protected void onDisable() {
    }
}
