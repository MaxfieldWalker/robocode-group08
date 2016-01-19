package communication;

public class Enemy_info {

	private String name;
	private double bearing, bearingRadians, distance, energy, heading, headingRadians, velocity, expX, expY;
	private boolean alive;

	// constructor //
	public Enemy_info(String n, double b, double br, double d, double e, double h, double hr, double v, double x, double y){
		name = n;
		bearing = b;
		bearingRadians = br;
		distance = d;
		energy = e;
		heading = h;
		headingRadians = hr;
		velocity = v;
		expX = x;
		expY = y;
		boolean alive = true;
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

	public double get_en_expX(){
		return expX;
	}

	public double get_en_expY(){
		return expY;
	}
	//end

	// // function registor new enemy information //
	// public void inputInformation(Hashtable<String, double, double, double, double, double, double, double> input_table){
	// 	name = input_table.get("Name");
	// 	bearing = input_table.get("Bearing");
	// 	bearingRadians = input_table.get("BearingRadians");
	// 	distance = input_table.get("Distance");
	// 	energy = input_table.get("Energy");
	// 	heading = input_table.get("Heading");
	// 	headingRadians = input_table.get("HeadingRadians");
	// 	velocity = input_table.get("Velocity");
	// }
	// //end

	// function update enemy information //
	public void dead(){
		alive = false;
	}

	public void updateInformation(double b, double br, double d, double e, double h, double hr, double v, double x, double y){
		bearing = b;
		bearingRadians = br;
		distance = d;
		energy = e;
		heading = h;
		headingRadians = hr;
		velocity = v;
		expX = x;
		expY = y;
	}
	//end
}