package com.mec.view.console.server;

import com.mec.core.IFeedBackMsg;
import com.mec.core.server.ServerSocketAcceptThread;

public class Server implements IFeedBackMsg {
	private ServerSocketAcceptThread acceptThread;
	
	@Override
	public void feedBackMsg(String msg) throws Exception {

	}
	
	public Server() {
		this.acceptThread = new ServerSocketAcceptThread(this);
	}

}
