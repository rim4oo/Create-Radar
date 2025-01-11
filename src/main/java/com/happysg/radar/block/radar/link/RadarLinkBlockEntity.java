package com.happysg.radar.block.radar.link;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class RadarLinkBlockEntity extends SmartBlockEntity {

    protected BlockPos targetOffset;

    public RadarSource activeSource;
    public RadarTarget activeTarget;

    private CompoundTag sourceConfig;

    public RadarLinkBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    public BlockPos getSourcePosition() {
        return null;
    }

    public BlockPos getTargetPosition() {
        return null;
    }
}
