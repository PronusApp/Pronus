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
		
		// Rimozione del titolo e della barra e delle notifiche
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.intro);

		intro = new ImageView(this);
		intro = (ImageView) findViewById(R.id.introImage);
		intro.setVisibility(View.VISIBLE);

        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
	    intro.startAnimation(fadeInAnimation);
	    
	    // Lancio i due servizi che girano sempre in background
	    startService(new Intent(this, SMSService.class));
	    startService(new Intent(this, PasswordUpdater.class));
	    
		IntentLauncher launcher = new IntentLauncher();

		launcher.start();
	}
	
	// Attività che dorme per tot secondi e poi lancia l'activity main
	
	private class IntentLauncher extends Thread {
		
		@Override
		public void run() {

			try {
				// Sleeping
				Thread.sleep(SLEEP_TIME * 1000);
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
