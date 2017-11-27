package com.kairos.service.position;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.agreement.cta.CostTimeAgreement;
import com.kairos.persistence.model.user.agreement.cta.RuleTemplate;
import com.kairos.persistence.model.user.agreement.wta.RuleTemplateCategoryDTO;
import com.kairos.persistence.model.user.agreement.wta.WTAWithCountryAndOrganizationTypeDTO;
import com.kairos.persistence.model.user.agreement.wta.WorkingTimeAgreement;
import com.kairos.persistence.model.user.agreement.wta.templates.RuleTemplateCategory;
import com.kairos.persistence.model.user.agreement.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.model.user.agreement.wta.templates.template_types.*;
import com.kairos.persistence.model.user.client.ClientMinimumDTO;
import com.kairos.persistence.model.user.country.EmploymentType;

import com.kairos.persistence.model.user.expertise.Expertise;

import com.kairos.persistence.model.user.position.PositionCtaWtaQueryResult;
import com.kairos.persistence.model.user.position.PositionName;

import com.kairos.persistence.model.user.position.UnitEmploymentPosition;

import com.kairos.persistence.model.user.position.UnitEmploymentPositionQueryResult;

import com.kairos.persistence.model.user.staff.Staff;
import com.kairos.persistence.model.user.staff.UnitEmployment;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.agreement.cta.CollectiveTimeAgreementGraphRepository;
import com.kairos.persistence.repository.user.agreement.wta.WorkingTimeAgreementGraphRepository;
import com.kairos.persistence.repository.user.client.ClientGraphRepository;
import com.kairos.persistence.repository.user.country.EmploymentTypeGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;

import com.kairos.persistence.repository.user.position.PositionNameGraphRepository;
import com.kairos.persistence.repository.user.position.UnitEmploymentPositionGraphRepository;
import com.kairos.persistence.repository.user.position.PositionCodeGraphRepository;

import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.persistence.repository.user.staff.UnitEmploymentGraphRepository;
import com.kairos.response.dto.web.UnitEmploymentPositionDTO;
import com.kairos.response.dto.web.PositionWrapper;
import com.kairos.service.UserBaseService;
import com.kairos.service.agreement.wta.WTAService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.service.staff.StaffService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.kairos.constants.AppConstants.*;

/**
 * Created by pawanmandhan on 26/7/17.
 */


@Transactional
@Service

public class UnitEmploymentPositionService extends UserBaseService {
    private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());

    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject
    private UnitEmploymentPositionGraphRepository unitEmploymentPositionGraphRepository;
    @Inject
    private PositionNameGraphRepository positionNameGraphRepository;
    @Inject
    private ExpertiseGraphRepository expertiseGraphRepository;
    @Inject
    private UnitEmploymentGraphRepository unitEmploymentGraphRepository;
    @Inject
    private WorkingTimeAgreementGraphRepository workingTimeAgreementGraphRepository;
    @Inject
    private CollectiveTimeAgreementGraphRepository costTimeAgreementGraphRepository;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private StaffService staffService;
    @Inject
    private EmploymentTypeGraphRepository employmentTypeGraphRepository;
    @Inject
    private OrganizationService organizationService;
    @Inject
    private PositionNameService positionNameService;
    @Inject
    private WTAService wtaService;
    @Inject
    private ClientGraphRepository clientGraphRepository;

    public UnitEmploymentPosition createUnitEmploymentPosition(Long id, long unitEmploymentId, UnitEmploymentPositionDTO unitEmploymentPositionDTO, String type) {
        UnitEmployment unitEmployment = unitEmploymentGraphRepository.findOne(unitEmploymentId);
        if (!Optional.ofNullable(unitEmployment).isPresent()) {
            throw new DataNotFoundByIdException("Invalid UnitEmployment id" + unitEmploymentId);
        }

        Organization organization = organizationService.getOrganizationDetail(id, type);
        if (!organization.isParentOrganization()) {
            organization = organizationService.getParentOfOrganization(organization.getId());

        }
        UnitEmploymentPosition unitEmploymentPosition = preparePosition(unitEmploymentPositionDTO, organization, id);
        List<UnitEmploymentPosition> unitEmploymentPositions = unitEmployment.getUnitEmploymentPositions();

        unitEmploymentPositions.add(unitEmploymentPosition);
        unitEmployment.setUnitEmploymentPositions(unitEmploymentPositions);
        save(unitEmployment);
        unitEmploymentPosition.setStaff(null);
        return unitEmploymentPosition;
    }


    public PositionWrapper updateUnitEmploymentPosition(long positionId, UnitEmploymentPositionDTO unitEmploymentPositionDTO) {

        List<ClientMinimumDTO> clientMinimumDTO = clientGraphRepository.getCitizenListForThisContactPerson(unitEmploymentPositionDTO.getStaffId());
        if (clientMinimumDTO.size() > 0) {
            return new PositionWrapper(clientMinimumDTO);
        }

        UnitEmploymentPosition oldUnitEmploymentPosition = unitEmploymentPositionGraphRepository.findOne(positionId);
        if (!Optional.ofNullable(oldUnitEmploymentPosition).isPresent()) {
            throw new DataNotFoundByIdException("Invalid positionId id " + positionId + " while updating the position");
        }
        preparePosition(oldUnitEmploymentPosition, unitEmploymentPositionDTO);
        save(oldUnitEmploymentPosition);
        return new PositionWrapper(oldUnitEmploymentPosition);

    }


    public boolean removePosition(long positionId) {

        UnitEmploymentPosition unitEmploymentPosition = unitEmploymentPositionGraphRepository.findOne(positionId);
        if (unitEmploymentPosition == null) {
            return false;
        }
        unitEmploymentPosition.setDeleted(false);
        save(unitEmploymentPosition);
        if (unitEmploymentPositionGraphRepository.findOne(positionId).isDeleted()) {
            return false;
        }
        return true;
    }


    public UnitEmploymentPosition getUnitEmploymentPosition(long positionId) {
        return unitEmploymentPositionGraphRepository.findOne(positionId);
    }
    /*
    * Created by vipul
    * 4-august-17
    * used to get all positions based on unitEmployment
    * */

    public List<UnitEmploymentPositionQueryResult> getAllUnitEmploymentPositions(long unitEmploymentId) {
        UnitEmployment unitEmployment = unitEmploymentGraphRepository.findOne(unitEmploymentId);

        if (unitEmployment == null) {
            throw new DataNotFoundByIdException("Invalid UnitEmployment id" + unitEmploymentId);
        }
        return unitEmploymentPositionGraphRepository.findAllUnitEmploymentPositions(unitEmploymentId);

    }

    private UnitEmploymentPosition preparePosition(UnitEmploymentPositionDTO unitEmploymentPositionDTO, Organization organization, Long unitId) {
        UnitEmploymentPosition unitEmploymentPosition = new UnitEmploymentPosition();

        //String name, String description, Expertise expertise, CostTimeAgreement cta, WorkingTimeAgreement wta
        /*WTAWithCountryAndOrganizationTypeDTO wtaWithRuleTemplateDTO = workingTimeAgreementGraphRepository.getWTAByExpertiseAndCountry(positionDTO.getExpertiseId());


        if (!Optional.ofNullable(wtaWithRuleTemplateDTO.getName()).isPresent()) {
            logger.info("Expertise Doesn't contains WTA.Please select different Expertise" + positionDTO.getExpertiseId());
            throw new DataNotFoundByIdException("Expertise Doesn't contains WTA.Please select different Expertise");
        }



        WorkingTimeAgreement wta = copyWTASettingAndRuleTemplateWithCategory(wtaWithRuleTemplateDTO);*/

        Optional<WorkingTimeAgreement> wta = workingTimeAgreementGraphRepository.findById(unitEmploymentPositionDTO.getWtaId());
        if(!wta.isPresent()){
            throw new DataNotFoundByIdException("Invalid wta id ");
        }
        unitEmploymentPosition.setWorkingTimeAgreement(wta.get());


        CostTimeAgreement cta = (unitEmploymentPositionDTO.getCtaId() == null)? null:
                costTimeAgreementGraphRepository.findOne(unitEmploymentPositionDTO.getCtaId());
        if(cta != null){
            unitEmploymentPosition.setCta(cta);
        }

        Optional<Expertise> expertise = expertiseGraphRepository.findById(unitEmploymentPositionDTO.getExpertiseId());
        if(!expertise.isPresent()){
            throw new DataNotFoundByIdException("Invalid expertise id");
        }


       /* if (!Optional.ofNullable(wtaWithRuleTemplateDTO.getExpertise()).isPresent()) {
            throw new DataNotFoundByIdException("Invalid Expertize" + positionDTO.getExpertiseId());
        }*/
        unitEmploymentPosition.setExpertise(expertise.get());

        WTAWithCountryAndOrganizationTypeDTO wtaWithRuleTemplateDTO = workingTimeAgreementGraphRepository.getWTAByExpertiseAndCountry(unitEmploymentPositionDTO.getExpertiseId());

        if (!Optional.ofNullable(wtaWithRuleTemplateDTO.getExpertise()).isPresent()) {
            throw new DataNotFoundByIdException("Invalid Expertize" + unitEmploymentPositionDTO.getExpertiseId());
        }
        unitEmploymentPosition.setExpertise(wtaWithRuleTemplateDTO.getExpertise());

        EmploymentType employmentType = organizationGraphRepository.getEmploymentTypeByOrganizationAndEmploymentId(organization.getId(), unitEmploymentPositionDTO.getEmploymentTypeId(), false);
        if (employmentType == null) {
            throw new DataNotFoundByIdException("Employment Type does not exist in unit " + employmentType.getId() + " AND " + unitEmploymentPositionDTO.getEmploymentTypeId());
        }
        unitEmploymentPosition.setEmploymentType(employmentType);
        UnitEmployment positionCode = unitEmploymentPositionDTO.getPositionCodeByUnitIdAndId(organization.getId(), unitEmploymentPositionDTO.getPositionNameId());
        if (!Optional.ofNullable(positionCode).isPresent()) {
            throw new DataNotFoundByIdException("position Name does not exist in unit " + unitEmploymentPositionDTO.getPositionNameId());
        }
        unitEmploymentPosition.setPositionCode(positionCode);

        /*CostTimeAgreement cta = costTimeAgreementGraphRepository.findOne(positionDTO.getCtaId());
         if (cta == null) {
            throw new DataNotFoundByIdException("Invalid CTA");
        }
        position.setParent(cta);<String, Object>

        */

        Staff staff = staffGraphRepository.findOne(unitEmploymentPositionDTO.getStaffId());
        if (!Optional.ofNullable(staff).isPresent()) {
            throw new DataNotFoundByIdException("Invalid Staff Id" + unitEmploymentPositionDTO.getStaffId());
        }
        unitEmploymentPosition.setStaff(staff);
        unitEmploymentPosition.setStartDate(unitEmploymentPositionDTO.getStartDate());
        unitEmploymentPosition.setEndDate(unitEmploymentPositionDTO.getEndDate());

        unitEmploymentPosition.setTotalWeeklyHours(unitEmploymentPositionDTO.getTotalWeeklyHours());
        unitEmploymentPosition.setAvgDailyWorkingHours(unitEmploymentPositionDTO.getAvgDailyWorkingHours());
        unitEmploymentPosition.setHourlyWages(unitEmploymentPositionDTO.getHourlyWages());
        unitEmploymentPosition.setSalary(unitEmploymentPositionDTO.getSalary());
        unitEmploymentPosition.setWorkingDaysInWeek(unitEmploymentPositionDTO.getWorkingDaysInWeek());

        return unitEmploymentPosition;
    }


  private void preparePosition(UnitEmploymentPosition oldUnitEmploymentPosition, UnitEmploymentPositionDTO unitEmploymentPositionDTO) {
        if (!oldUnitEmploymentPosition.getExpertise().getId().equals(unitEmploymentPositionDTO.getExpertiseId())) {
            WTAWithRuleTemplateDTO wtaWithRuleTemplateDTO = workingTimeAgreementGraphRepository.getWTAByExpertiseAndCountry(unitEmploymentPositionDTO.getExpertiseId());


        /*if (!oldPosition.getExpertise().getId().equals(positionDTO.getExpertiseId())) {
            WTAWithCountryAndOrganizationTypeDTO wtaWithRuleTemplateDTO = workingTimeAgreementGraphRepository.getWTAByExpertiseAndCountry(positionDTO.getExpertiseId());
            System.out.println(Optional.ofNullable(wtaWithRuleTemplateDTO.getExpertise()).isPresent());
            if (!Optional.ofNullable(wtaWithRuleTemplateDTO.getExpertise()).isPresent()) {
                throw new DataNotFoundByIdException("Invalid Expertize" + unitEmploymentPositionDTO.getExpertiseId());
            }
            if (!Optional.ofNullable(wtaWithRuleTemplateDTO.getName()).isPresent()) {
                logger.info("Expertise Doesn't contains WTA.Please select different Expertise" + positionDTO.getExpertiseId());
                throw new DataNotFoundByIdException("Expertise Doesn't contains WTA.Please select different Expertise");
            } else {
                WorkingTimeAgreement wta = copyWTASettingAndRuleTemplateWithCategory(wtaWithRuleTemplateDTO);
<<<<<<< HEAD:kairos-user/src/main/java/com/kairos/service/position/PositionService.java
                WorkingTimeAgreement oldWta = oldPosition.getWorkingTimeAgreement();
                oldPosition.setWorkingTimeAgreement(wta);
                wta.setParentWTA(oldWta);
=======
                WorkingTimeAgreement oldWta = oldUnitEmploymentPosition.getWta();
                oldUnitEmploymentPosition.setWta(wta);
                wta.setWta(oldWta);
>>>>>>> b503068... changed position to UEP:kairos-user/src/main/java/com/kairos/service/position/UnitEmploymentPositionService.java
                save(wta);
                workingTimeAgreementGraphRepository.breakRelationFromOldWTA(oldUnitEmploymentPosition.getId(), oldWta.getId());
            }
<<<<<<< HEAD:kairos-user/src/main/java/com/kairos/service/position/PositionService.java
        }*/

        Optional<WorkingTimeAgreement> wta = workingTimeAgreementGraphRepository.findById(positionDTO.getWtaId());
        if(!wta.isPresent()){
            throw new DataNotFoundByIdException("Invalid wta id ");
        }
        oldPosition.setWorkingTimeAgreement(wta.get());


        CostTimeAgreement cta = (positionDTO.getCtaId() == null)? null:
                costTimeAgreementGraphRepository.findOne(positionDTO.getCtaId());
        if(cta != null){
            oldPosition.setCta(cta);
        }

        Optional<Expertise> expertise = expertiseGraphRepository.findById(positionDTO.getExpertiseId());
        if(!expertise.isPresent()){
            throw new DataNotFoundByIdException("Invalid expertise id");
        }


        if (!oldPosition.getPositionName().getId().equals(positionDTO.getPositionNameId())) {
            PositionName positionName = positionNameGraphRepository.findOne(positionDTO.getPositionNameId());
            if (!Optional.ofNullable(positionName).isPresent()) {
                throw new DataNotFoundByIdException("PositionName Cannot be null" + positionDTO.getPositionNameId());
            }
            oldPosition.setPositionName(positionName);
        }


        if (!oldUnitEmploymentPosition.getPositionCode().getId().equals(unitEmploymentPositionDTO.getPositionNameId())) {
            PositionCode positionCode = positionCodeGraphRepository.findOne(unitEmploymentPositionDTO.getPositionNameId());
            if (!Optional.ofNullable(positionCode).isPresent()) {
                throw new DataNotFoundByIdException("PositionName Cannot be null" + unitEmploymentPositionDTO.getPositionNameId());
            }
            oldUnitEmploymentPosition.setPositionCode(positionCode);

        }

        if (!oldUnitEmploymentPosition.getEmploymentType().getId().equals(unitEmploymentPositionDTO.getEmploymentTypeId())) {
            EmploymentType employmentType = employmentTypeGraphRepository.findOne(unitEmploymentPositionDTO.getEmploymentTypeId());
            if (!Optional.ofNullable(employmentType).isPresent()) {
                throw new DataNotFoundByIdException("employmentType Cannot be null" + unitEmploymentPositionDTO.getEmploymentTypeId());
            }
            oldUnitEmploymentPosition.setEmploymentType(employmentType);
        }


        oldUnitEmploymentPosition.setStartDate(unitEmploymentPositionDTO.getStartDate());
        oldUnitEmploymentPosition.setEndDate(unitEmploymentPositionDTO.getEndDate());
        oldUnitEmploymentPosition.setWorkingDaysInWeek(unitEmploymentPositionDTO.getWorkingDaysInWeek());
        oldUnitEmploymentPosition.setTotalWeeklyHours(unitEmploymentPositionDTO.getTotalWeeklyHours());
        oldUnitEmploymentPosition.setAvgDailyWorkingHours(unitEmploymentPositionDTO.getAvgDailyWorkingHours());
        oldUnitEmploymentPosition.setHourlyWages(unitEmploymentPositionDTO.getHourlyWages());
        oldUnitEmploymentPosition.setSalary(unitEmploymentPositionDTO.getSalary());

    }

    /*
     * @auth vipul
     * used to get all positions of organization n buy organization and staff Id
     * */
    public List<UnitEmploymentPositionQueryResult> getAlllUnitEmploymentPositionsOfStaff(long id, long unitEmploymentId, long staffId, String type) {


        Long unitId = organizationService.getOrganization(id, type);

        Staff staff = staffGraphRepository.findOne(staffId);
        if (!Optional.ofNullable(staff).isPresent()) {
            throw new DataNotFoundByIdException("Invalid Staff Id" + staffId);
        }

        return unitEmploymentPositionGraphRepository.getAllUnitEmploymentPositionByStaff(unitId, unitEmploymentId, staffId);
    }

    public PositionCtaWtaQueryResult getCtaAndWtaByExpertiseId(Long unitId,Long expertiseId){
        return positionGraphRepository.getCtaAndWtaByExpertise(unitId,expertiseId);
    }

}
