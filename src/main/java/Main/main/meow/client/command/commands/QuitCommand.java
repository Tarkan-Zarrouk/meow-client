package Main.main.meow.client.command.commands;

import Main.main.meow.client.command.Command;
import net.minecraft.client.Minecraft;

public class QuitCommand extends Command {
    private static final String NAME = "quit";
    private static final String DESCRIPTION = "Quits the game";

    public QuitCommand() {
        super(NAME, DESCRIPTION);
    }

    @Override
    public void execute(String... args) {
        Minecraft.getInstance().stop();
    }
}
