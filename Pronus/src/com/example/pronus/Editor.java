package com.example.pronus;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;

public class Editor extends Fragment{

	private EditText message;
	private static ListView conversation;
	static DiscussArrayAdapter adapter;

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
				adapter.animationOn = 1;
				adapter.add(new OneComment(false, message.getText().toString()));
				message.setText("");
				adapter.animationOn = 0;
			}
			
		});
		return view;
	}

	public static void setItems(String nome, String messaggio){

		Main.instance.setTitle(nome);

		//bubble.setText(messaggio);

	}

}
