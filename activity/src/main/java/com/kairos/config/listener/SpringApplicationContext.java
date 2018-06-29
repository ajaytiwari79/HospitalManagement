package com.kairos.config.listener;


import org.springframework.context.ApplicationContext;

/**
 * Created by oodles on 29/1/17.
 */
public class SpringApplicationContext {
    private static ApplicationContext applicationContext;

    /**
     * Private constructor
     */
    private SpringApplicationContext() {
    }

    /**
     * Sets applicationContext
     *
     * @param context
     *            ApplicationContext
     */
    public static void set(ApplicationContext context) {
        applicationContext = context;
    }

    /**
     * Returns {@link ApplicationContext}
     *
     * @return ApplicationContext spring {@link ApplicationContext}
     */
    public static ApplicationContext get() {
        if (applicationContext == null) {
         //   throw "Spring application context is not initialised.";
        }
        return applicationContext;
    }
}
