package com.example.pronus;

import android.os.AsyncTask;
import android.util.Log;

public class UIUpdater extends AsyncTask<String,String,String>{
	String name, message;
	
	@Override
	protected String doInBackground(String... arg0) {
		name = arg0[0];
		message = arg0[1];
		return null;
	} 
<<<<<<< HEAD
	
	@Override
    protected void onPostExecute(String result) {
    	Log.i("UIUpdater","gonna update now");
		ConversationList.addNewSms("22:55", name, message,1,R.drawable.demo_profile,true);
    }
}
=======
    @Override
    protected void onPostExecute(String result) {     
    	Log.i("UIUpdater","gonna update now");
		ConversationList.addNewSms("22:55", name, message,1,R.drawable.demo_profile,true);
    }

    @Override
    protected void onPreExecute() {
    }

}
>>>>>>> refs/remotes/origin/master
