package com.kairos.service.unit_settings;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.utils.CommonsExceptionUtil;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.unit_settings.activity_configuration.AbsenceRankingDTO;
import com.kairos.dto.user.country.experties.CareDaysDetails;
import com.kairos.persistence.model.unit_settings.AbsenceRankingSettings;
import com.kairos.persistence.repository.unit_settings.AbsenceRankingSettingsRepository;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.kairos.commons.utils.ObjectUtils.isCollectionEmpty;
import static com.kairos.commons.utils.ObjectUtils.newArrayList;
import static com.kairos.constants.ActivityMessagesConstants.MESSAGE_DATANOTFOUND;

@Service
public class AbsenceRankingSettingsService {
    @Inject
    private AbsenceRankingSettingsRepository absenceRankingSettingsRepository;
    @Inject
    private ExceptionService exceptionService;

    public AbsenceRankingDTO saveAbsenceRankingSettings(AbsenceRankingDTO absenceRankingDTO){
        AbsenceRankingSettings absenceRankingSettings= ObjectMapperUtils.copyPropertiesByMapper(absenceRankingDTO,AbsenceRankingSettings.class);
        absenceRankingSettingsRepository.save(absenceRankingSettings);
        absenceRankingDTO.setId(absenceRankingSettings.getId());
        return absenceRankingDTO;
    }

    public AbsenceRankingDTO updateAbsenceRankingSettings(AbsenceRankingDTO absenceRankingDTO){
        AbsenceRankingSettings absenceRankingSettings = absenceRankingSettingsRepository.findById(absenceRankingDTO.getId()).orElseThrow(()->new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_DATANOTFOUND, "Absence Ranking", absenceRankingDTO.getId())));

        if (absenceRankingSettings.getDraftId()!=null) {
            exceptionService.dataNotFoundByIdException("MESSAGE_DRAFT_COPY_CREATED");
        }
        if (absenceRankingSettings.isPublished()) {
            // current is published so we need to create a  new copy and update in same
            AbsenceRankingSettings absenceRankingSettingsCopy = ObjectMapperUtils.copyPropertiesByMapper(absenceRankingDTO,AbsenceRankingSettings.class);
            absenceRankingSettingsCopy.setPublished(false);
            absenceRankingSettingsCopy.setId(null);
            absenceRankingSettingsRepository.save(absenceRankingSettingsCopy);

        } else {
            // update in current copy
            absenceRankingSettings=ObjectMapperUtils.copyPropertiesByMapper(absenceRankingDTO,AbsenceRankingSettings.class);
            absenceRankingSettingsRepository.save(absenceRankingSettings);
        }
        return absenceRankingDTO;
    }

    public List<AbsenceRankingDTO> getAbsenceRankingSettings(){
        return absenceRankingSettingsRepository.getAbsenceRankingSettingsDeletedFalse();
    }

    public List<AbsenceRankingDTO> getAbsenceRankingSettings(Long expertiseId){
        return absenceRankingSettingsRepository.getAbsenceRankingSettingsByExpertiseIdAndDeletedFalse(expertiseId);
    }

    public boolean deleteAbsenceRankingSettings(BigInteger id){
        AbsenceRankingSettings absenceRankingSettings=absenceRankingSettingsRepository.findOne(id);
        absenceRankingSettings.setDeleted(true);
        absenceRankingSettingsRepository.save(absenceRankingSettings);
        return true;
    }


    public AbsenceRankingDTO publishSeniorDays(BigInteger id, LocalDate publishedDate) {
        AbsenceRankingSettings absenceRankingSettings = absenceRankingSettingsRepository.findById(id).orElseThrow(()->new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_DATANOTFOUND, "Absence Ranking Settings", id)));
        if (absenceRankingSettings.getActivityRankings().isEmpty()) {
            exceptionService.actionNotPermittedException("MESSAGE_RANKING_EMPTY");
        }
        if (absenceRankingSettings.isPublished()) {
            exceptionService.dataNotFoundByIdException("MESSAGE_RANKING_ALREADYPUBLISHED");
        }
        if (absenceRankingSettings.getStartDate().isAfter(publishedDate) ||
                (absenceRankingSettings.getEndDate()!=null && absenceRankingSettings.getEndDate().isBefore(publishedDate))) {
            exceptionService.dataNotFoundByIdException("MESSAGE_PUBLISHDATE_NOTLESSTHAN_STARTDATE");
        }
        absenceRankingSettings.setPublished(true);
        absenceRankingSettings.setStartDate(publishedDate); // changing
        AbsenceRankingSettings parentAbsenceRanking = absenceRankingSettingsRepository.findByDraftIdAndDeletedFalse(absenceRankingSettings.getId());
        AbsenceRankingSettings lastAbsenceRanking = absenceRankingSettingsRepository.findByExpertiseIdAndDeletedFalseOrderByStartDateDescLimitOne(absenceRankingSettings.getExpertiseId());
        boolean onGoingUpdated = false;
        if (lastAbsenceRanking != null && publishedDate.isAfter(lastAbsenceRanking.getStartDate()) && lastAbsenceRanking.getEndDate() == null) {
            lastAbsenceRanking.setEndDate(publishedDate.minusDays(1));
            absenceRankingSettingsRepository.save(lastAbsenceRanking);
            absenceRankingSettings.setEndDate(null);
            onGoingUpdated = true;
        }
        if (!onGoingUpdated && Optional.ofNullable(parentAbsenceRanking).isPresent()) {
            if (parentAbsenceRanking.getStartDate().isEqual(publishedDate) || parentAbsenceRanking.getStartDate().isAfter(publishedDate)) {
                exceptionService.dataNotFoundByIdException("MESSAGE_PUBLISHDATE_NOTLESSTHAN_OR_EQUALS_PARENT_STARTDATE");
            }
            parentAbsenceRanking.setEndDate(publishedDate.minusDays(1L));
            if (lastAbsenceRanking == null && absenceRankingSettings.getEndDate() != null && absenceRankingSettings.getEndDate().isBefore(publishedDate)) {
                absenceRankingSettings.setEndDate(null);
            }
        }
        parentAbsenceRanking.setDraftId(null);
        absenceRankingSettingsRepository.saveEntities(newArrayList(absenceRankingSettings,parentAbsenceRanking));
        return ObjectMapperUtils.copyPropertiesByMapper(parentAbsenceRanking,AbsenceRankingDTO.class);

    }
}
