package com.mec.uti;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.ButtonGroup;
import javax.swing.DefaultButtonModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.mec.view.AbstractWindow;

public class XMLWindowBuilder {
	
	private static void parseWindowHeadE(JFrame jframe, Element headE1) {
		if(headE1 == null) {
			return;
		}
		Element titleE = (Element)headE1.getElementsByTagName("title").item(0);
		if(titleE != null) {
			jframe.setTitle(titleE.getTextContent());
		}
		Element sizeE = (Element)headE1.getElementsByTagName("size").item(0);
		if(sizeE != null) {
			try{
				jframe.setSize(new Integer(sizeE.getAttribute("width")), new Integer(sizeE.getAttribute("height")));
			}catch(Exception e){
				jframe.setSize(400, 400);
			}
		}
		
		Element locationE = (Element)headE1.getElementsByTagName("location").item(0);
		if(locationE != null) {
			try{
			int x = new Integer(sizeE.getAttribute("x"));
			int y = new Integer(sizeE.getAttribute("y"));
			jframe.setLocation(x, y);
			}catch(Exception e){
				jframe.setLocation(100, 100);
			}
		}
		
		Element resizableE = (Element)headE1.getElementsByTagName("resizable").item(0);
		if(resizableE != null) {
			try{
			boolean flag = new Boolean(resizableE.getTextContent());
			jframe.setResizable(flag);
			}catch(Exception e){
				jframe.setResizable(false);
			}
		}
		
	}
		
	private static void parseBtn(final JFrame jframe,AbstractWindow window, NodeList btnList) {
		if(btnList == null) {
			return;
		}
		for(int i = 0; i < btnList.getLength(); i++) {
			JButton jbtn = new JButton();
			try {
			Element btnElement = (Element)btnList.item(i);
			parseCompent(jbtn,btnElement);
			String id = btnElement.getAttribute("id");
			if(id != null) {
				window.addComponent(id, jbtn);
			}
	
			}catch(Exception e) {
				jbtn.setBounds(10, 10, 60, 40);
				jbtn.setText("OK");
			}
			jframe.add(jbtn);
		}
	}
	private static void parseWindowBodyE(JFrame jframe,AbstractWindow window, Element bodyE) {
		if(bodyE == null) {
			return;
		}
		NodeList btnList = bodyE.getElementsByTagName("button");
		parseBtn(jframe,window, btnList);
		
		NodeList labelEList = bodyE.getElementsByTagName("label");
		parseLabelList(window, labelEList);
		
		NodeList textAreaList = bodyE.getElementsByTagName("textarea");
		parseTextAreaList(window, textAreaList);
		
		NodeList textfieldList = bodyE.getElementsByTagName("textfield");
		parseTextFieldList(window, textfieldList);
		
		NodeList passwordList = bodyE.getElementsByTagName("password");
		parseTextPasswordList(window, passwordList);
		
		NodeList buttonGroupList = bodyE.getElementsByTagName("buttonGroup");
		parseButtonGroupList(window, buttonGroupList);
		
		NodeList listList = bodyE.getElementsByTagName("list");
		parseListList(window, listList);
	}
	
	private static void parseListList(AbstractWindow window, NodeList listList) {
		if(listList == null) {
			return;
		}
		
		for(int i = 0; i < listList.getLength(); i++) {
			Element listE = (Element)listList.item(i);
			
			String autoScroll = listE.getAttribute("autoScroll");
			DefaultListModel listModel = new DefaultListModel();
			JList list = new JList(listModel);
			if("true".equals(autoScroll)) {
				JScrollPane jsclPane = new JScrollPane();
				parseCompent(jsclPane, listE);
				
				jsclPane.setViewportView(list);
				window.getJframe().add(jsclPane);
			}else {
				parseCompent(list, listE);
				window.getJframe().add(list);
			}
			
			NodeList listItemList = listE.getElementsByTagName("item");
			
			if(listItemList != null) {
				for(int j = 0; j < listItemList.getLength(); j++) {
					Element itemE = (Element)listItemList.item(j);
					String itemText = itemE.getTextContent();
					listModel.addElement(itemText);
				}
			}
			
			String id = listE.getAttribute("id");
			if(id != null){
				window.addComponent(id, list);
			}else{
				id = list.hashCode() + "";
				window.addComponent(id, list);
			}
		}
	}

	private static void parseButtonGroupList(AbstractWindow window, NodeList buttonGroupList) {
		if(buttonGroupList == null) {
			return;
		}
		for(int i = 0; i < buttonGroupList.getLength(); i++) {
			Element buttonGroupE = (Element)buttonGroupList.item(i);
			
			ButtonGroup buttonGroup = new ButtonGroup();
			NodeList radioButtonList = buttonGroupE.getElementsByTagName("radio");
			parseRadioButton(window, buttonGroup, radioButtonList);
			
		}
	}
	
	private static void parseRadioButton(AbstractWindow window, ButtonGroup buttonGroup, NodeList radioList) {
		if(radioList == null) {
			return;
		}
		
		for(int i = 0; i < radioList.getLength(); i++) {
			Element radioE = (Element)radioList.item(i);
			JRadioButton jrbutton = new JRadioButton();
			parseCompent(jrbutton, radioE);
			
			String id = radioE.getAttribute("id");
			if(id != null) {
				window.addComponent(id, jrbutton);
			}else {
				id = jrbutton.hashCode() + "";
				window.addComponent(id, jrbutton);
			}
			
			String selected = radioE.getAttribute("selected");
			jrbutton.setSelected("true".equals(selected));
			
			buttonGroup.add(jrbutton);
			
			window.getJframe().add(jrbutton);
		}
	}
	
	private static void parseTextPasswordList(AbstractWindow window, NodeList passwordList) {
		if(passwordList == null) {
			return;
		}
		for(int i = 0; i < passwordList.getLength(); i++) {
			Element passwordElement = (Element)passwordList.item(i);
			JPasswordField jpasswordField = new JPasswordField();
			parseCompent(jpasswordField,passwordElement);
			
			String id = passwordElement.getAttribute("id");
			if(id != null) {
				window.addComponent(id, jpasswordField);
			}else {
				id = jpasswordField.hashCode() + "";
				window.addComponent(id, jpasswordField);
			}
			window.getJframe().add(jpasswordField);
		}
	}
	
	private static void parseTextFieldList(AbstractWindow window, NodeList textfieldList) {
		if(textfieldList == null) {
			return;
		}
		for(int i = 0; i < textfieldList.getLength(); i++) {
			Element textFieldElement = (Element)textfieldList.item(i);
			JTextField jTextfield = new JTextField();
			parseCompent(jTextfield,textFieldElement);
			
			String id = textFieldElement.getAttribute("id");
			if(id != null) {
				window.addComponent(id, jTextfield);
			}else {
				id = jTextfield.hashCode() + "";
				window.addComponent(id, jTextfield);
			}
			window.getJframe().add(jTextfield);
		}
	}
	
	private static void parseTextAreaList(AbstractWindow window, NodeList textAreaList) {
		if(textAreaList == null) {
			return;
		}
		for(int i = 0; i < textAreaList.getLength(); i++) {
			Element textAreaElement = (Element)textAreaList.item(i);
			String autoScroll = textAreaElement.getAttribute("autoScroll");
		
			JTextArea jTextArea = new JTextArea();
			
			if(autoScroll.equals("true")) {
				JScrollPane jscPane = new JScrollPane();
				
				parseCompent(jscPane,textAreaElement);
				jscPane.setViewportView(jTextArea);
				window.getJframe().add(jscPane);
			}else {
				parseCompent(jTextArea,textAreaElement);
				window.getJframe().add(jTextArea);
			}

//			jTextArea.setEditable(false);
			String id = textAreaElement.getAttribute("id");
			if(id != null) {
				window.addComponent(id, jTextArea);
			}else {
				id = jTextArea.hashCode() + "";
				window.addComponent(id, jTextArea);
			}
		}
	}
	
	private static void parseLabelList(AbstractWindow window, NodeList labelEList) {
		if(labelEList == null) {
			return;
		}
		for(int i = 0; i < labelEList.getLength(); i++) {
			Element labelElement = (Element)labelEList.item(i);
			JLabel jlbl = new JLabel();
			
			parseCompent(jlbl,labelElement);
			
			String id = labelElement.getAttribute("id");
			if(id != null) {
				window.addComponent(id, jlbl);
			}else {
				id = jlbl.hashCode() + "";
				window.addComponent(id, jlbl);
			}
			window.getJframe().add(jlbl);
		}
	}
	@SuppressWarnings("unchecked")
	private static void parseCompent(JComponent jcmp, Element elemnt) {
		try{
			int width = new Integer(elemnt.getAttribute("width"));
			int height = new Integer(elemnt.getAttribute("height"));
			
			jcmp.setSize(width, height);
		}catch(NumberFormatException e){
			jcmp.setSize(60, 30);
		}
		
		try{
			int x = new Integer(elemnt.getAttribute("x"));
			int y = new Integer(elemnt.getAttribute("y"));
			
			jcmp.setLocation(x, y);
		}catch(NumberFormatException e){
			jcmp.setLocation(10, 10);
		}
		jcmp.setEnabled(!"false".equals(elemnt.getAttribute("Enable")));
		
		
		
		try {
			
			Class cmpcls = jcmp.getClass();
			Method setTextMethod;
			setTextMethod = cmpcls.getMethod("setText", String.class);
			if(setTextMethod != null) {
				String text = elemnt.getAttribute("text");
				setTextMethod.invoke(jcmp, text);
			}
			
		} catch (NoSuchMethodException e) {
			System.out.println("没找到"+e.getMessage()+"方法");
//			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	

	public static JFrame buildWindow(AbstractWindow window, String xmlFilePath) {
		//传进去一个xml文件 构造出来一个jframe，这是一个伟大的工具 利用xml创造窗口
		JFrame jframe = new JFrame();
		
		jframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jframe.getContentPane().setLayout(null);
		window.setJframe(jframe);
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		InputStream is = Class.class.getResourceAsStream(xmlFilePath);
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
//			Document document = db.parse(new File(xmlFilePath));
			Document document = db.parse(is);
			
			Element headE = (Element)document.getElementsByTagName("head").item(0);
			parseWindowHeadE(jframe, headE);
			
			Element bodyE = (Element)document.getElementsByTagName("body").item(0);
			parseWindowBodyE(jframe, window, bodyE);
			
		} catch (ParserConfigurationException e) {
//			e.printStackTrace();
		} catch (SAXException e) {
//			e.printStackTrace();
		} catch (IOException e) {
//			e.printStackTrace();
		}
		
		return jframe;
	}
}
