package ru.vonabe.handler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.Timer;

import com.badlogic.gdx.utils.Graphics;

import ru.vonabe.manager.BattleManager;
import ru.vonabe.manager.Client;
import ru.vonabe.manager.ClientManager;
import ru.vonabe.manager.MapManager;
import ru.vonabe.map.Location;

public class GameLogicHandler implements ActionListener {

    private float delta = 0.0f;
    private BattleManager battle = new BattleManager();
    private Graphics graphics = new Graphics();

    public GameLogicHandler(int fps) {
	Timer timer = new Timer(1000 / fps, this);
	timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	delta = graphics.getDeltaTime();

	Collection<Location> locations = MapManager.getPoolMap();

	Iterator<Location> it_maps = locations.iterator();
	while (it_maps.hasNext()) {
	    Location map = it_maps.next();
	    map.render(delta);
	}

	Iterator<Client> it_clients = ClientManager.getClients().iterator();
	while (it_clients.hasNext()) {
	    Client client = it_clients.next();
	    if (client.auto) {
		client.render(delta);
	    }
	}
	battle.render(delta);

	graphics.updateTime();

    }

}
