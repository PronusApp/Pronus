package com.example.pronus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Intro extends Activity {

	private static String TAG = Intro.class.getName();
	private static long SLEEP_TIME = 5;    // Sleep for some time

	private static ImageView intro;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);    // Removes title bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);    // Removes notification bar

		setContentView(R.layout.intro);

		intro = new ImageView(this);

		intro = (ImageView) findViewById(R.id.introImage);
		
		intro.setVisibility(View.VISIBLE);
		
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
		
	    intro.startAnimation(fadeInAnimation);
	    
	    startService(new Intent(this, SMSService.class));
		
		// Start timer and launch main activity
		IntentLauncher launcher = new IntentLauncher();

		launcher.start();
	}

	private class IntentLauncher extends Thread {

		/**
		 * Sleep for some time and than start new activity.
		 */
		
		@Override
		public void run() {

			try {
				// Sleeping
				Thread.sleep(SLEEP_TIME*1000);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}

			// Start main activity
			Intent intent = new Intent(Intro.this, Login.class);
			Intro.this.startActivity(intent);
			Intro.this.finish();
		}
	}
}
