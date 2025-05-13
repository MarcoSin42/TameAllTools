package com.example.tamealltools;

import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.client.event.InputEvent.MouseButton.Post;

import org.jetbrains.annotations.Nullable;
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

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onBlockUse(PlayerInteractEvent.RightClickBlock event)
    {
        Level level = event.getLevel();
        Player player = event.getEntity();
        Item itemUsed = event.getItemStack().getItem();

        BlockPos blockPos = event.getPos();
        BlockState blockState = level.getBlockState(blockPos);
        BlockHitResult blockHit = (BlockHitResult) Minecraft.getInstance().hitResult;

        UseOnContext context = new UseOnContext(player, player.getUsedItemHand(), blockHit);
        boolean isFlattenable = blockState.getToolModifiedState(context, ToolActions.SHOVEL_FLATTEN, false) != null;

        if (itemUsed.isCorrectToolForDrops(blockState) && blockState.canHarvestBlock(level, blockPos, player)) {
            if ((isFlattenable && event.getFace() == Direction.UP) ||// Handle special case for choosing when to dig and when to flatten
                    hasBrokenBlock) {
                return;
            }

            event.setCanceled(true);
            if (blockState.getDestroyProgress(player, level, blockPos) > 0) {
                Minecraft.getInstance().gameMode.continueDestroyBlock(blockPos, blockHit.getDirection());
            } else {
                Minecraft.getInstance().gameMode.startDestroyBlock(blockPos, blockHit.getDirection());
            }
        }
    }

    @SubscribeEvent(receiveCanceled = true)
    public void onKeyInputEvent(Post event) {
        // Checks if LMB is released
        if (event.getButton() == 0 || event.getButton() == 1)
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

        if (hasBrokenBlock && player.isDescending() && itemUsed.isCorrectToolForDrops(blockState)) {
            if (event.isCancelable())
                event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        hasBrokenBlock = true;
    }
}
