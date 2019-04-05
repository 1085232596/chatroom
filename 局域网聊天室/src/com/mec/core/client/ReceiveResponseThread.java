package com.mec.core.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.mec.core.IFeedBackMsg;

/**
 * 接受服务器发来的响应，线程
 * @author Kwen
 *
 */
public class ReceiveResponseThread extends Thread {
	private DataInputStream inputStream;
	private boolean isAlive;
	private ArrayList<IFeedBackMsg> feedBackerList;
	
	public boolean isFeedBackerListEmpty() {
		return this.feedBackerList.isEmpty();
	}
	
	public int getFeedBackerCount() {
		return this.feedBackerList.size();
	}
	
	/**
	 * 处理服务器响应
	 * @param responseStr
	 * @throws Exception 
	 */
	private void processResponse(String responseStr) throws Exception {
		Exception retEx = null;
		System.out.println("responseStr：" + responseStr);
		for(int i = 0; i < this.feedBackerList.size(); i++) {
			try {
				this.feedBackerList.get(i).feedBackMsg(responseStr);
			} catch(Exception e) {
				retEx = e;
				e.printStackTrace();
			}
		}
		
		if(retEx != null) {
			throw retEx;
		}
	}
	
	public void run() {
		while(isAlive) {
			try {
				String responseStr = inputStream.readUTF();

				this.processResponse(responseStr);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void addFeedBacker(IFeedBackMsg feedBacker) {
		this.feedBackerList.add(feedBacker);
	}
	
	public void removeFeedBacker(IFeedBackMsg feedBacker) {
		this.feedBackerList.remove(feedBacker);
	}

	public void shutdown() throws IOException {
		this.isAlive = false;
		this.inputStream.close();
	}
	
	public ReceiveResponseThread(DataInputStream inStream) {
		this.inputStream = inStream;
		this.isAlive = true;
		this.feedBackerList = new ArrayList<IFeedBackMsg>();
		
	}
}
