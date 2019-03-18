package com.test;

import com.test.entity.user;
import com.test.mapper.UserMapper;
import com.test.session.Sqlsession;
import com.test.session.SqlsessionFactory;

public class TMain {

	public static void main(String[] args) {
		SqlsessionFactory sqlsessionFactory = new SqlsessionFactory();
		Sqlsession sqlsession = sqlsessionFactory.openSqlsession();
		System.out.println(sqlsession);
		UserMapper userMapper = sqlsession.getMapper(UserMapper.class);
		user yUser = userMapper.selectByName("XRog");
		System.out.println(yUser.getAddress());
	}

}
