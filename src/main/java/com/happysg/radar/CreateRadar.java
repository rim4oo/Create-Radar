package com.happysg.radar;

import com.happysg.radar.networking.ModMessages;
import com.happysg.radar.registry.*;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.stream.Collectors;

@Mod(CreateRadar.MODID)
public class CreateRadar {
    public static final String MODID = "create_radar";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);

    public CreateRadar() {
        getLogger().info("Initializing Create Radar!");
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
        REGISTRATE.registerEventListeners(modEventBus);
        ModItems.register();
        ModBlocks.register();
        ModBlockEntityTypes.register();
        ModCreativeTabs.register(modEventBus);
        ModLang.register();
        ModContraptionTypes.register();
        modEventBus.addListener(CreateRadar::init);
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MODID, path);
    }

    public static String toHumanReadable(String key) {
        String s = key.replace("_", " ");
        s = Arrays.stream(StringUtils.splitByCharacterTypeCamelCase(s))
                .map(StringUtils::capitalize)
                .collect(Collectors.joining(" "));
        return StringUtils.normalizeSpace(s);
    }

    public static void init(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModMessages::register);
        ModDisplayBehaviors.register();
    }
}
