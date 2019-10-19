package com.kairos.dto.user.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.client_exception.ClientExceptionCount;
import com.kairos.enums.Gender;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * this wrapper will wraps basic info infor client(citizen) and their client exceptions.
 * this is designed for mobile view
 * Created by prabjot on 14/9/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ClientExceptionCountWrapper {

    private String firstName;
    private String lastName;
    private Long id;
    private Gender gender;
    private List<ClientExceptionCount> clientExceptionCounts;

}
