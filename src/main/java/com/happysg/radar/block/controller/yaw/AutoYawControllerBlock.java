package com.happysg.radar.block.controller.yaw;

import com.happysg.radar.registry.ModBlockEntityTypes;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class AutoYawControllerBlock extends HorizontalKineticBlock implements IBE<AutoYawControllerBlockEntity> {

    public AutoYawControllerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == Direction.Axis.Y;
    }

    @Override
    public Class<AutoYawControllerBlockEntity> getBlockEntityClass() {
        return AutoYawControllerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends AutoYawControllerBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.AUTO_YAW_CONTROLLER.get();
    }
}
