package com.example.pronus;

public class DrawerSettings {
	private String settingName;
	private int photoRes;
	
	public DrawerSettings(String name,int photoRes){
		this.settingName = name;
		this.photoRes = photoRes;
	}
	
	public String getName(){
		return this.settingName;
	}
	
	public int getPhotoRes(){
		return this.photoRes;
	}
}
