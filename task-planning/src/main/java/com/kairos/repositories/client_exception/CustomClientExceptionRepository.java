package com.kairos.repositories.client_exception;

import com.kairos.persistence.model.client_exception.ClientException;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * Created by prabjot on 15/11/17.
 */
public interface CustomClientExceptionRepository {

    List<ClientException> getExceptionBetweenTaskDates(long citizenId, Date taskStartTime, Date taskEndTime);

    boolean isExceptionExistBetweenDate(long citizenId,Date startTime,Date endTime,BigInteger clientExceptionId);

    boolean isExceptionExistBetweenDate(List<Long> citizenIds,Date startTime,Date endTime,List<BigInteger> clientExceptionId);

    boolean isExceptionTypeExistBetweenDate(List<Long> citizenId,Date startTime,Date endTime,BigInteger exceptionTypeId);

    long countSickExceptionsAfterDate(long citizenId,Date fromDate);

    ClientException getSickExceptionForDate(long citizenId, Date date);



}
