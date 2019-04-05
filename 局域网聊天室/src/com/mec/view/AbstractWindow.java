package com.mec.view;

import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JFrame;

import com.mec.uti.XMLWindowBuilder;

public abstract class AbstractWindow {
	private JFrame jframe;
	private HashMap<String, JComponent> cmpMap; 
	//¼üÖµ¶Ô  key-value
	private String windowXmlFilePath;
	
	public JFrame getJframe() {
		return this.jframe;
	}

	public void setJframe(JFrame jframe) {
		this.jframe = jframe;
	}

	public void addComponent(String id, JComponent cmp) {
		this.cmpMap.put(id, cmp);
//		this.jframe.add(cmp);
	}
	
	public JComponent getComponent(String id) {
		return this.cmpMap.get(id);
	}
	
	public abstract void onCreate();
	
	public void show(){
		this.jframe.setVisible(true);
	}
	
	public AbstractWindow(String xmlPath) {
		this.windowXmlFilePath = xmlPath;
		this.cmpMap = new HashMap<String, JComponent>();
		XMLWindowBuilder.buildWindow(this,this.windowXmlFilePath);
		this.onCreate();
	}
}
