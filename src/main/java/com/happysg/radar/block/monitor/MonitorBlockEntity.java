package com.happysg.radar.block.monitor;

import com.simibubi.create.content.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;


public class MonitorBlockEntity extends SmartBlockEntity implements IHaveHoveringInformation {
    public static final int MAX_RADIUS = 5;
    protected BlockPos controller;
    protected int radius = 1;

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

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        if (tag.contains("Controller"))
            controller = NbtUtils.readBlockPos(tag.getCompound("Controller"));
        radius = tag.getInt("Size");
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        if (controller != null)
            tag.put("Controller", NbtUtils.writeBlockPos(controller));
        tag.putInt("Size", radius);
    }


    public boolean isController() {
        return getBlockPos().equals(controller) || controller == null;
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return super.createRenderBoundingBox().inflate(MAX_RADIUS);
    }
}
