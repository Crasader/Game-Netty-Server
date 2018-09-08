package ru.vonabe.packet;

import com.badlogic.gdx.utils.Pool;

public class PoolPacketWriter {

    private static Pool<PacketWriter> pool = new Pool<PacketWriter>() {
	@Override
	protected PacketWriter newObject() {
	    return new PacketWriter();
	}
    };

    public static PacketWriter getWriter() {
	PacketWriter writer = pool.obtain();
	writer.clear();
	return writer;
    }

    public static void free(PacketWriter writer) {
	pool.free(writer);
    }

}
