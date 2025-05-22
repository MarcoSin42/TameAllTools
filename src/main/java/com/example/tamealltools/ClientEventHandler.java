package com.example.tamealltools;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;


@Mod.EventBusSubscriber(modid = TameAllTools.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventHandler {
    private static boolean hasBrokenBlock = false;
    private static Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void onKeyInputEvent(InputEvent.MouseButton.Post event) {
        //LOGGER.info("Key released");
        if (event.getButton() == 0)
            hasBrokenBlock = false;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void onMining(PlayerInteractEvent.LeftClickBlock event)
    {
        Player player = event.getEntity();
        Item itemUsed = event.getItemStack().getItem();

        BlockPos blockPos = event.getPos();
        BlockState blockState = event.getLevel().getBlockState(blockPos);
        if (hasBrokenBlock && player.isDescending() && itemUsed.isCorrectToolForDrops(blockState)) {
            if (event.isCancelable())
                event.setCanceled(true);
        }
    }

    public static void handleBlockUpdate(BlockState blockState, BlockPos blockPos) {
        Minecraft instance = Minecraft.getInstance();
        HitResult hitResult = instance.hitResult;

        if (blockState.isAir()) {
            if (!hitResult.getLocation().closerThan(blockPos.getCenter(), 1))
                return;
            //LOGGER.info("Destroyed block");
            hasBrokenBlock = true;
        }

    }
}
