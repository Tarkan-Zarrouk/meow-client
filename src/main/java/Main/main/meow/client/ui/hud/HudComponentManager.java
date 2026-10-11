package Main.main.meow.client.ui.hud;

import Main.main.meow.client.setting.MeowConfig;
import Main.main.meow.client.manager.ModuleManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class HudComponentManager {
    private final List<HudComponent> components = new ArrayList<>();

    public void add(HudComponent component) {
        components.add(component);
    }

    public List<HudComponent> all() {
        return Collections.unmodifiableList(components);
    }

    public HudComponent at(double x, double y) {
        for (int i = components.size() - 1; i >= 0; i--) {
            if (components.get(i).contains(x, y))
                return components.get(i);
        }
        return null;
    }

    public void save(Minecraft client, ModuleManager modules) {
        MeowConfig.save(client, modules, this);
    }

    public void render(GuiGraphicsExtractor graphics, Minecraft client, boolean editor) {
        for (HudComponent component : components)
            component.render(graphics, client, editor);
    }
}
