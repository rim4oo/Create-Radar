package com.happysg.radar.block.monitor;

import com.happysg.radar.block.radar.bearing.RadarBearingBlockEntity;
import com.happysg.radar.block.radar.behavior.IHasTracks;
import com.happysg.radar.block.radar.link.screens.TargetingConfig;
import com.happysg.radar.block.radar.track.RadarTrack;
import com.happysg.radar.block.radar.track.RadarTrackUtil;
import com.happysg.radar.compat.vs2.VS2Utils;
import com.simibubi.create.content.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;


public class MonitorBlockEntity extends SmartBlockEntity implements IHaveHoveringInformation, IHasTracks {

    protected BlockPos controller;
    protected int radius = 1;
    private int ticksSinceLastUpdate = 0;
    protected BlockPos radarPos;
    RadarBearingBlockEntity radar;
    protected String hoveredEntity;
    protected String selectedEntity;
    Collection<RadarTrack> cachedTracks = List.of();
    MonitorFilter filter = MonitorFilter.DEFAULT;

    public MonitorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }


    @Override
    public void initialize() {
        super.initialize();
        updateCache();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    public void updateCache() {
        getRadar().ifPresent(radar -> cachedTracks = radar.getTracks().stream().filter(filter::test).toList());
    }

    public BlockPos getControllerPos() {
        if (controller == null)
            return getBlockPos();
        return controller;
    }

    public int getSize() {
        return radius;
    }

    @Override
    public void tick() {
        super.tick();
        if (ticksSinceLastUpdate > 20)
            setRadarPos(null);
        ticksSinceLastUpdate++;
    }

    public void setControllerPos(BlockPos pPos, int size) {
        controller = pPos;
        radius = size;
        notifyUpdate();
    }

    public void setRadarPos(BlockPos pPos) {
        if (level.isClientSide())
            return;

        if (level.getBlockEntity(getControllerPos()) instanceof MonitorBlockEntity monitor) {
            if (pPos == null) {
                radarPos = null;
                radar = null;
                notifyUpdate();
                return;
            }
            monitor.radarPos = pPos;
            monitor.ticksSinceLastUpdate = 0;
            monitor.notifyUpdate();
        }
    }


    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        controller = null;
        radarPos = null;
        radar = null;

        super.read(tag, clientPacket);
        if (tag.contains("Controller"))
            controller = NbtUtils.readBlockPos(tag.getCompound("Controller"));
        if (tag.contains("radarPos"))
            radarPos = NbtUtils.readBlockPos(tag.getCompound("radarPos"));
        if (tag.contains("SelectedEntity"))
            selectedEntity = tag.getString("SelectedEntity");
        if (tag.contains("HoveredEntity"))
            hoveredEntity = tag.getString("HoveredEntity");
        else
            hoveredEntity = null;
        filter = MonitorFilter.fromTag(tag.getCompound("Filter"));
        radius = tag.getInt("Size");
        if (clientPacket)
            cachedTracks = RadarTrackUtil.deserializeListNBT(tag.getCompound("tracks"));
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        if (controller != null)
            tag.put("Controller", NbtUtils.writeBlockPos(controller));
        if (radarPos != null)
            tag.put("radarPos", NbtUtils.writeBlockPos(radarPos));
        if (selectedEntity != null)
            tag.putString("SelectedEntity", selectedEntity);
        if (hoveredEntity != null)
            tag.putString("HoveredEntity", hoveredEntity);
        tag.put("Filter", filter.toTag());
        tag.putInt("Size", radius);
        if (clientPacket)
            tag.put("tracks", RadarTrackUtil.serializeNBTList(cachedTracks));
    }


    public boolean isController() {
        return getBlockPos().equals(controller) || controller == null;
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return super.createRenderBoundingBox().inflate(10);
    }


    //messy caching radar reference
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




    public MonitorBlockEntity getController() {
        if (isController())
            return this;
        if (level.getBlockEntity(controller) instanceof MonitorBlockEntity controller)
            return controller;
        return this;
    }

    public Vec3 getTargetPos(TargetingConfig targetingConfig) {
        AtomicReference<Vec3> targetPos = new AtomicReference<>();
        getRadar().ifPresent(
                radar -> {
                    if (selectedEntity == null)
                        tryFindAutoTarget(targetingConfig);
                    if (selectedEntity == null)
                        return;
                    for (RadarTrack track : getController().cachedTracks) {
                        if (track.id().equals(selectedEntity))
                            targetPos.set(track.position());
                    }

                }
        );
        if (targetPos.get() == null)
            selectedEntity = null;
        return targetPos.get();
    }

    private void tryFindAutoTarget(TargetingConfig targetingConfig) {
        if (!targetingConfig.autoTarget())
            return;
        final double[] distance = {Double.MAX_VALUE};
        getRadar().ifPresent(
                radar -> {
                    for (RadarTrack track : getController().cachedTracks) {
                        if (targetingConfig.test(track.trackCategory()) && track.position().distanceTo(Vec3.atCenterOf(getControllerPos())) < distance[0]) {
                            selectedEntity = track.id();
                            distance[0] = track.position().distanceTo(Vec3.atCenterOf(getControllerPos()));
                        }
                    }

                }
        );
        if (selectedEntity != null)
            notifyUpdate();
    }

    public void setFilter(MonitorFilter filter) {
        this.getController().filter = filter;
        this.filter = filter;
    }

    public Collection<RadarTrack> getTracks() {
        return cachedTracks;
    }

    @Nullable
    public Vec3 getRadarCenterPos() {
        if (radarPos == null)
            return null;
        return VS2Utils.getWorldVec(level, radarPos);
    }

    public float getRange() {
        return getRadar().map(RadarBearingBlockEntity::getRange).orElse(0f);
    }
}
