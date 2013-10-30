package com.example.pronus;

import org.jivesoftware.smack.packet.Message;

import android.app.ProgressDialog;
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
				
				//aggiungo un nuovo messaggio a una conversazione esistente
				ConversationList.addNewSms("22:55", name, message.getText().toString(),1,R.drawable.demo_profile,false);
				
				//aggiungo all'adapter un nuovo messaggio del tipo OneComment
				adapter.add(new OneComment(false, message.getText().toString()));
				
				String to = name + "/0123456789101"; // Destinatario
				String text = message.getText().toString(); // Testo da criptare
		
				if (database.addMessage(name, text, 0))
					Log.i("Editor", "Messaggio aggiunto al database");
	
				message.setText("");

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
					Log.i("Editor", "Nessuna password memorizzata per il contatto");
					
					// Richiesta esplicita della password al contatto
					
					Message msg = new Message(to, Message.Type.normal);
		
					msg.setBody("IWannaYourKey");
					
					if (Login.connection != null) {	
						Login.connection.sendPacket(msg);
						Log.i("Editor","Richiesta della password inviata con successo");
					}

					ProgressDialog pausingDialog = ProgressDialog.show(getActivity(), "Attendo la password...", "Aspetto la passowrd dal destinatario...");
					
					database.sendPassword();
					
					pausingDialog.dismiss();
					
					// Query per cercare la nuova password

					Cursor new_cursor = database.query("contatti", columns, selection, selectionArgs, null, null, null);
					
					if (new_cursor.moveToFirst() == false) {
						Log.i("Editor", "Password non presente per questo contatto");
						Toast.makeText(getActivity(), "Password non presente per questo contatto", Toast.LENGTH_LONG).show();
						database.sendPassword();
						return;
					}
									
					seed = new_cursor.getString(0);
					new_cursor.close();
				}
	
				String encrypt = "";
				
				
				try {
					if (seed == null) {
						Toast.makeText(getActivity(), "Impossibile ottenere la password.\nUso la password di default", Toast.LENGTH_LONG).show();
						Log.i("Editor", "Password non trovata, uso la password di default");
						seed = "ThisIsASecretKey";
					}
					Log.i("SMSService", "Sto cifrando con questa password" + seed);
					encrypt = Decoder.encrypt(seed, text);
				
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				database.sendPassword();
				
				Log.i("Editor", "Invio messaggio" + encrypt + " a " + to);
				
				// Creo un nuovo messaggio da inviare
				Message msg = new Message(to, Message.Type.chat);
				
				msg.setBody(encrypt);
				
				if (Login.connection != null) {	
					Login.connection.sendPacket(msg);
					conversation.setSelection(conversation.getAdapter().getCount()-1);
					Log.i("Editor", "Messaggio criptato inviato con successo");
				}
				
			}	
		});
		
		database.sendPassword();
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
}
