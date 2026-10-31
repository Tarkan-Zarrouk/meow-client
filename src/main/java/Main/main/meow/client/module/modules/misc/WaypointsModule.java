package Main.main.meow.client.module.modules.misc;

import Main.main.meow.client.module.modules.ClientModule;
import Main.main.meow.client.ui.clickgui.model.Category;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public final class WaypointsModule extends ClientModule {
    private static final String NAME = "Waypoints";
    private static final Category CATEGORY = Category.MISC;
    private static final boolean ENABLED = false;
    private static final String DESCRIPTION = "Marking and displaying useful locations in the world";
    public WaypointsModule() {
        super(NAME, CATEGORY, ENABLED, DESCRIPTION);
    }

}
