package Main.main.meow.client.module.modules.hud.modules;

import Main.main.meow.client.module.modules.hud.HudDisplayModule;
import Main.main.meow.client.ui.clickgui.model.Category;
import Main.main.meow.client.ui.clickgui.view.ClickGuiStyler;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.BooleanSetting;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.NumberSetting;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.colorpicker.ColorSetting;
import Main.main.meow.client.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;

public class WhatAmILookingAt extends HudDisplayModule {
    private static final String NAME = "WhatAmILookingAt";
    private static final Category CATEGORY = Category.HUD;
    private static final boolean ENABLED = false;
    private int panelWidth;
    private int panelHeight;
    private static final String DESCRIPTION = "Identifies information about the non-living object you're looking at";
    private static final NumberSetting HEIGHT_OF_PANEL = new NumberSetting("Panel Height", "Change the height of the panel", 40, 40, 55);
    private static final NumberSetting RADIUS_OF_PANEL = new NumberSetting("Panel Radius", "Change the roundedness of the borders", 0, 0, 4);
    private static final NumberSetting RADIUS_OF_OUTLINE = new NumberSetting("Outline of Block Outline", "Change the Roundedness for block outline", 4, 0, 4);
    private static final ColorSetting COLOR_OF_TEXT_NAME = new ColorSetting("Block Name Color", "Change the color of the block name", RenderUtil.getGuiTextColor());
    private static final ColorSetting COLOR_OF_TEXT_ID = new ColorSetting("Block ID Color", "Change the color of the block id", ClickGuiStyler.MUTED);
    private static final BooleanSetting SHOW_BLOCK_OUTLINE = new BooleanSetting("Show Block Outline", "Toggle on / off the outline", true);
    private static final ColorSetting COLOR_OF_BLOCK_OUTLINE = new ColorSetting("Color of Block Outline", "Color change of the block outline", RenderUtil.getGuiTextColor());
    private static final BooleanSetting SHOW_PANEL_OUTLINE = new BooleanSetting("Show Panel Outline", "Toggle on / off the outline", true);
    private static final ColorSetting COLOR_OF_PANEL_OUTLINE = new ColorSetting("Color of Panel Outline", "Color change of the Panel outline", RenderUtil.getGuiTextColor());
    private static final ColorSetting COLOR_OF_PANEL = new ColorSetting("Color of the Panel", "Color of the panel", RenderUtil.getGuiColorPanel());

    public WhatAmILookingAt() {
        super(NAME, CATEGORY, ENABLED, DESCRIPTION);
        super.addSetting(
                HEIGHT_OF_PANEL,
                RADIUS_OF_PANEL,
                COLOR_OF_TEXT_ID, COLOR_OF_TEXT_NAME,
                SHOW_BLOCK_OUTLINE, SHOW_PANEL_OUTLINE,
                COLOR_OF_BLOCK_OUTLINE, COLOR_OF_PANEL_OUTLINE, COLOR_OF_PANEL
        );
    }

    @Override
    public void drawInfo(GuiGraphicsExtractor graphics, Minecraft client, LocalPlayer player, int x, int y) {
        Block block;
        BlockHitResult blockHitResult = client.hitResult instanceof BlockHitResult hit ? hit : null;
        if (blockHitResult != null && client.level != null) {
            block = client.level.getBlockState(blockHitResult.getBlockPos()).getBlock();
            if(!block.getName().getString().equals("Air")) {
                String blockName = block.getName().getString();
                String blockId = block.getDescriptionId();
                int yPosObj = (HEIGHT_OF_PANEL.value() / 2 )- 4;
                panelWidth = blockId.length() * 7;
                panelHeight = HEIGHT_OF_PANEL.value();
                RenderUtil.roundedFill(graphics, x, y, panelWidth, panelHeight, COLOR_OF_PANEL.color() ,true, true, true, true, RADIUS_OF_PANEL.value());
                if(SHOW_BLOCK_OUTLINE.value()) {
                    RenderUtil.drawRoundedOutline(graphics, x + 4, y + yPosObj - 5, 18, 18, COLOR_OF_BLOCK_OUTLINE.color(), true, true, true, true, RADIUS_OF_OUTLINE.value());
                }
                if(SHOW_PANEL_OUTLINE.value()) {
                    RenderUtil.drawRoundedOutline(graphics, x + 1, y + 1, (int) Math.ceil(blockId.length() * 6.5), HEIGHT_OF_PANEL.value(), COLOR_OF_PANEL_OUTLINE.color(), true, true, true, true, RADIUS_OF_PANEL.value());
                }
                graphics.item(new ItemStack(block), x + 5, y + yPosObj - 5);
                RenderUtil.drawText(graphics, client, blockName, x + 25, y + yPosObj - 3, COLOR_OF_TEXT_NAME.color());
                RenderUtil.drawText(graphics, client, blockId, x + 25, y + yPosObj + 5, COLOR_OF_TEXT_ID.color());
            }
        }
    }

    @Override
    public int panelWidth(Minecraft client) {
        return this.panelWidth;
    }

    @Override
    public int panelHeight(Minecraft client) {
        return this.panelHeight;
    }
}
