package helium.games.farmingempire;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.Handler;

public class Job {

	public static final String CULTIVATING = "Cultivating";
	public static final String SOWING = "Sowing";
	public static final String FERTILIZING = "Fertilizing";
	public static final String HARVESTING = "Harvesting";
	public static final String SELLING = "Selling";
	public static final int WORKER_COST = 50; //Worker cost per hectare
	
	Engine engine;
	int vehicleID = 999;
	int attachmentID = 999;
	int fieldID = 999;
	String seed = Crops.WHEAT;
	int amount = 0;
	int sellPrice = SellPoint.WHE_PRICE;
	boolean selling = false;
	String jobType = CULTIVATING;
	Calendar ETA;
	Calendar now;
	int totalTime = 0;		//Seconds
	Handler timer = new Handler();
	boolean finished = false;
	
	
	public Job( Engine engine, int vehicle, int attachment, int field, String crop, int amount, Calendar eta ) {
		
		this.engine = engine;
		this.vehicleID = vehicle;
		this.attachmentID = attachment;
		this.fieldID = field;
		this.seed = crop;
		this.amount = amount;
		this.ETA = eta;
		now = Calendar.getInstance();
		timer.post(refresh);
		
		if( amount > 0 )
			selling = true;
	}

	public String getInfo(){
		
		String result;
		if( jobType.equals(SELLING) )
			result = engine.profile.getMachineByID(vehicleID).name + " is " + jobType + " " + engine.toThousands(amount) +" l of " + seed;	
		else
			result = engine.profile.getMachineByID(vehicleID).name + " is " + jobType + " Field " + engine.getFieldByID(fieldID).id;
		
		return result;
	}
	
	public String timeLeft(){
		
		String result;
		long seconds = (ETA.getTimeInMillis()-now.getTimeInMillis())/1000;
		String end = " seconds";
		if( seconds > 60 ){
			seconds = seconds/60;
			end = " minutes";
		}
		if( seconds > 60 ){
			seconds = seconds/60;
			end = "hours";
		}
		result = seconds+end;
		
		return result;
	}
	
	public String timeLeftLong(){
		
		String result;
		long seconds = (ETA.getTimeInMillis()-now.getTimeInMillis())/1000;
		long minutes = seconds/60;
		long hours = minutes/60;
		String end = " seconds";
		result = seconds+end;
		if( seconds > 60 ){
			int secs =  (int) (seconds)%60;
			if( secs < 10 )
				result = minutes+":0"+secs;
			else
				result = minutes+":"+secs;
			if( minutes >= 2 )
				result = result + " minutes";
			else
				result = result + " minute";
		}
		if( minutes > 60 ){
			int secs =  (int) (seconds)%60;
			minutes =  (int) (minutes)%60;
			if( secs < 10 ){
				if( minutes < 10 )
					result = hours+":0"+minutes+":0"+secs;
				else
					result = hours+":"+minutes+":0"+secs;
				
			}else{
				if( minutes < 10 )
					result = hours+":0"+minutes+":"+secs;
				else
					result = hours+":"+minutes+":"+secs;
				
			}
			if( hours >= 2 )
				result = result + " hours";
			else
				result = result + " hour";
		}
		
		
		return result;
	}
	
	Runnable refresh = new Runnable() {
		
		@Override
		public void run() {
			
			now = Calendar.getInstance();
			if(now.after(ETA)){							//Job done
			
				if( selling == false )
					finishedJob();
				else
					finishedSelling();
			}
			
			if(finished == false)
				timer.postDelayed(refresh, 1000);
			
		}
	};
	
	public void finishedJob(){
		
		engine.profile.workedTime += totalTime;
		engine.toast("Field " + engine.getFieldByID(fieldID).id + " is done " + jobType, 3);
		
		engine.profile.getMachineByID(vehicleID).inUse = false;
		engine.profile.getMachineByID(attachmentID).inUse = false;

		engine.getFieldByID(fieldID).busy = false;
		if( engine.profile.getMachineByID(attachmentID).name.equals( Machinery.SOWER2 ) ){
			engine.getFieldByID(fieldID).job_cultivated = true;
		}
		if( jobType.equals(CULTIVATING) )
			engine.getFieldByID(fieldID).job_cultivated = true;
		else if( jobType.equals(SOWING) ){
			engine.getFieldByID(fieldID).job_sowed = true;
			engine.getFieldByID(fieldID).seed = seed;
		}else if( jobType.equals(FERTILIZING) )
			engine.getFieldByID(fieldID).job_fertilized = true;
		else if( jobType.equals(HARVESTING) ){
			engine.getFieldByID(fieldID).job_harvested = true;
			
			//give inventory
			double pounds = (engine.getFieldByID(fieldID).hectare*Crops.LBS_PER_HECTARE);
			String lbs = engine.toDecimal(pounds);
			pounds = Double.parseDouble(lbs);
			if( engine.getFieldByID(fieldID).job_fertilized == true )
				pounds*=2;
			
			if( seed.equals(Crops.WHEAT) )
				engine.profile.crops.wheat += (int) pounds;
			else if( seed.equals(Crops.BARLEY) )
				engine.profile.crops.barley += (int) pounds;
			else if( seed.equals(Crops.CANOLA) )
				engine.profile.crops.canola += (int) pounds;
			
			engine.toast("Harvested " + engine.toThousands(pounds) +" l of " + seed , 3);
			/*
			//sell
			double money = pounds * (engine.profile.crops.getPrice(engine.getFieldByID(fieldID).seed)/1000);
			money = Double.parseDouble(engine.toDecimal(money));
			engine.profile.money += money;
			engine.toast( "Sold for " + engine.moneyToString(money),3 );
			*/
			//Reset field
			engine.getFieldByID(fieldID).job_cultivated = false;
			engine.getFieldByID(fieldID).job_sowed = false;
			engine.getFieldByID(fieldID).job_fertilized = false;
			engine.getFieldByID(fieldID).job_harvested = false;
		}
		
		if( engine.drawActivityDialog == true ){
			try{
				engine.activityFieldDialog( engine.selectedField.busy, engine.activityDialog.sellPoint );
			}catch( NullPointerException err ){}
		}
		finished = true;
		timer = new Handler();
	
	}
	
	public void finishedSelling(){

		engine.profile.workedTime += totalTime;
		engine.toast("Sold " + engine.toThousands( amount ) + " l of " + seed, 3);
		
		engine.profile.getMachineByID(vehicleID).inUse = false;
		engine.profile.getMachineByID(attachmentID).inUse = false;
		if( engine.profile.getMachineByID(attachmentID).subType.equals( Machinery.TRAILER ) ){
			engine.profile.getMachineByID(attachmentID).fuel = 0;
		}
		engine.profile.getMachineByID(attachmentID).seedInTrailer = Crops.WHEAT;
		
			//Sell inventory
			/*if( seed.equals(Crops.WHEAT) )
				engine.profile.crops.wheat -= (int) amount;
			if( seed.equals(Crops.BARLEY) )
				engine.profile.crops.barley -= (int) amount;
			if( seed.equals(Crops.CANOLA) )
				engine.profile.crops.canola -= (int) amount;*/

			//Sell
			double money = (double) amount * ((double)sellPrice/1000f);
			money = Double.parseDouble(engine.toDecimal(money));
			engine.profile.money += money;
			engine.profile.totalEarned += money;
			engine.toast( "Sold for " + engine.moneyToString(money),3 );
			engine.playSound( engine.sound_money );

		try{
			if( engine.drawActivityDialog == true ){
				engine.activityFieldDialog( engine.selectedField.busy, engine.activityDialog.sellPoint );//TODO may cause error
			}
		} catch( NullPointerException err ){}
		
		finished = true;
		timer = new Handler();
	}
	
	@Override
	public String toString(){
		String string = this.vehicleID + "," + this.attachmentID + "," + this.fieldID + "," + this.seed + "," + this.jobType + "," + this.selling + "," + this.sellPrice + "," + this.amount + "," + this.totalTime + "," + this.ETA.getTimeInMillis();
		return string;
	}
	
}

