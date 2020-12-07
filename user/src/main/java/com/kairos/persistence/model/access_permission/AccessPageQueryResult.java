package com.kairos.persistence.model.access_permission;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kairos.dto.TranslationInfo;
import com.kairos.enums.OrganizationCategory;
import com.kairos.persistence.model.common.TranslationConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.kairos.commons.utils.ObjectUtils.isEmpty;


/**
 * Created by prabjot on 26/1/17.
 */
@QueryResult
@NoArgsConstructor
@Getter
@Setter
public class AccessPageQueryResult {

    private long id;
    private String name;
    private boolean selected;
    private boolean module;
    private boolean read;
    private boolean write;
    private boolean active;
    private String moduleId;
    private Long parentId;
    private int sequence;
    private Boolean editable;
    private boolean accessibleForHub;
    private boolean accessibleForUnion;
    private boolean accessibleForOrganization;
    private List<AccessPageQueryResult> children = new ArrayList<>();
    private List<OrganizationCategory> accessibleFor = new ArrayList<>();
    private boolean hasSubTabs;
    private Map<String,String> translatedNames;
    private Map<String,String> translatedDescriptions;
    @Convert(TranslationConverter.class)
    private Map<String, TranslationInfo> translations;
    private String helperText;
    private String url;

    @JsonIgnore
    private AccessPage accessPage;


    public void setTranslatedNames(Map<String, String> translatedNames) {
        if(isEmpty(translatedNames)){
            translatedNames.put("english",this.name);
        }
        this.translatedNames = translatedNames;
    }

    @Override
    public String toString() {
        return "AccessPageQueryResult{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", selected=" + selected +
                ", module=" + module +
                ", read=" + read +
                ", write=" + write +
                ", active=" + active +
                ", moduleId='" + moduleId + '\'' +
                ", children=" + children +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccessPageQueryResult)) return false;
        AccessPageQueryResult that = (AccessPageQueryResult) o;
        return getId() == that.getId() &&
                isSelected() == that.isSelected() &&
                isModule() == that.isModule() &&
                isRead() == that.isRead() &&
                isWrite() == that.isWrite() &&
                isActive() == that.isActive() &&
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(getModuleId(), that.getModuleId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), isSelected(), isModule(), isRead(), isWrite(), isActive(), getModuleId());
    }
}
