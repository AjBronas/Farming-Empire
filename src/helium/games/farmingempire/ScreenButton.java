package helium.games.farmingempire;


import android.content.res.Resources;
import android.graphics.Rect;

public class ScreenButton extends ScreenObject{
	
	public static final int P_HEIGHT = 20; //%
	public static final int P_WIDTH = 20; //%


	public String name;
	public int pWidth = P_WIDTH;;
	public int pHeight = P_HEIGHT;
	public int id = 999;
	public boolean visable = true;
	public boolean touchable = true;
	public boolean enabled = true;
	public Rect area;
	public Runnable function;

	public ScreenButton(int id, int x, int y, int width, int height, String name, Resources res, Engine level, Runnable onClick ) {

		super( res, level );
		this.id = id;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.name = name;
		this.function = onClick;
		this.area = new Rect( x,y,x+width,y+height );
	}

	public ScreenButton(int id, Rect area , String name, Resources res, Engine level, Runnable onClick ) {

		super( res, level );
		this.id = id;
		this.x = area.left;
		this.y = area.top;
		this.width = area.width();
		this.height = area.height();
		this.name = name;
		this.function = onClick;
		this.area = area;
	}
	
	public int getX(){
		return this.x;
	}
	public int getY(){
		return this.y;
	}
	public int getWidth(){
		return this.width;
	}
	public int getHeight(){
		return this.height;
	}
	public boolean isVisable(){
		return this.visable;
	}
	public boolean isTouchable(){
		return this.touchable;
	}
	public Rect getRect(){
		Rect r = new Rect(x,y,width,height);
		return r;
	}

	public void setX( int x ){
		this.x = x;
	}
	public void setY( int y ){
		this.y = y;
	}
	public void setWidth( int width ){
		this.width = width;
	}
	public void setHeight( int height ){
		this.height = height;
	}
	public void setVisable( boolean visable ){
		this.visable = visable;
	}
	public void setTouchable( boolean touchable){
		this.touchable = touchable;
	}

}
