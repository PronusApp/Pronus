package com.example.pronus;

import java.util.List;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;

public class Main extends FragmentActivity {
	// list contains fragments to instantiate in the viewpager
	List<Fragment> fragments = new Vector<Fragment>();
	// page adapter between fragment list and view pager
	private PagerAdapter mPagerAdapter = null;
	// view pager
	public static CustomViewPager mPager;

	public static Activity instance;

	public static String[] contactNames;

	public static ContentResolver mainContentResolver;

	public static String mail;

	private static int RESULT_LOAD_IMAGE = 1; 

	Uri myPicture = null;


	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);    // Removes title bar
		setContentView(R.layout.activity_main);


		new UIUpdater();

		//acquisisco i dati passati alla classe main quando viene
		//lanciata come intent dalla classe Login

		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			mail = extras.getString("mail");
		}

		instance = this;
		mainContentResolver = getContentResolver();

		// creating fragments and adding to list

		fragments.add(Fragment.instantiate(this, ConversationList.class.getName()));
		fragments.add(Fragment.instantiate(this, Editor.class.getName()));

		// creating adapter and linking to view pager

		this.mPagerAdapter = new PagerAdap(super.getSupportFragmentManager(),fragments);

		mPager = (CustomViewPager) super.findViewById(R.id.pager);
		mPager.setSwipeable(false);
		//ridefinisco il metodo setOnTouchListener in modo che l'utente
		//non possa scorrere il pager con le dita
		mPager.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return false;
			}

		});
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
		menu.add("Nuovo Contatto");
		menu.add("Nuova Conversazione");
		menu.add("Impostazioni");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			//addNewContact();
			return true;
		case 1:
			//startNewConversation();
			return true;
		case 2:
			//launchSettings();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
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

		@Override
		protected void onPostExecute(String result) {
			Log.i("UIUpdater","gonna update now");
			ConversationList.addNewSms("22:55", name, message,1,R.drawable.demo_profile,true);
		}
	}

	public void addNewContact(View v){
		Intent intent = new Intent(this, pickContact.class);
		startActivity(intent);
	}

	/*
	 * Il metodo selectItem permette di gestire le azioni dopo aver premuto
	 * un elemento del navigation drawer
	 */
	public void launchSettings(View v) {
		//Qui verr� lanciata l'activity per le impostazioni
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = this.getLayoutInflater();
		builder.setView(inflater.inflate(R.layout.impostazioni, null))
		// Add action buttons
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				//
			}
		})
		.setNegativeButton("Cancella", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				//
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	/*
	 * 
	 * Inizio una nuova conversazione:
	 * Lancio un dialog che permette all'utente di selezionare un contatto
	 * per una conversazione
	 */
	public void startNewConversation(final View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = this.getLayoutInflater();
		builder.setView(inflater.inflate(R.layout.new_conversation, null))
		// Add action buttons
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				ConversationList.addNewSms("22:55", v.findViewById(R.id.contactName).toString(),"",1,R.drawable.demo_profile,true);
			}
		})
		.setNegativeButton("Cancella", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				finish();
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}
/*
 * metodo per selezionare l'immagine profile
 */
	public void selectProfileImage(View v){

		Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

		startActivityForResult(i, RESULT_LOAD_IMAGE);
	}

	/*
	 * Metodo derivato da Activity, permette di gestire la foto selezionata dall'utente e caricarla
	 * nell'imageview foto profilo
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();

			ImageView imageView = (ImageView) findViewById(R.id.profileImage);
			imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

		}
	}
}
