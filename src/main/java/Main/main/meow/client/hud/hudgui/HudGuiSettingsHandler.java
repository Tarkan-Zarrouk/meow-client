package Main.main.meow.client.hud.hudgui;

import Main.main.meow.client.MeowClient;
import Main.main.meow.client.clickgui.ColorPicker;
import Main.main.meow.client.clickgui.settings.ClickGuiLayoutSettings;
import Main.main.meow.client.clickgui.settings.ClickGuiSettings;
import Main.main.meow.client.modules.Module;
import Main.main.meow.client.modules.ModuleManager;
import Main.main.meow.client.modules.client.ZoomModule;
import Main.main.meow.client.modules.hud.NotificationsModule;
import Main.main.meow.client.settings.MeowConfig;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class HudGuiSettingsHandler {
    public static final int FONT_SIZE_MIN = 5, FONT_SIZE_RANGE = 15;

    private final ModuleManager modules;
    private final Set<Module> expanded = new HashSet<>();
    private Module awaitingKeybind;
    private ZoomModule awaitingZoomKeybind;
    private ColorPicker colorPicker;
    private boolean pickingCategoryColor;

    public HudGuiSettingsHandler(ModuleManager modules) {
        this.modules = modules;
    }

    public void loadExpanded() { // to be called after modules are loaded, since the expanded list is stored by module name
        expanded.clear();
        for (Module module : modules.all()) {
            if (ClickGuiLayoutSettings.isExpanded(module.name())) {
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
        awaitingZoomKeybind = null;
        save();
    }

    public void save() {
        Set<String> expandedNames = new HashSet<>();
        for (Module module : expanded)
            expandedNames.add(module.name());
        ClickGuiLayoutSettings.setExpandedModules(expandedNames);
        MeowConfig.save(Minecraft.getInstance(), modules, MeowClient.HUD_COMPONENTS);
    }

    public ColorPicker colorPicker() {
        return colorPicker;
    }

    public void closeColorPicker() {
        save();
        colorPicker = null;
    }

    private void openColorPicker(boolean category) {
        pickingCategoryColor = category;
        if (category) {
            colorPicker = new ColorPicker(260, 190, ClickGuiSettings.categoryColor(), color -> {
                ClickGuiSettings.setCategoryColor(color);
                save();
            });
        } else {
            colorPicker = new ColorPicker(260, 190, ClickGuiSettings.moduleColor(), color -> {
                ClickGuiSettings.setModuleColor(color);
                save();
            });
        }
    }

    public boolean isFontSizeSetting(Module module, int setting) {
        return isClickGui(module) && setting == 4;
    }

    public void setFontSizeFromMouse(double mouseX, int columnStart, int columnWidth) {
        double sliderStart = columnStart + 8;
        double sliderWidth = Math.max(1, columnWidth - 16);
        double progress = (mouseX - sliderStart) / sliderWidth;
        int size = FONT_SIZE_MIN + (int) Math.round(Math.max(0d, Math.min(1d, progress)) * FONT_SIZE_RANGE); // ide says math.clamp but no ty :3
        ClickGuiSettings.setFontSize(size);
    }

    // handles every setting click except the font size slider, which needs drag state
    public void clickSetting(Module module, int setting) {
        /*
         * 0: keybind
         * 1: zoom keybind / category color / notifications type
         * 2: module color / notifications timer
         * 3: font toggle
         * 4: font size slider
         */
        if (setting == 0) {
            awaitingZoomKeybind = null;
            awaitingKeybind = module;
        } else if (module instanceof ZoomModule zoom && setting == 1) { // zom keybind bc that module is special :3 (i couldn't figure out a better way)
            awaitingKeybind = null;
            awaitingZoomKeybind = zoom;
        } else if (isClickGui(module) && setting == 1) {
            openColorPicker(true);
        } else if (isClickGui(module) && setting == 2) {
            openColorPicker(false);
        } else if (isClickGui(module) && setting == 3) {
            ClickGuiSettings.setOdinFont(!ClickGuiSettings.odinFont()); // odin is a skyblock mod btw, but i like the name so i'm keeping it :3
            save();
        } else if (module instanceof NotificationsModule notifications && setting == 1) {
            if (notifications.mode() == NotificationsModule.Mode.CHAT) {
                notifications.setMode(NotificationsModule.Mode.ALERT);
            } else {
                notifications.setMode(NotificationsModule.Mode.CHAT);
            }
            save();
        } else if (module instanceof NotificationsModule notifications && setting == 2) {
            notifications.setShowTimer(!notifications.showTimer());
            save();
        }
    }

    // returns true when a pending keybind assignment consumed the key
    public boolean captureKey(int key) {
        int bound = key == GLFW.GLFW_KEY_DELETE ? GLFW.GLFW_KEY_UNKNOWN : key;
        boolean cancelled = key == GLFW.GLFW_KEY_ESCAPE;
        if (awaitingZoomKeybind != null) {
            if (!cancelled)
                awaitingZoomKeybind.setZoomKeybind(bound);
            awaitingZoomKeybind = null;
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
        String key;
        if (awaitingKeybind == module) {
            key = "Press a key...";
        } else {
            key = "Keybind: " + keyName(module.keybind());
        }
        if (module instanceof ZoomModule zoom) {
            String zoomKey;
            if (awaitingZoomKeybind == zoom) {
                zoomKey = "Press a key...";
            } else {
                zoomKey = "Zoom key: " + keyName(zoom.zoomKeybind());
            }
            return List.of(key, zoomKey);
        }
        if (module instanceof NotificationsModule notifications) {
            String type;
            if (notifications.mode() == NotificationsModule.Mode.CHAT) {
                type = "Chat";
            } else {
                type = "Alert";
            }
            String timer;
            if (notifications.showTimer()) {
                timer = "Show Timer";
            } else {
                timer = "Hide Timer";
            }
            return List.of(key, "Type: " + type, timer);
        }
        if (isClickGui(module)) {
            String font;
            if (ClickGuiSettings.odinFont()) {
                font = "Inter";
            } else {
                font = "Minecraft";
            }
            return List.of(key,
                    "Category colour: " + HudGuiStyler.hex(ClickGuiSettings.categoryColor()),
                    "Module colour: " + HudGuiStyler.hex(ClickGuiSettings.moduleColor()),
                    "Font: " + font,
                    "Font size: " + Math.round(ClickGuiSettings.fontSize()));
        }
        return List.of(key);
    }

    public String settingDescription(Module module, int setting) {
        if (setting == 0) {
            return "Right-click this setting, then press a key to toggle the module. Press Delete to clear the keybind.";
        }
        if (module instanceof ZoomModule && setting == 1) {
            return "Hold this key to smoothly zoom to a 30 degree field of view. Press Delete to clear it.";
        }
        if (module instanceof NotificationsModule && setting == 1) {
            return "Switches between pink [MeowMod] chat messages and animated top-right alerts.";
        }
        if (module instanceof NotificationsModule && setting == 2) {
            return "Shows or hides the magenta timer bar that drains until the alert is cleared.";
        }
        if (isClickGui(module) && setting == 1) {
            return "Opens a picker that changes the color of every category header in real time.";
        }
        if (isClickGui(module) && setting == 2) {
            return "Opens a picker that changes the color of every module row in real time.";
        }
        if (isClickGui(module) && setting == 3) {
            return "Switches the ClickGUI between the custom Inter font and Minecraft's default font.";
        }
        if (isClickGui(module) && setting == 4) {
            return "Drag across this slider to change the active font size in real time.";
        }
        return "";
    }

    private static boolean isClickGui(Module module) {
        return module.name().equalsIgnoreCase("ClickGUI");
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
