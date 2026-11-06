package Main.main.meow.client.module.modules.visuals.walkblockhighlighter;

import Main.main.meow.client.MeowClient;
import Main.main.meow.client.module.modules.ClientModule;
import Main.main.meow.client.ui.clickgui.model.Category;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.EnumSetting;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.colorpicker.ColorSetting;
import Main.main.meow.client.util.RenderUtil2D;
import Main.main.meow.client.util.RenderUtil3D;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;

public class WalkBlockHighlighter extends ClientModule {
    private static final String NAME = "WalkBlockHighlighter";
    private static final Category CATEGORY = Category.VISUALS;
    private static final boolean ENABLED = false;
    private static final String DESCRIPTION = "Highlights the block you're currently standing on";
    private static final EnumSetting<BlockHighlightMode> BLOCK_MODE = new EnumSetting<>("Highlight Mode", "Swap between a slab and a filled render", BlockHighlightMode.SLAB);
    private static final ColorSetting SLAB_COLOR = new ColorSetting("Box Color", "Customize the block color", RenderUtil2D.getVisualDefaultColor());
    private static final ColorSetting FILLED_COLOR = new ColorSetting("Filled Color", "Customize the filled color", RenderUtil2D.getVisualDefaultColor());

    public WalkBlockHighlighter() {
        super(NAME, CATEGORY, ENABLED, DESCRIPTION);
        super.addSetting(BLOCK_MODE, SLAB_COLOR, FILLED_COLOR);
    }

    public static void draw(LevelRenderContext levelRenderContext) {
        if (!(MeowClient.getModuleManager().byName(NAME) instanceof WalkBlockHighlighter module) || !module.getEnabled()) {
            return;
        }
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null) {
            return;
        }
        BlockPos standingOn = findStandingBlock(client.level, client.player);
        if (standingOn == null) {
            return;
        }
        switch(BLOCK_MODE.value()) {
            case SLAB -> RenderUtil3D.drawSlab(levelRenderContext, standingOn.getX(), standingOn.getY(), standingOn.getZ(), SLAB_COLOR.color());
            case FILLED -> RenderUtil3D.drawSlab(levelRenderContext, standingOn.getX(), standingOn.getY(), standingOn.getZ(), FILLED_COLOR.color());

        }
    }

    private static BlockPos findStandingBlock(Level level, Player player) {
        BlockPos.MutableBlockPos pos = player.blockPosition().mutable();
        for (int i = 0; i < 64 && pos.getY() >= level.getMinY(); i++) {
            if (!level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()) {
                return pos.immutable();
            }
            pos.move(Direction.DOWN);
        }
        return null;
    }
}
