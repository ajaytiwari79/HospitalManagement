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

    @Override
    @Query("MATCH(p:Phase{disabled:false}) return p")
    List<Phase> findAll();

    @Query("match(phase:Phase)\n" +
            " Match (o:Organization) where Id(o)={0}\n" +
            "match (phase)-[ph:PHASE_BELONGS_TO]->(o)\n" +
            "return phase.name as name,id(phase) as id, ph.durationInWeeks as  duration")
    List<PhaseDTO> findAllPhaseWithDuration(Long unitId);

    @Query("match(phase:Phase) where Id(phase)={1}\n" +
            " Match (o:Organization) where Id(o)={0}\n" +
            "match (phase)-[ph:PHASE_BELONGS_TO]->(o)\n" +
            " set ph.durationInWeeks={2} return phase")
    Phase findAndUpdateByPhaseAndDuration(Long unitId, Long phaseId, Long durationInWeek);

    public Phase findByNameAndDisabled(String name, boolean disabled);


    @Query("Match (phase:Phase{disabled:false}),(org:Organization) where id (phase)={0}  AND id(org) IN {1} with phase,org \n" +
            "Merge (phase)-[r:"+PHASE_BELONGS_TO+"]->(org) \n" +
            "ON CREATE SET r.durationInWeeks={2} \n" +
            "ON MATCH SET r.durationInWeeks={2}")
    void addOrganizationInPhase(long phaseId, List<Long> organizationIds, Long durationInWeek);



}
