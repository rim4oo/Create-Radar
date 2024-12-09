package com.happysg.radar.block.monitor;

import com.happysg.radar.block.radar.bearing.RadarBearingBlockEntity;
import com.simibubi.create.content.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.Optional;


public class MonitorBlockEntity extends SmartBlockEntity implements IHaveHoveringInformation {
    public static final int MAX_RADIUS = 5;
    protected BlockPos controller;
    protected int radius = 1;
    protected BlockPos radarPos;
    RadarBearingBlockEntity radar;
    public MonitorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }


    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    public BlockPos getControllerPos() {
        if (controller == null)
            return getBlockPos();
        return controller;
    }

    public int getSize() {
        return radius;
    }

    public void setControllerPos(BlockPos pPos, int size) {
        controller = pPos;
        radius = size;
        notifyUpdate();
    }

    public void setRadarPos(BlockPos pPos) {
        if (level.getBlockEntity(getControllerPos()) instanceof MonitorBlockEntity monitor) {
            monitor.radarPos = pPos;
            monitor.notifyUpdate();
        }
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if (!level.isClientSide())
            return;
        getRadar().ifPresent(radar -> {
            if (radar.isRunning())
                radar.getEntityPositions().forEach(radarTrack -> System.out.println(radarTrack.position()));
        });
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        if (tag.contains("Controller"))
            controller = NbtUtils.readBlockPos(tag.getCompound("Controller"));
        if (tag.contains("radarPos"))
            radarPos = NbtUtils.readBlockPos(tag.getCompound("radarPos"));
        radius = tag.getInt("Size");
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        if (controller != null)
            tag.put("Controller", NbtUtils.writeBlockPos(controller));
        if (radarPos != null)
            tag.put("radarPos", NbtUtils.writeBlockPos(radarPos));
        tag.putInt("Size", radius);
    }


    public boolean isController() {
        return getBlockPos().equals(controller) || controller == null;
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return super.createRenderBoundingBox().inflate(MAX_RADIUS);
    }

    public Optional<RadarBearingBlockEntity> getRadar() {
        if (radar != null)
            return Optional.of(radar);
        if (radarPos == null)
            return Optional.empty();
        if (level.getBlockEntity(radarPos) instanceof RadarBearingBlockEntity radar) {
            this.radar = radar;
        }
        return Optional.ofNullable(radar);
    }

    public AABB getMultiblockBounds(LevelAccessor level, BlockPos pos) {
        Direction facing = level.getBlockState(getControllerPos())
                .getValue(MonitorBlock.FACING).getClockWise();
        VoxelShape shape = level.getBlockState(getControllerPos())
                .getShape(level, getControllerPos());
        return shape.bounds()
                .move(getControllerPos()).expandTowards(facing.getStepX() * (radius - 1), radius - 1, facing.getStepZ() * (radius - 1));

    }
}
