package com.badlogic.gdx.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ru.vonabe.manager.DataBaseManager;

public class CreateBot {

    final private static String sql_insert_bot = "insert into Bots(login,lvl,map,x,y,army_id) values ('%1s',%2s,%3s,%4s,%5s,%6s);";
    private final static String SQL_INSERT_ADD_UNITS = "insert into Units(attack, protection, health, orders) values ('%1$s','%2$s','%3$s','%4$s');\n";
    private final static String SQL_INSERT_ADD_ID_UNITS = "insert into Army(sniper, desantnic, robot, unit1, unit2, unit3)"
	    + " values ('%1$s','%2$s','%3$s','%4$s','%5$s','%6$s');\n";

    final private static String SQL_LOGIN_LORDS = "select Lords.[id] from Lords where login = '%1$s';";
    final private static String SQL_LOGIN_BOTS = "select Bots.[id] from Bots where login = '%1$s';";

    public static boolean createBot(String login, int lvl, int map, float x, float y) throws Exception {
	if (isLogin(login)) {
	    throw new Exception("Этот логин уже занят.");
	} else {

	    String sniper_sql = String.format(SQL_INSERT_ADD_UNITS, 4, 5, 6, 1); // Sniper
	    String desantnic_sql = String.format(SQL_INSERT_ADD_UNITS, 5, 4, 6, 2); // Desantnic
	    String robot_sql = String.format(SQL_INSERT_ADD_UNITS, 10, 9, 11, 3); // Robot

	    int id0 = -1, id1 = -1, id2 = -1;
	    PreparedStatement state0 = DataBaseManager.getDB().insertOrUpdateStatement(sniper_sql, "id");
	    try {
		ResultSet generatedKeys = state0.getGeneratedKeys();
		if (generatedKeys.next()) {
		    id0 = generatedKeys.getInt(1);
		}
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
	    PreparedStatement state1 = DataBaseManager.getDB().insertOrUpdateStatement(desantnic_sql, "id");
	    try {
		ResultSet generatedKeys = state1.getGeneratedKeys();
		if (generatedKeys.next()) {
		    id1 = generatedKeys.getInt(1);
		}
	    } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    PreparedStatement state2 = DataBaseManager.getDB().insertOrUpdateStatement(robot_sql, "id");
	    try {
		ResultSet generatedKeys = state2.getGeneratedKeys();
		if (generatedKeys.next()) {
		    id2 = generatedKeys.getInt(1);
		}
	    } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }

	    PreparedStatement state_id_army = DataBaseManager.getDB()
		    .insertOrUpdateStatement(String.format(SQL_INSERT_ADD_ID_UNITS, id0, id1, id2, 2, 1, 2), "id");
	    ResultSet resultSet = state_id_army.getGeneratedKeys();
	    int id_army = -1;
	    if (resultSet.next())
		id_army = resultSet.getInt(1);

	    // login,lvl,map,x,y,army_id
	    DataBaseManager.getDB().insertOrUpdate(String.format(sql_insert_bot, login, lvl, map, x, y, id_army));
	    return true;
	}

    }

    private static boolean isLogin(String login) {
	DataBaseManager db = DataBaseManager.getDB();
	ResultSet result = db.query(String.format(SQL_LOGIN_LORDS, login));
	ResultSet result0 = db.query(String.format(SQL_LOGIN_BOTS, login));
	try {
	    if (result.next() || result0.next()) {
		return true;
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return false;
    }

}
