package com.kairos.persistence.model.organization.union;

import org.springframework.data.neo4j.annotation.QueryResult;
import java.util.List;
@QueryResult
public class LocationSectorQueryResult {

private List<Location> locations;
private List<Sector> sectors;

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public List<Sector> getSectors() {
        return sectors;
    }

    public void setSectors(List<Sector> sectors) {
        this.sectors = sectors;
    }
}
