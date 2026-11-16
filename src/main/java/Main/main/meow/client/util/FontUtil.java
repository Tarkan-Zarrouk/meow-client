package Main.main.meow.client.util;

import Main.main.meow.client.module.modules.client.clickgui.ClickGuiModule;
import Main.main.meow.mixin.FontAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GlyphSource;
import net.minecraft.client.gui.font.glyphs.EffectGlyph;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

public final class FontUtil {
    private static final FontDescription INTER_FONT = new FontDescription.Resource(Identifier.fromNamespaceAndPath("meow", "inter/inter"));
    private static final FontDescription BACKUP_FONT = new FontDescription.Resource(Identifier.withDefaultNamespace("uniform"));
    private static final FontDescription MONO_FONT = new FontDescription.Resource(Identifier.fromNamespaceAndPath("meow", "mono/mono"));

    /**
     * Specific font; inter, mono, or gnu default universal font
     * @return Data Object to render components with
     */
    public static FontDescription getFontDescription() {
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

    /**
     * Converts from FontDescription data object to a regular Font generic
     * @return Minecraft specific font
     */
    public static Font getFont() {
        Font.Provider provider = ((FontAccessor) Minecraft.getInstance().font).meow$getProvider();
        return new Font(new Font.Provider() {
            @Override
            public GlyphSource glyphs(FontDescription description) {
                return provider.glyphs(getFontDescription());
            }

            @Override
            public EffectGlyph effect() {
                return provider.effect();
            }
        });
    }
}
