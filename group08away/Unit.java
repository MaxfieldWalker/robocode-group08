package group08away;

import robocode.*;
import java.awt.Color;
import robocode.util.Utils;
import java.io.IOException;
import java.util.ArrayList;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;

import java.awt.geom.Point2D;

public class Unit extends TeamRobot
{
	// デフォルトの弾のパワー
	private static final double DEFAULTPOWER = 3.0;
	// 逃げるモードに移行する体力
	private static final double NIGERUENERGY = 10.0;

	// 基本の動く量
	private int moveAmount = 50;
	// 弾のエネルギー量
	private double power = DEFAULTPOWER;
	// 最後に存在した敵の角度
	private double lastEnemyHeading = 0;
	// 敵のリスト
	private ArrayList<EnemyInfo> enemies = new ArrayList<EnemyInfo>();
	// 最後に撃った弾の当たったロボット
	private BulletHitRobot lastBulletHitRobot = null;
	// 「逃げる」モード
	private boolean nigeruMode = false;

	public void run()
	{
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

	public void onScannedRobot(ScannedRobotEvent e)
	{
		// チームメンバーの場合は何もしない
		if (isTeammate(e.getName()) || nigeruMode)
		{
			setAhead(moveAmount);
			setTurnLeftRadians(Math.PI / 12);
			return;
		}

		// 敵リストにいなければ新たに追加
		if (isUndiscoveredEnemy(e.getName()))
		{
			EnemyInfo enemy = new EnemyInfo(e.getName(), e.getEnergy());
			this.enemies.add(enemy);
		}

		setTurnRadarLeftRadians(getRadarTurnRemainingRadians());

		double enemyHeading = e.getHeading();// 敵の向き
		double enemyBearing = this.getHeadingRadians() + e.getBearingRadians();// 自分と敵の角度
		double enemyX = e.getDistance() * Math.sin(enemyBearing);
		double enemyY = e.getDistance() * Math.cos(enemyBearing);
		double battlefieldWidh = getBattleFieldWidth();// フィールド幅
		double battlefieldHeight = getBattleFieldHeight();// フィールド高さ

		boolean isHeadingToCenter = (int) enemyHeading % 90 == 0;// 中心を向いている
		boolean isOnWall = nearlyEquals(enemyX, 18) || nearlyEquals(enemyX + 18, battlefieldWidh)
				|| nearlyEquals(enemyY, 18) || nearlyEquals(enemyY + 18, battlefieldHeight);// 壁に張り付いている

		// 中心を向いている&&壁際にいる(=Walls)なら射撃
		if (isHeadingToCenter && isOnWall)
		{
			System.out.println("Walls!!");
		}

		double dis = 0;
		double heading = lastEnemyHeading;
		do
		{
			dis += Rules.getBulletSpeed(power);
			heading += e.getHeadingRadians() - lastEnemyHeading;
			enemyX += e.getVelocity() * Math.sin(heading);
			enemyY += e.getVelocity() * Math.cos(heading);
		}
		while (dis < Point2D.distance(0, 0, enemyX, enemyY)); //

		// 相対角度に変換した上で砲塔の向きを変える
		setTurnGunRightRadians(Utils.normalRelativeAngle(Math.atan2(enemyX, enemyY) - getGunHeadingRadians()));

		// 残りエネルギーが少なくなったら「逃げる」モードに移行
		if (power > this.getEnergy() - NIGERUENERGY)
		{
			nigeruMode = true;
			moveAmount = -moveAmount;
		}
		// 逃げるモードなら撃たない
		if (!nigeruMode)
		{
			setFire(power);
		}

		lastEnemyHeading = e.getHeadingRadians();

		// 敵の居る方向へターンする
		setTurnRightRadians(e.getBearingRadians());
		// 前進する
		setAhead(moveAmount);
	}

	public void onHitWall(HitWallEvent e)
	{
		// 進む方向を反転する
		moveAmount = -moveAmount;
	}

	public void onHitRobot(HitRobotEvent e)
	{
		// チームメイトなら進む方向を反転する
		if (isTeammate(e.getName()))
			moveAmount = -moveAmount;
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
		}
		else
		{
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
					double desired = 1.0 * lastBulletHitRobot.getSeriesHitCount();
					// パワーをブーストさせる
					power = desired < this.getEnergy() - 1.0 ? desired : DEFAULTPOWER;
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

	// まだ発見していない敵かどうか
	private boolean isUndiscoveredEnemy(String enemyName)
	{
		for (EnemyInfo enemy : enemies)
		{
			if (enemy.getName() == enemyName)
				return false;
		}

		return true;
	}
}
