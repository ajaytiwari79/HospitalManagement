package com.kairos.service.staff;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.utils.CommonsExceptionUtil;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.user.staff.staff.StaffTeamRankingDTO;
import com.kairos.dto.user.staff.staff.TeamRankingInfoDTO;
import com.kairos.enums.team.TeamType;
import com.kairos.persistence.model.organization.StaffTeamRelationship;
import com.kairos.persistence.model.organization.team.Team;
import com.kairos.persistence.model.staff.StaffTeamRanking;
import com.kairos.persistence.model.staff.TeamRankingInfo;
import com.kairos.persistence.repository.user.staff.StaffTeamRankingGraphRepository;
import com.kairos.service.exception.ExceptionService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.CommonConstants.FULL_WEEK;
import static com.kairos.constants.UserMessagesConstants.*;

@Transactional
@Service
public class StaffTeamRankingService {

    @Inject private StaffTeamRankingGraphRepository staffTeamRankingGraphRepository;

    @Inject private ExceptionService exceptionService;

    public StaffTeamRankingDTO updateStaffTeamRanking(StaffTeamRankingDTO staffTeamRankingDTO){
        StaffTeamRanking staffTeamRanking = staffTeamRankingGraphRepository.findById(staffTeamRankingDTO.getId()).orElseThrow(()->new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_DATANOTFOUND, "Staff Team Ranking", staffTeamRankingDTO.getId())));
        if (isNotNull(staffTeamRanking.getDraftId())) {
            exceptionService.dataNotFoundByIdException(MESSAGE_DRAFT_COPY_CREATED);
        }
        Set<TeamRankingInfo> nextTeamRankingInfo= ObjectMapperUtils.copyCollectionPropertiesByMapper(staffTeamRankingDTO.getTeamRankingInfo(), TeamRankingInfo.class);
        if(isSameTeamInfo(staffTeamRanking.getTeamRankingInfo(), nextTeamRankingInfo)){
            exceptionService.actionNotPermittedException("Not Update list");
        }
        if (staffTeamRanking.isPublished()) {
            StaffTeamRanking activityRankingCopy = ObjectMapperUtils.copyPropertiesByMapper(staffTeamRankingDTO, StaffTeamRanking.class);
            activityRankingCopy.setPublished(false);
            activityRankingCopy.setId(null);
            staffTeamRankingGraphRepository.save(activityRankingCopy);
            staffTeamRanking.setDraftId(activityRankingCopy.getId());
            staffTeamRankingDTO.setId(activityRankingCopy.getId());
        } else {
            staffTeamRanking =ObjectMapperUtils.copyPropertiesByMapper(staffTeamRankingDTO, StaffTeamRanking.class);
        }
        staffTeamRankingGraphRepository.save(staffTeamRanking);
        return staffTeamRankingDTO;
    }

    public List<StaffTeamRankingDTO> getStaffTeamRankings(Long staffId){
        List<StaffTeamRanking> staffTeamRankings = staffTeamRankingGraphRepository.findByStaffIdAndDeletedFalse(staffId);
        return ObjectMapperUtils.copyCollectionPropertiesByMapper(staffTeamRankings, StaffTeamRankingDTO.class);
    }

    public StaffTeamRankingDTO publishStaffTeamRanking(Long id, Long staffId, LocalDate publishedDate) {
        StaffTeamRanking staffTeamRanking = staffTeamRankingGraphRepository.findById(id).orElseThrow(()->new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_DATANOTFOUND, "Staff Team Ranking", id)));
        if (staffTeamRanking.isPublished()) {
            exceptionService.actionNotPermittedException(MESSAGE_RANKING_ALREADY_PUBLISHED, "Staff team");
        }
        StaffTeamRanking publishedStaffTeamRanking = null;//staffTeamRankingGraphRepository.getStaffTeamRanking(staffId,publishedDate);
        staffTeamRanking.setStartDate(publishedDate);
        staffTeamRanking.setEndDate(publishedStaffTeamRanking.getEndDate());
        staffTeamRanking.setPublished(true);
        publishedStaffTeamRanking.setEndDate(publishedDate.minusDays(1));
        if(isNotNull(publishedStaffTeamRanking.getDraftId()) && publishedStaffTeamRanking.getDraftId().equals(id)){
            publishedStaffTeamRanking.setDraftId(null);
        } else {
            StaffTeamRanking parent = staffTeamRankingGraphRepository.findByDraftIdAndDeletedFalse(id);
            parent.setDraftId(null);
            staffTeamRankingGraphRepository.save(parent);
        }
        staffTeamRankingGraphRepository.saveAll(newArrayList(staffTeamRanking,publishedStaffTeamRanking));
        return ObjectMapperUtils.copyPropertiesByMapper(staffTeamRanking, StaffTeamRankingDTO.class);
    }

    @Async
    public void updateActivityIdInTeamRanking(Long teamId, BigInteger activityId){
        staffTeamRankingGraphRepository.updateActivityIdInTeamRanking(teamId, activityId);
    }

    @Async
    public void addStaffTeamRanking(Long staffId, Team team, StaffTeamRelationship staffTeamRelationship){
        List<StaffTeamRanking> staffTeamRankings = staffTeamRankingGraphRepository.findByStaffIdAndDeletedFalse(staffId);
        if(isCollectionEmpty(staffTeamRankings)){
            StaffTeamRanking staffTeamRanking = new StaffTeamRanking(staffId, staffTeamRelationship.getStartDate(), staffTeamRelationship.getEndDate(), newHashSet(new TeamRankingInfo(team.getId(), staffTeamRelationship.getTeamType(), team.getActivityId(), 0)));
            staffTeamRankingGraphRepository.save(staffTeamRanking);
        } else {
            List<StaffTeamRanking> updateStaffTeamRankings = staffTeamRankings.stream().filter(staffTeamRanking -> isNull(staffTeamRanking.getEndDate()) || staffTeamRanking.getEndDate().isAfter(staffTeamRelationship.getStartDate())).collect(Collectors.toList());
            if(isNull(updateStaffTeamRankings)){
                StaffTeamRanking staffTeamRanking = new StaffTeamRanking(staffId, staffTeamRelationship.getStartDate(), staffTeamRelationship.getEndDate(), newHashSet(new TeamRankingInfo(team.getId(), staffTeamRelationship.getTeamType(), team.getActivityId(), 0)));
                staffTeamRankingGraphRepository.save(staffTeamRanking);
            } else {
                updateStaffTeamRankings.sort(Comparator.comparing(StaffTeamRanking::getStartDate));
                StaffTeamRanking staffTeamRanking = null;
                if(updateStaffTeamRankings.get(0).getStartDate().isBefore(staffTeamRelationship.getStartDate())) {
                    staffTeamRanking = new StaffTeamRanking(staffId, updateStaffTeamRankings.get(0).getStartDate(), staffTeamRelationship.getStartDate().minusDays(1), updateStaffTeamRankings.get(0).getTeamRankingInfo());
                    updateStaffTeamRankings.get(0).setStartDate(staffTeamRelationship.getStartDate());
                } else if(updateStaffTeamRankings.get(0).getStartDate().isAfter(staffTeamRelationship.getStartDate())){
                    staffTeamRanking = new StaffTeamRanking(staffId, staffTeamRelationship.getStartDate(), updateStaffTeamRankings.get(0).getStartDate().minusDays(1), newHashSet(new TeamRankingInfo(team.getId(), staffTeamRelationship.getTeamType(), team.getActivityId(), 0)));
                }
                updateStaffTeamRankings.forEach(updateStaffTeamRanking->updateStaffTeamRanking.getTeamRankingInfo().add(new TeamRankingInfo(team.getId(), staffTeamRelationship.getTeamType(), team.getActivityId(), 0)));
                if(isNotNull(staffTeamRanking)){
                    updateStaffTeamRankings.add(staffTeamRanking);
                }
                staffTeamRankingGraphRepository.saveAll(updateStaffTeamRankings);
            }
        }
    }

    @Async
    public void removeStaffTeamInfo(Long staffId, Long teamId){
        List<StaffTeamRanking> staffTeamRankings = staffTeamRankingGraphRepository.findByStaffIdAndDeletedFalse(staffId);
        Set<Long> removeTeamRankingInfoIds = new HashSet<>();
        for (StaffTeamRanking staffTeamRanking : staffTeamRankings) {
            Set<Long> teamRankingInfoIds = staffTeamRanking.getTeamRankingInfo().stream().filter(teamRankingInfo -> teamId.equals(teamRankingInfo.getTeamId())).map(TeamRankingInfo::getId).collect(Collectors.toSet());
            if(isCollectionNotEmpty(teamRankingInfoIds)){
                removeTeamRankingInfoIds.addAll(teamRankingInfoIds);
            }
            staffTeamRanking.setTeamRankingInfo(staffTeamRanking.getTeamRankingInfo().stream().filter(teamRankingInfo -> !teamId.equals(teamRankingInfo.getTeamId())).collect(Collectors.toSet()));
        }
        if(isCollectionNotEmpty(removeTeamRankingInfoIds)) {
            staffTeamRankingGraphRepository.removeTeamRankingInfo(removeTeamRankingInfoIds);
        }
        mergeStaffTeamRanking(staffTeamRankings);
    }

    private void removeTeamInfo(Long teamId, List<StaffTeamRanking> staffTeamRankings) {

    }

    private void mergeStaffTeamRanking(List<StaffTeamRanking> staffTeamRankings) {
        Map<Long, StaffTeamRanking> mergeStaffTeamRankings = new HashMap<>();
        if(isCollectionNotEmpty(staffTeamRankings) && staffTeamRankings.size() > 1) {
            staffTeamRankings.sort(Comparator.comparing(StaffTeamRanking::getStartDate));
            for(int index=0; index < staffTeamRankings.size()-1; index++){
                StaffTeamRanking staffTeamRanking = staffTeamRankings.get(index);
                StaffTeamRanking nextStaffTeamRanking = staffTeamRankings.get(index+1);
                staffTeamRanking.setDeleted(isCollectionEmpty(staffTeamRanking.getTeamRankingInfo()));
                nextStaffTeamRanking.setDeleted(isCollectionEmpty(nextStaffTeamRanking.getTeamRankingInfo()));
                if(isSameTeamInfo(staffTeamRanking.getTeamRankingInfo(), nextStaffTeamRanking.getTeamRankingInfo())){
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
            staffTeamRankingGraphRepository.saveAll(mergeStaffTeamRankings.values());
        }
    }

    private boolean isSameTeamInfo(Set<TeamRankingInfo> teamRankingInfos, Set<TeamRankingInfo> nextTeamRankingInfos) {
        return teamRankingInfos.equals(nextTeamRankingInfos);
    }

    @Async
    public void updateStartDate(Long staffId, Team team, StaffTeamRelationship staffTeamRelationship, LocalDate oldStartDate){
        List<StaffTeamRanking> staffTeamRankings = staffTeamRankingGraphRepository.findByStaffIdAndPublishedTrueAndDeletedFalse(staffId);
        boolean increaseStartDate = oldStartDate.isBefore(staffTeamRelationship.getStartDate());
        StaffTeamRanking newStaffTeamRanking = null;
        Set<Long> removeTeamRankingInfoIds = new HashSet<>();
        for (StaffTeamRanking staffTeamRanking : staffTeamRankings) {
            if(staffTeamRanking.getStartDate().isBefore(staffTeamRelationship.getStartDate()) && isNull(staffTeamRanking.getEndDate()) || staffTeamRanking.getEndDate().isAfter(staffTeamRelationship.getStartDate())){
                newStaffTeamRanking = getNewStaffTeamRanking(staffTeamRelationship.getStartDate(), staffTeamRanking, team.getId(), increaseStartDate?staffTeamRelationship.getTeamType():null);
            } else if(increaseStartDate && isNotNull(staffTeamRanking.getEndDate()) && staffTeamRanking.getEndDate().isBefore(staffTeamRelationship.getStartDate())){
                Set<Long> teamRankingInfoIds = staffTeamRanking.getTeamRankingInfo().stream().filter(teamRankingInfo -> team.getId().equals(teamRankingInfo.getTeamId())).map(TeamRankingInfo::getId).collect(Collectors.toSet());
                if(isCollectionNotEmpty(teamRankingInfoIds)){
                    removeTeamRankingInfoIds.addAll(teamRankingInfoIds);
                }
                staffTeamRanking.setTeamRankingInfo(staffTeamRanking.getTeamRankingInfo().stream().filter(teamRankingInfo -> !teamRankingInfo.getTeamId().equals(team.getId())).collect(Collectors.toSet()));
            } else if(!increaseStartDate && isNotNull(staffTeamRanking.getEndDate())){
                staffTeamRanking.getTeamRankingInfo().add(new TeamRankingInfo(team.getId(), staffTeamRelationship.getTeamType(), team.getActivityId(), 0));
            }
        }
        if(isCollectionNotEmpty(removeTeamRankingInfoIds)) {
            staffTeamRankingGraphRepository.removeTeamRankingInfo(removeTeamRankingInfoIds);
        }
        if(isNotNull(newStaffTeamRanking)){
            staffTeamRankings.add(newStaffTeamRanking);
        }
        staffTeamRankingGraphRepository.saveAll(staffTeamRankings);
        mergeStaffTeamRanking(staffTeamRankings);
    }

    private StaffTeamRanking getNewStaffTeamRanking(LocalDate newStartDate, StaffTeamRanking staffTeamRanking, Long teamId, TeamType teamType) {
        StaffTeamRanking newStaffTeamRanking = ObjectMapperUtils.copyPropertiesByMapper(staffTeamRanking, StaffTeamRanking.class);
        newStaffTeamRanking.setId(null);
        newStaffTeamRanking.setDraftId(null);
        newStaffTeamRanking.setStartDate(newStartDate);
        staffTeamRanking.setEndDate(newStartDate.minusDays(1));
        if(isNotNull(teamType)){
            newStaffTeamRanking.getTeamRankingInfo().add(new TeamRankingInfo(teamId, teamType, new BigInteger("0"), 0));
        } else {
            staffTeamRanking.setTeamRankingInfo(staffTeamRanking.getTeamRankingInfo().stream().filter(teamRankingInfo -> teamRankingInfo.getTeamId()!=teamId).collect(Collectors.toSet()));
        }
        staffTeamRankingGraphRepository.save(newStaffTeamRanking);
        return newStaffTeamRanking;
    }

    @Async
    public void updateEndDate(Long staffId, Team team, StaffTeamRelationship staffTeamRelationship, LocalDate oldEndDate){
        List<StaffTeamRanking> staffTeamRankings = staffTeamRankingGraphRepository.findByStaffIdAndPublishedTrueAndDeletedFalse(staffId);
        if(isNull(staffTeamRelationship.getEndDate()) && isNotNull(oldEndDate)){
            resetStaffTeamEndDate(staffTeamRankings, staffId, team, staffTeamRelationship, oldEndDate);
        } else if(isNull(oldEndDate) && isNotNull(staffTeamRelationship.getEndDate())){
            if(isCollectionNotEmpty(staffTeamRankings)) {
                setStaffTeamEndDate(staffTeamRankings, team, staffTeamRelationship, oldEndDate);
            }
        } else {

        }
    }

    private void resetStaffTeamEndDate(List<StaffTeamRanking> staffTeamRankings, Long staffId, Team team, StaffTeamRelationship staffTeamRelationship, LocalDate oldEndDate) {
        List<StaffTeamRanking> modifiedStaffTeamRankings = new ArrayList<>();
        staffTeamRankings.sort(Comparator.comparing(StaffTeamRanking::getStartDate));
        for (StaffTeamRanking staffTeamRanking : staffTeamRankings) {
            if(!staffTeamRanking.getStartDate().isBefore(oldEndDate)) {
                staffTeamRanking.getTeamRankingInfo().add(new TeamRankingInfo(team.getId(), staffTeamRelationship.getTeamType(), team.getActivityId(), 0));
                modifiedStaffTeamRankings.add(staffTeamRanking);
            }
        }
        if(isCollectionNotEmpty(staffTeamRankings) && isNotNull(staffTeamRankings.get(staffTeamRankings.size()-1).getEndDate())){
            StaffTeamRanking newStaffTeamRanking = new StaffTeamRanking(staffId, staffTeamRankings.get(staffTeamRankings.size()-1).getEndDate().plusDays(1), null, newHashSet(new TeamRankingInfo(team.getId(), staffTeamRelationship.getTeamType(), team.getActivityId(), 0)));
            staffTeamRankingGraphRepository.save(newStaffTeamRanking);
            staffTeamRankings.add(newStaffTeamRanking);
        }
        if(isCollectionNotEmpty(modifiedStaffTeamRankings)) {
            staffTeamRankingGraphRepository.saveAll(modifiedStaffTeamRankings);
        }
        mergeStaffTeamRanking(staffTeamRankings);
    }

    private void setStaffTeamEndDate(List<StaffTeamRanking> staffTeamRankings, Team team, StaffTeamRelationship staffTeamRelationship, LocalDate oldEndDate) {
        StaffTeamRanking newStaffTeamRanking = null;
        Set<Long> removeTeamRankingInfoIds = new HashSet<>();
        for (StaffTeamRanking staffTeamRanking : staffTeamRankings) {
            if(isNull(newStaffTeamRanking) && (isNull(staffTeamRanking.getEndDate()) || (staffTeamRanking.getStartDate().isBefore(staffTeamRelationship.getEndDate()) && staffTeamRanking.getEndDate().isAfter(staffTeamRelationship.getEndDate())))){
                newStaffTeamRanking = getNewStaffTeamRanking(staffTeamRankings, team, staffTeamRelationship, staffTeamRanking);
            } else if(staffTeamRanking.getStartDate().isAfter(staffTeamRelationship.getEndDate())){
                Set<Long> teamRankingInfoIds = staffTeamRanking.getTeamRankingInfo().stream().filter(teamRankingInfo -> team.getId().equals(teamRankingInfo.getTeamId())).map(TeamRankingInfo::getId).collect(Collectors.toSet());
                if(isCollectionNotEmpty(teamRankingInfoIds)){
                    removeTeamRankingInfoIds.addAll(teamRankingInfoIds);
                }
                staffTeamRanking.setTeamRankingInfo(staffTeamRanking.getTeamRankingInfo().stream().filter(teamRankingInfo -> !team.getId().equals(teamRankingInfo.getTeamId())).collect(Collectors.toSet()));
            }
        }
        if(isCollectionNotEmpty(removeTeamRankingInfoIds)) {
            staffTeamRankingGraphRepository.removeTeamRankingInfo(removeTeamRankingInfoIds);
        }
        if(isNotNull(newStaffTeamRanking)){
            staffTeamRankings.add(newStaffTeamRanking);
        }
        mergeStaffTeamRanking(staffTeamRankings);
    }

    private StaffTeamRanking getNewStaffTeamRanking(List<StaffTeamRanking> staffTeamRankings, Team team, StaffTeamRelationship staffTeamRelationship, StaffTeamRanking staffTeamRanking) {
        StaffTeamRanking newStaffTeamRanking;
        newStaffTeamRanking = ObjectMapperUtils.copyPropertiesByMapper(staffTeamRankings.get(0), StaffTeamRanking.class);
        newStaffTeamRanking.setId(null);
        newStaffTeamRanking.setDraftId(null);
        newStaffTeamRanking.setStartDate(staffTeamRelationship.getEndDate().plusDays(1));
        newStaffTeamRanking.getTeamRankingInfo().forEach(teamRankingInfo -> teamRankingInfo.setId(null));
        newStaffTeamRanking.setTeamRankingInfo(newStaffTeamRanking.getTeamRankingInfo().stream().filter(teamRankingInfo -> !team.getId().equals(teamRankingInfo.getTeamId())).collect(Collectors.toSet()));
        staffTeamRanking.setEndDate(staffTeamRelationship.getEndDate());
        staffTeamRankingGraphRepository.save(newStaffTeamRanking);
        return newStaffTeamRanking;
    }
}
