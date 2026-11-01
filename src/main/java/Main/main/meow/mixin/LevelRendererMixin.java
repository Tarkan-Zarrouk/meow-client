package Main.main.meow.mixin;

import Main.main.meow.client.MeowClient;
import Main.main.meow.client.module.Module;
import Main.main.meow.client.module.modules.visuals.RayCastBlockCustomizer.Modes;
import Main.main.meow.client.module.modules.visuals.RayCastBlockCustomizer.RayCastBlockCustomizer;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Inject(method = "Lnet/minecraft/client/renderer/LevelRenderer;renderBlockOutline(Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lcom/mojang/blaze3d/vertex/PoseStack;ZLnet/minecraft/client/renderer/state/level/LevelRenderState;)V", at = @At("HEAD"), cancellable = true)
    private void meow$renderBlockOutline(CallbackInfo ci) {
        Module rayCastBlockCustomizer = MeowClient.getModuleManager().byName("RaycastBlockCustomizer");
        if (rayCastBlockCustomizer instanceof RayCastBlockCustomizer && (rayCastBlockCustomizer.getEnabled() || RayCastBlockCustomizer.getRayCastRenderMode() == Modes.NONE)) {
            ci.cancel();
            ((RayCastBlockCustomizer) rayCastBlockCustomizer).rayCastBlockRenderer();
        }
    }
}
