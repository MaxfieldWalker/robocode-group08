package group08away;

public class EnemyInfo
{
	private String name;
	private double energy;

	public EnemyInfo(String name, double energy)
	{
		this.name = name;
		this.energy = energy;
	}

	public String getName()
	{
		return this.name;
	}

	public double getEnergy()
	{
		return this.energy;
	}
}
