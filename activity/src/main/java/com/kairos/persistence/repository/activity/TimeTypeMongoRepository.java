package com.kairos.persistence.repository.activity;


import com.kairos.persistence.model.activity.TimeType;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.activity.time_type.TimeTypeDTO;
import com.kairos.response.dto.web.cta.TimeTypeResponseDTO;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface TimeTypeMongoRepository extends MongoBaseRepository<TimeType, BigInteger> {

    @Query("{'upperLevelTimeTypeId':{'$exists':false},'deleted' : false,'countryId':?0}")
    List<TimeType> getTopLevelTimeType(Long countryId);

    @Query("{'upperLevelTimeTypeId':{'$exists':true},'deleted' : false,'countryId':?0}")
    List<TimeType> findAllLowerLevelTimeType(Long countryId);

    @Query("{countryId:?1,label:?0,deleted : false}")
    TimeType exists(String name, Long countryId);

    @Query("{upperLevelTimeTypeId:?0,deleted : false,countryId:?1}")
    List<TimeType> findAllChildByParentId(BigInteger id, Long countryId);

    @Query("{countryId:?0,deleted : false}")
    List<TimeType> findAllByCountryId(Long countryId);

    @Query("{id:?0,countryId:?1,deleted : false}")
    TimeType findOneById(BigInteger TimeTypeId, Long countryId);

    @Query("{id:?0, deleted : false}")
    TimeType findOneById(BigInteger TimeTypeId);

    @Query(value = "{'upperLevelTimeTypeId':{'$exists':false},'deleted' : false,'countryId':?0}")
    List<TimeTypeDTO> getTopLevelTimeTypeIds(Long countryId);

    @Query("{upperLevelTimeTypeId:{$in:?0},deleted : false}")
    List<TimeTypeResponseDTO> findAllChildByParentId(List<BigInteger> id);
}
