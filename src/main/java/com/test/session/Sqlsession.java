package com.test.session;

import java.util.List;

public interface Sqlsession {

	/**
	    *       根据传入条件，进行单一查询
	 * @param statement menthod 对应的source+id
	 * @param parameter 传入sql语句中的查询参数
	 * @return 返回指定结果集
	 */
	<T> T selectOne(String statement, Object parameter);


	/**
	 * 根据传入条件，进行多个查询
	 * @param statement
	 * @param object
	 * @return
	 */
	<E> List<E> selectList(String statement, Object parameter);
	
	<T> T getMapper(Class<T> type);
}
