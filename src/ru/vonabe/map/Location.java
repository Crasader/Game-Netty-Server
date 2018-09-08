package ru.vonabe.map;

import java.util.Collection;
import java.util.HashMap;

import com.badlogic.gdx.math.Rectangle;

import ru.vonabe.handler.Renderer;
import ru.vonabe.manager.Client;
import ru.vonabe.packet.PacketWriter;

public abstract class Location implements Renderer {

    public abstract boolean addPlayer(String uuid);

    public abstract boolean removePlayer(String uuid);

    public abstract void addBattle(String uuid_attack, String uuid_attacked);

    public abstract void removeBattle(String uuid_attack, String uuid_attacked);

    public abstract String getName();

    public abstract int getIdLocation();

    public abstract Collection<Client> getClients();

    public abstract HashMap<String, Rectangle> getHashmapBlocks();

    public abstract PacketWriter getMapInfo();

}
