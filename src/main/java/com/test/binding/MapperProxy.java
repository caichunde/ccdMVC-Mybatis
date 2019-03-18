package com.test.binding;

import com.test.session.Sqlsession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;

public class MapperProxy implements InvocationHandler {
	
	private Sqlsession sqlsession;

	public MapperProxy(Sqlsession sqlsession) {
		super();
		this.sqlsession = sqlsession;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Class<?>  returntype = method.getReturnType();
		//判断这个类是不是其子类
		if(Collection.class.isAssignableFrom(returntype)) {
			return sqlsession.selectList(method.getDeclaringClass().getName()+"."+method.getName(), args==null?null:args[0]);
		}else {
			return sqlsession.selectOne(method.getDeclaringClass().getName()+"."+method.getName(), args==null?null:args[0]);
		}
	}

}
