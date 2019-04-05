package com.mec.uti;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class ProtocolData {
	
	/*请求命令或者响应类型?参数名称=参数值&参数名称....
	 * key-vlaue 放到hashmap里面去
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
	 * 从协议对象的参数集合，hashmap里面根据参数名称或者参数值
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
			//1. 获取 HashMap  this.parameters 的 所有的 key的集合
			//2. 遍历  this.parameters 的 key的集合
			Set<String> keys = this.parameters.keySet();  // 获取 key 的集合
			Iterator<String> keyI = keys.iterator(); // 获得集合 keys 的迭代器， Set 类如果需要被遍历，一定要使用其迭代器
			
			String paramStr = "";
			while(keyI.hasNext()){
				String name = keyI.next();
				String value = this.parameters.get(name);
				
				try {
					
					//URLEncoder.encode 方法，是将字符串转换成标准的 URL 字符形式
					//1. 根据字符编码格式先转换成字节；
					//2. 将特殊功能性符号以及非英语符号的字节的值转换成  %字节值 形式， 这个字节值以 16进制形式展现
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
















