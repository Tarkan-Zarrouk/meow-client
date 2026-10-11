package Main.main.meow.manager;

import Main.main.meow.client.module.Module;
import Main.main.meow.client.ui.clickgui.Category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public final class ModuleManager {
    private final List<Main.main.meow.client.module.Module> modules = new ArrayList<>();
    private Runnable changeListener = () -> {};
    private Consumer<Main.main.meow.client.module.Module> enabledChangeListener = module -> {};
    private final Set<Integer> pressedKeybinds = new HashSet<>();

    public void register(Main.main.meow.client.module.Module module) {
        if (modules.stream().anyMatch(existing -> existing.name().equalsIgnoreCase(module.name()))) {
            throw new IllegalArgumentException("A module named " + module.name() + " is already registered");
        }
        module.onChange(changeListener);
        module.onEnabledChange(enabledChangeListener);
        modules.add(module);
    }

    public void setChangeListener(Runnable listener) {
        changeListener = listener;
        modules.forEach(module -> module.onChange(listener));
    }

    public void setEnabledChangeListener(Consumer<Main.main.meow.client.module.Module> listener) {
        enabledChangeListener = listener;
        modules.forEach(module -> module.onEnabledChange(listener));
    }

    public List<Main.main.meow.client.module.Module> all() {
        return Collections.unmodifiableList(modules);
    }

    public List<Main.main.meow.client.module.Module> inCategory(Category category) {
        return modules.stream().filter(module -> module.category() == category).toList();
    }

    public Main.main.meow.client.module.Module byName(String name) {
        return modules.stream().filter(module -> module.name().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public void handleKeybinds(long windowHandle) {
        for (Module module : modules) {
            if (module.name().equalsIgnoreCase("ClickGUI")) continue;
            int key = module.keybind();
            if (key == org.lwjgl.glfw.GLFW.GLFW_KEY_UNKNOWN) continue;
            boolean down = org.lwjgl.glfw.GLFW.glfwGetKey(windowHandle, key) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
            if (down && pressedKeybinds.add(key)) module.toggle();
            if (!down) pressedKeybinds.remove(key);
        }
    }
}
