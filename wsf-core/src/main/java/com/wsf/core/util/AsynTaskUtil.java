package com.wsf.core.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class AsynTaskUtil {

    private static ExecutorService executorService = Executors.newFixedThreadPool(3);

    /**
     * 提交异步任务
     * @param callable
     * @return
     */
    public static FutureTask<Object> submit(Callable<Object> callable) {
        FutureTask<Object> futureTask = new FutureTask<>(callable);
        executorService.submit(futureTask);
        return futureTask;
    }
}
