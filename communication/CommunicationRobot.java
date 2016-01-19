package communication;

import robocode.*;

import java.awt.*;
import java.io.*;
import java.util.*;
//import java.awt.Color;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * CommunicationRobot - a robot by (your name here)
 */

public class CommunicationRobot extends TeamRobot
{
	HashMap<String, double, double, double, double, double, double, double> inputInfo;
	HashMap<double, double, double, double, double, double, double> updateInfo;
	Hashtable taegets;
	Enemy_info target;
	/**
	 * run: CommunicationRobot's default behavior
	 */
	public void run() {
		// Initialization of the robot should be put here

		// After trying out your robot, try uncommenting the import at the top,
		// and the next line:

		// setColors(Color.red,Color.blue,Color.green); // body,gun,radar

		// Robot main loop
		
		try {
			// Send RobotColors object to our entire team
			broadcastMessage("mess");
		} catch (IOException ignored) {}

		while(true) {
			// Replace the next 4 lines with any behavior you would like
			ahead(100);
			turnGunRight(360);
			back(100);
			turnGunRight(360);
		}
		
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		// Replace the next line with any behavior you would like
		Enemy_info enemy;
		if(targets.containKey(e.getName())){
			enemy = targets.get(e.getName());
			updateInfo.put("Name", e.getName());
			updateInfo.put("Bearing", e.getBearing());
			updateInfo.put("Velocity", e.getVelocity());
			updateInfo.put("Distance", e.getDistance());
			updateInfo.put("BearingRadians", e.getBearingRadians());
			updateInfo.put("Energy", e.getEnergy());
			updateInfo.put("Heading", e.getHeading());
			updateInfo.put("HeadingRadians", e.getHeadingRadians());
			enemy.updateInformation(updateInfo);
		} else {
			enrmy = new Enemy();
			targets.put(e.getName,enemy);
			inputInfo.put("Name", e.getName());
			inputInfo.put("Bearing", e.getBearing());
			inputInfo.put("Velocity", e.getVelocity());
			inputInfo.put("Distance", e.getDistance());
			inputInfo.put("BearingRadians", e.getBearingRadians());
			inputInfo.put("Energy", e.getEnergy());
			inputInfo.put("Heading", e.getHeading());
			inputInfo.put("HeadingRadians", e.getHeadingRadians());
			enemy.inputInformation(inputInfo);
		}

		//broadcastMessage(e.getName());
		try {
			// Send RobotColors object to our entire team
			broadcastMessage(e.getName());
		} catch (IOException ignored) {}
		
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
		back(10);
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		// Replace the next line with any behavior you would like
		back(20);
	}
	
	@Override
	public void onMessageReceived(MessageEvent e){
		System.out.println(e.getSender() + " sent me: " + e.getMessage());
		System.out.println();
	}
}


	
