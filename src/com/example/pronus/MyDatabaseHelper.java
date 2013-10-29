package com.example.pronus;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {

	public MyDatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	private static final String DB_NAME = "database_pronus";
	private static final int DB_VERSION = 1;

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		// Creazione tabella contatti
		String sql = "";
		sql += "CREATE TABLE contatti (";
		sql += "id INTEGER PRIMARY KEY,";
		sql += "nome TEXT NOT NULL,";
		sql += "numero TEXT NOT NULL,";
		sql += "password TEXT,";
		sql += "email TEXT)";
		db.execSQL(sql);
		
		// Creazione tabella conversazioni
		sql = "";
		sql += "CREATE TABLE conversazioni (";
		sql += "id INTEGER PRIMARY KEY,";
		sql += "messaggio TEXT NOT NULL,";
		sql += "bool INTEGER NOT NULL,";
		sql += "nome_conversazione TEXT NOT NULL)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub	
	}
}