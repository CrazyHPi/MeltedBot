package xyz.crazyh.meltedbot.player;

import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class NetHandlerPlayServerFake extends ServerGamePacketListenerImpl {
    public NetHandlerPlayServerFake(MinecraftServer server, Connection cc, ServerPlayer playerIn) {
        super(server, cc, playerIn);
    }

    @Override
    public void send(final Packet<?> packetIn) {
    }

    @Override
    public void disconnect(Component message) {
        if (message instanceof TranslatableComponent text && (text.getKey().equals("multiplayer.disconnect.idling") || text.getKey().equals("multiplayer.disconnect.duplicate_login"))) {
            ((ServerPlayerFake) player).kill(message);
        }
    }
}
