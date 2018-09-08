package ru.vonabe.packet;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class FastData {

    final private String uuid;
    private String packet_type = null;
    private JSONObject data = null;
    private boolean validation = false;

    public FastData(String uuid, String message) {
	this.uuid = uuid;
	// Parsing message to JSON ...
	try {
	    JSONObject object = (JSONObject) new JSONParser().parse(message);
	    packet_type = object.get("action").toString();
	    data = (JSONObject) object.get("data");
	    validation = true;
	} catch (ParseException e) {
	    validation = false;
	}
    }

    public boolean valid() {
	return validation;
    }

    public String getPacketType() {
	return this.packet_type;
    }

    public JSONObject getData() {
	return this.data;
    }

    public String getUuid() {
	return this.uuid;
    }

}
