package Main.main.meow.client.command.commands;

import Main.main.meow.client.command.Command;
import Main.main.meow.client.manager.NotificationManager;
import net.minecraft.client.Minecraft;

public final class ClearChatCommand extends Command {
    private static final String NAME = "clearchat";
    private static final String DESCRIPTION = "Clears your minecraft chat";
    public ClearChatCommand() {
        super(NAME, DESCRIPTION);
    }

    @Override
    public void execute() {
        if(Minecraft.getInstance().level == null) return;
        Minecraft.getInstance().gui.getChat().clearMessages(true);
        NotificationManager.notify("Chat Cleared!", false);
    }
}
