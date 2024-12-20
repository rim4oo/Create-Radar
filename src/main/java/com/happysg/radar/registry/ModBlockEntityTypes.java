package com.happysg.radar.registry;

import com.happysg.radar.CreateRadar;
import com.happysg.radar.block.monitor.MonitorBlockEntity;
import com.happysg.radar.block.monitor.MonitorRenderer;
import com.happysg.radar.block.radar.bearing.RadarBearingBlockEntity;
import com.happysg.radar.compat.cbc.controller.CannonControllerBlockEntity;
import com.happysg.radar.compat.cbc.controller.CannonControllerRenderer;
import com.simibubi.create.content.contraptions.bearing.BearingInstance;
import com.simibubi.create.content.contraptions.bearing.BearingRenderer;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import static com.happysg.radar.CreateRadar.REGISTRATE;

public class ModBlockEntityTypes {

    public static final BlockEntityEntry<MonitorBlockEntity> MONITOR = REGISTRATE
            .blockEntity("monitor", MonitorBlockEntity::new)
            .validBlocks(ModBlocks.MONITOR)
            .renderer(() -> MonitorRenderer::new)
            .register();

    public static final BlockEntityEntry<RadarBearingBlockEntity> RADAR_BEARING = REGISTRATE
            .blockEntity("radar_bearing", RadarBearingBlockEntity::new)
            .instance(() -> BearingInstance::new, true)
            .validBlocks(ModBlocks.RADAR_BEARING_BLOCK)
            .renderer(() -> BearingRenderer::new)
            .register();

    public static final BlockEntityEntry<CannonControllerBlockEntity> CANNON_CONTROLLER = REGISTRATE
            .blockEntity("cannon_controller", CannonControllerBlockEntity::new)
            .validBlocks(ModBlocks.CANNON_CONTROLLER_BLOCK)
            .renderer(() -> CannonControllerRenderer::new)
            .register();

    public static void register() {
        CreateRadar.getLogger().info("Registering block entity types!");
    }
}
