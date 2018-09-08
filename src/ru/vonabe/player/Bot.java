package ru.vonabe.player;

import java.sql.SQLException;

public class Bot extends Player {

    public Bot(String name, int exp, int vict, int def, int draw, int esc, int lvl, int map, float x, float y, int army_id) throws SQLException {
	super(name, exp, vict, def, draw, esc, lvl, map, x, y, army_id);
    }

}
