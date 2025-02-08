package com.happysg.radar.registry;

import com.happysg.radar.CreateRadar;
import com.happysg.radar.block.controller.pitch.PitchLinkBehavior;
import com.happysg.radar.block.controller.track.TrackLinkBehavior;
import com.happysg.radar.block.controller.yaw.YawLinkBehavior;
import com.happysg.radar.block.datalink.DataController;
import com.happysg.radar.block.datalink.DataLinkBehavior;
import com.happysg.radar.block.datalink.DataPeripheral;
import com.happysg.radar.block.monitor.MonitorRadarBehavior;
import com.happysg.radar.block.radar.behavior.RadarScannerLinkBehavior;
import com.simibubi.create.foundation.utility.AttachedRegistry;
import com.simibubi.create.foundation.utility.RegisteredObjects;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class AllDataBehaviors {
    public static final Map<ResourceLocation, DataLinkBehavior> GATHERER_BEHAVIOURS = new HashMap<>();

    private static final AttachedRegistry<Block, DataPeripheral> SOURCES_BY_BLOCK = new AttachedRegistry<>(ForgeRegistries.BLOCKS);
    private static final AttachedRegistry<BlockEntityType<?>, DataPeripheral> SOURCES_BY_BLOCK_ENTITY = new AttachedRegistry<>(ForgeRegistries.BLOCK_ENTITY_TYPES);

    private static final AttachedRegistry<Block, DataController> TARGETS_BY_BLOCK = new AttachedRegistry<>(ForgeRegistries.BLOCKS);
    private static final AttachedRegistry<BlockEntityType<?>, DataController> TARGETS_BY_BLOCK_ENTITY = new AttachedRegistry<>(ForgeRegistries.BLOCK_ENTITY_TYPES);


    public static void registerDefaults() {
        assignBlockEntity(register(CreateRadar.asResource("monitor"), new MonitorRadarBehavior()), ModBlockEntityTypes.MONITOR.get());
        assignBlockEntity(register(CreateRadar.asResource("radar"), new RadarScannerLinkBehavior()), ModBlockEntityTypes.RADAR_BEARING.get());
        assignBlockEntity(register(CreateRadar.asResource("pitch"), new PitchLinkBehavior()), ModBlockEntityTypes.AUTO_PITCH_CONTROLLER.get());
        assignBlockEntity(register(CreateRadar.asResource("yaw"), new YawLinkBehavior()), ModBlockEntityTypes.AUTO_YAW_CONTROLLER.get());
        assignBlockEntity(register(CreateRadar.asResource("track"), new TrackLinkBehavior()), ModBlockEntityTypes.TRACK_CONTROLLER.get());
        assignBlockEntity(register(CreateRadar.asResource("plane_radar"), new RadarScannerLinkBehavior()), ModBlockEntityTypes.PLANE_RADAR.get());
    }


    public static DataLinkBehavior register(ResourceLocation id, DataLinkBehavior behaviour) {
        behaviour.id = id;
        GATHERER_BEHAVIOURS.put(id, behaviour);
        return behaviour;
    }

    public static void assignBlock(DataLinkBehavior behaviour, ResourceLocation block) {
        if (behaviour instanceof DataPeripheral source) {
            SOURCES_BY_BLOCK.register(block, source);
        }
        if (behaviour instanceof DataController target) {
            TARGETS_BY_BLOCK.register(block, target);
        }
    }

    public static void assignBlockEntity(DataLinkBehavior behaviour, ResourceLocation beType) {
        if (behaviour instanceof DataPeripheral source) {
            SOURCES_BY_BLOCK_ENTITY.register(beType, source);
        }
        if (behaviour instanceof DataController target) {
            TARGETS_BY_BLOCK_ENTITY.register(beType, target);
        }
    }

    public static void assignBlock(DataLinkBehavior behaviour, Block block) {
        if (behaviour instanceof DataPeripheral source) {
            SOURCES_BY_BLOCK.register(block, source);
        }
        if (behaviour instanceof DataController target) {
            TARGETS_BY_BLOCK.register(block, target);
        }
    }

    public static void assignBlockEntity(DataLinkBehavior behaviour, BlockEntityType<?> beType) {
        if (behaviour instanceof DataPeripheral source) {
            SOURCES_BY_BLOCK_ENTITY.register(beType, source);
        }
        if (behaviour instanceof DataController target) {
            TARGETS_BY_BLOCK_ENTITY.register(beType, target);
        }
    }

    public static <B extends Block> NonNullConsumer<? super B> assignDataBehaviour(DataLinkBehavior behaviour,
                                                                                   String... suffix) {
        return b -> {
            ResourceLocation registryName = RegisteredObjects.getKeyOrThrow(b);
            String idSuffix = behaviour instanceof DataPeripheral ? "_source" : "_target";
            if (suffix.length > 0)
                idSuffix += "_" + suffix[0];
            assignBlock(register(new ResourceLocation(registryName.getNamespace(), registryName.getPath() + idSuffix),
                    behaviour), registryName);
        };
    }

    public static <B extends BlockEntityType<?>> NonNullConsumer<? super B> assignDataBehaviourBE(
            DataLinkBehavior behaviour, String... suffix) {
        return b -> {
            ResourceLocation registryName = RegisteredObjects.getKeyOrThrow(b);
            String idSuffix = behaviour instanceof DataPeripheral ? "_source" : "_target";
            if (suffix.length > 0)
                idSuffix += "_" + suffix[0];
            assignBlockEntity(
                    register(new ResourceLocation(registryName.getNamespace(), registryName.getPath() + idSuffix),
                            behaviour),
                    registryName);
        };
    }

    //

    @Nullable
    public static DataPeripheral getSource(ResourceLocation resourceLocation) {
        DataLinkBehavior available = GATHERER_BEHAVIOURS.getOrDefault(resourceLocation, null);
        if (available instanceof DataPeripheral source)
            return source;
        return null;
    }

    @Nullable
    public static DataController getTarget(ResourceLocation resourceLocation) {
        DataLinkBehavior available = GATHERER_BEHAVIOURS.getOrDefault(resourceLocation, null);
        if (available instanceof DataController target)
            return target;
        return null;
    }

    //

    public static DataPeripheral sourcesOf(Block block) {
        return SOURCES_BY_BLOCK.get(block);
    }

    public static DataPeripheral sourcesOf(BlockState state) {
        return sourcesOf(state.getBlock());
    }

    public static DataPeripheral sourcesOf(BlockEntityType<?> blockEntityType) {
        return SOURCES_BY_BLOCK_ENTITY.get(blockEntityType);
    }

    public static DataPeripheral sourcesOf(BlockEntity blockEntity) {
        return sourcesOf(blockEntity.getType());
    }

    @Nullable
    public static DataController targetOf(Block block) {
        return TARGETS_BY_BLOCK.get(block);
    }

    @Nullable
    public static DataController targetOf(BlockState state) {
        return targetOf(state.getBlock());
    }

    @Nullable
    public static DataController targetOf(BlockEntityType<?> blockEntityType) {
        return TARGETS_BY_BLOCK_ENTITY.get(blockEntityType);
    }

    @Nullable
    public static DataController targetOf(BlockEntity blockEntity) {
        return targetOf(blockEntity.getType());
    }

    public static DataPeripheral sourcesOf(LevelAccessor level, BlockPos pos) {
        BlockState blockState = level.getBlockState(pos);
        BlockEntity blockEntity = level.getBlockEntity(pos);

        DataPeripheral sourcesOfBlock = sourcesOf(blockState);
        DataPeripheral sourcesOfBlockEntity = blockEntity == null ? null : sourcesOf(blockEntity);

        if (sourcesOfBlockEntity == null)
            return sourcesOfBlock;
        return sourcesOfBlockEntity;
    }

    @Nullable
    public static DataController targetOf(LevelAccessor level, BlockPos pos) {
        BlockState blockState = level.getBlockState(pos);
        BlockEntity blockEntity = level.getBlockEntity(pos);

        DataController targetOfBlock = targetOf(blockState);
        DataController targetOfBlockEntity = blockEntity == null ? null : targetOf(blockEntity);

        if (targetOfBlockEntity == null)
            return targetOfBlock;
        return targetOfBlockEntity;
    }


}
