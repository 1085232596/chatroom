package com.mec.core.client;

import java.util.ArrayList;

public class ChatMsgCache {
	private String from;
	private ArrayList<String> chatMsgs;
	
	public ChatMsgCache(String from) {
		this.from = from;
		this.chatMsgs = new ArrayList<String>();
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public ArrayList<String> getChatMsgs() {
		return chatMsgs;
	}

	public void setChatMsgs(ArrayList<String> chatMsgs) {
		this.chatMsgs = chatMsgs;
	}
	
	
}
