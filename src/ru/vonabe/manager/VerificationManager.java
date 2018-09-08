package ru.vonabe.manager;

import java.util.HashMap;

import org.json.simple.JSONObject;

import ru.vonabe.packet.PacketWriter;
import ru.vonabe.packet.PoolPacketWriter;

public class VerificationManager {

    private static HashMap<String, String> hashmapVerify = new HashMap<String, String>();

    public static void onVerify(String uuid) {
	hashmapVerify.put(uuid, "vonabe");

	PacketWriter writer = PoolPacketWriter.getWriter();
	JSONObject data = writer.getData();
	JSONObject object = writer.getObject();
	data.put("key", uuid);
	object.put("action", "verify");
	object.put("data", data);

	ClientManager.getClient(uuid).write(writer);
    }

    public static boolean verification(String uuid, String key) {
	String code = hashmapVerify.get(uuid);
	if (key.equals(code)) {
	    hashmapVerify.remove(uuid);
	    return true;
	} else
	    return false;
    }

}
