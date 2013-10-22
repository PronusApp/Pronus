package com.example.pronus;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ListOfConversationAdapter  extends ArrayAdapter<Conversation>{
	private TextView userName;
	private TextView timeOfLastSms;
	private TextView smsMessage;
	private TextView smsCounter;
	private List<Conversation> conversations = new ArrayList<Conversation>();
	
	public ListOfConversationAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}
	@Override
	public void add(Conversation object) {
		conversations.add(object);
		super.add(object);
	}
	public int getCount() {
		return this.conversations.size();
	}

	public Conversation getItem(int index) {
		return this.conversations.get(index);
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.sms_preview, parent, false);
		}
		Conversation conv = getItem(position);

		userName = (TextView) row.findViewById(R.id.userName);

		userName.setText(conv.getUserName());

		timeOfLastSms = (TextView) row.findViewById(R.id.timeOfLastSms);

		timeOfLastSms.setText(conv.getTimeOfLastSms());
		
		smsMessage = (TextView) row.findViewById(R.id.smsMessage);

		smsMessage.setText(conv.getTextOfLastSms());
		
		smsCounter  = ((TextView) row.findViewById(R.id.smsCounter));
		
		//qui si genera un errore
		//smsCounter.setText(conv.getNewMessageCounter());
		
		return row;
	}

	public Bitmap decodeToBitmap(byte[] decodedByte) {
		return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
	}
	public TextView getSmsCounter() {
		return smsCounter;
	}
	public void setSmsCounter(TextView smsCounter) {
		this.smsCounter = smsCounter;
	}


}
