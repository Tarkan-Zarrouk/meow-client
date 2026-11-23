package Main.main.meow.client.command.commands;

import Main.main.meow.client.command.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class DisconnectCommand extends Command {
    private static final String NAME = "disconnect";
    private static final String DESCRIPTION = "disconnects from your current server";
    public DisconnectCommand() {
        super(NAME, DESCRIPTION);
    }

    @Override
    public void execute() {
        if(Minecraft.getInstance().level == null) return;
        Minecraft.getInstance().level.disconnect(Component.literal("Disconnected via command!"));
    }
}
