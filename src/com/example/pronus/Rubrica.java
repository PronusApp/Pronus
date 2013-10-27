package com.example.pronus;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.content.ContentResolver;
import android.database.Cursor;

public class Rubrica {

	public Map<String, String> rubrica = new HashMap<String, String>();

	Vector<String> nome = new Vector<String>();
	Vector<String> numero = new Vector<String>();

	public Rubrica(ContentResolver context) {

		// Ottengo tutti i contatti

		Cursor cursor = context.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

		// Mi muovo per ogni contatto

		while (cursor.moveToNext()) {

			// Prendo l'ID del contatto
			String contactId = cursor.getString(cursor.getColumnIndex(Contacts._ID));

			// Controllo che il contatto abbia almeno un numero di telefono associato
			String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

			if (!Boolean.parseBoolean(hasPhone)) {

				// Può avere più numero di telefono associati!
				// Prendo il numero di cellulare associato all'ID preso precedentemente

				Cursor phones = context.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);

				while (phones.moveToNext()) {

					String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					String nome = phones.getString(phones.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));


					rubrica.put(nome, number);
					rubrica.put(number, nome);

					this.nome.add(nome);
					this.numero.add(number);

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

	public String getAllContacts() {
		String result = "";

		for (int i = 0; i < this.nome.size(); i++) 
			result += "\n" + nome.get(i) + " " + numero.get(i);

		return result;
	}

	public Map<String, String> getMapOfContacts() {
		return rubrica;
	}
}