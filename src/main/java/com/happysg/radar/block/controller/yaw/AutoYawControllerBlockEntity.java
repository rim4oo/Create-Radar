package com.happysg.radar.block.controller.yaw;

import com.happysg.radar.CreateRadar;
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;


public class AutoYawControllerBlockEntity extends GeneratingKineticBlockEntity {
    private float targetAngle;

    public AutoYawControllerBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        if ((level.getBlockEntity(getBlockPos().above()) instanceof MechanicalBearingBlockEntity bearing))
            processBearing(bearing);


    }


    private void processBearing(MechanicalBearingBlockEntity bearing) {
        if (level.isClientSide())
            return;
        if (!bearing.isRunning())
            return;
        bearing.setAngle(targetAngle);
    }


    public void setTargetAngle(float targetAngle) {
        this.targetAngle = targetAngle;
        notifyUpdate();
    }


    public float getTargetAngle() {
        return targetAngle;
    }

    @Override
    protected void copySequenceContextFrom(KineticBlockEntity sourceBE) {
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        targetAngle = compound.getFloat("TargetAngle");
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putFloat("TargetAngle", targetAngle);
    }

    public void setTarget(Vec3 targetPos) {
        CreateRadar.getLogger().info("Setting target+ {}", targetPos);
        if (level.isClientSide())
            return;
        if (targetPos == null)
            return;

        Direction facing = getBlockState().getValue(AutoYawControllerBlock.HORIZONTAL_FACING);
        Vec3 bearingPos = Vec3.atCenterOf(getBlockPos().above());
        Vec3 diff = targetPos.subtract(bearingPos);
        double angle = Math.toDegrees(Math.atan2(diff.x, diff.z));
        angle = angle - facing.toYRot() + 180;
        setTargetAngle((float) angle);
    }

}
