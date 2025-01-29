package com.happysg.radar.block.controller.track;

import com.happysg.radar.compat.Mods;
import com.happysg.radar.compat.vs2.VS2Utils;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniondc;
import org.valkyrienskies.core.api.ships.LoadedShip;

public class TrackControllerBlockEntity extends KineticBlockEntity {

    public Vec3 target;

    public TrackControllerBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        if (!Mods.VALKYRIENSKIES.isLoaded())
            return;
        System.out.println("Yaw offset: " + getAngleOffsetToWorld());
    }

    private double getAngleOffsetToWorld() {
        LoadedShip ship = VS2Utils.getShipManagingPos(this);
        if (ship == null)
            return 0;
        Quaterniondc quaterniondc = ship.getTransform().getShipToWorldRotation();
        // Extract yaw directly from quaternion
        double qw = quaterniondc.w();
        double qx = quaterniondc.x();
        double qy = quaterniondc.y();
        double qz = quaterniondc.z();

        // Calculate yaw in radians
        double yaw = Math.atan2(2.0 * (qw * qy + qx * qz), 1.0 - 2.0 * (qy * qy + qz * qz));

        // Convert to degrees and normalize to range [-180, 180]
        yaw = Math.toDegrees(yaw);
        yaw = (yaw + 360) % 360; // Normalize to range [0, 360)

        return yaw;
    }

}
