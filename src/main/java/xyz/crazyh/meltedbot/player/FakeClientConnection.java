package xyz.crazyh.meltedbot.player;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;

public class FakeClientConnection extends Connection {
    public FakeClientConnection(PacketFlow packetFlow) {
        super(packetFlow);

        try {
            this.channelActive(new FakeChannelHandlerContext());
        } catch (Exception ignored) {
        }
    }

    @Override
    public void setReadOnly() {
    }

    @Override
    public void handleDisconnection() {
    }


}
