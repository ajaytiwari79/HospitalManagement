package com.kairos.service.access_permisson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.persistence.model.common.QueryResult;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.enums.OrganizationLevel;
import com.kairos.persistence.model.user.access_permission.AccessGroup;
import com.kairos.persistence.model.user.access_permission.AccessPage;
import com.kairos.persistence.model.user.access_permission.AccessPageCustomId;
import com.kairos.persistence.model.user.access_permission.Tab;
import com.kairos.persistence.model.user.staff.AccessPermission;
import com.kairos.persistence.model.user.staff.EmploymentAccessPageRelation;
import com.kairos.persistence.model.user.staff.Staff;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.access_permission.AccessGroupRepository;
import com.kairos.persistence.repository.user.access_permission.AccessPageCustomIdRepository;
import com.kairos.persistence.repository.user.access_permission.AccessPageRepository;
import com.kairos.persistence.repository.user.access_permission.AccessPermissionGraphRepository;
import com.kairos.persistence.repository.user.staff.EmploymentGraphRepository;
import com.kairos.persistence.repository.user.staff.EmploymentPageGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.service.UserBaseService;
import com.kairos.service.tree_structure.TreeStructureService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.inject.Inject;
import java.util.*;

import static com.kairos.constants.AppConstants.TEAM;
import static com.kairos.constants.AppConstants.ORGANIZATION;

/**
 * Created by prabjot on 3/1/17.
 */
@Transactional
@Service
public class AccessPageService extends UserBaseService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    AccessPageRepository accessPageRepository;
    @Inject
    AccessPermissionGraphRepository accessPermissionGraphRepository;
    @Inject
    EmploymentPageGraphRepository employmentPageGraphRepository;
    @Inject
    private EmploymentGraphRepository employmentGraphRepository;
    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private AccessGroupRepository accessGroupRepository;
    @Inject
    private TreeStructureService treeStructureService;
    @Inject
    private AccessPageCustomIdRepository accessPageCustomIdRepository;

    public AccessPage createAccessPage(@RequestBody AccessPage accessPage){
        accessPage.setModuleId(getTabId(accessPage.isModule()));
        //save(accessPage);
       /* List<AccessPermission> accessPermissions = accessPermissionGraphRepository.findAll();
        List<EmploymentAccessPageRelation> employmentAccessPageRelations = new ArrayList<>();
        for (AccessPermission accessPermission : accessPermissions) {
            EmploymentAccessPageRelation employmentAccessPageRelation = new EmploymentAccessPageRelation(accessPermission, accessPage);
            employmentAccessPageRelation.setRead(true);
            employmentAccessPageRelation.setWrite(true);
            employmentAccessPageRelation.setCreationDate(new DateTime().getMillis());
            employmentAccessPageRelation.setLastModificationDate(new DateTime().getMillis());
            employmentAccessPageRelations.add(employmentAccessPageRelation);
        }
        employmentPageGraphRepository.save(employmentAccessPageRelations);*/
        return accessPage;
    }

    public List<AccessPage> getAllAccessPage(){
        return accessPageRepository.findAll();
    }

    public void createAccessPageByXml(Tab tab){

        List<AccessPage> accessPages = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        for(Tab child : tab.getSubPages()){
            AccessPage accessPage = objectMapper.convertValue(child,AccessPage.class);
            accessPages.add(accessPage);
        }
        accessPageRepository.save(accessPages);
        //setPermissionToAccessPage();
    }

    public void setPermissionToAccessPage(){
        List<AccessPermission> accessPermissions = accessPermissionGraphRepository.findAll();
        List<AccessPage> accessPages = accessPageRepository.findAll();

        List<EmploymentAccessPageRelation> employmentAccessPageRelations = new ArrayList<>(accessPages.size());
        for(AccessPermission accessPermission : accessPermissions){
            for (AccessPage accessPage : accessPages) {
                EmploymentAccessPageRelation employmentAccessPageRelation = new EmploymentAccessPageRelation(accessPermission, accessPage);
                employmentAccessPageRelation.setRead(true);
                employmentAccessPageRelation.setWrite(false);
                employmentAccessPageRelation.setCreationDate(new DateTime().getMillis());
                employmentAccessPageRelation.setLastModificationDate(new DateTime().getMillis());
                employmentAccessPageRelations.add(employmentAccessPageRelation);
            }
        }
        employmentPageGraphRepository.save(employmentAccessPageRelations);
    }

    public void setPagePermissionToStaff(AccessPermission accessPermission,long accessGroupId) {
        List<AccessPage> accessPages = accessGroupRepository.getAccessPageByGroup(accessGroupId);
        List<EmploymentAccessPageRelation> employmentAccessPageRelations = new ArrayList<>(accessPages.size());
        for (AccessPage accessPage : accessPages) {
            EmploymentAccessPageRelation employmentAccessPageRelation = new EmploymentAccessPageRelation(accessPermission, accessPage);
            employmentAccessPageRelation.setRead(true);
            employmentAccessPageRelation.setWrite(true);
            employmentAccessPageRelation.setCreationDate(new DateTime().getMillis());
            employmentAccessPageRelation.setLastModificationDate(new DateTime().getMillis());
            employmentAccessPageRelations.add(employmentAccessPageRelation);
        }
        employmentPageGraphRepository.save(employmentAccessPageRelations);
    }

    public void setPagePermissionToAdmin(AccessPermission accessPermission) {
        List<AccessPage> accessPages = accessPageRepository.findAll();
        List<EmploymentAccessPageRelation> employmentAccessPageRelations = new ArrayList<>(accessPages.size());
        for (AccessPage accessPage : accessPages) {
            EmploymentAccessPageRelation employmentAccessPageRelation = new EmploymentAccessPageRelation(accessPermission, accessPage);
            employmentAccessPageRelation.setRead(true);
            employmentAccessPageRelation.setWrite(true);
            employmentAccessPageRelation.setCreationDate(new DateTime().getMillis());
            employmentAccessPageRelation.setLastModificationDate(new DateTime().getMillis());
            employmentAccessPageRelations.add(employmentAccessPageRelation);
        }
        employmentPageGraphRepository.save(employmentAccessPageRelations);
    }

    public List<Map<String, Object>> getWorkPlaces(long staffId, long unitId,String type) {
        Staff staff = staffGraphRepository.findOne(staffId);
        if (staff == null) {
            return null;
        }
        Organization unit;
        if(ORGANIZATION.equalsIgnoreCase(type)){
            unit = organizationGraphRepository.findOne(unitId);
        } else if(TEAM.equalsIgnoreCase(type)){
            unit = organizationGraphRepository.getOrganizationByTeamId(unitId);
            System.out.println("getting unit from team" + unit.getId());
        } else {
            throw new InternalError("type can not be null");
        }
        if (unit == null) {
            throw new InternalError("Organization not found");
        }
        List<AccessGroup> accessGroups;
        List<Map<String, Object>> units;

        Organization parentOrganization;
        if (unit.getOrganizationLevel().equals(OrganizationLevel.CITY)) {
            parentOrganization = organizationGraphRepository.getParentOrganizationOfCityLevel(unit.getId());

        } else {
            parentOrganization = organizationGraphRepository.getParentOfOrganization(unit.getId());
        }

        if (parentOrganization != null) {
            accessGroups = accessGroupRepository.getAccessGroups(parentOrganization.getId());
            units = organizationGraphRepository.getSubOrgHierarchy(parentOrganization.getId());
        } else {

            accessGroups = accessGroupRepository.getAccessGroups(unit.getId());
            units = organizationGraphRepository.getSubOrgHierarchy(unit.getId());
        }

        List<Map<String, Object>> employments;
        List<Map<String, Object>> workPlaces = new ArrayList<>();
        if (units.isEmpty() && unit.isParentOrganization()) {
            employments = new ArrayList<>();
            for (AccessGroup accessGroup : accessGroups) {
                QueryResult queryResult = new QueryResult();
                queryResult.setId(unit.getId());
                queryResult.setName(unit.getName());
                Map<String, Object> employment = employmentGraphRepository.getEmploymentOfParticularRole(staffId, unit.getId(), accessGroup.getId());
                if (employment != null && !employment.isEmpty()) {
                    employments.add(employment);
                    queryResult.setAccessable(true);
                } else {
                    queryResult.setAccessable(false);
                }
                Map<String, Object> workPlace = new HashMap<>();
                workPlace.put("id", accessGroup.getId());
                workPlace.put("name", accessGroup.getName());
                workPlace.put("tree", queryResult);
                workPlace.put("employments", employments);
                workPlaces.add(workPlace);
            }
            return workPlaces;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        List<QueryResult> list;
        List<Long> ids;
        for (AccessGroup accessGroup : accessGroups) {
            list = new ArrayList<>();
            ids = new ArrayList<>();
            employments = new ArrayList<>();
            for (Map<String, Object> unitData : units) {
                Map<String, Object> parentUnit = (Map<String, Object>) ((Map<String, Object>) unitData.get("data")).get("parent");
                long id = (long) parentUnit.get("id");
                Map<String, Object> employment;
                if (ids.contains(id)) {
                    for (QueryResult queryResult : list) {
                        if (queryResult.getId() == id) {
                            List<QueryResult> childs = queryResult.getChildren();
                            QueryResult child = objectMapper.convertValue(((Map<String, Object>) unitData.get("data")).get("child"), QueryResult.class);
                            employment = employmentGraphRepository.getEmploymentOfParticularRole(staffId, child.getId(), accessGroup.getId());
                            if (employment != null && !employment.isEmpty()) {
                                employments.add(employment);
                                child.setAccessable(true);
                            } else {
                                child.setAccessable(false);
                            }
                            childs.add(child);
                            break;
                        }
                    }
                } else {
                    List<QueryResult> queryResults = new ArrayList<>();
                    QueryResult child = objectMapper.convertValue(((Map<String, Object>) unitData.get("data")).get("child"), QueryResult.class);
                    employment = employmentGraphRepository.getEmploymentOfParticularRole(staffId, child.getId(), accessGroup.getId());
                    if (employment != null && !employment.isEmpty()) {
                        employments.add(employment);
                        child.setAccessable(true);
                    } else {
                        child.setAccessable(false);
                    }
                    queryResults.add(child);
                    QueryResult queryResult = new QueryResult((String) parentUnit.get("name"), id, queryResults);
                    employment = employmentGraphRepository.getEmploymentOfParticularRole(staffId, queryResult.getId(), accessGroup.getId());
                    if (employment != null && !employment.isEmpty()) {
                        employments.add(employment);
                        queryResult.setAccessable(true);
                    } else {
                        queryResult.setAccessable(false);
                    }
                    list.add(queryResult);
                }
                ids.add(id);
            }
            Map<String, Object> workPlace = new HashMap<>();
            workPlace.put("id", accessGroup.getId());
            workPlace.put("name", accessGroup.getName());
            workPlace.put("tree", treeStructureService.getTreeStructure(list));
            workPlace.put("employments", employments);
            workPlaces.add(workPlace);
        }
        return workPlaces;
    }

    private synchronized String getTabId(Boolean isModule){

        AccessPageCustomId accessPageCustomId = accessPageCustomIdRepository.findFirst();
        if(!Optional.ofNullable(accessPageCustomId).isPresent()){
            logger.error("AccessPageCustomId collection is not present");
            throw new InternalError("AccessPageCustomId collection is not present");
        }
        String content[];
        String tabId = null;
        if(isModule){
            content = accessPageCustomId.getModuleId().split("_");
            if(content.length>0){
                int id = Integer.parseInt(content[1]);
                id+=1;
                tabId = "module_" + id;
                accessPageCustomId.setModuleId(tabId);
            }
        } else {
            content = accessPageCustomId.getTabId().split("_");
            if(content.length>0){
                int id = Integer.parseInt(content[1]);
                id+=1;
                tabId = "tab_" + id;
                accessPageCustomId.setTabId(tabId);
            }
        }
        if(!Optional.ofNullable(tabId).isPresent()){
            throw new InternalError("tab id is not present");
        }
        save(accessPageCustomId);
        System.out.println("id -->" + tabId);
        return tabId;
    }
}
