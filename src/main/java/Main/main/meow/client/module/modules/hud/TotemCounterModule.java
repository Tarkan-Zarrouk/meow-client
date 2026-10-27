package Main.main.meow.client.module.modules.hud;

import Main.main.meow.client.module.HudDisplayModule;
import Main.main.meow.client.setting.modulesettings.BooleanSetting;
import Main.main.meow.client.setting.modulesettings.colorpicker.ColorSetting;
import Main.main.meow.client.ui.clickgui.Category;
import Main.main.meow.client.util.RenderUtil;
import Main.main.meow.client.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class TotemCounterModule extends HudDisplayModule {
    private static final BooleanSetting SHOW_BACKGROUND = new BooleanSetting("Show background", "Shows the background", true);
    private static final ColorSetting COLOR_OF_BACKGROUND = new ColorSetting("Color of Background", "Customize the colour of the background", RenderUtil.getGuiColorPanel());
    public TotemCounterModule() {
        super("Totem Counter", Category.HUD, false, "Displays the number of Totems of Undying in your inventory.");
        addSetting(SHOW_BACKGROUND, COLOR_OF_BACKGROUND);
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
        if (SHOW_BACKGROUND.enabled()) {
            RenderUtil.drawPanel(g, x, y, width, height, COLOR_OF_BACKGROUND.color());
        }
        g.item(new ItemStack(Items.TOTEM_OF_UNDYING), x + 1, y + 1);
        RenderUtil.drawText(g, client, String.valueOf(count), x + 19, y + 5, RenderUtil.getGuiTextColor());
    }

    public int getColor() {
        return COLOR_OF_BACKGROUND.color();
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
