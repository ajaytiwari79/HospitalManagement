package com.kairos.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Service
public class AsynchronousService {
    @Inject
    @Qualifier("executorService")
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




}
