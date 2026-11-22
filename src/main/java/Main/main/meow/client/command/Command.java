package Main.main.meow.client.command;

import Main.main.meow.client.manager.NotificationManager;
import Main.main.meow.client.module.modules.client.commandmodule.CommandModule;
import Main.main.meow.client.module.modules.client.commandmodule.modes.NotifyModes;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringRepresentable;

import java.util.List;
import java.util.Map;

public abstract class Command {
    private static String prefix = "*";
    private final String name;
    private final String description;
    /**
     * "command", [addons]
     */
    private final Map<String, List<String>> usage;

    public Command(String name, String description, Map<String, List<String>> usage) {
        this.name = name;
        this.description = description;
        this.usage = usage;
    }

    public abstract void execute();

    public static void setPrefix(String newPrefix) {
        prefix = newPrefix;
    }

    public static MutableComponent getErrorPrefixChat() {
        ChatFormatting prefixColor = CommandModule.getPrefixColor().getFormatting();
        ChatFormatting prefixFormatting = CommandModule.getPrefixFormatting().getFormatting();
        Component styledPrefix = Component.literal("[MeowClient]: ").withStyle(prefixColor, prefixFormatting);
        Component messagePrefix = Component.literal("Invalid Command, please use *help for a list of commands!");
        return Component.empty().append(styledPrefix).append(messagePrefix);
    }

    public boolean isValid(String input) {
        for (Map.Entry<String, List<String>> entry : this.usage.entrySet()) {
            String command = entry.getKey();
            List<String> expectedArgs = entry.getValue();
            AbstractClientPlayer player = Minecraft.getInstance().player;
            if (!input.equals(command) && !input.startsWith(command + " ") && player != null) {
                continue;
            }
            String remaining = input.substring(command.length()).trim();
            String[] inputArgs;
            if (remaining.isEmpty()) inputArgs = new String[0];
            else inputArgs = remaining.split("\\s+");
            return expectedArgs.size() == inputArgs.length;
        }
        return false;
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
    public void sendUsage() {
        Minecraft client = Minecraft.getInstance();

        if (client.player == null) {
            return;
        }
        client.player.sendSystemMessage(Component.literal(getName() + " - " + getDescription()).withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD));
        for (Map.Entry<String, List<String>> entry : usage.entrySet()) {
            String command = entry.getKey();
            List<String> arguments = entry.getValue();

            String syntax = prefix + command;

            if (!arguments.isEmpty()) {
                syntax += " " + String.join(" ", arguments);
            }

            client.player.sendSystemMessage(Component.literal(syntax).withStyle(ChatFormatting.GRAY));
        }
    }
}
