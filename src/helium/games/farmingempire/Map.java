package helium.games.farmingempire;

import java.util.ConcurrentModificationException;
import java.util.Random;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;

public class Map {

	public static final int GOLD_CHANCE = 5;	//Percent
	
	public String name = "Map01";
	public double cost = 0;
	public int totalFields = 1;
	public int totalSellPoints = 1;
	public int startMoney = 0;
	public int startGold = 0;
	public Machinery[] startingMachines;
	public Field[] fields;
	public SellPoint[] sellPoints;
	public Bitmap map;
	public boolean drawFieldRects = true;
	
	public boolean hasGoldCoin = false;
	public Point goldLocation;
	
	public Map() {
		
	}
	
	public Map Map01( Resources res, Engine engine ){

		Map map = new Map();
		map.map = BitmapFactory.decodeResource(res, R.drawable.map01med);
		map.map = Bitmap.createScaledBitmap(map.map,engine.screen_width , engine.screen_height,true);
		map.name = "Bjorhorn";
		map.startMoney = 16000;
		map.startGold = 3;
		map.totalFields = 10;
		map.totalSellPoints = 4;
		
		Rect field1Area = new Rect( engine.getPercentSize(62), engine.getPercentSize(37f), engine.getPercentSize(75), engine.getPercentSize(53f));
		Field field1 = new Field( res, engine, field1Area, 1, 4 );//Estimate:4
		field1.owned = true;

		Rect field2Area = new Rect( engine.getPercentSize(54), engine.getPercentSize(55f), engine.getPercentSize(87), engine.getPercentSize(68f));
		Field field2 = new Field( res, engine, field2Area, 2, 8.02 );//Estimate:5
		
		Rect field3Area = new Rect( engine.getPercentSize(66), engine.getPercentSize(69f), engine.getPercentSize(91), engine.getPercentSize(90f));
		Field field3 = new Field( res, engine, field3Area, 3, 9.80 );//Estimate:6
		
		Rect field4Area = new Rect( engine.getPercentSize(47), engine.getPercentSize(69f), engine.getPercentSize(64), engine.getPercentSize(90f));
		Field field4 = new Field( res, engine, field4Area, 4, 6.75 );//Estimate:5
		
		Rect field5Area = new Rect( engine.getPercentSize(48), engine.getPercentSize(54.3f), engine.getPercentSize(53), engine.getPercentSize(68f));
		Field field5 = new Field( res, engine, field5Area, 5, 1.32 );//Estimate:1.25
		
		Rect field6Area = new Rect( engine.getPercentSize(15), engine.getPercentSize(61.3f), engine.getPercentSize(45), engine.getPercentSize(90f));
		Field field6 = new Field( res, engine, field6Area, 6, 15.75 );//Estimate:7.5
		
		Rect field7Area = new Rect( engine.getPercentSize(34), engine.getPercentSize(36.4f), engine.getPercentSize(60), engine.getPercentSize(52.5f));
		Field field7 = new Field( res, engine, field7Area, 7, 8.15 );//Estimate:6.25
		
		Rect field7_1Area = new Rect( engine.getPercentSize(34), engine.getPercentSize(52.5f), engine.getPercentSize(46), engine.getPercentSize(60f));
		field7.combinedArea = field7_1Area;
		
		Rect field8Area = new Rect( engine.getPercentSize(10), engine.getPercentSize(36.4f), engine.getPercentSize(33), engine.getPercentSize(60f));
		Field field8 = new Field( res, engine, field8Area, 8, 10.45 );//Estimate:6
		
		Rect field9Area = new Rect( engine.getPercentSize(11), engine.getPercentSize(7.4f), engine.getPercentSize(60), engine.getPercentSize(34.3f));
		Field field9 = new Field( res, engine, field9Area, 9, 22.25 );//Estimate:12
		
		Rect field10Area = new Rect( engine.getPercentSize(62), engine.getPercentSize(7.3f), engine.getPercentSize(91), engine.getPercentSize(33.5f));
		Field field10 = new Field( res, engine, field10Area, 10, 14.05 );//Estimate:7.5
		
		Rect field9999Area = new Rect( -100, -100, -99, -99 );
		Field field9999 = new Field( res, engine, field9999Area, 9999, 0 );//Null
		
		map.fields = new Field[]{ field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field9999 };
		try{
			for( Field field : map.fields ){
				
				//field.hectare = field.getArea()/2002;	//Will adjust hectares according to screen size
				field.cost = (int) (Field.HECTARE_COST * field.hectare);
				
			}
		}catch( ConcurrentModificationException err ){}
		
		
		//Sell Points
		//Warehouse 1 loc:25,31,38,34.8
		//Warehouse 2 loc:83,10,92,13.3
		//Warehouse 3 loc:88,60,92,62.5
		//Warehouse 4 loc:10,83.5,15,93
		Rect mil_btn = new Rect( engine.getPercentSize(25), engine.getPercentSize(31f), engine.getPercentSize(38), engine.getPercentSize(34.8f));
		SellPoint mill = new SellPoint( "Mill", 101, mil_btn );
		mill.distance = 2;
		
		Rect sta_btn = new Rect( engine.getPercentSize(82), engine.getPercentSize(7f), engine.getPercentSize(92), engine.getPercentSize(14.3f));
		SellPoint station = new SellPoint( "Station", 102, sta_btn );
		station.distance = 2.5;
		
		Rect inn_btn = new Rect( engine.getPercentSize(88), engine.getPercentSize(58f), engine.getPercentSize(92), engine.getPercentSize(65.5f));
		SellPoint inn = new SellPoint( "Inn", 103, inn_btn );
		inn.distance = 1.75;
		
		Rect pla_btn = new Rect( engine.getPercentSize(10), engine.getPercentSize(83.5f), engine.getPercentSize(15), engine.getPercentSize(93f));
		SellPoint plant = new SellPoint( "Plant", 104, pla_btn );
		plant.distance = 2.5;
		
		map.sellPoints = new SellPoint[]{ mill, station, inn, plant };
		
		Random random = new Random();
		
		//Gold coins
		if( random.nextInt(100) <= GOLD_CHANCE ){ 

			map.hasGoldCoin = true;
		}
		
		//Indemand 
		try{
			for( SellPoint sell : map.sellPoints ){
				boolean inDemand = false;
				if( random.nextInt(100) <= SellPoint.DEMAND_CHANCE ){ //In Demand
					inDemand = true;
				}
				//Wheat
				int min = (int) (SellPoint.WHE_PRICE * SellPoint.MIN_DIF);
				int max = (int) (SellPoint.WHE_PRICE * SellPoint.MAX_DIF);
				sell.whe_price = random.nextInt((max - min) + 1) + min;
				//Log.e(sell.name, sell.whe_price+"");
				if(inDemand == true ){
					if( random.nextInt(100) <= SellPoint.DEMAND_CHANCE ){ //In Demand
						min = (int) (SellPoint.WHE_PRICE * SellPoint.MIN_DIF);
						max = (int) (SellPoint.WHE_PRICE * SellPoint.MAX_DIF);
						sell.whe_price = (random.nextInt((max - min) + 1) + min) * 2;
						sell.inDemand = true;
						sell.whe_demand = true;
						//Log.e(sell.name, sell.whe_price+"DEMAND");
					}
				}
				
				//Barley
				min = (int) (SellPoint.BAR_PRICE * SellPoint.MIN_DIF);
				max = (int) (SellPoint.BAR_PRICE * SellPoint.MAX_DIF);
				sell.bar_price = random.nextInt((max - min) + 1) + min;
				//Log.e(sell.name, sell.bar_price+"");
				if(inDemand == true ){
					if( random.nextInt(100) <= SellPoint.DEMAND_CHANCE ){ //In Demand
						min = (int) (SellPoint.BAR_PRICE * SellPoint.MIN_DIF);
						max = (int) (SellPoint.BAR_PRICE * SellPoint.MAX_DIF);
						sell.bar_price = (random.nextInt((max - min) + 1) + min) * 2;
						sell.inDemand = true;
						sell.bar_demand = true;
						//Log.e(sell.name, sell.bar_price+"DEMAND");
					}
				}
				
				//Canola
				min = (int) (SellPoint.CAN_PRICE * SellPoint.MIN_DIF);
				max = (int) (SellPoint.CAN_PRICE * SellPoint.MAX_DIF);
				sell.can_price = random.nextInt((max - min) + 1) + min;
				//Log.e(sell.name, sell.can_price+"");
				if(inDemand == true ){
					if( random.nextInt(200) <= SellPoint.DEMAND_CHANCE ){ //In Demand
						min = (int) (SellPoint.CAN_PRICE * SellPoint.MIN_DIF);
						max = (int) (SellPoint.CAN_PRICE * SellPoint.MAX_DIF);
						sell.can_price = (random.nextInt((max - min) + 1) + min) * 2;
						sell.inDemand = true;
						sell.can_demand = true;
						//Log.e(sell.name, sell.can_price+"DEMAND");
					}
				}
				
			}
		}catch( ConcurrentModificationException err ){}
		
		
		//Machines
		
		int imageW = 18;
		float imageH = 36;
		Bitmap t1 = BitmapFactory.decodeResource(res, R.drawable.tracter1_ill3);
		t1 = Bitmap.createScaledBitmap(t1,engine.getPercentSize(imageW) , engine.getPercentSize(imageH),true);
		Bitmap c1 = BitmapFactory.decodeResource(res, R.drawable.combine_ill2);
		c1 = Bitmap.createScaledBitmap(c1,engine.getPercentSize(imageW) , engine.getPercentSize(imageH),true);
	
		Bitmap cult1 = BitmapFactory.decodeResource(res, R.drawable.cultivator1_ill);
		cult1 = Bitmap.createScaledBitmap(cult1,engine.getPercentSize(imageW) , engine.getPercentSize(imageH),true);
		Bitmap seed1 = BitmapFactory.decodeResource(res, R.drawable.seeder1_ill);
		seed1 = Bitmap.createScaledBitmap(seed1,engine.getPercentSize(imageW) , engine.getPercentSize(imageH),true);
		Bitmap tip1 = BitmapFactory.decodeResource(res, R.drawable.tipper1_ill);
		tip1 = Bitmap.createScaledBitmap(tip1,engine.getPercentSize(imageW) , engine.getPercentSize(imageH),true);
		Bitmap head1 = BitmapFactory.decodeResource(res, R.drawable.header1_ill1);
		head1 = Bitmap.createScaledBitmap(head1,engine.getPercentSize(imageW) , engine.getPercentSize(imageH),true);
		
		Machinery tractor1 = new Machinery(res, engine, Machinery.TRACTOR1, t1, Machinery.VEHICLE, Machinery.TRACTOR, 37000, 90, 7, 50);
		Machinery combine1 = new Machinery(res, engine, Machinery.COMBINE1, c1, Machinery.VEHICLE, Machinery.COMBINE, 105000, 185, 10, 122);
		Machinery cultivator1 = new Machinery(res, engine, Machinery.CULTIVATOR1, cult1, Machinery.EQUIPMENT, Machinery.CULTIVATOR, 10570, 60, 8, 0);
		Machinery seeder1 = new Machinery(res, engine, Machinery.SOWER1, seed1, Machinery.EQUIPMENT, Machinery.SOWER, 15250, 90, 8, 120);
		Machinery header1 = new Machinery(res, engine, Machinery.HEADER1, head1, Machinery.EQUIPMENT, Machinery.HEADER, 30000, 160, 8, 0);
		Machinery tipper1 = new Machinery(res, engine, Machinery.TRAILER1, tip1, Machinery.EQUIPMENT, Machinery.TRAILER, 7000, 90, 1, 0);
		tipper1.capacity = 8500;
		
		map.startingMachines = new Machinery[]{ tractor1, combine1, header1, cultivator1, seeder1, tipper1 };
		
		try{
			for( Machinery mchn : map.startingMachines ){
				
				mchn.id = engine.profile.machineryOwned;
				engine.profile.machineryOwned++;
			}
		}catch( ConcurrentModificationException err ){}
		
		//Sell Points
			//Warehouse 1 loc:25,31,38,34.8
			//Warehouse 2 loc:83,10,92,13.3
			//Warehouse 3 loc:10,83.5,15,93
			//Warehouse 4 loc:88,60,92,62.5
		
		//Store loc: 42,61,46,68
		
		//Home loc: 55,54,60,60
		
		return map;
	}

}
