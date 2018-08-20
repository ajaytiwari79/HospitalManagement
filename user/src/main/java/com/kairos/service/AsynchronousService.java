package com.kairos.service;

import com.kairos.persistence.model.agreement.wta.templates.RuleTemplateCategory;
import com.kairos.persistence.model.country.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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

    public <T> Future<T> executeAsynchronously(Callable<T> task) throws InterruptedException {
        return executorService.submit(task);
    }


}
