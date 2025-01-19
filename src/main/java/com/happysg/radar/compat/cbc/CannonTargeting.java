package com.happysg.radar.compat.cbc;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.math3.analysis.solvers.*;
import org.apache.commons.math3.analysis.UnivariateFunction;
import rbasamoyai.createbigcannons.cannon_control.cannon_mount.CannonMountBlockEntity;
import rbasamoyai.createbigcannons.cannon_control.contraption.AbstractMountedCannonContraption;
import rbasamoyai.createbigcannons.cannon_control.contraption.PitchOrientedContraptionEntity;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.NaN;

//todo fix calculations
public class CannonTargeting {

    public static List<Double> calculatePitch(double chargePower, Vec3 targetPos, Vec3 mountPos, int barrelLength, double drag, double gravity) {
        if (chargePower == 0) {
            return null;
        }
        double d1 = targetPos.x - mountPos.x;
        double d2 = targetPos.z - mountPos.z;
        double distance = Math.abs(Math.sqrt(d1 * d1 + d2 * d2));
        double d3 = targetPos.y - mountPos.y;
        double u = chargePower;
        double g = Math.abs(gravity);
        double k = 1 - drag;

        UnivariateFunction diffFunction = theta -> {
            double thetaRad = Math.toRadians(theta);

            double dX = distance - (Math.cos(thetaRad) * (barrelLength + 0.5));
            double dY = d3 - (Math.sin(thetaRad) * (barrelLength));
            double log = Math.log(1 - (k * dX) / (u * Math.cos(thetaRad)));

            if (Double.isInfinite(log)) {
                log = NaN;
            }

            double y = (dX * Math.tan(thetaRad) + (dX * g) / (k * u * Math.cos(thetaRad)) + g / (k * k) * log);
            ;

            return y - dY;
        };

        UnivariateSolver solver = new BrentSolver(1e-6, 1e-10);

        double start = -90;
        double end = 90;
        double step = 1.0;
        List<Double> roots = new ArrayList<>();

        double prevValue = diffFunction.value(start);
        double prevTheta = start;
        for (double theta = start + step; theta <= end; theta += step) {
            double currValue = diffFunction.value(theta);
            if (prevValue * currValue < 0) {
                try {
                    double root = solver.solve(1000, diffFunction, prevTheta, theta);
                    roots.add(root);
                } catch (Exception e) {
                    return null;
                }
            }
            prevTheta = Double.isNaN(currValue) ? prevTheta : theta;
            prevValue = Double.isNaN(currValue) ? prevValue : currValue;
        }
        if (roots.isEmpty()) {
            return null;
        }
        return roots;
    }

    public static List<Double> calculatePitch(CannonMountBlockEntity mount, Vec3 targetPos, ServerLevel level) {
        if (mount == null || targetPos == null) {
            return null;
        }

        PitchOrientedContraptionEntity contraption = mount.getContraption();
        if (contraption == null) {
            return null;
        }

        if (!(contraption.getContraption() instanceof AbstractMountedCannonContraption cannonContraption)) {
            return null;
        }
        float chargePower = CannonUtil.getInitialVelocity(cannonContraption, level);

        Vec3 mountPos = mount.getBlockPos().above(2).getCenter();
        int barrelLength = CannonUtil.getFrontBarrelLength(cannonContraption);

        double drag = CannonUtil.getProjectileDrag((AbstractMountedCannonContraption) contraption.getContraption());
        double gravity = CannonUtil.getProjectileGravity((AbstractMountedCannonContraption) contraption.getContraption());

        return calculatePitch(chargePower, targetPos, mountPos, barrelLength, drag, gravity);
    }
}