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

// xとyだけで計算する！

/**
 * CommunicationRobot - a robot by (your name here)
 */
public class com_back extends TeamExtends
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

	/**
	 * run: CommunicationRobot's default behavior
	 */

	public void run() {
		enemy_data = new String[7];

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

	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		double now_x = this.getX();
			double now_y = this.getY();
			double ene_x = now_x + e.getDistance()*Math.sin(e.getBearingRadians());
			double ene_y = now_y + e.getDistance()*Math.cos(e.getBearingRadians());
			System.out.println("me "+now_x + " " +now_y+"  "+e.getName() + " x = " + ene_x + ": y = " + ene_y + " getB" + e.getBearingRadians());
			System.out.println(Math.cos(e.getBearingRadians())+"  "+Math.sin(e.getBearingRadians()));	

		String nnn = e.getName();
		Enemy_info enem = null;
		if(!isTeammate(nnn)) {
			//味方への情報送信
			try {
				broadcastMessage(nnn + ", " + e.getBearing() + ", " + e.getBearingRadians() + ", " + e.getDistance() + ", " + e.getEnergy() + ", " + e.getHeading() + ", " + e.getHeadingRadians() + ", " + e.getVelocity());
			} catch (IOException ignored) {}

			

			//スキャンした車両がLocal敵リストにいるかどうかのフラグ
			boolean flag = false;
			System.out.println("send scanned info");
			//スキャンした敵がLocal敵リストの中に存在するか
			for (Enemy_info temp : enes) {
				if (nnn.equals(temp.get_en_name())) {
					flag = true;
					enem = temp;
					//Local敵リストのアップデート
					temp.updateInformation(e.getBearing(), e.getBearingRadians(), e.getDistance(), e.getEnergy(), e.getHeading(), e.getHeadingRadians(), e.getVelocity());
					System.out.println("	update scanned Info");
				}
			}
			//スキャンした敵がLocal敵リストの中に存在しない場合
			if (!flag) {
				//Local敵リストに新規追加
				enem = new Enemy_info(nnn, e.getBearing(), e.getBearingRadians(), e.getDistance(), e.getEnergy(), e.getHeading(), e.getHeadingRadians(), e.getVelocity());
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
		}


		if(enemy_detected == true) {
			try {

				setTurnRadarLeftRadians(getRadarTurnRemainingRadians());

				//共通の敵が設定されている場合
				double enemyHeading = target_enemy.get_en_heading();// 敵の向き
				double enemyBearing = this.getHeadingRadians() + target_enemy.get_en_bearingRadians();// 自分と敵の角度
				double enemyX = target_enemy.get_en_distance() * Math.sin(enemyBearing);
				double enemyY = target_enemy.get_en_distance() * Math.cos(enemyBearing);
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
				lastEnemyHeading = target_enemy.get_en_headingRadians();

				// 敵の居る方向へターンする
				System.out.println("setTurn : " + target_enemy.get_en_bearingRadians());
				setTurnRightRadians(target_enemy.get_en_bearingRadians());
				// 前進する
				setAhead(moveAmount);
			} catch (NullPointerException ee) {
				System.out.println("NullPointerException");
				System.out.println(target_enemy);
			}
		}

		System.out.println("enemy_detected = " + enemy_detected);
		System.out.println("target is " + target_enemy.get_en_name());
	}


	public void onHitWall(HitWallEvent e)
	{
		// 進む方向を反転する
		moveAmount = -moveAmount;
	}

//	public void onHitWall(HitWallEvent e) {
//		// 壁にあたった時
//		double wall_rad = e.getBearingRadians();
//		turnLeftRadians(wall_rad-Math.PI);
//		ahead(10);
//	}

	public void onHitRobot(HitRobotEvent e)
	{
		// チームメイトなら進む方向を反転する
		if (isTeammate(e.getName())) {
			moveAmount = -moveAmount;
		}
	}



	public void onRobotDeath(RobotDeathEvent e){
		// ロボットが死んだ時
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
	}
	
	
	public void onMessageReceived(MessageEvent e){
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
							temp.updateInformation(Double.parseDouble(enemy_data[1]), Double.parseDouble(enemy_data[2]), Double.parseDouble(enemy_data[3]), Double.parseDouble(enemy_data[4]), Double.parseDouble(enemy_data[5]), Double.parseDouble(enemy_data[6]), Double.parseDouble(enemy_data[7]));
							System.out.println("	update received Info");
						}
					}
					if (!flag) {
						//スキャンした敵がLocal敵リストの中に存在しない
						////Local敵リストの情報を新規追加
						enem = new Enemy_info(nnn, Double.parseDouble(enemy_data[1]), Double.parseDouble(enemy_data[2]), Double.parseDouble(enemy_data[3]), Double.parseDouble(enemy_data[4]), Double.parseDouble(enemy_data[5]), Double.parseDouble(enemy_data[6]), Double.parseDouble(enemy_data[7]));
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
	}

	// 撃った弾が他のロボットに当たった時に発生するイベント
	public void onBulletHit(BulletHitEvent e)
	{
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
	}

	// ニアリーイコール
	private boolean nearlyEquals(double val1, double val2)
	{
		return Math.abs(val1 - val2) < 1.0;
	}

}

