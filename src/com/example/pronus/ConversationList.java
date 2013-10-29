package com.example.pronus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import android.content.ContentValues;
import android.content.Intent;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ConversationList extends Fragment {

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

	private static Button add,newMessage,settings;

	private static TextView myMail, hint;

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


				Editor.adapter = new DiscussArrayAdapter(Main.mainContext, R.layout.message);

				currentlyConv = smsList.get(nome);

				ArrayList<OneComment> list = currentlyConv.getMessage();

				for(OneComment s : list)

					if (s != null){
						Editor.adapter.add(s);
						Editor.conversation.setAdapter(Editor.adapter);

						if(s!=null){

							Editor.adapter.add(s);

							Editor.conversation.setAdapter(Editor.adapter);

						}

						//imposto la lista di messaggi all'ultimo elemento

						Editor.conversation.setSelection(Editor.conversation.getAdapter().getCount()-1);

						// NOME O MAIL?

						Editor.setItems(nome);

						Main.mPager.setCurrentItem(1,true);

					}
			}
		});

		mSmsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View v,
					int index, long arg3) {
				// TODO Auto-generated method stub
				Log.i("ConversationList","in onLongClick");
				return true;
			}
		}); 

		adapter = new ListOfConversationAdapter(Main.mainContext, R.layout.sms_preview);

		mSmsList.setAdapter(adapter);
		//salvo la mia mail nella textview associata

		myMail = (TextView)view.findViewById(R.id.myMail);

		hint = (TextView)view.findViewById(R.id.hint);

		add = (Button)view.findViewById(R.id.addContact);

		newMessage = (Button)view.findViewById(R.id.newMessage);

		settings = (Button)view.findViewById(R.id.settings);

		myMail.setText(Main.mail);

		// Quando clicco su questa listview mi appariranno i bottoni per le varie impostazioni
		myMail.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				arg0.setVisibility(View.INVISIBLE);
				ConversationList.hint.setVisibility(View.INVISIBLE);
				ConversationList.add.setVisibility(View.VISIBLE);
				ConversationList.newMessage.setVisibility(View.VISIBLE);
				ConversationList.settings.setVisibility(View.VISIBLE);
				return false;
			}

		});

		add.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				arg0.setVisibility(View.INVISIBLE);
				ConversationList.myMail.setVisibility(View.VISIBLE);
				ConversationList.hint.setVisibility(View.VISIBLE);
				ConversationList.newMessage.setVisibility(View.INVISIBLE);
				ConversationList.settings.setVisibility(View.INVISIBLE);
				addNewContact();
			}

		});

		newMessage.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				arg0.setVisibility(View.INVISIBLE);
				ConversationList.myMail.setVisibility(View.VISIBLE);
				ConversationList.hint.setVisibility(View.VISIBLE);
				ConversationList.add.setVisibility(View.INVISIBLE);
				ConversationList.settings.setVisibility(View.INVISIBLE);
			}

		});

		settings.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				arg0.setVisibility(View.INVISIBLE);
				ConversationList.myMail.setVisibility(View.VISIBLE);
				ConversationList.hint.setVisibility(View.VISIBLE);
				ConversationList.add.setVisibility(View.INVISIBLE);
				ConversationList.newMessage.setVisibility(View.INVISIBLE);
				launchSettings();
			}

		});

		updateSmsList();

		return view;
	}

	private void updateSmsList() {
		int bool = 0;

		mDatabaseHelper = new MyDatabaseHelper(Main.mainContext);
		SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();

		// Prendo l'insieme di nomi dei contatti

		Log.i("Main","Creo mDatabaseHelperForConversation");

		mDatabaseHelperForConversation= new myDatabaseHelperForConversation(Main.mainContext);

		SQLiteDatabase databaseConversazioni = mDatabaseHelperForConversation.getReadableDatabase();

		String[] columns = {"nome_conversazione"};

		Cursor cursor = databaseConversazioni.query("conversazioni", columns, null, null, null, null, null);

		while (cursor.moveToNext()) {

			while(cursor.moveToNext()){
				String mail = cursor.getString(0);
				Log.i("ConversationList","" + mail);

				if (!smsList.containsKey(mail)){

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
			database.close();
		}
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

		database.close();
		return cursor;
	}

	private void addNewContact(){

		Intent intent = new Intent(Main.mainContext,pickContact.class);

		startActivity(intent);
	}

	/*
	 * Il metodo selectItem permette di gestire le azioni dopo aver premuto
	 * un elemento del navigation drawer
	 */
	public void launchSettings() {
		//Qui verr� lanciata l'activity per le impostazioni
		Intent intent = new Intent(Main.mainContext, Impostazioni.class);

		startActivity(intent);

	}

}