package com.happysg.radar.block.radar.bearing;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public record RadarTrack(UUID entityId, Vec3 position, long scannedTime) {
    public RadarTrack(Entity entity) {
        this(entity.getUUID(), entity.position(), entity.level().getGameTime());
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("entityId", entityId);
        tag.putDouble("x", position.x);
        tag.putDouble("y", position.y);
        tag.putDouble("z", position.z);
        tag.putLong("scannedTime", scannedTime);
        return tag;
    }

    public static RadarTrack deserializeNBT(CompoundTag tag) {
        return new RadarTrack(tag.getUUID("entityId"), new Vec3(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z")), tag.getLong("scannedTime"));
    }

    public static CompoundTag serializeNBTList(Collection<RadarTrack> tracks) {
        ListTag list = new ListTag();
        for (RadarTrack track : tracks) {
            list.add(track.serializeNBT());
        }
        CompoundTag tag = new CompoundTag();
        tag.put("tracks", list);
        return tag;
    }

    public static List<RadarTrack> deserializeListNBT(CompoundTag tag) {
        List<RadarTrack> tracks = new ArrayList<>();
        ListTag list = tag.getList("tracks", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            tracks.add(RadarTrack.deserializeNBT(list.getCompound(i)));
        }
        return tracks;
    }
}
