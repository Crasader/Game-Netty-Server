package ru.vonabe.packet;

import org.json.simple.JSONObject;

public class PacketWriter {

    private JSONObject data = new JSONObject(), object = new JSONObject();

    public PacketWriter(JSONObject data, JSONObject object) {
	// TODO Auto-generated constructor stub
	this.data = data;
	this.object = object;
    }

    public PacketWriter() {
	// TODO Auto-generated constructor stub
    }

    public JSONObject getData() {
	return data;
    }

    public JSONObject getObject() {
	return object;
    }

    public void clear() {
	object.clear();
	data.clear();
    }

    @Override
    public String toString() {
	// TODO Auto-generated method stub
	return object.toJSONString();
    }

    @Override
    public PacketWriter clone() throws CloneNotSupportedException {
	// TODO Auto-generated method stub
	return new PacketWriter((JSONObject) data.clone(), (JSONObject) object.clone());
    }

}
