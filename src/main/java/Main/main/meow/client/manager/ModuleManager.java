package Main.main.meow.client.manager;

import Main.main.meow.client.module.Module;
import Main.main.meow.client.ui.clickgui.model.Category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public final class ModuleManager {
    private final List<Module> modules = new ArrayList<>();
    private Runnable changeListener = () -> {};
    private Consumer<Module> enabledChangeListener = module -> {};
    private final Set<Integer> pressedKeybinds = new HashSet<>();

    public void register(Module... module) {
        for(Module registeredModule : module) {
            if (modules.stream().anyMatch(existing -> existing.getName().equalsIgnoreCase(registeredModule.getName()))) {
                throw new IllegalArgumentException("A module named " + registeredModule.getName() + " is already registered");
            }

            registeredModule.onChange(changeListener);
            registeredModule.onEnabledChange(enabledChangeListener);
            modules.add(registeredModule);
        }
    }

    public void setChangeListener(Runnable listener) {
        changeListener = listener;
        modules.forEach(module -> module.onChange(listener));
    }

    public void setEnabledChangeListener(Consumer<Module> listener) {
        enabledChangeListener = listener;
        modules.forEach(module -> module.onEnabledChange(listener));
    }

    public List<Module> all() {
        return Collections.unmodifiableList(modules);
    }

    public List<Module> inCategory(Category category) {
        return modules.stream().filter(module -> module.getCategory() == category).toList();
    }

    public Module byName(String name) {
        return modules.stream().filter(module -> module.getName().equalsIgnoreCase(name)).findFirst().orElseThrow(() -> new IllegalArgumentException("MODULE_MANAGER.byName(" + name + ") not found"));

    }

    public void handleKeybinds(long windowHandle) {
        for (Module module : modules) {
            if (module.getName().equalsIgnoreCase("ClickGUI")) continue;
            int key = module.keybind();
            if (key == org.lwjgl.glfw.GLFW.GLFW_KEY_UNKNOWN) continue;
            boolean down = org.lwjgl.glfw.GLFW.glfwGetKey(windowHandle, key) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
            if (down && pressedKeybinds.add(key)) module.toggle();
            if (!down) pressedKeybinds.remove(key);
        }
    }
}
