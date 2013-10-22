package com.example.pronus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ConversationList extends Fragment{
	// list contains fragments to instantiate in the viewpager
	List<Fragment> convFragments = new Vector<Fragment>();
	// page adapter between fragment list and view pager
	public PagerAdapter mPagerAdapter = null;
	// view pager
	public static ViewPager mPager;

	public static Conversation currentlyConv = null;
	/*
	 * 
	 * inizializzo la lista degli sms
	 */
	public static Map<String,Conversation> smsList = new HashMap<String,Conversation>();

	static ListView mSmsList;

	private static String nome;

	private static String message;

	static ListOfConversationAdapter adapter;

	private static View view;

	public static MyDatabaseHelper mDatabaseHelper;

	public static myDatabaseHelperForConversation mDatabaseHelperForConversation;

	public static Rubrica contatti;

	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
		// fragment not when container null
		if (container == null) {
			return null;
		}

		// inflate view from layout
		view = (FrameLayout)inflater.inflate(R.layout.list_of_conversations,container,false);

		mSmsList = (ListView)view.findViewById(R.id.sms_list);

		mSmsList.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				View v =  mSmsList.getChildAt(arg2);
				nome = ((TextView)v.findViewById(R.id.userName)).getText().toString();
				message =((TextView)v.findViewById(R.id.smsMessage)).getText().toString();
				((TextView)v.findViewById(R.id.smsMessage)).setTypeface(null);
				((TextView)v.findViewById(R.id.smsMessage)).setTextColor(Color.parseColor("#000000"));
				((ImageView)v.findViewById(R.id.newSms)).setBackgroundResource(R.drawable.empty);
				// NOME O MAIL?
				Editor.setItems(nome,message);
				Editor.adapter = new DiscussArrayAdapter(Main.mainContext, R.layout.message);
				currentlyConv = smsList.get(nome);
				ArrayList<OneComment> list = currentlyConv.getMessage();
				for(OneComment s : list)
					if(s!=null){
						Editor.adapter.add(s);
						Editor.conversation.setAdapter(Editor.adapter);
					}


				Main.mPager.setCurrentItem(1,true);
			}


		});

		adapter = new ListOfConversationAdapter(Main.mainContext, R.layout.sms_preview);

		mSmsList.setAdapter(adapter);
		
		updateSmsList();

		return view;
	}

	private void updateSmsList() {
		int bool = 0;

		HashMap<String, String> mappaContatti;
		contatti = new Rubrica(Main.mainContentResolver);

		mDatabaseHelper = new MyDatabaseHelper(Main.mainContext);
		SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();

		mappaContatti = (HashMap<String, String>) contatti.getMapOfContacts();

		// Prendo l'insieme di nomi dei contatti
		Set<String> set = mappaContatti.keySet();

		for (String nome: set) {

			ContentValues values = new ContentValues();

			values.put("nome", nome);
			values.put("numero", contatti.getNumberByName(nome));

			long id = database.insert("contatti", null, values);

			if (id == -1)
				Log.i("Test","Errore nell'insert");
			else
				Log.i("Test", nome + " " + contatti.getNumberByName(nome));
		}   

		Log.i("Main","Creo mDatabaseHelperForConversation");


		mDatabaseHelperForConversation= new myDatabaseHelperForConversation(Main.mainContext);

		SQLiteDatabase databaseConversazioni = mDatabaseHelperForConversation.getReadableDatabase();

		String[] columns = {"nome_conversazione"};

		Cursor cursor = databaseConversazioni.query("conversazioni", columns, null, null, null, null, null);

		while(cursor.moveToNext()){
			
			String mail = cursor.getString(0);
			Log.i("ConversationList - Test","" + mail);
			if(!smsList.containsKey(mail)){

				Cursor cursorConv = getConversation(mail);

				Conversation tempConv = new Conversation("22:55",mail,null,1,R.drawable.demo_profile,true);
				
				

				while(cursorConv.moveToNext()){

					String messaggio = cursorConv.getString(0);

					if((bool = cursorConv.getInt(1)) == 1){

						tempConv.addMessageToList(true,messaggio);

					}else{

						tempConv.addMessageToList(false,messaggio);

					}
					
				}
				
				smsList.put(mail,tempConv);
			}

		}
		for(String s : smsList.keySet()){
			adapter.add(smsList.get(s));
		}
		
		SMSService.sendPublicKey();

	}

	public static void addNewSms(String timeOfLastSms,String userName,String sms, int numOfNewMessages,int profileImage, boolean isMine){
		boolean alreadyExists = false;
		if(ConversationList.smsList !=null){
			for(String p:ConversationList.smsList.keySet())
				if(p!=null && p.equals(userName))
					alreadyExists=true;
		}

		if(!alreadyExists){
			adapter.add(new Conversation(timeOfLastSms,userName,sms,numOfNewMessages,profileImage,true));
			ConversationList.smsList.put(userName,new Conversation(timeOfLastSms,userName,sms,numOfNewMessages,profileImage,true));

		}else{
			//prendo la conversazione relativa
			Conversation conv = ConversationList.smsList.get(userName);
			//aggiungo un nuovo meddaggio alla lista della conversazione
			conv.addMessageToList(isMine,sms);
			//mi prendo il numero di conversazioni nella listview di conversationlist
			int num_of_items = ConversationList.mSmsList.getCount();
			int i = 0;
			//controllo che sia un nuovo messaggio ricevuto
			if(isMine){
				//se � un messaggio ricevuto allora vado a cercare la conversazione nella lista
				//con lo scopo di aggiornare nella textview l'ultimo messaggio ricevuto
				while(i < num_of_items){
					View v =  ConversationList.mSmsList.getChildAt(i);
					if(((TextView)v.findViewById(R.id.userName)).getText().toString().equals(userName)){
						((TextView)v.findViewById(R.id.smsMessage)).setText(sms);
						((TextView)v.findViewById(R.id.smsMessage)).setTextColor(Color.parseColor("#33B5E5"));
						((TextView)v.findViewById(R.id.smsMessage)).setTypeface(Typeface.DEFAULT_BOLD);
						((ImageView)v.findViewById(R.id.newSms)).setBackgroundResource(R.drawable.new_sms);
					}
					i++;
				}}
		}
	}

	public Cursor getConversation(final String nome_conversazione) {

		SQLiteDatabase database = mDatabaseHelperForConversation.getReadableDatabase();

		String[] columns = {"messaggio","bool"};
		String selection = "nome_conversazione = ?" ;
		String[] selectionArgs = {nome_conversazione};
		//		String orderBy = "id DESC";

		// SELECT messaggio FROM conversazioni WHERE email = Valore(email) AND nome_conversazione = Valore(nome_conversazione);
		Cursor cursor = database.query("conversazioni", columns, selection, selectionArgs, null, null, null);

		// Per esaminare la conversazione con un preciso utente basta "scannerizzare"
		// il cursor ritornato con moveToNext() (finch� questo non � null)

		return cursor;
	}
}