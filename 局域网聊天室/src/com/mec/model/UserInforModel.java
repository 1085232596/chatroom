package com.mec.model;

public class UserInforModel {
	private String userName;
	private String userPassword;
	private boolean userSex;
	private String userPhoneNum;
	
	public UserInforModel() {
	}

	
	public UserInforModel(String userName, String userPassword, boolean userSex, String userPhoneNum) {
		this.userName = userName;
		this.userPassword = userPassword;
		this.userSex = userSex;
		this.userPhoneNum = userPhoneNum;
	}


	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public boolean getUserSex() {
		return userSex;
	}

	public void setUserSex(boolean userSex) {
		this.userSex = userSex;
	}

	public String getUserPhoneNum() {
		return userPhoneNum;
	}

	public void setUserPhoneNum(String userPhoneNum) {
		this.userPhoneNum = userPhoneNum;
	}

	@Override
	public String toString() {
		return "UserInforModel [userName=" + userName + ", userPassword=" + userPassword + ", userSex=" + userSex
				+ ", userPhoneNum=" + userPhoneNum + "]";
	}
	
	
}
