package ru.vonabe.map;

import org.json.simple.JSONObject;

public class BattleFlag {

    private JSONObject object = new JSONObject();
    public String name_attack = null, name_attacked = null, time = null;
    public float x, y;

    public JSONObject toObject() {
	object.clear();
	object.put("attack", name_attack);
	object.put("attacked", name_attacked);
	object.put("time", time);
	object.put("x", x);
	object.put("y", y);
	return object;
    }

}
