package helium.games.farmingempire;

public class Crops {
	
	public static final String CROP = "Crop";
	public static final String SEED = "Seed";
	public static final String WHEAT = "Wheat";
	public static final String BARLEY = "Barley";
	public static final String CANOLA = "Canola";
	public static final int LBS_PER_HECTARE = 2500;
	
	public double whe_price = 600; // $/ton
	public double bar_price = 650; // $/ton
	public double can_price = 1100; // $/ton
	
	public int wheat = 0;
	public int barley = 0;
	public int canola = 0;
	
	public static String[] crops = { WHEAT, BARLEY, CANOLA };
	
	public double getPrice( String crop ){
		
		double price = whe_price;
		
		if( crop.equals(BARLEY) )
			price = bar_price;
		else if( crop.equals(CANOLA) )
			price = can_price;
		
		return price;
	}
	
	public boolean checkInventory( String crop, int amount ){
		boolean result = false;
		
		if( crop.equals( Crops.WHEAT ) ){
			if( wheat > amount){
				result = true;
			}
		}
		
		if( crop.equals( Crops.BARLEY ) ){
			if( barley > amount){
				result = true;
			}
		}
		
		if( crop.equals( Crops.CANOLA ) ){
			if( canola > amount){
				result = true;
			}
		}
		
		return result;
	}
	
}
