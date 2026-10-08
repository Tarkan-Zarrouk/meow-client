package Main.main.meow.mixin;

import Main.main.meow.client.MeowClient;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {
    @Inject(method = "onButton", at = @At("HEAD"))
    private void meow$recordClick(long window, MouseButtonInfo button, int action, CallbackInfo ci) {
        if (button.button() == 0 && action == 1) MeowClient.recordClick();
    }
}
