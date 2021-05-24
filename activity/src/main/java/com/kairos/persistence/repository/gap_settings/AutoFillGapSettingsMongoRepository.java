package com.kairos.persistence.repository.gap_settings;

import com.kairos.dto.activity.auto_gap_fill_settings.AutoFillGapSettingsDTO;
import com.kairos.persistence.model.auto_gap_fill_settings.AutoFillGapSettings;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface AutoFillGapSettingsMongoRepository extends MongoBaseRepository<AutoFillGapSettings, BigInteger> {

    @Cacheable(value = "getAllAutoFillGapSettingsByCountryId", key = "#countryId", cacheManager = "cacheManager")
    @Query("{deleted : false,countryId:?0}")
    List<AutoFillGapSettingsDTO> getAllAutoFillGapSettingsByCountryId(Long countryId);

    @Cacheable(value = "getAllAutoFillGapSettingsByUnitId", key = "#unitId", cacheManager = "cacheManager")
    @Query("{deleted : false,unitId:?0}")
    List<AutoFillGapSettingsDTO> getAllAutoFillGapSettingsByUnitId(Long unitId);

    @Query("{deleted : false, published: true,unitId: ?0,phaseId: ?1,autoGapFillingScenario: ?2,_id:{$ne: ?3},gapApplicableFor: ?4, $or:[{startDate:{$lte:?5},endDate:{$exists:false} },{startDate: {$lte: ?5},endDate:{$gte:?5}}]}")
    AutoFillGapSettings getCurrentlyApplicableGapSettingsForUnit(Long unitId,  BigInteger phaseId, String gapFillingScenario, BigInteger id, String gapApplicableFor, LocalDate startDate);

    @Query("{deleted : false, published: true,countryId: ?0,organizationTypeId: ?1,organizationSubTypeId: ?2,phaseId: ?3,autoGapFillingScenario: ?4,_id:{$ne: ?5},gapApplicableFor: ?6, $or:[{startDate:{$lt:?7},endDate:{$exists:false} },{startDate: {$lt: ?7},endDate:{$gte:?7}}]}")
    AutoFillGapSettings getCurrentlyApplicableGapSettingsForCountry(Long countryId, Long organizationTypeId, Long organizationSubTypeId, BigInteger phaseId, String gapFillingScenario, BigInteger id, String gapApplicableFor, LocalDate startDate);

    @Query("{deleted : false, published: true,countryId: ?0,organizationTypeId: ?1,organizationSubTypeId: ?2,phaseId: ?3,autoGapFillingScenario: ?4,_id:{$ne: ?5},gapApplicableFor: ?6, startDate: {$gt:?7}}")
    List<AutoFillGapSettings> getGapSettingsForCountry(Long countryId, Long organizationTypeId, Long organizationSubTypeId, BigInteger phaseId, String gapFillingScenario, BigInteger id, String gapApplicableFor, LocalDate startDate);

    @Query("{deleted : false, published: true,unitId: ?0,organizationTypeId: ?1,organizationSubTypeId: ?2,phaseId: ?3,autoGapFillingScenario: ?4,_id:{$ne: ?5},gapApplicableFor: ?6, startDate: ?7}}")
    List<AutoFillGapSettings> getGapSettingsForUnit(Long unitId, Long organizationTypeId, Long organizationSubTypeId, BigInteger phaseId, String gapFillingScenario, BigInteger id, String gapApplicableFor, LocalDate startDate);

    @Query("{deleted : false, parentId: ?0, published: false}")
    AutoFillGapSettings getGapSettingsByParentId(BigInteger parentId);

    @Query("{deleted : false, published: true,countryId: ?0,organizationTypeId: ?1,organizationSubTypeId: {$in: ?2}}")
    List<AutoFillGapSettings> getAllDefautAutoFillSettings(Long countryId, Long organizationTypeId, List<Long> organizationSubTypeIds);

    @Query(value = "{deleted : false,unitId:?0}",exists = true)
    Boolean isAutoFillGapSettingsByUnitId(Long unitId);

}
