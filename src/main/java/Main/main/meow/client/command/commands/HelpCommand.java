package Main.main.meow.client.command.commands;

import Main.main.meow.client.command.Command;
import Main.main.meow.client.manager.CommandManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Map;

public class HelpCommand extends Command {
    private final CommandManager commandManager;
    public HelpCommand(CommandManager commandManager) {
        super("help", "Displays available commands", Map.of("help", List.of()));
        this.commandManager = commandManager;
    }


    @Override
    public void execute() {
        Minecraft client = Minecraft.getInstance();
        if(client.player == null) {
            return;
        }
        client.player.sendSystemMessage(Component.literal("Meow Commands").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD));
        for (Command command : commandManager.getCommands()) {
            client.player.sendSystemMessage(Component.literal(command.getPrefix() + command.getName()).withStyle(ChatFormatting.WHITE).append(Component.literal(" - " + command.getDescription()).withStyle(ChatFormatting.GRAY)));
        }
    }
}