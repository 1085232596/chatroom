package com.mec.service;

import com.mec.dao.UserBaseInforDao;
import com.mec.model.UserInforModel;

public class UserInforService {
	public UserInforService() {
		
	}
	
	public static void insertUserBaseInfo(UserInforModel user) {
		UserBaseInforDao.insertUserBaseInfo(user);
	}
	
	public static UserInforModel selectUserBaseInfoByUserName(String user_name) {
		UserInforModel user = UserBaseInforDao.selectUserBaseInfoByUserName(user_name);
		
		return user;
	}
}
