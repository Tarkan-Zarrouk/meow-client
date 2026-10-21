package Main.main.meow.client.module.modules.player;

import Main.main.meow.client.module.HudDisplayModule;
import Main.main.meow.client.ui.clickgui.Category;
import Main.main.meow.client.setting.modulesettings.BooleanSetting;
import Main.main.meow.client.setting.modulesettings.colorpicker.ColorSetting;
import Main.main.meow.client.setting.modulesettings.EnumSetting;
import Main.main.meow.client.util.RenderUtil;
import Main.main.meow.client.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public final class ArmorStatusModule extends HudDisplayModule {
    private enum Modes {
        HORIZONTAL,
        VERTICAL
    }
    private static final EnumSetting<Modes> ORIENTATION = new EnumSetting<>("Orientation", "Change the orientation from horizontal to vertical", Modes.HORIZONTAL);
    private static final BooleanSetting SHOW_BACKGROUND = new BooleanSetting("Show background", "Show the background or not", true);
    private static final ColorSetting COLOR_OF_BACKGROUND = new ColorSetting("Background Color", "Change the background of the colour", RenderUtil.getGuiColorPanel());
    public ArmorStatusModule() {
        super("Armor Status", Category.PLAYER, true, "Shows your equipped armor and its remaining durability on the HUD.");
        addSetting(ORIENTATION, SHOW_BACKGROUND, COLOR_OF_BACKGROUND);
    }

    // equipped pieces ordered helmet, chestplate, leggings, boots; empty slots are skipped
    private static List<ItemStack> equippedPieces(LocalPlayer player) {
        List<ItemStack> pieces = new ArrayList<>();
        for (int slot = 39; slot >= 36; slot--) {
            ItemStack piece = player.getInventory().getItem(slot);
            if (!piece.isEmpty()) {
                pieces.add(piece);
            }
        }
        return pieces;
    }

    // slot width is item (16px) + gap + text + padding, so horizontal slots never overlap
    private static int slotWidth(Minecraft client, ItemStack piece) {
        return 18 + TextUtil.getTextWidth(client, (piece + "%") + 4);
    }

    public static void drawInfo(GuiGraphicsExtractor g, Minecraft client, LocalPlayer player, int x, int y, int color) {
        int offset = 0;
        for (ItemStack piece : equippedPieces(player)) {
            int slotX = x;
            int slotY = y;
            if (ORIENTATION.value() == Modes.VERTICAL) {
                slotY += offset;
            } else {
                slotX += offset;
            }
            int width = slotWidth(client, piece);
            if (SHOW_BACKGROUND.enabled()) {
                RenderUtil.drawPanel(g, slotX, slotY, width, 18, COLOR_OF_BACKGROUND.color());
            }
            g.item(piece, slotX + 1, slotY + 1);
            RenderUtil.drawText(g, client, piece.getDisplayName().getString().substring(1, piece.getDisplayName().getString().length() - 1) + " " + String.valueOf(armourPercentageCalc(piece)) + "%", slotX + 19, slotY + 5, RenderUtil.getGuiTextColor());
            if (ORIENTATION.value() == Modes.VERTICAL) {
                offset += 18;
            } else {
                offset += width;
            }
        }
    }

    public int getColor() {
        return COLOR_OF_BACKGROUND.color();
    }

    private static float armourPercentageCalc(ItemStack armourPiece) {
        if (armourPiece.isEmpty() || armourPiece.getMaxDamage() <= 0) {
            return 100f;
        }
        float durability = armourPiece.getMaxDamage() - armourPiece.getDamageValue();
        return (durability / armourPiece.getMaxDamage()) * 100f;
    }

    @Override
    public int panelWidth(Minecraft client) {
        if (client.player == null) {
            return 0;
        }
        int width = 0;
        for (ItemStack piece : equippedPieces(client.player)) {
            if (ORIENTATION.value() == Modes.VERTICAL) {
                width = Math.max(width, slotWidth(client, piece));
            } else {
                width += slotWidth(client, piece);
            }
        }
        return width;
    }

    @Override
    public int panelHeight(Minecraft client) {
        if (client.player == null) {
            return 0;
        }
        int count = equippedPieces(client.player).size();
        if (ORIENTATION.value() == Modes.VERTICAL) {
            return count * 18;
        }
        return Math.min(count, 1) * 18;
    }
}
