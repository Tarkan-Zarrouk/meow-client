package Main.main.meow.client.modules.misc;

import Main.main.meow.client.modules.client.ClientModule;
import Main.main.meow.client.modules.category.Category;

public final class WaypointsModule extends ClientModule {
    public WaypointsModule() { super("Waypoints", Category.MISC, false, "Reserved for marking and displaying useful locations in the world."); }
}
