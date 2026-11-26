package Main.main.meow.client.command.commands;

import Main.main.meow.client.command.Command;
import Main.main.meow.client.manager.NotificationManager;
import net.minecraft.client.Minecraft;

import static Main.main.meow.client.MeowClient.COMMAND_MANAGER;

public class NameMCCommand extends Command {
    private static final String NAME = "namemc";
    private static final String DESCRIPTION = "Opens the NameMC page of a player";
    public NameMCCommand() {
        super(NAME, DESCRIPTION);
    }

    @Override
    public void execute(String... args) {
        if (args.length != 1) {
            COMMAND_MANAGER.errorMessage("Invalid usage! Usage: " + COMMAND_MANAGER.getPrefix() + "namemc <player>");
            return;
        }
        String playerName = args[0];
        String url = "https://namemc.com/profile/" + playerName;
        Minecraft.getInstance().keyboardHandler.setClipboard(url);
        NotificationManager.notify("Successfully copied to clipboard", false);

    }
}
