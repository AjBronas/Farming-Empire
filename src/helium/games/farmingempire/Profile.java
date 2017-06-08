package helium.games.farmingempire;

import helium.games.farmingempire.Engine.DIFFICULTY;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import android.content.res.Resources;

public class Profile {

	Engine engine;
	Resources res;
	boolean shownDemand = false;
	boolean showTutorial_Menu = false;
	boolean showTutorial_Farm = false;
	
	double money = 0;
	private int gold = 0;
	
	DIFFICULTY difficulty = DIFFICULTY.NORMAL;
	int map = 0;
	List<Integer> ownedFields = new ArrayList<Integer>();
	List<Machinery> machines = new ArrayList<Machinery>();
	public List<Job> jobs = new ArrayList<Job>();
	public Crops crops = new Crops();
	
	//Stats:
	long startTime = 0;
	long workedTime = 0;
	double workerCost = 0;
	double fieldCost = 0;
	double vehicleCost = 0;
	double maintenanceCost = 0;
	double totalEarned = 0;
	double moneySpent = 0;
	double timePlayed = 0;
	int fieldsOwned = 0;
	int machineryOwned = 0;
	
	public Profile( Engine engine ) {
		this.engine = engine;
	}

	public boolean purchase( double amount ){
		
		boolean result = false;
		if( money >= amount){
			money -= amount;
			result = true;
			moneySpent += amount;
			engine.playSound( engine.sound_money );
		}else{
			engine.toast("You cant afford this", 3);
		}
		
		return result;
	}
	
	public Machinery getMachineByID( int id ){
		
		Machinery result = null;
		try{
			for( Machinery mchn : machines ){
				if( mchn.id == id )
					result = mchn;
			}
		}catch( ConcurrentModificationException err ){}
		
		return result;
	}
	
	public Machinery getMachineByName( String name ){
		
		Machinery result = null;
		try{
			for( Machinery mchn : machines ){
				if( mchn.name.equals(name) )
					result = mchn;
			}
		}catch( ConcurrentModificationException err ){}
		
		return result;
	}
	
	public Job getJobByField( int fieldId ){
		
		Job result = null;
		try{
			for( Job job : jobs ){
				if( job.fieldID == fieldId )
					result = job;
			}
		}catch( ConcurrentModificationException err ){}
		
		return result;
	}

	public int countMachines( String name ){
		
		int count = 0;
		
		for( Machinery mchn : machines ){
			if( mchn.name.equals( name ) )
				count++;
		}
		return count;
	}
	
	protected void addCoin(){
		
		gold += 1;
		
	}
	
	protected void removeCoin(){
		
		gold -= 1;
		
		if( gold < 0 )
			gold = 0;
	}
	
	protected void setGold( int goldAmount ){
		
		gold = goldAmount;
	}

	protected int getGold(){
		
		return gold;
	}

}
