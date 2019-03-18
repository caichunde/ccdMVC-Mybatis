package com.test.core.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.test.annotation.MyAutowired;
import com.test.annotation.MyController;
import com.test.annotation.MyRequestMapping;
import com.test.annotation.MyRequestParam;
import com.test.service.TestService;

@MyController
@MyRequestMapping("/test")
public class TestController {
	
    @MyAutowired("MyServiceImpl")
    private TestService testService;

    @MyRequestMapping("/doTest")
    public void test1(HttpServletRequest request, HttpServletResponse response,
    		@MyRequestParam("param") String param){
        String s = testService.test(param);
	    try {
            response.getWriter().write( "doTest method success! param:"+ s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	 
	 
	 @MyRequestMapping("/doTest2")
    public void test2(HttpServletRequest request, HttpServletResponse response){
        try {
            response.getWriter().println("doTest2 method success!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
