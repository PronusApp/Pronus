package com.example.pronus;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class myDatabaseHelperForConversation extends SQLiteOpenHelper {
	private static final String DB_NAME = "conversazioni_pronus";
	private static final int DB_VERSION = 1;

	public myDatabaseHelperForConversation(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "";
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