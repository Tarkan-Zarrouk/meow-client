package Main.main.meow.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class RenderUtil {
    private static final int GUI_PANEL_COLOR = 0xb9161d2a;
    private static final int GUI_TEXT_COLOR = 0xfff2f0f4;
    public static int getGuiColorPanel() {
        return GUI_PANEL_COLOR;
    }
    public static int getGuiTextColor() {
        return GUI_TEXT_COLOR;
    }

    /**
     * Draws text on the screen
     * @param graphics Graphics renderer class
     * @param client Minecraft instance
     * @param value The string to draw
     * @param x The x position
     * @param y The y position
     * @param color Text color
     */
    public static void drawText(GuiGraphicsExtractor graphics, Minecraft client, String value, int x, int y, int color) {
        graphics.text(client.font, TextUtil.setFont(value), x, y, color);
    }

    /**
     * Draws text on the screen
     * @param graphics Graphics renderer class
     * @param client Minecraft instance
     * @param value The string to draw
     * @param x The x position
     * @param y The y position
     */
    public static void drawText(GuiGraphicsExtractor graphics, Minecraft client, String value, int x, int y) {
        graphics.text(client.font, TextUtil.setFont(value), x, y, getGuiTextColor());
    }

    /**
     * Draw a quadrilateral on the screen
     * @param graphics Graphics renderer class
     * @param x The x position
     * @param y The y position
     * @param width The width
     * @param height The height
     * @param color Text color
     */
    public static void drawPanel(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color) {
        graphics.fill(x, y, x + width, y + height, color);
    }

    /**
     * Draw a quadrilateral on the screen
     * @param graphics Graphics renderer class
     * @param x The x position
     * @param y The y position
     * @param width The width
     * @param height The height
     */
    public static void drawPanel(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + height, getGuiColorPanel());
    }

    /**
     * Draws a random object to the screen
     * @param graphics graphics library
     * @param x x axis position
     * @param y y axis position
     * @param width thickness of rounded object
     * @param height tallness of rounded object
     * @param color color of rounded object
     * @param topLeft round corner left-top
     * @param topRight round corner right-top
     * @param bottomLeft round corner left-bottom
     * @param bottomRight round corner right-bottom
     * @param radius the 'circleness'
     */
    public static void roundedFill(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color, boolean topLeft, boolean topRight, boolean bottomLeft, boolean bottomRight, int radius) {
        int borderRadius = Math.min(radius, width / 2);
        for (int row = 0; row < height; row++) {
            int leftInset = 0;
            int rightInset = 0;
            if (topLeft && row < borderRadius) {
                leftInset = roundedInset(borderRadius, row);
            }
            if (topRight && row < borderRadius) {
                rightInset = roundedInset(borderRadius, row);
            }
            if (bottomLeft && row >= height - borderRadius) {
                leftInset = roundedInset(borderRadius, height - row - 1);
            }
            if (bottomRight && row >= height - borderRadius) {
                rightInset = roundedInset(borderRadius, height - row - 1);
            }
            if (leftInset > 0 || rightInset > 0) {
                graphics.fill(x + leftInset, y + row, x + width - rightInset, y + row + 1, color);
            } else {
                graphics.fill(x, y + row, x + width, y + row + 1, color);
            }
        }
    }
    // see method above
    public static void drawRoundedOutline(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color, int radius) {
        roundedFill(graphics, x, y, width, height, color, true, true, true, true, radius);
    }

    private static int roundedInset(int radius, int distance) {
        double circle = radius - 0.5;
        double offset = distance - circle;
        return Math.max(0, (int) Math.ceil(circle - Math.sqrt(Math.max(0, circle * circle - offset * offset)))); // read a minecraft clients code for this, i have no idea how this works but it does and it looks good so im not complaining
    }

    /**
     * draws a 3D model of a living entity on the screen
     * @param graphics graphics library
     * @param client instance
     * @param entity the entity to render
     * @param x x position
     * @param y y position
     * @param modelSize size of the model being drawn
     */
    public static void drawModel(GuiGraphicsExtractor graphics, Minecraft client, LivingEntity entity, int x, int y, int modelSize) {
        var renderer = client.getEntityRenderDispatcher().getRenderer(entity);
        EntityRenderState entityRenderState = renderer.createRenderState(entity, 1.0f);
        entityRenderState.shadowPieces.clear();

        float spinAngle = (System.currentTimeMillis() % 4000L) / 4000.0f * (float) (Math.PI * 2.0);
        if (entityRenderState instanceof LivingEntityRenderState living) {
            living.bodyRot = 180.0f;
            living.yRot = 0.0f;
            living.xRot = 0.0f;
        }
        float largest = Math.max(1.0f, Math.max(entityRenderState.boundingBoxHeight, entityRenderState.boundingBoxWidth)); // whatever bigger: height or width, we want to scale it down to fit in the box
        float scale = (modelSize * 0.8f) / largest;
        Vector3f translation = new Vector3f(0.0f, entityRenderState.boundingBoxHeight / 2.0f, 0.0f);
        Quaternionf rotation = new Quaternionf().rotateZ((float) Math.PI).rotateY(spinAngle);
        graphics.entity(entityRenderState, scale, translation, rotation, null, x, y, x + modelSize, y + modelSize);
    }

    /**
     * Draw out a texture (sprite)
     * @param graphics graphics library
     * @param texture the texture to draw
     * @param x the x position of the texture
     * @param y the y position of the texture
     * @param width the width surrounding the texture (typically, if it's not humungous or is a mc default texture, assume 9 pixels)
     * @param height the height surrounding the texture (typically, if it's not humungous or is a mc default texture, assume 9 pixels)
     */
    public static void drawSprite(GuiGraphicsExtractor graphics, Identifier texture, int x, int y, int width, int height) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, texture, x, y, width, height);
    }
}
