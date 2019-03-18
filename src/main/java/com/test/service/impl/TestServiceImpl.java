package com.test.service.impl;

import com.test.annotation.MyService;
import com.test.service.TestService;

@MyService("MyServiceImpl")
public class TestServiceImpl implements TestService {
    @Override
    public String test(String name) {
        return name;
    }
}
