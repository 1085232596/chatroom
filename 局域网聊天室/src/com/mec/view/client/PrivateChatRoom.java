package com.mec.view.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JTextArea;

import com.mec.core.IFeedBackMsg;
import com.mec.core.client.ClienCore;
import com.mec.core.client.RequestCommand;
import com.mec.core.server.ResponseType;
import com.mec.uti.ProtocolData;
import com.mec.view.AbstractWindow;

public class PrivateChatRoom extends AbstractWindow implements IFeedBackMsg{
	private String friendName;
	private ClienCore clientCore;
	
	public PrivateChatRoom(String xmlPath, String value) {
		super(xmlPath);
		this.getJframe().setTitle(value);
		this.clientCore = ClienCore.getInstance();
		this.clientCore.addFeedBacker(this);
		this.friendName = value.substring(0, value.lastIndexOf("-"));
	}
	
	public PrivateChatRoom(String xmlPath) {
		super(xmlPath);
	}
	
	public void SetFriendName(String name) {
		this.friendName = name;
	}
	
	@Override
	public void onCreate() {
		final PrivateChatRoom kwen = this;
		JButton jbtnSendMsg = (JButton)this.getComponent("jbtnSend");
		
		jbtnSendMsg.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JTextArea jtetxInputing = (JTextArea)kwen.getComponent("inputing");
				String sendMsg = jtetxInputing.getText();
				ProtocolData request = new ProtocolData();
				request.setProtocolHead(RequestCommand.SEND_MSG_TO);
				request.addParameter("to", kwen.friendName);//
				request.addParameter("msg", sendMsg);
			}
		});
	 }

	@Override
	public void feedBackMsg(String msg) throws Exception {
		ProtocolData response = new ProtocolData(msg);
		if(response.getProtocolHead().equals(ResponseType.MSG_FROM)) {
			String message = response.getParameter("msg");
			String from = response.getParameter("from");
			JTextArea taReceive = (JTextArea) this.getComponent("taReceive");
			
			String textContent = taReceive.getText();
			textContent += "\n";
			textContent += (from + getNowTime() + "\n\t" + message);
			taReceive.setText(textContent);
			
		}
	}
	
	private String getNowTime() {
		Calendar today = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String todayString = sdf.format(today.getTime());

		return todayString;
	}


}
