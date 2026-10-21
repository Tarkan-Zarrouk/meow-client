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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class ArrowCounterModule extends HudDisplayModule {
    private static final BooleanSetting SHOW_BACKGROUND = new BooleanSetting("Show background", "Draws background", true);
    private static final ColorSetting BACKGROUND_COLOR = new ColorSetting("Background Color", "Custom background color", RenderUtil.getGuiColorPanel());
    public ArrowCounterModule() {
        super("Arrow Counter", Category.HUD, false, "Displays the number of arrows in your inventory.");
        addSetting(SHOW_BACKGROUND, BACKGROUND_COLOR);
    }
    public static int getItem(LocalPlayer player, Item item) {
        int count = 0;
        for(int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if(player.getInventory().getItem(i).is(item)) {
                count += player.getInventory().getItem(i).getCount();
            }
        }
        return count;
    }

    public static void drawArrowCounter(GuiGraphicsExtractor g, Minecraft client, int x, int y, Item item, LocalPlayer player) {
        int count = getItem(player, item);
        int textWidth = TextUtil.getTextWidth(client, String.valueOf(count));
        int width = 22 + textWidth;
        int height = 18;
        if(SHOW_BACKGROUND.enabled()) {
            RenderUtil.drawPanel(g, x, y, width, height, BACKGROUND_COLOR.color());
        }
        g.item(new ItemStack(item), x + 1, y + 1);
        RenderUtil.drawText(g, client, String.valueOf(count), x + 19, y + 5, RenderUtil.getGuiTextColor());
    }

    @Override
    public int panelWidth(Minecraft client) {
        if (client == null || client.player == null)
            return 28;
        int count = getItem(client.player, Items.ARROW);
        int textWidth = TextUtil.getTextWidth(client, String.valueOf(count));
        return 22 + textWidth;
    }

    @Override
    public int panelHeight(Minecraft client) {
        return 18;
    }
}
