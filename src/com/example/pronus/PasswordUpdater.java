package com.example.pronus;

import java.util.Random;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class PasswordUpdater extends Service {
		
	Database database;
	
	@Override
	public void onCreate() {
		Log.i("PasswordUpdater", "PasswordUpdater creato");
		SMSService.seed = randomString();
		database = new Database(getBaseContext());
		Log.i("PassowordUpdater", "La nuova password è " + SMSService.seed);
	}

	@Override
	public void onStart(Intent intent, int startid) {
		Log.i("PasswordUpdater", "PasswordUpdater partito");
		
		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Log.i("PasswordUpdater", "Sono passati 60 secondi, aggiorno la password");
				SMSService.seed = randomString();
				database.sendPassword();
				Log.i("PassowordUpdater", "La nuova password: " + SMSService.seed);
			}
		};

		new Thread(new Runnable(){
			public void run() {
				while(true) {
					try {
						Thread.sleep(60000);
						handler.sendEmptyMessage(0);

					} catch (InterruptedException e) {
						e.printStackTrace();
					} 
				}
			}
		}).start();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	} 
	
	@Override
	public void onDestroy() {
		Toast.makeText(this, "PasswordUpdater fermato", Toast.LENGTH_LONG).show();
	}
	
	private String randomString() {
		char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
		StringBuilder sb = new StringBuilder();

		Random random = new Random();

		for (int i = 0; i < 16; i++) {
			char c = chars[random.nextInt(chars.length)];
			sb.append(c);
		}

		return sb.toString();
	}
}
