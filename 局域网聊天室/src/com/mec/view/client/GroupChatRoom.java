package com.mec.view.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import com.mec.core.IFeedBackMsg;
import com.mec.core.client.ChatMsgCache;
import com.mec.core.client.ClienCore;
import com.mec.core.client.RequestCommand;
import com.mec.core.server.ResponseType;
import com.mec.uti.ProtocolData;
import com.mec.view.AbstractWindow;

public class GroupChatRoom extends AbstractWindow implements IFeedBackMsg {
	private UserLoginWindow loginWindow;
	private ClienCore clientCore;
	private HashMap<String, PrivateChatRoom> openedPrivChatView;
	private HashMap<String, ChatMsgCache> allChatMsgCache;
	
	public GroupChatRoom(String xmlPath) {
		super(xmlPath);
		this.clientCore = ClienCore.getInstance();
		this.clientCore.addFeedBacker(this);
		openedPrivChatView = new HashMap<String, PrivateChatRoom>();
		allChatMsgCache = new HashMap<String, ChatMsgCache>();
	}
	
	public GroupChatRoom() {
		this("/com/mec/xmlPath/GroupChatWindow.xml");
		
		//发送好友列表的请求
				try {
					ProtocolData request = new ProtocolData();
					request.setProtocolHead(RequestCommand.GET_FRIEND_LIST);
					this.clientCore.sendRequest(request + "");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
	}

	private void onBtnSendGroupMsgClick() {
	//获得要发送的消息，将消息发送给服务器AccordingTextArea
		JTextArea taGroupSendMsg = (JTextArea)this.getComponent("inputTextArea");
		String groupMsg = taGroupSendMsg.getText();
		
		if(groupMsg.length() <= 0) {
			JOptionPane.showMessageDialog(this.getJframe(), "请填写要发送的信息");
			return;
		}
		
		ProtocolData request = new ProtocolData();
		request.setProtocolHead(RequestCommand.SEND_TO_ALL);
		request.addParameter("chatMsg", groupMsg);
		
		try {
			this.clientCore.sendRequest(request + "");
			taGroupSendMsg.setText("");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onCreate() {
		final GroupChatRoom kwen = this;
		final JList peopleList = (JList)kwen.getComponent("listFriendList");
		JTextArea taGroupSendMsg = (JTextArea)this.getComponent("inputTextArea");
		JTextArea jtxeAreaAccording = (JTextArea)kwen.getComponent("AccordingTextArea");
		jtxeAreaAccording.setLineWrap(true);
		
		JButton jbtnSend = (JButton)kwen.getComponent("sendButton");
		JButton jbtnClear = (JButton)kwen.getComponent("exitClear");
		
		jbtnSend.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				onBtnSendGroupMsgClick();
//				String Message = jtxeAreaInput.getText();
//				
//				String sendMessage = "\n\t\t\t" + getNowTime() + ":\n" + "\t\t\t\t" + Message;
//				if(Message != null && Message.length() != 0) {
//					jtxeAreaAccording.setText(jtxeAreaAccording.getText()+sendMessage);
//				}
//				jtxeAreaInput.setText("");
			}
		});
		
		taGroupSendMsg.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == 10) {
					onBtnSendGroupMsgClick();
					taGroupSendMsg.setText("");
				}
				
			}
		});
		
		peopleList.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(arg0.getClickCount() == 2) {
					JOptionPane.showMessageDialog(kwen.getJframe(), peopleList.getSelectedValue());
					String privateName =  (String) peopleList.getSelectedValue();
					String getTime = getNowTime();

					String title = "局域网私聊(" + privateName + "聊天-时间： " + getTime + ")";
					PrivateChatRoom privateChattingRoom = new PrivateChatRoom("/com/mec/xmlPath"
							+ "/PrivateChatWindow.xml",title);
					
					privateChattingRoom.show();
					
				}
					
			}
		});
		
		jbtnClear.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				jtxeAreaAccording.setText("");
			}
		});
		
		
		JButton jbtnExit = (JButton)kwen.getComponent("exitButton");
		
		jbtnExit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int option = JOptionPane.showConfirmDialog(kwen.getJframe(), "确定注销吗？");
				if(option == 0) {
					try {
						kwen.clientCore.removeFeedBacker(kwen);
						kwen.clientCore.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					kwen.getJframe().dispose();
					loginWindow = new UserLoginWindow("/com/mec/xmlPath/LoginWindow.xml");
					loginWindow.show();
				}
			}
		});
		
		
		this.getJframe().addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosed(WindowEvent e) {
					try {
						kwen.clientCore.removeFeedBacker(kwen);
						kwen.clientCore.close();
						
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					kwen.getJframe().dispose();
			}
		});
	}
	
	private String getNowTime() {
		Calendar today = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String todayString = sdf.format(today.getTime());

		return todayString;
	}

	@Override
	public void feedBackMsg(String msg) throws Exception {
		ProtocolData response = new ProtocolData(msg);
		
		if(ResponseType.FRIEND_LIST.equals(response.getProtocolHead())) {
			String list = response.getParameter("list");
			
			JOptionPane.showMessageDialog(this.getJframe(), list);
			JList listFriendList = (JList)this.getComponent("listFriendList");
			DefaultListModel listModel = (DefaultListModel)listFriendList.getModel();
			
			String[] listArray = list.split(",");
			for(String friendInfor : listArray) {
				listModel.addElement(friendInfor);
			}
		}
		
		if(ResponseType.NEW_GUY_COME.equals(response.getProtocolHead())) {
			String userName = response.getParameter("userName");
			String userSex = response.getParameter("userSex");
			
			JList listFriendlist = (JList)this.getComponent("listFriendList");
			DefaultListModel listModel = (DefaultListModel)listFriendlist.getModel();
			
			listModel.addElement(userName + "-" + userSex);
		}
		
		//下线通知
		if(ResponseType.ONE_GUY_LEAVE.equals(response.getProtocolHead())) {
			String userName = response.getParameter("userName");
			
			JList listFriendList = (JList)this.getComponent("listFriendList");
			DefaultListModel listModel = (DefaultListModel)listFriendList.getModel();
			
			for(int i = 0; i < listModel.size(); i++) {
				String friendInfo = listModel.get(i).toString();
				int subIndex = friendInfo.lastIndexOf("-");
				String friendName = friendInfo.substring(0, subIndex);
				
				if(friendName.equals(userName)) {
					listModel.remove(i);
					break;
				}
			}
		}
		
		if(ResponseType.MSG_FROM_GROUP.equals(response.getProtocolHead())) {
		//接受网络上的数据，群消息 历史消息
			String fromUserName = response.getParameter("from");
			String chatMsg = response.getParameter("chatMsg");

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			JTextArea jtxeAreaAccording = (JTextArea)this.getComponent("AccordingTextArea");
			
			jtxeAreaAccording.setText(jtxeAreaAccording.getText() + 
					fromUserName + " (" + sdf.format(new Date()) + ")\n" 
					+ chatMsg + "\n\n");
		}
		
		if(ResponseType.MSG_FROM.equals(response.getProtocolHead())) {
			String from = response.getParameter("from");
			
			
			PrivateChatRoom pWindow = this.openedPrivChatView.get(response.getParameter("from"));
			
			if(pWindow != null) {
				return;
			}
			
			String fromMsg = response.getParameter("msg");
			ChatMsgCache fromCache = this.allChatMsgCache.get(from);
			if(fromCache == null) {
				fromCache = new ChatMsgCache(from);
				this.allChatMsgCache.put(from, fromCache);
			}
			fromCache.getChatMsgs().add(fromMsg);
		}
	}
	
}





















