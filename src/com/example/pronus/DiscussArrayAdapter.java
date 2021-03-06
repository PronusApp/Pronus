package com.example.pronus;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DiscussArrayAdapter extends ArrayAdapter<OneComment> {

	private TextView message;
	private List<OneComment> listOfMessages = new ArrayList<OneComment>();
	private LinearLayout wrapper;
	public int numOfItem = 0;
	public int animationOn = 2;
	Animation anim;
	@Override
	public void add(OneComment object) {
		listOfMessages.add(object);
		super.add(object);
	}

	public DiscussArrayAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	public int getCount() {
		return this.listOfMessages.size();
	}

	public OneComment getItem(int index) {
		return this.listOfMessages.get(index);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.message, parent, false);
		}

		wrapper = (LinearLayout) row.findViewById(R.id.wrapper);

		OneComment coment = getItem(position);

		message = (TextView) row.findViewById(R.id.message);

		message.setText(coment.message);

		message.setBackgroundResource(coment.left ? R.drawable.rounded_corners_blue: R.drawable.rounded_corners);

		wrapper.setGravity(coment.left ? Gravity.LEFT : Gravity.RIGHT);
		
		return row;
	}

	public Bitmap decodeToBitmap(byte[] decodedByte) {
		return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
	}

}