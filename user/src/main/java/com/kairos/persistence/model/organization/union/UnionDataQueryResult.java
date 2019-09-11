package com.kairos.persistence.model.organization.union;

import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.ZipCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
@Getter
@Setter
@NoArgsConstructor
public class UnionDataQueryResult {

    private Organization union;
    private List<Sector> sectors;
    private List<Location> locations;
    private ContactAddress address;
    private ZipCode zipCode;
    private Municipality municipality;
    private List<Municipality> municipalities;
    private Country country;
}
