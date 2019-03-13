package com.wsf.consumer.test;

import com.wsf.core.domain.RpcContext;
import com.wsf.demo.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.StringUtils;

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
        AtomicInteger ao = new AtomicInteger(0);
        SimpleExecutor sim = new SimpleExecutor(() -> {
            int i = ao.incrementAndGet();
            String s = userService.sayHello("abc");
            if (StringUtils.isEmpty(s)) {
                System.out.println(s);
                new RuntimeException();
            }
        });
        sim.execute(40, 60);
    }

    @Test
    public void test_ansy() throws Exception {
        UserService userService = applicationContext.getBean("userService", UserService.class);
        userService.sayHello("abc");
        FutureTask<Object> futureTask = RpcContext.getFutureTask().get();
        System.out.println(futureTask.get(5, TimeUnit.SECONDS));
    }
}
