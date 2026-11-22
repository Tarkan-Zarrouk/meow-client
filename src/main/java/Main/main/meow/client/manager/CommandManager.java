
package Main.main.meow.client.manager;

import Main.main.meow.client.command.Command;
import Main.main.meow.client.command.commands.ClearChatCommand;
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
        String commandName = commandInput.split("\\s+", 2)[0];
        Command command = getCommand(commandName);
        if (command == null && Minecraft.getInstance().player != null) {
            if (CommandModule.getNotificationMode().equals(NotifyModes.NOTIFICATION)) {
                NotificationManager.notify(Component.literal("[MeowClient] Invalid Command, please use *help for a list of commands!"), true);
            } else {
                Minecraft.getInstance().player.sendSystemMessage(Command.getErrorPrefixChat());
            }
            return;
        }
        if (command != null) {
            if (!commandInput.equals(commandName) || !command.isValid(command.getName())) {
                command.sendUsage();
                return;
            }
            command.execute();
        }
    }

    private void initializeCommands() {
        registerCommand(new HelpCommand(this));
        registerCommand(new ClearChatCommand());
    }
}
