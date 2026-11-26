package Main.main.meow.client.command.commands;

import Main.main.meow.client.command.Command;
import Main.main.meow.client.manager.NotificationManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.Locale;

public final class CopyCoordinatesCommand extends Command {
    private static final String NAME = "copycoordinates";
    private static final String DESCRIPTION = "Copies your current coordinates to your clipboard";
    public CopyCoordinatesCommand() {
        super(NAME, DESCRIPTION);
    }

    @Override
    public void execute(String... args) {
        if(Minecraft.getInstance().player == null) return;
        Vec3 playerPos = Minecraft.getInstance().player.position();
        String out = String.format(Locale.ROOT, "X: %.3f Y: %.3f Z: %.3f", playerPos.x, playerPos.y, playerPos.z);
        Minecraft.getInstance().keyboardHandler.setClipboard(out);
        NotificationManager.notify("Coordinates copied to clipboard!", false);
    }
}
