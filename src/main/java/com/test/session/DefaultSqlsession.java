package com.test.session;

import com.test.binding.MapperProxy;
import com.test.config.Configuration;
import com.test.config.MappedStatement;
import com.test.excutor.DefaultExcutor;
import com.test.excutor.Excutor;

import java.lang.reflect.Proxy;
import java.util.List;

//对外提供查询接口，并且转发给excutor
public class DefaultSqlsession implements Sqlsession {
	
	private Configuration configuration;
	
	private Excutor excutor;

	public Configuration getConfiguration() {
		return configuration;
	}

	public DefaultSqlsession(Configuration configuration) {
		super();
		this.configuration = configuration;
		excutor = new DefaultExcutor(configuration);
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	@SuppressWarnings("unchecked")
	public <T> T selectOne(String statement, Object parameter) {
		List<Object> selectlist = this.selectList(statement, parameter);
		if(selectlist==null||selectlist.size()==0) {
			return null;
		}
		if(selectlist.size()==1) {
			return (T) selectlist.get(0);
		}else {
			throw new RuntimeException("to many result!");
		}
	}

	public <E> List<E> selectList(String statement, Object parameter) {
		MappedStatement ms = configuration.getMappedstatements().get(statement);
		return excutor.query(ms, parameter);
	}

	@SuppressWarnings("unchecked")
	public <T> T getMapper(Class<T> type) {
		MapperProxy mapperProxy = new MapperProxy(this);
		//动态的实现类
		return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[] {type}, mapperProxy);
	}

}
