package com.happysg.radar.block.radar.link.screens;

import com.happysg.radar.block.radar.link.RadarLinkBlockEntity;
import com.happysg.radar.registry.ModGuiTextures;

public class RadarFilterScreen extends AbstractRadarLinkScreen {

    public RadarFilterScreen(RadarLinkBlockEntity be) {
        super(be);
        this.background = ModGuiTextures.RADAR_FILTER;
    }

}
