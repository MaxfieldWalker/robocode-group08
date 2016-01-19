package communication;

public class BulletHitRobot
{
	private String robotName;
	private int seriesHitCount;

	public BulletHitRobot(String robotName)
	{
		this.robotName = robotName;
		this.seriesHitCount = 0;
	}

	public String getRobotName()
	{
		return this.robotName;
	}

	public int getSeriesHitCount()
	{
		return this.seriesHitCount;
	}

	public void incrementSeriesHitCount()
	{
		this.seriesHitCount += 1;
	}
}

