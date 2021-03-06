package com.example.pronus;

import android.os.AsyncTask;
import android.util.Log;

public class UIUpdater extends AsyncTask<String,String,String>{
	String name, message;
	Database database;
	String isMine;

	@Override
	protected String doInBackground(String... arg0) {
		name = arg0[0];
		message = arg0[1];
		isMine = arg0[2];
		return null;
	} 

	@Override
	protected void onPostExecute(String result) {
		if(isMine.equals("false")){
			Log.i("UIUpdater","Messaggio da aggiungere alle conversazioni: " + message +" da: " + name);
			ConversationList.addNewSms("22:55",name, message,1,R.drawable.demo_profile,true);

		}else{
			Log.i("UIUpdater","Messaggio da aggiungere alle conversazioni: " + message +" verso: " + name);
			
			ConversationList.addNewSms("22:55",name, message,1,R.drawable.demo_profile,false);
		}

	}

	@Override
	protected void onPreExecute() {

	}

}

