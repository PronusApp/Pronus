package com.example.pronus;

import android.os.AsyncTask;
import android.util.Log;

class UIUpdater  extends AsyncTask<String,String, String>{

	@Override
	protected String doInBackground(String... arg0) {
		Log.i("UIUpdater","Devo aggiornare l'UI di sistema");
		Log.i("UIUpdater","Nome " + arg0[0] );
		Log.i("UIUpdater","Messaggio " + arg0[1] );
		onProgressUpdate(arg0[0],arg0[1]);
		return null;
	}

	@Override
	protected void onPostExecute(String result) {

	}

	@Override
	protected void onPreExecute() {

	}

	@Override
	protected void onProgressUpdate(String... values) {
		ConversationList.adapter.add(new Conversation("22:55",values[0],values[1],1,R.drawable.demo_profile));
		ConversationList.adapter.notifyDataSetChanged();
	}

}
