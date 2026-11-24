
package Main.main.meow.client.manager;

import Main.main.meow.client.command.Command;
import Main.main.meow.client.command.commands.ClearChatCommand;
import Main.main.meow.client.command.commands.CopyCoordinatesCommand;
import Main.main.meow.client.command.commands.DisconnectCommand;
import Main.main.meow.client.command.commands.HelpCommand;
import Main.main.meow.client.module.modules.client.commandmodule.CommandModule;
import Main.main.meow.client.module.modules.client.commandmodule.modes.NotifyModes;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandManager {
    private final List<Command> commands;
    private final String prefix;

    public CommandManager(String prefix) {
        this.commands = new ArrayList<>();
        this.prefix = prefix;
        initializeCommands();
    }

    public List<Command> getCommands() {
        return Collections.unmodifiableList(commands);
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean registerCommand(Command command) {
        if (command == null || getCommand(command.getName()) != null) {
            return false;
        }

        commands.add(command);
        return true;
    }


    public Command getCommand(String name) {
        for (Command command : commands) {
            if (command.getName().equalsIgnoreCase(name)) {
                return command;
            }
        }

        return null;
    }

    public void executeCommand(String input) {
        if (input == null || !input.startsWith(prefix)) {
            return;
        }
        String commandInput = input.substring(prefix.length());
        if (commandInput.isEmpty()) {
            return;
        }
        Command command = getCommand(commandInput);
        if (command == null && Minecraft.getInstance().player != null) {
            if (CommandModule.getNotificationMode().equals(NotifyModes.NOTIFICATION)) {
                NotificationManager.notify(Component.literal("[MeowClient] Invalid Command, please use *help for a list of commands!"), true);
            } else {
                Minecraft.getInstance().player.sendSystemMessage(Command.getErrorPrefixChat());
            }
            return;
        }
        if (command != null) {
            command.execute();
        }
    }

    private void initializeCommands() {
        this.registerCommand(new HelpCommand(this));
        this.registerCommand(new ClearChatCommand());
        this.registerCommand(new DisconnectCommand());
        this.registerCommand(new CopyCoordinatesCommand());
    }
}
