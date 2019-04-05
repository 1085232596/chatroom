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
	private boolean isAlive;  //线程是否还活着
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
				String userSex = client.getUser().getUserSex() ? "男":"女";
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
		//初始化开始
		feedBacker.feedBackMsg("初始化开始....\n");
		this.serverSocket = new ServerSocket();
		this.serverSocket.setReuseAddress(true);
		
		feedBacker.feedBackMsg("获取本地地址开始...\n");
		String localAddress = InetAddress.getLocalHost().getHostAddress();
		feedBacker.feedBackMsg("或得到的地址是："+localAddress+"[OK]\n");
		//获取本地地址
		InetSocketAddress localAddr = new InetSocketAddress(
				InetAddress.getLocalHost().getHostAddress(),
				port);
		//绑定地址地址和端口
		feedBacker.feedBackMsg("绑定本地地址开始...\n");
		this.serverSocket.bind(localAddr);
		feedBacker.feedBackMsg("[OK]\n");
		
		feedBacker.feedBackMsg("设置服务器线程状态....\n");
		//初始化结束
		this.isInited = true;
		this.isAlive = true;
		feedBacker.feedBackMsg("[OK]\n");
		feedBacker.feedBackMsg("初始化成功！！！\n");
		
	}
	
	public void senMsgTo(String toClientId, String string2) {
		for(ClientServiceThread clienth : this.allThread) {
			if(clienth.getClientId().equals(toClientId)) {
				
			}
		}
	}
	
	/**
	 * 关闭所有与服务器建立连接的客户端
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
		feedBacker.feedBackMsg("开始关闭所有客户端连接...\n");
		this.closeAllClient(feedBacker);
		feedBacker.feedBackMsg("所有客户端连接已关闭...[OK]\n");
		
		feedBacker.feedBackMsg("关闭本地监听...[OK]\n");
		this.serverSocket.close();
		feedBacker.feedBackMsg("OK]\n");
		
		feedBacker.feedBackMsg("重置状态...\n");
		this.isAlive = false;
		this.isInited = false;
		feedBacker.feedBackMsg("OK]\n");
	}
	public void run() {
		while(this.isAlive) {
			try {
				Socket clientSocket = this.serverSocket.accept();
				
				this.feedBacker.feedBackMsg("一个新连接产生..." + clientSocket.getRemoteSocketAddress() + "\n");
				
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