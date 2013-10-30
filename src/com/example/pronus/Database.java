package com.example.pronus;

import org.jivesoftware.smack.packet.Message;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Database {

	private MyDatabaseHelper helper;
	
	public Database(Context context) {
		// Creazione dell'helper; questo non deve mai morire
		helper = new MyDatabaseHelper(context);

	}

	public Cursor getConversation(final String nome_conversazione) {

		SQLiteDatabase database = helper.getReadableDatabase();

		String[] columns = {"messaggio","bool"};
		String selection = "nome_conversazione = ?" ;
		String[] selectionArgs = {nome_conversazione};

		// SELECT messaggio FROM conversazioni WHERE email = Valore(email) AND nome_conversazione = Valore(nome_conversazione);
		Cursor cursor = database.query("conversazioni", columns, selection, selectionArgs, null, null, null);

		// Per esaminare la conversazione con un preciso utente basta "scannerizzare"
		// il cursor ritornato con moveToNext() (finch� questo non � null)

		return cursor;
	}
	
	public boolean addMessage(final String nome_conversazione, final String messaggio, int bool) {

		ContentValues values = new ContentValues();

		values.put("nome_conversazione", nome_conversazione);
		values.put("bool", bool);
		values.put("messaggio", messaggio);

		SQLiteDatabase database = helper.getWritableDatabase();

		long id = database.insert("conversazioni", null, values);
		
		database.close();
		
		if (id == -1)
			return false;
		return true;
	}
	
	public boolean addNewContact(String nome, String numero, String email) {

		SQLiteDatabase database = helper.getWritableDatabase();

		ContentValues values = new ContentValues();

		if (!(email.substring(email.indexOf('@')).equals("@gmail.com")))
			return false;

		values.put("email", email);
		values.put("nome", nome);
		values.put("numero", numero);

		long id = database.insert("contatti", null, values);

		database.close();
		sendPassword();
		
		if (id == -1)
			return false;
		return true;
	}
	
	public boolean addEMailByName(final String nome, final String email) {
		SQLiteDatabase database = helper.getWritableDatabase();

		ContentValues values = new ContentValues();

		if (!(email.substring(email.indexOf('@')).equals("@gmail.com")))
			return false;

		values.put("email", email);
		String whereClause = "nome = ?";
		String[] whereArgs = { nome };
		int r = database.update("contatti", values, whereClause, whereArgs);

		
		database.close();
		sendPassword();
		
		if (r == -1)
			return false;
		return true;
	}
	
	public boolean addPassword(String public_key, String email) {

		SQLiteDatabase database = helper.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put("password", public_key);
		String whereClause = "email = ?";
		String[] whereArgs = {email};

		int id = database.update("contatti", values, whereClause, whereArgs);

		database.close();

		if (id == -1)
			return false;
		return true;
	}
	
	public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {

		SQLiteDatabase database = helper.getReadableDatabase();
		Cursor cursor = null;
		
		if ((selection == null) && (selectionArgs == null))
			cursor = database.query(table, columns, null, null, null, null, null);
		else if (selection == null)
			cursor = database.query(table, columns, null, selectionArgs, null, null, null);
		else if (selectionArgs == null)
			cursor = database.query(table, columns, selection, null, null, null, null);
		else 
			cursor = database.query(table, columns, selection, selectionArgs, null, null, null);
		
		return cursor;
	}
	
	public void sendPassword() {
		
		SQLiteDatabase database = helper.getReadableDatabase();

		String[] columns = {"email"};

		Cursor cursor = database.query("contatti", columns, null, null, null,null,null);

		while (cursor.moveToNext()) {
			String to = cursor.getString(0);

			if (to != null) {
				
				// Canale dedicato allo scambio di chiavi pubbliche
				Message msg = new Message(to, Message.Type.normal);
				msg.setBody(SMSService.seed);				

				if (Login.connection != null) 
					Login.connection.sendPacket(msg);

				Log.i("SMSService","Inviata password a " + to);
			}
		}
		
		cursor.close();
		database.close();
		Log.i("SMSService", "Inviata password a tutti i contatti");
	}
	
	public boolean deleteContactByEmail(String email) {
		SQLiteDatabase database = helper.getWritableDatabase();
		int result;
		
		String where = "email = ?";
		String[] whereArgs = {email};
		
		result = database.delete("contatti", where, whereArgs);
		
		database.close();
		
		if (result != -1)
			return true;
		return false;
	}
	
	public boolean deleteContactByName(String name) {
		SQLiteDatabase database = helper.getWritableDatabase();
		int result;
		
		String where = "nome = ?";
		String[] whereArgs = {name};
		
		result = database.delete("contatti", where, whereArgs);
		
		database.close();
		
		if (result != -1)
			return true;
		return false;
	}
	
	public boolean deleteConversation(String email) {
		SQLiteDatabase database = helper.getWritableDatabase();
		int result;
		
		String where = "email = ?";
		String[] whereArgs = {email};
		
		result = database.delete("conversazioni", where, whereArgs);
		
		database.close();
		
		if (result != -1)
			return true;
		return false;
	}
}
