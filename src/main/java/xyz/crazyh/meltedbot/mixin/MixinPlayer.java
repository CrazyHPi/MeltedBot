package xyz.crazyh.meltedbot.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.crazyh.meltedbot.player.ServerPlayerFake;

@Mixin(Player.class)
public abstract class MixinPlayer {
    @Redirect(
            method = "attack",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/entity/Entity;hurtMarked:Z",
                    ordinal = 0
            )
    )
    private boolean knockbackFake(Entity instance) {
        return instance.hurtMarked && !(instance instanceof ServerPlayerFake);
    }
}
