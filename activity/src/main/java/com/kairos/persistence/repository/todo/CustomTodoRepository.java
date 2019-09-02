package com.kairos.persistence.repository.todo;

import com.kairos.dto.activity.todo.TodoDTO;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface CustomTodoRepository {

    List<TodoDTO> findAllByKpiFilter(Long unit, Date startDate, Date endDate, List<Long> staffIds, Collection<String> statuses);
}
