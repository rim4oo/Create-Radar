package com.happysg.radar.block.radar.track;

import com.happysg.radar.block.monitor.MonitorSprite;
import com.happysg.radar.config.RadarConfig;
import com.jozufozu.flywheel.util.Color;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;


public record RadarTrack(String id, Vec3 position, Vec3 velocity, long scannedTime,
                         TrackCategory trackCategory, String entityType) {

    public RadarTrack(Entity entity) {
        this(entity.getUUID().toString(), entity.position(), entity.getDeltaMovement(), entity.level().getGameTime(),
                TrackCategory.get(entity), entity.getType().toString());
    }

    public Color getColor() {
        return switch (trackCategory) {
            case VS2 -> new Color(RadarConfig.client().VS2Color.get());
            case CONTRAPTION -> new Color(RadarConfig.client().contraptionColor.get());
            case PLAYER -> new Color(RadarConfig.client().playerColor.get());
            case ANIMAL -> new Color(RadarConfig.client().friendlyColor.get());
            case HOSTILE -> new Color(RadarConfig.client().hostileColor.get());
            case PROJECTILE -> new Color(RadarConfig.client().projectileColor.get());
            default -> Color.WHITE;
        };
    }

    public MonitorSprite getSprite() {
        return switch (trackCategory) {
            case VS2, CONTRAPTION -> MonitorSprite.CONTRAPTION_HITBOX;
            case PLAYER -> MonitorSprite.PLAYER;
            case PROJECTILE -> MonitorSprite.PROJECTILE;
            default -> MonitorSprite.ENTITY_HITBOX;
        };
    }

    public static RadarTrack deserializeNBT(CompoundTag tag) {
        return new RadarTrack(tag.getString("id"),
                new Vec3(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z")),
                new Vec3(tag.getDouble("vx"), tag.getDouble("vy"), tag.getDouble("vz")),
                tag.getLong("scannedTime"),
                TrackCategory.values()[tag.getInt("Category")],
                tag.getString("entityType")
        );
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", id);
        tag.putDouble("x", position.x);
        tag.putDouble("y", position.y);
        tag.putDouble("z", position.z);
        tag.putDouble("vx", velocity.x);
        tag.putDouble("vy", velocity.y);
        tag.putDouble("vz", velocity.z);
        tag.putLong("scannedTime", scannedTime);
        tag.putInt("Category", trackCategory.ordinal());
        tag.putString("entityType", entityType);
        return tag;
    }


}
