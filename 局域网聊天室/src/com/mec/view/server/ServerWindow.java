package com.mec.view.server;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.mec.core.IFeedBackMsg;
import com.mec.core.server.ServerSocketAcceptThread;
import com.mec.view.AbstractWindow;

public class ServerWindow extends AbstractWindow implements IFeedBackMsg{
	private int serverStatus;
	private ServerSocketAcceptThread acceptThread;
	public static final int CLOSED = 0;
	public static final int OPEND = 1;
	
	public ServerWindow(String xmlPath) {
		super(xmlPath);
//		this.acceptThread = new ServerSocketAcceptThread();
		this.serverStatus = CLOSED;
	}

	@Override
	public void feedBackMsg(String msg) {
		JTextArea jla = (JTextArea)this.getComponent("taReceiveMsg");
		
		jla.setText(jla.getText()+ msg);
	}

	
	@Override
	public void onCreate() {
		final ServerWindow kwen = this;
		
		JButton jbtnStart = (JButton)this.getComponent("buttonStart");
		
		jbtnStart.addMouseListener(new MouseAdapter() {
		
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(kwen.serverStatus != CLOSED) {
					JOptionPane.showConfirmDialog(kwen.getJframe(), "�������ر�״̬������������");
					return;
				}
				int option = JOptionPane.showConfirmDialog(kwen.getJframe(), "��Ҫ����������");
				int port = new Integer(((JTextField)kwen.getComponent("tbPort")).getText());
				
				if(option == 0) {
					try {
						kwen.acceptThread = new ServerSocketAcceptThread(kwen);
						acceptThread.initServerSocketThread(port);
						
						acceptThread.start();
						kwen.serverStatus = OPEND;
						
						kwen.feedBackMsg("\n�ɹ�����������.\n\n");
					} catch (IOException e) {
						e.printStackTrace();
						kwen.feedBackMsg("�ɹ������������쳣" + e.getMessage());
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			}
			
			
		});
		
		JButton jbtnStop = (JButton)this.getComponent("buttonStop");
		jbtnStop.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					if(kwen.serverStatus != OPEND) {
						return;
					}
					int option = JOptionPane.showConfirmDialog(kwen.getJframe(), "��Ҫ�رշ�����");
					if(option == 0) {
						System.out.println("�رշ�����");
					kwen.acceptThread.shutdown();
					kwen.serverStatus = CLOSED;
					kwen.feedBackMsg("\n�ɹ��رշ�����.\n\n");
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
	}
}
		

		

