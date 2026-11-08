package Main.main.meow.client.util;

import Main.main.meow.client.MeowClient;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MappableRingBuffer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

public class RenderUtil3D {
    private static MappableRingBuffer vertexBuffer;
    private static final ByteBufferBuilder ALLOCATOR = new ByteBufferBuilder(RenderType.SMALL_BUFFER_SIZE);
    // this just adds a tint before drawing to screen
    private static final Vector4f COLOR_MODULATOR = new Vector4f(1f, 1f, 1f, 1f);
    private static final Vector3f MODEL_OFFSET = new Vector3f();
    private static final Matrix4f TEXTURE_MATRIX = new Matrix4f();
    private static final RenderPipeline FILLED_BOX_THROUGH_WALLS = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET).withLocation(Identifier.fromNamespaceAndPath("meow", "pipeline/filled_box_depth_aware")).withDepthStencilState(Optional.empty()).build());

    public static void drawCuboid(LevelRenderContext context, int xPos, int yPos, int zPos, int minX, int maxX ,int minY, int maxY, int minZ, int maxZ, float r, float g, float b, float a) {
        // convert to 0.01 --> 1
        float minYFloat = minY / 100f;
        float maxYFloat = maxY / 100f;
        float minXFloat = minX / 100f;
        float maxXFloat = maxX / 100f;
        float minZFloat = minZ / 100f;
        float maxZFloat = maxZ / 100f;
        PoseStack poseStack = context.poseStack();
        var camera = context.levelState().cameraRenderState.pos;
        poseStack.pushPose();
        try {
            // relative distance, computes the current x position to the camera's absolute position to get your position
            poseStack.translate(xPos - camera.x, yPos - camera.y, zPos - camera.z);
            // what we're drawing and how to draw it
            BufferBuilder bufferBuilder = Tesselator.getInstance().begin(FILLED_BOX_THROUGH_WALLS.getVertexFormatMode(), FILLED_BOX_THROUGH_WALLS.getVertexFormat());
            renderFilledBox(poseStack.last().pose(), bufferBuilder, minXFloat, minYFloat, minZFloat, maxXFloat, maxYFloat, maxZFloat, r, g, b, a);
            try (MeshData builtBufferData = bufferBuilder.buildOrThrow()) {
                MeshData.DrawState drawState = builtBufferData.drawState();
                VertexFormat format = drawState.format();
                GpuBuffer vertices = upload(drawState, builtBufferData);
                draw(Minecraft.getInstance(), FILLED_BOX_THROUGH_WALLS, builtBufferData, drawState, vertices, format);
                vertexBuffer.rotate();
            }
        } finally {
            poseStack.popPose();
        }
    }

    private static GpuBuffer upload(MeshData.DrawState drawParameters, MeshData builtBuffer) {
        // max size = vertices * total vertices
        int vertexBufferSize = drawParameters.vertexCount() * drawParameters.format().getVertexSize();

        if(vertexBuffer == null || vertexBuffer.size() < vertexBufferSize) {
            if(vertexBuffer != null) {
                vertexBuffer.close();
            }
            String gpuResourceDebugger = MeowClient.MOD_ID + " Vertex Buffer";
            vertexBuffer = new MappableRingBuffer(() -> gpuResourceDebugger,GpuBuffer.USAGE_VERTEX | GpuBuffer.USAGE_MAP_WRITE, vertexBufferSize);
        }
        CommandEncoder gpuCommunicator = RenderSystem.getDevice().createCommandEncoder();
        int min = 0;
        int maxBytes = builtBuffer.vertexBuffer().remaining();
        GpuBufferSlice maximumBytesReadOnGpu = vertexBuffer.currentBuffer().slice(min, maxBytes);
        boolean readBackDataFromGpu = false;
        boolean sendDataToGpu = true;
        try(GpuBuffer.MappedView gpuBufferMapper = gpuCommunicator.mapBuffer(maximumBytesReadOnGpu, readBackDataFromGpu, sendDataToGpu)) {
            MemoryUtil.memCopy(builtBuffer.vertexBuffer(), gpuBufferMapper.data());
        }
        return vertexBuffer.currentBuffer();
    }

    private static void draw(Minecraft client, RenderPipeline pipeline, MeshData builtBuffer, MeshData.DrawState drawParameters, GpuBuffer vertices, VertexFormat format) {
        GpuBuffer indices;
        VertexFormat.IndexType indexType;
        if((pipeline.getVertexFormatMode() == VertexFormat.Mode.DEBUG_LINES)) {
            builtBuffer.sortQuads(ALLOCATOR, RenderSystem.getProjectionType().vertexSorting());
            indices = pipeline.getVertexFormat().uploadImmediateIndexBuffer(builtBuffer.indexBuffer());

            indexType = builtBuffer.drawState().indexType();
        } else {
            RenderSystem.AutoStorageIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(pipeline.getVertexFormatMode());
            indices = shapeIndexBuffer.getBuffer(drawParameters.indexCount());
            indexType = shapeIndexBuffer.type();
        }

        GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms().writeTransform(RenderSystem.getModelViewMatrix(), COLOR_MODULATOR, MODEL_OFFSET, TEXTURE_MATRIX);
        try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> MeowClient.MOD_ID + " example render pipeline", client.getMainRenderTarget().getColorTextureView(), OptionalInt.empty(), client.getMainRenderTarget().getDepthTextureView(), OptionalDouble.empty())) {
            renderPass.setPipeline(pipeline);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", dynamicTransforms);

            renderPass.setVertexBuffer(0, vertices);
            renderPass.setIndexBuffer(indices, indexType);

            renderPass.drawIndexed(0,0, drawParameters.indexCount(), 1);
        }
    }

    private static void renderFilledBox(Matrix4fc positionMatrix, BufferBuilder buffer, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float red, float green, float blue, float alpha) {
        // Front Face
        buffer.addVertex(positionMatrix, minX, minY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, minY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, maxY, maxZ).setColor(red, green, blue, alpha);

        // Back face
        buffer.addVertex(positionMatrix, maxX, minY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, minY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, maxY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, maxY, minZ).setColor(red, green, blue, alpha);

        // Left face
        buffer.addVertex(positionMatrix, minX, minY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, minY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, maxY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, maxY, minZ).setColor(red, green, blue, alpha);

        // Right face
        buffer.addVertex(positionMatrix, maxX, minY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, minY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, maxY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha);

        // Top face
        buffer.addVertex(positionMatrix, minX, maxY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, maxY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, maxY, minZ).setColor(red, green, blue, alpha);

        // Bottom face
        buffer.addVertex(positionMatrix, minX, minY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, minY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, minY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, minY, maxZ).setColor(red, green, blue, alpha);
    }
}
