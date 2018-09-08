package ru.vonabe.packet;

import org.json.simple.JSONObject;
import ru.vonabe.manager.ClientManager;

public class MovePacket implements Packet {

    private FastData data = null;

    @Override
    public void handler() {
        if (!ClientManager.getClient(data.getUuid()).auto)
            return;
        JSONObject object = this.data.getData();
        MoveType move = MoveType.valueOf(object.get("move").toString());
        ClientManager.getClient(data.getUuid()).player.setMove(move);
    }

    @Override
    public void setData(FastData data) {
	this.data = data;
    }

    @Override
    public FastData getData() {
	return this.data;
    }

    public static enum MoveType {
	TOP, BOT, RIGHT, LEFT, STOP
    }

}
