package com.happysg.radar.block.radar.bearing;

import com.happysg.radar.CreateRadar;
import com.happysg.radar.block.monitor.MonitorBlockEntity;
import com.happysg.radar.block.monitor.MonitorFilter;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.DisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTarget;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class RadarDisplayBehavior extends DisplaySource {

    @Override
    public void transferData(DisplayLinkContext context, DisplayTarget activeTarget, int line) {
        super.transferData(context, activeTarget, line);
        if (context.getTargetBlockEntity() instanceof MonitorBlockEntity monitor) {
            monitor.setRadarPos(context.getSourceBlockEntity().getBlockPos());
            monitor.notifyUpdate();
        }
    }

    @Override
    public List<MutableComponent> provideText(DisplayLinkContext context, DisplayTargetStats stats) {
        return List.of(Component.literal(""));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder, boolean isFirstLine) {
        if (isFirstLine)
            addFilterConfig(builder);
    }

    @OnlyIn(Dist.CLIENT)
    protected void addFilterConfig(ModularGuiLineBuilder builder) {
        builder.addSelectionScrollInput(0, 100,
                (si, l) -> si
                        .forOptions(Arrays.stream(MonitorFilter.values())
                                .map(MonitorFilter::name)
                                .map(name -> Component.translatable(CreateRadar.MODID + ".filter." + name.toLowerCase(Locale.ROOT)))
                                .toList())
                        .titled(Component.translatable(CreateRadar.MODID + ".filter.show")),
                "Filter");
    }
}
