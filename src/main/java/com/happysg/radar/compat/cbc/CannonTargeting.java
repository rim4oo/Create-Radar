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

    private static final double DRAG = 0.99;

//    }
    public static double calculatePitch(double chargePower, Vec3 targetPos, Vec3 mountPos, int barrelLength, double drag, double gravity) {
        if(chargePower == 0){
            return NaN;
        }
        double d1 = targetPos.x - mountPos.x;
        double d2 = targetPos.z - mountPos.z;
        double distance = Math.abs(Math.sqrt(d1 * d1 + d2 * d2));
        double dY = targetPos.y - mountPos.y;
        double u = chargePower;
        double g = Math.abs(gravity);
        double k = 1 - drag;
        double bestDiff = Double.MAX_VALUE;
        double bestTheta=0;
        double theta = Math.toRadians(-89.9);
        while(theta<=Math.toRadians(89.9)) {
            double dX = distance - (Math.cos(theta) * (barrelLength));
            double yTargetLocal = dY - (Math.sin(theta) * (barrelLength));

            double log = Math.log(1-(k*dX)/(u*Math.cos(theta)));
            if(Double.isInfinite(log)){
                log = NaN;
            }
            double y = (dX*Math.tan(theta)+(dX*g)/(k*u*Math.cos(theta)) + g/(k*k)*log);

            if (!Double.isNaN(y)){
                double diff = y - yTargetLocal;
                if (Math.abs(diff) < Math.abs(bestDiff)) {
                    bestDiff = diff;
                     bestTheta = theta;
                }
            }
            theta+=Math.toRadians(0.05);
        }
        return bestTheta/Math.PI*180;
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