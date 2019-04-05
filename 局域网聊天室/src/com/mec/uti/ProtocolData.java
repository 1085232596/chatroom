package com.mec.uti;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class ProtocolData {
	
	/*�������������Ӧ����?��������=����ֵ&��������....
	 * key-vlaue �ŵ�hashmap����ȥ
	 * */
	private String protocolHead;
	private HashMap<String, String> parameters;
	
	public HashMap<String, String> getParameters() {
		return parameters;
	}
	
	public String getProtocolHead() {
		return protocolHead;
	}
	
	public void setProtocolHead(String protocolHead) {
		this.protocolHead = protocolHead;
	}

	public void addParameter(String name, String value) {
		this.parameters.put(name, value);
	}
	/**
	 * ��Э�����Ĳ������ϣ�hashmap������ݲ������ƻ��߲���ֵ
	 * @param name
	 * @return
	 */
	public String getParameter(String name) {
		return this.parameters.get(name);
	}
	
	public ProtocolData(String protocolStr) throws UnsupportedEncodingException {
		int wenHaoIndex = protocolStr.indexOf("?");
		
		if(wenHaoIndex < 0) {
			this.protocolHead = URLDecoder.decode(protocolStr, "UTF-8");
			return;
		}
		
		this.protocolHead = URLDecoder.decode(protocolStr.substring(0, wenHaoIndex));
		String protocolDataStr = protocolStr.substring(wenHaoIndex + 1);
		String[] paramArray = protocolDataStr.split("&");
		
		this.parameters = new HashMap<String, String>();
		
		for(String paramStr : paramArray){
			int equalIndex = paramStr.indexOf("=");
			if(equalIndex >= 0){
				String name = paramStr.substring(0, equalIndex);
				String value = paramStr.substring(equalIndex + 1);
				
				name = URLDecoder.decode(name, "UTF-8");
				value = URLDecoder.decode(value, "UTF-8");
				
				this.parameters.put(name, value);
			}
		}
	}
	
	public String toString() {
		String retStr = "";
		
		try {
			retStr = URLEncoder.encode(this.protocolHead,"UTF-8");
			//1. ��ȡ HashMap  this.parameters �� ���е� key�ļ���
			//2. ����  this.parameters �� key�ļ���
			Set<String> keys = this.parameters.keySet();  // ��ȡ key �ļ���
			Iterator<String> keyI = keys.iterator(); // ��ü��� keys �ĵ������� Set �������Ҫ��������һ��Ҫʹ���������
			
			String paramStr = "";
			while(keyI.hasNext()){
				String name = keyI.next();
				String value = this.parameters.get(name);
				
				try {
					
					//URLEncoder.encode �������ǽ��ַ���ת���ɱ�׼�� URL �ַ���ʽ
					//1. �����ַ������ʽ��ת�����ֽڣ�
					//2. �����⹦���Է����Լ���Ӣ����ŵ��ֽڵ�ֵת����  %�ֽ�ֵ ��ʽ�� ����ֽ�ֵ�� 16������ʽչ��
					name = URLEncoder.encode(name, "UTF-8");
					value = URLEncoder.encode(value, "UTF-8");
					
					paramStr = paramStr + (name + "=" + value + "&");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			
			if(paramStr.length() > 0){
				paramStr = paramStr.substring(0, paramStr.length() - 1);
				retStr = retStr + "?" + paramStr;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		
		return retStr;
	}
	public ProtocolData() {
		this.parameters = new HashMap<String, String>();
	}
	
//	public static void main(String[] args){
//		ProtocolData request = new ProtocolData();
//		
//		request.setProtocolHead("Hel?lo");
//		request.addParameter("userName", "wan gxh");
//		request.addParameter("password", "111&1 &?11");
//		
//		System.out.println(request + "");
//		
//		try {
//			ProtocolData nextData = new ProtocolData(request + "");
//			System.out.println("nextData.head=" + nextData.getProtocolHead());
//			System.out.println("nextData.userName=" + nextData.getParameter("userName"));
//			System.out.println("nextData.password=" + nextData.getParameter("password"));
//			
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		
//	}
	
}
















