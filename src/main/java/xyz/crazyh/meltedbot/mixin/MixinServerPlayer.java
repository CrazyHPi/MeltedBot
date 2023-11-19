package xyz.crazyh.meltedbot.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.crazyh.meltedbot.MeltedBot;
import xyz.crazyh.meltedbot.fakes.IServerPlayer;
import xyz.crazyh.meltedbot.helpers.EntityPlayerActionPack;

@Mixin(ServerPlayer.class)
public abstract class MixinServerPlayer implements IServerPlayer {
    @Unique
    public EntityPlayerActionPack actionPack;

    @Override
    public EntityPlayerActionPack getActionPack() {
        return actionPack;
    }

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void onServerPlayerConstructor(MinecraftServer p_143384_, ServerLevel p_143385_, GameProfile p_143386_, CallbackInfo ci) {
        this.actionPack = new EntityPlayerActionPack((ServerPlayer) (Object) this);
    }

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void onTick(CallbackInfo ci) {
        try {
            actionPack.onUpdate();
        } catch (StackOverflowError e) {
            MeltedBot.LOGGER.error("Caused stack overflow when performing player action", e);
        }catch (Throwable e) {
            MeltedBot.LOGGER.error("Error executing player tasks", e);
        }
    }
}
