package Main.main.meow.client.module.modules.visuals.raycastentityhitboxrenderer;

import Main.main.meow.client.module.modules.ClientModule;
import Main.main.meow.client.ui.clickgui.model.Category;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.EnumSetting;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.NumberSetting;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.colorpicker.ColorSetting;
import Main.main.meow.client.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;

public class RayCastEntityHitBoxRenderer extends ClientModule {
    private static final String NAME = "RayCastEntityHitBoxRenderer";
    private static final Category CATEGORY = Category.VISUALS;
    private static final boolean ENABLED = false;
    private static final String DESCRIPTION = "Renders the hitboxes of entities that are being raycasted";
    private static final EnumSetting<Style> STYLE_ENUM_SETTING = new EnumSetting<>("Style", "Outline, outline and fill, outline", Style.OUTLINE);
    private static final ColorSetting STROKE_COLOR = new ColorSetting("Stroke Color", "Color of the outline", RenderUtil.getVisualDefaultColor());
    private static final NumberSetting STROKE_WIDTH = new NumberSetting("Stroke Width", "Width of the outline", 1, 1, 10);
    private static final ColorSetting FILL_COLOR = new ColorSetting("Fill Color", "Color of the filled box", RenderUtil.getVisualDefaultColor());

    public RayCastEntityHitBoxRenderer() {
        super(NAME, CATEGORY, ENABLED, DESCRIPTION);
        super.addSetting(
                STROKE_COLOR, STROKE_WIDTH, FILL_COLOR,
                STYLE_ENUM_SETTING
        );
    }

    public void rayCastEntityHitBoxRenderer() throws InterruptedException {
        Minecraft client = Minecraft.getInstance();
        if (!super.getEnabled() || client.player == null) {
            return;
        }
        if(client.hitResult instanceof EntityHitResult hitResult) {
            AABB entityBoundingBox = hitResult.getEntity().getBoundingBox();
            switch(STYLE_ENUM_SETTING.value()) {
                case Style.OUTLINE -> Gizmos.cuboid(entityBoundingBox, GizmoStyle.stroke(STROKE_COLOR.color(), STROKE_WIDTH.value()));
                case Style.FILL -> Gizmos.cuboid(entityBoundingBox, GizmoStyle.fill(FILL_COLOR.color()));
                case Style.FILL_AND_OUTLINE -> Gizmos.cuboid(entityBoundingBox, GizmoStyle.strokeAndFill(STROKE_COLOR.color(), STROKE_WIDTH.value(), FILL_COLOR.color()));
            }
        }
    }
}
