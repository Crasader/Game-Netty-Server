package ru.vonabe.player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import org.json.simple.JSONObject;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import ru.vonabe.manager.Client;
import ru.vonabe.manager.DataBaseManager;
import ru.vonabe.manager.MapManager;
import ru.vonabe.map.Location;
import ru.vonabe.packet.MovePacket.MoveType;
import ru.vonabe.packet.PacketWriter;
import ru.vonabe.packet.PoolPacketWriter;

public class Player {

    private static final String SQL_GET_PLAYER = "select Lords.* from Lords where login = '%1$s';";
    private static final String SQL_UPDATE_PLAYER = "update Lords set x='%1$s', y='%2$s' where Lords.login = '%3$s';";

    private Client client = null;
    private MoveType move = MoveType.STOP;
    private String name = null;
    public String uuid = null;

    private int lvl = 1, victories = 0, defeats = 0, draw = 0, escape = 0, victorycount = 0, map_id = 0;
    private int energy = 0, slot = 0, experience = 0, gildia_id = 0, clan_id = 0, army_id = 0;
    private boolean visible = true;

    private Army army_ = null;

    private Vector2 position = new Vector2(), tmp_position = new Vector2();
    private Rectangle rectangle = new Rectangle(0, 0, 64, 64 / 2);
    private float speed = 5.0f;
    private String collision = null;
    private JSONObject object_myinfo = new JSONObject(), object_coordinate = new JSONObject(), object_packet = new JSONObject();

    public Player(String name, Client cl) {
	this.client = cl;
	this.name = name;
	this.army_ = new Army(this);
	if (this.client != null) {
	    this.uuid = this.client.uuid;
	} else {
	    this.uuid = UUID.randomUUID().toString();
	}
    }

    public Client getClient() {
	return client;
    }

    public Player(String name, int exp, int vict, int def, int draw, int esc, int lvl, int map, float x, float y, int army_id) {
	this.army_ = new Army(this);
	if (this.client != null) {
	    this.uuid = this.client.uuid;
	} else {
	    this.uuid = UUID.randomUUID().toString();
	}
	this.name = name;
	this.experience = exp;
	this.victories = vict;
	this.defeats = def;
	this.draw = draw;
	this.escape = esc;
	this.lvl = lvl;
	this.map_id = map;
	this.army_id = army_id;
	this.position.set(x, y);
	this.tmp_position.set(this.position);
    }

    public Army getArmy() {
	return army_;
    }

    public void setClient(Client client) {
	this.client = client;
    }

    public void attack(String uuid_attack, String uuid_attacked) {
	this.army_.battle(uuid);
	this.visible = false;
    }

    public boolean isVisible() {
	return visible;
    }

    public boolean isBattle() {
	return this.army_.isBattle();
    }

    public void initBot() {
	try {
	    final ResultSet army = DataBaseManager.getDB().query(String.format("select Army.* from Army where id = '%1s'", army_id));
	    int id_sniper = army.getInt("sniper");
	    int id_desantnic = army.getInt("desantnic");
	    int id_robot = army.getInt("robot");
	    int unit1 = army.getInt("unit1");
	    int unit2 = army.getInt("unit2");
	    int unit3 = army.getInt("unit3");

	    final ResultSet sniperResultSet = DataBaseManager.getDB().query(String.format("select Units.* from Units where id='%1s'", id_sniper));
	    final ResultSet desantnicResultSet = DataBaseManager.getDB()
		    .query(String.format("select Units.* from Units where id='%1s'", id_desantnic));
	    final ResultSet robotResultSet = DataBaseManager.getDB().query(String.format("select Units.* from Units where id='%1s'", id_robot));

	    Unit unit_0 = createUnit(unit1, ((unit1 == 1) ? sniperResultSet : ((unit1 == 2) ? desantnicResultSet : robotResultSet)));
	    Unit unit_1 = createUnit(unit2, ((unit2 == 1) ? sniperResultSet : ((unit2 == 2) ? desantnicResultSet : robotResultSet)));
	    Unit unit_2 = createUnit(unit3, ((unit3 == 1) ? sniperResultSet : ((unit3 == 2) ? desantnicResultSet : robotResultSet)));

	    army_.init(unit_0, unit_1, unit_2);

	    army.close();
	    sniperResultSet.close();
	    desantnicResultSet.close();
	    robotResultSet.close();

	    System.out.println("CreateBot - " + this.name);
	} catch (SQLException e) {
	    e.printStackTrace();
	}

    }

    public void init() {
	try {
	    DataBaseManager db = DataBaseManager.getDB();

	    ResultSet result = db.query(String.format(SQL_GET_PLAYER, this.name));

	    String email = result.getString("email");
	    energy = result.getInt("energy");
	    slot = result.getInt("slot");
	    experience = result.getInt("experience");
	    victories = result.getInt("victories");
	    defeats = result.getInt("defeats");
	    draw = result.getInt("draw");
	    escape = result.getInt("escape");
	    victorycount = result.getInt("victorycount");
	    lvl = result.getInt("lvl");
	    map_id = result.getInt("map");
	    float x = result.getFloat("x");
	    float y = result.getFloat("y");

	    gildia_id = result.getInt("gildia_id");
	    clan_id = result.getInt("clan_id");
	    int army_id = result.getInt("army_id");

	    String date = result.getString("date");
	    String time = result.getString("time");
	    String ip = result.getString("ip");

	    ResultSet army = db.query(String.format("select Army.* from Army where id = '%1s'", army_id));
	    int id_sniper = army.getInt("sniper");
	    int id_desantnic = army.getInt("desantnic");
	    int id_robot = army.getInt("robot");
	    int unit1 = army.getInt("unit1");
	    int unit2 = army.getInt("unit2");
	    int unit3 = army.getInt("unit3");

	    ResultSet sniperResultSet = db.query(String.format("select Units.* from Units where id='%1s'", id_sniper));
	    ResultSet desantnicResultSet = db.query(String.format("select Units.* from Units where id='%1s'", id_desantnic));
	    ResultSet robotResultSet = db.query(String.format("select Units.* from Units where id='%1s'", id_robot));

	    Unit unit_0 = createUnit(unit1, ((unit1 == 1) ? sniperResultSet : ((unit1 == 2) ? desantnicResultSet : robotResultSet)));
	    Unit unit_1 = createUnit(unit2, ((unit2 == 1) ? sniperResultSet : ((unit2 == 2) ? desantnicResultSet : robotResultSet)));
	    Unit unit_2 = createUnit(unit3, ((unit3 == 1) ? sniperResultSet : ((unit3 == 2) ? desantnicResultSet : robotResultSet)));

	    army_.init(unit_0, unit_1, unit_2);

	    this.position.set(x, y);
	    this.tmp_position.set(position);

	    army.close();
	    sniperResultSet.close();
	    desantnicResultSet.close();
	    robotResultSet.close();
	    result.close();

	    System.out.println("Create Player " + name);
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    private Unit createUnit(int type, ResultSet set) {
	try {
	    int attack = set.getInt("attack");
	    int protection = set.getInt("protection");
	    int health = set.getInt("health");

	    int attack_techno = set.getInt("attack_techno");
	    int protection_techno = set.getInt("protection_techno");
	    int health_techno = set.getInt("health_techno");

	    boolean attack_techno_all = set.getBoolean("attack_techno_all");
	    boolean protection_techno_all = set.getBoolean("protection_techno_all");
	    boolean health_techno_all = set.getBoolean("health_techno_all");
	    boolean ready = set.getBoolean("ready");

	    if (type == 1) {
		return new Sniper(attack, protection, health, attack_techno, protection_techno, health_techno, attack_techno_all,
			protection_techno_all, health_techno_all, (ready) ? 100 : 0);
	    } else if (type == 2) {
		return new Desant(attack, protection, health, attack_techno, protection_techno, health_techno, attack_techno_all,
			protection_techno_all, health_techno_all, (ready) ? 100 : 0);
	    } else if (type == 3) {
		return new Robot(attack, protection, health, attack_techno, protection_techno, health_techno, attack_techno_all,
			protection_techno_all, health_techno_all, (ready) ? 100 : 0);
	    } else {
		System.out.println("error type exception");
	    }
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return null;
    }

    public void move() {
	switch (this.move) {
	case STOP:
	    break;
	case TOP:
	    this.tmp_position.add(0, speed);
	    if (step())
		this.position.add(0, speed);
	    else
		sendCollision();
	    break;
	case BOT:
	    this.tmp_position.add(0, -speed);
	    if (step())
		this.position.add(0, -speed);
	    else
		sendCollision();
	    break;
	case RIGHT:
	    this.tmp_position.add(speed, 0);
	    if (step())
		this.position.add(speed, 0);
	    else
		sendCollision();
	    break;
	case LEFT:
	    this.tmp_position.add(-speed, 0);
	    if (step())
		this.position.add(-speed, 0);
	    else
		sendCollision();
	    break;
	default:
	    break;
	}
	// System.out.println(
	// this.position + " : " +
	// MapManager.getMap(this.map_id).getHashmapBlocks().values().toArray()[0]);
    }

    private void sendCollision() {
	PacketWriter writer = PoolPacketWriter.getWriter();
	JSONObject data = writer.getData();
	data.put("block", collision);
	JSONObject object = writer.getObject();
	object.put("action", "collision");
	object.put("data", data);
	client.write(writer);
    }

    private boolean step() {
	this.rectangle.setPosition(this.tmp_position);
	Location location = MapManager.getMap(String.valueOf(this.map_id));
	HashMap<String, Rectangle> blocks = location.getHashmapBlocks();
	Iterator<String> it_block = blocks.keySet().iterator();
	while (it_block.hasNext()) {
	    String name_block = it_block.next();
	    Rectangle rect_block = blocks.get(name_block);
	    if (this.rectangle.overlaps(rect_block)) {
		this.rectangle.setPosition(this.position);
		this.tmp_position.set(this.position);
		this.collision = name_block;
		this.move = MoveType.STOP;
		return false;
	    }
	}
	this.collision = null;
	return true;
    }

    public JSONObject getMyinfo() {
	object_myinfo.clear();
	object_myinfo.put("lvl", lvl);
	object_myinfo.put("units", army_.getUnits());
	object_myinfo.put("victories", victories);
	object_myinfo.put("defeats", defeats);
	object_myinfo.put("draw", draw);
	object_myinfo.put("escape", escape);
	object_myinfo.put("victorycount", victorycount);
	object_myinfo.put("energy", energy);
	object_myinfo.put("slot", slot);
	object_myinfo.put("experience", experience);
	object_myinfo.put("clan_id", clan_id);
	object_myinfo.put("gildia_id", gildia_id);
	object_myinfo.put("army", army_.getData());
	return object_myinfo;
    }

    public JSONObject getCoordinate() {
	object_coordinate.clear();
	object_coordinate.put("login", getName());
	object_coordinate.put("x", getPosition().x);
	object_coordinate.put("y", getPosition().y);
	object_coordinate.put("v", move.toString());
	object_coordinate.put("id_map", map_id);
	return object_coordinate;
    }

    public JSONObject getPacketPublic() {
	object_packet.clear();
	object_packet.put("login", getName());
	object_packet.put("x", getPosition().x);
	object_packet.put("y", getPosition().y);
	object_packet.put("v", move.toString());
	object_packet.put("id_map", map_id);
	object_packet.put("lvl", lvl);
	object_packet.put("units", army_.getUnits());
	object_packet.put("victories", victories);
	object_packet.put("defeats", defeats);
	object_packet.put("draw", draw);
	object_packet.put("escape", escape);
	object_packet.put("victorycount", victorycount);
	object_packet.put("clan_id", clan_id);
	object_packet.put("gildia_id", gildia_id);
	object_packet.put("visible", visible);
	return object_packet;
    }

    public void setMap(int map) {
	this.map_id = map;
    }

    public int getMap() {
	return map_id;
    }

    public void setPosition(Vector2 position) {
	this.position = position;
    }

    public void setPosition(float x, float y) {
	this.position.set(x, y);
    }

    public MoveType getMove() {
	return move;
    }

    public boolean isMove() {
	return this.move != MoveType.STOP;
    }

    public void setMove(MoveType move) {
	this.move = move;
    }

    public String getName() {
	return this.name;
    }

    public Vector2 getPosition() {
	return this.position;
    }

    public void save() {
	DataBaseManager.getDB().insertOrUpdate(String.format(SQL_UPDATE_PLAYER, this.getPosition().x, this.getPosition().y, this.name));
    }

}
