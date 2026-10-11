package Main.main.meow.client.module.modules;

import Main.main.meow.client.module.Module;
import Main.main.meow.client.ui.clickgui.Category;

public class ClientModule extends Module {
    public ClientModule(String name, Category category, boolean enabled) {
        this(name, category, enabled, "");
    }

    public ClientModule(String name, Category category, boolean enabled, String description) {
        super(name, category, description);
        setEnabled(enabled);
    }
}
