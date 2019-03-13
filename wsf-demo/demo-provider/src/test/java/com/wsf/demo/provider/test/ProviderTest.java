package com.wsf.demo.provider.test;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class ProviderTest {

    private ClassPathXmlApplicationContext applicationContext;

    @Before
    public void before(){
        applicationContext = new ClassPathXmlApplicationContext("wsf-provider.xml");
    }

    @Test
    public void test() throws IOException {
        System.in.read();
    }
}
