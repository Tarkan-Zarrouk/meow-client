package Main.main.meow.client.module.modules.visuals.walkblockhighlighter;

import Main.main.meow.client.MeowClient;
import Main.main.meow.client.module.modules.ClientModule;
import Main.main.meow.client.ui.clickgui.model.Category;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.NumberSetting;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.colorpicker.ColorSetting;
import Main.main.meow.client.util.ColorUtil;
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
    private static final ColorSetting CUBOID_COLOR = new ColorSetting("Cuboid Color", "Color of cuboid", RenderUtil2D.getVisualDefaultColor());
    private static final NumberSetting MIN_X_CUBOID_POS = new NumberSetting("Min X Position", "Change the minimum x position", 0, 1, 100);
    private static final NumberSetting MAX_X_CUBOID_POS = new NumberSetting("Max X Position", "Change the maximum x position", 0, 1, 100);
    private static final NumberSetting MIN_Y_CUBOID_POS = new NumberSetting("Min Y Position", "Change the Minimum Height", 99, 1, 99);
    private static final NumberSetting MAX_Y_CUBOID_POS = new NumberSetting("Max Y Position", "Change the maximum height", 99, 1, 99);
    private static final NumberSetting MIN_Z_CUBOID_POS = new NumberSetting("Min Z Position", "Change the minimum depth", 99,1,99);
    private static final NumberSetting MAX_Z_CUBOID_POS = new NumberSetting("Max Z Position", "Change the maximum depth", 99, 1, 99);
    public WalkBlockHighlighter() {
        super(NAME, CATEGORY, ENABLED, DESCRIPTION);
        super.addSetting( CUBOID_COLOR, MIN_X_CUBOID_POS, MAX_X_CUBOID_POS, MIN_Y_CUBOID_POS, MAX_Y_CUBOID_POS, MIN_Z_CUBOID_POS, MAX_Z_CUBOID_POS);
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
        int filledColor = CUBOID_COLOR.color();
        float filledColorR = ColorUtil.getRed(filledColor);
        float filledColorG = ColorUtil.getGreen(filledColor);
        float filledColorB = ColorUtil.getBlue(filledColor);
        float filledColorA = ColorUtil.getAlpha(filledColor);
        RenderUtil3D.drawCuboid(levelRenderContext, standingOn.getX(), standingOn.getY(), standingOn.getZ(), MIN_X_CUBOID_POS.value(), MAX_X_CUBOID_POS.value(), MIN_Y_CUBOID_POS.value(), MAX_Y_CUBOID_POS.value(), MIN_Z_CUBOID_POS.value(), MAX_Z_CUBOID_POS.value(), filledColorR, filledColorG, filledColorB, filledColorA);
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
