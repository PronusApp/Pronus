package com.example.pronus;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class pickContact extends Activity {
	
	public Database database;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pick_contact);
		database = new Database(this);
		this.setTitle("Seleziona un contatto");
		final EditText newName = (EditText) findViewById(R.id.nome_new);
		final EditText newNumber = (EditText) findViewById(R.id.numero_new);
		final EditText newMail = (EditText) findViewById(R.id.mail_new);
		
		Button newContact = (Button)findViewById(R.id.new_contact);
		
		newContact.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Log.i("PICK_CONTACT","Devo salvare il contatto con i seguenti dati:");
				Log.i("PICK_CONTACT","Nome: " + newName.getText().toString());
				Log.i("PICK_CONTACT","Numero: " + newNumber.getText().toString());
				Log.i("PICK_CONTACT","Mail: " + newMail.getText().toString());

				if (database.addNewContact(newName.getText().toString(), newNumber.getText().toString(),newMail.getText().toString()))
					Log.i("PickContact", "Contatto aggiunto");

				ConversationList.addNewSms("22:55", newName.getText().toString(),"",1,R.drawable.demo_profile,true);
				finish();
			}
		});
	}
}
