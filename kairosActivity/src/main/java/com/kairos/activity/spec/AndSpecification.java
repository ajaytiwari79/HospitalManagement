package com.kairos.activity.spec;

/**
 * Created by prabjot on 24/11/17.
 */
public class AndSpecification<T> extends AbstractTaskSpecification<T> {

    private TaskSpecification<T> taskSpecification1;
    private TaskSpecification<T> taskSpecification2;

    public AndSpecification(TaskSpecification<T> taskSpecification1, TaskSpecification<T> taskSpecification2) {
        this.taskSpecification1 = taskSpecification1;
        this.taskSpecification2 = taskSpecification2;
    }

    @Override
    public boolean isSatisfied(T t) {
        return taskSpecification1.isSatisfied(t) && taskSpecification2.isSatisfied(t);
    }

}
