package com.happysg.radar.compat.cbc;

import com.happysg.radar.CreateRadar;
import com.happysg.radar.mixin.AutoCannonAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.createbigcannons.cannon_control.contraption.MountedAutocannonContraption;
import rbasamoyai.createbigcannons.cannon_control.contraption.PitchOrientedContraptionEntity;
import rbasamoyai.createbigcannons.cannons.ItemCannonBehavior;
import rbasamoyai.createbigcannons.cannons.autocannon.IAutocannonBlockEntity;
import rbasamoyai.createbigcannons.cannons.autocannon.breech.AbstractAutocannonBreechBlockEntity;
import rbasamoyai.createbigcannons.cannons.autocannon.material.AutocannonMaterialProperties;
import rbasamoyai.createbigcannons.config.CBCConfigs;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonAmmoItem;
import rbasamoyai.createbigcannons.munitions.autocannon.config.AutocannonProjectilePropertiesComponent;

//todo fix calculations
public class CannonTargeting {

    // Constants for drag and gravity
    private static final double DRAG = 0.99;
    private static final double GRAVITY = -0.05;


    // Calculate the optimal pitch to hit the target
    public static double calculatePitch(double power, double length, Vec3 cannonPos, Vec3 targetPos) {
        double dX = targetPos.x - cannonPos.x;
        double dZ = targetPos.z - cannonPos.z;
        double dY = targetPos.y - cannonPos.y;
        double distance = Math.sqrt(dX * dX + dZ * dZ);

        double bestPitch = 0.0;
        double bestAccuracy = Double.MAX_VALUE;
        double pitch = -80.0;
        double pitchIncrement = 0.01; // Increased resolution

        while (pitch <= 80.0) {
            double airtimeUp = computeAirtimeUp(power, pitch, dY, length);
            double airtimeDown = computeAirtimeDown(power, pitch, dY, length);

            if (airtimeUp != -1) {
                double projection = computeProjection(power, pitch, airtimeUp);
                double accuracy = Math.abs(projection - distance);
                if (accuracy < bestAccuracy) {
                    bestAccuracy = accuracy;
                    bestPitch = pitch;
                }
            }

            if (airtimeDown != -1) {
                double projection = computeProjection(power, pitch, airtimeDown);
                double accuracy = Math.abs(projection - distance);
                if (accuracy < bestAccuracy) {
                    bestAccuracy = accuracy;
                    bestPitch = pitch;
                }
            }

            pitch += pitchIncrement;
        }

        // Secondary refinement around the best pitch
        double refinedBestPitch = bestPitch;
        bestAccuracy = Double.MAX_VALUE;
        pitch = bestPitch - pitchIncrement;
        while (pitch <= bestPitch + pitchIncrement) {
            double airtimeUp = computeAirtimeUp(power, pitch, dY, length);
            double airtimeDown = computeAirtimeDown(power, pitch, dY, length);

            if (airtimeUp != -1) {
                double projection = computeProjection(power, pitch, airtimeUp);
                double accuracy = Math.abs(projection - distance);
                if (accuracy < bestAccuracy) {
                    bestAccuracy = accuracy;
                    refinedBestPitch = pitch;
                }
            }

            if (airtimeDown != -1) {
                double projection = computeProjection(power, pitch, airtimeDown);
                double accuracy = Math.abs(projection - distance);
                if (accuracy < bestAccuracy) {
                    bestAccuracy = accuracy;
                    refinedBestPitch = pitch;
                }
            }

            pitch += pitchIncrement / 10; // Finer increment for refinement
        }

        CreateRadar.getLogger().info("Best pitch: " + refinedBestPitch);
        return refinedBestPitch;
    }
    // Calculate airtime for upward motion
    private static double computeAirtimeUp(double power, double pitch, double dY, double length) {
        double vertVelocity = power * Math.sin(Math.toRadians(pitch));
        double vertPosition = 0.0;
        dY -= length * Math.sin(Math.toRadians(pitch));  // Adjust target height based on barrel length

        if (dY < 0) return -1;

        double ticks = 0;
        while (true) {
            ticks++;
            vertPosition += vertVelocity;
            vertVelocity = vertVelocity * DRAG + GRAVITY;
            if (vertPosition > dY) return ticks;
            if (vertVelocity < 0) break;  // Stop if velocity is downward before reaching the target
        }

        return -1;
    }

    public static float getSpeed(PitchOrientedContraptionEntity entity) {
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
        System.out.println("Speed: " + speed);
        System.out.println("barrelTravelled: " + barrelTravelled);
        return speed;
    }


    // Calculate airtime for downward motion
    private static double computeAirtimeDown(double power, double pitch, double dY, double length) {
        double vertVelocity = power * Math.sin(Math.toRadians(pitch));
        double vertPosition = 0.0;
        dY -= length * Math.sin(Math.toRadians(pitch));

        double ticks = 0;
        boolean passed = false;

        while (true) {
            ticks++;
            vertPosition += vertVelocity;
            vertVelocity = vertVelocity * DRAG + GRAVITY;

            if (!passed && vertVelocity < 0) break;
            if (vertVelocity > 0 && vertPosition > dY) passed = true;
            if (passed && vertVelocity < 0 && vertPosition < dY) return ticks;
        }

        return -1;
    }

    // Calculate the horizontal projection
    private static double computeProjection(double power, double pitch, double airtime) {
        double horizVelocity = power * Math.cos(Math.toRadians(pitch));
        double horizPosition = 0.0;

        for (int ticks = 1; ticks <= airtime; ticks++) {
            horizPosition += horizVelocity;
            horizVelocity *= DRAG;
        }

        return horizPosition;
    }
}