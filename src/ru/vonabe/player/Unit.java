package ru.vonabe.player;

import org.json.simple.JSONObject;

public abstract class Unit {

    private final int const_weapons, const_protection, const_health;
    private int weapons = 0, protection = 0, health = 0, progress = 100;
    private int techno_weapons = 0, techno_protection = 0, techno_health = 0;
    private boolean attack_techno_all = false, protection_techno_all = false, health_techno_all = false;

    public Unit(int weapons, int protection, int health, int techno_weapons, int techno_protection, int techno_health, boolean attack_techno_all,
	    boolean protection_techno_all, boolean health_techno_all, int progress) {
	this.weapons = weapons;
	this.protection = protection;
	this.health = health;
	this.techno_weapons = techno_weapons;
	this.techno_protection = techno_protection;
	this.techno_health = techno_health;
	this.progress = progress;
	this.attack_techno_all = attack_techno_all;
	this.protection_techno_all = protection_techno_all;
	this.health_techno_all = health_techno_all;

	this.const_weapons = weapons;
	this.const_protection = protection;
	this.const_health = health;
    }

    public void math() {
	weapons = const_weapons + techno_weapons;
	protection = const_protection + techno_protection;
	health = const_health + techno_protection;
    }

    public boolean isAttackTechnoAll() {
	return attack_techno_all;
    }

    public boolean isProtectionTechnoAll() {
	return protection_techno_all;
    }

    public boolean isHealthTechnoAll() {
	return health_techno_all;
    }

    public void setAttackTechnoAll(boolean attack_techno_all) {
	this.attack_techno_all = attack_techno_all;
    }

    public void setProtectionTechnoAll(boolean protection_techno_all) {
	this.protection_techno_all = protection_techno_all;
    }

    public void setHealthTechnoAll(boolean health_techno_all) {
	this.health_techno_all = health_techno_all;
    }

    public void technoWeapons(int techno) {
	this.techno_weapons = techno;
    }

    public void technoProtection(int techno) {
	this.techno_protection = techno;
    }

    public void technoHealth(int techno) {
	this.techno_health = techno;
    }

    public void weapons(int weapons) {
	// TODO Auto-generated method stub
	this.weapons = weapons;
    }

    public void protection(int protection) {
	// TODO Auto-generated method stub
	this.protection = protection;
    }

    public void health(int health) {
	// TODO Auto-generated method stub
	this.health = health;
    }

    public int getWeapons() {
	// TODO Auto-generated method stub
	return weapons;
    }

    public int getProtection() {
	// TODO Auto-generated method stub
	return protection;
    }

    public int getHealth() {
	// TODO Auto-generated method stub
	return health;
    }

    public int getTechnoWeapons() {
	return techno_weapons;
    }

    public int getTechnoProtection() {
	return techno_protection;
    }

    public int getTechnoHealth() {
	return techno_health;
    }

    public void progress(int progress) {
	// TODO Auto-generated method stub
	this.progress = progress;
    }

    public int getProgress() {
	// TODO Auto-generated method stub
	return progress;
    }

    public abstract JSONObject getPacket();

}
