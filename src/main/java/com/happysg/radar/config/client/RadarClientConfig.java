package com.happysg.radar.config.client;

import com.simibubi.create.foundation.config.ConfigBase;

public class RadarClientConfig extends ConfigBase {

    @Override
    public String getName() {
        return "Radar Client";
    }

    public ConfigInt groundRadarColor = i(0x00ff00, 0, "groundRadarColor", "This is the color of the ground radar on the monitor");
    public ConfigInt hostileColor = i(0xff0000, 0, "hostileColor", "This is the color of hostile entities on the monitor");
    public ConfigInt friendlyColor = i(0x00ff00, 0, "friendlyColor", "This is the color of friendly entities on the monitor");
    public ConfigInt playerColor = i(0xff0000, 0, "playerColor", "This is the color of players on the monitor");
    public ConfigInt projectileColor = i(0xffff00, 0, "projectileColor", "This is the color of projectiles on the monitor");
    public ConfigInt contraptionColor = i(0x000000, 0, "contraptionColor", "This is the color of contraptions on the monitor");
    public ConfigInt VS2Color = i(0xffff00, 0, "VS2Color", "This is the color of VS2 ships on the monitor");
    public ConfigInt neutral = i(0x000000, 0, "neutral", "This is the color of neutral entities on the monitor");
}
