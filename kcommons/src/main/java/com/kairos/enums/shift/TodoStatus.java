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
    PENDING("Pending"),VIEWED("Viewed"),DISAPPROVE("Disapproved"),APPROVE("Approved"),REQUESTED("Requested");

    private String value;
    TodoStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String toValue() {
        return value;
    }

    public static List<TodoStatus> getAllStatusExceptViewed() {
        return Stream.of(TodoStatus.values()).filter(todoStatus ->(!(todoStatus.equals(VIEWED)))).collect(Collectors.toList());
    }
}
