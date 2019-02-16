package com.kairos.persistence.repository.time_type;


import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.TimeTypeResponseDTO;
import com.kairos.enums.TimeTypes;
import com.kairos.enums.shift.BreakPaymentSetting;
import com.kairos.persistence.model.activity.TimeType;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface TimeTypeMongoRepository extends MongoBaseRepository<TimeType, BigInteger> ,CustomTimeTypeMongoRepository {

    @Query("{'upperLevelTimeTypeId':{'$exists':false},'deleted' : false,'countryId':?0}")
    List<TimeType> getTopLevelTimeType(Long countryId);

    @Query("{'upperLevelTimeTypeId':{'$exists':true},'deleted' : false,'countryId':?0}")
    List<TimeType> findAllLowerLevelTimeType(Long countryId);

    @Query("{label:{$in:?0},countryId:?1,deleted : false}")
    TimeType findByLabelsAndCountryId(List<String> label, Long countryId);

    @Query("{upperLevelTimeTypeId:?0,deleted : false,countryId:?1}")
    List<TimeType> findAllChildByParentId(BigInteger id, Long countryId);

    @Query("{countryId:?0,deleted : false}")
    List<TimeType> findAllTimeTypeByCountryId(Long countryId);

    @Query("{countryId:?0, deleted : false, timeTypes: ?1}")
    List<TimeType> findByTimeTypeEnumAndCountryId(Long countryId, TimeTypes timeTypeEnum);

    @Query("{id:?0,countryId:?1,deleted : false}")
    TimeType findOneById(BigInteger TimeTypeId, Long countryId);

    @Query("{id:?0, deleted : false}")
    TimeType findOneById(BigInteger TimeTypeId);

    @Query(value = "{'upperLevelTimeTypeId':{'$exists':false},'deleted' : false,'countryId':?0}")
    List<TimeTypeDTO> getTopLevelTimeTypeIds(Long countryId);

    @Query("{upperLevelTimeTypeId:{$in:?0},deleted : false}")
    List<TimeTypeResponseDTO> findAllChildByParentId(List<BigInteger> id);

    @Query(value = "{id: { $nin:?0},label:{ $in:?1},countryId:?2,deleted : false}",exists = true)
    Boolean findByIdNotEqualAndLabelAndCountryId(List<BigInteger> timeTypeIds,List<String> timeTypeNames, Long countryId);

    @Query(value = "{id:{$ne:?0},label: ?1,countryId:?2,deleted : false}",exists = true)
    boolean timeTypeAlreadyExistsByLabelAndCountryId(BigInteger timeTypeId,String timeTypeName, Long countryId);

    @Query("{id:{$in:?0},deleted : false}")
    List<TimeType> findAllByTimeTypeIds(List<BigInteger> id);

    @Query("{upperLevelTimeTypeId:{ $in:?0},deleted : false}")
    List<TimeType> findAllChildTimeTypeByParentId(List<BigInteger> timeTypeIds);

    @Query(value = "{ deleted:false, countryId:?0 , secondLevelType:?1 }")
    List<TimeType> findAllByDeletedFalseAndCountryIdAndTimeType(Long countryId, String timeType);
}
