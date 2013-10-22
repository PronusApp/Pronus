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
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class Main<MyDatabaseHelperForConversation> extends FragmentActivity{
	// list contains fragments to instantiate in the viewpager
	List<Fragment> fragments = new Vector<Fragment>();
	// page adapter between fragment list and view pager
	private PagerAdapter mPagerAdapter = null;
	// view pager
	public static CustomViewPager mPager;
	//main context
	public static Context mainContext;

	private DrawerLayout mDrawerLayout;

	private ListView mDrawerList;

	private ActionBarDrawerToggle mDrawerToggle;

	public static Activity instance;

	public static String[] contactNames;
	
	public static ContentResolver mainContentResolver;
	
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		this.setTitle("Messaggi");

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

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		ArrayList<DrawerSettings> settingList=new ArrayList<DrawerSettings>();

		DrawerSettings[] drawerSet ={

				new DrawerSettings("Impostazioni",R.drawable.dr_settings),
				new DrawerSettings("Account",R.drawable.demo_profile),
				new DrawerSettings("Condividi",R.drawable.dr_share),
		};

		for(int i = 0; i < drawerSet.length;i++)
			settingList.add(drawerSet[i]);

		ArrayList<HashMap<String, Object>> data=new ArrayList<HashMap<String,Object>>();
		//		String[] items = getResources().getStringArray(R.array.items);
		for(int i=0;i<settingList.size();i++){
			DrawerSettings p=settingList.get(i);// per ogni persona all'inteno della ditta

			HashMap<String,Object> personMap=new HashMap<String, Object>();//creiamo una mappa di valori

			personMap.put("image", p.getPhotoRes()); // per la chiave image, inseriamo la risorsa dell immagine
			personMap.put("name", p.getName()); // per la chiave name,l'informazine sul nome
			data.add(personMap);  //aggiungiamo la mappa di valori alla sorgente dati
		}

		String[] from={"image","name"}; //dai valori contenuti in queste chiavi
		int[] to={R.id.settingImage,R.id.name};//agli id delle vie
		// Set the adapter for the list view

		SimpleAdapter adapter=new SimpleAdapter(
				getApplicationContext(),
				data,//sorgente dati
				R.layout.settings_nav_drawer, //layout contenente gli id di "to"
				from,
				to);

		mDrawerList.setAdapter(adapter);

		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());


		mDrawerToggle = new ActionBarDrawerToggle(

				this,                  /* host Activity */

				mDrawerLayout,         /* DrawerLayout object */

				R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */

				R.string.drawer_open,  /* "open drawer" description */

				R.string.drawer_close  /* "close drawer" description */

				) {


			/** Called when a drawer has settled in a completely closed state. */

			public void onDrawerClosed(View view) {

				getActionBar().setTitle("Messaggi");

			}

			/** Called when a drawer has settled in a completely open state. */

			public void onDrawerOpened(View drawerView) {

				getActionBar().setTitle("Menu");

			}

		};
		
		
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		mPager.setPageTransformer(true, new DepthPageTransformer());

		getActionBar().setDisplayHomeAsUpEnabled(true);

		getActionBar().setHomeButtonEnabled(true);
	}
	@Override

	protected void onPostCreate(Bundle savedInstanceState) {

		super.onPostCreate(savedInstanceState);

		// Sync the toggle state after onRestoreInstanceState has occurred.

		mDrawerToggle.syncState();

	}

	@Override

	public void onConfigurationChanged(Configuration newConfig) {

		super.onConfigurationChanged(newConfig);

		mDrawerToggle.onConfigurationChanged(newConfig);

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

		if (mDrawerToggle.onOptionsItemSelected(item)) {

			return true;

		}
		switch (item.getItemId()) {

		case R.id.action_search:

			startContactList();

			return true;
		case R.id.action_add:
			
			addNewContact();

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

	private class DrawerItemClickListener implements ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			// Highlight the selected item, update the title, and close the drawer
			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(arg2, true);
			setTitle("......");

			String text= "menu click... should be implemented";
			Toast.makeText(Main.this, text , Toast.LENGTH_LONG).show();
			mDrawerLayout.closeDrawer(mDrawerList);

		}
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
}
