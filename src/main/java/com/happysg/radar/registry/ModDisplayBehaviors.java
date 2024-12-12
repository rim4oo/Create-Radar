package com.happysg.radar.registry;

import com.happysg.radar.CreateRadar;
import com.happysg.radar.block.monitor.MonitorDisplayBehavior;
import com.happysg.radar.block.radar.bearing.RadarDisplayBehavior;
import com.happysg.radar.compat.Mods;
import com.happysg.radar.compat.cbc.CBC;
import com.simibubi.create.content.redstone.displayLink.AllDisplayBehaviours;
import com.simibubi.create.content.redstone.displayLink.DisplayBehaviour;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static com.simibubi.create.content.redstone.displayLink.AllDisplayBehaviours.assignBlockEntity;

public class ModDisplayBehaviors {

    public static void register(String id, DisplayBehaviour behaviour, BlockEntityType<?> be) {
        assignBlockEntity(AllDisplayBehaviours.register(CreateRadar.asResource(id), behaviour), be);
    }

    public static void register() {
        CreateRadar.getLogger().info("Registering Display Behaviors!");
        register("monitor", new MonitorDisplayBehavior(), ModBlockEntityTypes.MONITOR.get());
        register("radar", new RadarDisplayBehavior(), ModBlockEntityTypes.RADAR_BEARING.get());
        if (Mods.CREATEBIGCANNONS.isLoaded())
            CBC.registerDisplayBehaviors();
    }
}
