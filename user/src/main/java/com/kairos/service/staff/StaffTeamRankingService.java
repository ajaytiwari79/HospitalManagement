package com.kairos.service.staff;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.utils.CommonsExceptionUtil;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.staff.staff.StaffTeamRankingDTO;
import com.kairos.enums.team.TeamType;
import com.kairos.persistence.model.organization.StaffTeamRelationShipQueryResult;
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
import static com.kairos.constants.UserMessagesConstants.*;
import static com.kairos.enums.team.TeamType.MAIN;

@Transactional
@Service
public class StaffTeamRankingService {

    public static final String STAFF_TEAM_RANKING = "Staff Team Ranking";
    public static final int TOP_RANK = 1;

    @Inject private StaffTeamRankingGraphRepository staffTeamRankingGraphRepository;

    @Inject private ExceptionService exceptionService;

    public StaffTeamRankingDTO updateStaffTeamRanking(StaffTeamRankingDTO staffTeamRankingDTO){
        StaffTeamRanking staffTeamRanking = staffTeamRankingGraphRepository.findById(staffTeamRankingDTO.getId()).orElseThrow(()->new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_DATANOTFOUND, STAFF_TEAM_RANKING, staffTeamRankingDTO.getId())));
        if (isNotNull(staffTeamRanking.getDraftId())) {
            exceptionService.dataNotFoundByIdException(MESSAGE_DRAFT_COPY_CREATED);
        }
        Set<TeamRankingInfo> nextTeamRankingInfo= ObjectMapperUtils.copyCollectionPropertiesByMapper(staffTeamRankingDTO.getTeamRankingInfo(), TeamRankingInfo.class);
        if(isSameTeamInfo(staffTeamRanking.getTeamRankingInfo(), nextTeamRankingInfo)){
            exceptionService.actionNotPermittedException("Not Update list");
        }
        if (staffTeamRanking.isPublished()) {
            nextTeamRankingInfo.forEach(teamRankingInfo -> teamRankingInfo.setId(null));
            StaffTeamRanking staffTeamRankingCopy = new StaffTeamRanking(staffTeamRanking.getStaffId(), staffTeamRanking.getStartDate(), staffTeamRanking.getEndDate(), nextTeamRankingInfo, false);
            staffTeamRankingGraphRepository.save(staffTeamRankingCopy);
            staffTeamRanking.setDraftId(staffTeamRankingCopy.getId());
            staffTeamRankingDTO.setId(staffTeamRankingCopy.getId());
        } else {
            staffTeamRanking =ObjectMapperUtils.copyPropertiesByMapper(staffTeamRankingDTO, StaffTeamRanking.class);
        }
        staffTeamRankingGraphRepository.save(staffTeamRanking);
        return staffTeamRankingDTO;
    }

    public boolean deleteStaffTeamRanking(Long id){
        StaffTeamRanking staffTeamRanking = staffTeamRankingGraphRepository.findById(id).orElseThrow(()->new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_DATANOTFOUND, STAFF_TEAM_RANKING, id)));
        if(staffTeamRanking.isPublished()){
            exceptionService.actionNotPermittedException(MESSAGE_RANKING_ALREADY_PUBLISHED);
        }
        staffTeamRanking.setDeleted(true);
        staffTeamRankingGraphRepository.save(staffTeamRanking);
        StaffTeamRanking parent = staffTeamRankingGraphRepository.findByDraftIdAndDeletedFalse(staffTeamRanking.getId());
        if(isNotNull(parent)) {
            parent.setDraftId(null);
            staffTeamRankingGraphRepository.save(parent);
        }
        return true;
    }

    public List<StaffTeamRankingDTO> getStaffTeamRankings(Long staffId, boolean includeDraft){
        List<StaffTeamRanking> staffTeamRankings;
        if(includeDraft) {
            staffTeamRankings = staffTeamRankingGraphRepository.findByStaffIdAndDeletedFalse(staffId);
        } else {
            staffTeamRankings = staffTeamRankingGraphRepository.findByStaffIdAndPublishedTrueAndDeletedFalse(staffId);
        }
        staffTeamRankings.sort(Comparator.comparing(StaffTeamRanking::getStartDate).thenComparing(StaffTeamRanking::getCreationDate));
        return ObjectMapperUtils.copyCollectionPropertiesByMapper(staffTeamRankings, StaffTeamRankingDTO.class);
    }

    public StaffTeamRankingDTO publishStaffTeamRanking(Long id, LocalDate publishedDate) {
        StaffTeamRanking staffTeamRanking = staffTeamRankingGraphRepository.findById(id).orElseThrow(()->new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_DATANOTFOUND, STAFF_TEAM_RANKING, id)));
        if (staffTeamRanking.isPublished()) {
            exceptionService.actionNotPermittedException(MESSAGE_RANKING_ALREADY_PUBLISHED, "Staff team");
        }
        StaffTeamRanking parent = staffTeamRankingGraphRepository.findByDraftIdAndDeletedFalse(id);
        if(!parent.getStartDate().isBefore(publishedDate) || (isNotNull(parent.getEndDate()) && parent.getEndDate().isBefore(publishedDate))){
            exceptionService.actionNotPermittedException("Invalid publish date");
        }
        staffTeamRanking.setStartDate(publishedDate);
        staffTeamRanking.setEndDate(parent.getEndDate());
        staffTeamRanking.setPublished(true);
        parent.setEndDate(publishedDate.minusDays(1));
        parent.setDraftId(null);
        staffTeamRankingGraphRepository.saveAll(newArrayList(staffTeamRanking,parent));
        return ObjectMapperUtils.copyPropertiesByMapper(staffTeamRanking, StaffTeamRankingDTO.class);
    }

    @Async
    public void updateActivityIdInTeamRanking(Long teamId, BigInteger activityId){
        staffTeamRankingGraphRepository.updateActivityIdInTeamRanking(teamId, activityId);
    }

    @Async
    public void addOrUpdateStaffTeamRanking(Long staffId, Team team, StaffTeamRelationship staffTeamRelationship, StaffTeamRelationShipQueryResult oldStaffTeamRelationship) {
        if(isNull(oldStaffTeamRelationship)){
            addStaffTeamRanking(staffId, team, staffTeamRelationship);
        } else {
            if(!staffTeamRelationship.getStartDate().isEqual(oldStaffTeamRelationship.getStartDate())){
                updateStartDate(staffId, team, staffTeamRelationship, oldStaffTeamRelationship.getStartDate());
            }
            LocalDate oldEndDate = oldStaffTeamRelationship.getEndDate();
            LocalDate newEndDate = staffTeamRelationship.getEndDate();
            if((isNotNull(oldEndDate) && isNotNull(newEndDate) && !oldEndDate.isEqual(newEndDate)) || (isNotNull(oldEndDate) && isNull(newEndDate)) || (isNull(oldEndDate) && isNotNull(newEndDate))){
                updateEndDate(staffId, team, staffTeamRelationship, oldEndDate);
            }
            if(!staffTeamRelationship.getTeamType().equals(oldStaffTeamRelationship.getTeamType())){
                updateTeamType(staffId, team.getId(), staffTeamRelationship.getTeamType());
            }
        }
    }

    @Async
    public void addStaffTeamRanking(Long staffId, Team team, StaffTeamRelationship staffTeamRelationship){
        List<StaffTeamRanking> staffTeamRankings = staffTeamRankingGraphRepository.findByStaffIdAndDeletedFalse(staffId);
        if(isCollectionEmpty(staffTeamRankings)){
            StaffTeamRanking staffTeamRanking = new StaffTeamRanking(staffId, staffTeamRelationship.getStartDate(), staffTeamRelationship.getEndDate(), newHashSet(new TeamRankingInfo(team.getId(), staffTeamRelationship.getTeamType(), team.getActivityId(), TOP_RANK, 0)), true);
            staffTeamRankingGraphRepository.save(staffTeamRanking);
        } else {
            LocalDate staffTeamRelationshipEndDate = staffTeamRelationship.getEndDate();
            staffTeamRelationship.setEndDate(null);
            updateStaffTeamRankingInfo(staffId, team, staffTeamRelationship, staffTeamRankings);
            if(isNotNull(staffTeamRelationshipEndDate)){
                staffTeamRelationship.setEndDate(staffTeamRelationshipEndDate);
                setStaffTeamEndDate(staffTeamRankings, team, staffTeamRelationship);
            }
        }
    }

    private void updateStaffTeamRankingInfo(Long staffId, Team team, StaffTeamRelationship staffTeamRelationship, List<StaffTeamRanking> staffTeamRankings) {
        List<StaffTeamRanking> updateStaffTeamRankings = staffTeamRankings.stream().filter(staffTeamRanking -> isNull(staffTeamRanking.getEndDate()) || staffTeamRanking.getEndDate().isAfter(staffTeamRelationship.getStartDate())).collect(Collectors.toList());
        if(isNull(updateStaffTeamRankings)){
            StaffTeamRanking staffTeamRanking = new StaffTeamRanking(staffId, staffTeamRelationship.getStartDate(), staffTeamRelationship.getEndDate(), newHashSet(new TeamRankingInfo(team.getId(), staffTeamRelationship.getTeamType(), team.getActivityId(), TOP_RANK, 0)), true);
            staffTeamRankingGraphRepository.save(staffTeamRanking);
        } else {
            updateStaffTeamRankings.sort(Comparator.comparing(StaffTeamRanking::getStartDate));
            List<StaffTeamRanking> newStaffTeamRankings = new ArrayList<>();
            if(updateStaffTeamRankings.get(0).getStartDate().isBefore(staffTeamRelationship.getStartDate())) {
                newStaffTeamRankings.add(new StaffTeamRanking(staffId, updateStaffTeamRankings.get(0).getStartDate(), staffTeamRelationship.getStartDate().minusDays(1), updateStaffTeamRankings.get(0).getTeamRankingInfo(), true));
                updateStaffTeamRankings.get(0).setStartDate(staffTeamRelationship.getStartDate());
                newStaffTeamRankings.get(0).getTeamRankingInfo().forEach(s-> s.setId(null));
            } else if(updateStaffTeamRankings.get(0).getStartDate().isAfter(staffTeamRelationship.getStartDate())){
                newStaffTeamRankings.add(new StaffTeamRanking(staffId, staffTeamRelationship.getStartDate(), updateStaffTeamRankings.get(0).getStartDate().minusDays(1), newHashSet(new TeamRankingInfo(team.getId(), staffTeamRelationship.getTeamType(), team.getActivityId(), TOP_RANK, 0)), true));
            }
            updateStaffTeamRankings.forEach(updateStaffTeamRanking->{
                updateStaffTeamRanking.getTeamRankingInfo().add(new TeamRankingInfo(team.getId(), staffTeamRelationship.getTeamType(), team.getActivityId(), updateStaffTeamRanking.getTeamRankingInfo().size()+1, 0));
                updateRank(updateStaffTeamRanking.getTeamRankingInfo());
            });
            if(isNotNull(updateStaffTeamRankings.get(updateStaffTeamRankings.size()-1).getEndDate())){
                newStaffTeamRankings.add(new StaffTeamRanking(staffId, updateStaffTeamRankings.get(updateStaffTeamRankings.size()-1).getEndDate().plusDays(1), null, newHashSet(new TeamRankingInfo(team.getId(), staffTeamRelationship.getTeamType(), team.getActivityId(), TOP_RANK, 0)), true));
            }
            if(isCollectionNotEmpty(newStaffTeamRankings)){
                staffTeamRankings.addAll(newStaffTeamRankings);
            }
            staffTeamRankingGraphRepository.saveAll(staffTeamRankings);
        }
    }

    private void updateRank(Set<TeamRankingInfo> teamRankingInfoSet) {
        TeamRankingInfo teamRank = teamRankingInfoSet.stream().filter(teamRankingInfo -> MAIN.equals(teamRankingInfo.getTeamType())).findAny().orElse(null);
        if(isNotNull(teamRank)){
            teamRankingInfoSet.forEach(teamRankingInfo ->
                    teamRankingInfo.setRank(teamRankingInfo.getRank() < teamRank.getRank() ? teamRankingInfo.getRank() + 1 : teamRankingInfo.getRank())
            );
            teamRank.setRank(TOP_RANK);
        }
    }

    @Async
    public void removeStaffTeamInfo(Long staffId, Long teamId){
        List<StaffTeamRanking> staffTeamRankings = staffTeamRankingGraphRepository.findByStaffIdAndDeletedFalse(staffId);
        Set<Long> removeTeamRankingInfoIds = new HashSet<>();
        for (StaffTeamRanking staffTeamRanking : staffTeamRankings) {
            removeTeamRankingInfo(teamId, removeTeamRankingInfoIds, staffTeamRanking);
        }
        if(isCollectionNotEmpty(removeTeamRankingInfoIds)) {
            staffTeamRankingGraphRepository.removeTeamRankingInfo(removeTeamRankingInfoIds);
        }
        mergeStaffTeamRanking(staffTeamRankings);
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
        boolean isSame = teamRankingInfos.size() == nextTeamRankingInfos.size();
        if(isSame){
            Map<Long,Integer> teamRankingInfoMap = teamRankingInfos.stream().collect(Collectors.toMap(k->k.getTeamId(), v->v.getRank()));
            for (TeamRankingInfo nextTeamRankingInfo : nextTeamRankingInfos) {
                int teamRank = teamRankingInfoMap.getOrDefault(nextTeamRankingInfo.getTeamId(), 0);
                if(nextTeamRankingInfo.getRank() != teamRank){
                    isSame = false;
                    break;
                }
            }
        }
        return isSame;
    }

    @Async
    public void updateStartDate(Long staffId, Team team, StaffTeamRelationship staffTeamRelationship, LocalDate oldStartDate){
        List<StaffTeamRanking> staffTeamRankings = staffTeamRankingGraphRepository.findByStaffIdAndPublishedTrueAndDeletedFalse(staffId);
        if(isCollectionNotEmpty(staffTeamRankings)) {
            staffTeamRankings.sort(Comparator.comparing(StaffTeamRanking::getStartDate));
            StaffTeamRanking newStaffTeamRanking;
            if (oldStartDate.isAfter(staffTeamRelationship.getStartDate())) {
                newStaffTeamRanking = increaseStaffTeamStartDate(staffId, team, staffTeamRelationship, oldStartDate, staffTeamRankings);
            } else {
                newStaffTeamRanking = decreaseStaffStartDate(team, staffTeamRelationship, oldStartDate, staffTeamRankings);
            }
            if (isNotNull(newStaffTeamRanking)) {
                staffTeamRankingGraphRepository.save(newStaffTeamRanking);
                staffTeamRankings.add(newStaffTeamRanking);
            }
            mergeStaffTeamRanking(staffTeamRankings);
        }
    }

    private StaffTeamRanking decreaseStaffStartDate(Team team, StaffTeamRelationship staffTeamRelationship, LocalDate oldStartDate, List<StaffTeamRanking> staffTeamRankings) {
        StaffTeamRanking newStaffTeamRanking = null;
        Set<Long> removeTeamRankingInfoIds = new HashSet<>();
        for (StaffTeamRanking staffTeamRanking : staffTeamRankings) {
            if(staffTeamRanking.getStartDate().isBefore(staffTeamRelationship.getStartDate()) && (isNull(staffTeamRanking.getEndDate()) || !staffTeamRanking.getEndDate().isBefore(staffTeamRelationship.getStartDate()))) {
                newStaffTeamRanking = ObjectMapperUtils.copyPropertiesByMapper(staffTeamRanking, StaffTeamRanking.class);
                newStaffTeamRanking.setId(null);
                newStaffTeamRanking.setDraftId(null);
                newStaffTeamRanking.setStartDate(staffTeamRelationship.getStartDate());
                newStaffTeamRanking.getTeamRankingInfo().forEach(teamRankingInfo -> teamRankingInfo.setId(null));
                staffTeamRanking.setEndDate(staffTeamRelationship.getStartDate().minusDays(1));
                Set<Long> teamRankingInfoIds = staffTeamRanking.getTeamRankingInfo().stream().filter(teamRankingInfo -> team.getId().equals(teamRankingInfo.getTeamId())).map(TeamRankingInfo::getId).collect(Collectors.toSet());
                if(isCollectionNotEmpty(teamRankingInfoIds)){
                    removeTeamRankingInfoIds.addAll(teamRankingInfoIds);
                }
                staffTeamRanking.setTeamRankingInfo(staffTeamRanking.getTeamRankingInfo().stream().filter(teamRankingInfo -> !team.getId().equals(teamRankingInfo.getTeamId())).collect(Collectors.toSet()));
            }else if(!staffTeamRanking.getStartDate().isBefore(oldStartDate) && isNotNull(staffTeamRanking.getEndDate()) && staffTeamRanking.getEndDate().isBefore(staffTeamRelationship.getStartDate())){
                removeTeamRankingInfo(team.getId(), removeTeamRankingInfoIds, staffTeamRanking);
            }
        }
        if(isCollectionNotEmpty(removeTeamRankingInfoIds)) {
            staffTeamRankingGraphRepository.removeTeamRankingInfo(removeTeamRankingInfoIds);
        }
        return newStaffTeamRanking;
    }

    private StaffTeamRanking increaseStaffTeamStartDate(Long staffId, Team team, StaffTeamRelationship staffTeamRelationship, LocalDate oldStartDate, List<StaffTeamRanking> staffTeamRankings) {
        StaffTeamRanking newStaffTeamRanking = null;
        if(staffTeamRelationship.getStartDate().isBefore(staffTeamRankings.get(0).getStartDate())){
            newStaffTeamRanking = new StaffTeamRanking(staffId, staffTeamRelationship.getStartDate(), staffTeamRankings.get(0).getStartDate().minusDays(1), newHashSet(new TeamRankingInfo(team.getId(), staffTeamRelationship.getTeamType(), team.getActivityId(), TOP_RANK, 0)), true);
        }
        for (StaffTeamRanking staffTeamRanking : staffTeamRankings) {
            if(isNull(newStaffTeamRanking) && staffTeamRanking.getStartDate().isBefore(staffTeamRelationship.getStartDate()) && (isNull(staffTeamRanking.getEndDate()) || !staffTeamRanking.getEndDate().isBefore(staffTeamRelationship.getStartDate()))) {
                newStaffTeamRanking = ObjectMapperUtils.copyPropertiesByMapper(staffTeamRanking, StaffTeamRanking.class);
                newStaffTeamRanking.setId(null);
                newStaffTeamRanking.setDraftId(null);
                newStaffTeamRanking.setStartDate(staffTeamRelationship.getStartDate());
                newStaffTeamRanking.getTeamRankingInfo().forEach(teamRankingInfo -> teamRankingInfo.setId(null));
                staffTeamRanking.setEndDate(staffTeamRelationship.getStartDate().minusDays(1));
                newStaffTeamRanking.getTeamRankingInfo().add(new TeamRankingInfo(team.getId(), staffTeamRelationship.getTeamType(), team.getActivityId(), staffTeamRanking.getTeamRankingInfo().size()+1, 0));
                updateRank(newStaffTeamRanking.getTeamRankingInfo());
            }else if(!staffTeamRanking.getStartDate().isBefore(staffTeamRelationship.getStartDate()) && isNotNull(staffTeamRanking.getEndDate()) && staffTeamRanking.getEndDate().isBefore(oldStartDate)){
                staffTeamRanking.getTeamRankingInfo().add(new TeamRankingInfo(team.getId(), staffTeamRelationship.getTeamType(), team.getActivityId(),staffTeamRanking.getTeamRankingInfo().size()+1, 0));
                updateRank(staffTeamRanking.getTeamRankingInfo());
            }
        }
        return newStaffTeamRanking;
    }

    @Async
    public void updateEndDate(Long staffId, Team team, StaffTeamRelationship staffTeamRelationship, LocalDate oldEndDate){
        List<StaffTeamRanking> staffTeamRankings = staffTeamRankingGraphRepository.findByStaffIdAndPublishedTrueAndDeletedFalse(staffId);
        if(isCollectionNotEmpty(staffTeamRankings)) {
            LocalDate newEndDate = staffTeamRelationship.getEndDate();
            if (isNull(newEndDate) && isNotNull(oldEndDate)) {
                resetStaffTeamEndDate(staffTeamRankings, staffId, team, staffTeamRelationship, oldEndDate);
            } else if (isNull(oldEndDate) && isNotNull(newEndDate)) {
                setStaffTeamEndDate(staffTeamRankings, team, staffTeamRelationship);
            } else {
                staffTeamRelationship.setEndDate(null);
                resetStaffTeamEndDate(staffTeamRankings, staffId, team, staffTeamRelationship, oldEndDate);
                staffTeamRelationship.setEndDate(newEndDate);
                setStaffTeamEndDate(staffTeamRankings, team, staffTeamRelationship);
            }
        }
    }

    private void resetStaffTeamEndDate(List<StaffTeamRanking> staffTeamRankings, Long staffId, Team team, StaffTeamRelationship staffTeamRelationship, LocalDate oldEndDate) {
        List<StaffTeamRanking> modifiedStaffTeamRankings = new ArrayList<>();
        staffTeamRankings.sort(Comparator.comparing(StaffTeamRanking::getStartDate));
        for (StaffTeamRanking staffTeamRanking : staffTeamRankings) {
            if(!staffTeamRanking.getStartDate().isBefore(oldEndDate)) {
                staffTeamRanking.getTeamRankingInfo().add(new TeamRankingInfo(team.getId(), staffTeamRelationship.getTeamType(), team.getActivityId(), staffTeamRanking.getTeamRankingInfo().size()+1, 0));
                updateRank(staffTeamRanking.getTeamRankingInfo());
                modifiedStaffTeamRankings.add(staffTeamRanking);
            }
        }
        if(isCollectionNotEmpty(staffTeamRankings) && isNotNull(staffTeamRankings.get(staffTeamRankings.size()-1).getEndDate())){
            StaffTeamRanking newStaffTeamRanking = new StaffTeamRanking(staffId, staffTeamRankings.get(staffTeamRankings.size()-1).getEndDate().plusDays(1), null, newHashSet(new TeamRankingInfo(team.getId(), staffTeamRelationship.getTeamType(), team.getActivityId(), TOP_RANK, 0)), true);
            staffTeamRankingGraphRepository.save(newStaffTeamRanking);
            staffTeamRankings.add(newStaffTeamRanking);
        }
        if(isCollectionNotEmpty(modifiedStaffTeamRankings)) {
            staffTeamRankingGraphRepository.saveAll(modifiedStaffTeamRankings);
        }
        mergeStaffTeamRanking(staffTeamRankings);
    }

    private void setStaffTeamEndDate(List<StaffTeamRanking> staffTeamRankings, Team team, StaffTeamRelationship staffTeamRelationship) {
        StaffTeamRanking newStaffTeamRanking = null;
        Set<Long> removeTeamRankingInfoIds = new HashSet<>();
        for (StaffTeamRanking staffTeamRanking : staffTeamRankings) {
            if(isNull(newStaffTeamRanking) && (isNull(staffTeamRanking.getEndDate()) || (staffTeamRanking.getStartDate().isBefore(staffTeamRelationship.getEndDate()) && staffTeamRanking.getEndDate().isAfter(staffTeamRelationship.getEndDate())))){
                newStaffTeamRanking = getNewStaffTeamRanking(team, staffTeamRelationship, staffTeamRanking);
            } else if(staffTeamRanking.getStartDate().isAfter(staffTeamRelationship.getEndDate())){
                removeTeamRankingInfo(team.getId(), removeTeamRankingInfoIds, staffTeamRanking);
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

    private void removeTeamRankingInfo(Long teamId, Set<Long> removeTeamRankingInfoIds, StaffTeamRanking staffTeamRanking) {
        TeamRankingInfo teamRank = staffTeamRanking.getTeamRankingInfo().stream().filter(teamRankingInfo -> teamRankingInfo.getTeamId().equals(teamId)).findAny().orElse(null);
        if(isNotNull(teamRank)){
            removeTeamRankingInfoIds.add(teamRank.getId());
            staffTeamRanking.setTeamRankingInfo(staffTeamRanking.getTeamRankingInfo().stream().filter(teamRankingInfo -> !teamId.equals(teamRankingInfo.getTeamId())).collect(Collectors.toSet()));
            staffTeamRanking.getTeamRankingInfo().forEach(teamRankingInfo ->
                    teamRankingInfo.setRank(teamRankingInfo.getRank() > teamRank.getRank() ? teamRankingInfo.getRank() - 1 : teamRankingInfo.getRank())
            );
        }
    }

    private StaffTeamRanking getNewStaffTeamRanking(Team team, StaffTeamRelationship staffTeamRelationship, StaffTeamRanking staffTeamRanking) {
        StaffTeamRanking newStaffTeamRanking;
        newStaffTeamRanking = ObjectMapperUtils.copyPropertiesByMapper(staffTeamRanking, StaffTeamRanking.class);
        newStaffTeamRanking.setId(null);
        newStaffTeamRanking.setDraftId(null);
        newStaffTeamRanking.setStartDate(staffTeamRelationship.getEndDate().plusDays(1));
        TeamRankingInfo teamRank = staffTeamRanking.getTeamRankingInfo().stream().filter(teamRankingInfo -> teamRankingInfo.getTeamId().equals(team.getId())).findAny().orElse(null);
        if(isNotNull(teamRank)){
            newStaffTeamRanking.setTeamRankingInfo(staffTeamRanking.getTeamRankingInfo().stream().filter(teamRankingInfo -> !team.getId().equals(teamRankingInfo.getTeamId())).collect(Collectors.toSet()));
            newStaffTeamRanking.getTeamRankingInfo().forEach(teamRankingInfo -> teamRankingInfo.setRank(teamRankingInfo.getRank() > teamRank.getRank() ? teamRankingInfo.getRank() - 1 : teamRankingInfo.getRank()));
        }
        newStaffTeamRanking.getTeamRankingInfo().forEach(teamRankingInfo -> teamRankingInfo.setId(null));
        staffTeamRanking.setEndDate(staffTeamRelationship.getEndDate());
        staffTeamRankingGraphRepository.save(newStaffTeamRanking);
        return newStaffTeamRanking;
    }

    @Async
    private void updateTeamType(Long staffId, Long teamId, TeamType newTeamType) {
        if(MAIN.equals(newTeamType)) {
            List<StaffTeamRanking> staffTeamRankings = staffTeamRankingGraphRepository.findByStaffIdAndPublishedTrueAndDeletedFalse(staffId);
            for (StaffTeamRanking staffTeamRanking : staffTeamRankings) {
                TeamRankingInfo teamRank = staffTeamRanking.getTeamRankingInfo().stream().filter(teamRankingInfo -> teamRankingInfo.getTeamId().equals(teamId)).findAny().orElse(null);
                if(isNotNull(teamRank)) {
                    teamRank.setTeamType(newTeamType);
                    staffTeamRanking.getTeamRankingInfo().forEach(teamRankingInfo ->
                        teamRankingInfo.setRank(teamRankingInfo.getRank() < teamRank.getRank() ? teamRankingInfo.getRank() + 1 : teamRankingInfo.getRank())
                    );
                    teamRank.setRank(TOP_RANK);
                }
            }
            staffTeamRankingGraphRepository.saveAll(staffTeamRankings);
        } else {
            staffTeamRankingGraphRepository.updateTeamType(staffId, teamId, newTeamType);
        }
    }

}
