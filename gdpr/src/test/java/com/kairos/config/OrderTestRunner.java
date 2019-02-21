package com.kairos.config;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prabjot on 21/12/17.
 */
class OrderTestRunner extends SpringJUnit4ClassRunner {

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
        List<FrameworkMethod> copy = new ArrayList<>(list);
        copy.sort((f1, f2) -> {
            OrderTest o1 = f1.getAnnotation(OrderTest.class);
            OrderTest o2 = f2.getAnnotation(OrderTest.class);

            if (o1 == null || o2 == null)
                return -1;

            return o1.order() - o2.order();
        });
        return copy;
    }
}
