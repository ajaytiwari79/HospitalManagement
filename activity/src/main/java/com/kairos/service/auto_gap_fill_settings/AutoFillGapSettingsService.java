package com.kairos.service.auto_gap_fill_settings;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.auto_gap_fill_settings.AutoFillGapSettingsDTO;
import com.kairos.persistence.model.auto_gap_fill_settings.AutoFillGapSettings;
import com.kairos.persistence.repository.gap_settings.AutoFillGapSettingsMongoRepository;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.isNull;

@Service
public class AutoFillGapSettingsService {
    @Inject
    private AutoFillGapSettingsMongoRepository autoFillGapSettingsMongoRepository;
    @Inject
    private ExceptionService exceptionService;
    private final String PUBLISH = "PUBLISH";

    public AutoFillGapSettingsDTO createAutoFillGapSettings(AutoFillGapSettingsDTO autoFillGapSettingsDTO, String action, LocalDate publishDate, boolean forCountry) {
        if(PUBLISH.equals(action)) {
            autoFillGapSettingsDTO.setStartDate(publishDate);
            validateGapSetting(autoFillGapSettingsDTO, true, forCountry);
            autoFillGapSettingsDTO.setPublished(true);
        } else {
            validateGapSetting(autoFillGapSettingsDTO , false, forCountry);
        }
        AutoFillGapSettings autoFillGapSettings = ObjectMapperUtils.copyPropertiesByMapper(autoFillGapSettingsDTO, AutoFillGapSettings.class);
        autoFillGapSettingsMongoRepository.save(autoFillGapSettings);
        autoFillGapSettingsDTO.setId(autoFillGapSettings.getId());
        return autoFillGapSettingsDTO;
    }

    public AutoFillGapSettingsDTO updateAutoFillGapSettings(AutoFillGapSettingsDTO autoFillGapSettingsDTO, String action, LocalDate publishDate, boolean forCountry) {
        AutoFillGapSettings autoFillGapSettings = autoFillGapSettingsMongoRepository.findOne(autoFillGapSettingsDTO.getId());
        if(isNull(autoFillGapSettings)){
            exceptionService.dataNotFoundByIdException("gap filling setting not found");
        }
        if(PUBLISH.equals(action)) {
            autoFillGapSettingsDTO.setStartDate(publishDate);
            validateGapSetting(autoFillGapSettingsDTO, true, forCountry);
            autoFillGapSettingsDTO.setPublished(true);
        } else {
            if(autoFillGapSettingsDTO.isPublished()){
                AutoFillGapSettings childAutoFillGapSetting = autoFillGapSettingsMongoRepository.getGapSettingsByParentId(autoFillGapSettingsDTO.getId());
                autoFillGapSettings.setId(isNotNull(childAutoFillGapSetting) ? childAutoFillGapSetting.getId() : null);
                autoFillGapSettingsDTO.setParentId(autoFillGapSettingsDTO.getId());
                autoFillGapSettingsDTO.setPublished(false);
            }
            validateGapSetting(autoFillGapSettingsDTO , false, forCountry);
        }
        autoFillGapSettings = ObjectMapperUtils.copyPropertiesByMapper(autoFillGapSettingsDTO, AutoFillGapSettings.class);
        autoFillGapSettingsMongoRepository.save(autoFillGapSettings);
        return autoFillGapSettingsDTO;
    }

    private void validateGapSetting(AutoFillGapSettingsDTO autoFillGapSettingsDTO, boolean forPublish, boolean forCountry) {
        if(isNull(autoFillGapSettingsDTO.getStartDate())){
            exceptionService.actionNotPermittedException("Start cannot be empty");
        }
        if(autoFillGapSettingsDTO.getStartDate().isBefore(DateUtils.getCurrentLocalDate()) || (isNotNull(autoFillGapSettingsDTO.getEndDate()) && autoFillGapSettingsDTO.getEndDate().isBefore(DateUtils.getCurrentLocalDate()))){
            exceptionService.actionNotPermittedException("Start or end date cannot be past date");
        }
        if(isNotNull(autoFillGapSettingsDTO.getEndDate()) && autoFillGapSettingsDTO.getStartDate().isAfter(autoFillGapSettingsDTO.getEndDate())){
            exceptionService.actionNotPermittedException("Start date is not greater than end date");
        }
        AutoFillGapSettings autoFillGapSettings;
        if(forPublish) {
            if(autoFillGapSettingsDTO.isPublished()){
                exceptionService.actionNotPermittedException("It is already published");
            }
            if (forCountry) {
                autoFillGapSettings = autoFillGapSettingsMongoRepository.getCurrentlyApplicableGapSettingsForCountry(autoFillGapSettingsDTO.getCountryId(), autoFillGapSettingsDTO.getOrganizationTypeId(), autoFillGapSettingsDTO.getOrganizationSubTypeId(), autoFillGapSettingsDTO.getPhaseId(), autoFillGapSettingsDTO.getAutoGapFillingScenario().toString(), autoFillGapSettingsDTO.getId(), autoFillGapSettingsDTO.getGapApplicableFor().toString());
            } else {
                autoFillGapSettings = autoFillGapSettingsMongoRepository.getCurrentlyApplicableGapSettingsForUnit(autoFillGapSettingsDTO.getUnitId(), autoFillGapSettingsDTO.getOrganizationTypeId(), autoFillGapSettingsDTO.getOrganizationSubTypeId(), autoFillGapSettingsDTO.getPhaseId(), autoFillGapSettingsDTO.getAutoGapFillingScenario().toString(), autoFillGapSettingsDTO.getId(), autoFillGapSettingsDTO.getGapApplicableFor().toString());
            }
            if(isNotNull(autoFillGapSettings)){
                exceptionService.duplicateDataException("Duplicate configuration for gap setting");
            }
            updateParentEndDate(autoFillGapSettingsDTO, autoFillGapSettingsDTO.getStartDate().minusDays(1));
        }
    }

    private void updateParentEndDate(AutoFillGapSettingsDTO autoFillGapSettingsDTO, LocalDate endDate) {
        AutoFillGapSettings autoFillGapSettings;
        if(isNotNull(autoFillGapSettingsDTO.getParentId())){
            autoFillGapSettings = autoFillGapSettingsMongoRepository.findOne(autoFillGapSettingsDTO.getParentId());
        } else if (isNotNull(autoFillGapSettingsDTO.getCountryId())) {
            autoFillGapSettings = autoFillGapSettingsMongoRepository.getCurrentlyApplicableGapSettingsForCountry(autoFillGapSettingsDTO.getCountryId(), autoFillGapSettingsDTO.getOrganizationTypeId(), autoFillGapSettingsDTO.getOrganizationSubTypeId(), autoFillGapSettingsDTO.getPhaseId(), autoFillGapSettingsDTO.getAutoGapFillingScenario().toString(), autoFillGapSettingsDTO.getId(), autoFillGapSettingsDTO.getGapApplicableFor().toString());
        } else {
            autoFillGapSettings = autoFillGapSettingsMongoRepository.getCurrentlyApplicableGapSettingsForUnit(autoFillGapSettingsDTO.getUnitId(), autoFillGapSettingsDTO.getOrganizationTypeId(), autoFillGapSettingsDTO.getOrganizationSubTypeId(), autoFillGapSettingsDTO.getPhaseId(), autoFillGapSettingsDTO.getAutoGapFillingScenario().toString(), autoFillGapSettingsDTO.getId(), autoFillGapSettingsDTO.getGapApplicableFor().toString());
        }
        if(isNotNull(autoFillGapSettings)) {
            autoFillGapSettingsDTO.setParentId(autoFillGapSettings.getId());
            autoFillGapSettings.setEndDate(endDate);
            autoFillGapSettingsMongoRepository.save(autoFillGapSettings);
        }
    }

    public List<AutoFillGapSettingsDTO> getAllAutoFillGapSettings(Long countryOrUnitId, boolean forCountry) {
        List<AutoFillGapSettings> autoFillGapSettingsList;
        if(forCountry){
            autoFillGapSettingsList = autoFillGapSettingsMongoRepository.getAllByCountryId(countryOrUnitId);
        } else {
            autoFillGapSettingsList = autoFillGapSettingsMongoRepository.getAllByUnitId(countryOrUnitId);
        }
        return ObjectMapperUtils.copyCollectionPropertiesByMapper(autoFillGapSettingsList, AutoFillGapSettingsDTO.class);
    }

    public Boolean deleteAutoFillGapSettings(BigInteger autoFillGapSettingsId) {
        AutoFillGapSettings autoFillGapSettings = autoFillGapSettingsMongoRepository.findOne(autoFillGapSettingsId);
        if(isNull(autoFillGapSettings)){
            exceptionService.dataNotFoundByIdException("gap filling setting not found");
        }
        autoFillGapSettings.setDeleted(true);
        autoFillGapSettingsMongoRepository.save(autoFillGapSettings);
        return true;
    }
}
