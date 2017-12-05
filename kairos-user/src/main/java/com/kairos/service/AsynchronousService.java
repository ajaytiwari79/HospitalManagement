package com.kairos.service;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Service
public class AsynchronousService {
    @Autowired
    private ExecutorService executorService;

    public List<Future<T>> executeAsynchronously(List<Callable<T>> callables) throws InterruptedException {
        List<Future<T>> futures=executorService.invokeAll(callables);
        return futures;
    }

    public Future<T> executeAsynchronously(Callable<T> callables) throws InterruptedException {
        Future<T> futures=executorService.submit(callables);
        return futures;
    }
}
