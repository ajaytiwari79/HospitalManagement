package com.kairos.service.staff_settings;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.user.staff.staff_settings.StaffTeamRankingDTO;
import com.kairos.persistence.model.staff_settings.StaffTeamRanking;
import com.kairos.persistence.repository.staff_settings.StaffTeamRankingRepository;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.commons.utils.ObjectUtils.newArrayList;


@Service
public class StaffTeamRankingService {
    @Inject
    private StaffTeamRankingRepository staffTeamRankingRepository;
    @Inject
    private ExceptionService exceptionService;

    public List<StaffTeamRankingDTO> getStaffTeamRankings(Long staffId){
        List<StaffTeamRanking> staffTeamRankings = staffTeamRankingRepository.getStaffTeamRankingByStaffIdAndDeletedFalse(staffId);
        return ObjectMapperUtils.copyCollectionPropertiesByMapper(staffTeamRankings, StaffTeamRankingDTO.class);
    }

    public StaffTeamRankingDTO publishStaffTeamRanking(BigInteger id, Long staffId, LocalDate publishedDate) {
        StaffTeamRanking staffTeamRanking = staffTeamRankingRepository.findOne(id);
        if(isNull(staffTeamRanking)){
            exceptionService.dataNotFoundByIdException("");
        }
        if (staffTeamRanking.isPublished()) {
            exceptionService.actionNotPermittedException("");
        }
        StaffTeamRanking publishedStaffTeamRanking = new StaffTeamRanking();
        staffTeamRanking.setStartDate(publishedDate);
        staffTeamRanking.setEndDate(publishedStaffTeamRanking.getEndDate());
        staffTeamRanking.setPublished(true);
        publishedStaffTeamRanking.setEndDate(publishedDate.minusDays(1));
        staffTeamRankingRepository.saveEntities(newArrayList(staffTeamRanking,publishedStaffTeamRanking));
        return ObjectMapperUtils.copyPropertiesByMapper(staffTeamRanking, StaffTeamRankingDTO.class);
    }
}
