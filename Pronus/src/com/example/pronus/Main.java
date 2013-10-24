package com.example.pronus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class Main<MyDatabaseHelperForConversation> extends FragmentActivity{
	// list contains fragments to instantiate in the viewpager
	List<Fragment> fragments = new Vector<Fragment>();
	// page adapter between fragment list and view pager
	private PagerAdapter mPagerAdapter = null;
	// view pager
	public static CustomViewPager mPager;
	//main context
	public static Context mainContext;

	public static Activity instance;

	public static String[] contactNames;

	public static ContentResolver mainContentResolver;
	
	public static String mail;


	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		this.setTitle("Messaggi");
		
		//acquisisco i dati passati alla classe main quando viene
		//lanciata come intent dalla classe Login
		
		Bundle extras = getIntent().getExtras();
		
		if (extras != null) {
			
		    mail = extras.getString("mail");
		    
		}

		instance = this;

		mainContentResolver = getContentResolver();

		mainContext = this.getBaseContext();

		// creating fragments and adding to list

		fragments.add(Fragment.instantiate(this,ConversationList.class.getName()));

		fragments.add(Fragment.instantiate(this,Editor.class.getName()));

		// creating adapter and linking to view pager

		this.mPagerAdapter = new PagerAdap(super.getSupportFragmentManager(),fragments);

		mPager = (CustomViewPager) super.findViewById(R.id.pager);

		mPager.setSwipeable(false);

		mPager.setAdapter(this.mPagerAdapter);

		mPager.setPageTransformer(true, new DepthPageTransformer());

		getActionBar().setDisplayHomeAsUpEnabled(true);

		getActionBar().setHomeButtonEnabled(true);
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

		switch (item.getItemId()) {

		case R.id.action_search:

			startContactList();

			return true;
		case R.id.action_add:

			addNewContact();

			return true;
		case R.id.settings:

			launchSettings();

			return true;

		default:

			return super.onOptionsItemSelected(item);

		}

	}

	public void startContactList(){

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

	private void addNewContact(){

		Intent intent = new Intent(this,pickContact.class);

		startActivity(intent);
	}
	
	/*
	 * Il metodo selectItem permette di gestire le azioni dopo aver premuto
	 * un elemento del navigation drawer
	 */
	public void launchSettings() {
		//Qui verrˆ lanciata l'activity per le impostazioni
		Intent intent = new Intent(Main.mainContext, Impostazioni.class);
		
		startActivity(intent);

	}

}
