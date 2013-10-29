package com.example.pronus;

import java.util.List;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

public class Main<MyDatabaseHelper> extends FragmentActivity {
	// list contains fragments to instantiate in the viewpager
	List<Fragment> fragments = new Vector<Fragment>();
	// page adapter between fragment list and view pager
	private PagerAdapter mPagerAdapter = null;
	// view pager
	public static CustomViewPager mPager;
	//main context
	//public static Context mainContext;

	public static Activity instance;

	public static String[] contactNames;

	public static ContentResolver mainContentResolver;
	
	public static String mail;


	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);    // Removes title bar
		setContentView(R.layout.activity_main);
	
		//acquisisco i dati passati alla classe main quando viene
		//lanciata come intent dalla classe Login
		
		Bundle extras = getIntent().getExtras();
		
		if (extras != null) {
		    mail = extras.getString("mail");
		}

		instance = this;
		mainContentResolver = getContentResolver();
		//mainContext = this.getBaseContext();

		// creating fragments and adding to list

		fragments.add(Fragment.instantiate(this, ConversationList.class.getName()));
		fragments.add(Fragment.instantiate(this, Editor.class.getName()));

		// creating adapter and linking to view pager

		this.mPagerAdapter = new PagerAdap(super.getSupportFragmentManager(),fragments);

		mPager = (CustomViewPager) super.findViewById(R.id.pager);
		mPager.setSwipeable(false);
		mPager.setAdapter(this.mPagerAdapter);
		mPager.setPageTransformer(true, new DepthPageTransformer());
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar

		MenuInflater inflater = getMenuInflater();

		inflater.inflate(R.menu.main_activity_actions, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return false;
	}

	public void startContactList() {
		Intent contact = new Intent(this, ContactActivity.class);
		startActivity(contact);
		overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
	}

	@Override
	public void onBackPressed() {
		if (mPager.getCurrentItem() == 0) {
			// If the user is currently looking at the first step, allow the system to handle the
			// Back button. This calls finish() on this activity and pops the back stack.
			super.onBackPressed();
		} else {
			// Otherwise, select the previous step.
			mPager.setCurrentItem(mPager.getCurrentItem() - 1);
			this.setTitle("Messaggi");
		}
	}
	public static class UIUpdater extends AsyncTask<String,String,String>{
		String name, message;
		@Override
		protected String doInBackground(String... arg0) {
			name = arg0[0];
			message = arg0[1];
			return null;
		} 
		
	    protected void onProgressUpdate(Integer... progress) {
	    	//
	    }

	    protected void onPostExecute(String result) {
	    	Log.i("UIUpdater","gonna update now");
			ConversationList.addNewSms("22:55", name, message,1,R.drawable.demo_profile,true);
	    }

	}
}
