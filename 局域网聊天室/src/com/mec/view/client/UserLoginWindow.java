package com.mec.view.client;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.mec.core.IFeedBackMsg;
import com.mec.core.client.ClienCore;
import com.mec.core.client.RequestCommand;
import com.mec.core.server.ResponseType;
import com.mec.uti.ProtocolData;
import com.mec.view.AbstractWindow;

public class UserLoginWindow extends AbstractWindow implements IFeedBackMsg {
	private boolean isSetting;
	private GroupChatRoom chatWindow;
	private ClienCore clientCore;
	
	public UserLoginWindow(String xmlPath) {
		super(xmlPath);
		this.isSetting = false;
		this.clientCore = ClienCore.getInstance();
	}

	@Override
	public void onCreate() {
		final UserLoginWindow kwen = this;
		
		JLabel jlblTitle = (JLabel)kwen.getComponent("title");
		jlblTitle.setFont(new Font("微软雅黑",Font.BOLD,20));
		jlblTitle.setForeground(Color.BLUE);
		
		JButton jbtnSetting = (JButton)kwen.getComponent("buttonSetting");
		JButton jbtnRegist = (JButton)kwen.getComponent("buttonRegister");
		JButton jbtnLogin = (JButton)kwen.getComponent("buttonLogin");
		
		jbtnSetting.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("这是设置按钮");
				System.out.println("width = " + kwen.getJframe().getWidth() + "height = " + kwen.getJframe().getHeight());
				if(! kwen.isSetting) {
					kwen.getJframe().setSize(kwen.getJframe().getWidth(), 205);
					kwen.isSetting = true;
				}else {
					kwen.getJframe().setSize(kwen.getJframe().getWidth(), 260);
					kwen.isSetting = false;
				}
			}
		});
		
		jbtnRegist.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JTextField tbServerIp = (JTextField)kwen.getComponent("tbServerIp");
				String serverIp = tbServerIp.getText();
				System.out.println("这是注册按钮");
				UserRegistWindow registerWindow;
				try {
					registerWindow = new UserRegistWindow(serverIp, 80);
					registerWindow.show();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			
			}
		});
		
		jbtnLogin.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("点击了登录");
				JTextField userName = (JTextField)kwen.getComponent("textUser");
				JPasswordField userPassword = (JPasswordField)kwen.getComponent("textPassword");
				System.out.println("这是登录按钮");
				String user = userName.getText();
				String password = userPassword.getText();
				
				if(user.length() <= 0) {
					JOptionPane.showMessageDialog(kwen.getJframe(), "用户名不得为空");
					return;
				}
				
				if(password.length() < 0) {
					JOptionPane.showMessageDialog(kwen.getJframe(), "密码不得为空");
					return;
				}
				
				try {
					JTextField tbServerip = (JTextField)kwen.getComponent("tbServerIp");
					kwen.clientCore.connect(tbServerip.getText(), 80);
					kwen.clientCore.addFeedBacker(kwen);
					
					ProtocolData request = new ProtocolData();
					request.setProtocolHead(RequestCommand.LOGIN);
					request.addParameter("userName", user);
					request.addParameter("userPassword", password);
					
					kwen.clientCore.sendRequest(request + "");
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void feedBackMsg(String msg) throws UnsupportedEncodingException {
		ProtocolData response = new ProtocolData(msg);
		
		if(ResponseType.LOGIN_DONE.equals(response.getProtocolHead())){
			String retCode = response.getParameter("retCode");
			String retMsg = response.getParameter("retMsg");
			System.out.println("retCode = " + retCode + "retMsg = " + retMsg);
			if(!"000000".equals(retCode)){
				JOptionPane.showMessageDialog(this.getJframe(), retMsg);
				return;
			}
		chatWindow = new GroupChatRoom();
		chatWindow.show();
		this.getJframe().dispose();
		
	}
	}

}
