package com.happysg.radar.block.radar.bearing;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity;
import com.simibubi.create.content.kinetics.BlockStressValues;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;

public class RadarBearingBlockEntity extends MechanicalBearingBlockEntity {
    private int dishCount;

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
        updateDishCount();
    }


    //code copied in order to replace with radar contraption and radar advancements
    @Override
    public void assemble() {
        if (!(level.getBlockState(worldPosition)
                .getBlock() instanceof RadarBearingBlock))
            return;

        RadarContraption contraption = createContraption();
        if (contraption == null)
            return;

        //replace with radar advancements
        if (isWindmill())
            award(AllAdvancements.WINDMILL);
        if (contraption.getSailBlocks() >= 16 * 8)
            award(AllAdvancements.WINDMILL_MAXED);

        updateGeneratedRotation();
        updateDishCount();
        notifyUpdate();
    }


    @Override
    public void disassemble() {
        super.disassemble();
        updateDishCount();
    }

    @Override
    public float calculateStressApplied() {
        float impact = (float) BlockStressValues.getImpact(getStressConfigKey()) + getDishCount();
        this.lastStressApplied = impact;
        return impact;
    }

    private RadarContraption createContraption() {
        RadarContraption contraption = new RadarContraption();
        try {
            if (!contraption.assemble(level, worldPosition))
                return null;

            lastException = null;
        } catch (AssemblyException e) {
            lastException = e;
            sendData();
            return null;
        }
        contraption.removeBlocksFromWorld(level, BlockPos.ZERO);
        movedContraption = ControlledContraptionEntity.create(level, this, contraption);
        BlockPos anchor = worldPosition.above();
        movedContraption.setPos(anchor.getX(), anchor.getY(), anchor.getZ());
        movedContraption.setRotationAxis(Direction.Axis.Y);
        level.addFreshEntity(movedContraption);

        AllSoundEvents.CONTRAPTION_ASSEMBLE.playOnServer(level, worldPosition);

        running = true;
        angle = 0;
        return contraption;
    }

    private void updateDishCount() {
        dishCount = getContraption().map(RadarContraption::getDishCount).orElse(0);
        notifyUpdate();
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        if (dishCount > 0)
            tooltip.add(Component.literal("    Dish Count: " + dishCount));
        return true;
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        dishCount = compound.getInt("dishCount");
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putInt("dishCount", dishCount);

    }

    public int getDishCount() {
        return dishCount;
    }

    public Optional<RadarContraption> getContraption() {
        return Optional.ofNullable(movedContraption)
                .map(ControlledContraptionEntity::getContraption)
                .filter(c -> c instanceof RadarContraption)
                .map(c -> (RadarContraption) c);
    }

}
