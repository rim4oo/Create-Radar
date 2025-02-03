package com.happysg.radar.block.controller.track;

import com.happysg.radar.compat.Mods;
import com.happysg.radar.compat.vs2.VS2Utils;
import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniondc;
import org.valkyrienskies.core.api.ships.LoadedShip;

import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;

public class LLMTrackControllerBlockEntity extends SplitShaftBlockEntity {

    private Vec3 target;
    private float leftSpeed = 0;
    private float rightSpeed = 0;
    private float lastLeftSpeed = 0;
    private float lastRightSpeed = 0;

    public LLMTrackControllerBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public float getRotationSpeedModifier(Direction face) {
        if (hasSource() && face == getSourceFacing()) return 1f;
        if (target == null) return 0;
        return switch (face) {
            case NORTH, SOUTH -> 1f;
            case WEST -> leftSpeed;
            case EAST -> rightSpeed;
            default -> 1f;
        };
    }

    @Override
    public Direction getSourceFacing() {
        return getBlockState().getValue(HORIZONTAL_FACING).getOpposite();
    }

    @Override
    public void tick() {
        super.tick();
        float newLeftSpeed = 0;
        float newRightSpeed = 0;

        if (Math.abs(getAngleDifference()) >= 20) {
            if (getAngleDifference() < 0) {
                newLeftSpeed = 1;
            } else {
                newRightSpeed = 1;
            }
        }

        // Only update if movement actually changes
        if (newLeftSpeed != lastLeftSpeed || newRightSpeed != lastRightSpeed) {
            leftSpeed = newLeftSpeed;
            rightSpeed = newRightSpeed;
            lastLeftSpeed = newLeftSpeed;
            lastRightSpeed = newRightSpeed;

            detachKinetics();
            attachKinetics();
            updateSpeed = true;
            notifyUpdate();
        }
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        leftSpeed = compound.getFloat("leftSpeed");
        rightSpeed = compound.getFloat("rightSpeed");
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putFloat("leftSpeed", leftSpeed);
        compound.putFloat("rightSpeed", rightSpeed);
    }

    private double getAngleDifference() {
        double diff = getAngleToTarget() - getYaw();
        return ((diff + 540) % 360) - 180; // Normalize to [-180, 180]
    }

    private double getAngleToTarget() {
        if (target == null) return getYaw();
        Vec3 center = VS2Utils.getWorldVec(this);
        Vec3 relative = target.subtract(center); // Correct order
        double yaw = Math.toDegrees(Math.atan2(relative.z, relative.x)) - 90;
        return (yaw + 360) % 360; // Normalize angle
    }

    private double getYaw() {
        return ((toYRot() - getAngleOffsetToWorld() + 360)) % 360;
    }

    private double getAngleOffsetToWorld() {
        if (!Mods.VALKYRIENSKIES.isLoaded()) return 0;

        LoadedShip ship = VS2Utils.getShipManagingPos(this);
        if (ship == null) return 0;

        Quaterniondc quaternion = ship.getTransform().getShipToWorldRotation();

        // Extract yaw using quaternion math
        double yaw = Math.toDegrees(Math.atan2(
                2.0 * (quaternion.w() * quaternion.y() + quaternion.x() * quaternion.z()),
                1.0 - 2.0 * (quaternion.y() * quaternion.y() + quaternion.z() * quaternion.z())
        ));

        return (yaw + 360) % 360; // Normalize to [0, 360]
    }

    private static final int[] DIRECTION_TO_ANGLE = {0, 0, 0, 180, 270, 90}; // N, S, W, E

    private double toYRot() {
        return DIRECTION_TO_ANGLE[getBlockState().getValue(HORIZONTAL_FACING).ordinal()];
    }

    public void setTarget(Vec3 targetPos) {
        this.target = targetPos;
    }

    public Vec3 getTarget() {
        return target;
    }
}
