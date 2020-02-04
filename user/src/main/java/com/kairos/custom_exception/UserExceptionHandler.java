package com.kairos.custom_exception;

import com.mindscapehq.raygun4java.core.RaygunClient;

public class UserExceptionHandler implements Thread.UncaughtExceptionHandler{
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        RaygunClient client = new RaygunClient("AxFRt3VvWVyRN2SLX9tnTg");
        client.send(e);
    }
}
