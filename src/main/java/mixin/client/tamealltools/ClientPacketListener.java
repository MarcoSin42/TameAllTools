package mixin.client.tamealltools;

import com.example.tamealltools.ClientEventHandler;
import com.mojang.logging.LogUtils;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientGamePacketListener.class)
public abstract class ClientPacketListener {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Inject(method = "handleBlockDestruction", at = @At("HEAD"))
    private void onBlockDestruction(ClientboundBlockDestructionPacket par1, CallbackInfo ci) {
        LOGGER.info("Received destruction packet");
        ClientEventHandler.onBlockBreak();
    }
}
