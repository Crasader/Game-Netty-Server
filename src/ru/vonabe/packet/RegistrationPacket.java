package ru.vonabe.packet;

import org.json.simple.JSONObject;

import ru.vonabe.manager.ClientManager;
import ru.vonabe.manager.RegistrationManager;

public class RegistrationPacket implements Packet {

    private FastData data = null;

    @Override
    public void handler() {
        JSONObject object = this.data.getData();
        String login = object.get("login").toString();
        String password = object.get("password").toString();
        String email = object.get("email").toString();
        String reg = RegistrationManager.registration(login, password, email, data.getUuid());
        if (reg == null) {
            ClientManager.getClient(data.getUuid()).setAuto(true, login);
        } else {
            PacketWriter writer = PoolPacketWriter.getWriter();
            JSONObject data = writer.getData();
            data.put("message", reg);
            JSONObject obj = writer.getObject();
            obj.put("action", "error");
            obj.put("data", data);
            ClientManager.getClient(this.data.getUuid()).write(writer);
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
