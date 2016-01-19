package communication;


import robocode.*;
import robocode.HitByBulletEvent;
import robocode.ScannedRobotEvent;
import robocode.TeamRobot;
import java.lang.Math;
import java.util.ArrayList;

import java.awt.*;
import java.io.*;
import java.io.Serializable;
//import java.awt.Color;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * CommunicationRobot - a robot by (your name here)
 */
public class TeamExtends extends TeamRobot
{
	
}

class Enemy_info {
	private String name;
	private double bearing;
	private double bearingRadians;
	private double distance;
	private double energy;
	private double heading;
	private double headingRadians;
	private double velocity;
	private boolean alive;

	// constructor //
	public Enemy_info(){
		System.err.println("err done constructor");
		name = "";
		bearing = 0;
		bearingRadians = 0;
		distance = 0;
		energy = 0;
		heading = 0;
		headingRadians = 0;
		velocity = 0;
		alive = false;
	}
	
	public Enemy_info(String in_name, double in_bearing, double in_bearingRadians, double in_distance, double in_energy, double in_heading, double in_headingRadians, double in_velocity){
		name = in_name;
		bearing = in_bearing;
		bearingRadians = in_bearingRadians;
		distance = in_distance;
		energy = in_energy;
		heading = in_heading;
		headingRadians = in_headingRadians;
		velocity = in_velocity;
		alive = true;
	}
	//end

	// function to get information about enemy //
	public String get_en_name(){
		return name;
	}

	public double get_en_bearingRadians(){
		return bearingRadians;
	}

	public double get_en_distance(){
		return distance;
	}

	public double get_en_energy(){
		return energy;
	}

	public double get_en_heading(){
		return heading;
	}

	public double get_en_headingRadians(){
		return headingRadians;
	}

	public double get_en_velocity(){
		return velocity;
	}
	//end

	// function update enemy information //
	public void dead(){
		alive = false;
	}

	public void updateInformation(double in_bearing, double in_bearingRadians, double in_distance, double in_energy, double in_heading, double in_headingRadians, double in_velocity){
		bearing = in_bearing;
		bearingRadians = in_bearingRadians;
		distance = in_distance;
		energy = in_energy;
		heading = in_heading;
		headingRadians = in_headingRadians;
		velocity = in_velocity;
	}
	//end
}