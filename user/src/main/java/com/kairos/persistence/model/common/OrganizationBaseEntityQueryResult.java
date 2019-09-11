package com.kairos.persistence.model.common;
/*
 *Created By Pavan on 6/6/19
 *
 */

import com.kairos.enums.OrganizationLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrganizationBaseEntityQueryResult {
    private String name;
    private long id;
    private List<QueryResult> children = new ArrayList<>();
    private List<QueryResult> units=new ArrayList<>();
    private boolean isAccessable = false;
    private String type;
    private boolean kairosHub;
    private boolean preKairos;
    private boolean isEnabled;
    private boolean isParentOrganization;
    private String timeZone;
    private Boolean union;
    private OrganizationLevel organizationLevel;
    private Long hubId;
    private boolean hasPermission;
}
