package Main.main.meow.mixin;

import Main.main.meow.client.modules.client.ZoomController;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Inject(method = "calculateFov", at = @At("RETURN"), cancellable = true)
    private void meow$applyZoom(CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(ZoomController.apply(cir.getReturnValueF()));
    }
}
