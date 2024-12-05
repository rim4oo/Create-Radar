package com.happysg.radar.registry;

import com.happysg.radar.CreateRadar;

import static com.happysg.radar.CreateRadar.REGISTRATE;

public class ModLang {


    public static void register() {
        CreateRadar.getLogger().info("Registering Lang!");
        REGISTRATE.addRawLang(CreateRadar.MODID + ".ponder.radar_contraption.header", "Creating a Radar!");
        REGISTRATE.addRawLang(CreateRadar.MODID + ".ponder.radar_contraption.text_1", "Place Radar Bearing");
        REGISTRATE.addRawLang(CreateRadar.MODID + ".ponder.radar_contraption.text_2", "Place Radar Receiver");
        REGISTRATE.addRawLang(CreateRadar.MODID + ".ponder.radar_contraption.text_3", "Add Radar Dishes");
        REGISTRATE.addRawLang(CreateRadar.MODID + ".ponder.radar_contraption.text_4", "Additional dishes/plates extend range");
        REGISTRATE.addRawLang(CreateRadar.MODID + ".ponder.radar_contraption.text_5", "Power Radar Bearing");
    }
}
