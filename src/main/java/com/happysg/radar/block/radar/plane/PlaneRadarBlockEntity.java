package com.happysg.radar.block.radar.plane;

import com.happysg.radar.block.radar.behavior.RadarScanningBlockBehavior;
import com.happysg.radar.compat.Mods;
import com.happysg.radar.compat.vs2.VS2Utils;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.valkyrienskies.core.api.ships.Ship;

import java.util.List;

public class PlaneRadarBlockEntity extends SmartBlockEntity {
    private RadarScanningBlockBehavior scanningBehavior;


    public PlaneRadarBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void initialize() {
        super.initialize();
        if (!Mods.VALKYRIENSKIES.isLoaded())
            return;
        Ship ship = VS2Utils.getShipManagingPos(this);
        if (ship == null)
            return;
        scanningBehavior.setScanPos(VS2Utils.getWorldVec(this));
        scanningBehavior.setRunning(true);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        scanningBehavior = new RadarScanningBlockBehavior(this);
        scanningBehavior.setRunning(true);
        scanningBehavior.setRange(250);
        scanningBehavior.setAngle((getBlockState().getValue(PlaneRadarBlock.FACING).toYRot() + 360) % 360);
        scanningBehavior.setScanPos(VS2Utils.getWorldVec(this));
        behaviours.add(scanningBehavior);
    }


}
