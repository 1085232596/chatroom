package com.mec.view.demo;

import com.mec.view.client.GroupChatRoom;
import com.mec.view.client.UserLoginWindow;

public class DemoLoginWindow {
	public static void main(String[] args) {
		UserLoginWindow lw = new UserLoginWindow("/com/mec/xmlPath/LoginWindow.xml");
		
		lw.show();
	}
}
