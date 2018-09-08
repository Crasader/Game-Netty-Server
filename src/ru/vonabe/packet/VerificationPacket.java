package ru.vonabe.packet;

import org.json.simple.JSONObject;

import ru.vonabe.manager.ClientManager;
import ru.vonabe.manager.VerificationManager;

public class VerificationPacket implements Packet {

    private FastData data = null;

    @Override
    public void handler() {
	JSONObject json_data = data.getData();
	String key = json_data.get("key").toString();
	boolean verify = VerificationManager.verification(data.getUuid(), key);
	ClientManager.getClient(data.getUuid()).verification(verify);
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
