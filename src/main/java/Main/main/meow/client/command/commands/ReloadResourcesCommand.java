package Main.main.meow.client.command.commands;

import Main.main.meow.client.command.Command;
import net.minecraft.client.Minecraft;

public class ReloadResourcesCommand extends Command {
    private static final String NAME = "reload";
    private static final String DESCRIPTION = "reloads your textures (and subsequently your config system too)";
    public ReloadResourcesCommand() {
        super(NAME, DESCRIPTION);
    }

    @Override
    public void execute(String... args) {
        Minecraft.getInstance().execute(() -> Minecraft.getInstance().reloadResourcePacks().resultNow());
    }
}
