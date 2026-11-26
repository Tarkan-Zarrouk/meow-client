
package Main.main.meow.client.manager;

import Main.main.meow.client.command.Command;
import Main.main.meow.client.command.commands.*;
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
    private String input;

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
        if (command == null || getCommand(command.getName()) != null) return false;
        commands.add(command);
        return true;
    }

    public void setInput(String newInput) {
        this.input = newInput;
    }

    public String getInput() {
        return this.input;
    }


    public Command getCommand(String name) {
        for (Command command : commands) {
            if (command.getName().equalsIgnoreCase(name)) return command;
        }
        return null;
    }

    public void executeCommand(String input) {
        if (input == null || !input.startsWith(prefix)) return;
        this.input = input;
        String commandAndArguments = input.substring(prefix.length()).stripLeading();
        if (commandAndArguments.isEmpty()) return;
        String[] parts = commandAndArguments.split("\\s+");
        Command command = getCommand(parts[0]);
        if (command == null) {
            errorMessage("Unknown command: " + parts[0]);
            return;
        }
        String[] args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, args.length);
        command.execute(args);
    }

    public void errorMessage(String message) {
        if (Minecraft.getInstance().player != null) {
            if (CommandModule.getNotificationMode().equals(NotifyModes.NOTIFICATION)) {
                NotificationManager.notify(Component.literal("[MeowClient] " + message), true);
            } else {
                Minecraft.getInstance().player.sendSystemMessage(Command.getErrorPrefixChat());
            }
        }
    }

    private void initializeCommands() {
        this.registerCommand(new HelpCommand(this));
        this.registerCommand(new ClearChatCommand());
        this.registerCommand(new DisconnectCommand());
        this.registerCommand(new CopyCoordinatesCommand());
        this.registerCommand(new CopyServerIpCommand());
        this.registerCommand(new ReloadResourcesCommand());
        this.registerCommand(new NameMCCommand());
        this.registerCommand(new QuitCommand());
        this.registerCommand(new ToggleCommand());
    }
}
