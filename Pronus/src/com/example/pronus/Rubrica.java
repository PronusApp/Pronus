package com.example.pronus;

import java.util.HashMap;
import java.util.Map;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.content.ContentResolver;
import android.database.Cursor;

public class Rubrica {

	private Map<String, String> rubrica = new HashMap<String, String>();

	public int numOfElements = 0;
	public Rubrica(ContentResolver context) {
		
		Cursor cursor = context.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

		while (cursor.moveToNext()) {

			String contactId = cursor.getString(cursor.getColumnIndex(Contacts._ID));
			String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));


			if (!Boolean.parseBoolean(hasPhone)) {

				Cursor phones = context.query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null,
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID
						+ " = " + contactId, null, null);

				while (phones.moveToNext()) {
					String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					String nome=phones.getString(phones.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

					rubrica.put(nome, number);
					rubrica.put(number, nome);
					numOfElements++;
				}
				phones.close();
				
			}
		}
		cursor.close();
	}

	public String getNumberByName(final String name) {
		return rubrica.get(name);
	}
	
	public String getNameByNUmber(final String number) {
		return rubrica.get(number);
	}
	
	public Map<String, String> getContacts(){
		return rubrica;
	}
}


