package com.kairos.repositories.task_type;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by neuron on 19/5/17.
 */
public interface CustomTaskMongoTemplateRepository {

    List<BigInteger> updateTasksActiveStatusInBulk(List<BigInteger> taskIds, boolean makeActive);

}
