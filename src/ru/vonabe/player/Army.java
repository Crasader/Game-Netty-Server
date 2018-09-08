package ru.vonabe.player;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.simple.JSONArray;

import ru.vonabe.handler.Renderer;
import ru.vonabe.manager.BattleManager;
import ru.vonabe.manager.ClientManager;
import ru.vonabe.packet.PacketWriter;
import ru.vonabe.packet.PoolPacketWriter;

public class Army implements Renderer {

    public static enum Type {
	DESANTNIK, SNIPER, ROBOT, NULL
    }

    private Player player = null;
    private ArrayList<Unit> units_array = new ArrayList<Unit>();
    private float step_time = BattleManager.timer;
    private JSONArray array_units = new JSONArray();
    private Unit unit0 = null;
    private Unit unit1 = null;
    private Unit unit2 = null;
    private boolean battle = false, step = false;
    private int[] from = null, to = null, addition = null;

    private String uuid_enemy = null;

    private int units = 0;

    public Army(Player player) {
	this.player = player;
    }

    public Unit getUnit(int id) {
	return units_array.get(id);
    }

    public void act(float time) {
	step_time -= time;
    }

    public float getTime() {
	return step_time;
    }

    public boolean isStep() {
	return step;
    }

    public boolean isAllStep() {
	return isStep() && ClientManager.getClient(uuid_enemy).player.getArmy().isStep();
    }

    public void battle(String uuid) {
	this.battle = true;
	this.uuid_enemy = uuid;
    }

    public void myAttack(int[] from, int[] to, int[] addition) {
	step = true;
	this.from = from;
	this.to = to;
	this.addition = addition;

	PacketWriter writer = PoolPacketWriter.getWriter();
	writer.getData().put("step", true);
	writer.getObject().put("data", writer.getData());
	writer.getObject().put("action", "step_notify");
	ClientManager.getClient(uuid_enemy).write(writer);

	if (ClientManager.getClient(uuid_enemy).player.getArmy().isStep()) {
	    ClientManager.getClient(uuid_enemy).player.getArmy().attack(from, to, addition);
	}
    }

    public void attack(int[] from, int[] to, int[] addition) {
	ClientManager.getClient(uuid_enemy).player.getArmy().attack(this.from, this.to, this.addition);

	PacketWriter writer = PoolPacketWriter.getWriter();
	writer.getData().put("from", this.from);
	writer.getData().put("to", this.to);
	writer.getData().put("addition", this.addition);
	writer.getObject().put("data", writer.getData());
	writer.getObject().put("action", "step");
	ClientManager.getClient(uuid_enemy).write(writer);

	for (int i = 0; i < from.length; i++) {
	    this.step_time = BattleManager.timer;
	    int f = from[i];
	    int t = to[i];
	    Unit unit_0 = ClientManager.getClient(uuid_enemy).player.getArmy().getUnit(f);
	    Unit unit_1 = getUnit(t);

	    boolean block = false;
	    if (Arrays.binarySearch(this.from, t) == -1)
		block = true;

	    if (!block) {
		if (unit_1.getHealth() - unit_0.getWeapons() <= 0) {
		    int damage = unit_1.getHealth() - unit_0.getWeapons();
		    unit_1.health(damage);
		}
	    } else {
		if (unit_0.getWeapons() > unit_1.getProtection()) {
		    int damage = unit_1.getHealth() - (unit_0.getWeapons() - unit_1.getProtection());
		    unit_1.health(damage);
		}
	    }
	    if (unit_1.getHealth() <= 0) {
		unit_1.progress(0);
		this.removeUnit(t);
		if (units == 0) {
		    defeat();
		}
	    }
	}

	this.step_time = BattleManager.timer;
	this.step = false;
	this.from = null;
	this.to = null;
	this.addition = null;

	if (ClientManager.getClient(uuid_enemy).player.getArmy().getUnits() == 0)
	    victory();
	else if (ClientManager.getClient(uuid_enemy).player.getArmy().getUnits() == 0 && getUnits() == 0)
	    draw();
    }

    public void victory() {
	battle = false;
	BattleManager.removeBattle(player.getName());
	PacketWriter writer = PoolPacketWriter.getWriter();
	writer.getData().put("status", "victory");
	writer.getData().put("exp", 500);
	writer.getData().put("energy", 5);
	writer.getObject().put("data", writer.getData());
	writer.getObject().put("action", "battle");
	ClientManager.getClient(uuid_enemy).write(writer);
    }

    public void draw() {
	battle = false;
	BattleManager.removeBattle(player.getName());
	PacketWriter writer = PoolPacketWriter.getWriter();
	writer.getData().put("status", "draw");
	writer.getData().put("exp", 0);
	writer.getData().put("energy", 0);
	writer.getObject().put("data", writer.getData());
	writer.getObject().put("action", "battle");
	ClientManager.getClient(uuid_enemy).write(writer);
    }

    public void escape() {
	battle = false;
	BattleManager.removeBattle(player.getName());
	PacketWriter writer = PoolPacketWriter.getWriter();
	writer.getData().put("status", "escape");
	writer.getData().put("exp", -500);
	writer.getData().put("energy", 0);
	writer.getObject().put("data", writer.getData());
	writer.getObject().put("action", "battle");
	ClientManager.getClient(uuid_enemy).write(writer);
    }

    public void defeat() {
	battle = false;
	BattleManager.removeBattle(player.getName());
	PacketWriter writer = PoolPacketWriter.getWriter();
	writer.getData().put("status", "defeat");
	writer.getData().put("exp", -500);
	writer.getData().put("energy", 0);
	writer.getObject().put("data", writer.getData());
	writer.getObject().put("action", "battle");
	ClientManager.getClient(uuid_enemy).write(writer);
    }

    public void removeUnit(int id_unit) {
	this.array_units.remove(id_unit);
	this.units = this.array_units.size();
    }

    public void addUnit(Unit unit) {
	this.units_array.add(unit);
	this.units = units_array.size();
	unit.math();
    }

    public void init(Unit unit0, Unit unit1, Unit unit2) {
	this.unit0 = unit0;
	this.unit1 = unit1;
	this.unit2 = unit2;

	if (this.unit0 == null) {
	    this.units--;
	} else {
	    this.units_array.add(unit0);
	    this.unit0.math();
	}

	if (this.unit1 == null)
	    this.units--;
	else {
	    this.units_array.add(unit1);
	    this.unit1.math();
	}
	if (this.unit2 == null)
	    this.units--;
	else {
	    this.units_array.add(unit2);
	    this.unit2.math();
	}
	this.units = this.units_array.size();
    }

    public boolean isBattle() {
	return battle;
    }

    public int getUnits() {
	return units;
    }

    public JSONArray getData() {
	array_units.clear();
	if (unit0 != null && unit0.getProgress() == 100)
	    array_units.add(unit0.getPacket());
	if (unit1 != null && unit1.getProgress() == 100)
	    array_units.add(unit1.getPacket());
	if (unit2 != null && unit2.getProgress() == 100)
	    array_units.add(unit2.getPacket());
	return array_units;
    }

    @Override
    public void render(float delta) {

    }

}
