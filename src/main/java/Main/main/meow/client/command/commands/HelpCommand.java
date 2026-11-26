package Main.main.meow.client.command.commands;

import Main.main.meow.client.MeowClient;
import Main.main.meow.client.command.Command;
import Main.main.meow.client.manager.CommandManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Array;
import java.util.Arrays;

import static Main.main.meow.client.MeowClient.COMMAND_MANAGER;

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
        if(args.length == 1) {
            try {
                client.player.sendSystemMessage(Component.literal("Meow Commands").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD));
                Minecraft.getInstance().player.sendSystemMessage(Component.literal(MeowClient.getModuleManager().byName(args[0]).getDescription()));
            } catch (IllegalArgumentException e) {
                COMMAND_MANAGER.errorMessage("Invalid module name! Usage: " + COMMAND_MANAGER.getPrefix() + "help <module>");
            }
        } else if(args.length == 0) {
            client.player.sendSystemMessage(Component.literal("Meow Commands").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD));
            for (Command command : commandManager.getCommands()) {
                client.player.sendSystemMessage(Component.literal(command.getPrefix() + command.getName()).withStyle(ChatFormatting.WHITE).append(Component.literal(" - " + command.getDescription()).withStyle(ChatFormatting.GRAY)));
            }
        }

//        Minecraft.getInstance().player.sendSystemMessage(Component.literal(Arrays.toString(args)));

    }
}