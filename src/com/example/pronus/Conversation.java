package com.example.pronus;

import java.util.ArrayList;

/*
 * La classe Conversation permette di definire un oggetto contenente i dati relativi ad
 * uan determinata conversazione con un utente in rubrica.
 */
public class Conversation {
	//tempo di arrivo ultimo messaggio
	private String timeOfLastSms;
	//nome utente associato a tale conversazione
	private String userName;
	//lista di messaggi
	private ArrayList<OneComment> message = new ArrayList<OneComment>();
	//contatore dei nuovi messaggi
	private int newMessageCounter;
	//immagine profilo
	private int profileImage;
	
	boolean isMine;
	
	public Conversation(String timeOfLastSms, String userName, String message,
			int newMessageCounter, int profileImage,boolean isMine) {
		
		this.timeOfLastSms = timeOfLastSms;
		
		this.userName = userName;
		
		this.message.add(new OneComment(isMine,message));
		
		this.newMessageCounter = newMessageCounter;
		
		this.profileImage=profileImage;
		
		this.isMine = isMine;
	
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
	
	public ArrayList<OneComment> getMessage() {
	
		return message;
	
	}
	
	public void setMessage(ArrayList<OneComment> message) {
	
		this.message = message;
	
	}
	
	public int getNewMessageCounter() {
	
		return newMessageCounter;
	
	}
	
	public void setNewMessageCounter(int newMessageCounter) {
		
		this.newMessageCounter = newMessageCounter;
	
	}
	
	public String getTextOfLastSms(){
		
		return message.get(message.size() - 1).getMessage();
	
	}
	
	public void addMessageToList(boolean isMine,String message){
		
		this.message.add(new OneComment(isMine,message));
		
	}
	
	
}
