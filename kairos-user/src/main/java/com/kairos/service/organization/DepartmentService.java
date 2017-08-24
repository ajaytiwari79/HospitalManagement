package com.kairos.service.organization;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.department.Department;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.service.UserBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by prabjot on 19/5/17.
 */
@Transactional
@Service
public class DepartmentService extends UserBaseService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private OrganizationGraphRepository organizationGraphRepository;

    public Organization createDepartment(long organizationId, Department department) {
        Organization organization = organizationGraphRepository.findOne(organizationId);
        if (organization == null) {
            throw new NullPointerException("organization can not be null");
        }
        List<Department> departments = (organization.getDepartments() == null) ? new ArrayList<>() : organization.getDepartments();
        departments.add(department);
        organization.setDepartments(departments);
        return organizationGraphRepository.save(organization);
    }


    public boolean manageStructure(long departmentId, List<Long> childIds) {
        organizationGraphRepository.linkDeptWithTeams(departmentId, childIds);
        return true;
    }


    public Organization addStaff(long organizationId, long departmentId, long userId) {
        return organizationGraphRepository.addStaff(organizationId, departmentId, userId);
    }

    public List<Department> getDepartment(Long organizationId) {
        return organizationGraphRepository.getAllDepartments(organizationId);
    }


    public List<Map<String, Object>> getDepartmentAccessibleOrganizations(Long departmentId) {
        Long organizationId = organizationGraphRepository.getOrganizationByDepartmentId(departmentId);
        logger.info("Department got OrganizationId = " + organizationId);
        return organizationGraphRepository.getOrganizationChildList(organizationId);
    }



}
