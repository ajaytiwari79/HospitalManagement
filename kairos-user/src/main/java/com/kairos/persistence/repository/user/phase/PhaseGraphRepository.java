package com.kairos.persistence.repository.user.phase;

import com.kairos.persistence.model.user.phase.Phase;
import com.kairos.persistence.model.user.phase.PhaseDTO;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;
import static com.kairos.persistence.model.constants.RelationshipConstants.PHASE_BELONGS_TO;

/**
 * Created by pawanmandhan on 29/8/17.
 */
@Repository
public interface PhaseGraphRepository extends GraphRepository<Phase> {

    @Query ("Match (org:Organization)  " +
            "Match (phase :Phase{disabled:false})-[:"+ PHASE_BELONGS_TO +"]->(org) where id(org)={0} and phase.name={1} and phase.disabled={2}\n" +
            "return phase" )
     Phase findByNameAndDisabled(Long unitId,String name, boolean disabled);

    @Query("Match (phase:Phase{disabled:false}),(org:Organization) where id (phase)={0}  AND id(org) IN {1} with phase,org \n" +
            "Merge (phase)-[r:"+PHASE_BELONGS_TO+"]->(org) \n" +
            "ON CREATE SET r.durationInWeeks={2} \n" +
            "ON MATCH SET r.durationInWeeks={2}")
    void addOrganizationInPhase(long phaseId, List<Long> organizationIds, Long durationInWeek);

    @Query(
            "Match (phase :Phase{disabled:false})-[:"+ PHASE_BELONGS_TO +"]->(org) where id(org)={0}\n" +
            "return id(phase) as id," +
            "phase.name as name," +
            "phase.description as description," +
            "phase.duration as duration," +
            "phase.sequence as sequence," +
            "phase.constructionPhaseStartsAtDay as constructionPhaseStartsAtDay," +
            "phase.activityAccess as activityAccess")
    List<PhaseDTO> getPhasesByUnit(Long unitId);

    @Query ("Match (org:Organization)  " +
            "Match (phase :Phase{disabled:false})-[:"+ PHASE_BELONGS_TO +"]->(org) where id(org)={0} and phase.sequence={1} and p.disabled={2}\n" +
            "return phase" )
    Phase findBySequenceAndDisabled(Long unitId, int sequence,boolean disabled);

   // void detachDeletePhases();
}
