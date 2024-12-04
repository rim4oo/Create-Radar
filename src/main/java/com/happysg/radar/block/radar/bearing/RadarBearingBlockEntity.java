package com.happysg.radar.block.radar.bearing;

import com.happysg.radar.block.radar.receiver.AbstractRadarFrame;
import com.happysg.radar.block.radar.receiver.RadarReceiverBlock;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;

public class RadarBearingBlockEntity extends MechanicalBearingBlockEntity {
    private int dishCount;
    private boolean hasReceiver;

    public RadarBearingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        movementMode.setValue(MovementMode.MOVE_NEVER_PLACE.ordinal());
    }

    @Override
    public void initialize() {
        super.initialize();
        updateRadarContraption();
    }

    @Override
    public void assemble() {
        super.assemble();
        updateRadarContraption();
    }

    @Override
    public void disassemble() {
        super.disassemble();
        updateRadarContraption();
    }

    private void updateRadarContraption() {
        if (movedContraption == null || movedContraption.getContraption() == null) {
            dishCount = 0;
            hasReceiver = false;
        } else {
            dishCount = (int) movedContraption.getContraption().getBlocks().values().stream()
                    .filter(blockState -> blockState.state().getBlock() instanceof AbstractRadarFrame)
                    .count();
            hasReceiver = movedContraption.getContraption().getBlocks().values().stream()
                    .anyMatch(blockState -> blockState.state().getBlock() instanceof RadarReceiverBlock);
        }
        notifyUpdate();
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        if (dishCount > 0)
            tooltip.add(Component.literal("    Dish Count: " + dishCount));
        if (!hasReceiver && isRunning())
            tooltip.add(Component.literal("    No Receiver Found!").withStyle(ChatFormatting.RED));
        return true;
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        dishCount = compound.getInt("dishCount");
        hasReceiver = compound.getBoolean("hasReceiver");
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putInt("dishCount", dishCount);
        compound.putBoolean("hasReceiver", hasReceiver);
    }

    public int getDishCount() {
        return dishCount;
    }

    public boolean hasReceiver() {
        return hasReceiver;
    }

    public Optional<Contraption> getContraption() {
        return Optional.ofNullable(movedContraption).map(ControlledContraptionEntity::getContraption);
    }

}
