package ru.vonabe.manager;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ru.vonabe.handler.Renderer;
import ru.vonabe.player.Player;

public class BattleManager implements Renderer {

    public final static float timer = 90;

    private static Map<String, Player> battles = new ConcurrentHashMap<String, Player>();

    public static Player getPlayer(String login) {
	return battles.get(login);
    }

    public static boolean contains(String login) {
	return battles.containsKey(login);
    }

    public static void addBattle(Player player) {
	battles.put(player.getName(), player);
    }

    public static void removeBattle(String name) {
	battles.remove(name);
    }

    @Override
    public void render(float delta) {
	Iterator<String> it = battles.keySet().iterator();
	while (it.hasNext()) {
	    String name = it.next();
	    Player player = battles.get(name);
	    player.getArmy().act(delta);
	    if (player.getArmy().getTime() <= 0) {
		player.getArmy().escape();
	    }
	    // if (player.getArmy().isAllStep()) {
	    // player.att
	    // }
	}
    }

}
