package net.minestom.server.network.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.network.ConnectionManager;
import net.minestom.server.network.PacketProcessor;
import net.minestom.server.network.netty.packet.PacketHandler;
import net.minestom.server.network.packet.PacketReader;
import net.minestom.server.network.packet.client.status.LegacyServerListPingPacket;
import net.minestom.server.network.player.PlayerConnection;
import net.minestom.server.utils.Utils;

public class ClientChannel extends ChannelInboundHandlerAdapter {

    private ConnectionManager connectionManager = MinecraftServer.getConnectionManager();
    private PacketProcessor packetProcessor;

    public ClientChannel(PacketProcessor packetProcessor) {
        this.packetProcessor = packetProcessor;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("CONNECTION");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj) {
        PacketHandler packetHandler = (PacketHandler) obj;

        int packetLength = packetHandler.length;
        ByteBuf buffer = packetHandler.buffer;

        if (packetLength == 0xFE) { // Legacy server ping
            LegacyServerListPingPacket legacyServerListPingPacket = new LegacyServerListPingPacket();
            legacyServerListPingPacket.read(new PacketReader(buffer));
            legacyServerListPingPacket.process(null, null);
            return;
        }

        final int varIntLength = Utils.lengthVarInt(packetLength);
        int packetId = Utils.readVarInt(buffer);

        int offset = varIntLength + Utils.lengthVarInt(packetId);
        packetProcessor.process(ctx, buffer, packetId, offset);

        buffer.release();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        PlayerConnection playerConnection = packetProcessor.getPlayerConnection(ctx);
        if (playerConnection != null) {
            playerConnection.refreshOnline(false);
            Player player = playerConnection.getPlayer();
            if (player != null) {
                player.remove();
                connectionManager.removePlayer(playerConnection);
            }
            packetProcessor.removePlayerConnection(ctx);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
