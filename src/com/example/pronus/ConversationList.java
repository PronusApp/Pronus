package com.example.pronus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
	 * inizializzo la lista degli sms
	 */

	public static Map<String,Conversation> smsList = new HashMap<String,Conversation>();

	static ListView mSmsList;

	private static String nome;

	private static String message;

	static ListOfConversationAdapter adapter;

	private static View view;

	public Database database;

	private static TextView myMail, hint, test; 

	private static Button menu;

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {

		// fragment not when container null
		if (container == null) {
			return null;
		}

		database = new Database(getActivity());

		// inflate view from layout
		view = (FrameLayout)inflater.inflate(R.layout.list_of_conversations,container,false);

		mSmsList = (ListView)view.findViewById(R.id.sms_list);

		mSmsList.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				View v =  mSmsList.getChildAt(arg2);

				nome = ((TextView)v.findViewById(R.id.userName)).getText().toString();

				message =((TextView)v.findViewById(R.id.smsMessage)).getText().toString();

				((TextView)v.findViewById(R.id.smsMessage)).setTypeface(null);

				((TextView)v.findViewById(R.id.smsMessage)).setTextColor(Color.parseColor("#000000"));


				Editor.adapter = new DiscussArrayAdapter(getActivity(), R.layout.message);

				currentlyConv = smsList.get(nome);

				ArrayList<OneComment> list = currentlyConv.getMessage();

				for (OneComment s : list)

					if (s != null ){
						//controllo che esista effettivamente un messaggio all'interno dell'oggetto
						if(s.getMessage() != null && !(s.getMessage().equals(""))){
							Editor.adapter.add(s);
							Editor.conversation.setAdapter(Editor.adapter);
						}

						//imposto la lista di messaggi all'ultimo elemento

						Editor.conversation.setSelection(Editor.conversation.getAdapter().getCount()-1);

						// NOME O MAIL?

						Editor.setItems(nome);

						Editor.conversation.setSelection(Editor.conversation.getAdapter().getCount()-1);

						Main.mPager.setCurrentItem(1,true);
					}
			}
		});

		mSmsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, final View v, int index, long arg3) {
				// TODO Auto-generated method stub
				// 1. Instantiate an AlertDialog.Builder with its constructor
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

				// 2. Chain together various setter methods to set the dialog characteristics
				LayoutInflater inflater = getActivity().getLayoutInflater();
				builder.setView(inflater.inflate(R.layout.delete_conversation, null));

				builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						database.deleteConversation(((TextView)v.findViewById(R.id.userName)).toString());
					}
				});
				builder.setNegativeButton("CANCELLA", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User cancelled the dialog
					}
				});

				// 3. Get the AlertDialog from create()
				AlertDialog dialog = builder.create();
				dialog.show();
				return true;
			}
		}); 

		adapter = new ListOfConversationAdapter(getActivity(), R.layout.sms_preview);

		mSmsList.setAdapter(adapter);
		//salvo la mia mail nella textview associata

		menu = (Button)view.findViewById(R.id.menu);

		menu.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				LayoutInflater inflater = getActivity().getLayoutInflater();
				builder.setView(inflater.inflate(R.layout.main_menu, null));

				AlertDialog dialog = builder.create();
				dialog.show();

			}

		});

		myMail = (TextView)view.findViewById(R.id.myMail);

		hint = (TextView)view.findViewById(R.id.hint);

		myMail.setText(Main.mail);

		updateSmsList();

		return view;
	}

	private void updateSmsList() {
		int bool = 0;

		Database database = new Database(getActivity());

		// Prendo l'insieme di nomi dei contatti

		String[] columns = {"nome_conversazione"};

		Cursor cursor = database.query("conversazioni", columns, null, null, null, null, null);

		while (cursor.moveToNext()) {

			String mail = cursor.getString(0);
			Log.i("ConversationList", "" + mail);

			if (!smsList.containsKey(mail)) {

				Cursor cursorConv = database.getConversation(mail);

				Conversation tempConv = new Conversation("22:55", mail, null, 1, R.drawable.demo_profile,true);


				while (cursorConv.moveToNext()) {

					String messaggio = cursorConv.getString(0);

					if ((bool = cursorConv.getInt(1)) == 1) {
						tempConv.addMessageToList(true,messaggio);
					} else {
						tempConv.addMessageToList(false,messaggio);
					}
				}

				cursorConv.close();
				smsList.put(mail,tempConv);
			}
		}

		for(String s : smsList.keySet())
			adapter.add(smsList.get(s));

		cursor.close();
		database.sendPassword();
	}


	public static void addNewSms(String timeOfLastSms, String userName, String sms, int numOfNewMessages, int profileImage, boolean isMine){
		boolean alreadyExists = false;

		if(ConversationList.smsList !=null) {
			for(String p:ConversationList.smsList.keySet())
				if (p!=null && p.equals(userName))
					alreadyExists = true;
		}

		if (!alreadyExists) {
			adapter.add(new Conversation(timeOfLastSms,userName,sms,numOfNewMessages,profileImage,true));
			ConversationList.smsList.put(userName,new Conversation(timeOfLastSms,userName,sms,numOfNewMessages,profileImage,true));
		} else {
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
					}
					i++;
				}
			}
		}
	}
}
