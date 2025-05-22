package mixin.client.tamealltools;


import com.example.tamealltools.ClientEventHandler;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ClientPacketListener.class)
public class blockUpdateHandler {
    @Inject(method = "handleBlockUpdate", at = @At("HEAD"))
    public void onBlockUpdate(ClientboundBlockUpdatePacket p_104980_, CallbackInfo ci) {
        ClientEventHandler.handleBlockUpdate(p_104980_.getBlockState(), p_104980_.getPos());
    }
}
