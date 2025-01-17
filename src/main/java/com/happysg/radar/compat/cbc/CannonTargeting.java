package com.happysg.radar.compat.cbc;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.math3.analysis.solvers.BisectionSolver;
import org.apache.commons.math3.analysis.solvers.BrentSolver;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.NewtonRaphsonSolver;
import org.apache.commons.math3.analysis.solvers.SecantSolver;
import org.apache.commons.math3.exception.NoBracketingException;
import rbasamoyai.createbigcannons.cannon_control.cannon_mount.CannonMountBlockEntity;
import rbasamoyai.createbigcannons.cannon_control.contraption.AbstractMountedCannonContraption;
import rbasamoyai.createbigcannons.cannon_control.contraption.PitchOrientedContraptionEntity;

import static java.lang.Double.NaN;

//todo fix calculations
public class CannonTargeting {

    // Constants for drag and gravity
    private static final double DRAG = 0.99;


    // Calculate the optimal pitch to hit the target

    public static double calculatePitch(double chargePower, Vec3 targetPos, Vec3 mountPos, int barrelLength, double drag, double gravity) {
        double xTarget = targetPos.x - mountPos.x;
        double yTarget = targetPos.y - mountPos.y;

        double g = Math.abs(gravity);
        double k = 1-drag;
        double bestDiff = Double.MAX_VALUE;
        double bestTheta=0;
        double theta = Math.toRadians(-90);
        while(theta<=Math.toRadians(90)) {
            double v0x = chargePower * Math.cos(theta);
            double v0y = chargePower * Math.sin(theta);
            double xTargetLocal = xTarget - Math.cos(theta) * (barrelLength + 0.5);
            double yTargetLocal = yTarget - Math.sin(theta) * (barrelLength + 0.5);

            // Calculate y when x at target
            double log = Math.log(1 - ((k * xTargetLocal) / (v0x*Math.cos(theta))));

            double y = (Math.tan(theta)+(g/(k*v0x*Math.cos(theta))*xTargetLocal + (g / (k * k))*log));
            if (!Double.isNaN(y)){
                double diff = y - yTargetLocal;
                if (Math.abs(diff) < Math.abs(bestDiff)) {
                    bestDiff = diff;
                    bestTheta = theta;
                }
            }
            theta+=Math.toRadians(0.1);
        }
        return bestTheta/Math.PI*180;
        }


    // Calculate airtime for upward motion
    private static double computeAirtimeUp(double power, double pitch, double dY, double length, double gravity) {
        double vertVelocity = power * Math.sin(Math.toRadians(pitch));
        double vertPosition = 0.0;
        dY -= length * Math.sin(Math.toRadians(pitch));  // Adjust target height based on barrel length

        if (dY < 0) return -1;

        double ticks = 0;
        while (true) {
            ticks++;
            vertPosition += vertVelocity;
            vertVelocity = vertVelocity * DRAG + gravity;
            if (vertPosition > dY) return ticks;
            if (vertVelocity < 0) break;  // Stop if velocity is downward before reaching the target
        }

        return -1;
    }

    // Calculate airtime for downward motion
    private static double computeAirtimeDown(double power, double pitch, double dY, double length, double gravity) {
        double vertVelocity = power * Math.sin(Math.toRadians(pitch));
        double vertPosition = 0.0;
        dY -= length * Math.sin(Math.toRadians(pitch));

        boolean passed = false;
        double ticks = 0;

        while (true) {
            ticks++;
            vertPosition += vertVelocity;
            vertVelocity = vertVelocity * DRAG + gravity;

            if(vertPosition > dY && vertVelocity < 0) passed = true;
            if(vertVelocity < 0 && vertPosition < dY && passed) return ticks;
            if(vertPosition < dY && vertVelocity < 0 ) break;

        }

        return -1;
    }
    // Calculate the horizontal projection
    private static double computeProjection(double power, double pitch, double airtime, double length) {
        double horizVelocity = power * Math.cos(Math.toRadians(pitch));
        double horizPosition = Math.sin(Math.abs(pitch))*length;

        for (int ticks = 1; ticks <= airtime; ticks++) {
            horizPosition += horizVelocity;
            horizVelocity *= DRAG;
        }

        return horizPosition;
    }
    public static double calculatePitch(CannonMountBlockEntity mount, Vec3 targetPos, ServerLevel level) {
        if (mount == null || targetPos == null) {
            return 0;
        }

        PitchOrientedContraptionEntity contraption = mount.getContraption();
        if (contraption == null) {
            return 0;
        }

        if (!(contraption.getContraption() instanceof AbstractMountedCannonContraption cannonContraption)) {
            return 0;
        }
        float chargePower = CannonUtil.getInitialVelocity(cannonContraption, level);
        Vec3 mountPos = mount.getBlockPos().above(2).getCenter();
        int barrelLength = CannonUtil.getFrontBarrelLength(cannonContraption);
        double drag = CannonUtil.getProjectileDrag((AbstractMountedCannonContraption) contraption.getContraption());
        double gravity = CannonUtil.getProjectileGravity((AbstractMountedCannonContraption) contraption.getContraption());
        return calculatePitch(chargePower, targetPos, mountPos, barrelLength, drag, gravity);
    }
}