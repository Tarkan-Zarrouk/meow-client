package Main.main.meow.client.module.modules.client.commandmodule.modes.prefix;

import net.minecraft.ChatFormatting;

public enum PrefixFormatting {
    OBFUSCATED(ChatFormatting.OBFUSCATED),
    BOLD(ChatFormatting.BOLD),
    STRIKETHROUGH(ChatFormatting.STRIKETHROUGH),
    UNDERLINE(ChatFormatting.UNDERLINE),
    ITALIC(ChatFormatting.ITALIC),
    RESET(ChatFormatting.RESET);

    private final ChatFormatting formatting;

    PrefixFormatting(ChatFormatting formatting) {
        this.formatting = formatting;
    }

    public ChatFormatting getFormatting() {
        return formatting;
    }
}