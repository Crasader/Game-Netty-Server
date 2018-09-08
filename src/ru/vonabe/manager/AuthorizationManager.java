package ru.vonabe.manager;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthorizationManager {

    final private static String SQL_AUTHO = "select Lords.login,password from Lords where Lords.login = '%1$s'";

    public static boolean authorization(String login, String password) {
	DataBaseManager db = DataBaseManager.getDB();
	ResultSet result = db.query(String.format(SQL_AUTHO, login));

	try {
	    if (!result.next())
		return false;
	    String r_login = result.getString("login");
	    String r_password = result.getString("password");
	    if (login.equals(r_login) && password.equals(r_password)) {
		return true;
	    }
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    // System.out.println(result.);
	    e.printStackTrace();
	}
	// if (login.equals("vonabe") && password.equals("qwerty")) {
	// return true;
	// } else {
	// return false;
	// }
	return false;
    }

}
