package Main.main.meow.client.ui.hud.hudgui;

import Main.main.meow.client.setting.modulesettings.colorpicker.ColorPicker;
import Main.main.meow.client.module.Module;
import Main.main.meow.client.module.modules.client.ClickGuiModule;
import Main.main.meow.client.setting.NumberSetting;
import Main.main.meow.client.ui.clickgui.Category;
import Main.main.meow.client.manager.ModuleManager;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.List;

import static Main.main.meow.client.ui.hud.hudgui.HudGuiLayout.HEADER_HEIGHT;
import static Main.main.meow.client.ui.hud.hudgui.HudGuiLayout.ROW_HEIGHT;
import static Main.main.meow.client.ui.hud.hudgui.HudGuiLayout.SUBCATEGORY_HEIGHT;
import static Main.main.meow.client.ui.hud.hudgui.HudGuiStyler.withAlpha;

// the clickgui screen: renders the ui and delegates state, layout and input to the other hudgui classes
public final class HudGuiRenderer extends Screen {
    private final ModuleManager modules;
    private HudGuiSettingsHandler settings;
    private HudGuiLayout layout;
    private HudGuiStyler styler;
    private HudGuiInputHandler input;

    public HudGuiRenderer(ModuleManager modules) {
        super(Component.literal("HUD Modules"));
        this.modules = modules;
    }

    @Override
    protected void init() {
        settings = new HudGuiSettingsHandler(modules);
        styler = new HudGuiStyler(font);
        layout = new HudGuiLayout(modules, settings);
        input = new HudGuiInputHandler(layout, settings);
        settings.loadExpanded();
        layout.init(width, height);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        extractBlurredBackground(graphics);
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
        int screenWidth = width;
        int screenHeight = height;
        int alpha = layout.transitionAlpha();
        int animationOffset = (int) ((1f - HudGuiStyler.easeIn(layout.transitionProgress())) * -18f);
        int columnWidth = layout.columnWidth();
        String tooltipDescription = null;
        for (int column = 0; column < layout.columnCount(); column++) {
            int x = layout.columnX(column);
            renderHeader(graphics, layout.category(column), x, layout.columnY(column) + animationOffset, columnWidth, alpha);

            List<Module> entries = layout.modulesIn(column);
            for (int row = 0; row < entries.size(); row++) {
                Module module = entries.get(row);
                int y = layout.moduleY(column, row);
                boolean hovered = isHovered(mouseX, mouseY, x, y, columnWidth, ROW_HEIGHT);
                if (hovered) {
                    tooltipDescription = module.description();
                }
                boolean lastModule = row == entries.size() - 1;
                renderModuleRow(graphics, module, x, y, columnWidth, hovered, lastModule, alpha);
                if (settings.isExpanded(module)) {
                    String settingTooltip = renderSettings(graphics, module, x, y + ROW_HEIGHT,
                            columnWidth, mouseX, mouseY, lastModule, alpha);
                    if (settingTooltip != null) tooltipDescription = settingTooltip;
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

    private void renderHeader(GuiGraphicsExtractor graphics, Category category, int x, int y, int columnWidth, int alpha) {
        HudGuiStyler.roundedFill(graphics, x, y, columnWidth, HEADER_HEIGHT, withAlpha(ClickGuiModule.categoryColor(), alpha), true, true, false, false);
        graphics.fill(x + HudGuiStyler.CORNER_RADIUS, y + HEADER_HEIGHT - 1, x + columnWidth - HudGuiStyler.CORNER_RADIUS, y + HEADER_HEIGHT, withAlpha(HudGuiStyler.BORDER, alpha));
        String label = styler.truncate(category.title(), columnWidth - 10);
        styler.drawText(graphics, styler.styled(label), styler.centeredX(x, columnWidth, label), styler.centeredY(y, HEADER_HEIGHT), withAlpha(HudGuiStyler.CATEGORY_TEXT, alpha), false);
    }

    private void renderModuleRow(GuiGraphicsExtractor graphics, Module module, int x, int y, int columnWidth, boolean hovered, boolean lastModule, int alpha) {
        boolean roundedBottom = lastModule && !settings.isExpanded(module);
        int background;
        if(module.isEnabled()) {
            background = ClickGuiModule.moduleColor();
        } else if(hovered) {
            background = HudGuiStyler.ROW_HOVER;
        } else {
            background = HudGuiStyler.ROW;
        }
        HudGuiStyler.roundedFill(graphics, x, y, columnWidth, ROW_HEIGHT, withAlpha(background, alpha), false, false, roundedBottom, roundedBottom);
        String label = styler.truncate(module.name(), columnWidth - 24);
        styler.drawText(graphics, styler.styled(label), styler.centeredX(x, columnWidth, label), styler.centeredY(y, ROW_HEIGHT), withAlpha(HudGuiStyler.TEXT, alpha), true);
        styler.drawText(graphics, styler.styled("..."), x + columnWidth - 14, y + 10, withAlpha(HudGuiStyler.MUTED, alpha), false);
    }

    // returns the description of the hovered setting, if any
    private String renderSettings(GuiGraphicsExtractor graphics, Module module, int x, int dropdownY, int columnWidth, int mouseX, int mouseY, boolean lastModule, int alpha) {
        String tooltip = null;
        List<String> entries = settings.settingsFor(module);
        for (int setting = 0; setting < entries.size(); setting++) {
            int settingY = dropdownY + setting * SUBCATEGORY_HEIGHT; // math to calculate the y position of the setting row
            String text = entries.get(setting);
            if (isHovered(mouseX, mouseY, x, settingY, columnWidth, SUBCATEGORY_HEIGHT)) {
                tooltip = settings.settingDescription(module, setting);
            }
            boolean lastSetting = lastModule && setting == entries.size() - 1; // -1 bc index starts at 0
            HudGuiStyler.roundedFill(graphics, x, settingY, columnWidth, SUBCATEGORY_HEIGHT, withAlpha(HudGuiStyler.SETTING_ROW, alpha), false, false, lastSetting, lastSetting);
            styler.drawText(graphics, styler.styled(text), styler.centeredX(x, columnWidth, text), styler.centeredY(settingY, SUBCATEGORY_HEIGHT), withAlpha(HudGuiStyler.MUTED, alpha), false);
            NumberSetting slider = settings.sliderAt(module, setting);
            if (slider != null)
                renderSlider(graphics, slider, x, settingY, columnWidth);
        }
        return tooltip;
    }

    private void renderSlider(GuiGraphicsExtractor graphics, NumberSetting slider, int x, int settingY, int columnWidth) {
        int sliderX = x + 8; // +8 for padding
        int sliderY = settingY + SUBCATEGORY_HEIGHT - 4; // -4 for padding
        int sliderWidth = columnWidth - 16;
        graphics.fill(sliderX, sliderY, sliderX + sliderWidth, sliderY + 2, 0xff555861);
        int knobX = sliderX + Math.round(slider.progress() * sliderWidth);
        graphics.fill(knobX - 2, sliderY - 2, knobX + 3, sliderY + 4, ClickGuiModule.moduleColor()); // +3 for width of knob
    }

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
        HudGuiStyler.roundedFill(graphics, tooltipX, tooltipY, tooltipWidth, tooltipHeight, 0xf0181b22, true, true, true, true);
        for (int line = 0; line < lines.size(); line++) {
            Component text = styler.styled(lines.get(line));
            int x = tooltipX + 6;
            int y = tooltipY + 4 + line * styler.textLineHeight();
            int color = HudGuiStyler.TEXT;
            boolean shadow = false;
            styler.drawText(graphics, text, x, y, color, shadow);
        }
    }

    private static boolean isHovered(int mouseX, int mouseY, int x, int y, int width, int height) {
        // use conditional rather than one line
        if (mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height) {
            return true;
        }
        return false;
    }
}
