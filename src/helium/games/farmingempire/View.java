package helium.games.farmingempire;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.MaskFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class View extends SurfaceView implements Runnable {

	public static final String APP_NAME = "Farm Manager";
	public static final int PERCENT_BAR_W = 10;
	public static final float PERCENT_BAR_H = 2;
	public static final float NOTIFICATION_BAR_H = 6;
	public static final float SUN_LIGHT_H = 60;
	
	public static final String GOLD_COIN = "Gold Coin";
	public static final int GOLD_COIN_W = 3;
	public static final float GOLD_COIN_H = 4;
	
	public static final int LIST_ITEM_AMOUNT = 3;
	
	public static final int ACTIVITY_HEIGHT_ADJ = 8;

	public static final int ALPHA_BLACK = Color.argb( 175, 24, 23, 21 );
	public static final int DARK_BLACK = Color.rgb( 24, 23, 21 );
	public static final int LIGHT_WHITE = Color.argb( 175, 188, 182, 181 );
	public static final int LIGHT_GRAY = Color.rgb( 145, 144, 144 );
	public static final int DARK_GREEN = Color.rgb( 118, 164, 89 );
	public static final int ORANGE = Color.rgb( 255, 165, 0 );
	public static final int DARK_ORANGE = Color.rgb( 255, 140, 0 );
	public static final int GOLD = Color.rgb( 255, 215, 0 );

	//HUD
	public static final Point loadingLoc = new Point( 5, 90 );
	public static final IntSize loadingSize = new IntSize( 7, 7 );
	public static final Point HUD_MONEY = new Point( 80, 5 );
	public static final Point SUN_LOC = new Point( 80, 20 );
	//
	SurfaceHolder surfaceHolder;
	Thread levelThread = null;
	public Engine level;
	private Handler handler;

	public int canvasH = 400;
	public int canvasW = 800;

	public boolean loaded = false;
	public boolean running = false;
	
	Matrix m = new Matrix();//Matrix for flipping images

	float loadingAngle = 0;
	float sunAngle = 0;
	
	Bitmap sunLight;
	Bitmap menuBG;
	Bitmap optionsBG;
	Bitmap mainScreenBG;
	Bitmap storeBG;
	Bitmap comboBoxArrow;
	Bitmap checkMark;
	Bitmap goldCoin;
	Bitmap goldCoinSmall;
	
	//icons
	Bitmap wheatIcon;
	Bitmap barleyIcon;
	Bitmap canolaIcon;
	
	Bitmap sellPointIcon;
	Bitmap sellPointDIcon;
	
	IntSize cbArrow;
	IntSize checkMarkSize;
	IntSize goldCoinSize;
	IntSize goldCoinSmallSize;
	IntSize iconSize;
	
	public float activityHeightAdj = ACTIVITY_HEIGHT_ADJ;
	
	Paint blackBG = new Paint();
	Paint blackAlphaBG = new Paint();
	Paint whiteBG = new Paint();
	Paint whiteSmallBG = new Paint();
	Paint whiteBigBG = new Paint();
	Paint whiteAlphaBG = new Paint();
	Paint blueBG = new Paint();
	Paint redBG = new Paint();
	Paint demandTxt = new Paint();
	Paint purchaseTxt = new Paint();
	Paint lightText = new Paint();
	Paint lightTextLarge = new Paint();
	Paint blackText = new Paint();
	Paint blackSmallText = new Paint();
	Paint blackBigText = new Paint();
	Paint greenText = new Paint();
	Paint rectPaint = new Paint();
	Paint rectFieldPaint = new Paint();
	Paint header = new Paint();
	Paint btnFG = new Paint();
	Paint btnBG = new Paint();
	
	
	Point sun = new Point(0,0);

	int fps = 0;
	int lastfps = 0;
	Handler hd;
	boolean runfps = true;

	DecimalFormat f = new DecimalFormat("0.00");

	public View( Context context, int swidth, int sheight, Engine lv ) {
		super(context);
		level = lv;
		surfaceHolder =  getHolder();
		handler = new Handler();

		hd = new Handler();
		sunLight = BitmapFactory.decodeResource(getResources(), R.drawable.sunlight);
		sunLight  = Bitmap.createScaledBitmap(sunLight, level.getPercentSize((int)SUN_LIGHT_H/2) , level.getPercentSize(SUN_LIGHT_H), true);
		menuBG = BitmapFactory.decodeResource(getResources(), R.drawable.bgsky2);
		menuBG  = Bitmap.createScaledBitmap(menuBG,swidth , sheight, true);
		mainScreenBG = BitmapFactory.decodeResource(getResources(), R.drawable.menubg2v2);
		mainScreenBG  = Bitmap.createScaledBitmap(mainScreenBG,swidth , sheight, true);
		optionsBG = BitmapFactory.decodeResource(getResources(), R.drawable.options);
		optionsBG  = Bitmap.createScaledBitmap(optionsBG,swidth , sheight, true);
		cbArrow = new IntSize(level.getPercentSize(5),level.getPercentSize(8f));
		comboBoxArrow = BitmapFactory.decodeResource(getResources(), R.drawable.arrow);
		comboBoxArrow  = Bitmap.createScaledBitmap(comboBoxArrow, (int)cbArrow.width , (int)cbArrow.height, true);
		checkMarkSize = new IntSize(level.getPercentSize(5),level.getPercentSize(5));
		checkMark = BitmapFactory.decodeResource(getResources(), R.drawable.checkmark2);
		checkMark  = Bitmap.createScaledBitmap(checkMark, (int)checkMarkSize.width , (int)checkMarkSize.height, true);
		goldCoinSize = new IntSize(level.getPercentSize(GOLD_COIN_W), level.getPercentSize(GOLD_COIN_H));
		goldCoin = BitmapFactory.decodeResource(getResources(), R.drawable.goldcoin);
		goldCoin  = Bitmap.createScaledBitmap(goldCoin, (int)goldCoinSize.width , (int)goldCoinSize.height, true);
		goldCoinSmallSize = new IntSize(level.getPercentSize(GOLD_COIN_W-1), level.getPercentSize(GOLD_COIN_H-1));
		goldCoinSmall = BitmapFactory.decodeResource(getResources(), R.drawable.goldcoin);
		goldCoinSmall  = Bitmap.createScaledBitmap(goldCoinSmall, (int)goldCoinSmallSize.width , (int)goldCoinSmallSize.height, true);
		
		//icons
		iconSize = new IntSize(level.getPercentSize(5),level.getPercentSize(8f));
		wheatIcon = BitmapFactory.decodeResource(getResources(), R.drawable.wheaticon);
		wheatIcon  = Bitmap.createScaledBitmap(wheatIcon, (int)iconSize.width , (int)iconSize.height,true);
		barleyIcon = BitmapFactory.decodeResource(getResources(), R.drawable.barleyicon);
		barleyIcon  = Bitmap.createScaledBitmap(barleyIcon, (int)iconSize.width , (int)iconSize.height,true);
		canolaIcon = BitmapFactory.decodeResource(getResources(), R.drawable.canolaicon);
		canolaIcon  = Bitmap.createScaledBitmap(canolaIcon, (int)iconSize.width , (int)iconSize.height,true);
		sellPointIcon = BitmapFactory.decodeResource(getResources(), R.drawable.sellpoint);
		sellPointIcon  = Bitmap.createScaledBitmap(sellPointIcon, (int)iconSize.width , (int)iconSize.height,true);
		sellPointDIcon = BitmapFactory.decodeResource(getResources(), R.drawable.sellpointdemand);
		sellPointDIcon  = Bitmap.createScaledBitmap(sellPointDIcon, (int)iconSize.width , (int)iconSize.height,true);
		
		setupPaints();
		
		//Flipped image
		//m.preScale(-1.0f, 1.0f);
		//player_bm = Bitmap.createBitmap(level.player.animation.images[level.player.animation.currentImage],0,0, level.player.size, level.player.size, m, false);

	}

	private void setupPaints(){

		header.setColor(Color.WHITE);
		header.setTextSize(level.getPercentSize(10));
		header.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
		header.setAntiAlias( true );
		
		lightTextLarge.setColor(LIGHT_GRAY);
		lightTextLarge.setTextSize(level.getPercentSize(8f));
		lightTextLarge.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.NORMAL));
		lightTextLarge.setAntiAlias( true );
		
		lightText.setColor(LIGHT_GRAY);
		lightText.setTextSize(level.getPercentSize(5f));
		lightText.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
		lightText.setAntiAlias( true );
		
		blackText.setColor(Color.BLACK);
		blackText.setTextSize(level.getPercentSize(5f));
		blackText.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
		blackText.setAntiAlias( true );
		
		blackSmallText.setColor(Color.BLACK);
		blackSmallText.setTextSize(level.getPercentSize(3f));
		blackSmallText.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
		blackSmallText.setAntiAlias( true );
		
		blackBigText.setColor(Color.BLACK);
		blackBigText.setTextSize(level.getPercentSize(8f));
		blackBigText.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
		blackBigText.setAntiAlias( true );
		
		greenText.setColor(DARK_GREEN);
		greenText.setTextSize(level.getPercentSize(5f));
		greenText.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
		greenText.setAntiAlias( true );
		
		rectPaint.setColor(Color.WHITE);
		rectPaint.setTextSize(level.getPercentSize(5f));
		rectPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
		rectPaint.setStyle(Paint.Style.STROKE);
		rectPaint.setAntiAlias( true );
		
		rectFieldPaint.setColor(Color.WHITE);
		rectFieldPaint.setTextSize(level.getPercentSize(5f));
		rectFieldPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
		rectFieldPaint.setStyle(Paint.Style.STROKE);
		rectFieldPaint.setAntiAlias( true );
		
		btnFG.setColor(Color.WHITE);
		btnFG.setTextSize(level.getPercentSize(5f));
		btnFG.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
		btnFG.setAntiAlias( true );
		
		btnBG.setColor( DARK_BLACK );
		btnBG.setTextSize(level.getPercentSize(5f));
		btnBG.setStyle(Paint.Style.FILL);
		
		blackBG = new Paint();
		blackBG.setColor(Color.BLACK);
		blackBG.setTextSize(level.getPercentSize(5.0f));
		blackBG.setStyle(Paint.Style.FILL);
		blackBG.setStrokeWidth(1);
		
		blackAlphaBG = new Paint();
		blackAlphaBG.setColor( DARK_BLACK );
		blackAlphaBG.setTextSize(level.getPercentSize(5.0f));
		blackAlphaBG.setStyle(Paint.Style.FILL);
		blackAlphaBG.setAlpha(150);
		
		whiteBG = new Paint();
		whiteBG.setColor(Color.WHITE);
		whiteBG.setTextSize(level.getPercentSize(5.0f));
		whiteBG.setStyle(Paint.Style.FILL);
		whiteBG.setStrokeWidth(1);

		whiteSmallBG = new Paint();
		whiteSmallBG.setColor(Color.WHITE);
		whiteSmallBG.setTextSize(level.getPercentSize(3.5f));
		whiteSmallBG.setStyle(Paint.Style.FILL);
		whiteSmallBG.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
		whiteSmallBG.setAntiAlias( true );
		whiteSmallBG.setStrokeWidth(1);
		
		whiteBigBG = new Paint();
		whiteBigBG.setColor(Color.WHITE);
		whiteBigBG.setTextSize(level.getPercentSize(8.0f));
		whiteBigBG.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
		whiteBigBG.setStyle(Paint.Style.FILL);
		whiteBigBG.setAntiAlias( true );
		
		whiteAlphaBG = new Paint();
		whiteAlphaBG.setColor(Color.WHITE);
		whiteAlphaBG.setTextSize(level.getPercentSize(5.0f));
		whiteAlphaBG.setStyle(Paint.Style.FILL);
		whiteAlphaBG.setAlpha(175);
		whiteAlphaBG.setStrokeWidth(1);
		
		blueBG = new Paint();
		blueBG.setColor(Color.CYAN);
		blueBG.setTextSize(level.getPercentSize(6.0f));
		blueBG.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
		blueBG.setAntiAlias( true );
		blueBG.setStyle(Paint.Style.FILL);
		blueBG.setStrokeWidth(1);
		
		redBG = new Paint();
		redBG.setColor(Color.RED);
		redBG.setTextSize(level.getPercentSize(5.0f));
		redBG.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
		redBG.setAntiAlias( true );
		redBG.setStyle(Paint.Style.FILL);
		redBG.setStrokeWidth(1);
		
		demandTxt = new Paint();
		demandTxt.setColor( DARK_GREEN );
		demandTxt.setTextSize(level.getPercentSize(5.0f));
		demandTxt.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
		demandTxt.setAntiAlias( true );
		demandTxt.setStyle(Paint.Style.FILL);
		
		purchaseTxt = new Paint();
		purchaseTxt.setColor( GOLD );
		purchaseTxt.setTextSize(level.getPercentSize(5.0f));
		purchaseTxt.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
		purchaseTxt.setAntiAlias( true );
		purchaseTxt.setStyle(Paint.Style.FILL);
		
	}
	
	private Runnable clearfps = new Runnable() {  
		public void run() {  
			lastfps = fps;
			fps=0;
			runfps=true;
		}
	};




	@Override
	public void run() {
		while(running){
			if(surfaceHolder.getSurface().isValid()){
				if(level.canDraw==true){
					Canvas canvas = surfaceHolder.lockCanvas();

					if(level.screen == level.MENU){
						DrawMenu(canvas);
					}else if(level.screen == level.OPTIONS){
						DrawOptions(canvas);
					}else if(level.screen == level.MAIN_SCREEN){
						DrawMainScreen(canvas);
					}else if(level.screen == level.FARM){
						DrawFarm(canvas);
					}else if(level.screen == level.STORE){
						DrawStore(canvas);
					}else if(level.screen == level.GARAGE){
						DrawGarage(canvas);
					}else if(level.screen == level.STATS){
						DrawStats(canvas);
					}else{
						//ERROR
						toast("Error loading screen");
						running = false;
					}
					
					fps++;
					if(runfps){
						runfps=false;
						hd.postDelayed(clearfps, 1000);
					}
					
					//FPS
					if( level.drawFPS == true ){
						String fpsString = "fps: " + lastfps;
						IntSize fpsLoc = level.getPercentSize(75,12);
						canvas.drawText(fpsString.toString(), fpsLoc.width, fpsLoc.height, whiteAlphaBG);
					}
					
					//Notification
					if( level.toastLog.size() > 0 ){
						try{
							if( level.toastLog.get(0).contains( "DEMAND" ) ){
								String text = level.toastLog.get(0);
								text = text.replace("DEMAND", "");
								/*text = text.toUpperCase();*/
								IntSize loc = level.getPercentSize(1, 10);
								canvas.drawText(text, loc.width, loc.height - demandTxt.getTextSize(), demandTxt);
							}else if( level.toastLog.get(0).contains( "PURCHASE" ) ){
								String text = level.toastLog.get(0);
								text = text.replace("PURCHASE", "");
								/*text = text.toUpperCase();*/
								IntSize loc = level.getPercentSize(1, 10);
								canvas.drawText(text, loc.width, loc.height - demandTxt.getTextSize(), purchaseTxt);
							}else{
								IntSize loc = level.getPercentSize(50, 10);
								canvas.drawText(level.toastLog.get(0), loc.width - whiteBG.measureText(level.toastLog.get(0))/2, loc.height - whiteBG.getTextSize(), whiteBG);
							}
						}catch( IndexOutOfBoundsException e ){

						}

					}
					//Tutorials
					if( level.tutorial.tutorialOpen == true )
						drawTutorial( canvas );
					
					if( level.loading == true )
						drawLoading( canvas );

					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
		}
	}


	public void LevelView_OnResume() {

		
		surfaceHolder = getHolder();
		running = true;
		levelThread = new Thread(this);
		levelThread.start();
		//canvasW = surfaceHolder.lockCanvas().getWidth();
		//canvasH = surfaceHolder.lockCanvas().getHeight();
		if(loaded==true){
			levelThread = new Thread();
			levelThread.start();
		}



	}

	public void LevelView_OnPause() {
		boolean retry = true;
		running = false;
		while(retry){
			try {
				levelThread.join();
				retry = false;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public void DrawMenu(Canvas canvas){


		canvas.drawBitmap(menuBG,0,0, null);

		canvas.drawText( APP_NAME,level.getPercentSize(50) - header.measureText(APP_NAME)/2, canvasH/5, header);	//Center Text: - header.measureText("String")
		
		drawSunlight( canvas );
		
		if( level.newGameConfirm == true ){
			drawNewGame( canvas );
		}
		
		try{
			for(ScreenButton btns : level.buttons){
				canvas.drawRect(btns.x, btns.y, btns.x + btns.width, btns.y + btns.height, btnBG);
				
				String btnText = btns.name;
				btnText = btnText.replace( "button","" );
				if( btns.enabled == true )
					canvas.drawText(btnText,btns.x + btns.width/2 - btnFG.measureText(btnText)/2, btns.y+btns.height/2 + btnFG.getTextSize()/2, btnFG); //Middle height: btns.y+btns.height/2 + btnfont.getTextSize()/2
				else
					canvas.drawText(btnText,btns.x + btns.width/2 - btnFG.measureText(btnText)/2, btns.y+btns.height/2 + btnFG.getTextSize()/2, lightText); //Middle height: btns.y+btns.height/2 + btnfont.getTextSize()/2
				
			}
		}catch( ConcurrentModificationException e ){

		}
	}

	public void DrawMainScreen( Canvas canvas ){

		canvas.drawBitmap(mainScreenBG,0,0, null);
		
		int iX = level.getPercentSize(7);
		int iY = level.getPercentSize(20f);
		
		if( level.profile.jobs.size() > 9 )
			canvas.drawRect( level.getPercentSize(4), level.getPercentSize(7f), level.getPercentSize(56), canvasH, whiteBG);
		else
			canvas.drawRect( level.getPercentSize(4), level.getPercentSize(7f), level.getPercentSize(56), level.getPercentSize(93f), whiteBG);
		
		canvas.drawText(Engine.INVENTORY + ":", level.getPercentSize(5), level.getPercentSize(15f), lightTextLarge);
		for( int i = 0; i < Crops.crops.length; i++ ){
				canvas.drawText(Crops.crops[i] + ":", iX, iY, lightText);
				if( Crops.crops[i].equals( Crops.WHEAT ) )
					canvas.drawText(level.toThousands(level.profile.crops.wheat), iX + level.getPercentSize(11), iY, lightText);
				else if( Crops.crops[i].equals( Crops.BARLEY ) )
					canvas.drawText(level.toThousands(level.profile.crops.barley), iX + level.getPercentSize(11), iY, lightText);
				else if( Crops.crops[i].equals( Crops.CANOLA ) )
					canvas.drawText(level.toThousands(level.profile.crops.canola), iX + level.getPercentSize(11), iY, lightText);
				
			iY += level.getPercentSize(5f);
		}
		
		canvas.drawText(Engine.ACTIVITES + ":", level.getPercentSize(5), level.getPercentSize(40f), lightTextLarge);
		int jobY = level.getPercentSize(45f);
		try{
			for( Job job : level.profile.jobs ){
				if( job.finished == false ){
					String jobText = job.getInfo();
					try{
						jobText = jobText.substring(0, 35);
					}catch( StringIndexOutOfBoundsException err ){}
					
					canvas.drawText(jobText, level.getPercentSize(7), jobY, lightText);
					jobY += level.getPercentSize(6f);
				}
			}
		}catch( ConcurrentModificationException err ){}
		try{
			for(ScreenButton btns : level.buttons){
				canvas.drawRect(btns.x, btns.y, btns.x + btns.width, btns.y + btns.height, btnBG);
				
				String btnText = btns.name;
				btnText = btnText.replace( "button","" );
				if( btns.enabled == true )
					canvas.drawText(btnText,btns.x + btns.width/2 - btnFG.measureText(btnText)/2, btns.y+btns.height/2 + btnFG.getTextSize()/2, btnFG); //Middle height: btns.y+btns.height/2 + btnfont.getTextSize()/2
				else
					canvas.drawText(btnText,btns.x + btns.width/2 - btnFG.measureText(btnText)/2, btns.y+btns.height/2 + btnFG.getTextSize()/2, lightText); //Middle height: btns.y+btns.height/2 + btnfont.getTextSize()/2
				
			}
		}catch( ConcurrentModificationException e ){}
		
		double money = level.profile.money;
		String moneyText = level.moneyToString(money);
		if( money > 0 )
			canvas.drawText(moneyText, level.getPercentSize(HUD_MONEY.x) - whiteBG.measureText(moneyText)/2, level.getPercentSize((float)HUD_MONEY.y), whiteBG);
		else
			canvas.drawText(moneyText, level.getPercentSize(HUD_MONEY.x) - whiteBG.measureText(moneyText)/2, level.getPercentSize((float)HUD_MONEY.y), redBG);
		
	}
	
	public void DrawFarm(Canvas canvas){


		//Draw background
		canvas.drawBitmap( level.selectedMap.map,0,0, null );
		//



		//Draw field buttons

		if(level.buttons.isEmpty() == false){
			try{
				for(Field btns : level.selectedMap.fields){
					if(level.selectedMap.drawFieldRects == true){
						if( btns.combinedArea != null){
							Path path = new Path();
							path.moveTo(btns.x, btns.y);
							path.lineTo(btns.x + btns.width, btns.y);
							path.lineTo(btns.x + btns.width, btns.y + btns.height);
							path.lineTo(btns.combinedArea.right, btns.y + btns.height);
							path.lineTo(btns.combinedArea.right,btns.combinedArea.bottom);
							path.lineTo(btns.combinedArea.left,btns.combinedArea.bottom);
							path.close();
							
							//canvas.drawPath(path, rectPaint);
							if( btns.job_cultivated == true )
								rectFieldPaint.setColor( Color.BLUE );
							if( btns.job_sowed == true )
								rectFieldPaint.setColor( DARK_ORANGE );
							if( btns.job_fertilized == true )
								rectFieldPaint.setColor( DARK_GREEN );
							
							if( btns.owned == true ){
								if( btns.busy == true )
									rectFieldPaint.setColor( Color.RED );
								canvas.drawPath(path, rectFieldPaint);
							}
							rectFieldPaint.setColor( Color.WHITE );
						}else{
							
								//canvas.drawRect(btns.area, rectPaint);
							if( btns.job_cultivated == true )
								rectFieldPaint.setColor( Color.CYAN );
							if( btns.job_sowed == true )
								rectFieldPaint.setColor( DARK_ORANGE );
							if( btns.job_fertilized == true )
								rectFieldPaint.setColor( DARK_GREEN );
							
							if( btns.owned == true ){
								if( btns.busy == true )
									rectFieldPaint.setColor( Color.RED );
								canvas.drawRect(btns.area, rectFieldPaint);
							}
							rectFieldPaint.setColor( Color.WHITE );
						}
					}
					String btnText = btns.id+"";
					Paint textP = whiteBG;
					
					if( btns.owned == true)
						textP = greenText;
					
					canvas.drawText(btnText, btns.x + btns.width/2 - level.getPercentSize(1), btns.y+btns.height/2 + textP.getTextSize()/2, textP); //Middle height: btns.y+btns.height/2 + btnfont.getTextSize()/2
					
					//float textWidth = textP.measureText(btnText)/2;//FCKN compiler is messing up here!
					//canvas.drawText(btnText, btns.x + btns.width/2 - textWidth, btns.y+btns.height/2 + textP.getTextSize()/2, textP); //Middle height: btns.y+btns.height/2 + btnfont.getTextSize()/2

				}
			}catch( IndexOutOfBoundsException e ){
				Log.e("Error", e.getMessage());
			}
		}

		//Draw sell point icons
		try{
		for ( SellPoint sp : level.selectedMap.sellPoints ){
			
			if( level.drawSellPointIcons == true ){
				
				if( sp.inDemand == false )
					canvas.drawBitmap( sellPointIcon, sp.area.exactCenterX() - iconSize.width/2, sp.area.exactCenterY() - iconSize.height/2, whiteAlphaBG );
				else
					canvas.drawBitmap( sellPointDIcon, sp.area.exactCenterX() - iconSize.width/2, sp.area.exactCenterY() - iconSize.height/2, whiteAlphaBG );
				
			}
		}
		} catch( ConcurrentModificationException er ){}
		
		//Gold coin
		if( level.selectedMap.hasGoldCoin == true )
			canvas.drawBitmap( goldCoinSmall, level.selectedMap.goldLocation.x, level.selectedMap.goldLocation.y, null);

		
		drawNotificationBar( canvas, true );
		drawCoinBox( canvas );
	
		if( level.drawPurchaseDialog == true )
			DrawFieldDialog ( canvas );
		else if( level.drawActivityDialog == true ){
			
			if( level.drawGarageDialog == true )
				DrawGarageDialog( canvas );
			else
				DrawActivityDialog( canvas );
		}
	}

	public void DrawFieldDialog( Canvas canvas ){
		//Black box
		canvas.drawRect( level.getPercentSize(25), level.getPercentSize(4f), level.getPercentSize(75), level.getPercentSize(18f), btnBG);
		canvas.drawText("BUY", level.getPercentSize(50) - whiteBigBG.measureText("BUY")/2, (int)level.getPercentSize(4f) + whiteBigBG.getTextSize()*1.5f, whiteBigBG);
		//White box
		canvas.drawRect( level.getPercentSize(25), level.getPercentSize(18f), level.getPercentSize(75), level.getPercentSize(83f), whiteAlphaBG);
		//Info
		canvas.drawText("Field No. " + level.selectedField.id, level.getPercentSize(30), (int)level.getPercentSize(35f), blackBigText);
		canvas.drawText("Area:", level.getPercentSize(30), (int)level.getPercentSize(50f), blackText);
		canvas.drawText("Nominal Value:", level.getPercentSize(30), (int)level.getPercentSize(60f), blackText);
		String area = level.toDecimal(level.selectedField.hectare) + " ha";
		canvas.drawText(area, level.getPercentSize(55), (int)level.getPercentSize(50f), lightText);
		canvas.drawText(level.moneyToString(level.selectedField.cost), level.getPercentSize(55), (int)level.getPercentSize(60f), lightText);
		//Buttons
		canvas.drawRect( level.getPercentSize(25), level.getPercentSize(83f), level.getPercentSize(75), level.getPercentSize(99f), btnBG);
		try{
			for(ScreenButton btns : level.buttons){
				canvas.drawRect(btns.x, btns.y, btns.x + btns.width, btns.y + btns.height, btnBG);
				
				String btnText = btns.name;
				btnText = btnText.replace( "button","" );
				if( btns.enabled == true )
					canvas.drawText(btnText,btns.x + btns.width/2 - btnFG.measureText(btnText)/2, btns.y+btns.height/2 + btnFG.getTextSize()/2, btnFG); //Middle height: btns.y+btns.height/2 + btnfont.getTextSize()/2
				else
					canvas.drawText(btnText,btns.x + btns.width/2 - btnFG.measureText(btnText)/2, btns.y+btns.height/2 + btnFG.getTextSize()/2, lightText); //Middle height: btns.y+btns.height/2 + btnfont.getTextSize()/2
				}
		}catch( ConcurrentModificationException e ){}

	}
	
	public void DrawActivityDialog( Canvas canvas ){
		
		
		//Black box
		canvas.drawRect( level.getPercentSize(25), level.getPercentSize(activityHeightAdj/2), level.getPercentSize(75), level.getPercentSize(14f) + level.getPercentSize(activityHeightAdj/2), btnBG);
		canvas.drawText("ACTIVITY", level.getPercentSize(50) - whiteBigBG.measureText("ACTIVITY")/2, (int)level.getPercentSize(activityHeightAdj/2) + whiteBigBG.getTextSize()*1.5f, whiteBigBG);
		//White box
		canvas.drawRect( level.getPercentSize(25), level.getPercentSize(14f) + level.getPercentSize(activityHeightAdj/2), level.getPercentSize(75), level.getPercentSize(83f), whiteAlphaBG);
		//Black box
		//canvas.drawRect( level.getPercentSize(25), level.getPercentSize(79f), level.getPercentSize(75), level.getPercentSize(95f), btnBG);
		
		if( level.selectingField == true )
			DrawActivityDialogField( canvas );
		else
			DrawActivityDialogSellPoint( canvas );
		
	}
	
	public void DrawActivityDialogField( Canvas canvas ){

		if( level.selectedField.busy == false ){
			//Info
			String field = "Field No. " + level.selectedField.id;
			canvas.drawText(field, level.getPercentSize(30), (int)level.getPercentSize(26f) + level.getPercentSize(activityHeightAdj), blackBigText);
			
			canvas.drawText("Machine:", level.getPercentSize(30), (int)level.getPercentSize(36f) + level.getPercentSize(activityHeightAdj), blackText);
			canvas.drawText("Attachment:", level.getPercentSize(30), (int)level.getPercentSize(51f) + level.getPercentSize(activityHeightAdj), blackText);
			canvas.drawText("Other:", level.getPercentSize(30), (int)level.getPercentSize(66f) + level.getPercentSize(activityHeightAdj), blackText);
			//ComboBoxs
			canvas.drawRect( level.getPercentSize(40), level.getPercentSize(38f) + level.getPercentSize(activityHeightAdj), level.getPercentSize(72), level.getPercentSize(46f) + level.getPercentSize(activityHeightAdj), whiteBG);
			canvas.drawRect( level.getPercentSize(40), level.getPercentSize(53f) + level.getPercentSize(activityHeightAdj), level.getPercentSize(72), level.getPercentSize(61f) + level.getPercentSize(activityHeightAdj), whiteBG);
			try{
				if( level.getButtonByID( 9999 ).enabled == true ){
					canvas.drawRect( level.getPercentSize(40), level.getPercentSize(68f) + level.getPercentSize(activityHeightAdj), level.getPercentSize(72), level.getPercentSize(76f) + level.getPercentSize(activityHeightAdj), whiteBG);
				}else{
					canvas.drawRect( level.getPercentSize(40), level.getPercentSize(68f) + level.getPercentSize(activityHeightAdj), level.getPercentSize(72), level.getPercentSize(76f) + level.getPercentSize(activityHeightAdj), whiteAlphaBG);
					
				}
			}catch( NullPointerException err ){}
			//Text
			
			String area = level.toDecimal(level.selectedField.hectare) + " ha";
			canvas.drawText(area, level.getPercentSize(34) + blackBigText.measureText(field), (int)level.getPercentSize(25.7f) + blackBigText.getTextSize() - (lightText.getTextSize()*1.5f) + level.getPercentSize(activityHeightAdj), lightText);
			
			
			String machine = level.activityDialog.machine.subType + ": " + level.activityDialog.machine.name;
			canvas.drawText(machine, level.getPercentSize(41), (int)level.getPercentSize(44f) + level.getPercentSize(activityHeightAdj), lightText);
			String attachment = level.activityDialog.attachment.subType + ": " + level.activityDialog.attachment.name;
			canvas.drawText(attachment, level.getPercentSize(41), (int)level.getPercentSize(59f) + level.getPercentSize(activityHeightAdj), lightText);
			String other = "Seed: " + level.activityDialog.other;
			try{
				if( level.getButtonByID( 9999 ).enabled == true ){
					canvas.drawText(other, level.getPercentSize(41), (int)level.getPercentSize(74f) + level.getPercentSize(activityHeightAdj), lightText);
					if( level.activityDialog.other.equals(Crops.WHEAT))
						canvas.drawBitmap( wheatIcon,  level.getPercentSize(41) + lightText.measureText(other), (int)level.getPercentSize(72f) + level.getPercentSize(activityHeightAdj) - iconSize.height/2, null);
					else if( level.activityDialog.other.equals(Crops.BARLEY))
						canvas.drawBitmap( barleyIcon,  level.getPercentSize(41) + lightText.measureText(other), (int)level.getPercentSize(72f) + level.getPercentSize(activityHeightAdj) - iconSize.height/2, null);
					else if( level.activityDialog.other.equals(Crops.CANOLA))
						canvas.drawBitmap( canolaIcon,  level.getPercentSize(41) + lightText.measureText(other), (int)level.getPercentSize(72f) + level.getPercentSize(activityHeightAdj) - iconSize.height/2, null);
					
				}
			}catch( NullPointerException err ){}
			//Arrows
			canvas.drawRect( level.getPercentSize(67), level.getPercentSize(38f) + level.getPercentSize(activityHeightAdj), level.getPercentSize(72), level.getPercentSize(46f) + level.getPercentSize(activityHeightAdj), whiteBG);
			canvas.drawBitmap(comboBoxArrow, level.getPercentSize(67), level.getPercentSize(38f) + level.getPercentSize(activityHeightAdj), null);
			canvas.drawRect( level.getPercentSize(67), level.getPercentSize(53f) + level.getPercentSize(activityHeightAdj), level.getPercentSize(72), level.getPercentSize(61f) + level.getPercentSize(activityHeightAdj), whiteBG);
			canvas.drawBitmap(comboBoxArrow, level.getPercentSize(67), level.getPercentSize(53f) + level.getPercentSize(activityHeightAdj), null);
			try{
				if( level.getButtonByID( 9999 ).enabled == true ){
					canvas.drawRect( level.getPercentSize(67), level.getPercentSize(68f) + level.getPercentSize(activityHeightAdj), level.getPercentSize(72), level.getPercentSize(76f) + level.getPercentSize(activityHeightAdj), whiteBG);
				}else{
					canvas.drawRect( level.getPercentSize(67), level.getPercentSize(68f) + level.getPercentSize(activityHeightAdj), level.getPercentSize(72), level.getPercentSize(76f) + level.getPercentSize(activityHeightAdj), whiteAlphaBG);
				}
			}catch( NullPointerException err ){}
			canvas.drawBitmap(comboBoxArrow, level.getPercentSize(67), level.getPercentSize(68f) + level.getPercentSize(activityHeightAdj), null);
			
		}else{
			//Info 
			String field = "Field No. " + level.selectedField.id;
			canvas.drawText(field, level.getPercentSize(30), (int)level.getPercentSize(30f), blackBigText);
			String area = level.toDecimal(level.selectedField.hectare) + " ha";
			canvas.drawText(area, level.getPercentSize(35) + blackBigText.measureText(field), (int)level.getPercentSize(29.7f) + blackBigText.getTextSize() - (lightText.getTextSize()*1.5f), lightText);
			String info = level.profile.getJobByField( level.selectedField.id ).jobType + " with " + level.profile.getMachineByID(level.profile.getJobByField( level.selectedField.id ).vehicleID).name;
			canvas.drawText(info, level.getPercentSize(30), (int)level.getPercentSize(40f), blackText);
			if( level.selectedField.job_sowed == true )
				canvas.drawText( "Growing " + level.selectedField.seed, level.getPercentSize(30), (int)level.getPercentSize(45f) , blackText);
			canvas.drawText("Time left: ", level.getPercentSize(30), (int)level.getPercentSize(55f), blackText);
			String time = level.profile.getJobByField( level.selectedField.id ).timeLeftLong();
			canvas.drawText(time, level.getPercentSize(41), (int)level.getPercentSize(63f), lightText);
			
		}
		
		//Buttons
		try{
			for(ScreenButton btns : level.buttons){
				if( !btns.name.equals("button") )
					canvas.drawRect(btns.x, btns.y, btns.x + btns.width, btns.y + btns.height, btnBG);
				
				String btnText = btns.name;
				btnText = btnText.replace( "button","" );
				if( btns.enabled == true )
					canvas.drawText(btnText,btns.x + btns.width/2 - btnFG.measureText(btnText)/2, btns.y+btns.height/2 + btnFG.getTextSize()/2, btnFG); //Middle height: btns.y+btns.height/2 + btnfont.getTextSize()/2
				else
					canvas.drawText(btnText,btns.x + btns.width/2 - btnFG.measureText(btnText)/2, btns.y+btns.height/2 + btnFG.getTextSize()/2, lightText); //Middle height: btns.y+btns.height/2 + btnfont.getTextSize()/2
				}
		}catch( ConcurrentModificationException e ){}
		
		//Skip
		if( level.selectedField.busy == true ){
			int b_width = level.getPercentSize(25);
			int b_height = level.getPercentSize(16f);
			int bX = level.getPercentSize(25);
			int bY = level.getPercentSize(83f);
			bX*=2;
			Rect bounds = new Rect();
			lightText.getTextBounds("Skip", 0, 4, bounds);
			canvas.drawText("Skip", bX + b_width/4 - bounds.width() - level.getPercentSize(1), bY - b_height + bounds.height(), lightText);
			canvas.drawBitmap( goldCoin, bX + b_width/4 + level.getPercentSize(GOLD_COIN_W)/2, bY - b_height + level.getPercentSize(GOLD_COIN_H)/2, null);
		}
	}
	
	public void DrawActivityDialogSellPoint( Canvas canvas ){

		
			//Info
			String field = level.selectedSellPoint.name;
			canvas.drawText(field, level.getPercentSize(30), (int)level.getPercentSize(26f) + level.getPercentSize(activityHeightAdj), blackBigText);
			
			
			String wheat = "$" + level.toThousands( level.selectedSellPoint.whe_price );
			String barley = "$" + level.toThousands( level.selectedSellPoint.bar_price );
			String canola = "$" + level.toThousands( level.selectedSellPoint.can_price );
			
			float cropX = level.getPercentSize(75) - blackSmallText.measureText(canola);
			
			//Crop icons
			canvas.drawText( canola, cropX, level.getPercentSize(26f) + level.getPercentSize(activityHeightAdj) + blackSmallText.getTextSize()*2, blackSmallText);
			cropX -= iconSize.width;//(blackSmallText.measureText(canola) + level.getPercentSize(1f) );
			canvas.drawBitmap( canolaIcon, cropX, level.getPercentSize(26f) + level.getPercentSize(activityHeightAdj), null);
			cropX -= blackSmallText.measureText(barley);//iconSize.width - level.getPercentSize(1);
			canvas.drawText( barley, cropX, level.getPercentSize(26f) + level.getPercentSize(activityHeightAdj) + blackSmallText.getTextSize()*2, blackSmallText);
			cropX -= iconSize.width;//blackSmallText.measureText(barley);
			canvas.drawBitmap( barleyIcon, cropX, level.getPercentSize(26f) + level.getPercentSize(activityHeightAdj), null);
			cropX -= blackSmallText.measureText(wheat);//iconSize.width - level.getPercentSize(1);
			canvas.drawText( wheat, cropX, level.getPercentSize(26f) + level.getPercentSize(activityHeightAdj) + blackSmallText.getTextSize()*2, blackSmallText);
			cropX -= iconSize.width;//blackSmallText.measureText(wheat);
			canvas.drawBitmap( wheatIcon, cropX, level.getPercentSize(26f) + level.getPercentSize(activityHeightAdj), null);
			cropX -= iconSize.width;
		
			canvas.drawText("Machine:", level.getPercentSize(30), (int)level.getPercentSize(36f) + level.getPercentSize(activityHeightAdj), blackText);
			canvas.drawText("Attachment:", level.getPercentSize(30), (int)level.getPercentSize(51f) + level.getPercentSize(activityHeightAdj), blackText);
			canvas.drawText("Other:", level.getPercentSize(30), (int)level.getPercentSize(66f) + level.getPercentSize(activityHeightAdj), blackText);
			//ComboBoxs
			canvas.drawRect( level.getPercentSize(40), level.getPercentSize(38f) + level.getPercentSize(activityHeightAdj), level.getPercentSize(72), level.getPercentSize(46f) + level.getPercentSize(activityHeightAdj), whiteBG);
			canvas.drawRect( level.getPercentSize(40), level.getPercentSize(53f) + level.getPercentSize(activityHeightAdj), level.getPercentSize(72), level.getPercentSize(61f) + level.getPercentSize(activityHeightAdj), whiteBG);
			try{
				if( level.getButtonByID( 9999 ).enabled == true ){
					canvas.drawRect( level.getPercentSize(40), level.getPercentSize(68f) + level.getPercentSize(activityHeightAdj), level.getPercentSize(72), level.getPercentSize(76f) + level.getPercentSize(activityHeightAdj), whiteBG);
				}else{
					canvas.drawRect( level.getPercentSize(40), level.getPercentSize(68f) + level.getPercentSize(activityHeightAdj), level.getPercentSize(72), level.getPercentSize(76f) + level.getPercentSize(activityHeightAdj), whiteAlphaBG);
					
				}
			}catch( NullPointerException err ){}
			
			//Text
			String machine = level.activityDialog.machine.subType + ": " + level.activityDialog.machine.name;
			canvas.drawText(machine, level.getPercentSize(41), (int)level.getPercentSize(44f) + level.getPercentSize(activityHeightAdj), lightText);
			String attachment = level.activityDialog.attachment.subType + ": " + level.activityDialog.attachment.name;
			canvas.drawText(attachment, level.getPercentSize(41), (int)level.getPercentSize(59f) + level.getPercentSize(activityHeightAdj), lightText);
			String other = "Seed: " + level.activityDialog.other;
			try{
				if( level.getButtonByID( 9999 ).enabled == true ){
					canvas.drawText(other, level.getPercentSize(41), (int)level.getPercentSize(74f) + level.getPercentSize(activityHeightAdj), lightText);
					if( level.activityDialog.other.equals(Crops.WHEAT))
						canvas.drawBitmap( wheatIcon,  level.getPercentSize(41) + lightText.measureText(other), (int)level.getPercentSize(72f) + level.getPercentSize(activityHeightAdj) - iconSize.height/2, null);
					else if( level.activityDialog.other.equals(Crops.BARLEY))
						canvas.drawBitmap( barleyIcon,  level.getPercentSize(41) + lightText.measureText(other), (int)level.getPercentSize(72f) + level.getPercentSize(activityHeightAdj) - iconSize.height/2, null);
					else if( level.activityDialog.other.equals(Crops.CANOLA))
						canvas.drawBitmap( canolaIcon,  level.getPercentSize(41) + lightText.measureText(other), (int)level.getPercentSize(72f) + level.getPercentSize(activityHeightAdj) - iconSize.height/2, null);
					
				}
			}catch( NullPointerException err ){}
			//Arrows
			canvas.drawRect( level.getPercentSize(67), level.getPercentSize(38f) + level.getPercentSize(activityHeightAdj), level.getPercentSize(72), level.getPercentSize(46f) + level.getPercentSize(activityHeightAdj), whiteBG);
			canvas.drawBitmap(comboBoxArrow, level.getPercentSize(67), level.getPercentSize(38f) + level.getPercentSize(activityHeightAdj), null);
			canvas.drawRect( level.getPercentSize(67), level.getPercentSize(53f) + level.getPercentSize(activityHeightAdj), level.getPercentSize(72), level.getPercentSize(61f) + level.getPercentSize(activityHeightAdj), whiteBG);
			canvas.drawBitmap(comboBoxArrow, level.getPercentSize(67), level.getPercentSize(53f) + level.getPercentSize(activityHeightAdj), null);
			try{
				if( level.getButtonByID( 9999 ).enabled == true ){
					canvas.drawRect( level.getPercentSize(67), level.getPercentSize(68f) + level.getPercentSize(activityHeightAdj), level.getPercentSize(72), level.getPercentSize(76f) + level.getPercentSize(activityHeightAdj), whiteBG);
				}else{
					canvas.drawRect( level.getPercentSize(67), level.getPercentSize(68f) + level.getPercentSize(activityHeightAdj), level.getPercentSize(72), level.getPercentSize(76f) + level.getPercentSize(activityHeightAdj), whiteAlphaBG);
				}
			}catch( NullPointerException err ){}
			canvas.drawBitmap(comboBoxArrow, level.getPercentSize(67), level.getPercentSize(68f) + level.getPercentSize(activityHeightAdj), null);
		
			if( level.activityDialog.sellPoint == true ){
				if ( level.activityDialog.attachment.subType.equals(Machinery.TRAILER) ){
					drawProgressBar( canvas, level.getPercentSize(40), level.getPercentSize(80f), level.getPercentSize(14), level.getPercentSize(3f), (float)level.activityDialog.attachment.capacity, (int)level.activityDialog.attachment.fuel );
					canvas.drawText( (int)level.activityDialog.attachment.fuel + "/" + (int)level.activityDialog.attachment.capacity, level.getPercentSize(55), (int)level.getPercentSize(81f), lightText);
				}
			}
			
			try{
				//Check if trailer is full
				if( level.activityDialog.attachment.fuel >= level.activityDialog.attachment.capacity ){
					level.getButtonByID( 9998 ).name = "Fill";
					level.getButtonByID( 9998 ).enabled = false;
					level.getButtonByName(Engine.GO).enabled = true;
				}
				//Check if enough seed in inventory;
				if( level.activityDialog.other.equals( Crops.WHEAT ) ){
					if( level.profile.crops.checkInventory( Crops.WHEAT, (int)level.activityDialog.attachment.fuel ) == false ){
						level.activityDialog.attachment.stopFillingTrailer( level );
						level.getButtonByID( 9998 ).name = "Fill";
						level.getButtonByID( 9998 ).enabled = false;
						level.getButtonByName(Engine.GO).enabled = true;
					}
				}
				
				if( level.activityDialog.other.equals( Crops.BARLEY ) ){
					if( level.profile.crops.checkInventory( Crops.BARLEY, (int)level.activityDialog.attachment.fuel ) == false ){
						level.activityDialog.attachment.stopFillingTrailer( level );
						level.getButtonByID( 9998 ).name = "Fill";
						level.getButtonByID( 9998 ).enabled = false;
						level.getButtonByName(Engine.GO).enabled = true;
					}
				}
				
				if( level.activityDialog.other.equals( Crops.CANOLA ) ){
					if( level.profile.crops.checkInventory( Crops.CANOLA, (int)level.activityDialog.attachment.fuel ) == false ){
						level.activityDialog.attachment.stopFillingTrailer( level );
						level.getButtonByID( 9998 ).name = "Fill";
						level.getButtonByID( 9998 ).enabled = false;
						level.getButtonByName(Engine.GO).enabled = true;
					}
				}
				
			}catch( NullPointerException err ){}
	
		//Buttons
		try{
			for(ScreenButton btns : level.buttons){
				if( !btns.name.equals("button") )
					canvas.drawRect(btns.x, btns.y, btns.x + btns.width, btns.y + btns.height, btnBG);
				
				String btnText = btns.name;
				btnText = btnText.replace( "button","" );
				if( btns.enabled == true )
					canvas.drawText(btnText,btns.x + btns.width/2 - btnFG.measureText(btnText)/2, btns.y+btns.height/2 + btnFG.getTextSize()/2, btnFG); //Middle height: btns.y+btns.height/2 + btnfont.getTextSize()/2
				else
					canvas.drawText(btnText,btns.x + btns.width/2 - btnFG.measureText(btnText)/2, btns.y+btns.height/2 + btnFG.getTextSize()/2, lightText); //Middle height: btns.y+btns.height/2 + btnfont.getTextSize()/2
				}
		}catch( ConcurrentModificationException e ){}
		
	}

	public void DrawGarageDialog( Canvas canvas ){
		//Black box
		canvas.drawRect( level.getPercentSize(10), level.getPercentSize(4f), level.getPercentSize(90), level.getPercentSize(18f), btnBG);
		canvas.drawText("Machinery", level.getPercentSize(50) - whiteBigBG.measureText("Machinery")/2, (int)level.getPercentSize(4f) + whiteBigBG.getTextSize()*1.5f, whiteBigBG);
		//White box
		canvas.drawRect( level.getPercentSize(10), level.getPercentSize(18f), level.getPercentSize(90), level.getPercentSize(83f), whiteAlphaBG);
		//Machines
		int yLoc = (int)level.getPercentSize(30f);
		for( int i = level.garageDialogPage*LIST_ITEM_AMOUNT; i < LIST_ITEM_AMOUNT + level.garageDialogPage*LIST_ITEM_AMOUNT; i++ ){
			if( level.activityDialog.listItems.size() > i ){
				Bitmap image = level.activityDialog.listItems.get(i).image;
				image = Bitmap.createScaledBitmap(image, (int)level.getPercentSize(12) , (int)level.getPercentSize(20f),true);
				
				canvas.drawBitmap( image, level.getPercentSize(11), yLoc - level.getPercentSize(12f), null);
				canvas.drawText( level.activityDialog.listItems.get(i).subType, level.getPercentSize(27), yLoc, blackText);
				canvas.drawText( level.activityDialog.listItems.get(i).name, level.getPercentSize(42), yLoc, blackText);
				if( !level.activityDialog.listItems.get(i).type.equals( Crops.SEED ) ){
					canvas.drawText( (int)level.activityDialog.listItems.get(i).horsePower+" hp", level.getPercentSize(63), yLoc, blackText);
					drawProgressBar( canvas, level.getPercentSize(78), yLoc , level.getPercentSize(PERCENT_BAR_W),  level.getPercentSize(PERCENT_BAR_H), (float)level.activityDialog.listItems.get(i).maxFuel, (int)level.activityDialog.listItems.get(i).fuel);
				}
				//canvas.drawText( level.activityDialog.listItems.get(i).fuel +"", level.getPercentSize(77), yLoc, blackText);
				yLoc += (int)level.getPercentSize(20f);
			}
		}

		//Buttons
		canvas.drawRect( level.getPercentSize(10), level.getPercentSize(83f), level.getPercentSize(90), level.getPercentSize(99f), btnBG);
		try{
			for(ScreenButton btns : level.buttons){
				if(btns.name.contains("item")){
					canvas.drawRect(btns.x, btns.y, btns.x + btns.width, btns.y + btns.height, rectPaint);

				}else{
					canvas.drawRect(btns.x, btns.y, btns.x + btns.width, btns.y + btns.height, btnBG);
					
					String btnText = btns.name;
					btnText = btnText.replace( "button","" );
					if( btns.enabled == true )
						canvas.drawText(btnText,btns.x + btns.width/2 - btnFG.measureText(btnText)/2, btns.y+btns.height/2 + btnFG.getTextSize()/2, btnFG); //Middle height: btns.y+btns.height/2 + btnfont.getTextSize()/2
					else
						canvas.drawText(btnText,btns.x + btns.width/2 - btnFG.measureText(btnText)/2, btns.y+btns.height/2 + btnFG.getTextSize()/2, lightText); //Middle height: btns.y+btns.height/2 + btnfont.getTextSize()/2
						
				}
			}
		}catch( ConcurrentModificationException e ){}

	}
	
	public void DrawStore(Canvas canvas){
/*
		IntSize imageLoc = level.getPercentSize(20,6);
		IntSize nameLoc = level.getPercentSize(35,5);
		IntSize descLoc = level.getPercentSize(64,65);
		IntSize amountLoc = level.getPercentSize(75,5);
		IntSize itemLoc = level.getPercentSize(25,10);
		IntSize ingredLoc = level.getPercentSize(66,10);
		IntSize weapLoc = level.getPercentSize(2,3);
		IntSize buildLoc = level.getPercentSize(2,36);
		IntSize miscLoc = level.getPercentSize(2,69);


		Paint p = new Paint();

		level.craft_bg  = Bitmap.createScaledBitmap(level.craft_bg,canvas.getWidth() , canvas.getHeight(),true);
		canvas.drawBitmap(level.craft_bg,0,0, null);

		canvas.drawBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.buttonbigpress), level.getPercentSize(14),level.getPercentSize(15),true),weapLoc.width,weapLoc.height, null);
		canvas.drawBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pickaxe), level.getPercentSize(7),level.getPercentSize(7),true),weapLoc.width + level.getPercentSize(7)/2,weapLoc.height + level.getPercentSize(8)/2, null);
		canvas.drawBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.buttonbig), level.getPercentSize(14),level.getPercentSize(15),true),buildLoc.width,buildLoc.height, null);
		canvas.drawBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.woodwall), level.getPercentSize(7),level.getPercentSize(7),true),buildLoc.width + level.getPercentSize(7)/2,buildLoc.height + level.getPercentSize(8)/2, null);
		canvas.drawBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.buttonbig), level.getPercentSize(14),level.getPercentSize(15),true),miscLoc.width,miscLoc.height, null);
		canvas.drawBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.crafttable), level.getPercentSize(7),level.getPercentSize(7),true),miscLoc.width + level.getPercentSize(7)/2,miscLoc.height + level.getPercentSize(8)/2, null);



		int x = level.buttons.get(0).x;
		int y = level.buttons.get(0).y;
		int width = level.buttons.get(0).width;
		int height = level.buttons.get(0).height;
		canvas.drawBitmap(Bitmap.createScaledBitmap(buttonbg, width, height, true),x,y, null);
		Paint craft = new Paint();
		craft.setTextSize(level.getPercentSize(10.0f));
		Typeface tf = Typeface.create("Arial", Typeface.BOLD);
		craft.setTypeface(tf);
		craft.setColor( Color.WHITE );

		canvas.drawText(Engine.CRAFT, x + width/2 - paint.measureText(Engine.CRAFT), y + height - height/4, craft);

		ArrayList<String> descriptions = new ArrayList<String>();



		if(level.selectedCraftable != null){
			String desc = level.selectedCraftable.description;
			for( int i = 0; i < desc.length(); i++){
				if( i  % 25 == 0){
					if(desc.length() > i + 25){
						descriptions.add(desc.substring(i, i + 25));
					}else{
						descriptions.add(desc.substring(i));

					}
				}
			}
			for( String desline : descriptions ){
				canvas.drawText(desline, descLoc.width, descLoc.height + paint.getTextSize()/4 + descriptions.indexOf(desline) * level.getPercentSize(5.0f), paint);
			}
		}
		*/
	}

	public void DrawGarage(Canvas canvas){

		
		int notificationHeight = level.getPercentSize( NOTIFICATION_BAR_H );
		int startX = 0;
		
		Point horsePower = new Point(level.getPercentSize(5),level.getPercentSize(75.0f) + notificationHeight);
		Point fuel = new Point(level.getPercentSize(31),level.getPercentSize(75.0f) + notificationHeight);
		Point cost = new Point(level.getPercentSize(55),level.getPercentSize(75.0f) + notificationHeight);
		Point own = new Point(level.getPercentSize(75),level.getPercentSize(75.0f) + notificationHeight);
		Point cap = new Point(level.getPercentSize(75),level.getPercentSize(75.0f) + notificationHeight);
		Point description = new Point(level.getPercentSize(5),level.getPercentSize(83.0f) + notificationHeight);
		GarageItem selected = null;
		if( level.storeView.selectedItem != null ){
			selected = level.storeView.selectedItem;
		}
		
		canvas.drawBitmap(mainScreenBG,0,0, null);
		blackBG.setAlpha(175);
		btnBG.setAlpha(175);
		//Black box
		canvas.drawRect( startX, 0, canvasW, level.getPercentSize(10f) + notificationHeight, blackBG);
		String category = Machinery.CATEGORIES[level.storeView.type];
		if( !category.equals(Machinery.ALL) ){
			category += "s";
		}
		canvas.drawText( category, level.getPercentSize(50) - whiteBigBG.measureText(category)/2, whiteBigBG.getTextSize() + notificationHeight, whiteBigBG);
		//Black box
		canvas.drawRect( startX, level.getPercentSize(10f) + notificationHeight, canvasW, level.getPercentSize(70f) + notificationHeight, btnBG);
		if( selected != null ){
			//Machines
			try{
				for( GarageItem item : level.storeView.garageItems ){
					
					canvas.drawRect( item.button.x + level.storeView.xOffset, item.button.y + notificationHeight/2, item.button.x + item.button.width  + level.storeView.xOffset, item.button.y + item.button.height + notificationHeight/2, whiteAlphaBG);
					
					if(item.equals(selected)){
						Paint paint = new Paint();
						paint.setColor(Color.WHITE);
						paint.setAlpha(75);
						canvas.drawRect( item.button.x + level.storeView.xOffset, item.button.y + notificationHeight/2, item.button.x + item.button.width  + level.storeView.xOffset, item.button.y + item.button.height + notificationHeight/2, paint);
					}
					canvas.drawBitmap( item.machine.image, item.button.x + level.getPercentSize(4) + level.storeView.xOffset, item.button.y + notificationHeight/2, null);
					//Gray box, if selected turn blue
					if(item.equals(selected))
						canvas.drawRect( item.button.x + level.storeView.xOffset, item.button.y + item.button.height - level.getPercentSize(20f) + notificationHeight/2, item.button.x + item.button.width + level.storeView.xOffset, item.button.y + item.button.height - level.getPercentSize(10f) + notificationHeight/2, blueBG);
					else
						canvas.drawRect( item.button.x + level.storeView.xOffset, item.button.y + item.button.height - level.getPercentSize(20f) + notificationHeight/2, item.button.x + item.button.width + level.storeView.xOffset, item.button.y + item.button.height - level.getPercentSize(10f) + notificationHeight/2, lightText);
					canvas.drawText( item.machine.subType, item.button.x + item.button.width/2 - whiteBG.measureText(item.machine.subType)/2 + level.storeView.xOffset,  item.button.y + item.button.height - level.getPercentSize(10f) - whiteBG.getTextSize()/2 + notificationHeight/2, whiteBG);
					//White box
					canvas.drawRect( item.button.x + level.storeView.xOffset, item.button.y + item.button.height - level.getPercentSize(10f) + notificationHeight/2, item.button.x + item.button.width + level.storeView.xOffset, item.button.y + item.button.height + notificationHeight/2, whiteBG);
					canvas.drawText( item.machine.name, item.button.x + item.button.width/2 - lightText.measureText(item.machine.name)/2 + level.storeView.xOffset, item.button.y + item.button.height - lightText.getTextSize()/2 + notificationHeight/2, lightText);
					
					//canvas.drawRect(item.button.area, blueBG);
				}
			}catch( ConcurrentModificationException err ){}
			//Description area
			canvas.drawRect( 0, level.getPercentSize(70f) + notificationHeight, canvasW, canvasH, btnBG);
			canvas.drawLine(startX, level.getPercentSize(78f) + notificationHeight, canvasW, level.getPercentSize(78f) + notificationHeight, whiteAlphaBG);
			//canvas.drawText( (int)item.machine.horsePower+" hp", item.button.x, itemY, blackText);
			//drawProgressBar( canvas, item.button.x, itemY , level.getPercentSize(PERCENT_BAR_W),  level.getPercentSize(PERCENT_BAR_H), (float)item.machine.maxFuel, (int)item.machine.fuel);
	
			int b_height = level.getPercentSize((float)Engine.BUTTON_SIZE_P/2.5f);
			canvas.drawRect( 0, canvasH - b_height + notificationHeight, canvasW, canvasH, blackBG);
			
			if( selected.machine.type.equals( Machinery.VEHICLE ))
				canvas.drawText("Max Power:", horsePower.x, horsePower.y, whiteSmallBG);
			else
				canvas.drawText("Req Power:", horsePower.x, horsePower.y, whiteSmallBG);
			
			canvas.drawText(selected.machine.horsePower+"hp", horsePower.x + level.getPercentSize(12), horsePower.y, whiteSmallBG);
			if( level.storeView.inStore == false ){
				if( selected.machine.type.equals( Machinery.VEHICLE ))
					canvas.drawText("Fuel:", fuel.x, fuel.y, whiteSmallBG);
				else
					canvas.drawText("Fill:", fuel.x, fuel.y, whiteSmallBG);
				
					drawProgressBar( canvas, fuel.x + level.getPercentSize(6), fuel.y , level.getPercentSize(PERCENT_BAR_W),  level.getPercentSize(PERCENT_BAR_H), (float)selected.machine.maxFuel, (int)selected.machine.fuel);
			}else{
				if( selected.machine.type.equals( Machinery.VEHICLE ) ){
					canvas.drawText("Max Fuel:", fuel.x, fuel.y, whiteSmallBG);
					canvas.drawText(selected.machine.maxFuel + " gal", fuel.x + level.getPercentSize(10), fuel.y, whiteSmallBG);
				
					canvas.drawText("Cost:", cost.x, cost.y, whiteSmallBG);
					canvas.drawText(level.moneyToString( selected.machine.cost ), cost.x + level.getPercentSize(6), cost.y, whiteSmallBG);
					
					canvas.drawText("You own:", own.x, own.y, whiteSmallBG);
					canvas.drawText(level.profile.countMachines( selected.machine.name ) +"", own.x + level.getPercentSize(10), own.y, whiteSmallBG);
				}else{
					
					canvas.drawText("Cost:", fuel.x, fuel.y, whiteSmallBG);
					canvas.drawText(level.moneyToString( selected.machine.cost ), fuel.x + level.getPercentSize(6), fuel.y, whiteSmallBG);
					
					canvas.drawText("You own:", cost.x, cost.y, whiteSmallBG);
					canvas.drawText(level.profile.countMachines( selected.machine.name ) +"", cost.x + level.getPercentSize(10), cost.y, whiteSmallBG);
					if( selected.machine.subType.equals( Machinery.TRAILER ) || selected.machine.subType.equals( Machinery.SOWER ) || selected.machine.subType.equals( Machinery.SPRAYER )){
						canvas.drawText("Capacity:", cap.x, cap.y, whiteSmallBG);
						canvas.drawText(selected.machine.capacity +" l", cap.x + level.getPercentSize(10), cap.y, whiteSmallBG);
					}
				}
				
			}
			canvas.drawText("Usage:", description.x, description.y, whiteSmallBG);
			canvas.drawText(selected.machine.description, description.x + level.getPercentSize(12), description.y, whiteSmallBG);
			
		}else{
			canvas.drawRect( 0, level.getPercentSize(70f) + notificationHeight, canvasW, canvasH, btnBG);
			canvas.drawLine(startX, level.getPercentSize(78f) + notificationHeight, canvasW, level.getPercentSize(78f) + notificationHeight, whiteAlphaBG);
	
			int b_height = level.getPercentSize((float)Engine.BUTTON_SIZE_P/2.5f);
			canvas.drawRect( 0, canvasH - b_height + notificationHeight, canvasW, canvasH, blackBG);
			
		}
		blackBG.setAlpha(255);
		btnBG.setAlpha(255);
			//Buttons
		try{
			for(ScreenButton btns : level.buttons){
				if(btns.draw == true ){
				
					canvas.drawRect(btns.x, btns.y, btns.x + btns.width, btns.y + btns.height, blackBG);
					
					String btnText = btns.name;
					btnText = btnText.replace( "button","" );
					if( btns.enabled == true )
						canvas.drawText(btnText,btns.x + btns.width/2 - btnFG.measureText(btnText)/2, btns.y+btns.height/2 + btnFG.getTextSize()/2, btnFG); //Middle height: btns.y+btns.height/2 + btnfont.getTextSize()/2
					else
						canvas.drawText(btnText,btns.x + btns.width/2 - btnFG.measureText(btnText)/2, btns.y+btns.height/2 + btnFG.getTextSize()/2, lightText); //Middle height: btns.y+btns.height/2 + btnfont.getTextSize()/2
				}
			}
		}catch( ConcurrentModificationException e ){}
		
		drawNotificationBar( canvas, true );
		
	}

	public void DrawStats(Canvas canvas){

		canvas.drawBitmap(mainScreenBG,0,0, null);
		
		int iX = level.getPercentSize(15);
		int iY = level.getPercentSize(20f);

		canvas.drawRect( level.getPercentSize(4), level.getPercentSize(7f), level.getPercentSize(96), level.getPercentSize(95f), whiteBG);
		
		/*canvas.drawText( "Time Played" + ":", level.getPercentSize(5), level.getPercentSize(15f), lightTextLarge);
		Calendar cal = Calendar.getInstance();
		Calendar start = Calendar.getInstance();
		start.setTimeInMillis( level.profile.startTime );
		long time = (cal.getTimeInMillis()-start.getTimeInMillis());
		long seconds = (time / 1000);//seconds
		long minutes = seconds / 60;//seconds
		long hours = minutes / 60;//seconds
		if( minutes > 0 )
			seconds -= ( minutes * 60 );
		if( hours > 0 )
			minutes -= ( hours * 60 );
		
		canvas.drawText( hours + " hours " + minutes + " minutes " + seconds + " seconds", iX + level.getPercentSize(10), iY, lightText);
		*/
		canvas.drawText( "Time Spent Working" + ":", level.getPercentSize(5), level.getPercentSize(15f), lightTextLarge);
		long seconds = level.profile.workedTime;
		long minutes = seconds / 60;
		long hours = minutes / 60;
		if( minutes > 0 )
			seconds -= ( minutes * 60 );
		if( hours > 0 )
			minutes -= ( hours * 60 );
		canvas.drawText( hours + " hours " + minutes + " minutes " + seconds + " seconds", iX + level.getPercentSize(10), iY, lightText);
		
		iY += level.getPercentSize(7f);
		
		canvas.drawText( "Total Money Earned" + ":", level.getPercentSize(5), iY, lightTextLarge);
		iY += level.getPercentSize(5.5f);
		canvas.drawText(level.moneyToString(level.profile.totalEarned)+"", iX + level.getPercentSize(10), iY, demandTxt);
		iY += level.getPercentSize(7f);
		canvas.drawText( "Total Money Spent" + ":", level.getPercentSize(5), iY, lightTextLarge);
		iY += level.getPercentSize(5.5f);
		canvas.drawText(level.moneyToString(level.profile.moneySpent)+"", iX + level.getPercentSize(10), iY, redBG);
		iY += level.getPercentSize(7f);
		canvas.drawText( "Money Spent on Workers" + ":", level.getPercentSize(5), iY, lightTextLarge);
		iY += level.getPercentSize(5.5f);
		canvas.drawText(level.moneyToString(level.profile.workerCost)+"", iX + level.getPercentSize(10), iY, lightText);
		iY += level.getPercentSize(7f);
		canvas.drawText( "Money Spent on Fields" + ":", level.getPercentSize(5), iY, lightTextLarge);
		iY += level.getPercentSize(5.5f);
		canvas.drawText(level.moneyToString(level.profile.fieldCost)+"", iX + level.getPercentSize(10), iY, lightText);
		iY += level.getPercentSize(7f);
		canvas.drawText( "Money Spent on Vehicles" + ":", level.getPercentSize(5), iY, lightTextLarge);
		iY += level.getPercentSize(5.5f);
		canvas.drawText(level.moneyToString(level.profile.vehicleCost)+"", iX + level.getPercentSize(10), iY, lightText);
		iY += level.getPercentSize(7f);
		canvas.drawText( "Money Spent on Maintenance" + ":", level.getPercentSize(5), iY, lightTextLarge);
		iY += level.getPercentSize(5f);
		canvas.drawText(level.moneyToString(level.profile.maintenanceCost)+"", iX + level.getPercentSize(10), iY, lightText);
		iY += level.getPercentSize(7f);
		try{
			for(ScreenButton btns : level.buttons){
				canvas.drawRect(btns.x, btns.y, btns.x + btns.width, btns.y + btns.height, btnBG);
				
				String btnText = btns.name;
				btnText = btnText.replace( "button","" );
				if( btns.enabled == true )
					canvas.drawText(btnText,btns.x + btns.width/2 - btnFG.measureText(btnText)/2, btns.y+btns.height/2 + btnFG.getTextSize()/2, btnFG); //Middle height: btns.y+btns.height/2 + btnfont.getTextSize()/2
				else
					canvas.drawText(btnText,btns.x + btns.width/2 - btnFG.measureText(btnText)/2, btns.y+btns.height/2 + btnFG.getTextSize()/2, lightText); //Middle height: btns.y+btns.height/2 + btnfont.getTextSize()/2
				
			}
		}catch( ConcurrentModificationException e ){}
		
		double money = level.profile.money;
		String moneyText = level.moneyToString(money);
		if( money > 0 )
			canvas.drawText(moneyText, level.getPercentSize(HUD_MONEY.x) - whiteBG.measureText(moneyText)/2, level.getPercentSize((float)HUD_MONEY.y), whiteBG);
		else
			canvas.drawText(moneyText, level.getPercentSize(HUD_MONEY.x) - redBG.measureText(moneyText)/2, level.getPercentSize((float)HUD_MONEY.y), redBG);
		
	}
	
	public void DrawOptions(Canvas canvas){

		Rect bounds = new Rect();
		
		canvas.drawBitmap(mainScreenBG,0,0, null);

		canvas.drawRect( new Rect( level.getPercentSize(5), level.getPercentSize(5f), level.getPercentSize(95), level.getPercentSize(95f) ), blackAlphaBG );
		
		whiteBigBG.getTextBounds( Engine.OPTIONS, 0, Engine.OPTIONS.length(), bounds );
		canvas.drawText( Engine.OPTIONS, level.getPercentSize(50) - bounds.width()/2, level.getPercentSize(12f), whiteBigBG );
		
		
		blackBigText.getTextBounds( "X", 0, 1, bounds );
		
		//Sound
		canvas.drawRect( new Rect( level.getPercentSize(Options.SOUND_LOC_BTN.x), level.getPercentSize((float)Options.SOUND_LOC_BTN.y), level.getPercentSize(Options.SOUND_LOC_BTN.x) + level.getPercentSize(5), level.getPercentSize((float)Options.SOUND_LOC_BTN.y) + level.getPercentSize(5) ), whiteBG );
		if( level.enableSound == true )
			canvas.drawBitmap( checkMark, level.getPercentSize(Options.SOUND_LOC_BTN.x), level.getPercentSize((float)Options.SOUND_LOC_BTN.y), null);
			//canvas.drawText( "X", level.getPercentSize(Options.SOUND_LOC_BTN.x) + (level.getPercentSize(5)/2) - bounds.width()/2, level.getPercentSize((float)Options.SOUND_LOC_BTN.y) + (level.getPercentSize(7f)), blackBigText );
		canvas.drawText( "Enable Sound?", level.getPercentSize(Options.SOUND_LOC_TXT.x), level.getPercentSize((float)Options.SOUND_LOC_TXT.y), whiteBG );
		
		//Notification
		canvas.drawRect( new Rect( level.getPercentSize(Options.NOTIF_LOC_BTN.x), level.getPercentSize((float)Options.NOTIF_LOC_BTN.y), level.getPercentSize(Options.NOTIF_LOC_BTN.x) + level.getPercentSize(5), level.getPercentSize((float)Options.NOTIF_LOC_BTN.y) + level.getPercentSize(5) ), whiteBG );
		if( level.enableNotif == true )
			canvas.drawBitmap( checkMark, level.getPercentSize(Options.NOTIF_LOC_BTN.x), level.getPercentSize((float)Options.NOTIF_LOC_BTN.y), null);
		canvas.drawText( "Enable Notifications?", level.getPercentSize(Options.NOTIF_LOC_TXT.x), level.getPercentSize((float)Options.NOTIF_LOC_TXT.y), lightText );
				
		//Auto Save
		canvas.drawRect( new Rect( level.getPercentSize(Options.SAVE_LOC_BTN.x), level.getPercentSize((float)Options.SAVE_LOC_BTN.y), level.getPercentSize(Options.SAVE_LOC_BTN.x) + level.getPercentSize(5), level.getPercentSize((float)Options.SAVE_LOC_BTN.y) + level.getPercentSize(5) ), whiteBG );
		if( level.enableAutoSave == true )
			canvas.drawBitmap( checkMark, level.getPercentSize(Options.SAVE_LOC_BTN.x), level.getPercentSize((float)Options.SAVE_LOC_BTN.y), null);
		canvas.drawText( "Enable Auto-Save?", level.getPercentSize(Options.SAVE_LOC_TXT.x), level.getPercentSize((float)Options.SAVE_LOC_TXT.y), whiteBG );
		
		
		try{
			for(ScreenButton btns : level.buttons){
				if( btns.draw == true ){
					canvas.drawRect(btns.x, btns.y, btns.x + btns.width, btns.y + btns.height, btnBG);
					
					String btnText = btns.name;
					btnText = btnText.replace( "button","" );
					btnFG.getTextBounds( btnText, 0, btnText.length(), bounds );
					
					if( btns.enabled == true )
						canvas.drawText(btnText,btns.x + btns.width/2 - bounds.width()/2, btns.y+btns.height/2 + bounds.height()/2, btnFG); //Middle height: btns.y+btns.height/2 + btnfont.getTextSize()/2
					else
						canvas.drawText(btnText,btns.x + btns.width/2 - bounds.width()/2, btns.y+btns.height/2 + bounds.height()/2, lightText); //Middle height: btns.y+btns.height/2 + btnfont.getTextSize()/2
				}
			}
		}catch( ConcurrentModificationException e ){}

	}
	
	public void drawSunlight( Canvas canvas ){
		
		Matrix rotator = new Matrix();

		int xRotate = level.getPercentSize((int)(SUN_LIGHT_H)/2)/2;
		int yRotate = level.getPercentSize(SUN_LIGHT_H)/2;
		
		rotator.postRotate(sunAngle, xRotate, yRotate);
		
		// to set the position in canvas where the bitmap should be drawn to;
		// NOTE: coords in canvas-space!
		int xTranslate = level.getPercentSize((int)SUN_LOC.x) - xRotate;
		int yTranslate = level.getPercentSize((float)SUN_LOC.y) - yRotate;
		
		rotator.postTranslate(xTranslate, yTranslate);

		if( sunAngle <= 360 )
			sunAngle += 0.15f;
		else
			sunAngle = 0;
		
		canvas.drawBitmap( sunLight, rotator, null );
		
	}
	
	public void drawNewGame( Canvas canvas ){

		canvas.drawRect( new Rect( level.getPercentSize(15), level.getPercentSize(25f), level.getPercentSize(85), level.getPercentSize(75f) ), whiteAlphaBG );
		
		canvas.drawText( "Are you sure you want to start a new game?", level.getPercentSize(50) - blackText.measureText("Are you sure you want to start a new game?")/2, level.getPercentSize(40f), blackText);
	}
	
	public void drawTutorial( Canvas canvas ){
		
		canvas.drawColor( ALPHA_BLACK );
		
		for( int i = 0; i < level.tutorial.descriptions.size(); i++ ){
			
			String desc = level.tutorial.descriptions.get(i);
			float x = level.tutorial.d_locations.get(i).x;
			float y = level.tutorial.d_locations.get(i).y;
			
			Paint textP = new Paint();
			textP.setColor(Color.WHITE);
			textP.setTextSize( level.getPercentSize(5f) );
			textP.setAntiAlias(true);
			textP.setStyle(Paint.Style.FILL);
			textP.setStrokeWidth(1);
			
			if( desc.startsWith("GOLD") ){
				textP.setColor(GOLD);
				desc = desc.replace("GOLD", "");
			}if( desc.startsWith("ORANGE") ){
				textP.setColor(ORANGE);
				desc = desc.replace("ORANGE", "");
			}if( desc.startsWith("GREEN") ){
				textP.setColor(DARK_GREEN);
				desc = desc.replace("GREEN", "");
			}if( desc.startsWith("BLUE") ){
				textP.setColor(Color.CYAN);
				desc = desc.replace("BLUE", "");
			}
			
			if( desc.contains("BOLD") ){
				textP.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
				desc = desc.replace("BOLD", "");
			}
			
			canvas.drawText( desc, x, y, textP);
			
			//specific screens
			if( level.tutorial.tutorialScreen.equals(Engine.MAIN_SCREEN) ){
				
			}else if( level.tutorial.tutorialScreen.equals(Engine.FARM) ){
				
				//Squares
				RectF area = new RectF( level.getPercentSize(1), level.getPercentSize(82.5f), level.getPercentSize(21), level.getPercentSize(92.5f));
				
				canvas.drawRect( area, rectFieldPaint );
				area.left += level.getPercentSize(21);
				area.right += level.getPercentSize(18);
				
				rectFieldPaint.setColor( Color.CYAN );
				canvas.drawRect( area, rectFieldPaint );
				area.left += level.getPercentSize(39);
				area.right += level.getPercentSize(39);
				
				rectFieldPaint.setColor( DARK_ORANGE );
				canvas.drawRect( area, rectFieldPaint );
				area.left += level.getPercentSize(18);
				area.right += level.getPercentSize(21);
				
				rectFieldPaint.setColor( DARK_GREEN );
				canvas.drawRect( area, rectFieldPaint );
				

				rectFieldPaint.setColor( Color.WHITE );
			}
		}
		
		//Draw Buttons
		try{
			for(ScreenButton btns : level.buttons){
				canvas.drawRect(btns.x, btns.y, btns.x + btns.width, btns.y + btns.height, btnBG);
				
				String btnText = btns.name;
				btnText = btnText.replace( "button","" );
				if( btns.enabled == true )
					canvas.drawText(btnText,btns.x + btns.width/2 - btnFG.measureText(btnText)/2, btns.y+btns.height/2 + btnFG.getTextSize()/2, btnFG); //Middle height: btns.y+btns.height/2 + btnfont.getTextSize()/2
				else
					canvas.drawText(btnText,btns.x + btns.width/2 - btnFG.measureText(btnText)/2, btns.y+btns.height/2 + btnFG.getTextSize()/2, lightText); //Middle height: btns.y+btns.height/2 + btnfont.getTextSize()/2
				
			}
		}catch( ConcurrentModificationException e ){}
		
	}
	
	public void drawLoading(Canvas canvas){

		float x = level.getPercentSize(loadingLoc.x);
		float y = level.getPercentSize((float)loadingLoc.y);
		RectF oval = new RectF( x, y, x + level.getPercentSize(loadingSize.width), y + level.getPercentSize(loadingSize.height));
		
		float start = -90;

		if( loadingAngle <= 360 )
			loadingAngle += 10;
		else
			loadingAngle = 0;
		
		canvas.drawArc( oval, start, loadingAngle, true, whiteAlphaBG );
		
		//canvas.drawText("LOADING...", canvas.getWidth()/2 - (25), canvas.getHeight(), whiteBG);

	}

	public void drawNotificationBar( Canvas canvas, boolean showMoney ){
		
		canvas.drawRect(0, 0, canvas.getWidth(), level.getPercentSize(NOTIFICATION_BAR_H), blackAlphaBG);
		double money = level.profile.money;
		String moneyText = level.moneyToString(money);
		if( showMoney == true ){
			if( money > 0 )
				canvas.drawText(moneyText, level.getPercentSize(HUD_MONEY.x) - whiteBG.measureText(moneyText)/4, level.getPercentSize((float)HUD_MONEY.y), whiteBG);
			else
				canvas.drawText(moneyText, level.getPercentSize(HUD_MONEY.x) - whiteBG.measureText(moneyText)/4, level.getPercentSize((float)HUD_MONEY.y), redBG);
		}
		
		//canvas.drawText(moneyText, level.getPercentSize(HUD_MONEY.x) - whiteBG.measureText(moneyText)/2, level.getPercentSize((float)HUD_MONEY.y), redBG);
	}
	
	public void drawCoinBox( Canvas canvas ){
		
		String coins = "x" + level.profile.getGold();
		Rect bounds = new Rect();
		whiteSmallBG.getTextBounds(coins, 0, coins.length(), bounds);
		canvas.drawText(coins, canvasW - bounds.width(), level.getPercentSize(NOTIFICATION_BAR_H), whiteSmallBG);
		canvas.drawBitmap( goldCoin, canvasW - level.getPercentSize(GOLD_COIN_W) - bounds.width(), NOTIFICATION_BAR_H, null);
		
	}
	
	public void drawProgressBar(Canvas canvas, int x, int y, int width, int height, float max, int value){


		if( value > max )
			value = (int) max;
		
		y -= height;
		canvas.drawBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.percentbarback), width, height, true), x, y, null);

		IntSize tmp = new IntSize(width, height);
		float healthP = 0;
		if( value > 0 ){
			healthP = (tmp.width / (max/value));

		}else{
			healthP = 1;
		}
		try{
			canvas.drawBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.percentbar), (int) healthP, (int) tmp.height, true), x, y, null);
		}catch( IllegalArgumentException e ){

		}
	}

	public void toast(String message){
		final String mes = message;
		handler.post(new Runnable(){
			public void run(){
				Toast.makeText(getContext(), mes, Toast.LENGTH_LONG).show();
			}
		});

	}
}

class Options{
	
	public static final Point SOUND_LOC_BTN = new Point( 50, 38 );
	public static final Point SOUND_LOC_TXT = new Point( 57, 45 );
	public static final Point NOTIF_LOC_BTN = new Point( 50, 48 );
	public static final Point NOTIF_LOC_TXT = new Point( 57, 55 );
	public static final Point SAVE_LOC_BTN = new Point( 50, 58 );
	public static final Point SAVE_LOC_TXT = new Point( 57, 65 );
	
	public Options(){
		
	}
	
}
