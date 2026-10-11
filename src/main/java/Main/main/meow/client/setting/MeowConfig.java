package Main.main.meow.client.setting;

import Main.main.meow.client.ui.clickgui.settings.ClickGuiLayoutSettings;
import Main.main.meow.client.ui.hud.HudComponent;
import Main.main.meow.client.ui.hud.HudComponentManager;
import Main.main.meow.client.module.Module;
import Main.main.meow.manager.ModuleManager;
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
    // lowkenuienly ai is used here, i have no idea how to do this without it, i tried for a few hours across multiple different test projects and gave up, this is the only way i could figure out how to do it
    private static final Logger LOGGER = LoggerFactory.getLogger("meow-config");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "meow-config.json";

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
            JsonObject guiPositions = object(root, "clickGuiPositions");
            JsonObject guiExpanded = object(root, "clickGuiExpanded");
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
                JsonObject saved = object(object(root, "moduleSettings"), module.name());
                for (Setting<?> setting : module.settings()) {
                    JsonElement stored = saved.get(setting.name());
                    setting.fromJson(stored);
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
        }
        root.add("keybinds", keybinds);
        JsonObject moduleSettings = new JsonObject();
        for (Module module : modules.all()) {
            if (module.settings().isEmpty()) continue;
            JsonObject saved = new JsonObject();
            for (Setting<?> setting : module.settings()) {
                saved.add(setting.name(), setting.toJson());
            }
            moduleSettings.add(module.name(), saved);
        }
        root.add("moduleSettings", moduleSettings);
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
