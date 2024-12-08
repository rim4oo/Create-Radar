package com.happysg.radar.block.radar.bearing;

import com.happysg.radar.block.monitor.MonitorBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.DisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTarget;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

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
                (si, l) -> si.forOptions(List.of(
                                Component.literal("All Entities"),
                                Component.literal("No Mobs"),
                                Component.literal("Players Only"),
                                Component.literal("Projectiles Only"),
                                Component.literal("VS2 Only"),
                                Component.literal("Mob Bosses Only")))
                        .titled(Component.literal("Show")),
                "Filter");
    }
}
