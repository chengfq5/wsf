package com.wsf.consumer.test;

import com.wsf.core.domain.RpcContext;
import com.wsf.demo.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ConsumerTest {

    private ClassPathXmlApplicationContext applicationContext;

    @Before
    public void before() {
        applicationContext = new ClassPathXmlApplicationContext("wsf-consumer.xml");
    }

    @Test
    public void test() throws IOException {
        UserService userService = applicationContext.getBean("userService", UserService.class);
        //System.out.println(userService.existsMobileNo("18601720063"));
        //System.out.println(userService.existsMobileNo("18601720062"));
        AtomicInteger ao = new AtomicInteger(0);
        SimpleExecutor sim = new SimpleExecutor(() -> {
            int i = ao.incrementAndGet();
            boolean s = userService.existsMobileNo("18601720063");
            if (!s) {
                System.out.println(s);
                new RuntimeException();
            }
        });
        sim.execute(40, 60);
    }

    @Test
    public void test_ansy() throws Exception{
        UserService userService = applicationContext.getBean("userService", UserService.class);
        userService.existsMobileNo("18601720063");
        FutureTask<Object> futureTask = RpcContext.getFutureTask().get();
        System.out.println(futureTask.get(5, TimeUnit.SECONDS));
    }
}
