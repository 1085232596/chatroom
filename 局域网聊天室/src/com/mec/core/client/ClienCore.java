package com.mec.core.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.mec.core.IFeedBackMsg;

public class ClienCore {
	private Socket socket;
	private DataOutputStream outStream;
	private DataInputStream inStream;
	private boolean connectStatus;
	private ReceiveResponseThread receiveThrea;
	
	private static ClienCore clientCore;
	
	public void addFeedBacker(IFeedBackMsg feedBacker) {
		this.receiveThrea.addFeedBacker(feedBacker);
	}
	
	public void removeFeedBacker(IFeedBackMsg feedBacker) {
		this.receiveThrea.removeFeedBacker(feedBacker);
	}
	
	/**
	 * 单例模式，单一或者唯一的实例 设计模式
	 * @return
	 */
	public static ClienCore getInstance( ) {
		if(clientCore == null) {
			clientCore = new ClienCore();
		}
		
		return clientCore;
	}
	
	public void close() throws IOException {
		if(this.connectStatus && this.receiveThrea.isFeedBackerListEmpty()) {
			this.receiveThrea.shutdown();
			this.outStream.close();
			this.socket.close();
			this.socket.close();
			this.connectStatus = false;
			this.receiveThrea = null;
		}
	}
	
	public void connect(String ip, int port) throws UnknownHostException, IOException {
		if(false == this.connectStatus) {
			this.socket = new Socket(ip, port);
			
			this.outStream = new DataOutputStream(this.socket.getOutputStream());
			this.inStream = new DataInputStream(this.socket.getInputStream());
			
			//得到接受响应数据的线程
			this.receiveThrea = new ReceiveResponseThread(this.inStream);
			this.receiveThrea.start();//启动接受数据的线程
			this.connectStatus = true;
		}
	} 
	
	public void sendRequest(String request) throws IOException {
		this.outStream.writeUTF(request);
	}
	
	private ClienCore(String serverIP, int port) throws UnknownHostException, IOException {
		this.connectStatus = false;
		this.connect(serverIP, port);
	}
	
	/**
	 * 私有构造方法，不允许在外面调用，被使用，如果类的无参构造方法设为private，
	 * 且该类没有其他的public的构造方法，利用getinstance
	 */
	private ClienCore() {
		this.connectStatus = false;
	}
}
