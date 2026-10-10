package Main.main.meow.client.modules.movement;

import Main.main.meow.client.modules.client.ClientModule;
import Main.main.meow.client.modules.category.Category;
import net.minecraft.client.Minecraft;

/**
 * Automatically holds vanilla forward sprinting while the player moves forward.
 * Strafing alone never activates sprinting, so this is intentionally not omni-sprint.
 */
public final class SprintModule extends ClientModule {
    public SprintModule() {
        super("Sprint", Category.PLAYER, false, "Automatically sprints when moving forward without requiring the sprint key.");
    }

    public void tick(Minecraft client) {
        if (client.player == null || client.screen != null) {
            return;
        }
        if (client.options.keyUp.isDown() && !client.options.keyDown.isDown() && !client.options.keyShift.isDown() && !client.player.isPassenger()) {
            client.player.setSprinting(true);
        }
    }
}
