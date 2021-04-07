package com.kairos.service.staff_settings;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.user.staff.staff_settings.StaffTeamRankingDTO;
import com.kairos.enums.team.TeamType;
import com.kairos.persistence.model.staff_settings.StaffTeamRanking;
import com.kairos.persistence.model.staff_settings.TeamRankingInfo;
import com.kairos.persistence.repository.staff_settings.StaffTeamRankingRepository;
import com.kairos.service.exception.ExceptionService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.*;


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
        StaffTeamRanking publishedStaffTeamRanking = staffTeamRankingRepository.getStaffTeamRanking(staffId,publishedDate);
        staffTeamRanking.setStartDate(publishedDate);
        staffTeamRanking.setEndDate(publishedStaffTeamRanking.getEndDate());
        staffTeamRanking.setPublished(true);
        publishedStaffTeamRanking.setEndDate(publishedDate.minusDays(1));
        staffTeamRankingRepository.saveEntities(newArrayList(staffTeamRanking,publishedStaffTeamRanking));
        return ObjectMapperUtils.copyPropertiesByMapper(staffTeamRanking, StaffTeamRankingDTO.class);
    }

    @Async
    public void addStaffTeamInfo(Long staffId, Long teamId, TeamType teamType, LocalDate startDate, LocalDate endDate){
        List<StaffTeamRanking> staffTeamRankings = staffTeamRankingRepository.getStaffTeamRankingByStaffIdAndDeletedFalse(staffId);
        TeamRankingInfo teamRankingInfo = new TeamRankingInfo(teamId, teamType, 0);
        if(isCollectionEmpty(staffTeamRankings)){
            StaffTeamRanking staffTeamRanking = new StaffTeamRanking(staffId, startDate, endDate, newHashSet(teamRankingInfo));
            staffTeamRankingRepository.save(staffTeamRanking);
        } else {
            List<StaffTeamRanking> updateStaffTeamRankings = staffTeamRankings.stream().filter(staffTeamRanking -> isNull(staffTeamRanking.getEndDate()) || staffTeamRanking.getEndDate().isAfter(startDate)).collect(Collectors.toList());
            if(isNull(updateStaffTeamRankings)){
                StaffTeamRanking staffTeamRanking = new StaffTeamRanking(staffId, startDate, endDate, newHashSet(teamRankingInfo));
                staffTeamRankingRepository.save(staffTeamRanking);
            } else {
                updateStaffTeamRankings.sort(Comparator.comparing(StaffTeamRanking::getStartDate));
                StaffTeamRanking staffTeamRanking = null;
                if(!updateStaffTeamRankings.get(0).getStartDate().isEqual(startDate)) {
                    staffTeamRanking = new StaffTeamRanking(staffId, updateStaffTeamRankings.get(0).getStartDate(), startDate.minusDays(1), updateStaffTeamRankings.get(0).getTeamRankingInfo());
                    updateStaffTeamRankings.get(0).setStartDate(startDate);
                }
                updateStaffTeamRankings.forEach(updateStaffTeamRanking->updateStaffTeamRanking.getTeamRankingInfo().add(teamRankingInfo));
                if(isNotNull(staffTeamRanking)){
                    updateStaffTeamRankings.add(staffTeamRanking);
                }
                staffTeamRankingRepository.saveEntities(updateStaffTeamRankings);
            }
        }
    }

    @Async
    public void removeStaffTeamInfo(Long staffId, Long teamId){
        List<StaffTeamRanking> staffTeamRankings = staffTeamRankingRepository.getStaffTeamRankingByStaffIdAndDeletedFalse(staffId);
        for (StaffTeamRanking staffTeamRanking : staffTeamRankings) {
            Set<TeamRankingInfo> updateTeamRankingInfoSet = staffTeamRanking.getTeamRankingInfo().stream().filter(teamRankingInfo -> teamRankingInfo.getTeamId()!=teamId).collect(Collectors.toSet());
            staffTeamRanking.setTeamRankingInfo(updateTeamRankingInfoSet);
        }
        staffTeamRankingRepository.saveEntities(staffTeamRankings);

    }
}
