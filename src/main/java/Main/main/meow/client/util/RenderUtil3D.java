package Main.main.meow.client.util;

import com.mojang.blaze3d.vertex.*;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import org.joml.Matrix4f;

public class RenderUtil3D {

    public static void drawSlab(LevelRenderContext context, int xPos, int yPos, int zPos, int color) {
        Camera camera = context.gameRenderer().getMainCamera();
        BlockPos targetPosition = new BlockPos(xPos, yPos,zPos);
        double renderX = targetPosition.getX() - camera.position().x;
        double renderY = targetPosition.getY() - camera.position().y;
        double renderZ = targetPosition.getZ() - camera.position().z;

        PoseStack poseStack = context.poseStack();
        if(!poseStack.isEmpty()) {
            poseStack.pushPose();
        }
        poseStack.translate(renderX, renderY, renderZ);
        Matrix4f matrix = poseStack.last().pose();

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        bufferBuilder.addVertex(matrix, 0,1,0).setColor(color);
        bufferBuilder.addVertex(matrix, 0,1,1).setColor(color);
        bufferBuilder.addVertex(matrix, 1,1,1).setColor(color);
        bufferBuilder.addVertex(matrix, 1,1,0).setColor(color);

        RenderTypes.debugFilledBox().draw(bufferBuilder.buildOrThrow());
        if(!poseStack.isEmpty()) {
            poseStack.popPose();
        }
    }

    public static void drawFilledBox(LevelRenderContext context, int xPos, int yPos, int zPos, int color) {
        Camera camera = context.gameRenderer().getMainCamera();
        BlockPos targetPosition = new BlockPos(xPos, yPos,zPos);
        double renderX = targetPosition.getX() - camera.position().x;
        double renderY = targetPosition.getY() - camera.position().y;
        double renderZ = targetPosition.getZ() - camera.position().z;

        PoseStack poseStack = context.poseStack();
        poseStack.pushPose();
        poseStack.translate(renderX, renderY, renderZ);
        Matrix4f matrix = poseStack.last().pose();

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        // bottom
        bufferBuilder.addVertex(matrix, 0,0,0).setColor(color);
        bufferBuilder.addVertex(matrix, 1,0,0).setColor(color);
        bufferBuilder.addVertex(matrix, 1,0,1).setColor(color);
        bufferBuilder.addVertex(matrix, 0,0,1).setColor(color);

        // top
        bufferBuilder.addVertex(matrix, 0,1,0).setColor(color);
        bufferBuilder.addVertex(matrix, 0,1,1).setColor(color);
        bufferBuilder.addVertex(matrix, 1,1,1).setColor(color);
        bufferBuilder.addVertex(matrix, 1,1,0).setColor(color);

        // front
        bufferBuilder.addVertex(matrix, 0,0,1).setColor(color);
        bufferBuilder.addVertex(matrix, 1,0,1).setColor(color);
        bufferBuilder.addVertex(matrix, 1,1,1).setColor(color);
        bufferBuilder.addVertex(matrix, 0,1,1).setColor(color);

        // back
        bufferBuilder.addVertex(matrix, 1,0,0).setColor(color);
        bufferBuilder.addVertex(matrix, 0,0,0).setColor(color);
        bufferBuilder.addVertex(matrix, 0,1,0).setColor(color);
        bufferBuilder.addVertex(matrix, 1,1,0).setColor(color);

        // left
        bufferBuilder.addVertex(matrix, 0,0,0).setColor(color);
        bufferBuilder.addVertex(matrix, 0,0,1).setColor(color);
        bufferBuilder.addVertex(matrix, 0,1,1).setColor(color);
        bufferBuilder.addVertex(matrix, 0,1,0).setColor(color);

        // right
        bufferBuilder.addVertex(matrix, 1,0,1).setColor(color);
        bufferBuilder.addVertex(matrix, 1,0,0).setColor(color);
        bufferBuilder.addVertex(matrix, 1,1,0).setColor(color);
        bufferBuilder.addVertex(matrix, 1,1,1).setColor(color);
        RenderTypes.debugFilledBox().draw(bufferBuilder.buildOrThrow());
        poseStack.popPose();
    }
}
