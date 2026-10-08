package Main.main.meow.client.settings;

import Main.main.meow.client.clickgui.settings.ClickGuiLayoutSettings;
import Main.main.meow.client.clickgui.settings.ClickGuiSettings;
import Main.main.meow.client.MeowClient;
import Main.main.meow.client.hud.HudComponent;
import Main.main.meow.client.hud.HudComponentManager;
import Main.main.meow.client.modules.Module;
import Main.main.meow.client.modules.ModuleManager;
import Main.main.meow.client.modules.client.ZoomModule;
import Main.main.meow.client.modules.hud.NotificationsModule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class MeowConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("meow-config");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "meow-config.json";

    private MeowConfig() {
    }

    public static void load(Minecraft client, ModuleManager modules, HudComponentManager components) {
        Path path = path(client);
        if (!Files.exists(path)) {
            return;
        }
        try {
            JsonObject root = GSON.fromJson(Files.readString(path, StandardCharsets.UTF_8), JsonObject.class);
            if (root == null) {
                return;
            }
            JsonObject enabled = object(root, "enabledModules");
            JsonObject keybinds = object(root, "keybinds");
            JsonObject colors = object(root, "clickGuiColors");
            JsonObject guiPositions = object(root, "clickGuiPositions");
            JsonObject guiExpanded = object(root, "clickGuiExpanded");
            if (colors.has("categories") && colors.has("modules")) {
                int category = colors.get("categories").getAsInt();
                int module = colors.get("modules").getAsInt();
                // Older configs stored small positive palette indexes instead of ARGB colors.
                // Do not inspect only the RGB portion: valid dark ARGB colors can have a
                // low RGB value, including black.
                if (category >= 0 && category < 0x100) {
                    category = 0xffc52dbd;
                }
                if (module >= 0 && module < 0x100) {
                    module = 0xffc52dbd;
                }
                ClickGuiSettings.setCategoryColor(category);
                ClickGuiSettings.setModuleColor(module);
            }
            if (colors.has("odinFont") && colors.get("odinFont").isJsonPrimitive()
                    && colors.getAsJsonPrimitive("odinFont").isBoolean()) {
                ClickGuiSettings.setOdinFont(colors.get("odinFont").getAsBoolean());
            }
            if (colors.has("fontSize") && colors.get("fontSize").isJsonPrimitive()
                    && colors.getAsJsonPrimitive("fontSize").isNumber()) {
                ClickGuiSettings.setFontSize(colors.get("fontSize").getAsInt());
            }
            for (Map.Entry<String, JsonElement> entry : guiPositions.entrySet()) {
                if (!entry.getValue().isJsonObject()) continue;
                JsonObject position = entry.getValue().getAsJsonObject();
                if (position.has("x") && position.has("y")) {
                    ClickGuiLayoutSettings.setPosition(entry.getKey(),
                            position.get("x").getAsInt(), position.get("y").getAsInt());
                }
            }
            Set<String> expandedModules = new HashSet<>();
            for (Map.Entry<String, JsonElement> expandedEntry : guiExpanded.entrySet()) {
                if (expandedEntry.getValue().isJsonPrimitive()
                        && expandedEntry.getValue().getAsJsonPrimitive().isBoolean()
                        && expandedEntry.getValue().getAsBoolean()) {
                    expandedModules.add(expandedEntry.getKey());
                }
            }
            ClickGuiLayoutSettings.setExpandedModules(expandedModules);
            for (Module module : modules.all()) {
                JsonElement value = enabled.get(module.name());
                if (!module.name().equalsIgnoreCase("ClickGUI")
                        && value != null && value.isJsonPrimitive()
                        && value.getAsJsonPrimitive().isBoolean()) {
                    module.setEnabled(value.getAsBoolean());
                }
                JsonElement keybind = keybinds.get(module.name());
                if (keybind != null && keybind.isJsonPrimitive() && keybind.getAsJsonPrimitive().isNumber()) {
                    module.setKeybind(keybind.getAsInt());
                }
                if (module instanceof ZoomModule zoom) {
                    JsonElement zoomKeybind = keybinds.get("Zoom (hold)");
                    if (zoomKeybind != null && zoomKeybind.isJsonPrimitive()
                            && zoomKeybind.getAsJsonPrimitive().isNumber()) {
                        zoom.setZoomKeybind(zoomKeybind.getAsInt());
                    }
                }
                if (module instanceof NotificationsModule notifications) {
                    JsonElement mode = root.get("notificationMode");
                    if (mode != null && mode.isJsonPrimitive()) {
                        try {
                            notifications.setMode(NotificationsModule.Mode.valueOf(mode.getAsString()));
                        } catch (IllegalArgumentException ignored) {
                            notifications.setMode(NotificationsModule.Mode.CHAT);
                        }
                    }
                    JsonElement showTimer = root.get("notificationShowTimer");
                    if (showTimer != null && showTimer.isJsonPrimitive()
                            && showTimer.getAsJsonPrimitive().isBoolean()) {
                        notifications.setShowTimer(showTimer.getAsBoolean());
                    }
                }
            }

            JsonObject positions = object(root, "hudPositions");
            for (HudComponent component : components.all()) {
                JsonObject position = object(positions, component.id());
                if (position.has("x") && position.has("y")) {
                    component.setPosition(position.get("x").getAsInt(), position.get("y").getAsInt());
                }
            }
        } catch (IOException | JsonParseException | IllegalStateException exception) {
            LOGGER.warn("Unable to load Meow configuration from {}", path, exception);
        }
    }

    public static void save(Minecraft client, ModuleManager modules, HudComponentManager components) {
        Path path = path(client);
        JsonObject root = new JsonObject();
        JsonObject enabled = new JsonObject();
        for (Module module : modules.all()) {
            enabled.addProperty(module.name(), module.isEnabled());
        }
        root.add("enabledModules", enabled);
        JsonObject keybinds = new JsonObject();
        for (Module module : modules.all()) {
            keybinds.addProperty(module.name(), module.keybind());
            if (module instanceof ZoomModule zoom) {
                keybinds.addProperty("Zoom (hold)", zoom.zoomKeybind());
            }
        }
        root.add("keybinds", keybinds);
        NotificationsModule notifications = (NotificationsModule) modules.byName("Notifications");
        if (notifications != null) {
            root.addProperty("notificationMode", notifications.mode().name());
            root.addProperty("notificationShowTimer", notifications.showTimer());
        }
        JsonObject colors = new JsonObject();
        colors.addProperty("categories", ClickGuiSettings.categoryColorValue());
        colors.addProperty("modules", ClickGuiSettings.moduleColorValue());
        colors.addProperty("odinFont", ClickGuiSettings.odinFont());
        colors.addProperty("fontSize", ClickGuiSettings.fontSize());
        root.add("clickGuiColors", colors);
        JsonObject guiPositions = new JsonObject();
        for (Map.Entry<String, ClickGuiLayoutSettings.Position> entry
                : ClickGuiLayoutSettings.allPositions().entrySet()) {
            JsonObject position = new JsonObject();
            position.addProperty("x", entry.getValue().x());
            position.addProperty("y", entry.getValue().y());
            guiPositions.add(entry.getKey(), position);
        }
        root.add("clickGuiPositions", guiPositions);
        JsonObject guiExpanded = new JsonObject();
        for (String module : ClickGuiLayoutSettings.expandedModules()) {
            guiExpanded.addProperty(module, true);
        }
        root.add("clickGuiExpanded", guiExpanded);

        JsonObject positions = new JsonObject();
        for (HudComponent component : components.all()) {
            JsonObject position = new JsonObject();
            position.addProperty("x", component.x());
            position.addProperty("y", component.y());
            positions.add(component.id(), position);
        }
        root.add("hudPositions", positions);

        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, GSON.toJson(root), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            LOGGER.warn("Unable to save Meow configuration to {}", path, exception);
        }
    }

    private static JsonObject object(JsonObject parent, String key) {
        JsonElement value = parent.get(key);
        return value != null && value.isJsonObject() ? value.getAsJsonObject() : new JsonObject();
    }

    private static Path path(Minecraft client) {
        return client.gameDirectory.toPath().resolve("config").resolve(FILE_NAME);
    }
}
