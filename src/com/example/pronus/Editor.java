package com.example.pronus;


import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;


import org.jivesoftware.smack.packet.Message;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

public class Editor extends Fragment {
	
	private EditText message;
	
	static ListView conversation;
	
	static DiscussArrayAdapter adapter;
	
	private static String name;
	
	private static TextView userName;
	
	private View EditorView;

	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
		// fragment not when container null
		
		if (container == null) {
			return null;
		}
		
		// inflate view from layout
		EditorView = (FrameLayout)inflater.inflate(R.layout.editor,container,false);
		//lista per i messaggi
		conversation = (ListView)EditorView.findViewById(R.id.conversation);
		//button per l'invio di messaggi
		Button send = (Button)EditorView.findViewById(R.id.send);
		//edittext per la stesura del messaggio
		message = (EditText)EditorView.findViewById(R.id.messageText);
		//adapter per la listview conversation
		adapter = new DiscussArrayAdapter(Main.mainContext, R.layout.message);
		
		//Nome dell'utente con cui sto conversando
		userName=(TextView)EditorView.findViewById(R.id.userMail);
		
		conversation.setAdapter(adapter);

		send.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				//aggiungo un nuovo messaggio a una conversazione esistente
				ConversationList.addNewSms("22:55",name, message.getText().toString(),1,R.drawable.demo_profile,false);
				//aggiungo all'adapter un nuovo messaggio del tipo OneComment
				adapter.add(new OneComment(false, message.getText().toString()));
				
				String to = name + "/0123456789101";
				
				// Questo è il testo da criptare con la chiave pubblica associata al contatto
				String text = message.getText().toString();
				//controllo che il messaggio sia stato inviato
				if (addMessage(name, text, 0))
					
					Log.i("Editor","Messaggio in uscita inviato a "+ name +" aggiunto al database");
				//pulisco l'edittext per l'invio del messaggio successivo
				message.setText("");
				
				SQLiteDatabase database = ConversationList.mDatabaseHelper.getReadableDatabase();
				
				String[] columns = {"password"};
				String selection = "email = ?";
				String[] selectionArgs = {name};
				
				Cursor cursor = database.query("contatti", columns, selection, selectionArgs, null,null,null);
				
				cursor.moveToFirst();
				
				// Ho ottenuto la chiave pubblica del destinatario sottoforma di stringa, 
				// devo convertirla in PublicKey
				String receiver_public_key_string = cursor.getString(0);
				
				/////
				
				String seed = "ThisIsASecretKey";

				String encrypt = "";
				
				try {
					encrypt = Decoder.encrypt(seed, text);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				Log.i("Editor", "Invio messaggio" + encrypt + " a " + to);
				
				// Creo un nuovo messaggio da inviare
				Message msg = new Message(to, Message.Type.chat);
				
				msg.setBody(encrypt);
				
				if (Login.connection != null) {	
					Login.connection.sendPacket(msg);
					Log.i("Editor","Messaggio criptato inviato con successo");
				}
			}	
		});
		
		return EditorView;
		
	}
	
	/*
	 * La funzione setItems permette di settare il titolo dell'activity con il nome
	 * della persona con cui si sta conversando
	 */
	public static void setItems(String nome){
		name = nome;
		userName.setText(name);
	}
	
	/*
	 * Il metodo addMessage permette di memorizzare il messaggio inviato nel database
	 * in modo da aggiornare lo storico di una determinata conversazione
	 */
	public boolean addMessage(final String nome_conversazione, final String messaggio, int bool) {

		ContentValues values = new ContentValues();

		values.put("nome_conversazione", nome_conversazione);
		values.put("bool", bool);
		values.put("messaggio", messaggio);

		SQLiteDatabase database = ConversationList.mDatabaseHelperForConversation.getWritableDatabase();

		long id = database.insert("conversazioni", null, values);

		if (id == -1)
			return false;
		return true;
	}
}
