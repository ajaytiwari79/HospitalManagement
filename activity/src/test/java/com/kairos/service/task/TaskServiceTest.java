package com.kairos.service.task;

import com.kairos.KairosActivityApplication;
import com.kairos.persistence.model.task.Task;
import com.kairos.service.task_type.TaskService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * Created by prabjot on 27/11/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = KairosActivityApplication.class,webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TaskServiceTest {

    @Autowired
    TaskService taskService;

    @Value("${spring.test.authorization}")
    String auth;

    /**
     * this test case doesn't any busy logic in application,
     * it just check rule_validator pattern rules
     */
    @Ignore
    @Test
    public void validateTask(){
        Task task = new Task();
        task.setTaskTypeId(new BigInteger("109920"));
     //   task.setExecutionDate(DateUtils.getCurrentDate());
        task.setPrefferedStaffIdsList(Arrays.asList(22L));

        taskService.validateTask(task);
    }
}
