package com.kairos.persistence.model.address;

import com.kairos.persistence.model.organization.union.Sector;
import com.kairos.persistence.model.user.region.ZipCode;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
public class ZipCodeSectorQueryResult {

    private List<ZipCode> zipCodes;
    private List<Sector> sectors;


    public List<ZipCode> getZipCodes() {
        return zipCodes;
    }

    public void setZipCodes(List<ZipCode> zipCodes) {
        this.zipCodes = zipCodes;
    }

    public List<Sector> getSectors() {
        return sectors;
    }

    public void setSectors(List<Sector> sectors) {
        this.sectors = sectors;
    }

}
