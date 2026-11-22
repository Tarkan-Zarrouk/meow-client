package Main.main.meow.client.command.commands;

import Main.main.meow.client.command.Command;
import Main.main.meow.client.manager.NotificationManager;
import net.minecraft.client.Minecraft;

import java.util.List;
import java.util.Map;

public class ClearChatCommand extends Command {
    public ClearChatCommand() {
        super("clearchat", "Clears your minecraft chat", Map.of("clearchat", List.of()));
    }

    @Override
    public void execute() {
        if(Minecraft.getInstance().level == null) return;
        Minecraft.getInstance().gui.getChat().clearMessages(true);
        NotificationManager.notify("Chat Cleared!", false);
    }
}
