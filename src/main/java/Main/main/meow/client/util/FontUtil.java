package Main.main.meow.client.util;

import Main.main.meow.client.module.modules.client.clickgui.ClickGuiModule;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public final class FontUtil {
    private static final FontDescription INTER_FONT = new FontDescription.Resource(Identifier.fromNamespaceAndPath("meow", "inter/inter"));
    private static final FontDescription BACKUP_FONT = new FontDescription.Resource(Identifier.withDefaultNamespace("uniform"));
    private static final FontDescription MONO_FONT = new FontDescription.Resource(Identifier.fromNamespaceAndPath("meow", "mono/mono"));
    private final Font font;

    public FontUtil(Font font) {
        this.font = font;
    }

    public static FontDescription getFont() {
        Main.main.meow.client.module.modules.client.clickgui.Font choice = ClickGuiModule.font();
        if (choice == Main.main.meow.client.module.modules.client.clickgui.Font.INTER) {
            return INTER_FONT;
        }
        if (choice == Main.main.meow.client.module.modules.client.clickgui.Font.BACKUP) {
            return BACKUP_FONT;
        }
        if(choice == Main.main.meow.client.module.modules.client.clickgui.Font.MONO) {
            return MONO_FONT;
        }
        return Style.EMPTY.getFont();
    }

    public Component styled(String value) {
        return Component.literal(value).withStyle(style -> style.withFont(getFont()));
    }

    public float textScale() {
        return ClickGuiModule.fontSize() / 11f;
    }

    public int textWidth(Component value) {
        return Math.round(font.width(value) * textScale());
    }

    public int textLineHeight() {
        return Math.round(font.lineHeight * textScale());
    }

    public String truncate(String value, int maxWidth) {
        if (textWidth(styled(value)) <= maxWidth) return value;
        String suffix = "..";
        StringBuilder result = new StringBuilder();
        for (char character : value.toCharArray()) {
            if (textWidth(styled(result.toString() + character + suffix)) > maxWidth)
                break;
            result.append(character);
        }
        return result + suffix;
    }

    public List<String> wrapTooltip(String description, int maxWidth) {
        List<String> lines = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (String word : description.split(" ")) {
            String candidate;
            if (current.isEmpty()) {
                candidate = word;
            } else {
                candidate = current + " " + word;
            }
            if (!current.isEmpty() && textWidth(styled(candidate)) > maxWidth) {
                lines.add(current.toString());
                current.setLength(0);
            }
            if(current.isEmpty()) {
                current.append(word);
            } else {
                current.append(" ").append(word);
            }
        }
        if (!current.isEmpty()) lines.add(current.toString());
        return lines;
    }

    public int centeredX(int x, int containerWidth, String value) {
        return x + Math.max(4, (containerWidth - textWidth(styled(value))) / 2);
    }

    public int centeredY(int y, int containerHeight) {
        return y + Math.max(0, (containerHeight - textLineHeight()) / 2);
    }
    public static float easeIn(float value) {
        return (float) Math.pow(value, 2);
    }


    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }
}
