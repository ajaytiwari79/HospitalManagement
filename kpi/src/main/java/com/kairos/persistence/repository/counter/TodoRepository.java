package com.kairos.persistence.repository.counter;

import com.kairos.dto.activity.todo.TodoDTO;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Repository
public class TodoRepository {
    public List<TodoDTO> findAllByKpiFilter(Long unitId, Date startDate, Date endDate, List<Long> staffIds, Collection<String> todoStatus) {
        return null;
    }
}
