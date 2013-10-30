package com.example.pronus;

public class OneComment {
	public boolean left;
	public String message;

	public String getMessage() {
		if(message!=null){
			return message;
		}else{
			return null;
		}

	}

	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @param left
	 * @param comment
	 */
	public OneComment(boolean left, String comment) {
		super();
		this.left = left;
		this.message = comment;
	}

}