package com.kairos.persistence.model.access_permission;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.kairos.constants.UserMessagesConstants.ERROR_NAME_NOTNULL;
import static com.kairos.persistence.model.constants.RelationshipConstants.SUB_PAGE;


/**
 * Created by arvind on 24/10/16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity
@Getter
@Setter
@NoArgsConstructor
public class AccessPage extends UserBaseEntity {

    @NotNull(message = ERROR_NAME_NOTNULL)
    private String name;
    private boolean isModule;
    private boolean kpiEnabled;
    private String moduleId;
    private boolean active;
    //this value is "false" only in case of "moduleId" : "module_1"
    private Boolean editable;


    @Relationship(type = SUB_PAGE)
    private List<AccessPage> subPages=new ArrayList<>();

    private int sequence;

    public AccessPage(String name){
        this.name = name;
    }

    public AccessPage(String name, String moduleId) {
        this.name = name;
        this.moduleId = moduleId;
    }

    public AccessPage(String name, boolean isModule, String moduleId) {
        this.name = name;
        this.isModule = isModule;
        this.moduleId = moduleId;
    }
}
