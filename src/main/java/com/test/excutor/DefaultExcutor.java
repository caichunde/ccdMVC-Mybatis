package com.test.excutor;

import com.test.config.Configuration;
import com.test.config.MappedStatement;
import com.test.reflection.ReflectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DefaultExcutor implements Excutor {

	private Configuration configuration;

	public DefaultExcutor(Configuration configuration) {
		super();
		this.configuration = configuration;
	}

	public <E> List<E> query(MappedStatement ms, Object parameter) {
		List<E> ret = new ArrayList<E>();
		try {
			Class.forName(configuration.getJdbcDriver());

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = DriverManager.getConnection(configuration.getJdbcUrl(), configuration.getJdbcUsername(),
					configuration.getJdbcPassword());
			preparedStatement = connection.prepareStatement(ms.getSql());
			parameterized(preparedStatement, parameter);
			resultSet = preparedStatement.executeQuery();
			handlerResultSet(resultSet,ret,ms.getResultMap());
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				resultSet.close();
				preparedStatement.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	
	@SuppressWarnings("unchecked")
	private<E> void handlerResultSet(ResultSet resultSet, List<E> ret, String resultMap) {
		Class<E> clazz = null;
		try {
			clazz = (Class<E>) Class.forName(resultMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			while(resultSet.next()) {
				Object entity = clazz.newInstance();
				ReflectionUtil.setProToBeanFromResult(entity,resultSet);
				ret.add((E)entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parameterized(PreparedStatement preparedStatement, Object parameter) throws SQLException {
		if(parameter instanceof Integer) {
			preparedStatement.setInt(1, (Integer) parameter);
		}else if(parameter instanceof Long) {
			preparedStatement.setLong(1, (Long) parameter);
		}else if(parameter instanceof String) {
			preparedStatement.setString(1, (String) parameter);
			System.out.println(preparedStatement);
		}
	}

}
