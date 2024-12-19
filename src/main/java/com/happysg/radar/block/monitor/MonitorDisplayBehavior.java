package com.happysg.radar.block.monitor;

import com.happysg.radar.block.radar.bearing.RadarBearingBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTarget;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class MonitorDisplayBehavior extends DisplayTarget {
    @Override
    public void acceptText(int line, List<MutableComponent> text, DisplayLinkContext context) {
        if (!(context.getTargetBlockEntity() instanceof MonitorBlockEntity monitor) || !(context.getSourceBlockEntity() instanceof RadarBearingBlockEntity radar))
            return;

        if (!radar.isRunning())
            return;
        MonitorFilter filter = MonitorFilter.values()[context.sourceConfig().getInt("Filter")];
        monitor.getController().setFilter(filter);
    }

    @Override
    public AABB getMultiblockBounds(LevelAccessor level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof MonitorBlockEntity monitor) {
            return monitor.getMultiblockBounds(level, pos);
        }
        return super.getMultiblockBounds(level, pos);
    }

    @Override
    public DisplayTargetStats provideStats(DisplayLinkContext context) {
        return new DisplayTargetStats(1, 1, this);
    }

}
