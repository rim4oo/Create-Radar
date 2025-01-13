package com.happysg.radar.block.radar.bearing;

import com.happysg.radar.block.radar.link.RadarLinkBlockEntity;
import com.happysg.radar.block.radar.link.RadarSource;
import com.happysg.radar.block.radar.link.screens.AbstractRadarLinkScreen;
import com.happysg.radar.block.radar.link.screens.RadarFilterScreen;

public class RadarBearingLinkBehavior extends RadarSource {

    @Override
    protected AbstractRadarLinkScreen getScreen(RadarLinkBlockEntity be) {
        return new RadarFilterScreen(be);
    }
}
