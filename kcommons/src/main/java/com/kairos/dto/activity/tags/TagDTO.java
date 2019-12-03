package com.kairos.dto.activity.tags;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.user.country.tag.PenaltyScoreDTO;
import com.kairos.enums.MasterDataTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class TagDTO {
    private BigInteger id;
    private String name;
    private MasterDataTypeEnum masterDataType;
    private boolean countryTag;
    private long countryId;
    private long organizationId;
    private Long orgTypeId;
    private List<Long> orgSubTypeIds;
    private PenaltyScoreDTO penaltyScore;
    private LocalDate startDate;
    private LocalDate endDate;
}
