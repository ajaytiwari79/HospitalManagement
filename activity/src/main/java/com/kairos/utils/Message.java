package com.kairos.utils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class Message {
    private Date currentDate;
    private int id;
    private String message;

    public Message(String message) {
        this.message = message;
    }
}
