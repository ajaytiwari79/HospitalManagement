package com.kairos.dto.activity.activity.activity_tabs;

import com.kairos.dto.activity.activity.TranslationInfo;
import com.kairos.dto.user.country.tag.TagDTO;
import com.kairos.enums.TimeTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This DTO is made just to customize the default view at frontend
 * For example:-
 * Default domain have
 * List<BigInteger> tags  but required List<Tag> tags
 * @Note:- please update the comment list whenever any more changes done
 */
@Getter
@Setter
public class GeneralActivityTabWithTagDTO {

    private String name;
    private String code;
    private String printoutSymbol;
    private BigInteger categoryId;
    private Boolean colorPresent;
    private String backgroundColor;
    private String description;
    private boolean isActive =true;
    private  String shortName;
    private boolean eligibleForUse=true;
    private String originalIconName;
    private String modifiedIconName;
    private String ultraShortName;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<TagDTO> tags = new ArrayList<>();
    private Integer addTimeTo;
    private BigInteger timeTypeId;
    private boolean onCallTimePresent;
    private Boolean negativeDayBalancePresent;
    private TimeTypeEnum timeType;
    private String content;
    private String originalDocumentName;
    private String modifiedDocumentName;
    private Map<String, TranslationInfo> translations;
}
