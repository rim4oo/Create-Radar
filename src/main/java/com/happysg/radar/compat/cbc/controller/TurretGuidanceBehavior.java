package com.happysg.radar.compat.cbc.controller;

import com.happysg.radar.block.monitor.MonitorBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.DisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTarget;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class TurretGuidanceBehavior extends DisplaySource {

    @Override
    public List<MutableComponent> provideText(DisplayLinkContext context, DisplayTargetStats stats) {
        return List.of(Component.literal(" "));
    }

    @Override
    public int getPassiveRefreshTicks() {
        return 1;
    }

    @Override
    public void transferData(DisplayLinkContext context, DisplayTarget activeTarget, int line) {
        super.transferData(context, activeTarget, line);
        if (!(context.getSourceBlockEntity() instanceof CannonControllerBlockEntity controller))
            return;

        if (!(context.getTargetBlockEntity() instanceof MonitorBlockEntity))
            return;

        MonitorBlockEntity monitor = ((MonitorBlockEntity) context.getTargetBlockEntity()).getController();
        Vec3 targetPos = monitor.getTargetPos();
        if (targetPos == null)
            return;

        controller.setTarget(targetPos);
    }


}
