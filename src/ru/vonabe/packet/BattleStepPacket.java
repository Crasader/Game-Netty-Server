package ru.vonabe.packet;

import org.json.simple.JSONObject;

import ru.vonabe.manager.ClientManager;

public class BattleStepPacket implements Packet {

    private FastData data = null;

    @Override
    public void handler() {
	if (!ClientManager.getClient(data.getUuid()).auto || !ClientManager.getClient(data.getUuid()).player.isBattle())
	    return;
	JSONObject data = this.data.getData();
	int[] from = (int[]) data.get("from");
	int[] to = (int[]) data.get("to");
	int[] addition = (int[]) data.get("addition");

	if (ClientManager.getClient(this.data.getUuid()).player.isBattle()) {
	    ClientManager.getClient(this.data.getUuid()).player.getArmy().myAttack(from, to, addition);
	}
    }

    @Override
    public void setData(FastData data) {
	this.data = data;
    }

    @Override
    public FastData getData() {
	return this.data;
    }

}
