package com.happysg.radar.compat.cbc;

import com.happysg.radar.CreateRadar;
import com.happysg.radar.registry.ModDisplayBehaviors;
import rbasamoyai.createbigcannons.index.CBCBlockEntities;

public class CBC {
    public static void registerDisplayBehaviors() {
        CreateRadar.getLogger().info("Registering CBC Turret Guidance behaviors");
        ModDisplayBehaviors.register("cannon_mount", new TurretGuidanceBehavior(), CBCBlockEntities.CANNON_MOUNT.get());
    }
}
