package Main.main.meow.client.module.modules.hud.modules;

import Main.main.meow.client.module.modules.hud.HudDisplayModule;
import Main.main.meow.client.module.modules.hud.annotation.RequireHudModuleFields;
import Main.main.meow.client.setting.modulesettings.colorpicker.ColorSetting;
import Main.main.meow.client.ui.clickgui.Category;
import Main.main.meow.client.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

@RequireHudModuleFields
public class InventoryViewer extends HudDisplayModule {
    private static final ColorSetting BACKGROUND_COLOR = new ColorSetting("Background Color", "Change the color of the background", RenderUtil.getGuiColorPanel());
    private static final ColorSetting TEXT_COLOR = new ColorSetting("Text Color", "Change the color of the text", RenderUtil.getGuiTextColor());
    public InventoryViewer() {
        super("Inventory Viewer", Category.HUD, false, "Shows the content of your inventory updating in real time");
        addSetting(BACKGROUND_COLOR, TEXT_COLOR);
    }

    @Override
    public void drawInfo(GuiGraphicsExtractor g, Minecraft client, LocalPlayer player, int x, int y) {
        Map<ItemStack, Integer> playerInventoryStorage = new HashMap<>(); // <ItemStack, SlotPos>
        Inventory playerInventory = player.getInventory();
        for(int i = 9; i < 36; i++) {
            ItemStack itemStack = playerInventory.getItem(i);
            if(itemStack.isEmpty()) {
                playerInventoryStorage.put(null, i);
            } else {
                playerInventoryStorage.put(itemStack, i);
            }
        }
        RenderUtil.drawPanel(g, x, y, 162, 54, BACKGROUND_COLOR.color());
        for(ItemStack item : playerInventoryStorage.keySet()) {
            int column = playerInventoryStorage.get(item) % 9;
            int row = playerInventoryStorage.get(item) / 9 - 1;
            if(item != null) {
                g.item(item, x + (column * 18), y + (row * 18));
                RenderUtil.drawText(g, client, Integer.toString(playerInventory.getItem(playerInventoryStorage.get(item)).getCount()), x + (column * 18) + 7, y + (row * 18) + 7, TEXT_COLOR.color());
            }
        }
    }

    @Override
    public int panelWidth(Minecraft client) {
        return 162;
    }

    @Override
    public int panelHeight(Minecraft client) {
        return 54;
    }
}
