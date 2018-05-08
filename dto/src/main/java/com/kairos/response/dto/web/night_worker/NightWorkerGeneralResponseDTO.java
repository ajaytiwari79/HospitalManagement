package com.kairos.response.dto.web.night_worker;

import com.kairos.persistence.model.enums.PersonType;

import java.math.BigInteger;
import java.time.LocalDate;

/**
 * Created by prerna on 8/5/18.
 */
public class NightWorkerGeneralResponseDTO {

    private BigInteger id;
    private boolean nightWorker;
    private LocalDate startDate;
    private PersonType personType;
    private int questionnaireFrequency;

    public NightWorkerGeneralResponseDTO(){
        // default constructor
    }
}
