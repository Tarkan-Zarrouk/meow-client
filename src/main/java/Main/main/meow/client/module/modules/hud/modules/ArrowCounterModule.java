package Main.main.meow.client.module.modules.hud.modules;

import Main.main.meow.client.module.modules.hud.HudDisplayModule;
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


public final class ArrowCounterModule extends HudDisplayModule {
    private static final BooleanSetting SHOW_BACKGROUND = new BooleanSetting("Show Background", "Toggle the background of your panel", true);
    private static final ColorSetting BACKGROUND_COLOR = new ColorSetting("Background Color", "Custom background color", RenderUtil.getGuiColorPanel());
    private static final ColorSetting TEXT_COLOR = new ColorSetting("Text Color", "Custom text color", RenderUtil.getGuiTextColor());
    public ArrowCounterModule() {
        super("Arrow Counter", Category.HUD, false, "Displays the number of arrows in your inventory.");
        super.addSetting(SHOW_BACKGROUND, BACKGROUND_COLOR, TEXT_COLOR);
    }
    private static int getItem(LocalPlayer player) {
        int count = 0;
        for(int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if(player.getInventory().getItem(i).is(Items.ARROW)) {
                count += player.getInventory().getItem(i).getCount();
            }
        }
        return count;
    }
    @Override
    public void drawInfo(GuiGraphicsExtractor g, Minecraft client, LocalPlayer player, int x, int y) {
        int count = getItem(player);
        int textWidth = TextUtil.getTextWidth(client, String.valueOf(count));
        int width = 22 + textWidth;
        int height = 18;
        if (SHOW_BACKGROUND.value()) {
            RenderUtil.drawPanel(g, x, y, width, height, BACKGROUND_COLOR.color());
        }
        g.item(new ItemStack(Items.ARROW), x + 1, y + 1);
        RenderUtil.drawText(g, client, String.valueOf(count), x + 19, y + 5, TEXT_COLOR.color());
    }

    @Override
    public int panelWidth(Minecraft client) {
        if (client == null || client.player == null)
            return 28; // there's no magic number here, i just spent a few minutes adjusting the size, that is all.
        int count = getItem(client.player);
        int textWidth = TextUtil.getTextWidth(client, String.valueOf(count));
        return 22 + textWidth;
    }

    @Override
    public int panelHeight(Minecraft client) {
        return 18; // read comment above lol
    }
}
