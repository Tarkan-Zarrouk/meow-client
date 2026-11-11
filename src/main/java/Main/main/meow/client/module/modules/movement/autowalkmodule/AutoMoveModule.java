package Main.main.meow.client.module.modules.movement.autowalkmodule;

import Main.main.meow.client.module.modules.ClientModule;
import Main.main.meow.client.ui.clickgui.model.Category;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.EnumSetting;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.NumberSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

public class AutoMoveModule extends ClientModule {
    private static final String NAME = "AutoWalk";
    private static final Category CATEGORY = Category.MOVEMENT;
    private static final boolean ENABLED = false;
    private static final String DESCRIPTION = "this is overkill";
    private static final EnumSetting<MovementChoices> MOVEMENT_CHOICES_ENUM_SETTING = new EnumSetting<>("Movement Style", "This is overkill", MovementChoices.FORWARD);
    private static final NumberSetting CUSTOM_YAW = new NumberSetting("Custom Yaw", "Set a custom yaw for the CUSTOM movement choice", 0, -180, 180);
    private static final NumberSetting CUSTOM_PITCH = new NumberSetting("Custom Pitch", "Set a custom pitch for the CUSTOM movement choice", 0, -90, 90);

    public AutoMoveModule() {
        super(NAME,CATEGORY, ENABLED, DESCRIPTION);
        this.addSetting(MOVEMENT_CHOICES_ENUM_SETTING, CUSTOM_YAW, CUSTOM_PITCH);
    }

    public void tick(Minecraft client) {
        if (!super.getEnabled() || client.player == null || client.screen != null) {
            return;
        }
        switch(MOVEMENT_CHOICES_ENUM_SETTING.value()) {
            case FORWARD -> client.options.keyUp.setDown(true);
            case BACKWARD -> client.options.keyDown.setDown(true);
            case LEFT -> client.options.keyLeft.setDown(true);
            case RIGHT -> client.options.keyRight.setDown(true);
            // this is what I'm doing with my life btw
            case FORWARD_AND_JUMP -> {
                client.options.keyUp.setDown(true);
                client.options.keyJump.setDown(true);
            }
            case BACKWARD_AND_JUMP -> {
                client.options.keyDown.setDown(true);
                client.options.keyJump.setDown(true);
            }
            case LEFT_AND_JUMP -> {
                client.options.keyLeft.setDown(true);
                client.options.keyJump.setDown(true);
            }
            case RIGHT_AND_JUMP -> {
                client.options.keyRight.setDown(true);
                client.options.keyJump.setDown(true);
            }
            case FORWARD_AND_SNEAK -> {
                client.options.keyShift.setDown(true);
                client.options.keyUp.setDown(true);
            }
            case BACKWARD_AND_SNEAK -> {
                client.options.keyDown.setDown(true);
                client.options.keyShift.setDown(true);
            }
            case LEFT_AND_SNEAK -> {
                client.options.keyLeft.setDown(true);
                client.options.keyShift.setDown(true);
            }
            case RIGHT_AND_SNEAK -> {
                client.options.keyRight.setDown(true);
                client.options.keyShift.setDown(true);
            }
            case FORWARD_AND_SNEAK_AND_JUMP -> {
                client.options.keyUp.setDown(true);
                client.options.keyShift.setDown(true);
                client.options.keyJump.setDown(true);
            }
            case BACKWARD_AND_SNEAK_AND_JUMP -> {
                client.options.keyDown.setDown(true);
                client.options.keyShift.setDown(true);
                client.options.keyJump.setDown(true);
            }
            case LEFT_AND_SNEAK_AND_JUMP -> {
                client.options.keyLeft.setDown(true);
                client.options.keyShift.setDown(true);
                client.options.keyJump.setDown(true);
            }
            case RIGHT_AND_SNEAK_AND_JUMP -> {
                client.options.keyRight.setDown(true);
                client.options.keyShift.setDown(true);
                client.options.keyJump.setDown(true);
            }
            case FORWARD_AND_SPRINT -> {
                client.options.keyUp.setDown(true);
                client.player.setSprinting(true);
            }
            case BACKWARD_AND_SPRINT -> {
                client.options.keyDown.setDown(true);
                client.player.setSprinting(true);
            }
            case LEFT_AND_SPRINT -> {
                client.options.keyLeft.setDown(true);
                client.player.setSprinting(true);
            }
            case RIGHT_AND_SPRINT -> {
                client.options.keyRight.setDown(true);
                client.player.setSprinting(true);
            }
            case FORWARD_AND_SPRINT_AND_JUMP -> {
                client.options.keyUp.setDown(true);
                client.player.setSprinting(true);
                client.options.keyJump.setDown(true);
            }
            case BACKWARD_AND_SPRINT_AND_JUMP -> {
                client.options.keyDown.setDown(true);
                client.player.setSprinting(true);
                client.options.keyJump.setDown(true);
            }
            case LEFT_AND_SPRINT_AND_JUMP -> {
                client.options.keyLeft.setDown(true);
                client.player.setSprinting(true);
                client.options.keyJump.setDown(true);
            }
            case RIGHT_AND_SPRINT_AND_JUMP -> {
                client.options.keyRight.setDown(true);
                client.player.setSprinting(true);
                client.options.keyJump.setDown(true);
            }
            case FORWARD_AND_SPRINT_AND_SNEAK -> {
                client.options.keyUp.setDown(true);
                client.player.setSprinting(true);
                client.options.keyShift.setDown(true);
            }
            case BACKWARD_AND_SPRINT_AND_SNEAK -> {
                client.options.keyDown.setDown(true);
                client.player.setSprinting(true);
                client.options.keyShift.setDown(true);
            }
            case LEFT_AND_SPRINT_AND_SNEAK -> {
                client.options.keyLeft.setDown(true);
                client.player.setSprinting(true);
                client.options.keyShift.setDown(true);
            }
            case RIGHT_AND_SPRINT_AND_SNEAK -> {
                client.options.keyRight.setDown(true);
                client.player.setSprinting(true);
                client.options.keyShift.setDown(true);
            }
            case FORWARD_AND_SPRINT_AND_SNEAK_AND_JUMP -> {
                client.options.keyUp.setDown(true);
                client.player.setSprinting(true);
                client.options.keyShift.setDown(true);
                client.options.keyJump.setDown(true);
            }
            case BACKWARD_AND_SPRINT_AND_SNEAK_AND_JUMP -> {
                client.options.keyDown.setDown(true);
                client.player.setSprinting(true);
                client.options.keyShift.setDown(true);
                client.options.keyJump.setDown(true);
            }
            case LEFT_AND_SPRINT_AND_SNEAK_AND_JUMP -> {
                client.options.keyLeft.setDown(true);
                client.player.setSprinting(true);
                client.options.keyShift.setDown(true);
                client.options.keyJump.setDown(true);
            }
            case RIGHT_AND_SPRINT_AND_SNEAK_AND_JUMP -> {
                client.options.keyRight.setDown(true);
                client.player.setSprinting(true);
                client.options.keyShift.setDown(true);
                client.options.keyJump.setDown(true);
            }
            case CUSTOM -> {
                float pitchBounds = Mth.clamp(CUSTOM_PITCH.value(), -90.0f, 90.0f);

                client.player.setYRot(CUSTOM_YAW.value());
                client.player.setXRot(pitchBounds);

                // Sync head and previous tick rotations to prevent visual jitter
                client.player.yRotO = CUSTOM_YAW.value();
                client.player.xRotO = pitchBounds;
                client.player.setYHeadRot(CUSTOM_YAW.value());
            }
        }
    }
}
