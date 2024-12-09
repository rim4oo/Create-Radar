package com.happysg.radar.block.monitor;

import com.happysg.radar.block.radar.bearing.RadarBearingBlockEntity;
import com.happysg.radar.block.radar.bearing.RadarTrack;
import com.happysg.radar.registry.ModRenderTypes;
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

import java.util.List;

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
        blockEntity.getRadar().ifPresent(radar -> {
            if (!radar.isRunning())
                return;
            // renderGrid(blockEntity, ms, bufferSource);
            renderBG(blockEntity, ms, bufferSource, MonitorSprite.RADAR_BG_FILLER);
            renderBG(blockEntity, ms, bufferSource, MonitorSprite.RADAR_BG_CIRCLE);
            renderSweep(radar, blockEntity, ms, bufferSource);
            renderRadarTracks(radar, blockEntity, ms, bufferSource);
        });
    }

    private void renderRadarTracks(RadarBearingBlockEntity radar, MonitorBlockEntity blockEntity, PoseStack ms, MultiBufferSource bufferSource) {
        List<RadarTrack> tracks = radar.getEntityPositions();
        for (RadarTrack track : tracks) {
            renderTrack(track, blockEntity, ms, bufferSource);
        }
    }

    private void renderTrack(RadarTrack track, MonitorBlockEntity blockEntity, PoseStack ms, MultiBufferSource bufferSource) {
        VertexConsumer buffer = bufferSource.getBuffer(ModRenderTypes.polygonOffset(MonitorSprite.ENTITY_HITBOX.getTexture()));
        Matrix4f m = ms.last().pose();
        Matrix3f n = ms.last().normal();
        Color color = new Color(0, 0, 255);
        float alpha = 0.5f;
        float deptY = 0.97f;
        float size = blockEntity.getSize();
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
                .color(color.getRedAsFloat(), color.getGreenAsFloat(), color.getBlueAsFloat(), alpha)
                .uv(u0, v0)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(255)
                .normal(n, 0, 0, 0)
                .endVertex();

        buffer
                .vertex(m, 1, deptY, 1f - size)
                .color(color.getRedAsFloat(), color.getGreenAsFloat(), color.getBlueAsFloat(), alpha)
                .uv(u1, v1)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(255)
                .normal(n, 0, 1, 0)
                .endVertex();

        buffer
                .vertex(m, 1, deptY, 1f)
                .color(color.getRedAsFloat(), color.getGreenAsFloat(), color.getBlueAsFloat(), alpha)
                .uv(u2, v2)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(255)
                .normal(n, 0, 1, 0)
                .endVertex();

        buffer
                .vertex(m, 1f - size, deptY, 1f)
                .color(color.getRedAsFloat(), color.getGreenAsFloat(), color.getBlueAsFloat(), alpha)
                .uv(u3, v3)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(255)
                .normal(n, 0, 1, 0)
                .endVertex();


    }

    private void renderGrid(MonitorBlockEntity blockEntity, PoseStack ms, MultiBufferSource bufferSource) {
        VertexConsumer buffer = bufferSource.getBuffer(ModRenderTypes.polygonOffset(MonitorSprite.GRID_SQUARE.getTexture()));
        int size = blockEntity.getSize();
        Matrix4f m = ms.last().pose();
        Matrix3f n = ms.last().normal();
        Color color = new Color(0, 255, 0);
        float alpha = 1f;
        float deptY = 0.95f;

        float u0 = 0;
        float v0 = 0;
        float u1 = 1;
        float v1 = 0;
        float u2 = 1;
        float v2 = 1;
        float u3 = 0;
        float v3 = 1;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                buffer
                        .vertex(m, 1f - size + i, deptY, 1f - size + j)
                        .color(color.getRedAsFloat(), color.getGreenAsFloat(), color.getBlueAsFloat(), alpha)
                        .uv(u0, v0)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(255)
                        .normal(n, 0, 0, 0)
                        .endVertex();

                buffer
                        .vertex(m, 1f - size + i + 1, deptY, 1f - size + j)
                        .color(color.getRedAsFloat(), color.getGreenAsFloat(), color.getBlueAsFloat(), alpha)
                        .uv(u1, v1)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(255)
                        .normal(n, 0, 1, 0)
                        .endVertex();

                buffer
                        .vertex(m, 1f - size + i + 1, deptY, 1f - size + j + 1)
                        .color(color.getRedAsFloat(), color.getGreenAsFloat(), color.getBlueAsFloat(), alpha)
                        .uv(u2, v2)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(255)
                        .normal(n, 0, 1, 0)
                        .endVertex();

                buffer
                        .vertex(m, 1f - size + i, deptY, 1f - size + j + 1)
                        .color(color.getRedAsFloat(), color.getGreenAsFloat(), color.getBlueAsFloat(), alpha)
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
                .color(color.getRedAsFloat(), color.getGreenAsFloat(), color.getBlueAsFloat(), alpha)
                .uv(u0, v0)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(255)
                .normal(n, 0, 0, 0)
                .endVertex();

        buffer
                .vertex(m, 1, deptY, 1f - size)
                .color(color.getRedAsFloat(), color.getGreenAsFloat(), color.getBlueAsFloat(), alpha)
                .uv(u1, v1)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(255)
                .normal(n, 0, 1, 0)
                .endVertex();

        buffer
                .vertex(m, 1, deptY, 1f)
                .color(color.getRedAsFloat(), color.getGreenAsFloat(), color.getBlueAsFloat(), alpha)
                .uv(u2, v2)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(255)
                .normal(n, 0, 1, 0)
                .endVertex();

        buffer
                .vertex(m, 1f - size, deptY, 1f)
                .color(color.getRedAsFloat(), color.getGreenAsFloat(), color.getBlueAsFloat(), alpha)
                .uv(u3, v3)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(255)
                .normal(n, 0, 1, 0)
                .endVertex();


    }

    public void renderSweep(RadarBearingBlockEntity radar, MonitorBlockEntity controller, PoseStack ms, MultiBufferSource bufferSource) {
        if (!radar.isRunning())
            return;
        VertexConsumer buffer = bufferSource.getBuffer(ModRenderTypes.polygonOffset(MonitorSprite.RADAR_SWEEP.getTexture()));
        Matrix4f m = ms.last().pose();
        Matrix3f n = ms.last().normal();
        Color color = new Color(0, 255, 0);
        float alpha = 0.5f;
        Direction monitorFacing = controller.getBlockState().getValue(MonitorBlock.FACING);
        Direction radarFacing = radar.getReceiverFacing();
        float angleDiff = monitorFacing.toYRot() - radarFacing.toYRot();
        float angle = ((radar.getAngle() + angleDiff) % 360.0f) * (float) Math.PI / 180.0f;
        // System.out.println(angle);
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);

        //center to change when panning and partial rendering added
        float centerX = 0.5f;
        float centerY = 0.5f;
        float deptY = .96f;

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
                .color(color.getRedAsFloat(), color.getGreenAsFloat(), color.getBlueAsFloat(), alpha)
                .uv(u0, v0)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(255)
                .normal(n, 0, 0, 0)
                .endVertex();

        buffer
                .vertex(m, 1, deptY, 1f - size)
                .color(color.getRedAsFloat(), color.getGreenAsFloat(), color.getBlueAsFloat(), alpha)
                .uv(u1, v1)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(255)
                .normal(n, 0, 1, 0)
                .endVertex();

        buffer
                .vertex(m, 1, deptY, 1f)
                .color(color.getRedAsFloat(), color.getGreenAsFloat(), color.getBlueAsFloat(), alpha)
                .uv(u2, v2)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(255)
                .normal(n, 0, 1, 0)
                .endVertex();

        buffer
                .vertex(m, 1f - size, deptY, 1f)
                .color(color.getRedAsFloat(), color.getGreenAsFloat(), color.getBlueAsFloat(), alpha)
                .uv(u3, v3)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(255)
                .normal(n, 0, 1, 0)
                .endVertex();
    }

}
