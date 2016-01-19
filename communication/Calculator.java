class Calculator{
	public ArrayList<GravPoint> gravPointsList;

	public Calculator(){
		gravPointsList = new ArrayList<GravPoint>();
	}

	public void get_enemy_pointX(double radian, double distance){
		// 基準は実機
		double x = distance*cos(radian);
		double y = distance*sin(radian);
	}

	public void get_enemy_pointY(double radian, double distance){
		// 基準は実機
		double x = distance*cos(radian);
		double y = distance*sin(radian);
	}

	public void antiGrav(){
		double x_force = 0;
		double y_force = 0;
		double sum_force = 0;
		double rad;
		GravPoint gravPoint;
		

		for (GravPoint nowGravPoint : pointsList){
			sum_force = nowGravPoint.power/Math.pow(Math.sqrt(Math.pow(getX - nowGravPoint.x,2)+Math.pow(getY() - nowGravPoint.y,2)));
			rad = nowGravPoint.getRadian(getX,getY);
			xforce += Math.sin(rad) * sum_force;
			yforce += Math.cos(rad) * sum_force;
		}

    	x_force += 5000/Math.pow(getBattleFieldWidth() - getX(), 3);
	    x_force -= 5000/Math.pow(getX(), 3);
	    y_force += 5000/Math.pow(getBattleFieldHeight() - getY(), 3);
	    y_force -= 5000/Math.pow(getY()3);

	    System.out.println("x_force" + x_force);
	    System.out.println("y_force" + y_force);
	}


	public void testgrav(){
		gravPointsList.add(new GravPoint(43,39,78));
		gravPointsList.add(new GravPoint(71,35,48));
		gravPointsList.add(new GravPoint(30,52,64));
		gravPointsList.add(new GravPoint(20,77,48));
		gravPointsList.add(new GravPoint(79,89,88));
		gravPointsList.add(new GravPoint(4,71,3));
	}
}

class GravPoint {
    public double x,y,power;
    public GravPoint(double XX,double YY,double PP) {
        x = XX;
        y = YY;
        power = PP;
    }
    public getRadian(double XX, double YY){
    	return Math.atan2(XX - x, YY - y);;
    }
}