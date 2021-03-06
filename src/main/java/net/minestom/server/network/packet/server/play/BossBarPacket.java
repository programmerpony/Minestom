package net.minestom.server.network.packet.server.play;

import net.kyori.text.Component;
import net.minestom.server.bossbar.BarColor;
import net.minestom.server.bossbar.BarDivision;
import net.minestom.server.chat.Chat;
import net.minestom.server.network.packet.PacketWriter;
import net.minestom.server.network.packet.server.ServerPacket;
import net.minestom.server.network.packet.server.ServerPacketIdentifier;

import java.util.UUID;

public class BossBarPacket implements ServerPacket {

    public UUID uuid;
    public Action action;

    public Component title;
    public float health;
    public BarColor color;
    public BarDivision division;
    public byte flags;


    @Override
    public void write(PacketWriter writer) {
        writer.writeUuid(uuid);
        writer.writeVarInt(action.ordinal());

        switch (action) {
            case ADD:
                writer.writeSizedString(Chat.toJsonString(title));
                writer.writeFloat(health);
                writer.writeVarInt(color.ordinal());
                writer.writeVarInt(division.ordinal());
                writer.writeByte(flags);
                break;
            case REMOVE:

                break;
            case UPDATE_HEALTH:
                writer.writeFloat(health);
                break;
            case UPDATE_TITLE:
                writer.writeSizedString(Chat.toJsonString(title));
                break;
            case UPDATE_STYLE:
                writer.writeVarInt(color.ordinal());
                writer.writeVarInt(division.ordinal());
                break;
            case UPDATE_FLAGS:
                writer.writeByte(flags);
                break;
        }
    }

    @Override
    public int getId() {
        return ServerPacketIdentifier.BOSS_BAR;
    }

    public enum Action {
        ADD,
        REMOVE,
        UPDATE_HEALTH,
        UPDATE_TITLE,
        UPDATE_STYLE,
        UPDATE_FLAGS
    }

}
