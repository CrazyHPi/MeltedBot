package xyz.crazyh.meltedbot.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xyz.crazyh.meltedbot.player.NetHandlerPlayServerFake;
import xyz.crazyh.meltedbot.player.ServerPlayerFake;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Mixin(PlayerList.class)
public abstract class MixinPlayerList {

    @Shadow
    @Final
    private MinecraftServer server;

    @Inject(
            method = "load",
            at = @At(
                    value = "RETURN",
                    shift = At.Shift.BEFORE
            )
    )
    private void fixStartingPos(ServerPlayer player, CallbackInfoReturnable<CompoundTag> cir) {
        if (player instanceof ServerPlayerFake) {
            ((ServerPlayerFake) player).fixStartingPosition.run();
        }
    }

    @Redirect(
            method = "placeNewPlayer",
            at = @At(
                    value = "NEW",
                    target = "net/minecraft/server/network/ServerGamePacketListenerImpl"
            )
    )
    private ServerGamePacketListenerImpl replaceNetworkHandler(MinecraftServer server, Connection connection, ServerPlayer playerIn) {
        /*if (playerIn instanceof ServerPlayerFake) {
            return new NetHandlerPlayServerFake(this.server, connection, (ServerPlayerFake) playerIn);
        } else {
            return new ServerGamePacketListenerImpl(this.server, connection, playerIn);
        }*/

        boolean isServerPlayerEntity = playerIn instanceof ServerPlayerFake;
        if (isServerPlayerEntity) {
            return new NetHandlerPlayServerFake(this.server, connection, playerIn);
        } else {
            return new ServerGamePacketListenerImpl(this.server, connection, playerIn);
        }
    }
}
