package com.mec.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mec.model.UserInforModel;
import com.mec.uti.MecDatabase;

public class UserBaseInforDao {
	public static void insertUserBaseInfo(UserInforModel user) {
		
		String SQLString = "INSERT INTO user_base_info(userName, userPasswd,"
				+ " userSex, userPhoneNo)" + 
				" VALUES('" + user.getUserName()+"','" 
				+ user.getUserPassword() +"'," 
				+ ((user.getUserSex() == false) ? 0 : 1) + ",'"
				+ user.getUserPhoneNum()+"')";
		System.out.println(SQLString);
		MecDatabase md = new MecDatabase(SQLString);
		
		try {
			md.doUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		md.close();
	}
	
	public static UserInforModel selectUserBaseInfoByUserName(String user_name) {
		UserInforModel user = new UserInforModel();
		
//		SELECT  userName,userSex,userPasswd,userPhoneNo
//		FROM user_base_info
		String SQLString = "SELECT userName, userPasswd, userSex, userPhoneNo"
						+ " FROM user_base_info"
						+ " WHERE userName = '" + user_name + "'";
		System.out.println(SQLString);
		MecDatabase md = new MecDatabase(SQLString);
		
		try {
			ResultSet rs = md.doQuery();
			while(rs.next()) {
				
				user.setUserName(rs.getString("username"));
				user.setUserSex(rs.getBoolean("userSex"));
				user.setUserPassword(rs.getString("userPasswd"));
				user.setUserPhoneNum(rs.getString("userPhoneNo"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}
	
}











