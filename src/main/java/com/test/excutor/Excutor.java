package com.test.excutor;

import com.test.config.MappedStatement;

import java.util.List;

public interface Excutor {
	
	/**
	 * feng zhuang ms,chuan ru sql canshu
	 * @param ms
	 * @param parameter
	 * @return
	 */
	<E> List<E> query(MappedStatement ms, Object parameter);
}
