package Main.main.meow.client.module.modules.visuals;

import Main.main.meow.client.module.modules.ClientModule;
import Main.main.meow.client.ui.clickgui.model.Category;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.BooleanSetting;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.NumberSetting;

public class CameraTools extends ClientModule {
    private static final String NAME = "CameraTools";
    private static final String DESCRIPTION = "Camera modifications";
    private static final boolean ENABLED = false;
    private static final Category CATEGORY = Category.VISUALS;
    private static final BooleanSetting TOGGLE_CAMERA_CLIP = new BooleanSetting("Camera Clip", "Clips through blocks", false);
    private static final NumberSetting CAMERA_CLIP_DISTANCE = new NumberSetting("Camera Clip Distance", "Distance to clip through blocks", 4, 1, 10);
    private static final NumberSetting CAMERA_X_POS = new NumberSetting("Camera X Position", "X position of the camera", 0, -10, 10);
    private static final NumberSetting CAMERA_Y_POS = new NumberSetting("Camera Y Position", "Y position of the camera", 0, -10, 10);
    private static final NumberSetting CAMERA_Z_POS = new NumberSetting("Camera Z Position", "Z position of the camera", 0, -10, 10);
    public CameraTools() {
        super(NAME, CATEGORY, ENABLED, DESCRIPTION);
        this.addSetting(TOGGLE_CAMERA_CLIP, CAMERA_CLIP_DISTANCE, CAMERA_X_POS, CAMERA_Y_POS, CAMERA_Z_POS);
    }
    // see MixinCamera
    public static boolean getToggleCameraClip() {
        return TOGGLE_CAMERA_CLIP.value();
    }
    public static float getCameraClipDistance() {
        return CAMERA_CLIP_DISTANCE.value();
    }
    public static float getCameraXPos() {
        return CAMERA_X_POS.value();
    }
    public static float getCameraYPos() {
        return CAMERA_Y_POS.value();
    }
    public static float getCameraZPos() {
        return CAMERA_Z_POS.value();
    }
}
