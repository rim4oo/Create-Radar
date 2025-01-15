package com.happysg.radar.block.radar.link;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.RenderTypes;
import com.simibubi.create.foundation.utility.AngleHelper;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public class RadarLinkRenderer extends SafeBlockEntityRenderer<RadarLinkBlockEntity> {

    public RadarLinkRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(RadarLinkBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        long gameTime = be.getLevel().getGameTime();
        float glow = be.ledState ? (float) (0.5 * (1 + Math.sin(gameTime * 0.1))) : 0;
        if (glow < .125f)
            return;

        glow = (float) (1 - (2 * Math.pow(glow - .75f, 2)));
        glow = Mth.clamp(glow, -1, 1);

        int color = (int) (200 * glow);

        BlockState blockState = be.getBlockState();
        TransformStack msr = TransformStack.cast(ms);

        Direction face = blockState.getOptionalValue(RadarLinkBlock.FACING)
                .orElse(Direction.UP);

        if (face.getAxis()
                .isHorizontal())
            face = face.getOpposite();

        ms.pushPose();

        msr.centre()
                .rotateY(AngleHelper.horizontalAngle(face))
                .rotateX(-AngleHelper.verticalAngle(face) - 90)
                .unCentre();

        CachedBufferer.partial(AllPartialModels.DISPLAY_LINK_TUBE, blockState)
                .light(LightTexture.FULL_BRIGHT)
                .renderInto(ms, buffer.getBuffer(RenderType.translucent()));

        CachedBufferer.partial(AllPartialModels.DISPLAY_LINK_GLOW, blockState)
                .light(LightTexture.FULL_BRIGHT)
                .color(color, color, color, 255)
                .disableDiffuse()
                .renderInto(ms, buffer.getBuffer(RenderTypes.getAdditive()));

        ms.popPose();
    }

}
