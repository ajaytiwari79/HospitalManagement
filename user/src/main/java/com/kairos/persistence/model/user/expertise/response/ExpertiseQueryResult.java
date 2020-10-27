package com.kairos.persistence.model.user.expertise.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import com.kairos.dto.activity.cta_compensation_setting.CTACompensationSettingDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.shift.BreakPaymentSetting;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.services.OrganizationService;
import com.kairos.persistence.model.organization.union.Location;
import com.kairos.persistence.model.organization.union.Sector;
import com.kairos.persistence.model.pay_table.PayTable;
import com.kairos.persistence.model.user.expertise.CareDays;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.time.LocalDate;
import java.util.*;

import static com.kairos.commons.utils.DateUtils.startDateIsEqualsOrBeforeEndDate;

/**
 * Created by vipul on 28/3/18.
 */
@QueryResult
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class ExpertiseQueryResult {
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer fullTimeWeeklyMinutes;
    private Integer numberOfWorkingDaysInWeek;
    private Long id;
    private Boolean published;
    private Boolean history;
    private List<CareDays> seniorDays;
    private List<CareDays> childCareDays;
    private Level organizationLevel;
    private List<OrganizationService> organizationService;
    //TODO in current unwinded property cant be set to any nested domain to QueryResult DTO , We will change if in feature this will handle
    private Organization union;
    private PayTable payTable;
    private List<Map<String, Object>> seniorityLevels;
    private BreakPaymentSetting breakPaymentSetting;
    private Sector sector;
    private Map<String,Object> unionRepresentative;// in case of expertise at unit level only
    private Location unionLocation;// in case of expertise at unit level only
    private Set<Long> supportedUnitIds;
    private List<ExpertiseLineQueryResult> expertiseLines=new ArrayList<>();
    private CTACompensationSettingDTO ctaCompensationSetting;
    private Long countryId;
    private Long unitId;
    private Map<String, TranslationInfo> translations;

    @JsonIgnore
    public ExpertiseLineQueryResult getCurrentlyActiveLine(){
        ExpertiseLineQueryResult currentExpertiseLineQueryResult=null;
        for (ExpertiseLineQueryResult expertiseLineQueryResult:this.getExpertiseLines()) {
            if(startDateIsEqualsOrBeforeEndDate(expertiseLineQueryResult.getStartDate(),LocalDate.now()) && (expertiseLineQueryResult.getEndDate()==null || startDateIsEqualsOrBeforeEndDate(LocalDate.now(),expertiseLineQueryResult.getEndDate()))){
                currentExpertiseLineQueryResult=expertiseLineQueryResult;
                break;
            }
        }
        return currentExpertiseLineQueryResult;
    }

    public List<ExpertiseLineQueryResult> getExpertiseLines() {
        expertiseLines.forEach(k->k.setBreakPaymentSetting(this.breakPaymentSetting));
        return expertiseLines;
    }

    public String getName() {
        Map<String,TranslationInfo> modifiableMap =  TranslationUtil.convertUnmodifiableMapToModifiableMap(translations);
        if(TranslationUtil.isVerifyTranslationDataOrNotForName(modifiableMap)) {
            return modifiableMap.get(UserContext.getUserDetails().getLanguage()).getName();
        }else {
            return name;
        }
    }

    public String getDescription() {
        Map<String,TranslationInfo> modifiableMap = TranslationUtil.convertUnmodifiableMapToModifiableMap(translations);
        if(TranslationUtil.isVerifyTranslationDataOrNotForDescription(modifiableMap)) {
            return modifiableMap.get(UserContext.getUserDetails().getLanguage()).getDescription();
        }else {
            return description;
        }
    }

}
