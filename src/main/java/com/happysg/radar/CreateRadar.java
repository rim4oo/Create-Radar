package com.happysg.radar;

import com.happysg.radar.block.controller.id.IDManager;
import com.happysg.radar.block.datalink.DataLinkBlockItem;
import com.happysg.radar.block.monitor.MonitorInputHandler;
import com.happysg.radar.config.RadarConfig;
import com.happysg.radar.networking.ModMessages;
import com.happysg.radar.registry.*;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
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
        ModLoadingContext context = ModLoadingContext.get();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
        REGISTRATE.registerEventListeners(modEventBus);
        ModItems.register();
        ModBlocks.register();
        ModBlockEntityTypes.register();
        ModCreativeTabs.register(modEventBus);
        ModLang.register();
        ModPartials.init();
        RadarConfig.register(context);
        ModContraptionTypes.register();
        modEventBus.addListener(CreateRadar::init);
        modEventBus.addListener(CreateRadar::clientInit);
        modEventBus.addListener(CreateRadar::onLoadComplete);
        MinecraftForge.EVENT_BUS.addListener(MonitorInputHandler::monitorPlayerHovering);
        MinecraftForge.EVENT_BUS.addListener(CreateRadar::clientTick);
        MinecraftForge.EVENT_BUS.addListener(CreateRadar::onLoadWorld);
    }

    private static void clientTick(TickEvent.ClientTickEvent event) {
        DataLinkBlockItem.clientTick();
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

    public static void clientInit(final FMLClientSetupEvent event) {
        ModPonderIndex.register();
        ModPonderTags.register();
    }

    public static void onLoadComplete(FMLLoadCompleteEvent event) {
        ModContainer container = ModList.get()
                .getModContainerById(CreateRadar.MODID)
                .orElseThrow(() -> new IllegalStateException("Radar mod container missing on LoadComplete"));

        container.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(RadarConfig::createConfigScreen));
    }

    public static void onLoadWorld(LevelEvent.Load event) {
        LevelAccessor world = event.getLevel();
        if (world.getServer() != null) {
            IDManager.load(world.getServer());
        }
    }


    public static void init(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModMessages::register);
        ModDisplayBehaviors.register();
        AllDataBehaviors.registerDefaults();
    }
}
