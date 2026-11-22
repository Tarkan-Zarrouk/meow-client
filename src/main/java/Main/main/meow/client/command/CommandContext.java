package Main.main.meow.client.command;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.level.Level;

public class CommandContext {
    private Minecraft client;
    private AbstractClientPlayer player;
    private Level world;
    private CommandSource commandSource;
    private String[] arguments;
    private String rawInput;

    public CommandContext(Minecraft client, AbstractClientPlayer player, Level world, CommandSource commandSource, String[] arguments, String rawInput) {
        this.client = client;
        this.player = player;
        this.world = world;
        this.commandSource = commandSource;
        this.arguments = arguments;
        this.rawInput = rawInput;
    }

    public Minecraft getClient() {
        return client;
    }

    public AbstractClientPlayer getPlayer() {
        return player;
    }

    public Level getWorld() {
        return world;
    }

    public CommandSource getCommandSource() {
        return commandSource;
    }

    public String[] getArguments() {
        return arguments;
    }

    public String getRawInput() {
        return rawInput;
    }
}
