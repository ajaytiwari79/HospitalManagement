package com.kairos.dto.user.organization.union;

import com.kairos.dto.user.organization.ZipCodeDTO;
import java.util.List;

public class UnionGlobalDataDTO {

    private List<ZipCodeDTO> zipCodes;
    private List<SectorDTO> sectors;
    private List<UnionDataDTO> unions;

    public UnionGlobalDataDTO() {

    }
    public UnionGlobalDataDTO(List<ZipCodeDTO>zipCodes,List<SectorDTO> sectors) {
        this.zipCodes = zipCodes;
        this.sectors = sectors;
    }
    public List<ZipCodeDTO> getZipCodes() {
        return zipCodes;
    }

    public void setZipCodes(List<ZipCodeDTO> zipCodes) {
        this.zipCodes = zipCodes;
    }

    public List<SectorDTO> getSectors() {
        return sectors;
    }

    public void setSectors(List<SectorDTO> sectors) {
        this.sectors = sectors;
    }

    public List<UnionDataDTO> getUnions() {
        return unions;
    }

    public void setUnions(List<UnionDataDTO> unions) {
        this.unions = unions;
    }



}
