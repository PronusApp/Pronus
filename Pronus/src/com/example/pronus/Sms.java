package com.example.pronus;

public class Sms {
	private String message;
	private String timeOfLastSms;
	public Sms(String message, String timeOfLastSms){
		this.message=message;
		this.timeOfLastSms=timeOfLastSms;
	}
	public String getMessage(){
		return message;
	}
	public String getTimeOfLastSms(){
		return timeOfLastSms;
	}
}
