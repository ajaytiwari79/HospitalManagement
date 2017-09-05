package com.kairos.persistence.repository.user.agreement.wta;

import com.kairos.persistence.model.user.agreement.wta.WTAWithCountryAndOrganizationTypeDTO;
import com.kairos.persistence.model.user.agreement.wta.WorkingTimeAgreement;
import com.kairos.persistence.model.user.agreement.wta.WorkingTimeAgreementQueryResult;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by pawanmandhan on 27/7/17.
 */
@Repository
public interface WorkingTimeAgreementGraphRepository  extends GraphRepository<WorkingTimeAgreement> {


    @Query("MATCH (wta:WorkingTimeAgreement {isEnabled:true}) where id(wta)={0} return wta")
        //@Query("start s=NODE({0}) match ")
    WorkingTimeAgreement getWta(Long wtaId);

    @Query("match(organization:OrganizationType)\n" +
            "where id(organization)={0} match (organization)<-[:"+BELONGS_TO+"]-(w:WorkingTimeAgreement {isEnabled:true})\n" +
            "match(w)-[:"+HAS_EXPERTISE_IN+"]->(e:Expertise)\n" +
            "RETURN w.startDate as startDate," +
            "e as expertise,\n"+
            "w.isEnabled as isEnabled,"+
            "w.creationDate as creationDate,"+
            "w.endDate as endDate,"+
            "w.expiryDate as expiryDate,"+
            "w.description as description,"+
            "w.name as name,"+
            "id(w) as id")
    List<WorkingTimeAgreementQueryResult> getAllWTAByOrganizationId(long organizationId);

    @Query("match (c:Country) where id(c)={0}\n" +
            "match (wta:WorkingTimeAgreement{isEnabled:true})-[:"+BELONGS_TO+"]-(c)\n" +
            "match(wta)-[:"+HAS_EXPERTISE_IN+"]->(e:Expertise) " +
            "match(wta)-[:"+BELONGS_TO+"]->(o:OrganizationType)" +
            "match(wta)-[:HAS_RULE_TEMPLATE]->(WBRT:WTABaseRuleTemplate)<-[:HAS_RULE_TEMPLATES]-(ruleTemplateCategory:RuleTemplateCategory)\n"+
            "optional match(o)-[:HAS_SUB_TYPE]->(ora:OrganizationType)\n" +
            "with wta,o,e,ora, collect ({name:WBRT.name,id:Id(WBRT),minimumRest:WBRT.minimumRest,templateType:WBRT.templateType,nightsWorked:WBRT.nightsWorked,description:WBRT.description,checkAgainstTimeRules:WBRT.checkAgainstTimeRules,ruleTemplateCategory:ruleTemplateCategory.name}) as ruleTemplates\n" +
            "with wta,e,ruleTemplates,o,collect ({name:ora.name,id:id(ora)}) as organizatioSubType\n" +
            "with wta,e,ruleTemplates,{name:o.name,id:id(o),organizationTypeList:organizatioSubType} as organization\n" +
            "return wta.startDate as startDate,e as expertise,"+
            "collect(organization) as organizationTypes,ruleTemplates as ruleTemplates,"+
            "wta.isEnabled as isEnabled,"+
            "wta.creationDate as creationDate,"+
            "wta.endDate as endDate,"+
            "wta.expiryDate as expiryDate,"+
            "wta.description as description,"+
            "wta.name as name,"+
            "id(wta) as id")
    List<WTAWithCountryAndOrganizationTypeDTO> getAllWTAByCountryId(long countryId);


    @Query("match (o:OrganizationType) where id(o)={0}" +
            "match (wta:WorkingTimeAgreement)-[:"+BELONGS_TO+"]->(o) match(wta)-[:"+HAS_EXPERTISE_IN+"]->(e:Expertise)" +
            "return wta.startDate as startDate,e as expertise,"
            +"wta.isEnabled as isEnabled,"+
            "wta.creationDate as creationDate,"+
            "wta.endDate as endDate,"+
            "wta.expiryDate as expiryDate,"+
            "wta.description as description,"+
            "wta.name as name,"+
            "id(wta) as id")
    List<WorkingTimeAgreementQueryResult>getAllWTAByOrganizationSubType(long organizationSubTypeId);
    @Query("match(c:Country) where id(c)={0}\n" +
            "match(c)<-[:BELONGS_TO]-(or:OrganizationType)\n" +
            "optional match(or)-[:"+HAS_SUB_TYPE+"]->(ora:OrganizationType)\n" +
            "optional match(w:WorkingTimeAgreement)-[:"+BELONGS_TO+"]->(ora)\n" +
            "with or,ora,w\n" +
            "with or,ora,{WTA:CASE WHEN w IS NOT NULL THEN collect({id:id(w),name:w.name}) ELSE [] END} as oraRes\n" +
            "WITH {name: or.name,id:id(or), children: CASE WHEN ora IS NOT NULL THEN collect({id:id(ora),name:ora.name,wtaa:oraRes}) ELSE [] END} as orga\n" +
            "RETURN orga as result")
    List<Map<String,Object>> getAllWTAWithOrganization(long countryId);
    @Query("match(c:Country) where id(c)={0}\n" +
            "match(c)<-[:BELONGS_TO]-(or:OrganizationType)\n" +
            " match(w:WorkingTimeAgreement) where id(w)={1}\n" +
            " match(or)-[:HAS_SUB_TYPE]->(ora:OrganizationType)\n" +
            " match(w)-[:BELONGS_TO]->(ora)\n" +
            "with or,ora,w\n" +
            "with or,ora,{WTA:CASE WHEN w IS NOT NULL THEN collect({id:id(w),name:w.name}) ELSE [] END} as oraRes\n" +
            "WITH {name: or.name,id:id(or), children: CASE WHEN ora IS NOT NULL THEN collect({id:id(ora),name:ora.name,wtaa:oraRes}) ELSE [] END} as orga\n" +
            "RETURN orga as result")
    List<Map<String,Object>> getAllWTAWithWTAId(long countryId, long wtaId);
}