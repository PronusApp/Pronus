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

	@Override
    protected void onPostExecute(String result) {
    	Log.i("UIUpdater","gonna update now");
		ConversationList.addNewSms("22:55", name, message,1,R.drawable.demo_profile,true);
		OneComment temp = new OneComment(true,message);
		//se l'utente sta chattando devo inserire nella conversazione il messaggio ricevuto
		Editor.adapter.add(temp);
		
		Editor.conversation.setAdapter(Editor.adapter);
		
		Editor.conversation.setSelection(Editor.conversation.getAdapter().getCount()-1);
		
    }

    @Override
    protected void onPreExecute() {
    }

}

