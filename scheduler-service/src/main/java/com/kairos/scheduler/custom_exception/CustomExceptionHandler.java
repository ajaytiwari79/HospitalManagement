package com.kairos.scheduler.custom_exception;

import com.mindscapehq.raygun4java.core.RaygunClient;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class CustomExceptionHandler implements Thread.UncaughtExceptionHandler{

    @Inject
    private RaygunClient raygunClient;

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        raygunClient.send(e);
    }
}
