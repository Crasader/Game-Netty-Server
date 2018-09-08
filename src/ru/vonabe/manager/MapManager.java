package ru.vonabe.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONObject;

import ru.vonabe.map.Location;
import ru.vonabe.map.LocationMap0;
import ru.vonabe.map.LocationMap1;
import ru.vonabe.packet.PacketWriter;
import ru.vonabe.packet.PoolPacketWriter;

public class MapManager {

    final private static HashMap<String, Class<? extends Location>> hashMap = new HashMap<String, Class<? extends Location>>();

    final private static HashMap<String, Location> poolmap = new HashMap<String, Location>();

    final private static HashMap<String, Location> hashplayer = new HashMap<String, Location>();

    static {
	hashMap.put("0", LocationMap0.class);
	hashMap.put("1", LocationMap1.class);
    }

    private MapManager() {
    }

    public static boolean registration(String uuid, String id_map) {
	unregistration(uuid);
	Location location = getMap(id_map);
	if (location.addPlayer(uuid)) {
	    hashplayer.put(uuid, location);
	    notifyClientsSpawn(location, uuid);
	    return true;
	}
	return false;
    }

    public static boolean unregistration(String uuid) {
	if (hashplayer.containsKey(uuid)) {
	    Location location = hashplayer.get(uuid);
	    notifyClientsUnspawn(location, uuid);
	    hashplayer.remove(uuid, location);
	    boolean remove = location.removePlayer(uuid);
	    return remove;
	} else
	    return false;
    }

    public static Location getMap(String id_map) {
	if (poolmap.containsKey(id_map)) {
	    return poolmap.get(id_map);
	} else {
	    try {
		Location location = hashMap.get(id_map).newInstance();
		poolmap.put(id_map, location);
		return location;
	    } catch (InstantiationException | IllegalAccessException e) {
	    }
	    return null;
	}
    }

    public static Collection<Location> getPoolMap() {
	return poolmap.values();
    }

    private static void notifyClientsSpawn(Location location, String uuid) {
	Client client = ClientManager.getClient(uuid);

	PacketWriter writer = PoolPacketWriter.getWriter();

	JSONObject data = writer.getData();
	data.put("login", client.player.getName());
	data.put("x", client.player.getPosition().x);
	data.put("y", client.player.getPosition().y);
	data.put("id_map", location.getIdLocation());
	JSONObject object = writer.getObject();
	object.put("action", "spawn_player");
	object.put("data", data);

	Collection<Client> clients = location.getClients();

	Iterator<Client> it_clients = clients.iterator();
	while (it_clients.hasNext()) {
	    Client c = it_clients.next();
	    c.write(writer);
	}

	// writer.clear();

    }

    private static void notifyClientsUnspawn(Location location, String uuid) {
	Client client = ClientManager.getClient(uuid);

	PacketWriter writer = PoolPacketWriter.getWriter();

	JSONObject data = writer.getData();
	data.put("name", client.player.getName());
	data.put("id_map", location.getIdLocation());
	JSONObject object = writer.getObject();
	object.put("action", "remove_player");
	object.put("data", data);

	Collection<Client> clients = location.getClients();

	Iterator<Client> it_clients = clients.iterator();
	while (it_clients.hasNext()) {
	    Client c = it_clients.next();
	    c.write(writer);
	}
	// writer.clear();

    }

}
