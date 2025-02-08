package com.happysg.radar.block.controller.firing;

import com.happysg.radar.block.controller.pitch.AutoPitchControllerBlockEntity;
import com.happysg.radar.block.controller.yaw.AutoYawControllerBlockEntity;
import com.happysg.radar.block.datalink.screens.TargetingConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.createbigcannons.cannon_control.cannon_mount.CannonMountBlockEntity;

public class FiringControlBlockEntity {

    TargetingConfig targetingConfig = TargetingConfig.DEFAULT;
    Vec3 target;
    boolean firing;
    CannonMountBlockEntity cannonMount;
    AutoPitchControllerBlockEntity pitchController;
    Level level;

    public FiringControlBlockEntity(AutoPitchControllerBlockEntity controller, CannonMountBlockEntity cannonMount) {
        this.cannonMount = cannonMount;
        this.pitchController = controller;
        this.level = cannonMount.getLevel();
    }

    public void tick() {
        if (isTargetInRange()) {
            tryFireCannon();
        } else {
            stopFireCannon();
        }
    }

    private boolean isTargetInRange() {
        if (target == null)
            return false;
        if (!hasCorrectYawPitch())
            return false;
        //todo range check
        return true;
    }

    private boolean hasCorrectYawPitch() {
        BlockPos yawControllerPos = cannonMount.getBlockPos().below();
        if (level.getBlockEntity(yawControllerPos) instanceof AutoYawControllerBlockEntity yawController && pitchController != null) {
            System.out.println("yawController: " + yawController.atTargetYaw() + " pitchController: " + pitchController.atTargetPitch());
            return yawController.atTargetYaw() && pitchController.atTargetPitch();
        }
        return false;
    }

    public void setTarget(Vec3 target, TargetingConfig config) {
        if (target == null)
            stopFireCannon();
        this.target = target;
        this.targetingConfig = config;
    }

    private void stopFireCannon() {
        cannonMount.onRedstoneUpdate(true, true, false, true, 0);
        firing = false;
    }

    private void tryFireCannon() {
        cannonMount.onRedstoneUpdate(true, true, true, false, 15);
        firing = true;
    }


}
