package com.kairos.persistence.repository.user.client;

import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by oodles on 7/10/16.
 */
@Repository
public interface ContactAddressGraphRepository extends Neo4jBaseRepository<ContactAddress,Long>{

    @Query("MATCH (organization:OrganizationBaseEntity) WHERE id(organization)={0} WITH organization " +
            "OPTIONAL MATCH (organization)-[:" + CONTACT_ADDRESS + "]->(contactAddress:ContactAddress)-[rel:" + MUNICIPALITY + "]->(municipality:Municipality) \n" +
            " OPTIONAL MATCH (contactAddress)-[zipRel:" + ZIP_CODE + "]->(zipCode:ZipCode)" +
            "RETURN contactAddress,rel,municipality,zipRel,zipCode ")
    ContactAddress getContactAddressOfOrganization(Long unitId);

}
