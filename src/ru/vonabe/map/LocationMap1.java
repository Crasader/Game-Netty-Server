package ru.vonabe.map;

import java.util.Collection;
import java.util.HashMap;

import com.badlogic.gdx.math.Rectangle;

import ru.vonabe.manager.Client;
import ru.vonabe.manager.ClientManager;
import ru.vonabe.packet.PacketWriter;

public class LocationMap1 extends Location {

    private HashMap<String, Client> hashplayer = new HashMap<String, Client>();
    private String name = "Location 1";
    private int id_location = 1;

    @Override
    public boolean addPlayer(String uuid) {
        // TODO Auto-generated method stub
        return (hashplayer.put(uuid, ClientManager.getClient(uuid)) == null) ? false : true;
    }

    @Override
    public boolean removePlayer(String uuid) {
	// TODO Auto-generated method stub
	return hashplayer.remove(uuid, ClientManager.getClient(uuid));
    }

    @Override
    public void render(float delta) {

    }

    @Override
    public String getName() {
	// TODO Auto-generated method stub
	return name;
    }

    @Override
    public int getIdLocation() {
	// TODO Auto-generated method stub
	return id_location;
    }

    @Override
    public Collection<Client> getClients() {
	// TODO Auto-generated method stub
	return hashplayer.values();
    }

    @Override
    public HashMap<String, Rectangle> getHashmapBlocks() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public PacketWriter getMapInfo() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void addBattle(String uuid_attack, String uuid_attacked) {
	// TODO Auto-generated method stub

    }

    @Override
    public void removeBattle(String uuid_attack, String uuid_attacked) {
	// TODO Auto-generated method stub

    }

}
