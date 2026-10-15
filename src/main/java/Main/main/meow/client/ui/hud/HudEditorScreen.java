package Main.main.meow.client.ui.hud;

import Main.main.meow.client.MeowClient;
import Main.main.meow.client.module.modules.client.ClickGuiModule;
import Main.main.meow.client.ui.hud.hudgui.HudGuiStyler;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public final class HudEditorScreen extends Screen {
    private static final int HOVER_DARKEN = 0x50000000;

    private final HudComponentManager components;
    private HudComponent dragging;
    private double offsetX;
    private double offsetY;

    public HudEditorScreen(HudComponentManager components) {
        super(Component.literal("HUD Editor"));
        this.components = components;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        graphics.fill(0, 0, width, height, 0x40000000);
    }
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        components.render(graphics, net.minecraft.client.Minecraft.getInstance(), true);
        // keep the dragged component darkened even if the cursor briefly leaves its box
        HudComponent hovered = dragging;
        if (hovered == null) {
            hovered = components.at(mouseX, mouseY);
        }
        if (hovered != null) {
            graphics.fill(hovered.x(), hovered.y(), hovered.x() + hovered.width(), hovered.y() + hovered.height(), HOVER_DARKEN);
        }
        graphics.text(font, QolHudRenderer.styled("HUD EDITOR  •  Drag components  •  ESC to close"), 10, height - 18, 0xffffffff, true);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            dragging = components.at(event.x(), event.y());
            if (dragging != null) {
                offsetX = event.x() - dragging.x();
                offsetY = event.y() - dragging.y();
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        if (dragging != null) {
            dragging.moveTo((int) (event.x() - offsetX), (int) (event.y() - offsetY), width, height);
            return true;
        }
        return super.mouseDragged(event, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            dragging = null;
            components.save(net.minecraft.client.Minecraft.getInstance(), MeowClient.MODULE_MANAGER);
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == GLFW.GLFW_KEY_ESCAPE) {
            onClose();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
