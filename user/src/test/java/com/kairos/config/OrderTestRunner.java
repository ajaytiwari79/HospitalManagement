package com.kairos.config;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by prabjot on 21/12/17.
 */
public class OrderTestRunner extends SpringJUnit4ClassRunner {

    /**
     * Construct a new {@code SpringJUnit4ClassRunner} and initialize a
     *  to provide Spring testing functionality to
     * standard JUnit tests.
     *
     * @param clazz the test class to be run
     * @see #createTestContextManager(Class)
     */
    public OrderTestRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        List<FrameworkMethod> list = super.computeTestMethods();
        List<FrameworkMethod> copy = new ArrayList<FrameworkMethod>(list);
        Collections.sort(copy, new Comparator<FrameworkMethod>() {
            @Override
            public int compare(FrameworkMethod f1, FrameworkMethod f2) {
                OrderTest o1 = f1.getAnnotation(OrderTest.class);
                OrderTest o2 = f2.getAnnotation(OrderTest.class);

                if (o1 == null || o2 == null)
                    return -1;

                return o1.order() - o2.order();
            }
        });
        return copy;
    }
}
