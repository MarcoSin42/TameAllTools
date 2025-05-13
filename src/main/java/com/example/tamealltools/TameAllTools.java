package com.example.tamealltools;

import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.client.event.InputEvent.MouseButton.Post;

import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(TameAllTools.MODID)
public class TameAllTools
{
    public static final String MODID = "tamealltools";
    private static final Logger LOGGER = LogUtils.getLogger();

    private boolean hasBrokenBlock = false;

    public TameAllTools(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();
        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }


    private void commonSetup(final FMLCommonSetupEvent event)
    {
    }

    /**
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBlockUse(PlayerInteractEvent.RightClickBlock event)
    {
        Player player = event.getEntity();
        Item itemUsed = event.getItemStack().getItem();
        Level level = event.getLevel();

        BlockPos blockPos = event.getPos();
        BlockState blockState = event.getLevel().getBlockState(blockPos);

        if (itemUsed.isCorrectToolForDrops(blockState)) {
            blockState.
        }
    }
     */

    @SubscribeEvent(receiveCanceled = true)
    public void onKeyInputEvent(Post event) {
        // Checks if LMB is released
        if (event.getButton() == 0)
            hasBrokenBlock = false;

    }

    @SubscribeEvent
    public void onMining(PlayerInteractEvent.LeftClickBlock event)
    {
        Level level = event.getLevel();
        if (!level.isClientSide()) return;

        Player player = event.getEntity();
        Item itemUsed = event.getItemStack().getItem();

        BlockPos blockPos = event.getPos();
        BlockState blockState = event.getLevel().getBlockState(blockPos);

        if (hasBrokenBlock && (player.isCrouching() || player.isDescending()) && itemUsed.isCorrectToolForDrops(blockState)) {
            if (event.isCancelable())
                event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        hasBrokenBlock = true;
    }
}
