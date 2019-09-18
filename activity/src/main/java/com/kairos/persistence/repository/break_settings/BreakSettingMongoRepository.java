package com.kairos.persistence.repository.break_settings;

import com.kairos.dto.activity.break_settings.BreakSettingsDTO;
import com.kairos.persistence.model.break_settings.BreakSettings;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface BreakSettingMongoRepository extends MongoBaseRepository<BreakSettings, BigInteger> , CustomBreakSettingsMongoRepository{

    List<BreakSettingsDTO> findAllByDeletedFalseAndExpertiseIdOrderByCreatedAtAsc(Long expertiseId);

    BreakSettings findByIdAndDeletedFalse(BigInteger id);

    @Query("{countryId:?0,deleted:false,primary:true,expertiseId:?1}")
    BreakSettings findByDeletedFalseAndCountryIdAndExpertiseIdAndPrimaryTrue(Long countryId, Long expertiseId);

    List<BreakSettings> findAllByDeletedFalseAndExpertiseIdOrderByCreatedAtAsc(Long expertiseId, Long shiftDurationInMinute);

    List<BreakSettings> findAllByDeletedFalseAndExpertiseIdInOrderByCreatedAtAsc(List<Long> expertiseIds);

    @Query("{deleted:false,primary:true,expertiseId:?0}")
    BreakSettings findAllByDeletedFalseAndExpertiseId(Long expertiseId);

}
