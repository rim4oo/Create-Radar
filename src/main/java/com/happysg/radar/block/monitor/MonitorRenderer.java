package com.happysg.radar.block.monitor;

import com.happysg.radar.registry.ModRenderTypes;
import com.jozufozu.flywheel.util.AnimationTickHolder;
import com.jozufozu.flywheel.util.Color;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class MonitorRenderer extends SmartBlockEntityRenderer<MonitorBlockEntity> {

    public MonitorRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(MonitorBlockEntity blockEntity, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        super.renderSafe(blockEntity, partialTicks, ms, bufferSource, light, overlay);
        if (!blockEntity.isController())
            return;


        Direction direction = blockEntity.getBlockState().getValue(MonitorBlock.FACING);
        ms.translate(0.5, 0.5, 0.5);
        ms.mulPose(Axis.YN.rotationDegrees(direction.toYRot()));
        ms.translate(-0.5, -0.5, -0.5);
        ms.translate(0.5, 0.5, 0.5);
        ms.mulPose(Axis.XP.rotationDegrees(90));
        ms.translate(-0.5, -0.5, -0.5);
        renderGrid(blockEntity, ms, bufferSource);
        renderBG(blockEntity, ms, bufferSource, MonitorSprite.RADAR_BG_CIRCLE);
        renderBG(blockEntity, ms, bufferSource, MonitorSprite.RADAR_BG_FILLER);
        renderSweep(blockEntity, ms, bufferSource);
    }

    private void renderGrid(MonitorBlockEntity blockEntity, PoseStack ms, MultiBufferSource bufferSource) {
        VertexConsumer buffer = bufferSource.getBuffer(ModRenderTypes.polygonOffset(MonitorSprite.GRID_SQUARE.getTexture()));
        int size = blockEntity.getSize();
        Matrix4f m = ms.last().pose();
        Matrix3f n = ms.last().normal();
        Color color = new Color(0, 255, 0);
        float alpha = 1f;
        float deptY = 0.95f;

        // Define UV coordinates
        float u0 = 0;
        float v0 = 0;
        float u1 = 1;
        float v1 = 0;
        float u2 = 1;
        float v2 = 1;
        float u3 = 0;
        float v3 = 1;

        // Increase the number of iterations to create more squares
        // Adjust this value to create more squares

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                buffer
                        .vertex(m, 1f - size + i, deptY, 1f - size + j)
                        .color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alpha)
                        .uv(u0, v0)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(255)
                        .normal(n, 0, 0, 0)
                        .endVertex();

                buffer
                        .vertex(m, 1f - size + i + 1, deptY, 1f - size + j)
                        .color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alpha)
                        .uv(u1, v1)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(255)
                        .normal(n, 0, 1, 0)
                        .endVertex();

                buffer
                        .vertex(m, 1f - size + i + 1, deptY, 1f - size + j + 1)
                        .color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alpha)
                        .uv(u2, v2)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(255)
                        .normal(n, 0, 1, 0)
                        .endVertex();

                buffer
                        .vertex(m, 1f - size + i, deptY, 1f - size + j + 1)
                        .color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alpha)
                        .uv(u3, v3)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(255)
                        .normal(n, 0, 1, 0)
                        .endVertex();
            }
        }
    }

    private void renderBG(MonitorBlockEntity blockEntity, PoseStack ms, MultiBufferSource bufferSource, MonitorSprite monitorSprite) {
        VertexConsumer buffer = bufferSource.getBuffer(ModRenderTypes.polygonOffset(monitorSprite.getTexture()));
        int size = blockEntity.getSize();
        Matrix4f m = ms.last().pose();
        Matrix3f n = ms.last().normal();
        Color color = new Color(0, 255, 0);
        float alpha = .6f;
        float deptY = 0.95f;
        float u0 = 0;
        float v0 = 0;
        float u1 = 1;
        float v1 = 0;
        float u2 = 1;
        float v2 = 1;
        float u3 = 0;
        float v3 = 1;

        buffer
                .vertex(m, 1f - size, deptY, 1f - size)
                .color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alpha)
                .uv(u0, v0)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(255)
                .normal(n, 0, 0, 0)
                .endVertex();

        buffer
                .vertex(m, 1, deptY, 1f - size)
                .color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alpha)
                .uv(u1, v1)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(255)
                .normal(n, 0, 1, 0)
                .endVertex();

        buffer
                .vertex(m, 1, deptY, 1f)
                .color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alpha)
                .uv(u2, v2)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(255)
                .normal(n, 0, 1, 0)
                .endVertex();

        buffer
                .vertex(m, 1f - size, deptY, 1f)
                .color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alpha)
                .uv(u3, v3)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(255)
                .normal(n, 0, 1, 0)
                .endVertex();


    }

    public void renderSweep(MonitorBlockEntity controller, PoseStack ms, MultiBufferSource bufferSource) {
        VertexConsumer buffer = bufferSource.getBuffer(ModRenderTypes.polygonOffset(MonitorSprite.RADAR_SWEEP.getTexture()));
        int speed = 20;


        Matrix4f m = ms.last().pose();
        Matrix3f n = ms.last().normal();
        Color color = new Color(0, 255, 0);
        float alpha = .5f;
        int timer = AnimationTickHolder.getTicks();
        float angle = (timer * speed % 360) * (float) Math.PI / 180.0f;
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);
        float centerX = 0.5f;
        float centerY = 0.5f;
        float deptY = 0.94f;

        int size = controller.getSize();

        float u0 = centerX + (0 - centerX) * cos - (0 - centerY) * sin;
        float v0 = centerY + (0 - centerX) * sin + (0 - centerY) * cos;
        float u1 = centerX + (1 - centerX) * cos - (0 - centerY) * sin;
        float v1 = centerY + (1 - centerX) * sin + (0 - centerY) * cos;
        float u2 = centerX + (1 - centerX) * cos - (1 - centerY) * sin;
        float v2 = centerY + (1 - centerX) * sin + (1 - centerY) * cos;
        float u3 = centerX + (0 - centerX) * cos - (1 - centerY) * sin;
        float v3 = centerY + (0 - centerX) * sin + (1 - centerY) * cos;

        buffer
                .vertex(m, 1f - size, deptY, 1f - size)
                .color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alpha)
                .uv(u0, v0)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(255)
                .normal(n, 0, 0, 0)
                .endVertex();

        buffer
                .vertex(m, 1, deptY, 1f - size)
                .color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alpha)
                .uv(u1, v1)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(255)
                .normal(n, 0, 1, 0)
                .endVertex();

        buffer
                .vertex(m, 1, deptY, 1f)
                .color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alpha)
                .uv(u2, v2)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(255)
                .normal(n, 0, 1, 0)
                .endVertex();

        buffer
                .vertex(m, 1f - size, deptY, 1f)
                .color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alpha)
                .uv(u3, v3)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(255)
                .normal(n, 0, 1, 0)
                .endVertex();
    }

}
