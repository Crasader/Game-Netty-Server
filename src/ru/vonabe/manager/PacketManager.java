package ru.vonabe.manager;

import java.util.HashMap;
import java.util.Map;

import ru.vonabe.packet.*;

public class PacketManager {

    final private static Map<String, Class<? extends Packet>> map_packets = new HashMap<>();

    static {
	map_packets.put("reg", RegistrationPacket.class);
	map_packets.put("auto", AuthorizationPacket.class);
	map_packets.put("verify", VerificationPacket.class);
	map_packets.put("move", MovePacket.class);
	map_packets.put("mapinfo", MapInfoPacket.class);
	map_packets.put("attack", AttackPacket.class);
	map_packets.put("battlestep", BattleStepPacket.class);
    }

    public static Packet getPacket(String key) {
	try {
	    return map_packets.get(key).newInstance();
	} catch (InstantiationException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IllegalAccessException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return null;
    }

}
