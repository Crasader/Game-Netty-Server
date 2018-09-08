package ru.vonabe.packet;

import org.json.simple.JSONObject;

import ru.vonabe.manager.AuthorizationManager;
import ru.vonabe.manager.ClientManager;

public class AuthorizationPacket implements Packet {

    private FastData data = null;
    private String login = null, password = null;

    @Override
    public void handler() {
	JSONObject object = this.data.getData();
	this.login = object.get("login").toString();
	this.password = object.get("password").toString();

	boolean authorization = AuthorizationManager.authorization(login, password);
	if (authorization) {
	    ClientManager.getClient(this.data.getUuid()).setAuto(true, login);
	} else {
	    PacketWriter writer = PoolPacketWriter.getWriter();
	    JSONObject data = writer.getData();
	    data.put("message", "Неверный логин или пароль.");
	    JSONObject obj = writer.getObject();
	    obj.put("action", "error");
	    obj.put("data", data);
	    ClientManager.getClient(this.data.getUuid()).write(writer);
	    // writer.clear();
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
