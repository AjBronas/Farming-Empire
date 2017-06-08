package helium.games.farmingempire;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class ScreenObject {

	public String name = "NULL";
	public int x = 0;
	public int y = 0;
	public int width = 50;
	public int height = 50;
	public int size = 50;
	public String type = Engine.TYPE_IMAGE;
	public boolean draw = true;
	public Bitmap image;
	public Animation animation;
	public boolean addToInventory = false;
	public Resources res;
	public Engine level;
	public String displayName = "null";
	public String optionString = "";
	
	public ScreenObject( Resources res, Engine level ){
		
		this.res = res;
		this.animation = new Animation( res );
		this.level = level;
	}
	
	public void setX(int x){

		this.x = x;

	}

	public void setY(int y){

		this.y = y;

	}
	
	public void setWidth(int width){

		this.width = width;

	}

	public void setHeight(int height){

		this.height = height;

	}
	
	public int getX(){

		return this.x;

	}

	public int getY(){

		return this.y;

	}
	
	
	public Rect Collision(){
		
		Rect r = new Rect(this.x,this.y,this.x+this.width,this.y+this.height);
		return r;
		
	}
	
}
