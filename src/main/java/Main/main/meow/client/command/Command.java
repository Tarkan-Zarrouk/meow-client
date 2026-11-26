package Main.main.meow.client.command;

import Main.main.meow.client.module.modules.client.commandmodule.CommandModule;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public abstract class Command {
    private static String prefix = "*";
    private final String name;
    private final String description;

    public Command(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public abstract void execute(String... args);

    public static MutableComponent getErrorPrefixChat() {
        ChatFormatting prefixColor = CommandModule.getPrefixColor().getFormatting();
        ChatFormatting prefixFormatting = CommandModule.getPrefixFormatting().getFormatting();
        Component styledPrefix = Component.literal("[MeowClient]: ").withStyle(prefixColor, prefixFormatting);
        Component messagePrefix = Component.literal("Invalid Command, please use *help for a list of commands!");
        return Component.empty().append(styledPrefix).append(messagePrefix);
    }

    public String getName() {
        return this.name;
    }
    public String getDescription() {
        return this.description;
    }
    public String getPrefix() {
        return this.prefix;
    }
}
