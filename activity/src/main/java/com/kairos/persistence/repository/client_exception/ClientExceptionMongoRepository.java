package com.kairos.persistence.repository.client_exception;

import com.kairos.persistence.model.client_exception.ClientException;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * Created by oodles on 14/2/17.
 */
public interface ClientExceptionMongoRepository extends MongoBaseRepository<ClientException,BigInteger>,CustomClientExceptionRepository {

    @Override
    List<ClientException> findAll();

    @Query("{clientId:?0,unitId:?3,fromTime :{$gte:?1},toTime:{$lte:?2},isDeleted : false }")
    List<ClientException> getExceptionOfCitizenBetweenDates(Long clientId, Date startDate, Date endDate, long unitId);

    @Query("{fromTime :{$gte:?0},toTime:{$lte:?1},isDeleted : false }")
    List<ClientException> getExceptionBetweenDates(Date startDate, Date endDate, Sort sort);



    @Query("{clientId:?0,exceptionTypeId:?2,fromTime :{$gt:?1},isDeleted : false }")
    List<ClientException> getExceptionAfterDate(long clientId, Date startDate, BigInteger exceptionTypeId);

    void deleteByIdIn(List<BigInteger> exceptionIdsToDelete);

    List<ClientException> findExceptionByClientIdInAndExceptionTypeIdAndFromTimeAndToTime(List<Long> clientIds,BigInteger exceptionTypeId,
                                                                                          Date timeFrom,Date timeTo);
}
