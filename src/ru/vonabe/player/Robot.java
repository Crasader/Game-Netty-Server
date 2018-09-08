package ru.vonabe.player;

import org.json.simple.JSONObject;

public class Robot extends Unit {

    private JSONObject object = new JSONObject();

    public Robot(int weapons, int protection, int health, int techno_weapons, int techno_protection, int techno_health, boolean attack_techno_all,
	    boolean protection_techno_all, boolean health_techno_all, int progress) {
	super(weapons, protection, health, techno_weapons, techno_protection, techno_health, attack_techno_all, protection_techno_all,
		health_techno_all, progress);
    }

    @Override
    public JSONObject getPacket() {
	object.clear();
	object.put("type", "robot");
	object.put("w", super.getWeapons());
	object.put("p", super.getProtection());
	object.put("h", super.getHealth());
	object.put("tw", super.getTechnoWeapons());
	object.put("tp", super.getTechnoProtection());
	object.put("th", super.getTechnoHealth());
	object.put("ps", super.getProgress());
	object.put("ata", super.isAttackTechnoAll());
	object.put("pta", super.isProtectionTechnoAll());
	object.put("hta", super.isHealthTechnoAll());
	return object;
    }

}
