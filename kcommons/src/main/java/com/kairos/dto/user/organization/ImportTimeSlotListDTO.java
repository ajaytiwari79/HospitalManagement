package com.kairos.dto.user.organization;

import java.util.List;

public class ImportTimeSlotListDTO {

    private List<ImportTimeSlotDTO> importTimeSlotDTOList;

    public List<ImportTimeSlotDTO> getImportTimeSlotDTOList() {
        return importTimeSlotDTOList;
    }

    public void setImportTimeSlotDTOList(List<ImportTimeSlotDTO> importTimeSlotDTOList) {
        this.importTimeSlotDTOList = importTimeSlotDTOList;
    }
}