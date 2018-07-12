package com.kairos.activity.counter;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class InitialAccessGroupKPIDataDTO {
    private Map<Long, List<BigInteger>> accessGroupKPIIdsMap;

    public InitialAccessGroupKPIDataDTO(){

    }

    public InitialAccessGroupKPIDataDTO(Map accessGroupKPIIdsMap){
        this.accessGroupKPIIdsMap = accessGroupKPIIdsMap;
    }

    public Map<Long, List<BigInteger>> getAccessGroupKPIIdsMap() {
        return accessGroupKPIIdsMap;
    }

    public void setAccessGroupKPIIdsMap(Map<Long, List<BigInteger>> accessGroupKPIIdsMap) {
        this.accessGroupKPIIdsMap = accessGroupKPIIdsMap;
    }
}
