package com.happysg.radar.registry;

import com.happysg.radar.CreateRadar;
import com.happysg.radar.block.monitor.MonitorBlock;
import com.happysg.radar.block.radar.bearing.RadarBearingBlock;
import com.happysg.radar.block.radar.receiver.AbstractRadarFrame;
import com.happysg.radar.block.radar.receiver.RadarReceiverBlock;
import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.client.model.generators.ConfiguredModel;

import static com.happysg.radar.CreateRadar.REGISTRATE;

public class ModBlocks {

    public static final BlockEntry<MonitorBlock> MONITOR =
            REGISTRATE.block("monitor", MonitorBlock::new)
                    .initialProperties(SharedProperties::softMetal)
                    .properties(BlockBehaviour.Properties::noOcclusion)
                    .blockstate((c, p) -> p.getVariantBuilder(c.get())
                            .forAllStates(state -> {
                                String shape = state.getValue(MonitorBlock.SHAPE).toString().toLowerCase();
                                return ConfiguredModel.builder()
                                        .modelFile(p.models()
                                                .getExistingFile(CreateRadar.asResource("block/monitor/monitor_" + shape)))
                                        .rotationY(((int) state.getValue(MonitorBlock.FACING).toYRot() + 180) % 360)
                                        .build();
                            }))
                    .addLayer(() -> RenderType::cutoutMipped)
                    .item()
                    .model((c, p) -> p.withExistingParent(c.getName(), CreateRadar.asResource("block/monitor/monitor_single")))
                    .build()
                    .register();

    public static final BlockEntry<RadarBearingBlock> RADAR_BEARING_BLOCK =
            REGISTRATE.block("radar_bearing", RadarBearingBlock::new)
                    .initialProperties(SharedProperties::softMetal)
                    .transform(BlockStressDefaults.setImpact(64))
                    .properties(BlockBehaviour.Properties::noOcclusion)
                    .blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p)))
                    .item()
                    .model(AssetLookup.customBlockItemModel("_", "item"))
                    .build()
                    .register();

    @SuppressWarnings("unused")
    public static final BlockEntry<RadarReceiverBlock> RADAR_RECEIVER_BLOCK =
            REGISTRATE.block("radar_receiver_block", RadarReceiverBlock::new)
                    .initialProperties(SharedProperties::softMetal)
                    .transform(BlockStressDefaults.setImpact(0))
                    .properties(BlockBehaviour.Properties::noOcclusion)
                    .blockstate((ctx, prov) -> prov.directionalBlock(ctx.getEntry(), prov.models()
                            .getExistingFile(ctx.getId()), 0))
                    .simpleItem()
                    .register();

    @SuppressWarnings("unused")
    public static final BlockEntry<AbstractRadarFrame> RADAR_DISH_BLOCK =
            REGISTRATE.block("radar_dish_block", properties -> new AbstractRadarFrame(properties, ModShapes.RADAR_DISH))
                    .initialProperties(SharedProperties::softMetal)
                    .transform(BlockStressDefaults.setImpact(0))
                    .properties(BlockBehaviour.Properties::noOcclusion)
                    .addLayer(() -> RenderType::cutoutMipped)
                    .blockstate((ctx, prov) -> prov.directionalBlock(ctx.getEntry(), prov.models()
                            .getExistingFile(ctx.getId()), 0))
                    .simpleItem()
                    .register();

    @SuppressWarnings("unused")
    public static final BlockEntry<AbstractRadarFrame> RADAR_PLATE_BLOCK =
            REGISTRATE.block("radar_plate_block", properties -> new AbstractRadarFrame(properties, ModShapes.RADAR_PLATE))
                    .initialProperties(SharedProperties::softMetal)
                    .transform(BlockStressDefaults.setImpact(0))
                    .properties(BlockBehaviour.Properties::noOcclusion)
                    .blockstate((ctx, prov) -> prov.directionalBlock(ctx.getEntry(), prov.models()
                            .getExistingFile(ctx.getId()), 0))
                    .simpleItem()
                    .register();


    public static void register() {
        CreateRadar.getLogger().info("Registering blocks!");
    }
}
