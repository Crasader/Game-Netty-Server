package ru.vonabe.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ClientManager {

    private static Map<String, Client> mapClients = new HashMap<String, Client>();

    private ClientManager() {
    }

    public static Client getClient(String uuid) {
	return mapClients.get(uuid);
    }

    public static Client getClientName(String name) {
	Iterator<Client> clients = mapClients.values().iterator();
	while (clients.hasNext()) {
	    Client client = clients.next();
	    if (client.player.getName().equals(name)) {
		return client;
	    }
	}
	return null;
    }

    public static boolean registerClient(Client client) {
	return (mapClients.put(client.uuid, client) == null) ? true : false;
    }

    public static boolean unregisterClient(Client client) {
	return mapClients.remove(client.uuid, client);
    }

    public static boolean contains(String key) {
	return mapClients.containsKey(key);
    }

    public static boolean contains(Client key) {
	return mapClients.containsValue(key);
    }

    public static void clearAllClients() {
	mapClients.clear();
    }

    public static Collection<Client> getClients() {
	return mapClients.values();
    }

}
