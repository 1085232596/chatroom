package com.mec.view.demo;

import com.mec.view.server.ServerWindow;

public class DemoServer {

	public static void main(String[] args) {
		String xmlPath = "/com/mec/xmlPath/ServerWindow.xml";

		ServerWindow window = new ServerWindow(xmlPath);
		window.show();
	}
}