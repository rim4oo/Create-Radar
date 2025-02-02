package com.happysg.radar.block.radar.behavior;

import com.happysg.radar.block.monitor.MonitorBlockEntity;
import com.happysg.radar.block.monitor.MonitorFilter;
import com.happysg.radar.block.radar.link.RadarLinkBlockEntity;
import com.happysg.radar.block.radar.link.RadarLinkContext;
import com.happysg.radar.block.radar.link.RadarSource;
import com.happysg.radar.block.radar.link.RadarTarget;
import com.happysg.radar.block.radar.link.screens.AbstractRadarLinkScreen;
import com.happysg.radar.block.radar.link.screens.RadarFilterScreen;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class RadarScannerLinkBehavior extends RadarSource {

    public void transferData(@NotNull RadarLinkContext context, @NotNull RadarTarget activeTarget) {
        if (context.level().isClientSide())
            return;
        if (context.getSourceBlockEntity() instanceof SmartBlockEntity smartBlockEntity) {
            RadarScanningBlockBehavior behavior = smartBlockEntity.getBehaviour(RadarScanningBlockBehavior.TYPE);
            if (behavior != null) {
                if (context.getTargetBlockEntity() instanceof MonitorBlockEntity monitorBlockEntity) {
                    monitorBlockEntity.getController().setRadarPos(context.getSourcePos());
                    if (context.sourceConfig().contains("filter")) {
                        monitorBlockEntity.setFilter(MonitorFilter.fromTag(context.sourceConfig().getCompound("filter")));
                    }
                    monitorBlockEntity.getController().updateCache();
                }
            }
        }
    }


    @OnlyIn(value = Dist.CLIENT)
    @Override
    protected AbstractRadarLinkScreen getScreen(RadarLinkBlockEntity be) {
        return new RadarFilterScreen(be);
    }
}
