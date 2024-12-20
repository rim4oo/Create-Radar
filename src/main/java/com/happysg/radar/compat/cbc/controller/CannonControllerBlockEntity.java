package com.happysg.radar.compat.cbc.controller;

import com.happysg.radar.compat.Mods;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.createbigcannons.cannon_control.cannon_mount.CannonMountBlockEntity;
import rbasamoyai.createbigcannons.cannon_control.contraption.PitchOrientedContraptionEntity;

import java.util.Optional;

public class CannonControllerBlockEntity extends KineticBlockEntity {

    double targetYaw;
    double targetPitch;

    public CannonControllerBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        if (getSpeed() == 0)
            return;
        if (Mods.CREATEBIGCANNONS.isLoaded())
            getCannon().ifPresent(this::aimCannonAtTarget);
    }

    private void aimCannonAtTarget(CannonMountBlockEntity cannon) {
        PitchOrientedContraptionEntity contraption = cannon.getContraption();
        if (contraption == null)
            return;

        if (targetYaw == 0 && targetPitch == 0)
            return;

        if (level.isClientSide)
            return;

        double currentYaw = contraption.yaw;
        double currentPitch = contraption.pitch;

        if (currentYaw == targetYaw && currentPitch == targetPitch)
            return;

        double yawDifference = targetYaw - currentYaw;
        double pitchDifference = targetPitch - currentPitch;
        double speedFactor = Math.abs(getSpeed()) / 32.0;
        double tolerance = 2; // Tolerance in degrees

        if (Math.abs(yawDifference) > tolerance) {
            if (Math.abs(yawDifference) > speedFactor) {
                currentYaw += Math.signum(yawDifference) * speedFactor;
            } else {
                currentYaw = targetYaw;
            }
        } else {
            currentYaw = targetYaw;
        }

        //todo can't get pitch to work
        currentPitch = targetPitch;

        contraption.yaw = (float) currentYaw;
        contraption.pitch = (float) currentPitch;
        cannon.setYaw((float) currentYaw);
        cannon.setPitch((float) currentPitch);
        cannon.notifyUpdate();
    }

    private Optional<CannonMountBlockEntity> getCannon() {
        BlockEntity be = level.getBlockEntity(getBlockPos().above());
        if (be == null)
            return Optional.empty();
        if (be instanceof CannonMountBlockEntity)
            return Optional.of((CannonMountBlockEntity) be);
        return Optional.empty();
    }

    public void setTarget(Vec3 pos) {
        if (pos == null)
            return;


        Vec3 cannonCenter = getBlockPos().above(2).getCenter();
        double dx = pos.x - cannonCenter.x;
        double dy = pos.y - cannonCenter.y;
        double dz = pos.z - cannonCenter.z;
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);

        targetYaw = Math.toDegrees(Math.atan2(dz, dx)) - 90;
        targetPitch = Math.toDegrees(Math.atan2(dy, horizontalDistance));

        // Normalize yaw to 0-360 degrees
        if (targetYaw < 0) {
            targetYaw += 360;
        }

        // Ensure pitch is within -90 to 90 degrees
        if (targetPitch < -90) {
            targetPitch = -90;
        } else if (targetPitch > 90) {
            targetPitch = 90;
        }
        notifyUpdate();
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        targetYaw = compound.getDouble("targetYaw");
        targetPitch = compound.getDouble("targetPitch");
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putDouble("targetYaw", targetYaw);
        compound.putDouble("targetPitch", targetPitch);
    }
}
