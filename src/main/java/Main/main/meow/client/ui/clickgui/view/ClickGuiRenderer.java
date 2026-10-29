package Main.main.meow.client.ui.clickgui.view;

import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.colorpicker.ColorPicker;
import Main.main.meow.client.module.Module;
import Main.main.meow.client.module.modules.client.clickgui.ClickGuiModule;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.NumberSetting;
import Main.main.meow.client.manager.ModuleManager;
import Main.main.meow.client.ui.clickgui.controller.ClickGuiInputHandler;
import Main.main.meow.client.ui.clickgui.controller.ClickGuiSettingsHandler;
import Main.main.meow.client.ui.clickgui.model.Category;
import Main.main.meow.client.ui.clickgui.model.ClickGuiLayout;
import Main.main.meow.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.List;


public final class ClickGuiRenderer extends Screen {
    private final ModuleManager modules;
    private ClickGuiSettingsHandler settings;
    private ClickGuiLayout layout;
    private final int headerHeight = ClickGuiLayout.getHeaderHeight();
    private final int subcategoryHeight = ClickGuiLayout.getSubcategoryHeight();
    private ClickGuiStyler styler;
    private ClickGuiInputHandler input;
    private double scrolledAmount = 0;

    public ClickGuiRenderer(ModuleManager modules) {
        super(Component.literal("HUD Modules")); // screen reader purpose
        this.modules = modules;
    }

    @Override
    protected void init() {
        settings = new ClickGuiSettingsHandler(modules);
        styler = new ClickGuiStyler(font);
        layout = new ClickGuiLayout(modules, settings);
        input = new ClickGuiInputHandler(layout, settings);
        settings.loadExpanded();
        layout.init(width, height);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        this.scrolledAmount += verticalAmount;
        input.setScrollOffset((int) scrolledAmount);
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }


    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        if(ClickGuiModule.getBlurBackgroundValue()) {
            extractBlurredBackground(graphics);
        }
    }


    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        return input.mouseClicked(event) || super.mouseClicked(event, doubleClick);
    }
    @Override
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        return input.mouseDragged(event, width, height) || super.mouseDragged(event, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        return input.mouseReleased(event) || super.mouseReleased(event);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        return input.keyPressed(event) || super.keyPressed(event);
    }

    @Override
    public void removed() {
        settings.save();
        super.removed();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        int screenWidth = super.width;
        int screenHeight = super.height;
        layout.syncWithSettings(screenWidth, screenHeight);
        int alpha = layout.transitionAlpha();
        int animationOffset = (int) ((1f - ClickGuiStyler.easeIn(layout.transitionProgress())) * -18f); // arbitrary values, like 5 minutes of rerunning to see what's nice
        int columnWidth = layout.columnWidth();
        int rowHeight = ClickGuiModule.getModuleHeight();
        String tooltipDescription = null;
        for (int column = 0; column < layout.columnCount(); column++) {
            int x = layout.columnX(column);
            renderCategories(graphics, layout.category(column), x, layout.columnY(column) + animationOffset + (int) scrolledAmount, columnWidth, alpha);

            List<Module> entries = layout.modulesIn(column);
            for (int row = 0; row < entries.size(); row++) {
                Module module = entries.get(row);
                int y = layout.moduleY(column, row);
                boolean hovered = isHovered(mouseX, mouseY, x, y + (int) scrolledAmount, columnWidth, rowHeight);
                if (hovered && ClickGuiModule.getToolTipShowVal()) {
                    tooltipDescription = module.getDescription();
                }
                boolean lastModule = row == entries.size() - 1;
                renderModuleRow(graphics, module, x, y + animationOffset + (int) scrolledAmount, columnWidth, rowHeight, hovered, lastModule, alpha);
                if (settings.isExpanded(module)) {
                    String settingTooltip = renderSettings(graphics, module, x, y + rowHeight, columnWidth, mouseX, mouseY, lastModule, alpha, scrolledAmount);
                    if (settingTooltip != null && ClickGuiModule.getToolTipShowVal()) tooltipDescription = settingTooltip;
                }
            }
        }
        ColorPicker colorPicker = settings.colorPicker();
        if (colorPicker != null) {
            colorPicker.setPosition(Math.max(8, (screenWidth - colorPicker.width()) / 2), Math.max(8, (screenHeight - colorPicker.height()) / 2));
            colorPicker.render(graphics);
        } else if (tooltipDescription != null && !tooltipDescription.isBlank()) {
            renderTooltip(graphics, tooltipDescription, mouseX, mouseY, screenWidth, screenHeight);
        }
    }

    /**
     * assist extractRenderState method by drawing the categories
     * @param graphics graphics library
     * @param category a category
     * @param x desired x position
     * @param y desired y position
     * @param columnWidth width of category
     * @param alpha alpha value for transparency
     */
    private void renderCategories(GuiGraphicsExtractor graphics, Category category, int x, int y, int columnWidth, int alpha) {
        RenderUtil.roundedFill(graphics, x, y, columnWidth, headerHeight, ClickGuiStyler.withAlpha(ClickGuiModule.categoryColor(), alpha), true, true, false, false, ClickGuiModule.getClickGuiHeaderBorderRadius());
        graphics.fill(x + ClickGuiStyler.CORNER_RADIUS, y + headerHeight - 1, x + columnWidth - ClickGuiStyler.CORNER_RADIUS, y + headerHeight, ClickGuiStyler.withAlpha(ClickGuiStyler.BORDER, alpha));
        String label = styler.truncate(category.title(), columnWidth - 10);
        styler.drawText(graphics, styler.styled(label), styler.centeredX(x, columnWidth, label), styler.centeredY(y, headerHeight), ClickGuiStyler.withAlpha(ClickGuiModule.getCategoryTextColor(), alpha), false);
    }

    /**
     * Renders a single module row for a given module at the specified position and size.
     * @param graphics graphics library
     * @param module a module
     * @param x desired x position
     * @param y desired y position
     * @param columnWidth width of category
     * @param hovered hovered state
     * @param lastModule last in the list
     * @param alpha transparency value
     */
    private void renderModuleRow(GuiGraphicsExtractor graphics, Module module, int x, int y, int columnWidth, int rowHeight, boolean hovered, boolean lastModule, int alpha) {
        boolean roundedBottom = lastModule && !settings.isExpanded(module);
        int background;
        if(module.getEnabled()) {
            background = ClickGuiModule.moduleColor();
        } else if(hovered) {
            background = ClickGuiStyler.ROW_HOVER;
        } else {
            background = ClickGuiStyler.ROW;
        }
        RenderUtil.roundedFill(graphics, x, y, columnWidth, rowHeight, ClickGuiStyler.withAlpha(background, alpha), false, false, roundedBottom, roundedBottom, ClickGuiModule.getClickGuiBorderRadius());
        String label = styler.truncate(module.getName(), columnWidth - 24);
        styler.drawText(graphics, styler.styled(label), styler.centeredX(x, columnWidth, label), styler.centeredY(y, rowHeight), ClickGuiStyler.withAlpha(ClickGuiStyler.TEXT, alpha), true);
        if(settings.isExpanded(module)) {
            styler.drawText(graphics, styler.styled("..."), x + columnWidth - 14, y + 5, ClickGuiStyler.withAlpha(ClickGuiStyler.MUTED, alpha), false);
        } else {

            styler.drawText(graphics, styler.styled("⋮"), x + columnWidth - 14, y + 5, ClickGuiStyler.withAlpha(ClickGuiStyler.MUTED, alpha), false);
        }
    }

    /**
     * Renders the settings for a given module at the specified position and size, and returns a tooltip description if any setting is hovered.
     * @param graphics graphics library
     * @param module a module
     * @param x desired x position
     * @param dropdownY desired y position for the dropdown
     * @param columnWidth width of category
     * @param mouseX position of mouse in x axis
     * @param mouseY position of mouse in y axis
     * @param lastModule last in the list
     * @param alpha transparency value
     * @return tooltip description if any setting is hovered, null otherwise
     */
    private String renderSettings(GuiGraphicsExtractor graphics, Module module, int x, int dropdownY, int columnWidth, int mouseX, int mouseY, boolean lastModule, int alpha, double scrolledAmount) {
        String tooltip = null;
        List<String> entries = settings.settingsFor(module);
        for (int setting = 0; setting < entries.size(); setting++) {
            int settingY = dropdownY + setting * subcategoryHeight + (int) scrolledAmount; // math to calculate the y position of the setting row
            String text = entries.get(setting);
            if (isHovered(mouseX, mouseY, x, settingY, columnWidth, subcategoryHeight)) {
                tooltip = settings.settingDescription(module, setting);
            }
            boolean lastSetting = lastModule && setting == entries.size() - 1; // -1 bc index starts at 0
            RenderUtil.roundedFill(graphics, x, settingY, columnWidth, subcategoryHeight, ClickGuiStyler.withAlpha(ClickGuiStyler.SETTING_ROW, alpha), false, false, lastSetting, lastSetting, ClickGuiModule.getClickGuiBorderRadius());
            styler.drawText(graphics, styler.styled(text), styler.centeredX(x, columnWidth, text), styler.centeredY(settingY, subcategoryHeight), ClickGuiStyler.withAlpha(ClickGuiStyler.MUTED, alpha), false);
            NumberSetting slider = settings.sliderAt(module, setting);
            if (slider != null)
                renderSlider(graphics, slider, x, settingY, columnWidth);
        }
        return tooltip;
    }

    /**
     * Renders a slider for a given NumberSetting at the specified position and size.
     * @param graphics graphics library
     * @param slider the slider to render
     * @param x desired x position
     * @param settingY the setting position in y axis
     * @param columnWidth width of module
     */
    private void renderSlider(GuiGraphicsExtractor graphics, NumberSetting slider, int x, int settingY, int columnWidth) {
        int sliderX = x + 8; // +8 for padding
        int sliderY = settingY + subcategoryHeight - 4; // -4 for padding
        int sliderWidth = columnWidth - 16;
        graphics.fill(sliderX, sliderY, sliderX + sliderWidth, sliderY + 2, 0xff555861);
        int knobX = sliderX + Math.round(slider.progress() * sliderWidth);
        graphics.fill(knobX - 2, sliderY - 2, knobX + 3, sliderY + 4, ClickGuiModule.moduleColor()); // +3 for width of knob
    }

    /**
     * Renders a tooltip with the given description at the specified mouse position, ensuring it stays within the screen bounds.
     * @param graphics graphics library
     * @param description tooltip
     * @param mouseX mouse in x axis value
     * @param mouseY mouse in y axis value
     * @param screenWidth user screen instance height
     * @param screenHeight user screeninstance height
     */
    private void renderTooltip(GuiGraphicsExtractor graphics, String description, int mouseX, int mouseY, int screenWidth, int screenHeight) {
        List<String> lines = styler.wrapTooltip(description, 220);
        int tooltipWidth = 0;
        for (String line : lines)
            tooltipWidth = Math.max(tooltipWidth, styler.textWidth(styler.styled(line)));

        tooltipWidth += 12; // +12 for padding
        int tooltipHeight = lines.size() * styler.textLineHeight() + 8; // math: size of tooltip = number of lines * line height + padding weird issues thanks ai
        int tooltipX = mouseX + 12;
        int tooltipY = mouseY + 12;
        if (tooltipX + tooltipWidth > screenWidth - 4)
            tooltipX = mouseX - tooltipWidth - 12;
        if (tooltipY + tooltipHeight > screenHeight - 4)
            tooltipY = mouseY - tooltipHeight - 12;
        tooltipX = Math.max(4, tooltipX);
        tooltipY = Math.max(4, tooltipY);
        if(ClickGuiModule.showOutline()) {
            RenderUtil.drawRoundedOutline(graphics, tooltipX - 1, tooltipY - 1, tooltipWidth + 2, tooltipHeight + 2, ClickGuiModule.tooltipBorderColor(), ClickGuiModule.getToolTipBorderRadius());
        }
        RenderUtil.roundedFill(graphics, tooltipX, tooltipY, tooltipWidth, tooltipHeight, ClickGuiModule.getToolTipBackgroundColor(), true, true, true, true, ClickGuiModule.getToolTipBorderRadius());
        for (int line = 0; line < lines.size(); line++) {
            Component text = styler.styled(lines.get(line));
            int x = tooltipX + 6;
            int y = tooltipY + 4 + line * styler.textLineHeight();
            styler.drawText(graphics, text, x, y, ClickGuiModule.getToolTipTextColor(), ClickGuiModule.getShadowBoolean());
        }
    }

    /**
     * Checks if the mouse is hovered over a given rectangle defined by x, y, width, and height.
     * @param mouseX mouse in x-axis
     * @param mouseY mouse in y-axis
     * @param x desired x position
     * @param y desired y position
     * @param width width of tooltip
     * @param height height of tooltip
     * @return true: hovered; false: not hovered
     */
    private static boolean isHovered(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }
}
