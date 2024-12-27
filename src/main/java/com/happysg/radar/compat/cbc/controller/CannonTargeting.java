package com.happysg.radar.compat.cbc.controller;

import net.minecraft.world.phys.Vec3;

public class CannonTargeting {

    // Constants for drag and gravity
    private static final double DRAG = 0.99;
    private static final double GRAVITY = -0.05;

    // Increment for pitch in degrees
    private static final double PITCH_INCREMENT = 0.125;

    // Calculate the optimal pitch to hit the target
    public static double calculatePitch(double power, double length, Vec3 cannonPos, Vec3 targetPos) {
        // Calculate deltas
        double dX = targetPos.x - cannonPos.x;
        double dZ = targetPos.z - cannonPos.z;
        double dY = targetPos.y - cannonPos.y;

        // Calculate distance
        double distance = Math.sqrt(dX * dX + dZ * dZ);

        double bestPitch = 0.0;
        double bestAccuracy = Double.MAX_VALUE;
        double pitch = -60.0;  // Start with the minimum pitch angle

        while (pitch <= 60.0) {
            // Calculate airtime for upward and downward motion
            double airtimeUp = computeAirtimeUp(power, pitch, dY, length);
            double airtimeDown = computeAirtimeDown(power, pitch, dY, length);

            // Check the accuracy for upward motion
            if (airtimeUp != -1) {
                double projection = computeProjection(power, pitch, airtimeUp);
                double accuracy = Math.abs(projection - distance);
                if (accuracy < bestAccuracy) {
                    bestAccuracy = accuracy;
                    bestPitch = pitch;
                }
            }

            // Check the accuracy for downward motion
            if (airtimeDown != -1) {
                double projection = computeProjection(power, pitch, airtimeDown);
                double accuracy = Math.abs(projection - distance);
                if (accuracy < bestAccuracy) {
                    bestAccuracy = accuracy;
                    bestPitch = pitch;
                }
            }

            // Increment pitch
            pitch += PITCH_INCREMENT;
        }

        return bestPitch;
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