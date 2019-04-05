package com.mec.core.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.mec.core.IFeedBackMsg;

public class ServerSocketAcceptThread extends Thread {
	private ServerSocket serverSocket;
	private boolean isInited;
	private boolean isAlive;  //�߳��Ƿ񻹻���
	private IFeedBackMsg feedBacker;
	private ArrayList<ClientServiceThread> allThread;
	
	public void sendGroupMsg(String msg) {
		boolean flag = true;
		for(int i = 0; i <  this.allThread.size(); i++) {
			if(this.allThread.get(i).getClientId() != null) {
				try {
					this.allThread.get(i).sendResponseToClient(msg);
				} catch (IOException e) {
					e.printStackTrace();
					flag = false;
				}
			}
		}
	}
	
	
	public String getFriendList() {
		String retList = "";
		
		for(int i = 0; i < this.allThread.size(); i++) {
			ClientServiceThread client = this.allThread.get(i);
			
			if(client.getClientId() != null) {
				String userName = client.getUser().getUserName();
				String userSex = client.getUser().getUserSex() ? "��":"Ů";
				retList = retList + userName + "-" + userSex + ",";
			}
		}
		
		if(retList.length() > 1) {
			retList = retList.substring(0, retList.length() - 1);
		}
		
		return retList;
	}
	
	public ServerSocketAcceptThread(IFeedBackMsg feedBacker) {
		this.isInited = false;
		this.isAlive = false;
		this.feedBacker = feedBacker;
		this.allThread = new ArrayList<ClientServiceThread>();
	}
	
	public void removeOnClientTh(ClientServiceThread clienth) {
		this.allThread.remove(clienth);
	}
	
	public void initServerSocketThread(int port) throws Exception {
		if(isInited == true) {
			return;
		}
		//��ʼ����ʼ
		feedBacker.feedBackMsg("��ʼ����ʼ....\n");
		this.serverSocket = new ServerSocket();
		this.serverSocket.setReuseAddress(true);
		
		feedBacker.feedBackMsg("��ȡ���ص�ַ��ʼ...\n");
		String localAddress = InetAddress.getLocalHost().getHostAddress();
		feedBacker.feedBackMsg("��õ��ĵ�ַ�ǣ�"+localAddress+"[OK]\n");
		//��ȡ���ص�ַ
		InetSocketAddress localAddr = new InetSocketAddress(
				InetAddress.getLocalHost().getHostAddress(),
				port);
		//�󶨵�ַ��ַ�Ͷ˿�
		feedBacker.feedBackMsg("�󶨱��ص�ַ��ʼ...\n");
		this.serverSocket.bind(localAddr);
		feedBacker.feedBackMsg("[OK]\n");
		
		feedBacker.feedBackMsg("���÷������߳�״̬....\n");
		//��ʼ������
		this.isInited = true;
		this.isAlive = true;
		feedBacker.feedBackMsg("[OK]\n");
		feedBacker.feedBackMsg("��ʼ���ɹ�������\n");
		
	}
	
	public void senMsgTo(String toClientId, String string2) {
		for(ClientServiceThread clienth : this.allThread) {
			if(clienth.getClientId().equals(toClientId)) {
				
			}
		}
	}
	
	/**
	 * �ر�������������������ӵĿͻ���
	 * @param feedBacker
	 * @throws IOException
	 */
	private void closeAllClient(IFeedBackMsg feedBacker) throws IOException {
		for(int index = 0; index < allThread.size(); index++) {
			this.allThread.get(index).close();
		}
		this.allThread.clear();
	}
	public void shutdown() throws Exception {
		feedBacker.feedBackMsg("��ʼ�ر����пͻ�������...\n");
		this.closeAllClient(feedBacker);
		feedBacker.feedBackMsg("���пͻ��������ѹر�...[OK]\n");
		
		feedBacker.feedBackMsg("�رձ��ؼ���...[OK]\n");
		this.serverSocket.close();
		feedBacker.feedBackMsg("OK]\n");
		
		feedBacker.feedBackMsg("����״̬...\n");
		this.isAlive = false;
		this.isInited = false;
		feedBacker.feedBackMsg("OK]\n");
	}
	public void run() {
		while(this.isAlive) {
			try {
				Socket clientSocket = this.serverSocket.accept();
				
				this.feedBacker.feedBackMsg("һ�������Ӳ���..." + clientSocket.getRemoteSocketAddress() + "\n");
				
				ClientServiceThread clientTh = 
						new ClientServiceThread(clientSocket, this, this.feedBacker);
				
				clientTh.start();
				this.allThread.add(clientTh);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}