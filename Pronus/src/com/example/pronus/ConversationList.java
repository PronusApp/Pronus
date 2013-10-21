package com.example.pronus;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ConversationList extends Fragment{
	// list contains fragments to instantiate in the viewpager
	List<Fragment> convFragments = new Vector<Fragment>();
	// page adapter between fragment list and view pager
	public PagerAdapter mPagerAdapter = null;
	// view pager
	public static ViewPager mPager;
	
	/*
	 * 
	 * inizializzo la lista degli sms
	 */
	private static ArrayList<Conversation> smsList=new ArrayList<Conversation>();

	public static ListView mSmsList;

	private static String nome;

	private static String message;

	static ListOfConversationAdapter adapter;

	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
		// fragment not when container null
		if (container == null) {
			return null;
		}
		// inflate view from layout
		View view = (FrameLayout)inflater.inflate(R.layout.list_of_conversations,container,false);
		mSmsList = (ListView)view.findViewById(R.id.sms_list);
		mSmsList.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				View v =  mSmsList.getChildAt(arg2);
				nome = ((TextView)v.findViewById(R.id.userName)).getText().toString();
				message =((TextView)v.findViewById(R.id.smsMessage)).getText().toString();
				Editor.setItems(nome,message);
				Editor.adapter.numOfItem++;
				Editor.adapter.add(new OneComment(true, message));
				Main.mPager.setCurrentItem(1,true);

			}


		});

		smsList.add(null);
		
		adapter = new ListOfConversationAdapter(Main.mainContext, R.layout.sms_preview);

		mSmsList.setAdapter(adapter);
		
		return view;
	}

	public static void addNewSms(String timeOfLastSms,String userName,String sms, int numOfNewMessages,int profileImage){
		boolean alreadyExists = false;
		if(smsList !=null){
			for(Conversation p:smsList)
				if(p!=null && p.getUserName().equals(userName))
					alreadyExists=true;
		}

		if(!alreadyExists){
			
			adapter.add(new Conversation(timeOfLastSms,userName,sms,numOfNewMessages,profileImage));

		}else{

			Toast.makeText(Main.mainContext,"Conversazione gia esistente.",Toast.LENGTH_SHORT).show();

		}
	}
}