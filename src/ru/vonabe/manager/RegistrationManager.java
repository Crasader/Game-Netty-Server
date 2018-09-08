package ru.vonabe.manager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RegistrationManager {

    private final static String SQL_INSERT_ADD_UNITS = "insert into Units(attack, protection, health, orders) values ('%1$s','%2$s','%3$s','%4$s');\n";
    private final static String SQL_INSERT_ADD_ID_UNITS = "insert into Army(sniper, desantnic, robot, unit1, unit2, unit3)"
	    + " values ('%1$s','%2$s','%3$s','%4$s','%5$s','%6$s');\n";
    private final static String SQL_INSERT_ADD_LORDY = "insert into Lords(login,password,email,army_id,date,time,ip)"
	    + " values ('%1$s','%2$s','%3$s','%4$s','%5$s','%6$s','%7$s');\n";

    final private static String SQL_LOGIN = "select Lords.[id] from Lords where login = '%1$s';";
    final private static String SQL_EMAIL = "select Lords.[id] from Lords where email = '%1$s';";

    private static DateFormat format_date = new SimpleDateFormat("yyyy-MM-dd");
    private static DateFormat format_time = new SimpleDateFormat("HH:mm:ss");

    public static String registration(String login, String password, String email, String uuid) {

	if (isLogin(login)) {
	    return "Этот логин уже занят.";
	} else if (isEmail(email)) {
	    return "Этот Email уже занят.";
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
		// TODO Auto-generated catch block
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
	    ResultSet resultSet;
	    int id_army = -1;
	    try {
		resultSet = state_id_army.getGeneratedKeys();
		if (resultSet.next())
		    id_army = resultSet.getInt(1);
	    } catch (SQLException e) {
		e.printStackTrace();
	    }

	    Date now = new Date();
	    String date_utc = format_date.format(now);
	    String date_time = format_time.format(now);

	    String insert_lordy_sql = String.format(SQL_INSERT_ADD_LORDY, login, password, email, id_army, date_utc, date_time,
		    ClientManager.getClient(uuid).getIP());

	    DataBaseManager.getDB().insertOrUpdate(insert_lordy_sql);

	    // System.out.println(Arrays.toString(result) + " " + id_army + " "
	    // + date_utc + " " + date_time + " " + result_lordy);

	    return null;
	}
    }

    private static boolean isLogin(String login) {
	DataBaseManager db = DataBaseManager.getDB();
	ResultSet result = db.query(String.format(SQL_LOGIN, login));
	try {
	    // String r_login = result.getString("login");
	    if (result.next()) {
		return true;
	    }
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return false;
    }

    private static boolean isEmail(String email) {
	DataBaseManager db = DataBaseManager.getDB();
	ResultSet result = db.query(String.format(SQL_EMAIL, email));
	try {
	    // String r_email = result.getString("login");
	    if (result.next()) {
		return true;
	    }
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return false;
    }

}
