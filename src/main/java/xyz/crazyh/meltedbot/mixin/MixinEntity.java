package xyz.crazyh.meltedbot.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.crazyh.meltedbot.player.ServerPlayerFake;

import javax.annotation.Nullable;

@Mixin(Entity.class)
public abstract class MixinEntity {
    @Shadow
    public Level level;

    @Shadow
    @Nullable
    public abstract Entity getControllingPassenger();

    @Inject(method = "isControlledByLocalInstance", at = @At("HEAD"), cancellable = true)
    private void isFakePlayer(CallbackInfoReturnable<Boolean> cir) {
        if (getControllingPassenger() instanceof ServerPlayerFake) cir.setReturnValue(!level.isClientSide);
    }
}
