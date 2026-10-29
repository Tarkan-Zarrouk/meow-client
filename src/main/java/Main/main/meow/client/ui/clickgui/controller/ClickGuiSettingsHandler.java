package Main.main.meow.client.ui.clickgui.controller;

import Main.main.meow.client.MeowClient;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.colorpicker.ColorPicker;
import Main.main.meow.client.module.Module;
import Main.main.meow.client.manager.ModuleManager;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.colorpicker.ColorSetting;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.KeybindSetting;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.NumberSetting;
import Main.main.meow.client.ui.clickgui.view.setting.Setting;
import Main.main.meow.client.manager.ConfigManager;
import Main.main.meow.client.ui.clickgui.model.ClickGuiLayoutSettings;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ClickGuiSettingsHandler {
    private final ModuleManager modules;
    private final Set<Module> expanded = new HashSet<>();
    private Module awaitingKeybind;
    private KeybindSetting awaitingSetting;
    private ColorPicker colorPicker;

    public ClickGuiSettingsHandler(ModuleManager modules) {
        this.modules = modules;
    }

    public void loadExpanded() { // to be called after modules are loaded, since the expanded list is stored by module name
        expanded.clear();
        for (Module module : modules.all()) {
            if (ClickGuiLayoutSettings.isExpanded(module.getName())) {
                expanded.add(module);
            }
        }
    }

    public boolean isExpanded(Module module) {
        return expanded.contains(module);
    }

    public void toggleExpanded(Module module) {
        if (!expanded.add(module))
            expanded.remove(module);
        awaitingKeybind = null;
        awaitingSetting = null;
        save();
    }

    public void save() {
        Set<String> expandedNames = new HashSet<>();
        for (Module module : expanded)
            expandedNames.add(module.getName());
        ClickGuiLayoutSettings.setExpandedModules(expandedNames);
        ConfigManager.save(Minecraft.getInstance(), modules, MeowClient.HUD_COMPONENTS);
    }

    public ColorPicker colorPicker() {
        return colorPicker;
    }

    public void closeColorPicker() {
        save();
        colorPicker = null;
    }

    private void openColorPicker(ColorSetting setting) {
        colorPicker = setting.createPicker(this::save);
    }

    // the slider setting at this row, or null when the row is not a slider
    public NumberSetting sliderAt(Module module, int row) {
        if (row >= 1 && row <= module.settings().size() && module.settings().get(row - 1) instanceof NumberSetting slider) {
            return slider;
        }
        return null;
    }

    public void setSliderFromMouse(NumberSetting slider, double mouseX, int columnStart, int columnWidth) {
        double sliderStart = columnStart + 8;
        double sliderWidth = Math.max(1, columnWidth - 16);
        slider.setProgress((mouseX - sliderStart) / sliderWidth);
    }

    // handles every setting click except sliders, which need drag state
    public void clickSetting(Module module, int setting) {
        // row 0 is the keybind, rows 1..n are the module's registered settings in order
        if (setting == 0) {
            awaitingSetting = null;
            awaitingKeybind = module;
            return;
        }
        List<Setting<?>> registered = module.settings();
        if (setting > registered.size()) {
            return;
        }
        Setting<?> clicked = registered.get(setting - 1);
        if (clicked instanceof KeybindSetting keybind) {
            awaitingKeybind = null;
            awaitingSetting = keybind;
        } else if (clicked instanceof ColorSetting color) {
            openColorPicker(color);
        } else {
            clicked.click();
            save();
        }
    }

    // returns true when a pending keybind assignment consumed the key
    public boolean captureKey(int key) {
        int bound = key == GLFW.GLFW_KEY_DELETE ? GLFW.GLFW_KEY_UNKNOWN : key;
        boolean cancelled = key == GLFW.GLFW_KEY_ESCAPE;
        if (awaitingSetting != null) {
            if (!cancelled)
                awaitingSetting.setValue(bound);
            awaitingSetting = null;
            save();
            return true;
        }
        if (awaitingKeybind != null) {
            if (!cancelled)
                awaitingKeybind.setKeybind(bound);
            awaitingKeybind = null;
            return true;
        }
        return false;
    }

    public List<String> settingsFor(Module module) {
        List<String> rows = new ArrayList<>();
        if (awaitingKeybind == module) {
            rows.add("Press a key...");
        } else {
            rows.add("Keybind: " + keyName(module.keybind()));
        }
        for (Setting<?> setting : module.settings()) {
            if (setting instanceof KeybindSetting keybind) {
                if (awaitingSetting == keybind) {
                    rows.add("Press a key...");
                } else {
                    rows.add(keybind.name() + ": " + keyName(keybind.key()));
                }
            } else {
                rows.add(setting.label());
            }
        }
        return rows;
    }

    public String settingDescription(Module module, int setting) {
        if (setting == 0) {
            return "Right-click this setting, then press a key to toggle the module. Press Delete to clear the keybind.";
        }
        List<Setting<?>> registered = module.settings();
        if (setting <= registered.size()) {
            return registered.get(setting - 1).description();
        }
        return "";
    }

    private static String keyName(int key) {
        if (key == GLFW.GLFW_KEY_UNKNOWN) return "None";
        String name = GLFW.glfwGetKeyName(key, 0);
        if (name != null) return name.toUpperCase();
        return switch (key) {
            case GLFW.GLFW_KEY_ESCAPE -> "ESCAPE";
            case GLFW.GLFW_KEY_ENTER -> "ENTER";
            case GLFW.GLFW_KEY_TAB -> "TAB";
            case GLFW.GLFW_KEY_BACKSPACE -> "BACKSPACE";
            case GLFW.GLFW_KEY_INSERT -> "INSERT";
            case GLFW.GLFW_KEY_DELETE -> "DELETE";
            case GLFW.GLFW_KEY_RIGHT -> "RIGHT ARROW";
            case GLFW.GLFW_KEY_LEFT -> "LEFT ARROW";
            case GLFW.GLFW_KEY_DOWN -> "DOWN ARROW";
            case GLFW.GLFW_KEY_UP -> "UP ARROW";
            case GLFW.GLFW_KEY_SPACE -> "SPACE";
            case GLFW.GLFW_KEY_LEFT_SHIFT -> "LEFT SHIFT";
            case GLFW.GLFW_KEY_RIGHT_SHIFT -> "RIGHT SHIFT";
            case GLFW.GLFW_KEY_LEFT_CONTROL -> "LEFT CTRL";
            case GLFW.GLFW_KEY_RIGHT_CONTROL -> "RIGHT CTRL";
            case GLFW.GLFW_KEY_LEFT_ALT -> "LEFT ALT";
            case GLFW.GLFW_KEY_RIGHT_ALT -> "RIGHT ALT";
            case GLFW.GLFW_KEY_CAPS_LOCK -> "CAPS LOCK";
            case GLFW.GLFW_KEY_NUM_LOCK -> "NUM LOCK";
            case GLFW.GLFW_KEY_SCROLL_LOCK -> "SCROLL LOCK";
            case GLFW.GLFW_KEY_PAGE_UP -> "PAGE UP";
            case GLFW.GLFW_KEY_PAGE_DOWN -> "PAGE DOWN";
            case GLFW.GLFW_KEY_HOME -> "HOME";
            case GLFW.GLFW_KEY_END -> "END";
            case GLFW.GLFW_KEY_PRINT_SCREEN -> "PRINT SCREEN";
            case GLFW.GLFW_KEY_PAUSE -> "PAUSE";
            case GLFW.GLFW_KEY_KP_0 -> "NUMPAD 0";
            case GLFW.GLFW_KEY_KP_1 -> "NUMPAD 1";
            case GLFW.GLFW_KEY_KP_2 -> "NUMPAD 2";
            case GLFW.GLFW_KEY_KP_3 -> "NUMPAD 3";
            case GLFW.GLFW_KEY_KP_4 -> "NUMPAD 4";
            case GLFW.GLFW_KEY_KP_5 -> "NUMPAD 5";
            case GLFW.GLFW_KEY_KP_6 -> "NUMPAD 6";
            case GLFW.GLFW_KEY_KP_7 -> "NUMPAD 7";
            case GLFW.GLFW_KEY_KP_8 -> "NUMPAD 8";
            case GLFW.GLFW_KEY_KP_9 -> "NUMPAD 9";
            case GLFW.GLFW_KEY_KP_DECIMAL -> "NUMPAD DECIMAL";
            case GLFW.GLFW_KEY_KP_DIVIDE -> "NUMPAD DIVIDE";
            case GLFW.GLFW_KEY_KP_MULTIPLY -> "NUMPAD MULTIPLY";
            case GLFW.GLFW_KEY_KP_SUBTRACT -> "NUMPAD MINUS";
            case GLFW.GLFW_KEY_KP_ADD -> "NUMPAD PLUS";
            case GLFW.GLFW_KEY_KP_ENTER -> "NUMPAD ENTER";
            case GLFW.GLFW_KEY_F1 -> "F1";
            case GLFW.GLFW_KEY_F2 -> "F2";
            case GLFW.GLFW_KEY_F3 -> "F3";
            case GLFW.GLFW_KEY_F4 -> "F4";
            case GLFW.GLFW_KEY_F5 -> "F5";
            case GLFW.GLFW_KEY_F6 -> "F6";
            case GLFW.GLFW_KEY_F7 -> "F7";
            case GLFW.GLFW_KEY_F8 -> "F8";
            case GLFW.GLFW_KEY_F9 -> "F9";
            case GLFW.GLFW_KEY_F10 -> "F10";
            case GLFW.GLFW_KEY_F11 -> "F11";
            case GLFW.GLFW_KEY_F12 -> "F12";
            default -> "UNKNOWN";
        };
    }
}
