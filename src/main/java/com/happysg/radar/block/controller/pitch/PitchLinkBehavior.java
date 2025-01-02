package com.happysg.radar.block.controller.pitch;

import com.happysg.radar.block.monitor.MonitorBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.DisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTarget;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class PitchLinkBehavior extends DisplaySource {

    @Override
    public List<MutableComponent> provideText(DisplayLinkContext context, DisplayTargetStats stats) {
        return List.of();
    }

    @Override
    public int getPassiveRefreshTicks() {
        return 1;
    }

    @Override
    public void transferData(DisplayLinkContext context, DisplayTarget activeTarget, int line) {
        super.transferData(context, activeTarget, line);
        if (!(context.getSourceBlockEntity() instanceof AutoPitchControllerBlockEntity controller))
            return;

        if (!(context.getTargetBlockEntity() instanceof MonitorBlockEntity))
            return;

        MonitorBlockEntity monitor = ((MonitorBlockEntity) context.getTargetBlockEntity()).getController();

        if (monitor == null)
            return;

        Vec3 targetPos = monitor.getTargetPos();
        controller.setTarget(targetPos);
    }
}
