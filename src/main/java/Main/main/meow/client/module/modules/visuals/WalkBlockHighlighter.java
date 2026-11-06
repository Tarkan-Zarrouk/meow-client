package Main.main.meow.client.module.modules.visuals;

import Main.main.meow.client.MeowClient;
import Main.main.meow.client.module.modules.ClientModule;
import Main.main.meow.client.ui.clickgui.model.Category;
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
    private static final ColorSetting BOX_COLOR = new ColorSetting("Box Color", "Customize the block color", RenderUtil2D.getVisualDefaultColor());

    public WalkBlockHighlighter() {
        super(NAME, CATEGORY, ENABLED, DESCRIPTION);
        super.addSetting(BOX_COLOR);
    }

    public static void draw(LevelRenderContext worldRenderContext) {
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
        RenderUtil3D.drawFilledBox(worldRenderContext, standingOn.getX(), standingOn.getY(), standingOn.getZ(), BOX_COLOR.color());
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
