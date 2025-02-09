package com.happysg.radar.block.controller.id;

import com.happysg.radar.compat.Mods;
import com.happysg.radar.compat.vs2.VS2Utils;
import com.simibubi.create.foundation.gui.ScreenOpener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.api.ships.Ship;

public class IDBlock extends Block {
    public IDBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, @NotNull Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!Mods.VALKYRIENSKIES.isLoaded()) {
            pPlayer.displayClientMessage(Component.translatable("create_radar.id_block.not_on_vs2"), true);
            return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
        }
        Ship ship = VS2Utils.getShipManagingPos(pLevel, pPos);
        if (ship == null) {
            pPlayer.displayClientMessage(Component.translatable("create_radar.id_block.not_on_ship"), true);
            return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
        }
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> this.displayScreen(ship, pPlayer));
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        Ship ship = VS2Utils.getShipManagingPos(pLevel, pPos);
        if (ship != null) {
            IDManager.removeIDRecord(ship);
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @OnlyIn(value = Dist.CLIENT)
    protected void displayScreen(Ship ship, Player player) {
        if (!(player instanceof LocalPlayer))
            return;

        ScreenOpener.open(new IDBlockScreen(ship));
    }
}
