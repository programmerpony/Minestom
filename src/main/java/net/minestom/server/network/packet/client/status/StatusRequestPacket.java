package net.minestom.server.network.packet.client.status;

import net.minestom.server.MinecraftServer;
import net.minestom.server.network.ConnectionManager;
import net.minestom.server.network.packet.PacketReader;
import net.minestom.server.network.packet.client.ClientPreplayPacket;
import net.minestom.server.network.packet.server.handshake.ResponsePacket;
import net.minestom.server.network.player.PlayerConnection;
import net.minestom.server.ping.ResponseData;
import net.minestom.server.ping.ResponseDataConsumer;

public class StatusRequestPacket implements ClientPreplayPacket {

    @Override
    public void process(PlayerConnection connection, ConnectionManager connectionManager) {
        ResponseDataConsumer consumer = MinecraftServer.getResponseDataConsumer();
        ResponseData responseData = new ResponseData();

        // Fill default params
        responseData.setName("1.15.2");
        responseData.setProtocol(MinecraftServer.PROTOCOL_VERSION);
        responseData.setMaxPlayer(0);
        responseData.setOnline(0);
        responseData.setDescription("Minestom Server");
        responseData.setFavicon("data:image/png;base64,<data>");

        if (consumer != null)
            consumer.accept(connection, responseData);

        ResponsePacket responsePacket = new ResponsePacket();
        responsePacket.jsonResponse = responseData.build().toString();

        connection.sendPacket(responsePacket);
    }

    @Override
    public void read(PacketReader reader) {
        // Empty
    }
}
