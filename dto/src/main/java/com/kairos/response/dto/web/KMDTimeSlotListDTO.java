package com.kairos.response.dto.web;

import java.util.List;

/**
 * Created by oodles on 26/9/17.
 */
public class KMDTimeSlotListDTO {

    private List<KMDTimeSlotDTO> kmdTimeSlotDTOList;

    public List<KMDTimeSlotDTO> getKmdTimeSlotDTOList() {
        return kmdTimeSlotDTOList;
    }

    public void setKmdTimeSlotDTOList(List<KMDTimeSlotDTO> kmdTimeSlotDTOList) {
        this.kmdTimeSlotDTOList = kmdTimeSlotDTOList;
    }
}
