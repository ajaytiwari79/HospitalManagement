package com.kairos.persistence.model.organization;

import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.ZipCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by prabjot on 3/3/17.
 */
@QueryResult
@Getter
@Setter
public class OrganizationContactAddress {

    private Unit unit;
    private ContactAddress contactAddress;
    private ZipCode zipCode;
    private Municipality municipality;
    private Long organizationId;
}

