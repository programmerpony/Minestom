package net.minestom.server.network.packet.server.login;

import net.minestom.server.network.packet.PacketWriter;
import net.minestom.server.network.packet.server.ServerPacket;

import java.util.UUID;

public class LoginSuccessPacket implements ServerPacket {

    public UUID uuid;
    public String username;

    public LoginSuccessPacket(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    @Override
    public void write(PacketWriter writer) {
        writer.writeSizedString(uuid.toString()); // TODO mojang auth
        writer.writeSizedString(username);
    }

    @Override
    public int getId() {
        return 0x02;
    }
}
