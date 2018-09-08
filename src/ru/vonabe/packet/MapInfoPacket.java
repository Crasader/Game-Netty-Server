package ru.vonabe.packet;

import org.json.simple.JSONObject;

import ru.vonabe.manager.ClientManager;
import ru.vonabe.manager.MapManager;
import ru.vonabe.map.Location;

public class MapInfoPacket implements Packet {

    private FastData data = null;

    @Override
    public void handler() {
        JSONObject object = data.getData();
        String map_id = object.get("map_id").toString();
        Location location = MapManager.getMap(map_id);
        ClientManager.getClient(data.getUuid()).write(location.getMapInfo());
    }

    @Override
    public void setData(FastData data) {
	// TODO Auto-generated method stub
	this.data = data;
    }

    @Override
    public FastData getData() {
	// TODO Auto-generated method stub
	return data;
    }

}
