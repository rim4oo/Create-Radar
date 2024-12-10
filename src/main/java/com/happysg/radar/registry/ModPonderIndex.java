package com.happysg.radar.registry;

import com.happysg.radar.CreateRadar;
import com.happysg.radar.ponder.ProcessingScenes;
import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;

public class ModPonderIndex {
    static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(CreateRadar.MODID);

    public static void register() {
        CreateRadar.getLogger().info("Registering Ponder!");
        HELPER.forComponents(ModBlocks.RADAR_BEARING_BLOCK)
                .addStoryBoard("radar_contraption", ProcessingScenes::radarContraption, ModPonderTags.RADAR_COMPONENT)
                .addStoryBoard("radar_linking", ProcessingScenes::radarLinking, ModPonderTags.RADAR_COMPONENT);

        HELPER.addStoryBoard(ModBlocks.RADAR_RECEIVER_BLOCK, "radar_contraption", ProcessingScenes::radarContraption, ModPonderTags.RADAR_COMPONENT);
        HELPER.addStoryBoard(ModBlocks.RADAR_DISH_BLOCK, "radar_contraption", ProcessingScenes::radarContraption, ModPonderTags.RADAR_COMPONENT);
        HELPER.addStoryBoard(ModBlocks.RADAR_PLATE_BLOCK, "radar_contraption", ProcessingScenes::radarContraption, ModPonderTags.RADAR_COMPONENT);

        HELPER.addStoryBoard(ModBlocks.MONITOR, "radar_linking", ProcessingScenes::radarLinking, ModPonderTags.RADAR_COMPONENT);
    }
}
