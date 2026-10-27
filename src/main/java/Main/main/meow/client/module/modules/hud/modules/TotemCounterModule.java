package Main.main.meow.client.module.modules.hud.modules;

import Main.main.meow.client.module.modules.hud.HudDisplayModule;
import Main.main.meow.client.module.modules.hud.annotation.RequireHudModuleFields;
import Main.main.meow.client.setting.modulesettings.colorpicker.ColorSetting;
import Main.main.meow.client.ui.clickgui.Category;
import Main.main.meow.client.util.RenderUtil;
import Main.main.meow.client.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@RequireHudModuleFields
public final class TotemCounterModule extends HudDisplayModule {
    private static final ColorSetting BACKGROUND_COLOR = new ColorSetting("Color of Background", "Customize the colour of the background", RenderUtil.getGuiColorPanel());
    private static final ColorSetting TEXT_COLOR = new ColorSetting("Text Color", "Customize the colour of the text", RenderUtil.getGuiTextColor());
    public TotemCounterModule() {
        super("Totem Counter", Category.HUD, false, "Displays the number of Totems of Undying in your inventory.");
        super.addSetting(BACKGROUND_COLOR, TEXT_COLOR);
    }

    private static int totemCount(LocalPlayer player) {
        if (player == null) return 0;
        int count = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (player.getInventory().getItem(i).is(Items.TOTEM_OF_UNDYING)) {
                count += player.getInventory().getItem(i).getCount();
            }
        }
        return count;
    }

    @Override
    public void drawInfo(GuiGraphicsExtractor g, Minecraft client, LocalPlayer player, int x, int y) {
        int count = totemCount(player);
        int textWidth = TextUtil.getTextWidth(client, String.valueOf(count));
        int width = 22 + textWidth;
        int height = 18;
        RenderUtil.drawPanel(g, x, y, width, height, BACKGROUND_COLOR.color());
        g.item(new ItemStack(Items.TOTEM_OF_UNDYING), x + 1, y + 1);
        RenderUtil.drawText(g, client, String.valueOf(count), x + 19, y + 5, TEXT_COLOR.color());
    }

    @Override
    public int panelWidth(Minecraft client) {
        if (client == null || client.player == null) {
            return 28;
        }
        int count = totemCount(client.player);
        int textWidth = TextUtil.getTextWidth(client, String.valueOf(count));
        return 22 + textWidth;
    }

    @Override
    public int panelHeight(Minecraft client) {
        return 18;
    }
}
