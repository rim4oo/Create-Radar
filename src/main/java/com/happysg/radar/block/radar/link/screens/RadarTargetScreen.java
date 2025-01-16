package com.happysg.radar.block.radar.link.screens;

import com.happysg.radar.block.radar.link.RadarLinkBlockEntity;
import com.happysg.radar.registry.ModGuiTextures;

public class RadarTargetScreen extends AbstractRadarLinkScreen {

    public RadarTargetScreen(RadarLinkBlockEntity be) {
        super(be);
        this.background = ModGuiTextures.CANNON_TARGETING;
    }

}
