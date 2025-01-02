package com.happysg.radar.block.controller.pitch;

import com.happysg.radar.registry.ModBlockEntityTypes;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class AutoPitchControllerBlock extends HorizontalKineticBlock implements IBE<AutoPitchControllerBlockEntity> {

    public AutoPitchControllerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return null;
    }

    @Override
    public Class<AutoPitchControllerBlockEntity> getBlockEntityClass() {
        return AutoPitchControllerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends AutoPitchControllerBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.AUTO_PITCH_CONTROLLER.get();
    }
}
