package com.happysg.radar.compat.cbc;

import com.happysg.radar.block.monitor.MonitorBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.DisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTarget;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.createbigcannons.cannon_control.cannon_mount.CannonMountBlockEntity;
import rbasamoyai.createbigcannons.cannon_control.contraption.PitchOrientedContraptionEntity;

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
        if (!(context.getSourceBlockEntity() instanceof CannonMountBlockEntity turret))
            return;

        PitchOrientedContraptionEntity contraptionEntity = turret.getContraption();
        if (contraptionEntity == null)
            return;

        if (!(context.getTargetBlockEntity() instanceof MonitorBlockEntity))
            return;

        MonitorBlockEntity monitor = ((MonitorBlockEntity) context.getTargetBlockEntity()).getController();
        Vec3 targetPos = monitor.getTargetPos();
        if (targetPos == null)
            return;

        Vec3 turretPos = turret.getBlockPos().above(2).getCenter();

        double dx = targetPos.x() - turretPos.x;
        double dy = targetPos.y() - turretPos.y;
        double dz = targetPos.z() - turretPos.z;


        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        double newYaw = Math.toDegrees(Math.atan2(dz, dx)) - 90; // Subtracting 90 to align with the game's coordinate system
        double newPitch = Math.toDegrees(Math.atan2(dy, horizontalDistance));


        // Ensure pitch is within -90 to 90 degrees
        if (newPitch < -90) {
            newPitch = -90;
        } else if (newPitch > 90) {
            newPitch = 90;
        }


        contraptionEntity.yaw = (float) newYaw;
        contraptionEntity.pitch = (float) newPitch;
        turret.setYaw((float) newYaw);
        turret.setPitch((float) newPitch);
        turret.notifyUpdate();

    }


}
