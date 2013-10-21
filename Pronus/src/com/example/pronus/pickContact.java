package com.example.pronus;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

public class pickContact extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pick_contact);
		this.setTitle("Seleziona un contatto");
		final AutoCompleteTextView autocomplete = (AutoCompleteTextView) findViewById(R.id.autocomplete);
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				this,
				android.R.layout.simple_dropdown_item_1line, 
				Main.contactNames
				);
		autocomplete.setAdapter(adapter);
		autocomplete.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

			}
		});
		Button confirm = (Button)findViewById(R.id.confirm);
		
		confirm.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				ConversationList.addNewSms("22:55",autocomplete.getText().toString(),"Hi man!\nWassup?I'm going to eat an apple today rofl",1,R.drawable.demo_profile);
				
				finish();
				
			}
			
		});
	}
}
