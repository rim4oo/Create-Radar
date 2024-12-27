package com.happysg.radar.compat.cbc.controller;

import com.happysg.radar.CreateRadar;
import com.happysg.radar.compat.Mods;
import com.happysg.radar.mixin.AutoCannonAccessor;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.createbigcannons.cannon_control.cannon_mount.CannonMountBlockEntity;
import rbasamoyai.createbigcannons.cannon_control.contraption.MountedAutocannonContraption;
import rbasamoyai.createbigcannons.cannon_control.contraption.PitchOrientedContraptionEntity;
import rbasamoyai.createbigcannons.cannons.ItemCannonBehavior;
import rbasamoyai.createbigcannons.cannons.autocannon.IAutocannonBlockEntity;
import rbasamoyai.createbigcannons.cannons.autocannon.breech.AbstractAutocannonBreechBlockEntity;
import rbasamoyai.createbigcannons.cannons.autocannon.material.AutocannonMaterialProperties;
import rbasamoyai.createbigcannons.config.CBCConfigs;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonAmmoItem;
import rbasamoyai.createbigcannons.munitions.autocannon.config.AutocannonProjectilePropertiesComponent;

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
        if (Mods.CREATEBIGCANNONS.isLoaded()) {
            getCannon().ifPresent(this::aimCannonAtTarget);
        }
    }

    private void aimCannonAtTarget(CannonMountBlockEntity cannon) {
        PitchOrientedContraptionEntity contraption = cannon.getContraption();
        if (contraption == null)
            return;

        if (targetYaw == 0 && targetPitch == 0) {
            stopCannon(cannon);
            return;
        }

        if (level.isClientSide)
            return;

        double currentYaw = contraption.yaw;
        double currentPitch = contraption.pitch;

        if (currentYaw == targetYaw && currentPitch == targetPitch) {
            return;
        }

        double yawDifference = targetYaw - currentYaw;
        double pitchDifference = targetPitch - currentPitch;
        double speedFactor = Math.abs(getSpeed()) / 32.0;
        double tolerance = .1; // Tolerance in degrees

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
        CreateRadar.getLogger().info("Yaw: " + currentYaw + " Pitch: " + currentPitch);
        contraption.yaw = (float) currentYaw;
        contraption.pitch = (float) currentPitch;
        cannon.setYaw((float) currentYaw);
        cannon.setPitch((float) currentPitch);
        if (currentYaw == targetYaw && currentPitch == targetPitch) {
            fireCannon(cannon);
            return;
        }
        cannon.notifyUpdate();
    }

    public float getSpeed(ServerLevel level, PitchOrientedContraptionEntity entity) {
        if (entity == null) return 0;
        if (!(entity.getContraption() instanceof MountedAutocannonContraption autocannon))
            return 0;

        if (autocannon.getStartPos() == null
                || ((AutoCannonAccessor) autocannon).getMaterial() == null
                || !(autocannon.presentBlockEntities.get(autocannon.getStartPos()) instanceof AbstractAutocannonBreechBlockEntity breech)
                || !breech.canFire()) return 0;

        ItemStack foundProjectile = breech.extractNextInput();
        if (!(foundProjectile.getItem() instanceof AutocannonAmmoItem round)) return 0;

        AutocannonMaterialProperties properties = ((AutoCannonAccessor) autocannon).getMaterial().properties();
        AutocannonProjectilePropertiesComponent roundProperties = round.getAutocannonProperties(foundProjectile);

        boolean canFail = !CBCConfigs.SERVER.failure.disableAllFailure.get();

        float speed = properties.baseSpeed();
        boolean canSquib = roundProperties == null || roundProperties.canSquib();
        canSquib &= canFail;

        BlockPos currentPos = autocannon.getStartPos().relative(autocannon.initialOrientation());
        int barrelTravelled = 0;

        while (autocannon.presentBlockEntities.get(currentPos) instanceof IAutocannonBlockEntity autocannonI) {
            ItemCannonBehavior behavior = autocannonI.cannonBehavior();

            if (behavior.canLoadItem(foundProjectile)) {
                ++barrelTravelled;
                if (barrelTravelled <= properties.maxSpeedIncreases())
                    speed += properties.speedIncreasePerBarrel();
                if (canSquib && barrelTravelled > properties.maxBarrelLength()) {
                    break;
                }
                currentPos = currentPos.relative(autocannon.initialOrientation());
            } else {
                if (canFail) {
                    return speed;
                }
            }
        }

        return speed;
    }

    private void fireCannon(CannonMountBlockEntity cannon) {
        cannon.onRedstoneUpdate(true, true, true, false, 15);
        notifyUpdate();
    }

    private void stopCannon(CannonMountBlockEntity cannon) {
        cannon.onRedstoneUpdate(true, true, false, true, 0);
        notifyUpdate();
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
        if (level.isClientSide)
            return;
        if (!Mods.CREATEBIGCANNONS.isLoaded())
            return;
        if (pos == null || pos.equals(Vec3.ZERO)) {
            targetYaw = 0;
            targetPitch = 0;
            notifyUpdate();
            return;
        }


        Vec3 cannonCenter = getBlockPos().above(3).getCenter();
        double dx = pos.x - cannonCenter.x;
        double dy = pos.y - cannonCenter.y;
        double dz = pos.z - cannonCenter.z;
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);

        targetYaw = Math.toDegrees(Math.atan2(dz, dx)) - 90;
        targetPitch = Math.toDegrees(Math.atan2(dy, horizontalDistance)) + calculatePitch(horizontalDistance);

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
        CreateRadar.getLogger().info("Yaw: " + targetYaw + " Pitch: " + targetPitch);
        notifyUpdate();
    }

    private double calculatePitch(double range) {
        if (!(level instanceof ServerLevel serverLevel))
            return 0;

        if (getCannon().isEmpty())
            return 0;

        float bestPitch = 0;
        double bestRangeDifference = Double.MAX_VALUE;
        float muzzleVelocity = getSpeed(serverLevel, getCannon().get().getContraption()) * 20;

        if (muzzleVelocity == 0)
            return 0;

        float muzzleVelocityPerTick = muzzleVelocity / 20;
        float gravity = 0.025f;
        float drag = 0.99f;
        float magicNumber = 0.0028f;

        for (float pitch = -80; pitch <= 80; pitch += 0.1F) {
            double part1 = muzzleVelocityPerTick * Math.cos(Math.toRadians(pitch)) / Math.log(drag);

            double part2 = Math.pow(((gravity * drag) /
                            (gravity * drag + (1 - drag) * muzzleVelocityPerTick * Math.sin(Math.toRadians(pitch)))),
                    (2 + magicNumber * muzzleVelocity * Math.sin(Math.toRadians(pitch)))) - 1;

            double calculatedRange = part1 * part2;

            double rangeDifference = Math.abs(range - calculatedRange);
            if (bestRangeDifference > rangeDifference) {
                bestRangeDifference = rangeDifference;
                bestPitch = pitch;
            }
        }
        return bestPitch;
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
