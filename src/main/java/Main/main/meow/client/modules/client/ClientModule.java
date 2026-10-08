package Main.main.meow.client.modules.client;

import Main.main.meow.client.modules.Module;
import Main.main.meow.client.modules.category.Category;

public class ClientModule extends Module {
    public ClientModule(String name, Category category, boolean enabled) {
        this(name, category, enabled, "");
    }

    public ClientModule(String name, Category category, boolean enabled, String description) {
        super(name, category, description);
        setEnabled(enabled);
    }
}
