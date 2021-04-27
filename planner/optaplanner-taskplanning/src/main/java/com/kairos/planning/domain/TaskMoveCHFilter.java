package com.kairos.planning.domain;

import com.kairos.planning.solution.TaskPlanningSolution;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.util.ArrayList;
import java.util.List;

/**
 * This is , as of now, for  only CH
 */

public class TaskMoveCHFilter implements SelectionFilter<TaskPlanningSolution, ChangeMove> {

    public static int attemts = 0;
    List<Long> taskList = new ArrayList<>(), prevTaskList = new ArrayList<>();
    {
        taskList.add(1716651l);
        taskList.add(1902857l);
        taskList.add(1789761l);
        prevTaskList.add(1718679l);
        prevTaskList.add(1926095l);
        prevTaskList.add(1781400l);
    }
    public  boolean accept(ScoreDirector<TaskPlanningSolution> scoreDirector, ChangeMove changeMove) {
        updateAttempt();
        boolean acceptable = true, matches = false;
        Task task = (Task) changeMove.getEntity();
        TaskOrEmployee prevTaskOrEmployee = (TaskOrEmployee) changeMove.getToPlanningValue();
        if (task != null && prevTaskOrEmployee != null && prevTaskOrEmployee instanceof Task) {
            matches = true;

            if (!task.getInitialStartTime1().isAfter(((Task) prevTaskOrEmployee).getInitialEndTime1())) {
                acceptable = false;
            }
        }
        if (matches && taskList.contains(task.getId()) && prevTaskList.contains(((Task) prevTaskOrEmployee).getId())) {
        }
        return acceptable;
    }

    private static void updateAttempt() {
        attemts++;
    }

}
