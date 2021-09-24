package com.kairos.dto.user.organization;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ImportTimeSlotListDTO {

    private List<ImportTimeSlotDTO> importTimeSlotDTOList;
}