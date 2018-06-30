package com.planner.util.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorUtil {
    public static ExecutorService getExecutorService(){
        return Executors.newFixedThreadPool(10);
    }


}
