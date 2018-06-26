package com.kairos.persistance.model.template_type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * @Auther vikash patwal
 */

@Document(collection = "template_type")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TemplateType extends MongoBaseEntity {


    @NotNullOrEmpty(message = "templateName cannot be empty ")
    @Pattern(message = "Numbers and Special characters are not allowed",regexp = "^[a-zA-Z\\s]+$")
    private String templateName;

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }


}

