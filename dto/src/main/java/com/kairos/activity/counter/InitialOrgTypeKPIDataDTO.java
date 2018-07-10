package com.kairos.activity.counter;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class InitialOrgTypeKPIDataDTO {
    private Map<Long, List<BigInteger>> orgTypeKPIIdsMap;

    public InitialOrgTypeKPIDataDTO(){}

    public InitialOrgTypeKPIDataDTO(Map<Long, List<BigInteger>> orgTypeKPIIdsMap){
        this.orgTypeKPIIdsMap=orgTypeKPIIdsMap;
    }

    public Map<Long, List<BigInteger>> getOrgTypeKPIIdsMap() {
        return orgTypeKPIIdsMap;
    }

    public void setOrgTypeKPIIdsMap(Map<Long, List<BigInteger>> orgTypeKPIIdsMap) {
        this.orgTypeKPIIdsMap = orgTypeKPIIdsMap;
    }
}
