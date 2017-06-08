package helium.games.farmingempire;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Handler;

public class Machinery extends ScreenObject{

	public static final String ALL = "ALL";
	public static final String VEHICLE = "Vehicle";
	public static final String EQUIPMENT = "Equipment";
	public static final String OTHER = "Other";
	public static final String TRACTOR = "Tractor";
	public static final String COMBINE = "Combine";
	public static final String HEADER = "Header";
	public static final String CULTIVATOR = "Cultivator";
	public static final String SOWER = "Sower";
	public static final String SPRAYER = "Sprayer";
	public static final String TRAILER = "Trailer";
	public static final double MIN_TIME = 7.5;		// min seconds per hectare
	public static final double FUEL_USAGE = 0.50;	// fuel usage per hectare
	public static final double FUEL_COST = 3;
	public static final double SEED_COST = 6.5;
	public static final double FERT_COST = 2;
	
	public static final int REFILL_RATE = 1;	// milliseconds
	public static final int REFILL_RATE_PER = 5;	// milliseconds
	
	public static final String TRACTOR1 = "TCB 3230";
	public static final String TRACTOR2 = "BEAR8000";
	public static final String TRACTOR3 = "Star 661";
	public static final String TRACTOR4 = "Series500JD";
	public static final String TRACTOR5 = "PUMA60";
	public static final String TRACTOR6 = "MagnumIH";
	public static final String TRACTOR7 = "Holl T8435";
	public static final String COMBINE1 = "CB99";
	public static final String COMBINE2 = "H7130";
	public static final String HEADER1 = "Rose C6";
	public static final String HEADER2 = "IH3020";
	public static final String CULTIVATOR1 = "Tri500";
	public static final String CULTIVATOR2 = "Terra600";
	public static final String SOWER1 = "DRILL1000";
	public static final String SOWER2 = "Rapid 600";
	public static final String SPRAYER1 = "AzoneUF18";
	public static final String SPRAYER2 = "AzoneGB";
	public static final String TRAILER1 = "HaulerE88";
	public static final String TRAILER2 = "HKD302";
	public static final String TRAILER3 = "TAW30";
	public static final String TRAILER4 = "AS2101";
	
	public static final String TRACTOR_DESC = "Tractors are required to pull trailers and tools.";
	public static final String COMBINE_DESC = "Combines harvest the indicated seed type. It will also need a header.";
	public static final String HEADER_DESC = "Headers are attached to combine harvesters.";
	public static final String CULTIVATOR_DESC = "Cultivators prepare fields for the next sowing.";
	public static final String SOWER_DESC = "Sowing machines sow fields with the indicated seed type.";
	public static final String SPRAYER_DESC = "Sprayers improve the harvest of your fields.";
	public static final String TRAILER_DESC = "Trailers are used to transport the harvest.";
	
	public static final String CATEGORIES[] = { ALL, TRACTOR, COMBINE, HEADER, CULTIVATOR, SOWER, SPRAYER, TRAILER };
	//public Machinery nullAttachment;
	String type = VEHICLE;
	String subType = TRACTOR;
	double horsePower = 100;
	double cost = 50000;
	double hinder = 5;			//1-10 : fast-slow
	double maxFuel = 100;
	double fuel = 100;
	int capacity = 1250;
	int id = 999;
	boolean inUse = false;
	double fillAmount = 0;
	String seedInTrailer = Crops.WHEAT;
	String description = TRACTOR_DESC;
	
	
	boolean refilling = false;
	Handler handler = new Handler();
	
	public Machinery(Resources res, Engine level, String name, String type, String subType ) {
		
		super(res, level);
		this.name = name;
		this.type = type;
		this.subType = subType;
		setupDescriptions();
		//nullAttachment = new Machinery(res, level, "", null, EQUIPMENT, "null", 0, 0, 0, 0);
	}

	public Machinery(Resources res, Engine level, String name, Bitmap image, String type, String subType, double cost, double power, double hinder, double maxFuel ) {
		
		super(res, level);
		this.name = name;
		this.image = image;
		this.type = type;
		this.subType = subType;
		this.cost = cost;
		this.horsePower = power;
		this.hinder = hinder;
		this.maxFuel = maxFuel;
		this.capacity = (int) maxFuel;
		this.fuel = maxFuel;
		
		setupDescriptions();
		//nullAttachment = new Machinery(res, level, "", null, EQUIPMENT, "null", 0, 0, 0, 0);
	}

	public void setupDescriptions(){

		if( subType.equals(COMBINE) )
			this.description = COMBINE_DESC;
		else if( subType.equals(HEADER) )
			this.description = HEADER_DESC;
		else if( subType.equals(CULTIVATOR) )
			this.description = CULTIVATOR_DESC;
		else if( subType.equals(SOWER) )
			this.description = SOWER_DESC;
		else if( subType.equals(SPRAYER) )
			this.description = SPRAYER_DESC;
		else if( subType.equals(TRAILER) )
			this.description = TRAILER_DESC;
	
		if( this.name.equals(SOWER2) )
			this.description += " This machine also cultivates.";
	}
	
	public boolean useFuel( double amount ){
		
		boolean result = true;
		
		if( amount >= this.fuel ){
			result = false;
		}else{
			this.fuel -= amount;
		}
		
		return result;
	}
	
	public void refillTrailer(){
		
		refilling = true;
		handler.post(refillTrailer);
	}
	
	public void stopFillingTrailer( Engine engine ){
		
		refilling = false;

		if( seedInTrailer.equals( Crops.WHEAT ) ){
			if( fuel > engine.profile.crops.wheat ){
				fuel = engine.profile.crops.wheat;
			}
		}
		
		if( seedInTrailer.equals( Crops.BARLEY ) ){
			if( fuel > engine.profile.crops.barley ){
				fuel = engine.profile.crops.barley;
			}
		}
		
		if( seedInTrailer.equals( Crops.CANOLA ) ){
			if( fuel > engine.profile.crops.canola ){
				fuel = engine.profile.crops.canola;
			}
		}
		
		/*if( seedInTrailer.equals( Crops.WHEAT ) ){
			engine.profile.crops.wheat -= fillAmount;
		}else if( seedInTrailer.equals( Crops.BARLEY ) ){
			engine.profile.crops.barley -= fillAmount;
		}else if( seedInTrailer.equals( Crops.CANOLA ) ){
			engine.profile.crops.canola -= fillAmount;
		}
		
		fillAmount = 0;
		*/
	}
	
	@Override
	public String toString(){
		String string = this.id + "," + this.type + "," + this.subType + "," + this.name + "," + this.horsePower + "," + this.cost + "," + this.hinder + "," + this.maxFuel + "," + this.fuel + "," + this.description + "," + this.capacity;
		return string;
	}
	
	public Runnable refillTrailer = new Runnable() {  
		public void run() {  
			
			if( fuel < capacity ){
				fuel += REFILL_RATE_PER;
				fillAmount += REFILL_RATE_PER;
			}else{
				fuel = capacity;
				refilling = false;
				
			}
			
			if( refilling == true )
				handler.postDelayed(refillTrailer, REFILL_RATE);
		}
	};
	
}
