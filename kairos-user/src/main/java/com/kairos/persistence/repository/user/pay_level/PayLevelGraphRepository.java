package com.kairos.persistence.repository.user.pay_level;

import com.kairos.persistence.model.user.pay_level.PayLevel;
import com.kairos.persistence.model.user.pay_level.PayLevelDTO;
import com.kairos.persistence.model.user.pay_level.PayLevelGlobalData;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by prabjot on 26/12/17.
 */
@Repository
public interface PayLevelGraphRepository extends GraphRepository<PayLevel>{

    @Query("Match (organizationType:OrganizationType)-[:"+BELONGS_TO+"]->(country:Country) where id(country)={0}\n" +
            "Optional Match (organizationType)-[:"+HAS_SUB_TYPE+"]->(subType:OrganizationType) with subType,organizationType\n" +
            "Optional Match (organizationType:OrganizationType)-[:"+HAS_LEVEL+"]->(level:Level) with organizationType,level,subType\n" +
            "Optional Match (subType)-[r:"+ORG_TYPE_HAS_EXPERTISE+"{isEnabled:true}]->(expertise:Expertise) with expertise,organizationType,level\n" +
            "return id(organizationType) as id ,organizationType.name as name, collect(level) as level,collect(expertise) as expertise;")
    List<PayLevelGlobalData> getPayLevelGlobalData(Long countryId);

    @Query("Match (payLevel:PayLevel)-[:"+BELONGS_TO+"]->(c:Country) where id(c)={0}\n" +
            "Match (payLevel)-[:"+HAS_ORGANIZATION_TYPE+"]->(orgType:OrganizationType),\n" +
            "(payLevel)-[:"+HAS_EXPERTISE+"]->(expertise:Expertise)\n" +
            "Optional Match (payLevel)-[:"+HAS_LEVEL+"]->(level:Level)\n" +
            "return id(payLevel) as id, payLevel.name as name,payLevel.paymentUnit as paymentUnit,payLevel.startDate as startDate," +
            "payLevel.endDate as endDate,id(orgType) as OrganizationTypeId,id(level) as levelId,id(expertise) as expertiseId")
    List<PayLevelDTO> getPayLevels(Long countryId);


}
