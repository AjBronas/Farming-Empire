package helium.games.farmingempire;


import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class Engine extends Activity {

	public static enum DIFFICULTY { EASY, NORMAL, HARD };
	public static final int BUTTON_SIZE_P = 35;
	public static final String NEW_GAME = "New Game";
	public static final String RESUME_GAME = "Resume Game";
	public static final String TYPE_BUTTON = "BUTTON";
	public static final String TYPE_IMAGE = "IMAGE";
	public static final String TYPE_ANIMATION = "ANIMATION";
	public static final String INVENTORY = "Inventory";
	public static final String ACTIVITES = "Activites";
	public static final String PLAY = "Play";
	public static final String LOADING = "Loading";
	public static final String PAUSE = "Pause";
	public static final String QUIT = "Quit";
	public static final String STATS = "Stats";
	public static final String MENU = "Menu";
	public static final String OPTIONS = "Options";
	public static final String MAIN_SCREEN = "Main Screen";
	public static final String FARM = "Farm";
	public static final String STORE = "Store";
	public static final String GARAGE = "Garage";
	public static final String TUTORIAL = "Tutorial";
	public static final String BUY = "Buy";
	public static final String SELL = "Sell";
	public static final String GO = "Go";
	public static final String CANCEL = "Cancel";
	public static final String BACK = "Back";
	public static final String LEFT = "Left";
	public static final String RIGHT = "Right";
	
	public static int screen_width = 800;
	public static int screen_height = 400;

	SharedPreferences getGarageSettings;
	SharedPreferences getActivitesSettings;
	SharedPreferences getProfileSettings;
	SharedPreferences.Editor garageEditor;
	SharedPreferences.Editor activitesEditor;
	SharedPreferences.Editor profileEditor;

	
	public String screen = "World";
	public List<ScreenButton> buttons;
	public ArrayList<String> toastLog = new ArrayList<String>();
	Canvas canvas;
	Bitmap canvasB;
	Handler handler = new Handler();
	Handler timeHandler = new Handler();
	int timeDelay = 1000;

	ButtonFunctions btnFunctions = new ButtonFunctions(this);
	Profile profile = new Profile( this );
	public Map selectedMap = new Map();
	ScreenButton selectedBtn = null;
	Field selectedField = null;
	SellPoint selectedSellPoint = null;
	boolean selectingField = true;
	boolean drawSellPointIcons = true;
	boolean drawFPS = false;
	
	View levelView = null;
	Store storeView = new Store();
	ActivityDialog activityDialog = new ActivityDialog();
	Tutorial tutorial;
	
	boolean newGameConfirm = false;
	boolean drawPurchaseDialog = false;
	boolean drawActivityDialog = false;
	boolean drawGarageDialog = false;
	int garageDialogPage = 0;
	
	boolean loading = false;
	boolean saving = false;
	boolean canDraw=true;
	boolean touchEnabled = true;
	boolean showOptions = true;
	boolean enableSound = true;
	boolean enableNotif = false;
	
	boolean enableAutoSave = false;
	
	
	//Sounds
	MediaPlayer sound_click;
	MediaPlayer sound_money;
	MediaPlayer sound_tractor;
	
	//Crop garage items
	Machinery wheatItem;
	Machinery barleyItem;
	Machinery canolaItem;
	
	@Override
	public void onBackPressed() {

		tutorial.tutorialOpen = false;
		
		if( screen == FARM && drawGarageDialog == true ){
			openFarm();
			if( selectingField == true )
				activityFieldDialog( selectedField.busy, activityDialog.sellPoint );
			else
				activityFieldDialog( false, true );
			
			return;
		}
		
		if( screen == FARM || screen == STORE || screen == GARAGE || screen == STATS){
			
			if( (screen == FARM && drawPurchaseDialog == true) || (screen == FARM && drawActivityDialog == true) ){
				openFarm();
			}else{
				openMainScreen();
			}
			return;
		}
		
		if( screen == MAIN_SCREEN){
			
			save( true );
			openMenu();
			return;
		}

		if( screen == OPTIONS ){
			
			openMenu();
			return;
		}
		
		if( screen == MENU ){
			
			finish();
			return;
		}
		
		super.onBackPressed();	
	}

	@Override
	protected void onResume() {

		super.onResume();
		levelView.LevelView_OnResume();

	}

	@Override
	protected void onPause() {

		super.onPause();
		levelView.LevelView_OnPause();
		
	}



	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		DisplayMetrics dismet = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dismet);
		screen_width = dismet.widthPixels;
		screen_height = dismet.heightPixels;
		
		wheatItem = new Machinery(getResources(), this, Crops.WHEAT, getImage( Crops.WHEAT ), Crops.SEED, Crops.SEED, 0, 0, 0, 0);
		barleyItem = new Machinery(getResources(), this, Crops.BARLEY, getImage( Crops.BARLEY ), Crops.SEED, Crops.SEED, 0, 0, 0, 0);;
		canolaItem = new Machinery(getResources(), this, Crops.CANOLA, getImage( Crops.CANOLA ), Crops.SEED, Crops.SEED, 0, 0, 0, 0);;
		
		
		buttons = new ArrayList<ScreenButton>();

		levelView = new View(this,screen_width,screen_height, this);
		levelView.canvasW = dismet.widthPixels;
		levelView.canvasH = dismet.heightPixels;   
		
		setContentView(levelView);

		getGarageSettings = getSharedPreferences("getGarage",0); 
		getActivitesSettings = getSharedPreferences("getActivites",0);
		getProfileSettings = getSharedPreferences("getPlayerInfo",0); 
		garageEditor = getGarageSettings.edit(); 
		activitesEditor = getActivitesSettings.edit(); 
		profileEditor = getProfileSettings.edit();

		setupSounds();
		
		tutorial = new Tutorial( this );
		
		
		enableSound = getProfileSettings.getBoolean( "OptionsSound", true );
		enableAutoSave = getProfileSettings.getBoolean( "OptionsSave", false );
		enableNotif = getProfileSettings.getBoolean( "OptionsNotif", false );
		
		openMenu();
		timer.run();
	}

	public void setupSounds(){
		
		sound_click = MediaPlayer.create(getApplicationContext(), R.raw.click1quite);
		sound_money = MediaPlayer.create(getApplicationContext(), R.raw.moneyquite);
		sound_tractor = MediaPlayer.create(getApplicationContext(), R.raw.tractor);
	}
	
	public void newGame( DIFFICULTY difficulty, int map ){
		
		loading = true;
		activitesEditor.clear();
		activitesEditor.commit();
		profileEditor.clear();
		profileEditor.commit();
		garageEditor.clear();
		garageEditor.commit();
		
		//--Clear any past data--
				selectedMap = new Map();
				selectedBtn = null;
				selectedField = null;
				selectedSellPoint = null;
				
				storeView = new Store();
				activityDialog = new ActivityDialog();
				drawPurchaseDialog = false;
				drawActivityDialog = false;
				drawGarageDialog = false;
				garageDialogPage = 0;
				saving = false;
				profile = new Profile( this );
		//----------------------
		

		Calendar cal = Calendar.getInstance();
		profile.startTime = cal.getTimeInMillis();
		
		profile.map = map;
		
		if( profile.map == 1 )
			selectedMap = selectedMap.Map01( getResources(), this );
		else
			toast("ERROR! Can't find map", 3);

		profile.money = selectedMap.startMoney;
		profile.setGold ( selectedMap.startGold );
		profile.difficulty = difficulty;
		for( Field field : selectedMap.fields){
			
			if( field.owned == true )
				profile.ownedFields.add(field.id);
		}
		for( Machinery mchn : selectedMap.startingMachines ){
			
			profile.machines.add(mchn);
		}

		
		Machinery vehicle = profile.machines.get(0);
		Machinery attachment = profile.machines.get(0);
		for( Machinery mchn : profile.machines ){
			if( mchn.type.equals(Machinery.VEHICLE)){
				vehicle = mchn;
				break;
			}
		}
		for( Machinery mchn : profile.machines ){
			if( mchn.type.equals(Machinery.EQUIPMENT)){
				attachment = mchn;
				break;
			}
		}
		activityDialog = new ActivityDialog( vehicle, attachment, Crops.WHEAT );

		createStore();
		
		openMainScreen();
		loading = false;
	}
	
	public void loadGame( DIFFICULTY difficulty, int map ){
		
		loading = true;
		//--Clear any past data--
				selectedMap = new Map();
				selectedBtn = null;
				selectedField = null;
				selectedSellPoint = null;
				
				storeView = new Store();
				activityDialog = new ActivityDialog();
				drawPurchaseDialog = false;
				drawActivityDialog = false;
				drawGarageDialog = false;
				garageDialogPage = 0;
				saving = false;
				profile = new Profile( this );
		//----------------------
		
		profile.map = map;
		if( profile.map == 1 )
			selectedMap = selectedMap.Map01( getResources(), this );
		else
			toast("ERROR! Can't find map", 3);
		
		profile.money = selectedMap.startMoney;
		profile.difficulty = difficulty;

		//Gold coin
		if( selectedMap.hasGoldCoin == true ){
			Random random = new Random();
			int x = random.nextInt( (int) (screen_width - getPercentSize(View.GOLD_COIN_W) ) );
			int y = random.nextInt( (int) (screen_height - getPercentSize(View.GOLD_COIN_H*2) ) ) + getPercentSize(View.NOTIFICATION_BAR_H);
			
			selectedMap.goldLocation = new Point( x, y );
		}

		if(readData() == false )
			toast("Error reading save file",3);
		
		if( profile.machines.size() > 0 ){
			Machinery vehicle = profile.machines.get(0);
			Machinery attachment = profile.machines.get(0);
			for( Machinery mchn : profile.machines ){
				if( mchn.type.equals(Machinery.VEHICLE)){
					vehicle = mchn;
					break;
				}
			}
			for( Machinery mchn : profile.machines ){
				if( mchn.type.equals(Machinery.EQUIPMENT)){
					attachment = mchn;
					break;
				}
			}
			
			activityDialog = new ActivityDialog( vehicle, attachment, Crops.WHEAT );
		}
		
		createStore();
		openMainScreen();
		loading = false;
	}

	public boolean readData(){
		
		//Load player stats
		profile.money = getProfileSettings.getFloat("Money", selectedMap.startMoney);
		profile.setGold( getProfileSettings.getInt("Coins", 0) );
		profile.machineryOwned = getProfileSettings.getInt("Machines", 4);
		profile.crops.wheat = getProfileSettings.getInt(Crops.WHEAT, 0);
		profile.crops.barley = getProfileSettings.getInt(Crops.BARLEY, 0);
		profile.crops.canola = getProfileSettings.getInt(Crops.CANOLA, 0);
		profile.startTime = getProfileSettings.getLong("StartTime", Calendar.getInstance().getTimeInMillis());
		profile.workedTime = getProfileSettings.getLong("WorkedTime", 0);
		profile.workerCost = getProfileSettings.getFloat("WorkerCost", 0);
		profile.fieldCost = getProfileSettings.getFloat("FieldCost", 0);
		profile.vehicleCost = getProfileSettings.getFloat("VehicleCost", 0);
		profile.maintenanceCost = getProfileSettings.getFloat("MaintenanceCost", 0);
		profile.totalEarned = getProfileSettings.getFloat("TotalEarned", 0);
		profile.moneySpent = getProfileSettings.getFloat("MoneySpent", 0);
		profile.showTutorial_Menu = getProfileSettings.getBoolean( "TutorialMenu", false );
		profile.showTutorial_Farm = getProfileSettings.getBoolean( "TutorialFarm", false );
		
		//Load vehicles
		String actItems = getGarageSettings.getString("Vehicles", "{}");
		String item = "";
		int startOfItem = 0;
		int endOfItem = 0;
		

		while(actItems.length() > 2){
			startOfItem = actItems.indexOf("[") + 1;
			endOfItem = actItems.indexOf("]");
			try{
				item = actItems.substring(startOfItem, endOfItem);
				String tmpF = "[" + item + "]";

				actItems = actItems.replace(tmpF, "");

				String vehid = item.substring(0, item.indexOf(","));
				item = item.substring(vehid.length()+1, item.length());

				String vehtype = item.substring(0, item.indexOf(","));
				item = item.substring(vehtype.length()+1, item.length());

				String vehsub = item.substring(0, item.indexOf(","));
				item = item.substring(vehsub.length()+1, item.length());

				String vehname = item.substring(0, item.indexOf(","));
				item = item.substring(vehname.length()+1, item.length());
				
				String vehpower = item.substring(0, item.indexOf(","));
				item = item.substring(vehpower.length()+1, item.length());
				
				String vehcost = item.substring(0, item.indexOf(","));
				item = item.substring(vehcost.length()+1, item.length());
				
				String vehhinder = item.substring(0, item.indexOf(","));
				item = item.substring(vehhinder.length()+1, item.length());
				
				String vehmax = item.substring(0, item.indexOf(","));
				item = item.substring(vehmax.length()+1, item.length());
				
				String vehfuel = item.substring(0, item.indexOf(","));
				item = item.substring(vehfuel.length()+1, item.length());
				
				String vehdesc = item.substring(0, item.indexOf(","));
				item = item.substring(vehdesc.length()+1, item.length());
				
				int vehcap = Integer.parseInt(item);
				
				Bitmap image = getImage( vehname );
				
				Machinery mchn  = new Machinery(getResources(), this, vehname, image, vehtype, vehsub, Double.parseDouble(vehcost), Double.parseDouble(vehpower), Double.parseDouble(vehhinder), Double.parseDouble(vehmax));
				mchn.fuel = Double.parseDouble(vehfuel);
				mchn.capacity = vehcap;
				mchn.description = vehdesc;
				mchn.id = Integer.parseInt(vehid);
				
				profile.machines.add( mchn );

			}catch( StringIndexOutOfBoundsException e ){
				actItems = "{}";
				toast("Field Error",5);
			}
		}
		
		//Load activities
		actItems = getActivitesSettings.getString("Activities", "{}");
		item = "";
		startOfItem = 0;
		endOfItem = 0;

		while(actItems.length() > 2){
			startOfItem = actItems.indexOf("[") + 1;
			endOfItem = actItems.indexOf("]");
			try{
				item = actItems.substring(startOfItem, endOfItem);
				String tmp = "[" + item + "]";


				actItems = actItems.replace(tmp, "");

				String vehid = item.substring(0, item.indexOf(","));
				item = item.substring(vehid.length()+1, item.length());

				String equipid = item.substring(0, item.indexOf(","));
				item = item.substring(equipid.length()+1, item.length());

				String fieldId = item.substring(0, item.indexOf(","));
				item = item.substring(fieldId.length()+1, item.length());

				String seed = item.substring(0, item.indexOf(","));
				item = item.substring(seed.length()+1, item.length());

				String jobType = item.substring(0, item.indexOf(","));
				item = item.substring(jobType.length()+1, item.length());

				String selling = item.substring(0, item.indexOf(","));
				item = item.substring(selling.length()+1, item.length());
				
				String sellPrice = item.substring(0, item.indexOf(","));
				item = item.substring(sellPrice.length()+1, item.length());
				
				String amount = item.substring(0, item.indexOf(","));
				item = item.substring(amount.length()+1, item.length());
				
				String time = item.substring(0, item.indexOf(","));
				item = item.substring(time.length()+1, item.length());
				
				long etaTime = Long.parseLong(item);
				Calendar eta = Calendar.getInstance();
				eta.setTimeInMillis( etaTime );
				Job job = new Job( this, Integer.parseInt(vehid), Integer.parseInt(equipid), Integer.parseInt(fieldId), seed, Integer.parseInt(amount), eta );
				job.totalTime = Integer.parseInt(time);
				job.selling = Boolean.parseBoolean(selling);
				job.sellPrice = Integer.parseInt(sellPrice);
				job.jobType = jobType;
				
				profile.jobs.add(job);
				if( Boolean.parseBoolean(selling) == false )
					getFieldByID(Integer.parseInt(fieldId)).busy = true;

				profile.getMachineByID(Integer.parseInt(vehid)).inUse = true;
				profile.getMachineByID(Integer.parseInt(equipid)).inUse = true;
				
				

			}catch( StringIndexOutOfBoundsException e ){
				actItems = "{}";
				toast("Activity Error",5);
			}
		}
		
		//Load field
				actItems = getProfileSettings.getString("Fields", "{}");
				item = "";
				startOfItem = 0;
				endOfItem = 0;

				while(actItems.length() > 2){
					startOfItem = actItems.indexOf("[") + 1;
					endOfItem = actItems.indexOf("]");
					try{
						item = actItems.substring(startOfItem, endOfItem);
						String tmpF = "[" + item + "]";

						actItems = actItems.replace(tmpF, "");

						String fldid = item.substring(0, item.indexOf(","));
						item = item.substring(fldid.length()+1, item.length());

						String fldOwned = item.substring(0, item.indexOf(","));
						item = item.substring(fldOwned.length()+1, item.length());

						String seed = item.substring(0, item.indexOf(","));
						item = item.substring(seed.length()+1, item.length());

						String culted = item.substring(0, item.indexOf(","));
						item = item.substring(culted.length()+1, item.length());

						String sowed = item.substring(0, item.indexOf(","));
						item = item.substring(sowed.length()+1, item.length());
						
						String sprayed = item.substring(0, item.indexOf(","));
						item = item.substring(sprayed.length()+1, item.length());
						
						boolean harvested = Boolean.parseBoolean(item);
						getFieldByID(Integer.parseInt(fldid)).owned = Boolean.parseBoolean(fldOwned);
						getFieldByID(Integer.parseInt(fldid)).seed = seed;
						getFieldByID(Integer.parseInt(fldid)).job_cultivated = Boolean.parseBoolean(culted);
						getFieldByID(Integer.parseInt(fldid)).job_sowed = Boolean.parseBoolean(sowed);
						getFieldByID(Integer.parseInt(fldid)).job_fertilized = Boolean.parseBoolean(sprayed);
						getFieldByID(Integer.parseInt(fldid)).job_harvested = harvested;
						
						

					}catch( StringIndexOutOfBoundsException e ){
						actItems = "{}";
						toast("Field Error",5);
					}
				}
				
				
		return true;
	}
	
	public Bitmap getImage( String name ){
		
		Bitmap result = BitmapFactory.decodeResource( getResources(), R.drawable.tracter1_ill3);
		
		int imageW = 18;
		float imageH = 36;
		
		if( name.equals(Machinery.TRACTOR1))
			result = BitmapFactory.decodeResource( getResources(), R.drawable.tracter1_ill3);
		if( name.equals(Machinery.TRACTOR2))
			result = BitmapFactory.decodeResource( getResources(), R.drawable.tractor2_ill2);
		if( name.equals(Machinery.TRACTOR3))
			result = BitmapFactory.decodeResource( getResources(), R.drawable.tractor4_ill);
		if( name.equals(Machinery.TRACTOR4))
			result = BitmapFactory.decodeResource( getResources(), R.drawable.tracter3_ill);
		if( name.equals(Machinery.TRACTOR5))
			result = BitmapFactory.decodeResource( getResources(), R.drawable.tractor5_ill);
		if( name.equals(Machinery.TRACTOR6))
			result = BitmapFactory.decodeResource( getResources(), R.drawable.tractor6_ill2);
		if( name.equals(Machinery.TRACTOR7))
			result = BitmapFactory.decodeResource( getResources(), R.drawable.tractor7_ill);
		if( name.equals(Machinery.HEADER1))
			result = BitmapFactory.decodeResource( getResources(), R.drawable.header1_ill1);
		if( name.equals(Machinery.HEADER2))
			result = BitmapFactory.decodeResource( getResources(), R.drawable.header2_ill);
		if( name.equals(Machinery.COMBINE1))
			result = BitmapFactory.decodeResource( getResources(), R.drawable.combine_ill2);
		if( name.equals(Machinery.COMBINE2))
			result = BitmapFactory.decodeResource( getResources(), R.drawable.combine2_ill);
		if( name.equals(Machinery.CULTIVATOR1))
			result = BitmapFactory.decodeResource( getResources(), R.drawable.cultivator1_ill);
		if( name.equals(Machinery.CULTIVATOR2))
			result = BitmapFactory.decodeResource( getResources(), R.drawable.cult2_ill);
		if( name.equals(Machinery.SOWER1))
			result = BitmapFactory.decodeResource( getResources(), R.drawable.seeder1_ill);
		if( name.equals(Machinery.SOWER2))
			result = BitmapFactory.decodeResource( getResources(), R.drawable.seeder2_ill);
		if( name.equals(Machinery.SPRAYER1))
			result = BitmapFactory.decodeResource( getResources(), R.drawable.fertilizer1_ill);
		if( name.equals(Machinery.SPRAYER2))
			result = BitmapFactory.decodeResource( getResources(), R.drawable.fertilizer2_ill);
		if( name.equals(Machinery.TRAILER1))
			result = BitmapFactory.decodeResource( getResources(), R.drawable.tipper1_ill);
		if( name.equals(Machinery.TRAILER2))
			result = BitmapFactory.decodeResource( getResources(), R.drawable.tipper2_ill);
		if( name.equals(Machinery.TRAILER3))
			result = BitmapFactory.decodeResource( getResources(), R.drawable.tipper3_ill);
		if( name.equals(Machinery.TRAILER4))
			result = BitmapFactory.decodeResource( getResources(), R.drawable.tipper4_ill_blue);
		
		//Crop
		if( name.equals(Crops.WHEAT))
			result = BitmapFactory.decodeResource( getResources(), R.drawable.wheat_ill);
		if( name.equals(Crops.BARLEY))
			result = BitmapFactory.decodeResource( getResources(), R.drawable.barley_ill);
		if( name.equals(Crops.CANOLA))
			result = BitmapFactory.decodeResource( getResources(), R.drawable.canola_ill);
		
		result = Bitmap.createScaledBitmap(result,this.getPercentSize(imageW) , this.getPercentSize(imageH),true);
		
		return result;
	}
	
	private Runnable timer = new Runnable() {  
		public void run() {
			
			if(toastLog.size() > 0){
				toastLog.remove(0);
			}
			
			try{
				for( Job job : profile.jobs ){
					if( job.finished == true && saving == false ){
						profile.jobs.remove(job);
						if( enableAutoSave == true )
							save( false );
					}
						
				}
			}catch( ConcurrentModificationException err ){}
			
			timeHandler.postDelayed(timer, timeDelay);
		}
	};
	
	public void createStore(){
		
		//Tractors
		
		Machinery tractor1 = new Machinery( getResources(), this, Machinery.TRACTOR1, getImage(Machinery.TRACTOR1), Machinery.VEHICLE, Machinery.TRACTOR, 37000, 90, 7, 50);
		Machinery tractor2 = new Machinery( getResources(), this, Machinery.TRACTOR2, getImage(Machinery.TRACTOR2), Machinery.VEHICLE, Machinery.TRACTOR, 44000, 113, 6, 54);
		Machinery tractor3 = new Machinery( getResources(), this, Machinery.TRACTOR3, getImage(Machinery.TRACTOR3), Machinery.VEHICLE, Machinery.TRACTOR, 67500, 133, 5, 57.5);
		Machinery tractor4 = new Machinery( getResources(), this, Machinery.TRACTOR4, getImage(Machinery.TRACTOR4), Machinery.VEHICLE, Machinery.TRACTOR, 99500, 165, 3, 68);
		Machinery tractor5 = new Machinery( getResources(), this, Machinery.TRACTOR5, getImage(Machinery.TRACTOR5), Machinery.VEHICLE, Machinery.TRACTOR, 136500, 203, 2, 87.5);
		Machinery tractor6 = new Machinery( getResources(), this, Machinery.TRACTOR6, getImage(Machinery.TRACTOR6), Machinery.VEHICLE, Machinery.TRACTOR, 288000, 395, 1, 115);
		Machinery tractor7 = new Machinery( getResources(), this, Machinery.TRACTOR7, getImage(Machinery.TRACTOR7), Machinery.VEHICLE, Machinery.TRACTOR, 315250, 435, 0, 198);
		
		storeView.storeItems.add( tractor1 );
		storeView.storeItems.add( tractor2 );
		storeView.storeItems.add( tractor3 );
		storeView.storeItems.add( tractor4 );
		storeView.storeItems.add( tractor5 );
		storeView.storeItems.add( tractor6 );
		storeView.storeItems.add( tractor7 );
		
		//Combines

		Machinery combine1 = new Machinery( getResources(), this, Machinery.COMBINE1, getImage(Machinery.COMBINE1), Machinery.VEHICLE, Machinery.COMBINE, 105000, 185, 10, 122);
		Machinery combine2 = new Machinery( getResources(), this, Machinery.COMBINE2, getImage(Machinery.COMBINE2), Machinery.VEHICLE, Machinery.COMBINE, 235750, 410, 6, 185);
		
		storeView.storeItems.add( combine1 );
		storeView.storeItems.add( combine2 );
		
		//Headers

		Machinery header1 = new Machinery( getResources(), this, Machinery.HEADER1, getImage(Machinery.HEADER1), Machinery.EQUIPMENT, Machinery.HEADER, 30000, 160, 8, 0);
		Machinery header2 = new Machinery( getResources(), this, Machinery.HEADER2, getImage(Machinery.HEADER2), Machinery.EQUIPMENT, Machinery.HEADER, 48000, 325, 4, 0);
		
		storeView.storeItems.add( header1 );
		storeView.storeItems.add( header2 );
				
		//Cultivators
		
		Machinery cultivator1 = new Machinery( getResources(), this, Machinery.CULTIVATOR1, getImage(Machinery.CULTIVATOR1), Machinery.EQUIPMENT, Machinery.CULTIVATOR, 10570, 60, 8, 0);
		Machinery cultivator2 = new Machinery( getResources(), this, Machinery.CULTIVATOR2, getImage(Machinery.CULTIVATOR2), Machinery.EQUIPMENT, Machinery.CULTIVATOR, 25000, 130, 4, 0);
		
		storeView.storeItems.add( cultivator1 );
		storeView.storeItems.add( cultivator2 );
		
		//Sower
		
		Machinery seeder1 = new Machinery( getResources(), this, Machinery.SOWER1, getImage(Machinery.SOWER1), Machinery.EQUIPMENT, Machinery.SOWER, 15250, 90, 8, 120);
		Machinery seeder2 = new Machinery( getResources(), this, Machinery.SOWER2, getImage(Machinery.SOWER2), Machinery.EQUIPMENT, Machinery.SOWER, 50550, 160, 7, 275);
		
		storeView.storeItems.add( seeder1 );
		storeView.storeItems.add( seeder2 );
		
		//Sprayer

		Machinery fertilizer1 = new Machinery( getResources(), this, Machinery.SPRAYER1, getImage(Machinery.SPRAYER1), Machinery.EQUIPMENT, Machinery.SPRAYER, 10000, 60, 1, 60);
		Machinery fertilizer2 = new Machinery( getResources(), this, Machinery.SPRAYER2, getImage(Machinery.SPRAYER2), Machinery.EQUIPMENT, Machinery.SPRAYER, 22500, 65, 0, 185);
		
		storeView.storeItems.add( fertilizer1 );
		storeView.storeItems.add( fertilizer2 );
		
		//Trailer
		
		Machinery tipper1 = new Machinery( getResources(), this, Machinery.TRAILER1, getImage(Machinery.TRAILER1), Machinery.EQUIPMENT, Machinery.TRAILER, 7000, 90, 1, 0);
		tipper1.capacity = 8500;
		Machinery tipper2 = new Machinery( getResources(), this, Machinery.TRAILER2, getImage(Machinery.TRAILER2), Machinery.EQUIPMENT, Machinery.TRAILER, 15000, 120, 1, 0);
		tipper2.capacity = 21200;
		Machinery tipper3 = new Machinery( getResources(), this, Machinery.TRAILER4, getImage(Machinery.TRAILER4), Machinery.EQUIPMENT, Machinery.TRAILER, 35000, 155, 1, 0);
		tipper3.capacity = 32000;
		Machinery tipper4 = new Machinery( getResources(), this, Machinery.TRAILER3, getImage(Machinery.TRAILER3), Machinery.EQUIPMENT, Machinery.TRAILER, 52500, 170, 1, 0);
		tipper4.capacity = 55500;
	
		
		storeView.storeItems.add( tipper1 );
		storeView.storeItems.add( tipper2 );
		storeView.storeItems.add( tipper3 );
		storeView.storeItems.add( tipper4 );
		
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		float x = event.getX();
		float y = event.getY();


		if (event.getAction()==MotionEvent.ACTION_DOWN) {
			ScreenButton touchingButton = getButton( x, y );
			
			if( touchingButton != null ){
				if( touchingButton.enabled == true ){
					selectedBtn = touchingButton;
					playSound( sound_click );
					touchingButton.function.run();
				}				
			}else{
				GarageItem garageButton = getGarageItem( x, y );
				if( garageButton != null ){
					if( garageButton.button.enabled == true ){
						storeView.mouseDownX = x;
						storeView.selectedItem = garageButton;
						garageButton.button.function.run();
						if( storeView.garageItems.size() > 3 )
							storeView.touchingItem = true;
					}				
				}
			}
		}
		
		if (event.getAction()==MotionEvent.ACTION_MOVE) {
			
			if( storeView.touchingItem == true ){
				float offset = storeView.mouseDownX - x;
				float xOff =storeView.xOffset;
				if( xOff < 0 )
					xOff *= -1;
				if( xOff > 10 )
					storeView.xOffset = 0;
				
				//if( storeView.garageItems.get(0).button.x < getPercentSize(6)  && storeView.garageItems.get(storeView.garageItems.size()-1).button.x > getPercentSize(94) ){
				
					storeView.xOffset -= offset;
				//}
			}
		
		}
		
		if (event.getAction()==MotionEvent.ACTION_UP) {
			
			if( storeView.touchingItem == true ){
				for( GarageItem item : storeView.garageItems ){
					item.button.x += storeView.xOffset;
					
				}
				storeView.xOffset = 0;
			}
			
			storeView.touchingItem = false;
		}

		return super.onTouchEvent(event);
		
	}
	
	public ScreenButton getButton( float x, float y ){

		ScreenButton btn = null;
		try{
			for ( ScreenButton button : buttons){
				if(x >= button.x && x <= button.x+button.getWidth() && y >= button.y && y<= button.y+button.getHeight()){
	
					btn = button;	
				}
			}
		}catch( ConcurrentModificationException err ){}
		return btn;
	}
	
	public GarageItem getGarageItem( float x, float y ){

		GarageItem btn = null;
		try{
			for ( GarageItem item : storeView.garageItems ){
				if(x >= item.button.x && x <= item.button.x+item.button.getWidth() && y >= item.button.y && y<= item.button.y+item.button.getHeight()){
	
					btn = item;	
				}
			}
		}catch( ConcurrentModificationException err ){}
		return btn;
	}
	
	public ScreenButton getButtonByName( String name ){

		ScreenButton buttonName = buttons.get(0);

		try{
			for ( ScreenButton button : buttons ){
				if(button.name.contains(name) ){
					buttonName = button;	
	
				}
			}
		}catch( ConcurrentModificationException err ){}
		

		return buttonName;
	}

	public void save( boolean closeAfter ){
		
		saving = true;
		//TODO 
		//Save vehicles/equipment(garage) 			into "getGarage"
		//Save activites 							into "getActivites"
		//Save player stats(money,time played, etc) into "getPlayerInfo"

		//Save vehicles
		String value = "{";
		try{
		for( Machinery mchn : profile.machines ){
			
				value += "[" + mchn.toString() + "]";
			
		}
		}catch( ConcurrentModificationException err ){}
		
		value += "}";
		
		garageEditor.putString("Vehicles", value);
		garageEditor.commit();
		
		//Save Activities
		String valueA = "{";
		try{
		for( Job job : profile.jobs ){
			if(job.finished == false){
				valueA += "[" + job.toString() + "]";
			}
		}
		}catch( ConcurrentModificationException err ){}
		
		valueA += "}";
		activitesEditor.putString("Activities", valueA);
		activitesEditor.commit();

		//Save Fields
		String valueF = "{";
		try{
		for( Field fld : selectedMap.fields ){
			if( fld.owned == true ){
				valueF += "[" + fld.toString() + "]";
			}
		}
		}catch( ConcurrentModificationException err ){}
		
		valueF += "}";
		profileEditor.putString("Fields", valueF);
		profileEditor.commit();
		
		//Player stats
		profileEditor.putFloat("Money", (float)profile.money);
		profileEditor.putInt( "Coins", profile.getGold() );
		profileEditor.putInt("Machines", profile.machineryOwned);
		profileEditor.putInt(Crops.WHEAT, profile.crops.wheat);
		profileEditor.putInt(Crops.BARLEY, profile.crops.barley);
		profileEditor.putInt(Crops.CANOLA, profile.crops.canola);
	
		profileEditor.putLong( "StartTime", profile.startTime );
		profileEditor.putLong( "WorkedTime", profile.workedTime );
		profileEditor.putFloat( "WorkerCost", (float) profile.workerCost );
		profileEditor.putFloat( "FieldCost", (float) profile.fieldCost );
		profileEditor.putFloat( "VehicleCost", (float) profile.vehicleCost );
		profileEditor.putFloat( "MaintenanceCost", (float) profile.maintenanceCost );
		profileEditor.putFloat( "TotalEarned", (float) profile.totalEarned );
		profileEditor.putFloat( "MoneySpent", (float) profile.moneySpent );
		profileEditor.putBoolean( "TutorialMenu", profile.showTutorial_Menu );
		profileEditor.putBoolean( "TutorialFarm", profile.showTutorial_Farm );
		profileEditor.commit();
		
		if( closeAfter == true ){
			selectedMap.map = null;
			finish();
			System.exit( 0 );
		}

	}

	public void openMenu(){
	
		
		int b_width = getPercentSize(BUTTON_SIZE_P);
		int b_height = getPercentSize((float)BUTTON_SIZE_P/2.5f) + getPercentSize(1f);
		int bX = getPercentSize(50) - b_width/2;
		int bY = getPercentSize(30.0f);
		
		buttons.clear();
		buttons.add( new ScreenButton( buttons.size(),bX,bY,b_width,b_height,"button"+NEW_GAME,getResources(),this, btnFunctions.newGame ) );
		bY += b_height + getPercentSize(0.25f);
		ScreenButton resume =  new ScreenButton( buttons.size(),bX,bY,b_width,b_height,"button"+RESUME_GAME, getResources(),this, btnFunctions.resumeGame );

		//toast( getActivitesSettings.getAll().toString(), 7 );
		if( getActivitesSettings.getAll().toString().equals("{}") && getProfileSettings.getAll().toString().equals("{}"))
			resume.enabled = false;
		buttons.add(resume);
		bY += b_height + getPercentSize(0.25f);
		if( showOptions == true ){
			buttons.add( new ScreenButton( buttons.size(),bX,bY,b_width,b_height,"button"+OPTIONS, getResources(),this, btnFunctions.options ) );
			bY += b_height + getPercentSize(0.25f);
		}
		buttons.add( new ScreenButton( buttons.size(),bX,bY,b_width,b_height,"button"+QUIT, getResources(),this, btnFunctions.quit ) );
		
		Draw(MENU);

	}
	
	public void openMainScreen(){
		
		int b_width = getPercentSize(BUTTON_SIZE_P);
		int b_height = getPercentSize((float)BUTTON_SIZE_P/2.5f) + getPercentSize(1f);
		int bX = getPercentSize(76) - b_width/2;
		int bY = getPercentSize(18.4f);
		
		buttons.clear();
		
		if( profile.showTutorial_Menu == false ){
			openTutorial(MAIN_SCREEN);
			Draw(MAIN_SCREEN);
			return;
		}
		
		buttons.add( new ScreenButton( buttons.size(),bX,bY,b_width,b_height,"button"+FARM,getResources(),this, btnFunctions.farm ) );
		bY += b_height + getPercentSize(0.25f);
		ScreenButton store =  new ScreenButton( buttons.size(),bX,bY,b_width,b_height,"button"+STORE, getResources(),this, btnFunctions.store );
		buttons.add(store);
		bY += b_height + getPercentSize(0.25f);
		buttons.add( new ScreenButton( buttons.size(),bX,bY,b_width,b_height,"button"+GARAGE, getResources(),this, btnFunctions.garage ) );
		bY += b_height + getPercentSize(0.25f);
		buttons.add( new ScreenButton( buttons.size(),bX,bY,b_width,b_height,"button"+STATS, getResources(),this, btnFunctions.stats ) );
		bY += b_height + getPercentSize(0.25f);
		buttons.add( new ScreenButton( buttons.size(),bX,bY,b_width,b_height,"button"+QUIT, getResources(),this, btnFunctions.saveQuit ) );
		
		//Safety Net: make sure inventory doesnt go below 0
		if( profile.crops.wheat < 0 )
			profile.crops.wheat = 0;
		if( profile.crops.barley < 0 )
			profile.crops.barley = 0;
		if( profile.crops.canola < 0 )
			profile.crops.canola = 0;
		
		Draw(MAIN_SCREEN);
		toastLog.clear();
	}
	
	public void openGarage( boolean store ){
		
		int notificationHeight = getPercentSize( levelView.NOTIFICATION_BAR_H );
		
		int b_width = getPercentSize(BUTTON_SIZE_P);
		int b_height = getPercentSize((float)BUTTON_SIZE_P/2.5f) + getPercentSize(1f);
		int bX = getPercentSize(76) - b_width/2;
		int bY = getPercentSize(20.0f) + notificationHeight;
		
		drawPurchaseDialog = false;
		drawActivityDialog = false;
		drawGarageDialog = false;
		
		buttons.clear();
		storeView.garageItems.clear();

		int itemY = getPercentSize(15f) + notificationHeight/2;
		int itemX = getPercentSize(5);
		int width = getPercentSize(25);
		int height = getPercentSize(55f);
			
		if( store == false ){
				
			if( profile.machines.size() > 0 ){
				try{
					for( Machinery mchn : profile.machines ){
						
						if( mchn.subType.equals( Machinery.CATEGORIES[storeView.type]) ){
							ScreenButton btn = new ScreenButton( mchn.id, itemX, itemY, width, height, "item"+mchn.id, getResources(), this, btnFunctions.garageItemBtn );
							GarageItem item = new GarageItem( mchn, btn );
							storeView.garageItems.add( item );
							itemX += width + getPercentSize(1);
						}else if( storeView.type == 0 ){	//All
							
							ScreenButton btn = new ScreenButton( mchn.id, itemX, itemY, width, height, "item"+mchn.id, getResources(), this, btnFunctions.garageItemBtn );
							GarageItem item = new GarageItem( mchn, btn );
							storeView.garageItems.add( item );
							itemX += width + getPercentSize(1);
							
						}
					
					}
					if( storeView.garageItems.size() > 0 ){
						storeView.selectedItem = storeView.garageItems.get(0);
					}else{
						
						btnFunctions.rightCat.run();
						return;
					}
				}catch( ConcurrentModificationException err ){}
				
				int b_h = getPercentSize((float)BUTTON_SIZE_P/3f) - getPercentSize(2f);
				ScreenButton leftCat = new ScreenButton( buttons.size(),0, notificationHeight, b_width/2, b_h, "button"+"Previous", getResources(), this, btnFunctions.leftCat );
				ScreenButton rightCat = new ScreenButton( buttons.size(),screen_width - b_width/2, notificationHeight, b_width/2, b_h, "button"+"Next", getResources(), this, btnFunctions.rightCat );
				buttons.add( leftCat );
				buttons.add( rightCat );
			}else{
				storeView.selectedItem  = null;
			}
			
			int b_h = getPercentSize((float)BUTTON_SIZE_P/3f) - getPercentSize(2f);
			b_width = screen_width/3;
			
			ScreenButton back = new ScreenButton( buttons.size(),0, screen_height - b_h, b_width, b_h, "button"+BACK, getResources(), this, btnFunctions.back );
			ScreenButton storeB = new ScreenButton( buttons.size(),b_width, screen_height - b_h, b_width, b_h, "button"+STORE, getResources(), this, btnFunctions.openStore );
			ScreenButton sell = new ScreenButton( buttons.size(),screen_width - b_width, screen_height - b_h, b_width, b_h, "button"+SELL, getResources(), this, btnFunctions.sell );
			if( profile.machines.size() <= 0 ){
				sell.enabled = false;
			}
			
			
			ScreenButton refill = new ScreenButton( buttons.size(), getPercentSize(48), getPercentSize(78.0f) - b_height/2 + notificationHeight, b_width/3, b_height/2, "button"+"Refill", getResources(), this, btnFunctions.refill );
			
			buttons.add( back );
			buttons.add( storeB );
			buttons.add( sell );
			buttons.add( refill );
			
			Draw(GARAGE);
		}else{
			

			try{
				for( Machinery mchn : storeView.storeItems ){
					
					if( mchn.subType.equals( Machinery.CATEGORIES[storeView.type]) ){
						ScreenButton btn = new ScreenButton( mchn.id, itemX, itemY, width, height, "item"+mchn.id, getResources(), this, btnFunctions.garageItemBtn );
						GarageItem item = new GarageItem( mchn, btn );
						storeView.garageItems.add( item );
						itemX += width + getPercentSize(1);
					}else if( storeView.type == 0 ){	//All
						
						ScreenButton btn = new ScreenButton( mchn.id, itemX, itemY, width, height, "item"+mchn.id, getResources(), this, btnFunctions.garageItemBtn );
						GarageItem item = new GarageItem( mchn, btn );
						storeView.garageItems.add( item );
						itemX += width + getPercentSize(1);
						
					}
				
				}
				
					storeView.selectedItem = storeView.garageItems.get(0);
				
			}catch( ConcurrentModificationException err ){}
			
			int b_h = getPercentSize((float)BUTTON_SIZE_P/3f) - getPercentSize(2f);
			ScreenButton leftCat = new ScreenButton( buttons.size(),0, notificationHeight, b_width/2, b_h, "button"+"Previous", getResources(), this, btnFunctions.leftCat );
			ScreenButton rightCat = new ScreenButton( buttons.size(),screen_width - b_width/2, notificationHeight, b_width/2, b_h, "button"+"Next", getResources(), this, btnFunctions.rightCat );
			
			b_width = screen_width/3;
	
			ScreenButton back = new ScreenButton( buttons.size(),0, screen_height - b_h, b_width, b_h, "button"+BACK, getResources(), this, btnFunctions.back );
			ScreenButton garageB = new ScreenButton( buttons.size(),b_width, screen_height - b_h, b_width, b_h, "button"+GARAGE, getResources(), this, btnFunctions.openStore );
			ScreenButton buy = new ScreenButton( buttons.size(),screen_width - b_width, screen_height - b_h, b_width, b_h, "button"+BUY, getResources(), this, btnFunctions.buy );
			
			buttons.add( leftCat );
			buttons.add( rightCat );
			buttons.add( back );
			buttons.add( garageB );
			buttons.add( buy );

			Draw(GARAGE);
		}
		
	}
	
	public void openFarm(){
		
		drawPurchaseDialog = false;
		drawActivityDialog = false;
		drawGarageDialog = false;
		drawSellPointIcons = false;
		
		buttons.clear();
		if( profile.showTutorial_Farm == false ){
			openTutorial(FARM);
			Draw(FARM);
			return;
		}
		
		//Farms
		try{
			for(Field field : selectedMap.fields ){
				
				buttons.add( new ScreenButton( field.id,field.area,"field"+field.id,getResources(),this, btnFunctions.field ) );
				if(field.combinedArea != null)
					buttons.add( new ScreenButton( field.id,field.combinedArea,"field"+field.id,getResources(),this, btnFunctions.field ) );
				
			}
		}catch( ConcurrentModificationException err ){}
		
		//Sell Points
		try{
			for(SellPoint sell : selectedMap.sellPoints ){
				
				buttons.add( new ScreenButton( sell.id, sell.area,"sellPoint"+sell.id,getResources(),this, btnFunctions.sellPoint ) );
				if(sell.combinedArea != null)
					buttons.add( new ScreenButton( sell.id,sell.combinedArea,"sellPoint"+sell.id,getResources(),this, btnFunctions.sellPoint ) );
				
				if( profile.shownDemand == false ){
					if( sell.whe_demand == true )
						toast("DEMAND" + Crops.WHEAT + " is in great demand at the " + sell.name + "!", 5);
					if( sell.bar_demand == true )
						toast("DEMAND" + Crops.BARLEY + " is in great demand at the " + sell.name + "!", 5);
					if( sell.can_demand == true )
						toast("DEMAND" + Crops.CANOLA + " is in great demand at the " + sell.name + "!", 5);
				}
			}
		}catch( ConcurrentModificationException err ){}
		
		//Gold Coin
		if( selectedMap.hasGoldCoin == true ){
			ScreenButton coin = new ScreenButton( 666 , selectedMap.goldLocation.x, selectedMap.goldLocation.y, getPercentSize(View.GOLD_COIN_W), getPercentSize(View.GOLD_COIN_H), "GOLD_COIN", getResources(), this,  btnFunctions.goldCoin ); 
			buttons.add( coin );
		}

		profile.shownDemand = true;
		Draw(FARM);
	}
	
	public void openStats(){
		
		drawPurchaseDialog = false;
		drawActivityDialog = false;
		drawGarageDialog = false;
		
		buttons.clear();
		
		Draw(STATS);
	}
	
	public void purchaseFieldDialog(){
		
		//Add buttons
		int b_width = getPercentSize(25);
		int b_height = getPercentSize(16f);
		int bX = getPercentSize(25);
		int bY = getPercentSize(83f);
		
		buttons.clear();
		buttons.add( new ScreenButton( buttons.size() ,bX,bY,b_width,b_height,"button"+BACK,getResources(),this, btnFunctions.farm_back ) );
		bX += b_width;
		buttons.add( new ScreenButton( buttons.size() ,bX,bY,b_width,b_height,"button"+BUY,getResources(),this, btnFunctions.purchase_buy ) );
		
		drawPurchaseDialog = true;
	}
	
	public void activityFieldDialog( boolean busy, boolean sellPoint ){
		
		//Add buttons
		int b_width = getPercentSize(25);
		int b_height = getPercentSize(16f);
		int bX = getPercentSize(25);
		int bY = getPercentSize(83f);
		
		activityDialog.sellPoint = sellPoint;
		buttons.clear();
		buttons.add( new ScreenButton( buttons.size() ,bX,bY,b_width,b_height,"button"+BACK,getResources(),this, btnFunctions.farm_back ) );
		bX += b_width;
	
		if( busy == false ){
			if( sellPoint == false ){
				levelView.activityHeightAdj = View.ACTIVITY_HEIGHT_ADJ;
				buttons.add( new ScreenButton( buttons.size() ,bX,bY,b_width,b_height,"button"+GO,getResources(),this, btnFunctions.activity_go ) );
			}else{
				levelView.activityHeightAdj = 0;
				ScreenButton go = new ScreenButton( buttons.size() ,bX,bY,b_width,b_height,"button"+GO,getResources(),this, btnFunctions.activity_sell_go );
				go.enabled = false;
				buttons.add( go );
				ScreenButton fill = new ScreenButton( 9998 ,getPercentSize(30),getPercentSize(76f),(int)(b_width/3),(int)(b_height/2.5f),"button"+"Fill",getResources(),this, btnFunctions.activity_refill );
				if ( !activityDialog.attachment.subType.equals(Machinery.TRAILER) )
					fill.enabled = false;
					
				buttons.add( fill );
				
			}
			
			ScreenButton other = new ScreenButton( 9999 ,getPercentSize(67),getPercentSize(72f),getPercentSize(5),getPercentSize(8f),"button",getResources(),this, btnFunctions.activity_other );
			
			if( sellPoint == false ){
				buttons.add( new ScreenButton( buttons.size() ,getPercentSize(67) ,getPercentSize(42f),getPercentSize(5),getPercentSize(8f),"button",getResources(),this, btnFunctions.activity_machine ) );
				buttons.add( new ScreenButton( buttons.size() ,getPercentSize(67),getPercentSize(57f),getPercentSize(5),getPercentSize(8f),"button",getResources(),this, btnFunctions.activity_attachment ) );
				other = new ScreenButton( 9999 ,getPercentSize(67),getPercentSize(72f),getPercentSize(5),getPercentSize(8f),"button",getResources(),this, btnFunctions.activity_other );
			}else{
				buttons.add( new ScreenButton( buttons.size() ,getPercentSize(67) , getPercentSize(38f) + getPercentSize(levelView.activityHeightAdj),getPercentSize(5),getPercentSize(8f),"button",getResources(),this, btnFunctions.activity_machine ) );
				buttons.add( new ScreenButton( buttons.size() ,getPercentSize(67), getPercentSize(53f) + getPercentSize(levelView.activityHeightAdj),getPercentSize(5),getPercentSize(8f),"button",getResources(),this, btnFunctions.activity_attachment ) );
				other = new ScreenButton( 9999 ,getPercentSize(67), getPercentSize(68f) + getPercentSize(levelView.activityHeightAdj),getPercentSize(5),getPercentSize(8f),"button",getResources(),this, btnFunctions.activity_other );
				
			}
			
			if( sellPoint == false ){
				if( !activityDialog.attachment.subType.equals( Machinery.SOWER) )
					other.enabled = false;
				buttons.add( other );
			}else{
				if( !activityDialog.attachment.subType.equals( Machinery.TRAILER) )
					other.enabled = false;
				buttons.add( other );
			}
		}else{
			
			ScreenButton skip = new ScreenButton( buttons.size() ,bX + b_width/4 ,bY- b_height,b_width/2,b_height/2,"button"+" x1",getResources(),this, btnFunctions.skip_job );
			
			if( profile.getGold() <= 0 )
				skip.enabled = false;
			
			buttons.add( skip );
			buttons.add( new ScreenButton( buttons.size() ,bX,bY,b_width,b_height,"button"+CANCEL,getResources(),this, btnFunctions.activity_cancel ) );
		}
		
		drawActivityDialog = true;
	}
	
	public void activityGarageDialog( String type, int page ){
		
		garageDialogPage = page;
		
		List<Machinery> items = new ArrayList<Machinery>();
		
			try{
				if (type.equals(Machinery.VEHICLE)){
					
					for( Machinery mchn : profile.machines ){
						if(mchn.type.equals(Machinery.VEHICLE)){
							if( mchn.inUse == false ){
								items.add(mchn);
							}
						}
					}
				}else if(type.equals(Machinery.EQUIPMENT)){
					
					for( Machinery mchn : profile.machines ){
						if(mchn.type.equals(Machinery.EQUIPMENT))
							if( mchn.inUse == false ){
								items.add(mchn);
							}
					}
				}else{
					
					items.add(wheatItem);
					items.add(barleyItem);
					items.add(canolaItem);
				}
			}catch( ConcurrentModificationException err ){}
		
		
		activityDialog.listItems = items;
		//Add buttons
		int b_width = getPercentSize(40);
		int b_height = getPercentSize(16f);
		int bX = getPercentSize(10);
		int bY = getPercentSize(83f);
		
		buttons.clear();
		ScreenButton prev = new ScreenButton( buttons.size() ,bX,bY,b_width,b_height,"button"+"Previous",getResources(),this, btnFunctions.activity_garage_prev );
		prev.enabled = false;
		buttons.add( prev );
		bX += b_width;
		ScreenButton next = new ScreenButton( buttons.size() ,bX,bY,b_width,b_height,"button"+"Next",getResources(),this, btnFunctions.activity_garage_next );
		next.enabled = false;
		if( activityDialog.listItems.size() > View.LIST_ITEM_AMOUNT ){
			next.enabled = true;
		}
		buttons.add( next );
		
		for(int i = 1; i <= View.LIST_ITEM_AMOUNT; i++){
			
			buttons.add( new ScreenButton( i+View.LIST_ITEM_AMOUNT*garageDialogPage ,getPercentSize(10),getPercentSize((float)(18+20*(i-1))),getPercentSize(80),getPercentSize(20f),"button"+"item",getResources(),this, btnFunctions.activity_garage_item ) );
		}

		drawGarageDialog = true;
	}
	
	public void openNewGame( ){
		
		buttons.clear();
		
		int b_width = getPercentSize(BUTTON_SIZE_P)/2;
		int b_height = getPercentSize((float)BUTTON_SIZE_P/2.5f) + getPercentSize(1f);
		int bX = getPercentSize(20);
		int bY = getPercentSize(55f);
		
		ScreenButton no = new ScreenButton( buttons.size() ,bX,bY,b_width,b_height,"button"+"No",getResources(),this, btnFunctions.newGameCancel );
		bX += getPercentSize(60) - b_width;
		ScreenButton yes = new ScreenButton( buttons.size() ,bX,bY,b_width,b_height,"button"+"Yes",getResources(),this, btnFunctions.newGameConfirm );
		
		buttons.add( no );
		buttons.add( yes );
		
		newGameConfirm = true;
	}
	
	public void openTutorial( String screen ){
		
		int b_width = getPercentSize(BUTTON_SIZE_P)/2;
		int b_height = getPercentSize((float)BUTTON_SIZE_P/3f) - getPercentSize(1f);
		int bX = getPercentSize(50) - b_width/2;
		int bY = getPercentSize(93f);
		
		toastLog.clear();
		buttons.clear();
		tutorial.clear();
		
		if( screen.equals(MAIN_SCREEN) ){
			
			buttons.add( new ScreenButton( buttons.size() , bX, bY - b_height/2, b_width, b_height,"button"+"Got it!", getResources(), this, btnFunctions.tutorial_okay ) );
			tutorial.addDescription("ORANGE" + "BOLD" + "Welcome to your farm!" , new Point( getPercentSize(10), getPercentSize(5f)), false );
			tutorial.addDescription("This area will display" , new Point( getPercentSize(30), getPercentSize(15f)), true );
			tutorial.addDescription("your crop inventory" , new Point( getPercentSize(30), getPercentSize(20f)), true );
			tutorial.addDescription("and your current activities." , new Point( getPercentSize(30), getPercentSize(25f)), true );
			tutorial.addDescription("This is your main headquaters" , new Point( getPercentSize(76), getPercentSize(35f)), true );
			tutorial.addDescription("for managing your farm!" , new Point( getPercentSize(76), getPercentSize(40f)), true );
			tutorial.addDescription("From here you can keep track" , new Point( getPercentSize(76), getPercentSize(46f)), true );
			tutorial.addDescription("of all your jobs and vehicles." , new Point( getPercentSize(76), getPercentSize(51f)), true );
			tutorial.addDescription("You can also buy new vehicles" , new Point( getPercentSize(76), getPercentSize(57f)), true );
			tutorial.addDescription("from the Store." , new Point( getPercentSize(76), getPercentSize(62f)), true );
			tutorial.addDescription("Click Farm to get started!" , new Point( getPercentSize(76), getPercentSize(68f)), true );
			tutorial.tutorialScreen = screen;
		}else if( screen.equals(FARM) ){
			
			buttons.add( new ScreenButton( buttons.size() , bX, bY - b_height/2, b_width, b_height,"button"+"Got it!", getResources(), this, btnFunctions.tutorial_okay ) );
			tutorial.addDescription("GOLD" + "BOLD" + "Starting your farm..." , new Point( getPercentSize(10), getPercentSize(5f)), false );
			tutorial.addDescription("BLUE" + "To start off the fields we need" , new Point( getPercentSize(50), getPercentSize(15f)), true );
			tutorial.addDescription("BLUE" + "to cultivate them." , new Point( getPercentSize(50), getPercentSize(20f)), true );
			tutorial.addDescription("ORANGE" + "Next we will need to sow the fields" , new Point( getPercentSize(50), getPercentSize(26f)), true );
			tutorial.addDescription("ORANGE" + "with seeds and let them grow." , new Point( getPercentSize(50), getPercentSize(31f)), true );
			tutorial.addDescription("ORANGE" + "Feritlizing your fields is optional" , new Point( getPercentSize(50), getPercentSize(36f)), true );
			tutorial.addDescription("ORANGE" + "but it will double the yield of your harvest." , new Point( getPercentSize(50), getPercentSize(41f)), true );
			tutorial.addDescription("GREEN" + "The final stage of farming" , new Point( getPercentSize(50), getPercentSize(47f)), true );
			tutorial.addDescription("GREEN" + "is to harvest your crop." , new Point( getPercentSize(50), getPercentSize(52f)), true );
			tutorial.addDescription("GREEN" + "Harvesting collects all the usable crop." , new Point( getPercentSize(50), getPercentSize(57f)), true );
			tutorial.addDescription("Once we have the crops we can" , new Point( getPercentSize(50), getPercentSize(64f)), true );
			tutorial.addDescription("sell it at any of the four" , new Point( getPercentSize(50), getPercentSize(69f)), true );
			tutorial.addDescription("selling areas using a trailer." , new Point( getPercentSize(50), getPercentSize(74f)), true );
			tutorial.addDescription("GOLD" + "Owned fields will be highlighted with its current state:" , new Point( getPercentSize(50), getPercentSize(80f)), true );
			tutorial.addDescription("Nothing done" , new Point( getPercentSize(11), getPercentSize(90f)), true );
			tutorial.addDescription("BLUE" + "Culitivated" , new Point( getPercentSize(30), getPercentSize(90f)), true );
			tutorial.addDescription("ORANGE" + "Sowed" , new Point( getPercentSize(69), getPercentSize(90f)), true );
			tutorial.addDescription("GREEN" + "Fertilized" , new Point( getPercentSize(89), getPercentSize(90f)), true );
	
			tutorial.tutorialScreen = screen;
		}

		tutorial.tutorialOpen = true;
	}
	
	public void openOptions(){
		
		buttons.clear();
		int b_width = getPercentSize(5);
		int b_height = getPercentSize(5);

		ScreenButton sound = new ScreenButton( buttons.size() , getPercentSize(Options.SOUND_LOC_BTN.x), getPercentSize((float)Options.SOUND_LOC_BTN.y), b_width, b_height,"button"+"Sound", getResources(), this, btnFunctions.options_sound );
		sound.draw = false;
		
		ScreenButton notif = new ScreenButton( buttons.size() , getPercentSize(Options.NOTIF_LOC_BTN.x), getPercentSize((float)Options.NOTIF_LOC_BTN.y), b_width, b_height,"button"+"Notifications", getResources(), this, btnFunctions.options_notif );
		notif.draw = false;
		notif.enabled = false;
		
		ScreenButton save = new ScreenButton( buttons.size() , getPercentSize(Options.SAVE_LOC_BTN.x), getPercentSize((float)Options.SAVE_LOC_BTN.y), b_width, b_height,"button"+"Auto-Save", getResources(), this, btnFunctions.options_save );
		save.draw = false;
		
		buttons.add( sound );
		buttons.add( notif );
		buttons.add( save );
		
		b_width = getPercentSize(BUTTON_SIZE_P)/2;
		b_height = getPercentSize((float)BUTTON_SIZE_P/3f) - getPercentSize(1f);
		ScreenButton okay = new ScreenButton( buttons.size() , getPercentSize(50) - b_width/2, getPercentSize(80f) - b_height/2, b_width, b_height,"button"+"Okay", getResources(), this, btnFunctions.options_okay );
		
		buttons.add( okay );
		
		Draw(OPTIONS);
	}
	
	public void Draw(String screen){

		this.screen = screen;
		levelView.loaded = true;
		levelView.running = true;
	}
	
	public void playSound( MediaPlayer sound ){
		
		if( enableSound == true )
			sound.start();
	}
	
	public void toast( String message, int seconds ){

		if( toastLog.size() > 0 ){
			if( !toastLog.get(toastLog.size()-1).equals(message) ){
				for( int i = 0; i < seconds; i++ ){
					toastLog.add( message );
				}
			}
		}else{
			for( int i = 0; i < seconds; i++ ){
				toastLog.add( message );
			}
		}
	}

	public Field getFieldByID( int id ){
		
		Field result = null;
		try{
			for( Field field : selectedMap.fields ){
				if( field.id == id )
					result = field;
			}
		}catch( ConcurrentModificationException err ){}
		return result;
	}
	
	public SellPoint getSellPointByID( int id ){
		
		SellPoint result = null;
		try{
			for( SellPoint sell : selectedMap.sellPoints ){
				if( sell.id == id )
					result = sell;
			}
		}catch( ConcurrentModificationException err ){}
		return result;
	}
	
	public ScreenButton getButtonByID( int id ){
		ScreenButton result = null;
		try{
			//result = buttons.get(0);
			try{
				for( ScreenButton btn : buttons ){
					if( btn.id == id )
						result = btn;
				}
			}catch( ConcurrentModificationException err ){}
		}catch( IndexOutOfBoundsException err ){}
		
		return result;
	}
	
	public String moneyToString( double money ){
		String result = "";
		result = NumberFormat.getCurrencyInstance().format(money);
		return result;
	}
	
	public String toDecimal( double dec ){
		DecimalFormat df = new DecimalFormat("#0.00");
		String result = df.format(dec);
		return result;
	}
	
	public String toThousands( double dec ){
		DecimalFormat df = new DecimalFormat("###,###");
		String result = df.format(dec);
		return result;
	}
	
	public IntSize getPercentSize( float percentWidth, float percentHeight ){	//x = width, y = height

		IntSize size = new IntSize();
		if( percentWidth > 100 ){
			percentWidth = 100;
		}
		if( percentHeight > 100 ){
			percentHeight = 100;
		}
		size.width = screen_width / (100f/percentWidth);		//width
		size.height = screen_height / (100f/percentHeight);	//height

		return size;
	}

	public int getPercentSize( int percentSize ){	// screen width

		double size = screen_width;
		
		if( percentSize > 100){
			percentSize = 100;
		}
		
		float percent = (100f/percentSize);
		size = (size/percent);
		
		return (int) size;
	}

	public int getPercentSize( float percentSizeH ){	// screen height

		int size = 0;
		if( percentSizeH > 100){
			percentSizeH = 100;
		}

		size = (int) (screen_height / (100f/percentSizeH));


		return size;
	}


}

class ActivityDialog{
	
	List<Machinery> listItems = new ArrayList<Machinery>();
	Machinery machine;
	Machinery attachment;
	String other;
	String clicked = Machinery.VEHICLE;
	boolean sellPoint = false;
	
	public ActivityDialog(){
		
	}
	
	public ActivityDialog( Machinery vehicle, Machinery equipment, String seed ){
	
		this.machine = vehicle;
		this.attachment = equipment;
		this.other = seed;
	}
	
}

class Store{
	
	ArrayList<Machinery> storeItems = new ArrayList<Machinery>();
	ArrayList<GarageItem> garageItems = new ArrayList<GarageItem>();
	GarageItem selectedItem;
	float mouseDownX = 0;
	float xOffset = 0;
	boolean touchingItem = false;
	int type = 0;
	boolean inStore = false;
	
	public Store(){
		
	}
	
}

class Tutorial{
	
	ArrayList<String> descriptions = new ArrayList<String>();	//Supports color(ie "ORANGE"+"Welcome to your farm!")
	ArrayList<Point> d_locations = new ArrayList<Point>();

	String tutorialScreen = Engine.MAIN_SCREEN;
	boolean tutorialOpen = false;
	
	Engine engine;
	
	public Tutorial( Engine engine ){
		
		this.engine = engine;
	}
	
	public void addDescription( String description, Point location, boolean center ){
		
		String desc = description;
		if( desc.startsWith("GOLD") ){
			desc = desc.replace("GOLD", "");
		}if( desc.startsWith("ORANGE") ){
			desc = desc.replace("ORANGE", "");
		}if( desc.startsWith("GREEN") ){
			desc = desc.replace("GREEN", "");
		}if( desc.startsWith("BLUE") ){
			desc = desc.replace("BLUE", "");
		}
		
		this.descriptions.add( description );
		
		if( center == true )
			location.x -= engine.levelView.whiteBG.measureText( desc )/2;
		
		this.d_locations.add( location );
	}
	
	public void clear(){
		
		this.descriptions.clear();
		this.d_locations.clear();
	}
	
}
