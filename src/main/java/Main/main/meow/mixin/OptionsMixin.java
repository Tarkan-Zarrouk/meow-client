package Main.main.meow.mixin;

import Main.main.meow.client.module.modules.client.clickgui.ClickGuiModule;
import net.minecraft.client.Options;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Options.class)
public class OptionsMixin {

    @Inject(method = "Lnet/minecraft/client/Options;getMenuBackgroundBlurriness()I", cancellable = true, at = @At("TAIL"))
    private void setCustomBlurValue(CallbackInfoReturnable<Integer> ci) {
        ci.setReturnValue(ClickGuiModule.getBackgroundBlurIntensity());
    }
}
