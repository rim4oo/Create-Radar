package com.happysg.radar.block.monitor;

import net.minecraft.world.entity.Entity;

import java.util.function.Predicate;

public enum MonitorFilter {
    ALL_ENTITIES(entity -> true);
    // NO_MOBS(entity->!(entity instanceof Mob)),
    //   PLAYERS_ONLY(entity->entity instanceof Player),
    //  PROJECTILES_ONLY(entity->entity instanceof Projectile),
    //  VS2_ONLY(entity->false),
    //  MOB_BOSSES_ONLY(entity->entity instanceof EnderDragon||entity instanceof WitherBoss);

    private final Predicate<Entity> predicate;

    MonitorFilter(Predicate<Entity> test) {
        this.predicate = test;
    }

    public boolean test(Entity entity) {
        return predicate.test(entity);
    }
}