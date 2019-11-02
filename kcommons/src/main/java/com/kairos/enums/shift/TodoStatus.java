package com.kairos.enums.shift;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by pradeep
 * Created at 13/6/19
 **/
public enum TodoStatus {
    PENDING,VIEWED,DISAPPROVE,APPROVE,REQUESTED;

    public static List<TodoStatus> getAllStatusExceptViewed() {
        return Stream.of(TodoStatus.values()).filter(todoStatus ->(!(todoStatus.equals(VIEWED)))).collect(Collectors.toList());
    }
}
