package helium.games.farmingempire;

import android.graphics.Rect;

public class SellPoint {

	/*
	 * Names:
	 * Station
	 * Mill
	 * Inn
	 * Plant
	 */
	public static final int WHE_PRICE = 600;
	public static final int BAR_PRICE = 700;
	public static final int CAN_PRICE = 950;
	public static final double MIN_DIF = 0.75;
	public static final double MAX_DIF = 1.25;
	public static final double DEMAND_CHANCE = 10; //Percent

	String name = "Sell Point";
	int id = 999;
	
	public Rect area;
	public Rect combinedArea = null;
	
	double distance = 2; //Distance from farm
	
	int whe_price = WHE_PRICE;
	int bar_price = BAR_PRICE;
	int can_price = CAN_PRICE;

	boolean inDemand = false;
	boolean whe_demand= false;
	boolean bar_demand = false;
	boolean can_demand = false;
	
	public SellPoint( String name, int id, Rect area ) {
		
		this.name = name;
		this.id = id;
		this.area = area;
	}

	public double getPrice( String crop ){
		
		double price = whe_price;
		
		if( crop.equals(Crops.BARLEY) )
			price = bar_price;
		else if( crop.equals(Crops.CANOLA) )
			price = can_price;
		
		return price;
	}
	
}
