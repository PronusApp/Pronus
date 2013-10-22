package com.example.pronus;



import org.jivesoftware.smack.packet.Message;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;

public class Editor extends Fragment{

	private EditText message;
	static ListView conversation;
	static DiscussArrayAdapter adapter;
	private static String name;

	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
		// fragment not when container null
		if (container == null) {
			return null;
		}
		// inflate view from layout
		View view = (FrameLayout)inflater.inflate(R.layout.editor,container,false);

		conversation = (ListView)view.findViewById(R.id.conversation);

		Button send = (Button)view.findViewById(R.id.send);

		message = (EditText)view.findViewById(R.id.messageText);

		adapter = new DiscussArrayAdapter(Main.mainContext, R.layout.message);

		conversation.setAdapter(adapter);

		message.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// If the event is a key-down event on the "enter" button
				if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
					// Perform action on key press
					adapter.numOfItem++;
					adapter.animationOn = 0;
					adapter.add(new OneComment(false, message.getText().toString()));
					message.setText("");
					return true;
				}
				return false;
			}
		});



		send.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				adapter.numOfItem++;
				ConversationList.addNewSms("22:55",name, message.getText().toString(),1,R.drawable.demo_profile,false);
				adapter.add(new OneComment(false, message.getText().toString()));
				String to = name + "/0123456789101";
				String text = message.getText().toString();
				if(addMessage(name, text, 0))
					Log.i("PickContact - ","messaggio inviato aggiunto");
				message.setText("");
				Log.i("XMPPChatDemoActivity", "Sending text " + text + " to " + to);
				Message msg = new Message(to, Message.Type.chat);
				msg.setBody(text);				
				if (Login.connection != null) {
					Login.connection.sendPacket(msg);
				}

			}
		});
		return view;
	}

	public static void setItems(String nome, String messaggio){

		Main.instance.setTitle(nome);

		name = nome;

	}

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
