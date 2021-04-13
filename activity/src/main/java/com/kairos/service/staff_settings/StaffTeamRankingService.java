package com.kairos.service.staff_settings;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.utils.CommonsExceptionUtil;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.user.staff.staff_settings.StaffTeamRankingDTO;
import com.kairos.enums.team.TeamType;
import com.kairos.persistence.model.staff_settings.StaffTeamRanking;
import com.kairos.persistence.model.staff_settings.TeamRankingInfo;
import com.kairos.persistence.model.unit_settings.ActivityRanking;
import com.kairos.persistence.repository.staff_settings.StaffTeamRankingRepository;
import com.kairos.service.exception.ExceptionService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.*;


@Service
public class StaffTeamRankingService {
    @Inject
    private StaffTeamRankingRepository staffTeamRankingRepository;
    @Inject
    private ExceptionService exceptionService;

    public StaffTeamRankingDTO updateStaffTeamRanking(StaffTeamRankingDTO staffTeamRankingDTO){
        StaffTeamRanking staffTeamRanking = staffTeamRankingRepository.findById(staffTeamRankingDTO.getId()).orElseThrow(()->new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_DATANOTFOUND, "Staff Team Ranking", staffTeamRankingDTO.getId())));
        if (isNotNull(staffTeamRanking.getDraftId())) {
            exceptionService.dataNotFoundByIdException(MESSAGE_DRAFT_COPY_CREATED);
        }
        if (staffTeamRanking.isPublished()) {
            StaffTeamRanking activityRankingCopy = ObjectMapperUtils.copyPropertiesByMapper(staffTeamRankingDTO, StaffTeamRanking.class);
            activityRankingCopy.setPublished(false);
            activityRankingCopy.setId(null);
            staffTeamRankingRepository.save(activityRankingCopy);
            staffTeamRanking.setDraftId(activityRankingCopy.getId());
            staffTeamRankingDTO.setId(activityRankingCopy.getId());
        } else {
            staffTeamRanking =ObjectMapperUtils.copyPropertiesByMapper(staffTeamRankingDTO, StaffTeamRanking.class);
        }
        staffTeamRankingRepository.save(staffTeamRanking);
        return staffTeamRankingDTO;
    }

    public List<StaffTeamRankingDTO> getStaffTeamRankings(Long staffId){
        List<StaffTeamRanking> staffTeamRankings = staffTeamRankingRepository.getStaffTeamRankingByStaffIdAndDeletedFalse(staffId);
        return ObjectMapperUtils.copyCollectionPropertiesByMapper(staffTeamRankings, StaffTeamRankingDTO.class);
    }

    public StaffTeamRankingDTO publishStaffTeamRanking(BigInteger id, Long staffId, LocalDate publishedDate) {
        StaffTeamRanking staffTeamRanking = staffTeamRankingRepository.findById(id).orElseThrow(()->new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_DATANOTFOUND, "Staff Team Ranking", id)));
        if (staffTeamRanking.isPublished()) {
            exceptionService.actionNotPermittedException(MESSAGE_RANKING_ALREADY_PUBLISHED, "Staff team");
        }
        StaffTeamRanking publishedStaffTeamRanking = staffTeamRankingRepository.getStaffTeamRanking(staffId,publishedDate);
        staffTeamRanking.setStartDate(publishedDate);
        staffTeamRanking.setEndDate(publishedStaffTeamRanking.getEndDate());
        staffTeamRanking.setPublished(true);
        publishedStaffTeamRanking.setEndDate(publishedDate.minusDays(1));
        if(isNotNull(publishedStaffTeamRanking.getDraftId()) && publishedStaffTeamRanking.getDraftId().equals(id)){
            publishedStaffTeamRanking.setDraftId(null);
        } else {
            StaffTeamRanking parent = staffTeamRankingRepository.getStaffTeamRankingByDraftIdAndDeletedFalse(id);
            parent.setDraftId(null);
            staffTeamRankingRepository.save(parent);
        }
        staffTeamRankingRepository.saveEntities(newArrayList(staffTeamRanking,publishedStaffTeamRanking));
        return ObjectMapperUtils.copyPropertiesByMapper(staffTeamRanking, StaffTeamRankingDTO.class);
    }

    @Async
    public void addStaffTeamInfo(Long staffId, Long teamId, TeamType teamType, LocalDate startDate, LocalDate endDate){
        List<StaffTeamRanking> staffTeamRankings = staffTeamRankingRepository.getStaffTeamRankingByStaffIdAndDeletedFalse(staffId);
        if(isCollectionEmpty(staffTeamRankings)){
            StaffTeamRanking staffTeamRanking = new StaffTeamRanking(staffId, startDate, endDate, newHashSet(new TeamRankingInfo(teamId, teamType, 0)));
            staffTeamRankingRepository.save(staffTeamRanking);
        } else {
            List<StaffTeamRanking> updateStaffTeamRankings = staffTeamRankings.stream().filter(staffTeamRanking -> isNull(staffTeamRanking.getEndDate()) || staffTeamRanking.getEndDate().isAfter(startDate)).collect(Collectors.toList());
            if(isNull(updateStaffTeamRankings)){
                StaffTeamRanking staffTeamRanking = new StaffTeamRanking(staffId, startDate, endDate, newHashSet(new TeamRankingInfo(teamId, teamType, 0)));
                staffTeamRankingRepository.save(staffTeamRanking);
            } else {
                updateStaffTeamRankings.sort(Comparator.comparing(StaffTeamRanking::getStartDate));
                StaffTeamRanking staffTeamRanking = null;
                if(!updateStaffTeamRankings.get(0).getStartDate().isEqual(startDate)) {
                    staffTeamRanking = new StaffTeamRanking(staffId, updateStaffTeamRankings.get(0).getStartDate(), startDate.minusDays(1), updateStaffTeamRankings.get(0).getTeamRankingInfo());
                    updateStaffTeamRankings.get(0).setStartDate(startDate);
                }
                updateStaffTeamRankings.forEach(updateStaffTeamRanking->updateStaffTeamRanking.getTeamRankingInfo().add(new TeamRankingInfo(teamId, teamType, 0)));
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
        mergeStaffTeamRanking(staffTeamRankings);
    }

    private void mergeStaffTeamRanking(List<StaffTeamRanking> staffTeamRankings) {
        Map<BigInteger, StaffTeamRanking> mergeStaffTeamRankings = new HashMap<>();
        if(isCollectionNotEmpty(staffTeamRankings) && staffTeamRankings.size() > 1) {
            staffTeamRankings.sort(Comparator.comparing(StaffTeamRanking::getStartDate));
            for(int index=0; index < staffTeamRankings.size()-1; index++){
                StaffTeamRanking staffTeamRanking = staffTeamRankings.get(index);
                StaffTeamRanking nextStaffTeamRanking = staffTeamRankings.get(index+1);
                staffTeamRanking.setDeleted(isCollectionEmpty(staffTeamRanking.getTeamRankingInfo()));
                nextStaffTeamRanking.setDeleted(isCollectionEmpty(nextStaffTeamRanking.getTeamRankingInfo()));
                if(isMerged(staffTeamRanking, nextStaffTeamRanking)){
                    staffTeamRanking.setDeleted(true);
                    nextStaffTeamRanking.setStartDate(staffTeamRanking.getStartDate());
                }
                mergeStaffTeamRankings.put(staffTeamRanking.getId(), staffTeamRanking);
                mergeStaffTeamRankings.put(nextStaffTeamRanking.getId(), nextStaffTeamRanking);
            }
        } else if(isCollectionNotEmpty(staffTeamRankings) && isCollectionEmpty(staffTeamRankings.get(0).getTeamRankingInfo())){
            staffTeamRankings.get(0).setDeleted(true);
            mergeStaffTeamRankings.put(staffTeamRankings.get(0).getId(), staffTeamRankings.get(0));
        }
        if(isMapNotEmpty(mergeStaffTeamRankings)){
            staffTeamRankingRepository.saveEntities(mergeStaffTeamRankings.values());
        }
    }

    private boolean isMerged(StaffTeamRanking staffTeamRanking, StaffTeamRanking nextStaffTeamRanking) {
        return staffTeamRanking.equals(nextStaffTeamRanking);
    }

    @Async
    public void updateStartDate(Long staffId, Long teamId, TeamType teamType, LocalDate oldStartDate, LocalDate newStartDate){
        List<StaffTeamRanking> staffTeamRankings = staffTeamRankingRepository.getStaffTeamRankingByStaffIdAndPublishedTrueAndDeletedFalse(staffId);
        boolean increaseStartDate = oldStartDate.isBefore(newStartDate);
        StaffTeamRanking newStaffTeamRanking = null;
        for (StaffTeamRanking staffTeamRanking : staffTeamRankings) {
            if(staffTeamRanking.getStartDate().isBefore(newStartDate) && isNull(staffTeamRanking.getEndDate()) || staffTeamRanking.getEndDate().isAfter(newStartDate)){
                newStaffTeamRanking = getNewStaffTeamRanking(newStartDate, staffTeamRanking, teamId, increaseStartDate?teamType:null);
            } else if(increaseStartDate && isNotNull(staffTeamRanking.getEndDate()) && staffTeamRanking.getEndDate().isBefore(newStartDate)){
                staffTeamRanking.setTeamRankingInfo(staffTeamRanking.getTeamRankingInfo().stream().filter(teamRankingInfo -> teamRankingInfo.getTeamId()!=teamId).collect(Collectors.toSet()));
            } else if(!increaseStartDate && isNotNull(staffTeamRanking.getEndDate())){
                staffTeamRanking.getTeamRankingInfo().add(new TeamRankingInfo(teamId, teamType, 0));
            }
        }
        if(isNotNull(newStaffTeamRanking)){
            staffTeamRankings.add(newStaffTeamRanking);
        }
        staffTeamRankingRepository.saveEntities(staffTeamRankings);
        mergeStaffTeamRanking(staffTeamRankings);
    }

    private StaffTeamRanking getNewStaffTeamRanking(LocalDate newStartDate, StaffTeamRanking staffTeamRanking, Long teamId, TeamType teamType) {
        StaffTeamRanking newStaffTeamRanking = ObjectMapperUtils.copyPropertiesByMapper(staffTeamRanking, StaffTeamRanking.class);
        newStaffTeamRanking.setId(null);
        newStaffTeamRanking.setDraftId(null);
        newStaffTeamRanking.setStartDate(newStartDate);
        staffTeamRanking.setEndDate(newStartDate.minusDays(1));
        if(isNotNull(teamType)){
            newStaffTeamRanking.getTeamRankingInfo().add(new TeamRankingInfo(teamId, teamType, 0));
        } else {
            staffTeamRanking.setTeamRankingInfo(staffTeamRanking.getTeamRankingInfo().stream().filter(teamRankingInfo -> teamRankingInfo.getTeamId()!=teamId).collect(Collectors.toSet()));
        }
        staffTeamRankingRepository.save(newStaffTeamRanking);
        return newStaffTeamRanking;
    }

    @Async
    public void updateEndDate(Long staffId, Long teamId, TeamType teamType, LocalDate oldEndDate, LocalDate newEndDate){
        List<StaffTeamRanking> staffTeamRankings = staffTeamRankingRepository.getStaffTeamRankingByStaffIdAndPublishedTrueAndDeletedFalse(staffId);
        if(isNull(oldEndDate) || isNull(newEndDate)){
            setOrUnsetTeamEndDate(teamId, teamType, oldEndDate, newEndDate, staffTeamRankings);
        } else {
            List<StaffTeamRanking> modifiedStaffTeamRankings;
            if(oldEndDate.isBefore(newEndDate)){
                modifiedStaffTeamRankings = staffTeamRankings.stream().filter(staffTeamRanking -> isNull(staffTeamRanking.getEndDate()) || staffTeamRanking.getEndDate().isAfter(oldEndDate)).collect(Collectors.toList());
                for (StaffTeamRanking modifiedStaffTeamRanking : modifiedStaffTeamRankings) {
                    modifiedStaffTeamRanking.getTeamRankingInfo().add(new TeamRankingInfo(teamId, teamType, 0));
                }
            } else {

            }
        }
    }

    private void setOrUnsetTeamEndDate(Long teamId, TeamType teamType, LocalDate oldEndDate, LocalDate newEndDate, List<StaffTeamRanking> staffTeamRankings) {
        List<StaffTeamRanking> modifiedStaffTeamRankings;
        if(isNull(newEndDate)){
            modifiedStaffTeamRankings = staffTeamRankings.stream().filter(staffTeamRanking -> isNull(staffTeamRanking.getEndDate()) || staffTeamRanking.getEndDate().isAfter(oldEndDate)).collect(Collectors.toList());
            for (StaffTeamRanking modifiedStaffTeamRanking : modifiedStaffTeamRankings) {
                modifiedStaffTeamRanking.getTeamRankingInfo().add(new TeamRankingInfo(teamId, teamType, 0));
            }
        } else {
            modifiedStaffTeamRankings = staffTeamRankings.stream().filter(staffTeamRanking -> isNull(staffTeamRanking.getEndDate()) || staffTeamRanking.getEndDate().isAfter(newEndDate)).collect(Collectors.toList());
            modifiedStaffTeamRankings.sort(Comparator.comparing(StaffTeamRanking::getStartDate));
            StaffTeamRanking newStaffTeamRanking = null;
            if(!modifiedStaffTeamRankings.get(0).getStartDate().isEqual(newEndDate.plusDays(1))){
                newStaffTeamRanking = ObjectMapperUtils.copyPropertiesByMapper(modifiedStaffTeamRankings.get(0), StaffTeamRanking.class);
                newStaffTeamRanking.setId(null);
                newStaffTeamRanking.setDraftId(null);
                newStaffTeamRanking.setEndDate(newEndDate);
                modifiedStaffTeamRankings.get(0).setStartDate(newEndDate.plusDays(1));
                staffTeamRankingRepository.save(newStaffTeamRanking);
            }
            for (StaffTeamRanking modifiedStaffTeamRanking : modifiedStaffTeamRankings) {
                modifiedStaffTeamRanking.setTeamRankingInfo(modifiedStaffTeamRanking.getTeamRankingInfo().stream().filter(teamRankingInfo -> teamRankingInfo.getTeamId()!=teamId).collect(Collectors.toSet()));
            }
            if(isNotNull(newEndDate)){
                staffTeamRankings.add(newStaffTeamRanking);
            }
        }
    }
}
