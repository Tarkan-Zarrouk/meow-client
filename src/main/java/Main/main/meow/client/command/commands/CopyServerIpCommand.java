package Main.main.meow.client.command.commands;

import Main.main.meow.client.command.Command;
import Main.main.meow.client.manager.NotificationManager;
import net.minecraft.client.Minecraft;

public class CopyServerIpCommand extends Command {
    private static final String NAME = "copyip";
    private static final String DESCRIPTION = "Copies the server ip you're currently in!";
    public CopyServerIpCommand() {
        super(NAME, DESCRIPTION);
    }

    @Override
    public void execute() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        if (mc.hasSingleplayerServer()) {NotificationManager.notify("You're in single player right now.", true); return;}
        if(Minecraft.getInstance().getCurrentServer() == null) return;
        mc.keyboardHandler.setClipboard(Minecraft.getInstance().getCurrentServer().ip);
        NotificationManager.notify("Copied IP to clipboard!", false);

    }
}
