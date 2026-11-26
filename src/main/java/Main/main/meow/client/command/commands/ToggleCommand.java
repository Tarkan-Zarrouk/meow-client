package Main.main.meow.client.command.commands;

import Main.main.meow.client.MeowClient;
import Main.main.meow.client.command.Command;
import Main.main.meow.client.manager.CommandManager;

import static Main.main.meow.client.MeowClient.COMMAND_MANAGER;

public class ToggleCommand extends Command {
    private static final String NAME = "toggle";
    private static final String DESCRIPTION = "Toggles a module on or off";
    public ToggleCommand() {
        super(NAME, DESCRIPTION);
    }

    @Override
    public void execute(String... args) {
        if(args.length < 1) {
            COMMAND_MANAGER.errorMessage("Invalid usage! Usage: " + COMMAND_MANAGER.getPrefix() + "toggle <module>");
            return;
        }
        String moduleName = args[0];
        try {
            MeowClient.getModuleManager().byName(moduleName);
        } catch (IllegalArgumentException e) { // ty comp 1406 for teaching me custom error implementations 
            COMMAND_MANAGER.errorMessage("Module " + moduleName + " does not exist!");
            return;
        }
        MeowClient.getModuleManager().byName(moduleName).toggle();
    }
}
