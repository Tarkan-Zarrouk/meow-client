package Main.main.meow.client.ui.hud;

import Main.main.meow.client.MeowClient;
import Main.main.meow.client.manager.HudComponentManager;
import Main.main.meow.client.module.Module;
import Main.main.meow.client.manager.NotificationManager;
import Main.main.meow.client.module.modules.client.clickgui.ClickGuiModule;
import Main.main.meow.client.ui.clickgui.view.ClickGuiRenderer;
import Main.main.meow.client.util.RenderUtil2D;
import Main.main.meow.client.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public final class HudEditorScreen extends Screen {
    private static final int HOVER_DARKEN = 0x50000000;
    private static final Identifier CLICKGUI_TEXTURE = Identifier.fromNamespaceAndPath("meow", "button/clickgui");
    private static final Identifier HUD_TEXTURE = Identifier.fromNamespaceAndPath("meow", "button/hud");
    private static boolean rightShiftDown;

    private final HudComponentManager components;
    private final TextUtil textUtil = new TextUtil();
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
            RenderUtil2D.drawPanel(graphics, hovered.x(), hovered.y(), hovered.width(), hovered.height(), HOVER_DARKEN);
        }
        if(ClickGuiModule.getShowHudInfoText()) {
            RenderUtil2D.drawText(graphics, super.font, TextUtil.setComponentFont("HUD EDITOR  •  Drag components  •  ESC to close"), 10, height - 18, 0xffffffff, true);
        }
        renderSideScreenButtons(graphics, mouseX, mouseY);
    }

    private void renderSideScreenButtons(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int width = 50;
        int height = 50;
        int yPos = (graphics.guiHeight() / 2) - height;

        boolean clickGuiHovered = isHovered(mouseX, mouseY, 0, yPos, width, height);
        boolean hudHovered = isHovered(mouseX, mouseY, 0, yPos + height + (int) ClickGuiModule.getButtonGap(), width, height);

        if (clickGuiHovered) RenderUtil2D.drawPanel(graphics, 0, yPos, width, height,
            ClickGuiModule.getClickGuiButtonHoverBackgroundColor());
        else RenderUtil2D.drawPanel(graphics, 0, yPos, width, height,
            ClickGuiModule.getClickGuiButtonBackgroundColor());

        if (hudHovered)
            RenderUtil2D.drawPanel(graphics, 0, yPos + height + (int) ClickGuiModule.getButtonGap(), width, height,
                ClickGuiModule.getHudButtonHoverBackgroundColor());
        else RenderUtil2D.drawPanel(graphics, 0, yPos + height + (int) ClickGuiModule.getButtonGap(), width, height,
            ClickGuiModule.getHudButtonBackgroundColor());

        RenderUtil2D.drawSprite(graphics, CLICKGUI_TEXTURE, 0, yPos, width, height);
        Component clickTextComponent = Component.literal("ClickGui");
        int getTextCenteredX = textUtil.centeredX(0, width, clickTextComponent.getString());
        int getTextCenteredY = yPos + height - 10;

        if (clickGuiHovered)
            RenderUtil2D.drawText(graphics, font, clickTextComponent, getTextCenteredX, getTextCenteredY, ClickGuiModule.getClickGuiButtonHoverTextColor(), ClickGuiModule.getClickGuiButtonTextShadow(), textUtil.textScale());
        else RenderUtil2D.drawText(graphics, font, clickTextComponent, getTextCenteredX, getTextCenteredY,
            ClickGuiModule.getClickGuiButtonTextColor(),
            ClickGuiModule.getClickGuiButtonTextShadow(), textUtil.textScale());

        RenderUtil2D.drawSprite(graphics, HUD_TEXTURE, 0, yPos + height + (int) ClickGuiModule.getButtonGap(), width, height);
        Component hudTextComponent = Component.literal("HUD");
        int getHudTextCenteredX = textUtil.centeredX(0, width, hudTextComponent.getString());
        int getHudTextCenteredY = yPos + (2 * height) + (int) ClickGuiModule.getButtonGap() - 10;

        if (hudHovered)
            RenderUtil2D.drawText(graphics, font, hudTextComponent, getHudTextCenteredX, getHudTextCenteredY, ClickGuiModule.getHudButtonHoverTextColor(), ClickGuiModule.getHudButtonTextShadow(), textUtil.textScale());
        else RenderUtil2D.drawText(graphics, font, hudTextComponent, getHudTextCenteredX, getHudTextCenteredY,
            ClickGuiModule.getHudButtonTextColor(),
            ClickGuiModule.getHudButtonTextShadow(), textUtil.textScale());

        if (ClickGuiModule.getClickGuiButtonShowOutline()) {
            int outlineColor = ClickGuiModule.getClickGuiButtonOutlineColor();
            int outlineThickness = (int) ClickGuiModule.getClickGuiButtonOutlineThickness();

            for (int i = 0; i < outlineThickness; i++) {
                RenderUtil2D.drawHorizontalLine(graphics, i, width - i, yPos + i, outlineColor);
                RenderUtil2D.drawHorizontalLine(graphics, i, width - i, yPos + height - i, outlineColor);
                RenderUtil2D.drawVerticalLine(graphics, i, yPos + i, yPos + height - i, outlineColor);
                RenderUtil2D.drawVerticalLine(graphics, width - i, yPos + i, yPos + height - i, outlineColor);
            }
        }

        if (ClickGuiModule.getHudButtonShowOutline()) {
            int outlineColor = ClickGuiModule.getHudButtonOutlineColor();
            int outlineThickness = (int) ClickGuiModule.getHudButtonOutlineThickness();
            int hudYPos = yPos + height + (int) ClickGuiModule.getButtonGap();

            for (int i = 0; i < outlineThickness; i++) {
                RenderUtil2D.drawHorizontalLine(graphics, i, width - i, hudYPos + i, outlineColor);
                RenderUtil2D.drawHorizontalLine(graphics, i, width - i, hudYPos + height - i, outlineColor);
                RenderUtil2D.drawVerticalLine(graphics, i, hudYPos + i, hudYPos + height - i, outlineColor);
                RenderUtil2D.drawVerticalLine(graphics, width - i, hudYPos + i, hudYPos + height - i, outlineColor);
            }
        }
        if (ClickGuiModule.getShowButtonDivider()) {
            RenderUtil2D.drawHorizontalLine(graphics, 0, width, yPos + height + (int) (ClickGuiModule.getButtonGap() / 2), ClickGuiModule.getButtonDividerColor());
        }
    }

    private static boolean isHovered(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            double x = event.x();
            double y = event.y();
            if (x >= 0 && x < 50 && y >= height / 2 - 50 && y < height / 2) {
                Minecraft.getInstance().setScreen(new ClickGuiRenderer(MeowClient.getModuleManager()));
                return true;
            }
            int hudY = height / 2 + (int) ClickGuiModule.getButtonGap();
            if (x >= 0 && x < 50 && y >= hudY && y < hudY + 50) {
                return true;
            }
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
            components.save(net.minecraft.client.Minecraft.getInstance(), MeowClient.getModuleManager());
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

    public static void render(GuiGraphicsExtractor graphics, Minecraft client) {
        LocalPlayer player = client.player;
        if (player == null || client.level == null) {
            return;
        }
        MeowClient.getHudComponentManager().render(graphics, client, false);
        NotificationManager.render(graphics, client);
    }


    public static void handleHudEditorKey(Minecraft client) {

        Module clickGui = MeowClient.getModuleManager().byName("ClickGUI");
        int key;
        if(clickGui == null) {
            key = GLFW.GLFW_KEY_RIGHT_SHIFT;
        } else {
            key = clickGui.keybind();
        }
        boolean rightShift;
        if(key == GLFW.GLFW_KEY_UNKNOWN) {
            rightShift = GLFW.glfwGetKey(client.getWindow().handle(), GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
        } else {
            rightShift = GLFW.glfwGetKey(client.getWindow().handle(), key) == GLFW.GLFW_PRESS;
        }
        if (rightShift && !rightShiftDown && client.screen == null) {
            client.setScreen(new ClickGuiRenderer(MeowClient.getModuleManager()));
        }
        if (GLFW.glfwGetKey(client.getWindow().handle(), GLFW.GLFW_KEY_F8) == GLFW.GLFW_PRESS && client.screen == null) {
            client.setScreen(new HudEditorScreen(MeowClient.getHudComponentManager()));
        }
        rightShiftDown = rightShift;
    }

    public static boolean enabled(String name) {
        Module module = MeowClient.getModuleManager().byName(name);
        return module != null && module.getEnabled();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
