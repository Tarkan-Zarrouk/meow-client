package Main.main.meow.mixin;

import Main.main.meow.client.MeowClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public abstract class InventoryMoverMixin {
    @Inject(method = "keyPressed", at = @At("HEAD"))
    private void meow$moveInInventory(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (!enabled() || !(Minecraft.getInstance().screen instanceof AbstractContainerScreen)) return;
        Minecraft client = Minecraft.getInstance();
        set(client.options.keyUp, event, true);
        set(client.options.keyLeft, event, true);
        set(client.options.keyDown, event, true);
        set(client.options.keyRight, event, true);
        set(client.options.keyJump, event, true);
        set(client.options.keyShift, event, true);
    }

    private static void set(net.minecraft.client.KeyMapping mapping, KeyEvent event, boolean down) {
        if (mapping.matches(event)) mapping.setDown(down);
    }

    private static boolean enabled() {
        return MeowClient.MODULE_MANAGER.byName("Inventory Mover") != null
                && MeowClient.MODULE_MANAGER.byName("Inventory Mover").isEnabled();
    }
}
