package com.example.pronus;

import org.jivesoftware.smack.packet.Message;
import android.database.Cursor;
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
import android.widget.Toast;

public class Editor extends Fragment {
	
	private EditText message;
	static ListView conversation;
	static DiscussArrayAdapter adapter;
	private static String name;
	private static TextView userName;
	private View EditorView;
	public Database database;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		// Fragment not when container null
		if (container == null) {
			return null;
		}
		
		database = new Database(getActivity());
		// inflate view from layout
		EditorView = (FrameLayout)inflater.inflate(R.layout.editor, container, false);
		//lista per i messaggi
		conversation = (ListView)EditorView.findViewById(R.id.conversation);
		//button per l'invio di messaggi
		Button send = (Button)EditorView.findViewById(R.id.send);
		//edittext per la stesura del messaggio
		message = (EditText)EditorView.findViewById(R.id.messageText);
		//adapter per la listview conversation
		adapter = new DiscussArrayAdapter(getActivity(), R.layout.message);
		
		//Nome dell'utente con cui sto conversando
		userName=(TextView)EditorView.findViewById(R.id.userMail);
		
		conversation.setAdapter(adapter);

		send.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				String to = name + "/0123456789101";
				String text = message.getText().toString();
				
				//aggiungo un nuovo messaggio a una conversazione esistente
				if (text == null) {
					return;
				}

				if (database.addMessage(name, text, 0))
					Log.i("Editor", "Messaggio in uscita inviato a "+ name +" aggiunto al database");
		
				// Richiesta esplicita della chiave
				
				Message msg = new Message(to, Message.Type.normal);
				msg.setBody("IWannaYourKey");
				
				if (Login.connection != null) {	
					Login.connection.sendPacket(msg);
					Log.i("Editor","Richiesta della password inviata con successo");
				}
				
				try {
					Thread.currentThread().sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				Log.i("Editor","Sto cercando la password relativa a " + name);
				
				String[] columns = {"password"};
				String selection = "email = ?";
				String[] selectionArgs = {name};
				
				Cursor cursor = database.query("contatti", columns, selection, selectionArgs, null, null, null);
				
				if (cursor.moveToFirst() == false) {
					Log.i("Editor","Contatto non presente in rubrica");
					Toast.makeText(getActivity(), "Contatto non presente in rubrica", Toast.LENGTH_LONG).show();
					return;
				}
				
				String seed = cursor.getString(0);
				cursor.close();
		
				if (seed == null) {
					Toast.makeText(getActivity(), "Impossibile inviare il messaggio ora.\nPassword non disponibile.", Toast.LENGTH_LONG).show();
					return;
				}
					
				if (!sendMessageByInternet(text, to, seed)) {
					Toast.makeText(getActivity(), "Impossibile inviare il messaggio ora.\nControllare la propria connessione.", Toast.LENGTH_LONG).show();
					return;
				}
				
				//aggiungo all'adapter un nuovo messaggio del tipo OneComment
				adapter.add(new OneComment(false, message.getText().toString()));
				
				conversation.setAdapter(adapter);
				message.setText("");
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
	
	private boolean sendMessageByInternet(String text, String to, String seed) {
		String encrypt = "";
		
		try {
			encrypt = Decoder.encrypt(seed, text);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Log.i("Editor", "Invio messaggio " + encrypt + " a " + to);

		Message msg = new Message(to, Message.Type.chat);
		
		msg.setBody(encrypt);
		
		if (Login.connection != null) {	
			Login.connection.sendPacket(msg);
			conversation.setSelection(conversation.getAdapter().getCount()-1);
			Log.i("Editor", "Messaggio criptato inviato con successo");
			return true;
		}
		
		return false;
	}
}
