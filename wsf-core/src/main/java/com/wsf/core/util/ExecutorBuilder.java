package com.wsf.core.util;


import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ExecutorBuilder {

    public static Executor executorBuild(String nameFormat, boolean daemon){
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat(nameFormat).setDaemon(daemon).build();
        return Executors.newCachedThreadPool(threadFactory);
    }
}
