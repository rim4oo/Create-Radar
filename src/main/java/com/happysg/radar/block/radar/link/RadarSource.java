package com.happysg.radar.block.radar.link;

import com.happysg.radar.block.radar.link.screens.AbstractRadarLinkScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public abstract class RadarSource extends RadarLinkBehavior {

    @Nullable
    @OnlyIn(value = Dist.CLIENT)
    protected abstract AbstractRadarLinkScreen getScreen(RadarLinkBlockEntity be);

    protected abstract void transferData(@NotNull RadarLinkContext context, @NotNull RadarTarget activeTarget);
}
