package com.happysg.radar.block.radar.link;

import com.happysg.radar.block.radar.link.screens.AbstractRadarLinkScreen;

public abstract class RadarSource extends RadarLinkBehavior {

    protected abstract AbstractRadarLinkScreen getScreen(RadarLinkBlockEntity be);

    public void transferData(RadarLinkBlockEntity be, RadarTarget activeTarget) {
    }
}
