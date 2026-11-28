package Main.main.meow.mixin;

import Main.main.meow.client.module.modules.visuals.CameraTools;
import Main.main.meow.client.module.modules.visuals.ZoomModule;
import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.waypoints.TrackedWaypoint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public abstract class CameraMixin implements TrackedWaypoint.Camera {
    @Shadow
    protected abstract void setPosition(Vec3 position);

    @Inject(method = "calculateFov", at = @At("RETURN"), cancellable = true)
    private void meow$applyZoom(CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(ZoomModule.apply(cir.getReturnValueF()));
    }
    @Inject(method = "getMaxZoom(F)F", at = @At("HEAD"), cancellable = true)
    private void meow$getMaxZoom(float cameraDist, CallbackInfoReturnable<Float> cir) {
        if(CameraTools.getToggleCameraClip()) {
            cir.setReturnValue(CameraTools.getCameraClipDistance());
        }
    }
    @Inject(method = "setPosition(DDD)V", at = @At("HEAD"), cancellable = true)
    private void meow$setPosition(double x, double y, double z, CallbackInfo cir) {
        this.setPosition(new Vec3(
                x + CameraTools.getCameraXPos(),
                y + CameraTools.getCameraYPos(),
                z + CameraTools.getCameraZPos()
        ));
        cir.cancel();
    }
}
