package com.example.pronus;

import java.util.HashMap;
import java.util.Map;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ContactActivity extends Activity {

	Context myCont;

	private Map<String, String> myRubrica = new HashMap<String, String>();

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.contact_list);

		this.setTitle("Seleziona un contatto");

		myCont = this.getBaseContext();

		//myRubrica = Main.contatti.getContacts();

		String[] names = myRubrica.values().toArray(new String[0]);

		final ListView list = (ListView)findViewById(R.id.list);

		ListAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,names);

		list.setAdapter(adapter);

		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView parent, View v, int position, long id){

				//chiudo l'activity
				Intent i = new Intent(myCont, ContactActivity.class);
				
			    i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			    
			    i.putExtra("finish",true);
			    
			    startActivity(i);
			    
			}
			
		});

		ImageButton exit = (ImageButton)findViewById(R.id.cancel);
		
		exit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {

				Intent i = new Intent(myCont, ContactActivity.class);
				
			    i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			    
			    i.putExtra("finish",true);
			    
			    startActivity(i);
			    
			}
		});		
	}

	protected void onNewIntent (Intent i){
		if( i.getBooleanExtra("finish",false) ){
			finish();
		}
	}
}
