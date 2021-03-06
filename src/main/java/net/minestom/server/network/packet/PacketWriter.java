package net.minestom.server.network.packet;

import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.BlockPosition;
import net.minestom.server.utils.SerializerUtils;
import net.minestom.server.utils.Utils;
import net.minestom.server.utils.buffer.BufferWrapper;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.Consumer;

public class PacketWriter {

    private ByteArrayOutputStream output = new ByteArrayOutputStream();
    private DataOutputStream data = new DataOutputStream(output);

    public PacketWriter() {
    }

    public void writeBoolean(boolean b) {
        try {
            data.writeBoolean(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeByte(byte b) {
        try {
            data.writeByte(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeChar(char s) {
        try {
            data.writeChar(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeShort(short s) {
        try {
            data.writeShort(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeInt(int i) {
        try {
            data.writeInt(i);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeLong(long l) {
        try {
            data.writeLong(l);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeFloat(float f) {
        try {
            data.writeFloat(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeDouble(double d) {
        try {
            data.writeDouble(d);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeVarInt(int i) {
        Utils.writeVarInt(this, i);
    }

    public void writeVarLong(long l) {
        Utils.writeVarLong(this, l);
    }

    public void writeSizedString(String string) {
        byte[] bytes;
        bytes = string.getBytes(StandardCharsets.UTF_8);
        writeVarInt(bytes.length);
        writeBytes(bytes);
    }

    public void writeShortSizedString(String string) {
        byte[] bytes;
        bytes = string.getBytes(StandardCharsets.UTF_8);
        writeShort((short) bytes.length);
        writeBytes(bytes);
    }

    public void writeVarIntArray(int[] array) {
        if (array == null) {
            writeVarInt(0);
            return;
        }
        writeVarInt(array.length);
        for (int element : array) {
            writeVarInt(element);
        }
    }

    public void writeBytes(byte[] bytes) {
        try {
            data.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeStringArray(String[] array) {
        if (array == null) {
            writeVarInt(0);
            return;
        }
        writeVarInt(array.length);
        for (String element : array) {
            writeSizedString(element);
        }
    }

    public void write(Consumer<PacketWriter> consumer) {
        if (consumer != null)
            consumer.accept(this);
    }

    public void writeBufferAndFree(BufferWrapper buffer) {
        ByteBuffer byteBuffer = buffer.getByteBuffer();
        int size = buffer.getSize();
        byte[] cache = new byte[size];
        byteBuffer.position(0).get(cache, 0, size);
        writeBytes(cache);
        buffer.free();
    }

    public void writeUuid(UUID uuid) {
        writeLong(uuid.getMostSignificantBits());
        writeLong(uuid.getLeastSignificantBits());
    }

    public void writeBlockPosition(BlockPosition blockPosition) {
        writeBlockPosition(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
    }

    public void writeBlockPosition(int x, int y, int z) {
        writeLong(SerializerUtils.positionToLong(x, y, z));
    }

    public void writeItemStack(ItemStack itemStack) {
        Utils.writeItemStack(this, itemStack);
    }

    public byte[] toByteArray() {
        return output.toByteArray();
    }

}
