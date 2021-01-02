package com.kairos.persistence.repository.gap_settings;

import com.kairos.dto.activity.auto_gap_fill_settings.AutoFillGapSettingsDTO;
import com.kairos.persistence.model.auto_gap_fill_settings.AutoFillGapSettings;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface AutoFillGapSettingsMongoRepository extends MongoBaseRepository<AutoFillGapSettings, BigInteger> {

    @Query("{deleted : false,countryId:?0}")
    List<AutoFillGapSettingsDTO> getAllByCountryId(Long countryId);

    @Query("{deleted : false,unitId:?0}")
    List<AutoFillGapSettingsDTO> getAllByUnitId(Long unitId);

    @Query("{deleted : false, published: true,unitId: ?0,organizationTypeId: ?1,organizationSubTypeId: ?2,phaseId: ?3,autoGapFillingScenario: ?4,_id:{$ne: ?5},gapApplicableFor: ?6, $or:[{startDate:{$lte:?7},endDate:{$exists:false} },{startDate: {$lte: ?7},endDate:{$gte:?8}}]}")
    AutoFillGapSettings getCurrentlyApplicableGapSettingsForUnit(Long unitId, Long organizationTypeId, Long organizationSubTypeId, BigInteger phaseId, String gapFillingScenario, BigInteger id, String gapApplicableFor, LocalDate stratDate, LocalDate endDate);

    @Query("{deleted : false, published: true,countryId: ?0,organizationTypeId: ?1,organizationSubTypeId: ?2,phaseId: ?3,autoGapFillingScenario: ?4,_id:{$ne: ?5},gapApplicableFor: ?6, startDate: ?7}")
    AutoFillGapSettings getGapSettingsForCountry(Long countryId, Long organizationTypeId, Long organizationSubTypeId, BigInteger phaseId, String gapFillingScenario, BigInteger id, String gapApplicableFor, LocalDate stratDate);

    @Query("{deleted : false, published: true,unitId: ?0,organizationTypeId: ?1,organizationSubTypeId: ?2,phaseId: ?3,autoGapFillingScenario: ?4,_id:{$ne: ?5},gapApplicableFor: ?6, startDate: ?7}")
    AutoFillGapSettings getGapSettingsForUnit(Long unitId, Long organizationTypeId, Long organizationSubTypeId, BigInteger phaseId, String gapFillingScenario, BigInteger id, String gapApplicableFor, LocalDate stratDate);

    @Query("{deleted : false, parentId: ?0, published: false}")
    AutoFillGapSettings getGapSettingsByParentId(BigInteger parentId);
}
