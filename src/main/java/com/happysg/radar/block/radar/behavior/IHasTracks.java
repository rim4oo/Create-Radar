package com.happysg.radar.block.radar.behavior;

import com.happysg.radar.block.radar.track.RadarTrack;

import java.util.Collection;

public interface IHasTracks {
    Collection<RadarTrack> getTracks();
}