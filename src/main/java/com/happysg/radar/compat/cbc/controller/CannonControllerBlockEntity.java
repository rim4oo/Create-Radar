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

import java.util.Optional;

public class CannonControllerBlockEntity extends KineticBlockEntity {

    Vec3 requestedTarget;
    Vec3 currentTarget;

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

        if (requestedTarget == null || requestedTarget.equals(Vec3.ZERO))
            return;

        if (currentTarget != null && currentTarget.equals(requestedTarget))
            return;

        if (cannon.getContraption() == null)
            return;

        Vec3 cannonCenter = getBlockPos().above(2).getCenter();
        double dx = requestedTarget.x - cannonCenter.x;
        double dy = requestedTarget.y - cannonCenter.y;
        double dz = requestedTarget.z - cannonCenter.z;
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);

        double newYaw = Math.toDegrees(Math.atan2(dz, dx)) - 90;
        double newPitch = Math.toDegrees(Math.atan2(dy, horizontalDistance));

        // Normalize yaw to 0-360 degrees
        if (newYaw < 0) {
            newYaw += 360;
        }

        // Ensure pitch is within -90 to 90 degrees
        if (newPitch < -90) {
            newPitch = -90;
        } else if (newPitch > 90) {
            newPitch = 90;
        }


        System.out.println("Cannon: " + cannonCenter.x + ", " + cannonCenter.y + ", " + cannonCenter.z);
        System.out.println("Target: " + requestedTarget.x + ", " + requestedTarget.y + ", " + requestedTarget.z());
        System.out.println("Yaw: " + newYaw);
        System.out.println("Pitch: " + newPitch);
        cannon.setYaw((float) newYaw);
        cannon.setPitch((float) newPitch);
        cannon.getContraption().yaw = (float) newYaw;
        cannon.getContraption().pitch = (float) newPitch;
        currentTarget = requestedTarget;
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
        if (requestedTarget != null && requestedTarget.equals(pos))
            return;
        this.requestedTarget = pos;
        notifyUpdate();
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        if (compound.contains("targetx")) {
            requestedTarget = new Vec3(compound.getDouble("targetx"), compound.getDouble("targety"), compound.getDouble("targetz"));
        }
        if (compound.contains("currentx")) {
            currentTarget = new Vec3(compound.getDouble("currentx"), compound.getDouble("currenty"), compound.getDouble("currentz"));
        }

    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        if (requestedTarget != null) {
            compound.putDouble("targetx", requestedTarget.x);
            compound.putDouble("targety", requestedTarget.y);
            compound.putDouble("targetz", requestedTarget.z);
        }
        if (currentTarget != null) {
            compound.putDouble("currentx", currentTarget.x);
            compound.putDouble("currenty", currentTarget.y);
            compound.putDouble("currentz", currentTarget.z);
        }
    }
}
