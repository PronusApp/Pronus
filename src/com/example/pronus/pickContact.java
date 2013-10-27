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

public class pickContact extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pick_contact);
		this.setTitle("Seleziona un contatto");
		final EditText autocomplete = (EditText) findViewById(R.id.nome);
		final EditText newName = (EditText) findViewById(R.id.nome_new);
		final EditText newNumber = (EditText) findViewById(R.id.numero_new);
		final EditText newMail = (EditText) findViewById(R.id.mail_new);
		final EditText mail = (EditText) findViewById(R.id.mail);

		Button confirm = (Button)findViewById(R.id.confirm);

		confirm.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {

				if(addEMailByName(autocomplete.getText().toString(),mail.getText().toString()))
					Log.i("PickContact","Contatto aggiunto");

				ConversationList.addNewSms("22:55",mail.getText().toString(),"",1,R.drawable.demo_profile,true);

				finish();
			}
		});
		
		Button newContact = (Button)findViewById(R.id.new_contact);
		
		newContact.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {

				if(addNewContact(newName.getText().toString(), newNumber.getText().toString(),newMail.getText().toString()))
					Log.i("PickContact","Contatto aggiunto");

				ConversationList.addNewSms("22:55", newMail.getText().toString(),"",1,R.drawable.demo_profile,true);

				finish();

			}

		});
	}
	public boolean addEMailByName(final String nome, final String email) {
		SQLiteDatabase database = ConversationList.mDatabaseHelper.getReadableDatabase();

		ContentValues values = new ContentValues();

		if (!(email.substring(email.indexOf('@')).equals("@gmail.com")))
			return false;

		values.put("email", email);
		String whereClause = "nome = ?";
		String[] whereArgs = { nome };
		int r = database.update("contatti", values, whereClause, whereArgs);

		if (r == -1)
			return false;
		
		SMSService.sendPublicKey();
		return true;
	}
	
	public boolean addNewContact(String nome, String numero, String email) {

		SQLiteDatabase database = ConversationList.mDatabaseHelper.getReadableDatabase();

		ContentValues values = new ContentValues();

		if (!(email.substring(email.indexOf('@')).equals("@gmail.com")))
			return false;

		values.put("email", email);
		values.put("nome", nome);
		values.put("numero", numero);

		long id = database.insert("contatti", null, values);

		if (id == -1)
			return false;
		
		SMSService.sendPublicKey();
		return true;
	}
}
