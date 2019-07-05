package com.kairos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

@Service
public class AsynchronousService {
    @Autowired
    private ExecutorService executorService;

    public <T> List<Future<T>> executeAsynchronously(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return executorService.invokeAll(tasks);
    }

    public <T> List<Future<T>> executeAsynchronously(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException {
        return executorService.invokeAll(tasks, timeout, unit);
    }

    public <T> Future<T> executeAsynchronously(Callable<T> task)  {
        return executorService.submit(task);
    }

    public void executeInBackGround(Runnable task) {
        executorService.execute(task);
    }

}
