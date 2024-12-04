package com.happysg.radar.block.radar.bearing;

import com.happysg.radar.block.radar.receiver.AbstractRadarFrame;
import com.happysg.radar.block.radar.receiver.RadarReceiverBlock;
import com.happysg.radar.registry.ModContraptionTypes;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.ContraptionType;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.bearing.BearingContraption;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.lang3.tuple.Pair;

public class RadarContraption extends BearingContraption {

    private int dishCount;
    private boolean hasReceiver;
    private Direction receiverFacing;

    public RadarContraption() {
        facing = Direction.UP;
    }

    @Override
    public boolean assemble(Level world, BlockPos pos) throws AssemblyException {
        boolean assembled = super.assemble(world, pos);
        if (!hasReceiver) {
            throw new AssemblyException(Component.literal("No receiver found"));
        }
        return assembled;
    }

    @Override
    public void addBlock(BlockPos pos, Pair<StructureTemplate.StructureBlockInfo, BlockEntity> capture) {
        super.addBlock(pos, capture);
        //replace with tag instead of block instance check
        if (capture.getKey().state().getBlock() instanceof AbstractRadarFrame)
            dishCount++;
        //replace with tag instead of block instance check
        if (capture.getKey().state().getBlock() instanceof RadarReceiverBlock) {
            hasReceiver = true;
            receiverFacing = capture.getKey().state().getValue(RadarReceiverBlock.FACING);
        }

    }

    @Override
    public void tickStorage(AbstractContraptionEntity entity) {
        super.tickStorage(entity);
        if (entity instanceof ControlledContraptionEntity contraptionEntity) {
            System.out.println(contraptionEntity.getAngle(1));
        }
    }

    public int getDishCount() {
        return dishCount;
    }

    public boolean hasReceiver() {
        return hasReceiver;
    }

    public Direction getReceiverFacing() {
        return receiverFacing;
    }

    @Override
    public ContraptionType getType() {
        return ModContraptionTypes.RADAR_BEARING;
    }
}
