package com.kairos.persistence.model.pay_table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import com.kairos.persistence.model.common.UserTranslationInfoConverter;
import com.kairos.persistence.model.organization.Level;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Created by vipul on 15/3/18.
 */
@QueryResult
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class PayTableResponse {
    private Long id;
    private String name;
    private String shortName;
    private LocalDate startDateMillis;
    private LocalDate endDateMillis;
    private String paymentUnit;
    private Level level;
    private List<PayGrade> payGrades;
    private String description;
    private Boolean published;
    private Boolean editable;
    private BigDecimal percentageValue;
    @Convert(UserTranslationInfoConverter.class)
    private Map<String, TranslationInfo> translations;


    public PayTableResponse() {
    }

    public PayTableResponse(String name, String shortName, String description, LocalDate startDateMillis, LocalDate endDateMillis, Boolean published, String paymentUnit, Boolean editable) {
        this.name = name;
        this.published = published;
        this.description = description;
        this.shortName = shortName;
        this.startDateMillis = startDateMillis;
        this.editable = editable;
        this.endDateMillis = endDateMillis;
        this.paymentUnit = paymentUnit;


    }
    public String getName() {
        return TranslationUtil.getName(TranslationUtil.convertUnmodifiableMapToModifiableMap(translations),name);
    }

    public String getDescription() {
        return TranslationUtil.getDescription(TranslationUtil.convertUnmodifiableMapToModifiableMap(translations),description);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PayTableQueryResult{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", shortName='").append(shortName).append('\'');
        sb.append(", startDateMillis=").append(startDateMillis);
        sb.append(", endDateMillis=").append(endDateMillis);
        sb.append(", description='").append(description).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
