package Main.main.meow.client.module.modules.visuals.raycastblockcustomizer;

import Main.main.meow.client.module.modules.ClientModule;
import Main.main.meow.client.ui.clickgui.model.Category;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.EnumSetting;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.NumberSetting;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.colorpicker.ColorSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

public class RayCastBlockCustomizer extends ClientModule {
    private static final String NAME = "RaycastBlockCustomizer";
    private static final Category CATEGORY = Category.VISUALS;
    private static final boolean ENABLED = false;
    private static final String DESCRIPTION = "Swap between filled, outline, and more whenever you look at an object!";
    private static final EnumSetting<Modes> RAYCAST_RENDER_MODES = new EnumSetting<>("Mode", "Change between outline, stroke and outline, stroke, or none!", Modes.OUTLINE);
    private static final NumberSetting OUTLINE_RENDER_WIDTH = new NumberSetting("Outline Width", "Change the thickness", 4, 1,10);
    private static final ColorSetting RENDER_COLOR = new ColorSetting("Outline Color", "Customize how it looks", 0xFF00FFFF);
    private static final ColorSetting FILL_COLOR = new ColorSetting("Fill Color", "Customize how it looks", 0xFF00FFFF);
    public RayCastBlockCustomizer() {
        super(NAME, CATEGORY, ENABLED, DESCRIPTION);
        super.addSetting(
                RAYCAST_RENDER_MODES,
                OUTLINE_RENDER_WIDTH,
                RENDER_COLOR, FILL_COLOR
        );
    }
    public static Modes getRayCastRenderMode() {
        return RAYCAST_RENDER_MODES.value();
    }

    public void rayCastBlockRenderer() {
        Minecraft client = Minecraft.getInstance();
        if (!super.getEnabled() || client.player == null) {
            return;
        }
        if(client.hitResult instanceof BlockHitResult blockHitResult && client.level != null && !client.level.getBlockState(blockHitResult.getBlockPos()).isAir()) {
            AABB box = new AABB(blockHitResult.getBlockPos());
            switch(RAYCAST_RENDER_MODES.value()) {
                case Modes.OUTLINE -> Gizmos.cuboid(box, GizmoStyle.stroke(RENDER_COLOR.color(), OUTLINE_RENDER_WIDTH.value()));
                case Modes.FILL -> Gizmos.cuboid(box, GizmoStyle.fill(RENDER_COLOR.color()));
            }
        }
    }
}
