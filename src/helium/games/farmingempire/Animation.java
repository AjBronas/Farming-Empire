package helium.games.farmingempire;



import javax.security.auth.Subject;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;

//New version of Animation doesnt change main to current image in images[]. Instead the LevelView
// just draws the current image in images[].

public class Animation {

	public int x = 0;
	public int y = 0;
	public int width = 50;
	public int height = 50;
	public boolean isAvailable = false;
	public Bitmap main;			//Levelview will always use this image[OUTDATED]
	public Bitmap images[];
	public int durations[];
	public boolean repeat = true;
	public int maxRepeatAmount = 0;
	public int repeatedAmount = 0;
	public int currentImage = 0;
	public int lastImage = 0;
	public int firstImage = 0;
	public boolean playing = false;
	public Handler handler = new Handler();

	public Animation( Resources res ){

		this.main = BitmapFactory.decodeResource(res, R.drawable.blank);
		this.isAvailable = false;
	}

	public Animation(Bitmap main,int x, int y, int width, int height){			//must use createAnimaiton()

		this.main = main;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.isAvailable = true;
	}

	private final Runnable nextImage = new Runnable() {
		@Override
		public void run() {
			
				if(currentImage < lastImage-1){
					//main = images[currentImage];
					handler.postDelayed(nextImage, durations[currentImage]);
					currentImage++;
					//Log.i("ANIMATION", "currentImage = " +currentImage);
				}else{
					if(repeat == true){
						if(maxRepeatAmount != 0){
							if(repeatedAmount < maxRepeatAmount){
								currentImage = firstImage;
								//main = images[currentImage];
								handler.postDelayed(nextImage, durations[currentImage]);
								currentImage++;
								repeatedAmount++;
							}else{
								stop();
							}
						}else{
							currentImage = firstImage;
							//main = images[currentImage];
							handler.postDelayed(nextImage, durations[currentImage]);
							currentImage++;
						}
						//Log.i("ANIMATION", "Repeat: " + repeatedAmount);
					}else{
						stop();
					}
				}
			
		}
	};

	public void createAnimation( Bitmap images[], int durations[], boolean repeat, int repeatTimes){

		this.images = images;
		this.durations = durations;
		this.repeat = repeat;
		this.maxRepeatAmount = repeatTimes;

	}
	public void start( int startAt, int endAt ){

		if(startAt == 0 && endAt == 0){

			currentImage = 0;
			firstImage = 0;
			lastImage = images.length;
			handler.post(nextImage);
			//Log.i("ANIMATION", "Start");

		}else{

			currentImage = startAt;
			firstImage = startAt;
			lastImage = endAt;
			handler.post(nextImage);
			//Log.i("ANIMATION", "Start" + startAt +"," + endAt);
		}
		playing = true;


	}

	public void stop(){
		try{
		currentImage = 0;
		main = images[0];
		repeatedAmount = 0;
		handler.removeCallbacks(nextImage);
		playing = false;
	}catch( NullPointerException e ){
		
	}
		//Log.i("ANIMATION", "Stop");
	}

	public void setImageTo( int imageInt ){

		if(imageInt < images.length){
			main = images[imageInt];
			currentImage = imageInt;
		}
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

	public void isAvailable( boolean available){

		this.isAvailable = available;
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
