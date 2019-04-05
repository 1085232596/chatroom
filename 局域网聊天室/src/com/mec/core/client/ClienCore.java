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
	 * ����ģʽ����һ����Ψһ��ʵ�� ���ģʽ
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
			
			//�õ�������Ӧ���ݵ��߳�
			this.receiveThrea = new ReceiveResponseThread(this.inStream);
			this.receiveThrea.start();//�����������ݵ��߳�
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
	 * ˽�й��췽������������������ã���ʹ�ã��������޲ι��췽����Ϊprivate��
	 * �Ҹ���û��������public�Ĺ��췽��������getinstance
	 */
	private ClienCore() {
		this.connectStatus = false;
	}
}
