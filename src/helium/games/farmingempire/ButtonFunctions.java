package helium.games.farmingempire;

import java.util.Calendar;
import java.util.Date;

import javax.crypto.Mac;

import android.util.Log;

import helium.games.farmingempire.Engine.DIFFICULTY;

public class ButtonFunctions {

	Engine engine;
	
	public ButtonFunctions( Engine engine ) {

		this.engine = engine;
	}
	
	//Menu Buttons
	public Runnable newGame = new Runnable() {  
		public void run() {  
			
			if( engine.getActivitesSettings.getAll().toString().equals("{}") && engine.getProfileSettings.getAll().toString().equals("{}")){
				
				engine.toast("Creating Profile...",5);
				engine.newGame( DIFFICULTY.NORMAL, 1 );
			}else{
				
				/*int b_width = engine.getPercentSize(Engine.BUTTON_SIZE_P) - engine.getPercentSize(5);
				int b_height = engine.getPercentSize((float)Engine.BUTTON_SIZE_P/2.5f) + engine.getPercentSize(1f);
				int bX = engine.getPercentSize(50) - b_width/2;
				int bY = engine.getPercentSize(30.0f);
				bX += b_width + engine.getPercentSize(5f);
				engine.getButtonByName( Engine.NEW_GAME ).enabled = false;
				engine.buttons.add( new ScreenButton( engine.buttons.size(),bX,bY,b_width,b_height,"button"+"Are you sure?",engine.getResources(),engine, engine.btnFunctions.newGameConfirm ) );
				 */
				engine.openNewGame();
			}
		}
	};
	
	public Runnable newGameConfirm = new Runnable() {  
		public void run() {  
			
			engine.buttons.clear();
			engine.newGameConfirm = false;
			engine.toast("Creating Profile...",5);
			engine.newGame( DIFFICULTY.NORMAL, 1 );
		}
	};
	
	public Runnable newGameCancel = new Runnable() {  
		public void run() {  
			
			engine.newGameConfirm = false;
			engine.openMenu();
		}
	};
	
	public Runnable resumeGame = new Runnable() {  
		public void run() {  
			engine.toast("Loading Map...",5);
			engine.loadGame( DIFFICULTY.NORMAL, 1 );
		}
	};
	
	public Runnable options = new Runnable() {  
		public void run() {  
			engine.openOptions();
		}
	};
	
	public Runnable quit = new Runnable() {  
		public void run() {  
			engine.finish();
		}
	};
	
	//Options Buttons
	
	public Runnable options_sound = new Runnable() {  
		public void run() {  
			engine.enableSound = !engine.enableSound;
			engine.profileEditor.putBoolean( "OptionsSound", engine.enableSound );
			engine.profileEditor.commit();
		}
	};
	
	public Runnable options_notif = new Runnable() {  
		public void run() {  
			engine.enableNotif = !engine.enableNotif;
			engine.profileEditor.putBoolean( "OptionsNotif", engine.enableNotif );
			engine.profileEditor.commit();
		}
	};
	
	public Runnable options_save = new Runnable() {  
		public void run() {  
			engine.enableAutoSave = !engine.enableAutoSave;
			engine.profileEditor.putBoolean( "OptionsSave", engine.enableAutoSave );
			engine.profileEditor.commit();
		}
	};
	
	public Runnable options_okay = new Runnable() {  
		public void run() {  
			engine.openMenu();
		}
	};
	
	//Main Screen Buttons
	public Runnable farm = new Runnable() {  
		public void run() {  
			engine.openFarm();
		}
	};
	
	public Runnable garage = new Runnable() {  
		public void run() {  
			engine.storeView.inStore = false;
			engine.openGarage( false );
		}
	};
	
	public Runnable store = new Runnable() {  
		public void run() {  
			engine.storeView.inStore = true;
			engine.openGarage( true );
		}
	};
	
	public Runnable stats = new Runnable() {  
		public void run() { 
			
			engine.openStats();
		}
	};
	
	public Runnable saveQuit = new Runnable() {  
		public void run() {  
			engine.save( true );
			engine.openMenu();
			engine.toast("Saved",5);
		}
	};
	
	//Garage
	public Runnable garageItemBtn = new Runnable() {  
		public void run() {
			if( engine.storeView.inStore == false ){
				if( engine.storeView.selectedItem.machine.type.equals(Machinery.VEHICLE) ){
					
					ScreenButton refill = engine.getButtonByName( "Refill" );
					refill.enabled = true;
					refill.draw = true;
				}else{
					
					ScreenButton refill = engine.getButtonByName( "Refill" );
					refill.enabled = false;
					refill.draw = false;
					if( engine.storeView.selectedItem.machine.subType.equals(Machinery.SOWER) || engine.storeView.selectedItem.machine.subType.equals(Machinery.SPRAYER)){
						
						refill.enabled = true;
						refill.draw = true;
					}
				}
			}
		}
	};
	
	public Runnable leftCat = new Runnable() {  
		public void run() {
			if( engine.storeView.type > 0 ){
				engine.storeView.type--;
				engine.openGarage( engine.storeView.inStore );
			}
			
		}
	};
	
	public Runnable rightCat = new Runnable() {  
		public void run() {
			if( engine.storeView.type < Machinery.CATEGORIES.length-1 ){
				engine.storeView.type++;
				engine.openGarage( engine.storeView.inStore );
			}else{
				engine.storeView.type = 0;
				engine.openGarage( engine.storeView.inStore );
			}
		}
	};
	
	public Runnable back = new Runnable() {  
		public void run() {
			engine.openMainScreen();
		}
	};
	
	public Runnable openStore = new Runnable() {  
		public void run() {
			engine.storeView.type = 0;
			engine.storeView.inStore = !engine.storeView.inStore;
			engine.openGarage( engine.storeView.inStore );
		}
	};
	
	public Runnable sell = new Runnable() {  
		public void run() {
			boolean canSell = false;
			int count = 0;
			int total = engine.profile.jobs.size();
			for( Job job : engine.profile.jobs ){
				
				if( job.vehicleID != engine.storeView.selectedItem.machine.id && job.attachmentID != engine.storeView.selectedItem.machine.id){
					count ++;
				}
			}
			if( count >= total )
				canSell = true;
			
			boolean mchnVeh = false;
			boolean mchnEquip = false;
			int countVeh = 0;
			int countEquip = 0;
			
			for( Machinery mchn : engine.profile.machines ){
				if( mchn.type.equals(Machinery.VEHICLE)){
					mchnVeh = true;
					countVeh++;
				}
				if( mchn.type.equals(Machinery.EQUIPMENT)){
					mchnEquip = true;
					countEquip++;
				}
			}
			
			if( engine.storeView.selectedItem.machine.type.equals( Machinery.VEHICLE ) ){
				if( mchnVeh == false ||  countVeh <= 1 ){
					canSell = false;
					engine.toast("Must have at least one vehicle!", 2 );
				}
			}
			
			if( engine.storeView.selectedItem.machine.type.equals( Machinery.EQUIPMENT ) ){
				if( mchnEquip == false ||  countEquip <= 1){
					canSell = false;
					engine.toast("Must have at least one attachment!", 2 );
				}
			}
			
			if( canSell == true ){
				double money = engine.storeView.selectedItem.machine.cost/2;
				
				engine.toast( "Sold for " + engine.moneyToString(money), 2);
				engine.playSound( engine.sound_money );
				engine.profile.machines.remove(engine.storeView.selectedItem.machine);
				engine.profile.money += money;
				engine.profile.totalEarned += money;
				engine.openGarage( engine.storeView.inStore );
			}else{
				engine.toast( "Can't sell while machine is in use", 2);
			}
		}
	};
	
	public Runnable buy = new Runnable() {  
		public void run() {
			
			if( engine.profile.purchase( engine.storeView.selectedItem.machine.cost ) == true ){
				engine.profile.machineryOwned++;
				engine.profile.vehicleCost += engine.storeView.selectedItem.machine.cost;
				engine.storeView.selectedItem.machine.id = engine.profile.machineryOwned;
				engine.profile.machines.add(engine.storeView.selectedItem.machine);
				engine.toast("PURCHASE" + engine.storeView.selectedItem.machine.name + " bought for " + engine.moneyToString( engine.storeView.selectedItem.machine.cost ), 3 );
				engine.openGarage( engine.storeView.inStore ); 
			}
		
		}
	};
	
	public Runnable refill = new Runnable() {  
		public void run() {
			
			double maxFuel = engine.profile.getMachineByID(engine.storeView.selectedItem.machine.id).maxFuel;
			double cost = (maxFuel - engine.profile.getMachineByID(engine.storeView.selectedItem.machine.id).fuel)* Machinery.FUEL_COST;
			if( cost > 0 ){
				engine.profile.getMachineByID(engine.storeView.selectedItem.machine.id).fuel = maxFuel;
				if( engine.activityDialog.attachment.equals( engine.profile.getMachineByID(engine.storeView.selectedItem.machine.id) ) )
					engine.activityDialog.attachment.fuel = maxFuel;
				engine.profile.money -= cost;
				engine.profile.moneySpent += cost;
				engine.profile.maintenanceCost += cost;
				engine.toast("Refilled for " + engine.moneyToString(cost), 2);
			}
		}
	};
	
	public Runnable goldCoin = new Runnable() {
		
		public void run() { 
		
			engine.profile.addCoin();
			engine.selectedMap.hasGoldCoin = false;
			engine.playSound( engine.sound_money );
			engine.toast( "PURCHASE" + "You found a gold coin!", 2);
		}
	};
	
	//Farm
	public Runnable field = new Runnable() {  
		public void run() { 
			
			engine.selectedField = engine.getFieldByID(engine.selectedBtn.id);
			engine.selectingField = true;
			
			
			
			/*Purchase Field*/
			if( engine.selectedField.owned == false ){
				//Open purchase dialog
				engine.purchaseFieldDialog();
			}else{
				//Open activity dialog
				if( engine.selectedField.busy == false ){
					engine.activityFieldDialog( false, false );
				}else{
					engine.activityFieldDialog( true, false );
				}
			}
		
		}
	};
	
	public Runnable sellPoint = new Runnable() {  
		public void run() { 
			
			//engine.selectedField = engine.getFieldByID(9999);
			engine.drawSellPointIcons = true;
			engine.selectingField = false;
			engine.selectedSellPoint = engine.getSellPointByID(engine.selectedBtn.id);

			engine.toastLog.clear();
			//engine.toast(engine.selectedSellPoint.name +": Whe:$" + engine.toThousands(engine.selectedSellPoint.whe_price) +"  Bar:$" + engine.toThousands(engine.selectedSellPoint.bar_price) +" Can:$" + engine.toThousands(engine.selectedSellPoint.can_price), 3);
			engine.activityFieldDialog( false, true );
			
		}
	};
	
	//Farm Dialogs
	public Runnable purchase_buy = new Runnable() {  
		public void run() {  
			
			if( engine.profile.purchase( engine.selectedField.cost ) == true ){
				engine.selectedField.owned = true;
				engine.profile.fieldCost += engine.selectedField.cost;
				engine.toast("PURCHASE" + "Field " + engine.selectedField.id + " bought for " + engine.moneyToString( engine.selectedField.cost ), 3 );
			}
			engine.openFarm();
		}
	};
	
	public Runnable activity_go = new Runnable() {  
		public void run() {  
			Machinery veh = engine.activityDialog.machine;
			Machinery equip = engine.activityDialog.attachment;
			String seed = engine.activityDialog.other;
			double hectares = engine.selectedField.hectare;
			double eta = (60*hectares) + (((Machinery.MIN_TIME*veh.hinder)*hectares) + ((Machinery.MIN_TIME*equip.hinder)*hectares));
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.SECOND, (int) eta);	//Time of completion
			Job job = new Job(engine, veh.id, equip.id, engine.selectedField.id, seed, 0, cal);
			job.totalTime = (int) eta;
			Log.i( "JOB", seed );
			if( veh.subType.equals(Machinery.COMBINE) )
				job.jobType = Job.HARVESTING;
			else if( equip.subType.equals(Machinery.CULTIVATOR) )
				job.jobType = Job.CULTIVATING;
			else if( equip.subType.equals(Machinery.SOWER) )
				job.jobType = Job.SOWING;
			else if( equip.subType.equals(Machinery.TRAILER) )
				job.jobType = Job.SELLING;
			else if( equip.subType.equals(Machinery.SPRAYER) )
				job.jobType = Job.FERTILIZING;
			
			boolean verify = false;
			if( engine.selectedField.job_cultivated == false ){
				if( job.jobType.equals(Job.CULTIVATING) ){
					verify = true;
				}
				if( equip.name.equals( Machinery.SOWER2 ) ){
					
						verify = true;
				}
			}else if( engine.selectedField.job_sowed == false ){
				if( job.jobType.equals(Job.SOWING) ){
					verify = true;
				}
			}else if( engine.selectedField.job_harvested == false ){
				if( job.jobType.equals(Job.HARVESTING) ){
					
					seed = engine.selectedField.seed;
					job.seed = seed;
					if( equip.subType.equals( Machinery.HEADER ) ){
						verify = true;
					}else{
						engine.toast( "You need a header on a combine!",2 );
						verify = false;
					}
				}
			}
			
			if( job.jobType.equals(Job.FERTILIZING) ){
				if( engine.selectedField.job_fertilized == false ){
					verify = true;
				}else{
					verify = false;
					engine.toast( "Field is already fertilized", 3 );
				}
			}
			
			if( job.jobType.equals(Job.SELLING)){
				verify = false;
			}
			
			
			//Check fuel
			if( veh.useFuel( Machinery.FUEL_USAGE * engine.selectedField.hectare ) == false ){
				engine.toast( veh.name + " does not have enough fuel", 2);
				verify = false;
			}
			
			//Check fill
			if( equip.subType.equals(Machinery.SOWER) ){
				if( equip.useFuel( Machinery.FUEL_USAGE * engine.selectedField.hectare ) == false ){
					engine.toast( equip.name + " does not have enough seed", 2);
					verify = false;
				}
			}else if( equip.subType.equals(Machinery.SPRAYER) ){
				if( equip.useFuel( Machinery.FUEL_USAGE * engine.selectedField.hectare ) == false ){
					engine.toast( equip.name + " does not have enough fertilizer", 2);
					verify = false;
				}
			}
			//Check if busy
			if( veh.inUse == true){
				engine.toast( veh.name + " already in use", 2);
				verify = false;
			}	
			if( equip.inUse == true){
				engine.toast( equip.name + " already in use", 2);
				verify = false;
			}	
			
			//Check power
			if( veh.horsePower < equip.horsePower ){
				engine.toast( veh.name + " does not have enough power", 2);
				verify = false;
			}
			if( verify == true ){
				engine.playSound( engine.sound_tractor );
				engine.toast("Worker Hired! Done in " + job.timeLeft(), 3);
				double workerCost = Double.parseDouble(engine.toDecimal( engine.selectedField.hectare * Job.WORKER_COST ) );
				engine.toast("Worker Cost : " + engine.moneyToString(workerCost), 3);
				engine.profile.money -= workerCost;
				engine.profile.moneySpent += workerCost;
				engine.profile.workerCost += workerCost;
				engine.profile.jobs.add(job);
				engine.selectedField.busy = true;
				veh.inUse = true;
				equip.inUse = true;
				engine.openFarm();
				if( engine.enableAutoSave == true )
					engine.save( false );
			}else{
				engine.toast("Field not ready for this job",3);
			}
		}
	};
	
	public Runnable activity_sell_go = new Runnable() {  
		public void run() {  
			
			
			Machinery veh = engine.activityDialog.machine;
			Machinery equip = engine.activityDialog.attachment;
			String seed = engine.activityDialog.other;
			//Stop filling equipment
			equip.stopFillingTrailer( engine );
			
			double distance = engine.getSellPointByID(engine.selectedSellPoint.id).distance;
			double eta = (60*distance) + (((Machinery.MIN_TIME*veh.hinder)*distance) + ((Machinery.MIN_TIME*equip.hinder)*distance));
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.SECOND, (int) eta);	//Time of completion
			int amount = (int)equip.fuel;
			Job job = new Job(engine, veh.id, equip.id, engine.selectedSellPoint.id, seed, amount, cal);
			job.sellPrice = (int) engine.getSellPointByID(engine.selectedSellPoint.id).getPrice(seed);
			job.totalTime = (int) eta;

			if( veh.subType.equals(Machinery.COMBINE) )
				job.jobType = Job.HARVESTING;
			else if( equip.subType.equals(Machinery.CULTIVATOR) )
				job.jobType = Job.CULTIVATING;
			else if( equip.subType.equals(Machinery.SOWER) )
				job.jobType = Job.SOWING;
			else if( equip.subType.equals(Machinery.TRAILER) )
				job.jobType = Job.SELLING;
			else if( equip.subType.equals(Machinery.SPRAYER) )
				job.jobType = Job.FERTILIZING;
			
			boolean verify = false;
			
			if( job.jobType.equals(Job.SELLING)){
				verify = true;
			}
			
			
			//Check fuel
			if( veh.useFuel( Machinery.FUEL_USAGE * distance ) == false ){
				//TODO Change 1 to distance
				engine.toast( veh.name + " does not have enough fuel", 2);
				verify = false;
			}
			
			//Check fill
			if( equip.subType.equals(Machinery.TRAILER) ){
				if( equip.useFuel( Machinery.FUEL_USAGE * distance ) == false ){
					//TODO Change 1 to distance
					engine.toast( "Fill " + equip.name + " with crop to sell first", 2);
					verify = false;
				}
			}
			
			//Check if busy
			if( veh.inUse == true){
				engine.toast( veh.name + " already in use", 2);
				verify = false;
			}	
			if( equip.inUse == true){
				engine.toast( equip.name + " already in use", 2);
				verify = false;
			}	
			
			//Check power
			if( veh.horsePower < equip.horsePower ){
				engine.toast( veh.name + " does not have enough power", 2);
				verify = false;
			}
			
			if( verify == true ){
				engine.playSound( engine.sound_tractor );
				engine.toast("Selling for " + engine.moneyToString(job.sellPrice) + "/ton", 3 );
				engine.toast("Worker Hired! Done in " + job.timeLeft(), 3);
				double workerCost = Double.parseDouble(engine.toDecimal( distance * Job.WORKER_COST ) );
				engine.toast("Worker Cost : " + engine.moneyToString(workerCost), 3);
				engine.profile.money -= workerCost;
				engine.profile.moneySpent += workerCost;
				engine.profile.workerCost += workerCost;
				engine.profile.jobs.add(job);
				//Reduce inventory
				if( equip.seedInTrailer.equals( Crops.WHEAT ) ){
					engine.profile.crops.wheat -= amount;
				}else if( equip.seedInTrailer.equals( Crops.BARLEY ) ){
					engine.profile.crops.barley -= amount;
				}else if( equip.seedInTrailer.equals( Crops.CANOLA ) ){
					engine.profile.crops.canola -= amount;
				}
				
				equip.fillAmount = 0;
				veh.inUse = true;
				equip.inUse = true;
				engine.openFarm();
				if( engine.enableAutoSave == true )
					engine.save( false );
			}else{
				engine.toast("Can't sell crop with this!",3);
			}
		}
	};
	
	
	public Runnable skip_job = new Runnable() {  
		public void run() {  
			
			if( engine.profile.getGold() > 0 ){
				engine.profile.removeCoin();
				engine.toast( "Job Skipped!", 2 );
				engine.profile.getJobByField( engine.selectedField.id ).ETA = Calendar.getInstance();
			}
		}
	};
	
	public Runnable activity_refill = new Runnable() {  
		public void run() {  

			try{
				if( engine.activityDialog.attachment.refilling == false ){
					if( engine.activityDialog.attachment.fuel <= 0 ){
						engine.getButtonByID( 9998 ).name = "Stop";
						
						engine.getButtonByID( 9999 ).enabled = false;	//Disable seed choosing
						//Refill equipment
						engine.activityDialog.attachment.seedInTrailer = engine.activityDialog.other;
						engine.activityDialog.attachment.refillTrailer();
					}else{
						if( engine.activityDialog.other.equals( engine.activityDialog.attachment.seedInTrailer ) ){
							
							engine.getButtonByID( 9998 ).name = "Stop";
							
							engine.getButtonByID( 9999 ).enabled = false;	//Disable seed choosing
							//Refill equipment
							engine.activityDialog.attachment.seedInTrailer = engine.activityDialog.other;
							engine.activityDialog.attachment.refillTrailer();
						}else{
							engine.toast( engine.activityDialog.attachment.name + " is already filled with " + engine.activityDialog.attachment.seedInTrailer, 2 );
						}
					}
				
				}else{
					engine.getButtonByName(Engine.GO).enabled = true;
					engine.getButtonByID( 9998 ).name = "Fill";
					
					//Stop filling equipment
					engine.activityDialog.attachment.stopFillingTrailer( engine );
				}
			}catch( NullPointerException err ){}
			
		}
	};
	
	public Runnable activity_cancel = new Runnable() {  
		public void run() {  

			engine.selectedField.busy = false;
			engine.profile.getMachineByID(engine.profile.getJobByField( engine.selectedField.id ).vehicleID).inUse = false;
			engine.profile.getMachineByID(engine.profile.getJobByField( engine.selectedField.id ).attachmentID).inUse = false;
			engine.profile.jobs.remove( engine.profile.getJobByField( engine.selectedField.id ) );
			engine.toast( "Job canceled",3 );
			engine.openFarm();
		}
	};
	
	public Runnable activity_machine = new Runnable() {  
		public void run() {  

			engine.activityDialog.clicked = Machinery.VEHICLE;
			engine.activityGarageDialog( Machinery.VEHICLE, 0 );
		}
	};
	
	public Runnable activity_attachment = new Runnable() {  
		public void run() {  

			if( engine.activityDialog.attachment.refilling == true )
				engine.activityDialog.attachment.stopFillingTrailer( engine );
			engine.activityDialog.clicked = Machinery.EQUIPMENT;
			engine.activityGarageDialog( Machinery.EQUIPMENT, 0 );

			
		}
	};
	
	public Runnable activity_other = new Runnable() {  
		public void run() {  
			
			engine.activityDialog.clicked = Machinery.OTHER;
			engine.activityGarageDialog( Machinery.OTHER, 0 );
		}
	};
	
	public Runnable activity_garage_prev = new Runnable() {  
		public void run() {  

			if(engine.garageDialogPage > 0){
				engine.activityGarageDialog( engine.activityDialog.clicked, engine.garageDialogPage-1 );
				engine.buttons.get(1).enabled = true;
			}
		}
	};
	
	public Runnable activity_garage_next = new Runnable() {  
		public void run() {  
			int maxPages = engine.activityDialog.listItems.size() / View.LIST_ITEM_AMOUNT;
			if( engine.garageDialogPage < maxPages ){
				engine.activityGarageDialog( engine.activityDialog.clicked, engine.garageDialogPage+1 );
				engine.buttons.get(0).enabled = true;
				engine.buttons.get(1).enabled = false;
				//engine.toast("Max: " + maxPages + ", DialogPage: " + engine.garageDialogPage, 2 );
				if(engine.garageDialogPage < maxPages)
					
					engine.buttons.get(1).enabled = true;
			}else{
				engine.buttons.get(1).enabled = false;
			}
		}
	};
	
	public Runnable activity_garage_item = new Runnable() {  
		public void run() {  
			if( engine.activityDialog.listItems.size() > (engine.selectedBtn.id-1)){
				if(engine.activityDialog.clicked.equals(Machinery.VEHICLE))
					engine.activityDialog.machine = engine.activityDialog.listItems.get(engine.selectedBtn.id-1);
				else if(engine.activityDialog.clicked.equals(Machinery.EQUIPMENT)){
					engine.activityDialog.attachment = engine.activityDialog.listItems.get(engine.selectedBtn.id-1);
				}else
					engine.activityDialog.other = engine.activityDialog.listItems.get(engine.selectedBtn.id-1).name;
				
				engine.openFarm();
				if( engine.selectingField == true )
					engine.activityFieldDialog( engine.selectedField.busy, engine.activityDialog.sellPoint );
				else
					engine.activityFieldDialog( false, true );
				
				try{
					if ( engine.activityDialog.attachment.subType.equals(Machinery.SOWER) ){
						engine.getButtonByID( 9999 ).enabled = true;
					}else{
						engine.getButtonByID( 9999 ).enabled = false;
					}
					
					if( engine.activityDialog.sellPoint == true ){
						if ( engine.activityDialog.attachment.subType.equals(Machinery.TRAILER) ){
							boolean enable = false;
							
							engine.getButtonByID( 9999 ).enabled = true;
							
							if( engine.activityDialog.other.equals( Crops.WHEAT ) ){
								if( engine.profile.crops.checkInventory( Crops.WHEAT, 0) == true ){
									enable = true;
								}
							}
							
							if( engine.activityDialog.other.equals( Crops.BARLEY ) ){
								if( engine.profile.crops.checkInventory( Crops.BARLEY, 0) == true ){
									enable = true;
								}
							}
							
							if( engine.activityDialog.other.equals( Crops.CANOLA ) ){
								if( engine.profile.crops.checkInventory( Crops.CANOLA, 0) == true ){
									enable = true;
								}
							}
							
							if( enable == true )
								engine.getButtonByID( 9998 ).enabled = true;
						}else{
							engine.getButtonByID( 9999 ).enabled = false;
							engine.getButtonByID( 9998 ).enabled = false;
						}
					}
					
				}catch( NullPointerException err ){}
			}
		}
	};
	
	public Runnable tutorial_okay = new Runnable() {  
		public void run() {  
			
			engine.tutorial.tutorialOpen = false;
			
			if( engine.tutorial.tutorialScreen.equals( Engine.MAIN_SCREEN )){
				engine.profile.showTutorial_Menu = true;
				engine.openMainScreen();
			}else if( engine.tutorial.tutorialScreen.equals( Engine.FARM )){
				engine.profile.showTutorial_Farm = true;
				engine.openFarm();
			}
		}
	};
	
	public Runnable farm_back = new Runnable() {  
		public void run() {  
			engine.openFarm();
		}
	};
}