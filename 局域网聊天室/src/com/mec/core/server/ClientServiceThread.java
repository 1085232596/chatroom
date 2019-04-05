package com.mec.core.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import com.mec.core.IFeedBackMsg;
import com.mec.core.client.RequestCommand;
import com.mec.model.UserInforModel;
import com.mec.service.UserInforService;
import com.mec.uti.ProtocolData;

public class ClientServiceThread extends Thread {
	private Socket clientSocket;
	private ServerSocketAcceptThread acceptThread;
	
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	
	private IFeedBackMsg feedBacker;
	private boolean isAlive;
	
	private UserInforModel user;
	private String ClientId;
	
	public String getClientId() {
		return ClientId;
	}
	
	public UserInforModel getUser() {
		return this.user;
	}

	public void close() throws IOException {
		isAlive = false;
		inputStream.close();
		outputStream.close();
		this.clientSocket.close();
	}
	
	/**
	 * 
	 * 命令名称?参数名=参数值...
	 * @param 处理客户端发来的请求
	 */
	private void procRequest(String requestStr) {
		try {
			HashMap<String, String> paramMap = new HashMap<String, String>();
			this.feedBacker.feedBackMsg("一个新的消息来到\n");
			
			ProtocolData request = new ProtocolData(requestStr);
			this.feedBacker.feedBackMsg(request.getProtocolHead());
			
			ProtocolData response = new ProtocolData();
			this.feedBacker.feedBackMsg("\n");
			if(RequestCommand.LOGIN.equals(request.getProtocolHead())) {
				String userName = request.getParameter("userName");
				String passWord = request.getParameter("userPassword");

				response.setProtocolHead(ResponseType.LOGIN_DONE);
				
				this.user = UserInforService.selectUserBaseInfoByUserName(userName);
					
				if(this.user == null) {
					response.addParameter("retCode", "LOG001");
					response.addParameter("retMsg", "用户名不存在");
					this.sendResponseToClient(response + "");
					return;
				}
				
				if(!user.getUserPassword().equals(passWord)) {
					response.addParameter("retCode", "LOG002");
					response.addParameter("retMsg", "密码输入有误");
					this.sendResponseToClient(response + "");
					this.user = null;
					return;
				}
				
				response.addParameter("retCode", "000000");
				response.addParameter("retMsg", "SUCCESS");
				
				this.ClientId = user.getUserName();
				this.sendResponseToClient(response + "");
				
				return;
			}
			
			if(RequestCommand.REGIST.equals(request.getProtocolHead())) {
				String userName = request.getParameter("userName");
				String userPassword = request.getParameter("userPassword");
				String userSex = request.getParameter("userSex");
				String phoneNo = request.getParameter("userPhoneNo");
				
				response = new ProtocolData();
				response.setProtocolHead(ResponseType.REGIST_DONE);
		
				UserInforModel user = new UserInforModel();
				user.setUserName(userName);
				user.setUserPassword(userPassword);
				user.setUserSex("true".equals(userSex));
				user.setUserPhoneNum(phoneNo);
				UserInforService.insertUserBaseInfo(user);
				
				response.addParameter("retCode", "000000");
				response.addParameter("retMsg", "SUCCESS");
					
				this.sendResponseToClient(response + "");
				return;
			}
			
			if(RequestCommand.GET_FRIEND_LIST.equals(request.getProtocolHead())) {
				//FRIEND_LIST?list=姓名-性别,姓名-性别
				response = new ProtocolData();
				response.setProtocolHead(ResponseType.FRIEND_LIST);
				String list = "";
				
				list = this.acceptThread.getFriendList();
				
				response.addParameter("list", list);
				this.sendResponseToClient(response + "");
				return;
			}
			
			//群发消息
			if(RequestCommand.SEND_TO_ALL.equals(request.getProtocolHead())) {
				String chatMsg = request.getParameter("chatMsg");
				
				ProtocolData groupResponse = new ProtocolData();
				groupResponse.setProtocolHead(ResponseType.MSG_FROM_GROUP);
				groupResponse.addParameter("from", this.user.getUserName());
				groupResponse.addParameter("chatMsg", chatMsg);
				
				this.acceptThread.sendGroupMsg(groupResponse + "");
			}
			
			//群发消息
			if(RequestCommand.SEND_MSG_TO.equals(request.getProtocolHead())) {
				String to = request.getParameter("to");
				String msg = request.getParameter("msg");
				String from = this.ClientId;
				response = new ProtocolData();
				
				response.setProtocolHead(ResponseType.MSG_FROM);
				response.addParameter("from", from);
				response.addParameter("msg", msg);

			}
		} catch(Exception e) {
			e.printStackTrace();
		} 
		
			
		
	}
	
	//向客户端发送响应数据
	public void sendResponseToClient(String responseStr) throws IOException {
		this.outputStream.writeUTF(responseStr);
	}
	
	private String receiveRequestFromClient() throws IOException {
		return this.inputStream.readUTF();
	}
	
	public void run() {
		try {
			while(this.isAlive) {
				/*1、读取数据 客户端的请求数据
				 * 2、调用procRequest处理请求数据
				 */
				String requestStr = this.receiveRequestFromClient();
				System.out.println(requestStr);
				this.procRequest(requestStr);
			}
		} catch (IOException e) {
			e.printStackTrace();
			try {
				this.close();
				this.acceptThread.removeOnClientTh(this);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public ClientServiceThread(Socket client,
			ServerSocketAcceptThread acceptThread,
			IFeedBackMsg feedBacker) throws Exception {
		this.clientSocket = client;
		this.acceptThread = acceptThread;
		this.isAlive = true;
		this.feedBacker = feedBacker;
		
		this.feedBacker.feedBackMsg("一个新客户端服务器线程产生\n");
		
		this.inputStream = 
				new DataInputStream(this.clientSocket.getInputStream());
		this.outputStream = 
				new DataOutputStream(this.clientSocket.getOutputStream());
	}
	
}
