package Main.main.meow.client.command.commands;

import Main.main.meow.client.command.Command;
import Main.main.meow.client.manager.CommandManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public final class HelpCommand extends Command {
    private final CommandManager commandManager;
    private static final String NAME = "help";
    private static final String DESCRIPTION = "Displays available commands";
    public HelpCommand(CommandManager commandManager) {
        super(NAME, DESCRIPTION);
        this.commandManager = commandManager;
    }


    @Override
    public void execute(String... args) {
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