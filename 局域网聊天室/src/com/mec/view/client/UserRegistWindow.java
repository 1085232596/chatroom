package com.mec.view.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.mec.core.IFeedBackMsg;
import com.mec.core.client.ClienCore;
import com.mec.core.client.RequestCommand;
import com.mec.core.server.ResponseType;
import com.mec.uti.ProtocolData;
import com.mec.view.AbstractWindow;

public class UserRegistWindow extends AbstractWindow implements IFeedBackMsg{
	private ClienCore  clientCore;
	
	public UserRegistWindow(String xmlPath) {
		super(xmlPath);
	}
	
	public UserRegistWindow(String serverIp, int port) throws UnknownHostException, IOException {
		this("/com/mec/xmlPath/UserRegistWindow.xml");
		this.clientCore = ClienCore.getInstance();
		this.clientCore.connect(serverIp, port);
		this.clientCore.addFeedBacker(this);
	}

	
	private void registSubmit() {
		//1. ��ȡ�����ؼ���ֵ��
		//2. У������ؼ���ֵ�Ƿ���ȷ��
		//3. �����������У��ɹ����򴫵����ݸ�������
		JTextField tbUserName = (JTextField)this.getComponent("tbUserName");
		String userName = tbUserName.getText();
		//�û�������Ϊ�գ��ҳ��ȱ���С��30���ַ�
		if(userName.length() <= 0 || userName.length() > 30){
			JOptionPane.showMessageDialog(this.getJframe(),  "�û�������Ϊ�գ��ҳ��ȱ���С��30���ַ�");
			return;
		}
		
		JPasswordField tbUserPassword = (JPasswordField)this.getComponent("tbUserPassword");
		String userPassword = tbUserPassword.getText();
	
		if(userPassword.length() <= 0 || userPassword.length() > 20){
			JOptionPane.showMessageDialog(this.getJframe(), "���벻��Ϊ�գ��ҳ��ȱ���С��20���ַ�");
			return;
		}
		
		JPasswordField tbReUserPassword = (JPasswordField)this.getComponent("tbReUserPassword");
		String reUserPassword = tbReUserPassword.getText();
		if(!reUserPassword.equals(userPassword)){
			JOptionPane.showMessageDialog(this.getJframe(), "�����������ظ���һ��");
			return;
		}
		
		JRadioButton rdSexMale = (JRadioButton)this.getComponent("rdSexMale");
		String userSex = rdSexMale.isSelected() + "";
		
		JTextField tbPhoneNo = (JTextField)this.getComponent("tbPhoneNo");
		String phoneNo = tbPhoneNo.getText();
		if(phoneNo.length() > 12){
			JOptionPane.showMessageDialog(this.getJframe(), "�绰���볤�ȱ���С�ڵ���12");
			return;
		}
		
		ProtocolData request = new ProtocolData();
		
		request.setProtocolHead(RequestCommand.REGIST);
		request.addParameter("userName", userName);
		request.addParameter("userPassword", userPassword);
		request.addParameter("userSex", userSex);
		request.addParameter("userPhoneNo", phoneNo);
		
		try {
			this.clientCore.sendRequest(request + "");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onCreate() {
		final UserRegistWindow kwen = this;
		
		JButton jbtnSubmit = (JButton)this.getComponent("btnSubmit");
		jbtnSubmit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				registSubmit();
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
			}
		});
	}

	@Override
	public void feedBackMsg(String msg) throws UnsupportedEncodingException {
		ProtocolData response = new ProtocolData(msg);
		
		if(ResponseType.REGIST_DONE.equals(response.getProtocolHead())){
			String retCode = response.getParameter("retCode");
			String retMsg = response.getParameter("retMsg");
			
			if("000000".equals(retCode)){
				JOptionPane.showMessageDialog(this.getJframe(), "��ϲ����ע��ɹ�");
				this.getJframe().dispose(); //�ر�ע�ᴰ��
			}else {
				JOptionPane.showMessageDialog(this.getJframe(), retMsg);
			}
		}
	}

}
