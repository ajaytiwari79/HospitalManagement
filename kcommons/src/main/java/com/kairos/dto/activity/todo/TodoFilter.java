package com.kairos.dto.activity.todo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TodoFilter {
    short daysLeft;
    LocalDate filterDate;
    String type;
}
