package com.kairos.dto.user.organization.union;

import com.kairos.dto.user.organization.ZipCodeDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
public class UnionGlobalDataDTO {

    private List<ZipCodeDTO> zipCodes = new ArrayList<>();
    private List<SectorDTO> sectors = new ArrayList<>();
    private List<UnionDataDTO> unions = new ArrayList<>();

    public UnionGlobalDataDTO(List<ZipCodeDTO>zipCodes,List<SectorDTO> sectors) {
        this.zipCodes = zipCodes;
        this.sectors = sectors;
    }




}
