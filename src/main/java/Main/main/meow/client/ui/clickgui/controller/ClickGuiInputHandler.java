package Main.main.meow.client.ui.clickgui.controller;

import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.colorpicker.ColorPicker;
import Main.main.meow.client.module.Module;
import Main.main.meow.client.module.modules.client.clickgui.ClickGuiModule;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.NumberSetting;
import Main.main.meow.client.ui.clickgui.model.ClickGuiLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.lwjgl.glfw.GLFW;

// every handler returns true when the event was consumed, false lets the screen fall through to vanilla
public final class ClickGuiInputHandler {
    private final ClickGuiLayout layout;
    private final ClickGuiSettingsHandler settings;
    private int draggingColumn = -1, sliderColumn = -1;
    private boolean dragging;
    private NumberSetting draggingSlider;
    private double dragOffsetX, dragOffsetY;
    private int scrollOffset;
    private final int subcategoryHeight = ClickGuiLayout.getSubcategoryHeight();

    public ClickGuiInputHandler(ClickGuiLayout layout, ClickGuiSettingsHandler settings) {
        this.layout = layout;
        this.settings = settings;
    }

    public void setScrollOffset(int scrollOffset) {
        this.scrollOffset = scrollOffset;
    }

    public boolean mouseClicked(MouseButtonEvent event) {
        if (layout.transitionProgress() < 1f) { // don't allow interaction while animating
            return true;
        }
        ColorPicker colorPicker = settings.colorPicker();
        if (colorPicker != null) {
            if (colorPicker.mouseClicked(event.x(), event.y())) return true;
            if (event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                settings.closeColorPicker();
                return true;
            }
        }
        double mouseY = event.y() - scrollOffset;
        if (event.button() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            int column = layout.columnAt(event.x(), mouseY);
            Module module = layout.moduleAt(column, event.x(), mouseY);
            if (module != null) {
                settings.toggleExpanded(module);
            }
            return true;
        }
        if (event.button() != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            return false;
        }
        int column = layout.columnAt(event.x(), mouseY);
        if (column >= 0 && mouseY >= layout.columnY(column) && mouseY < layout.columnY(column) + ClickGuiLayout.getHeaderHeight()) {
            dragging = true;
            draggingColumn = column;
            dragOffsetX = event.x() - layout.columnX(column);
            dragOffsetY = mouseY - layout.columnY(column);
            return true;
        }
        Module module = layout.moduleAt(column, event.x(), mouseY);
        if (module == null) {
            return false;
        }
        int rowY = layout.moduleY(column, layout.modulesIn(column).indexOf(module));
        int rowHeight = ClickGuiModule.getModuleHeight();
        // if the module is expanded and the click is within the settings area, handle setting clicks
        if (settings.isExpanded(module) && mouseY >= rowY + rowHeight && mouseY < rowY + rowHeight + settings.settingsFor(module).size() * subcategoryHeight) {
            int setting = (int) ((mouseY - rowY - rowHeight) / subcategoryHeight);
            NumberSetting slider = settings.sliderAt(module, setting);
            if (slider != null) {
                settings.setSliderFromMouse(slider, event.x(), layout.columnX(column), layout.columnWidth());
                draggingSlider = slider;
                sliderColumn = column;
                settings.save();
            } else {
                settings.clickSetting(module, setting);
            }
        } else if (!module.getName().equalsIgnoreCase("ClickGUI")) {
            module.toggle();
        }
        return true;
    }

    public boolean mouseDragged(MouseButtonEvent event, int screenWidth, int screenHeight) {
        ColorPicker colorPicker = settings.colorPicker();
        // if the color picker is open, let it handle dragging first
        if (colorPicker != null && colorPicker.mouseDragged(event.x(), event.y())) return true;
        if (draggingSlider != null) {
            if (sliderColumn >= 0 && sliderColumn < layout.columnCount()) {
                settings.setSliderFromMouse(draggingSlider, event.x(), layout.columnX(sliderColumn), layout.columnWidth());
            }
            settings.save();
            return true;
        }
        if (layout.transitionProgress() >= 1f && dragging) {
            // if the layout is fully transitioned, and we are dragging a column, move it
            layout.moveColumn(draggingColumn, (int) (event.x() - dragOffsetX), (int) (event.y() - scrollOffset - dragOffsetY), screenWidth, screenHeight);
            return true;
        }
        return false;
    }

    public boolean mouseReleased(MouseButtonEvent event) {
        ColorPicker colorPicker = settings.colorPicker();
        if (colorPicker != null) {
            colorPicker.mouseReleased();
            settings.save();
        }
        if (event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            dragging = false;
            draggingColumn = -1;
            draggingSlider = null;
            sliderColumn = -1;
            settings.save();
            return true;
        }
        return false;
    }

    public boolean keyPressed(KeyEvent event) {
        if (settings.captureKey(event.key())) {
            return true;
        }
        if (event.key() == GLFW.GLFW_KEY_ESCAPE) {
            settings.save();
            Minecraft.getInstance().setScreen(null);
            return true;
        }
        return false;
    }
}
