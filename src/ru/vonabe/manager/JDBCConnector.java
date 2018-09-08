package ru.vonabe.manager;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author vonabe
 */
public class JDBCConnector {

    private static String path = null;
    private static Connection connection = null;
    private static Statement statement = null;
    final private static String prefix = "jdbc:sqlite:";
    private static String login = null, password = null;

    public static void setPath(String path) {
	JDBCConnector.path = "file:" + path;
    }

    public static void setLoginPassword(String login, String password) {
	JDBCConnector.login = login;
	JDBCConnector.password = password;
    }

    public static Connection getConnection() {
	try {
	    if (path != null && connection == null) {
		Driver driver = (Driver) Class.forName("org.sqlite.JDBC").newInstance();
		DriverManager.registerDriver(driver);
		URL url = new URL(path);
		connection = DriverManager.getConnection(prefix + url.getFile());
	    }
	} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | MalformedURLException | SQLException ex) {
	    System.err.println("error getConnection > " + ex.getMessage());
	    return null;
	}
	return connection;
    }

    public static Statement getStatement() {
	if (statement != null) {
	    return statement;
	} else if (connection == null) {
	    if (getConnection() != null) {
		try {
		    statement = connection.createStatement();
		} catch (SQLException ex) {
		    System.err.println("0 error create statement > " + ex.getMessage());
		}
	    }
	} else {
	    try {
		statement = connection.createStatement();
	    } catch (SQLException ex) {
		System.err.println("1 error create statement > " + ex.getMessage());
	    }
	}
	return statement;
    }

    public static ResultSet executeQuery(String query) {
	if (statement != null) {
	    try {
		ResultSet rquery = statement.executeQuery(query);
		return rquery;
	    } catch (SQLException ex) {
		System.err.println("error execute query: " + query);
	    }
	} else
	    System.err.println("null statement");
	return null;
    }

    public static int executeUpdate(String query) {
	if (statement != null) {
	    try {
		return statement.executeUpdate(query);
	    } catch (SQLException ex) {
		System.err.println("error execute update");
		return -2;
	    }
	}
	System.err.println("error execute update -1");
	return -1;
    }

    public static void close() {
	if (connection != null) {
	    try {
		if (statement != null)
		    statement.close();
		connection.close();
		connection = null;
		statement = null;
		path = null;
	    } catch (SQLException ex) {
		System.err.println("error close jdbcconnector > " + ex.getMessage());
	    }
	}
    }

}
