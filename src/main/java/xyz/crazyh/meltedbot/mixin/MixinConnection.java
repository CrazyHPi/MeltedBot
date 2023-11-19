package xyz.crazyh.meltedbot.mixin;

import io.netty.channel.Channel;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import xyz.crazyh.meltedbot.fakes.IClientConnection;

@Mixin(Connection.class)
public abstract class MixinConnection implements IClientConnection {

    @Override
    @Accessor //Compat with adventure-platform-fabric
    public abstract void setChannel(Channel channel);
}
