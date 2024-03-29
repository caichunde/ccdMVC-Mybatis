package com.test.service;

import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface HandlerAdapterService {
    Object[] handle(HttpServletRequest req, HttpServletResponse resp,
                           Method method, Map<String, Object> beans);
}

