package com.example.pronus;

import android.widget.ImageView;

public class Conversation {
	private String timeOfLastSms;
	private String userName;
	private String message;
	private int newMessageCounter;
	private int profileImage;
	public Conversation(String timeOfLastSms, String userName, String message,
			int newMessageCounter, int profileImage) {
		super();
		this.timeOfLastSms = timeOfLastSms;
		this.userName = userName;
		this.message = message;
		this.newMessageCounter = newMessageCounter;
		this.profileImage=profileImage;
	}
	public int getProfileImage() {
		return profileImage;
	}
	public void setProfileImage(int profileImage) {
		this.profileImage = profileImage;
	}
	public String getTimeOfLastSms() {
		return timeOfLastSms;
	}
	public void setTimeOfLastSms(String timeOfLastSms) {
		this.timeOfLastSms = timeOfLastSms;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getNewMessageCounter() {
		return newMessageCounter;
	}
	public void setNewMessageCounter(int newMessageCounter) {
		this.newMessageCounter = newMessageCounter;
	}
	
}
