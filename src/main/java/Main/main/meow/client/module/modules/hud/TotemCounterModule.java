package Main.main.meow.client.module.modules.hud;

import Main.main.meow.client.module.HudDisplayModule;
import Main.main.meow.client.module.modules.ClientModule;
import Main.main.meow.client.setting.modulesettings.BooleanSetting;
import Main.main.meow.client.setting.modulesettings.colorpicker.ColorSetting;
import Main.main.meow.client.ui.clickgui.Category;
import Main.main.meow.client.ui.hud.QolHudRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class TotemCounterModule extends HudDisplayModule {
    private static final BooleanSetting SHOW_BACKGROUND = new BooleanSetting("Show background", "Shows the background", true);
    private static final ColorSetting COLOR_OF_BACKGROUND = new ColorSetting("Color of Background", "Customize the colour of the background", QolHudRenderer.getPanel());
    public TotemCounterModule() {
        super("Totem Counter", Category.HUD, false, "Displays the number of Totems of Undying in your inventory.");
        addSetting(SHOW_BACKGROUND, COLOR_OF_BACKGROUND);
    }

    private static int totemCount(LocalPlayer player, Item item) {
        if (player == null) return 0;
        int count = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (player.getInventory().getItem(i).is(item)) {
                count += player.getInventory().getItem(i).getCount();
            }
        }
        return count;
    }

    public static void drawTotemCounter(GuiGraphicsExtractor g, Minecraft client, int x, int y, Item item, LocalPlayer player) {
        int count = totemCount(player, item);
        int textWidth = QolHudRenderer.getTextWidth(client, String.valueOf(count));
        int width = 22 + textWidth;
        int height = 18;
        if (SHOW_BACKGROUND.enabled()) {
            QolHudRenderer.panel(g, x, y, width, height, COLOR_OF_BACKGROUND.color());
        }
        g.item(new ItemStack(item), x + 1, y + 1);
        QolHudRenderer.text(g, client, String.valueOf(count), x + 19, y + 5, QolHudRenderer.getTEXT());
    }

    @Override
    public int panelWidth(Minecraft client) {
        if (client == null || client.player == null) {
            return 28;
        }
        int count = totemCount(client.player, Items.TOTEM_OF_UNDYING);
        int textWidth = QolHudRenderer.getTextWidth(client, String.valueOf(count));
        return 22 + textWidth;
    }

    @Override
    public int panelHeight(Minecraft client) {
        return 18;
    }
}
