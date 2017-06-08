package helium.games.farmingempire;

import android.content.res.Resources;
import android.graphics.Rect;

public class Field extends ScreenObject {

	public static final int HECTARE_COST = 30000;
	public Rect area;
	public Rect combinedArea = null;
	public double hectare = 1;
	public int id = 999;
	public int cost = 100000;
	public boolean owned = false;
	//public Activity activity;
	public String seed = Crops.WHEAT;
	public boolean busy = false;
	public boolean job_cultivated = false;
	public boolean job_sowed = false;
	public boolean job_fertilized = false;
	public boolean job_harvested = false;

	
	public Field(Resources res, Engine level, Rect area, int id, double hectare ) {
		super(res, level);
		this.area = area;
		this.x = area.left;
		this.y = area.top;
		this.width = area.width();
		this.height = area.height();
		this.id = id;
		this.hectare = hectare;
	}

	public double getArea(){
		
		double result;
		result = area.width()*area.height();
		return result;
	}
	
	@Override
	public String toString(){
		
		String string = id + "," + owned + "," + seed + "," + job_cultivated + "," + job_sowed + "," + job_fertilized + "," + job_harvested;
		return string;
	}
	
}

