package communication;

import robocode.*;
import robocode.HitByBulletEvent;
import robocode.ScannedRobotEvent;
import robocode.TeamRobot;
import java.lang.Math;
import java.util.ArrayList;

import java.awt.Color;
import robocode.util.Utils;
//import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import java.awt.geom.Point2D;

import java.awt.*;
import java.io.*;
import java.io.Serializable;
//import java.awt.Color;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * CommunicationRobot - a robot by (your name here)
 */
public class CommunicationLeader extends TeamExtends
{
	private static final double DEFAULTPOWER = 3.0;

	// 基本の動く量
	private int moveAmount = 50;
	// 弾のエネルギー量
	private double power = DEFAULTPOWER;
	// 最後に存在した敵の角度
	private double lastEnemyHeading = 0;
	
	private BulletHitRobot lastBulletHitRobot = null;

	private ArrayList<Enemy_info> enes = new ArrayList<Enemy_info>();
	private boolean enemy_detected = false;
	private int enemy_count = 3;
	private int walls_count = 3;
	private String enemy_data[];
	private Enemy_info target_enemy;

	private long nowtime;
	private boolean hit = false;

	/**
	 * run: CommunicationRobot's default behavior
	 */

	public void run() {
		enemy_data = new String[9];

		// 色の設定
		setColors(Color.white, Color.white, Color.white); // body,gun,radar
		/* 以下ではロボットと砲塔とレーダーの動きを全部一致するように設定している */
		// ロボットの動きとガンの動きを合わせる
		setAdjustGunForRobotTurn(true);
		// レーダーの動きをガンの動きに合わせる
		setAdjustRadarForGunTurn(true);
		// レーダーの動きをロボットの動きに合わせる
		setAdjustRadarForRobotTurn(true);
		// 常にレーダーを回転させる
		turnRadarRightRadians(Double.POSITIVE_INFINITY);

		while(true){
			if((getTime() - nowtime >10)&&(hit)){
				moveAmount = - moveAmount;
				hit = false;
				System.out.println("OK");
			}
			System.out.println(getTime());
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		System.out.println("START at : " + getTime() + " onScannedRobot----------------------------");
		String nnn = e.getName();
		System.out.println("scan " + nnn);
		double eneX = getX() + Math.sin(e.getBearingRadians()+Math.toRadians(getHeading()))*e.getDistance();
		double eneY = getY() + Math.cos(e.getBearingRadians()+Math.toRadians(getHeading()))*e.getDistance();
		Enemy_info enem = null;
		if(!isTeammate(nnn)) {
			//味方への情報送信
			try {
				broadcastMessage(nnn + ", " + e.getBearing() + ", " + e.getBearingRadians() + ", " + e.getDistance() + ", " + e.getEnergy() + ", " + e.getHeading() + ", " + e.getHeadingRadians() + ", " + e.getVelocity() + ", " + eneX + ", " + eneY);
			} catch (IOException ignored) {
			}
			//スキャンした車両がLocal敵リストにいるかどうかのフラグ
			boolean flag = false;
			System.out.println("send scanned info");
			//スキャンした敵がLocal敵リストの中に存在するか
			for (Enemy_info temp : enes) {
				if (nnn.equals(temp.get_en_name())) {
					flag = true;
					enem = temp;
					//Local敵リストのアップデート
					temp.updateInformation(e.getBearing(), e.getBearingRadians(), e.getDistance(), e.getEnergy(), e.getHeading(), e.getHeadingRadians(), e.getVelocity(), eneX, eneY);
					System.out.println("	update scanned Info");
				}
			}
			//スキャンした敵がLocal敵リストの中に存在しない場合
			if (!flag) {
				//Local敵リストに新規追加
				enem = new Enemy_info(nnn, e.getBearing(), e.getBearingRadians(), e.getDistance(), e.getEnergy(), e.getHeading(), e.getHeadingRadians(), e.getVelocity(), eneX, eneY);
				enes.add(enem);
				System.out.println("	add scanned info");
			}
			if (enemy_detected == false) {
				//共通の敵が設定されていない場合
				enemy_detected = true;
				target_enemy = enem;
				try {
					broadcastMessage("Kill , " + target_enemy.get_en_name() + ", !!");
				} catch (IOException ignored) {}
			}

			if(enemy_detected == true) {
			try {
				double enemyX = target_enemy.get_en_expX();
				double enemyY = target_enemy.get_en_expY();
				setTurnRadarLeftRadians(getRadarTurnRemainingRadians());

				System.out.println("abs : eX " + enemyX + " : " + "eY " + enemyY);

				//共通の敵が設定されている場合
				double enemyBearing = Math.atan((enemyX-getX())/(enemyY-getY()));
				// if(enemyBearing<0){
				// 	System.out.println("change");
				// 	enemyBearing = Math.PI*2 + enemyBearing;
				// }else if (enemyBearing < Math.PI){
					
				// }
				// System.out.println("atan " + Math.atan((eneY-getY())/(eneX-getX())));
				// System.out.println("atan1 " + Math.atan((eneX-getX())/(eneY-getY())));
				// System.out.println("trueeee " + (e.getBearingRadians() + this.getHeadingRadians()));
				System.out.println("enerad" + enemyBearing + " ?= " + "enemyBearing " + (this.getHeadingRadians() + e.getBearingRadians()));
				System.out.println(enemyBearing+Math.PI);
				double enemyHeading = target_enemy.get_en_heading();// 敵の向き
				System.out.println("enemy heading:" + enemyHeading);
				//double enemyBearing = this.getHeadingRadians() + target_enemy.get_en_bearingRadians();// 自分と敵の角度

				// double enemyX = target_enemy.get_en_distance() * Math.sin(enemyBearing);
				// double enemyY = target_enemy.get_en_distance() * Math.cos(enemyBearing);

				enemyX = enemyX - getX();
				enemyY = enemyY - getY();
				System.out.println("Relative : eX " + enemyX + " : " + "eY " + enemyY);


				double battlefieldWidh = getBattleFieldWidth();// フィールド幅
				double battlefieldHeight = getBattleFieldHeight();// フィールド高さ

				boolean isHeadingToCenter = (int) enemyHeading % 90 == 0;// 中心を向いている
				boolean isOnWall = nearlyEquals(enemyX, 18) || nearlyEquals(enemyX + 18, battlefieldWidh)
						|| nearlyEquals(enemyY, 18) || nearlyEquals(enemyY + 18, battlefieldHeight);// 壁に張り付いている

				// 中心を向いている&&壁際にいる(=Walls)なら射撃
				if (isHeadingToCenter && isOnWall) {
					System.out.println("Walls!!");
				}

				double dis = 0;
				double heading = lastEnemyHeading;
				do {
					dis += Rules.getBulletSpeed(power);
					heading += target_enemy.get_en_headingRadians() - lastEnemyHeading;
					enemyX += target_enemy.get_en_velocity() * Math.sin(heading);
					enemyY += target_enemy.get_en_velocity() * Math.cos(heading);
				}
				while (dis < Point2D.distance(0, 0, enemyX, enemyY)); //

				// 相対角度に変換した上で砲塔の向きを変える
				setTurnGunRightRadians(Utils.normalRelativeAngle(Math.atan2(enemyX, enemyY) - getGunHeadingRadians()));
				setFire(power);
				//lastEnemyHeading = e.getHeadingRadians();
				lastEnemyHeading = target_enemy.get_en_headingRadians();
				System.out.println("lastEnemyHeading " + e.getHeadingRadians());
				System.out.println(lastEnemyHeading);

				// 敵の居る方向へターンする
				//setTurnRightRadians(e.getBearingRadians());
				setTurnRightRadians(enemyBearing - this.getHeadingRadians());
				System.out.println("setTurnRightRadians " + e.getBearingRadians());
				System.out.println(enemyBearing - this.getHeadingRadians());

				// 前進する
				setAhead(moveAmount);
			} catch (NullPointerException ee) {
				System.out.println("NullPointerException");
				System.out.println(target_enemy);
			}
			}
		}

		System.out.println("enemy_detected = " + enemy_detected);
		System.out.println("target is " + target_enemy.get_en_name());
		System.out.println(target_enemy.get_en_expX() + " " + target_enemy.get_en_expY());
		System.out.println("END at : " + getTime() + " onScannedRobot----------------------------");
	}


	public void onHitWall(HitWallEvent e)
	{
		System.out.println("START at : " + getTime() + " onHitWall----------------------------");
		// 進む方向を反転する
		moveAmount = -moveAmount;
		nowtime = getTime();
		hit = true;
		System.out.println("hit wall");
		System.out.println("END at : " + getTime() + " onHitWall----------------------------");
	}

//	public void onHitWall(HitWallEvent e) {
//		// 壁にあたった時
//		double wall_rad = e.getBearingRadians();
//		turnLeftRadians(wall_rad-Math.PI);
//		ahead(10);
//	}

	public void onHitRobot(HitRobotEvent e)
	{
		System.out.println("START at : " + getTime() + " onHitRobot----------------------------");
		// チームメイトなら進む方向を反転する
		String name = e.getName();
		if (isTeammate(name)){
			moveAmount = -moveAmount;
			System.out.println("Sorry "+ name);
		}else{
			for (Enemy_info temp : enes){
				if(name.equals(temp.get_en_name())){
					System.out.println("taeget change to " + name);
					target_enemy = temp;
					enemy_detected = true;
					power = power * 3;
				}
			} 
		}
		System.out.println("END at : " + getTime() + " onHitRobot----------------------------");
	}



	public void onRobotDeath(RobotDeathEvent e){
		// ロボットが死んだ時
		System.out.println("START at : " + getTime() + " onRobotDeath----------------------------");
		System.out.println("print death "+e.getName());
		if(isTeammate(e.getName())){
		}else{
			//それが敵である時
			if(target_enemy.get_en_name().equals(e.getName())){

				enemy_detected=false;//敵の生存フラグを折る
				enemy_count--;//敵のカウントを減らす
			}
			walls_count--;//wallsのカウントを減らす
		}
		System.out.println("END at : " + getTime() + " onRobotDeath----------------------------");
	}
	
	
	public void onMessageReceived(MessageEvent e){
		System.out.println("START at : " + getTime() + " onMessageReceived----------------------------");
		// メッセージを受け取った時
		System.out.println("receive Message");
		if(e.getMessage() instanceof String){
			boolean flag = false;
			String raw = (String)e.getMessage();
			enemy_data = raw.split(", ",-1);
			Enemy_info enem = null;
			String nnn = enemy_data[0];

			if(raw.indexOf("Kill") != -1){
				System.out.println("Receive kill instruction!!");
				for (Enemy_info temp : enes){
					if(enemy_data[1].equals(temp.get_en_name())){
						target_enemy = temp;
						enemy_detected = true;
					}
				}
			}else {
				try {
					for (Enemy_info temp : enes) {
						if (nnn.equals(temp.get_en_name())) {
							//スキャンした敵がLocal敵リストの中に存在する
							flag = true;
							enem = temp; //参照のコピー
							//Local敵リストの情報をアップデート
							temp.updateInformation(Double.parseDouble(enemy_data[1]), Double.parseDouble(enemy_data[2]), Double.parseDouble(enemy_data[3]), Double.parseDouble(enemy_data[4]), Double.parseDouble(enemy_data[5]), Double.parseDouble(enemy_data[6]), Double.parseDouble(enemy_data[7]), Double.parseDouble(enemy_data[8]), Double.parseDouble(enemy_data[9]));
							System.out.println("	update received Info");
						}
					}
					if (!flag) {
						//スキャンした敵がLocal敵リストの中に存在しない
						////Local敵リストの情報を新規追加
						enem = new Enemy_info(nnn, Double.parseDouble(enemy_data[1]), Double.parseDouble(enemy_data[2]), Double.parseDouble(enemy_data[3]), Double.parseDouble(enemy_data[4]), Double.parseDouble(enemy_data[5]), Double.parseDouble(enemy_data[6]), Double.parseDouble(enemy_data[7]), Double.parseDouble(enemy_data[8]), Double.parseDouble(enemy_data[9]));
						enes.add(enem);
						System.out.println("	add received info");
					}
				} catch (ArrayIndexOutOfBoundsException e2) {
					System.out.println("ArrayIndexOutOfBoundsException");
					for (String n : enemy_data) {
						System.out.println(n);
					}
				}
				if(enemy_detected==false){
					enemy_detected =true;
					target_enemy = enem;
				}
			}
		}
		System.out.println("target is " + target_enemy.get_en_name());
		System.out.println("enemy_detected = " + enemy_detected);
		System.out.println("END at : " + getTime() + " onMessageReceived----------------------------");

	}

	// 撃った弾が他のロボットに当たった時に発生するイベント
	public void onBulletHit(BulletHitEvent e){
		System.out.println("START at : " + getTime() + " onBulletHit----------------------------");
		// チームメイトに撃った弾が衝突したら
		// 反対向きを向いてその場をすこし離れる
		if (isTeammate(e.getName()))
		{
			setTurnRightRadians(Math.PI / 2);
			setAhead(moveAmount);
		}else {
			//撃った弾に当たったのが敵であった場合
			System.out.println("Hit enemy! Shot more bullets!!");
			if (lastBulletHitRobot == null || lastBulletHitRobot.getRobotName() != e.getName())
			{
				// パワーを元に戻す
				power = DEFAULTPOWER;
				lastBulletHitRobot = new BulletHitRobot(e.getName());
				lastBulletHitRobot.incrementSeriesHitCount();
			}
			else
			{
				lastBulletHitRobot.incrementSeriesHitCount();
				if (lastBulletHitRobot.getSeriesHitCount() > 3)
				{
					//パワーをブーストさせる
					power = 1.0 * lastBulletHitRobot.getSeriesHitCount();
					System.out.println("POWER BOOST (POWER: )" + power);
				}
			}
		}
		System.out.println("END at : " + getTime() + " onBulletHit----------------------------");
	}

	// ニアリーイコール
	private boolean nearlyEquals(double val1, double val2)
	{
		return Math.abs(val1 - val2) < 1.0;
	}

}

