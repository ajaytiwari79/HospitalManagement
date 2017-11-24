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
<<<<<<< HEAD:kairos-user/src/main/java/com/kairos/service/position/PositionService.java
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.position.Position;
import com.kairos.persistence.model.user.position.PositionCtaWtaQueryResult;
import com.kairos.persistence.model.user.position.PositionName;
import com.kairos.persistence.model.user.position.PositionQueryResult;
=======
import com.kairos.persistence.model.user.position.UnitEmploymentPosition;
import com.kairos.persistence.model.user.position.PositionCode;
import com.kairos.persistence.model.user.position.UnitEmploymentPositionQueryResult;
>>>>>>> b503068... changed position to UEP:kairos-user/src/main/java/com/kairos/service/position/UnitEmploymentPositionService.java
import com.kairos.persistence.model.user.staff.Staff;
import com.kairos.persistence.model.user.staff.UnitEmployment;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.agreement.cta.CollectiveTimeAgreementGraphRepository;
import com.kairos.persistence.repository.user.agreement.wta.WorkingTimeAgreementGraphRepository;
import com.kairos.persistence.repository.user.client.ClientGraphRepository;
import com.kairos.persistence.repository.user.country.EmploymentTypeGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
<<<<<<< HEAD:kairos-user/src/main/java/com/kairos/service/position/PositionService.java
import com.kairos.persistence.repository.user.position.PositionGraphRepository;
import com.kairos.persistence.repository.user.position.PositionNameGraphRepository;
=======
import com.kairos.persistence.repository.user.position.UnitEmploymentPositionGraphRepository;
import com.kairos.persistence.repository.user.position.PositionCodeGraphRepository;
>>>>>>> b503068... changed position to UEP:kairos-user/src/main/java/com/kairos/service/position/UnitEmploymentPositionService.java
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
<<<<<<< HEAD:kairos-user/src/main/java/com/kairos/service/position/PositionService.java
public class PositionService extends UserBaseService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

=======
public class UnitEmploymentPositionService extends UserBaseService {
    private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());
>>>>>>> b503068... changed position to UEP:kairos-user/src/main/java/com/kairos/service/position/UnitEmploymentPositionService.java
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
<<<<<<< HEAD:kairos-user/src/main/java/com/kairos/service/position/PositionService.java
        /*WTAWithCountryAndOrganizationTypeDTO wtaWithRuleTemplateDTO = workingTimeAgreementGraphRepository.getWTAByExpertiseAndCountry(positionDTO.getExpertiseId());


        if (!Optional.ofNullable(wtaWithRuleTemplateDTO.getName()).isPresent()) {
            logger.info("Expertise Doesn't contains WTA.Please select different Expertise" + positionDTO.getExpertiseId());
            throw new DataNotFoundByIdException("Expertise Doesn't contains WTA.Please select different Expertise");
        }



        WorkingTimeAgreement wta = copyWTASettingAndRuleTemplateWithCategory(wtaWithRuleTemplateDTO);*/

        Optional<WorkingTimeAgreement> wta = workingTimeAgreementGraphRepository.findById(positionDTO.getWtaId());
        if(!wta.isPresent()){
            throw new DataNotFoundByIdException("Invalid wta id ");
        }
        position.setWorkingTimeAgreement(wta.get());


        CostTimeAgreement cta = (positionDTO.getCtaId() == null)? null:
                costTimeAgreementGraphRepository.findOne(positionDTO.getCtaId());
        if(cta != null){
            position.setCta(cta);
        }

        Optional<Expertise> expertise = expertiseGraphRepository.findById(positionDTO.getExpertiseId());
        if(!expertise.isPresent()){
            throw new DataNotFoundByIdException("Invalid expertise id");
        }


       /* if (!Optional.ofNullable(wtaWithRuleTemplateDTO.getExpertise()).isPresent()) {
            throw new DataNotFoundByIdException("Invalid Expertize" + positionDTO.getExpertiseId());
        }*/
        position.setExpertise(expertise.get());
=======
        WTAWithRuleTemplateDTO wtaWithRuleTemplateDTO = workingTimeAgreementGraphRepository.getWTAByExpertiseAndCountry(unitEmploymentPositionDTO.getExpertiseId());

        if (!Optional.ofNullable(wtaWithRuleTemplateDTO.getExpertise()).isPresent()) {
            throw new DataNotFoundByIdException("Invalid Expertize" + unitEmploymentPositionDTO.getExpertiseId());
        }
        unitEmploymentPosition.setExpertise(wtaWithRuleTemplateDTO.getExpertise());
>>>>>>> b503068... changed position to UEP:kairos-user/src/main/java/com/kairos/service/position/UnitEmploymentPositionService.java

        EmploymentType employmentType = organizationGraphRepository.getEmploymentTypeByOrganizationAndEmploymentId(organization.getId(), unitEmploymentPositionDTO.getEmploymentTypeId(), false);
        if (employmentType == null) {
            throw new DataNotFoundByIdException("Employment Type does not exist in unit " + employmentType.getId() + " AND " + unitEmploymentPositionDTO.getEmploymentTypeId());
        }
        unitEmploymentPosition.setEmploymentType(employmentType);

<<<<<<< HEAD:kairos-user/src/main/java/com/kairos/service/position/PositionService.java
        PositionName positionName = positionNameService.getPositionNameByUnitIdAndId(organization.getId(), positionDTO.getPositionNameId());
        if (!Optional.ofNullable(positionName).isPresent()) {
            throw new DataNotFoundByIdException("position Name does not exist in unit " + positionDTO.getPositionNameId());
        }
        position.setPositionName(positionName);
=======
        PositionCode positionCode = positionCodeService.getPositionCodeByUnitIdAndId(organization.getId(), unitEmploymentPositionDTO.getPositionNameId());
        if (!Optional.ofNullable(positionCode).isPresent()) {
            throw new DataNotFoundByIdException("position Name does not exist in unit " + unitEmploymentPositionDTO.getPositionNameId());
        }
        unitEmploymentPosition.setPositionCode(positionCode);
>>>>>>> b503068... changed position to UEP:kairos-user/src/main/java/com/kairos/service/position/UnitEmploymentPositionService.java

        /*CostTimeAgreement cta = costTimeAgreementGraphRepository.findOne(positionDTO.getCtaId());
         if (cta == null) {
            throw new DataNotFoundByIdException("Invalid CTA");
        }
        position.setParent(cta);<String, Object>

        */
<<<<<<< HEAD:kairos-user/src/main/java/com/kairos/service/position/PositionService.java
=======
        if (Optional.ofNullable(wtaWithRuleTemplateDTO.getId()).isPresent()) {
            WorkingTimeAgreement wta = copyWTASettingAndRuleTemplateWithCategory(wtaWithRuleTemplateDTO);
            save(wta);
            unitEmploymentPosition.setWta(wta);
        }
>>>>>>> b503068... changed position to UEP:kairos-user/src/main/java/com/kairos/service/position/UnitEmploymentPositionService.java

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

    private WorkingTimeAgreement copyWTASettingAndRuleTemplateWithCategory(WTAWithCountryAndOrganizationTypeDTO wtaWithRuleTemplateDTO) {
        WorkingTimeAgreement wta = new WorkingTimeAgreement();
        wta.setName(wtaWithRuleTemplateDTO.getName());
        wta.setDescription(wtaWithRuleTemplateDTO.getDescription());
        wta.setEndDateMillis(wtaWithRuleTemplateDTO.getEndDateMillis());
        wtaWithRuleTemplateDTO.getRuleTemplates();

        List<RuleTemplate> wtaBaseRuleTemplates = new ArrayList<>();
        for (int i = 0; i < wtaWithRuleTemplateDTO.getRuleTemplates().size(); i++) {
            ObjectMapper objectMapper = new ObjectMapper();
            RuleTemplateCategoryDTO response = objectMapper.convertValue(wtaWithRuleTemplateDTO.getRuleTemplates().get(i), RuleTemplateCategoryDTO.class);
            wtaBaseRuleTemplates.add(copyRuleTemplateAndLinkWithThisWTA(response));
        }
        wta.setRuleTemplates(wtaBaseRuleTemplates);
        return wta;
    }

    private RuleTemplate copyRuleTemplateAndLinkWithThisWTA(RuleTemplateCategoryDTO wtaRuleTemplateQueryResponse) {
        WTABaseRuleTemplate wtaBaseRuleTemplates = null;
        switch (wtaRuleTemplateQueryResponse.getTemplateType()) {
            case TEMPLATE1:
                MaximumShiftLengthWTATemplate maximumShiftLengthWTATemplate = new MaximumShiftLengthWTATemplate();
                maximumShiftLengthWTATemplate.setName(wtaRuleTemplateQueryResponse.getName());
                maximumShiftLengthWTATemplate.setDescription(wtaRuleTemplateQueryResponse.getDescription());
                maximumShiftLengthWTATemplate.setTimeLimit(wtaRuleTemplateQueryResponse.getTimeLimit());
                maximumShiftLengthWTATemplate.setBalanceType(wtaRuleTemplateQueryResponse.getBalanceType());
                maximumShiftLengthWTATemplate.setCheckAgainstTimeRules(wtaRuleTemplateQueryResponse.isCheckAgainstTimeRules());
                maximumShiftLengthWTATemplate.setTemplateType(wtaRuleTemplateQueryResponse.getTemplateType());
                maximumShiftLengthWTATemplate.setDisabled(wtaRuleTemplateQueryResponse.isDisabled());
                wtaBaseRuleTemplates = save(maximumShiftLengthWTATemplate);

                break;

            case TEMPLATE2:
                MinimumShiftLengthWTATemplate minimumShiftLengthWTATemplate = new MinimumShiftLengthWTATemplate();
                minimumShiftLengthWTATemplate.setName(wtaRuleTemplateQueryResponse.getName());
                minimumShiftLengthWTATemplate.setDescription(wtaRuleTemplateQueryResponse.getDescription());
                minimumShiftLengthWTATemplate.setTimeLimit(wtaRuleTemplateQueryResponse.getTimeLimit());
                minimumShiftLengthWTATemplate.setBalanceType(wtaRuleTemplateQueryResponse.getBalanceType());
                minimumShiftLengthWTATemplate.setCheckAgainstTimeRules(wtaRuleTemplateQueryResponse.isCheckAgainstTimeRules());
                minimumShiftLengthWTATemplate.setTemplateType(wtaRuleTemplateQueryResponse.getTemplateType());
                minimumShiftLengthWTATemplate.setDisabled(wtaRuleTemplateQueryResponse.isDisabled());
                wtaBaseRuleTemplates = save(minimumShiftLengthWTATemplate);
                break;

            case TEMPLATE3:
                MaximumConsecutiveWorkingDaysWTATemplate maximumConsecutiveWorkingDaysWTATemplate = new MaximumConsecutiveWorkingDaysWTATemplate();
                maximumConsecutiveWorkingDaysWTATemplate.setName(wtaRuleTemplateQueryResponse.getName());
                maximumConsecutiveWorkingDaysWTATemplate.setDescription(wtaRuleTemplateQueryResponse.getDescription());
                maximumConsecutiveWorkingDaysWTATemplate.setDaysLimit(wtaRuleTemplateQueryResponse.getDaysLimit());
                maximumConsecutiveWorkingDaysWTATemplate.setBalanceType(wtaRuleTemplateQueryResponse.getBalanceType());
                maximumConsecutiveWorkingDaysWTATemplate.setCheckAgainstTimeRules(wtaRuleTemplateQueryResponse.isCheckAgainstTimeRules());
                maximumConsecutiveWorkingDaysWTATemplate.setTemplateType(wtaRuleTemplateQueryResponse.getTemplateType());
                maximumConsecutiveWorkingDaysWTATemplate.setDisabled(wtaRuleTemplateQueryResponse.isDisabled());
                wtaBaseRuleTemplates = save(maximumConsecutiveWorkingDaysWTATemplate);

                break;

            case TEMPLATE4:
                MinimumRestInConsecutiveDaysWTATemplate minimumRestInConsecutiveDaysWTATemplate = new MinimumRestInConsecutiveDaysWTATemplate();
                minimumRestInConsecutiveDaysWTATemplate.setName(wtaRuleTemplateQueryResponse.getName());
                minimumRestInConsecutiveDaysWTATemplate.setDescription(wtaRuleTemplateQueryResponse.getDescription());
                minimumRestInConsecutiveDaysWTATemplate.setMinimumRest(wtaRuleTemplateQueryResponse.getMinimumRest());
                minimumRestInConsecutiveDaysWTATemplate.setDaysWorked(wtaRuleTemplateQueryResponse.getDaysWorked());
                minimumRestInConsecutiveDaysWTATemplate.setTemplateType(wtaRuleTemplateQueryResponse.getTemplateType());
                minimumRestInConsecutiveDaysWTATemplate.setDisabled(wtaRuleTemplateQueryResponse.isDisabled());
                wtaBaseRuleTemplates = save(minimumRestInConsecutiveDaysWTATemplate);
                break;

            case TEMPLATE5:
                MaximumNightShiftLengthWTATemplate maximumNightShiftLengthWTATemplate = new MaximumNightShiftLengthWTATemplate();
                maximumNightShiftLengthWTATemplate.setName(wtaRuleTemplateQueryResponse.getName());
                maximumNightShiftLengthWTATemplate.setDisabled(wtaRuleTemplateQueryResponse.isDisabled());
                maximumNightShiftLengthWTATemplate.setDescription(wtaRuleTemplateQueryResponse.getDescription());
                maximumNightShiftLengthWTATemplate.setTimeLimit(wtaRuleTemplateQueryResponse.getTimeLimit());
                maximumNightShiftLengthWTATemplate.setBalanceType(wtaRuleTemplateQueryResponse.getBalanceType());
                maximumNightShiftLengthWTATemplate.setCheckAgainstTimeRules(wtaRuleTemplateQueryResponse.isCheckAgainstTimeRules());
                maximumNightShiftLengthWTATemplate.setTemplateType(wtaRuleTemplateQueryResponse.getTemplateType());
                wtaBaseRuleTemplates = save(maximumNightShiftLengthWTATemplate);
                break;

            case TEMPLATE6:
                MinimumConsecutiveNightsWTATemplate minimumConsecutiveNightsWTATemplate = new MinimumConsecutiveNightsWTATemplate();
                minimumConsecutiveNightsWTATemplate.setName(wtaRuleTemplateQueryResponse.getName());
                minimumConsecutiveNightsWTATemplate.setDescription(wtaRuleTemplateQueryResponse.getDescription());
                minimumConsecutiveNightsWTATemplate.setDaysLimit(wtaRuleTemplateQueryResponse.getDaysLimit());
                minimumConsecutiveNightsWTATemplate.setTemplateType(wtaRuleTemplateQueryResponse.getTemplateType());
                minimumConsecutiveNightsWTATemplate.setDisabled(wtaRuleTemplateQueryResponse.isDisabled());

                wtaBaseRuleTemplates = save(minimumConsecutiveNightsWTATemplate);
                break;

            case TEMPLATE7:
                MaximumConsecutiveWorkingNightsWTATemplate maximumConsecutiveWorkingNights = new MaximumConsecutiveWorkingNightsWTATemplate();
                maximumConsecutiveWorkingNights.setName(wtaRuleTemplateQueryResponse.getName());
                maximumConsecutiveWorkingNights.setDescription(wtaRuleTemplateQueryResponse.getDescription());
                maximumConsecutiveWorkingNights.setTemplateType(wtaRuleTemplateQueryResponse.getTemplateType());
                maximumConsecutiveWorkingNights.setNightsWorked(wtaRuleTemplateQueryResponse.getNightsWorked());
                maximumConsecutiveWorkingNights.setBalanceType(wtaRuleTemplateQueryResponse.getBalanceType());
                maximumConsecutiveWorkingNights.setCheckAgainstTimeRules(wtaRuleTemplateQueryResponse.isCheckAgainstTimeRules());
                maximumConsecutiveWorkingNights.setDisabled(wtaRuleTemplateQueryResponse.isDisabled());
                wtaBaseRuleTemplates = save(maximumConsecutiveWorkingNights);
                break;
            case TEMPLATE8:
                MinimumRestConsecutiveNightsWTATemplate minimumRestConsecutiveNightsWTATemplate = new MinimumRestConsecutiveNightsWTATemplate();
                minimumRestConsecutiveNightsWTATemplate.setName(wtaRuleTemplateQueryResponse.getName());
                minimumRestConsecutiveNightsWTATemplate.setTemplateType(wtaRuleTemplateQueryResponse.getTemplateType());
                minimumRestConsecutiveNightsWTATemplate.setDescription(wtaRuleTemplateQueryResponse.getDescription());
                minimumRestConsecutiveNightsWTATemplate.setNightsWorked(wtaRuleTemplateQueryResponse.getNightsWorked());
                minimumRestConsecutiveNightsWTATemplate.setBalanceType(wtaRuleTemplateQueryResponse.getBalanceType());
                minimumRestConsecutiveNightsWTATemplate.setMinimumRest(wtaRuleTemplateQueryResponse.getMinimumRest());
                minimumRestConsecutiveNightsWTATemplate.setDisabled(wtaRuleTemplateQueryResponse.isDisabled());
                wtaBaseRuleTemplates = save(minimumRestConsecutiveNightsWTATemplate);
                break;
            case TEMPLATE9:
                MaximumNumberOfNightsWTATemplate maximumNumberOfNightsWTATemplate = new MaximumNumberOfNightsWTATemplate();
                maximumNumberOfNightsWTATemplate.setName(wtaRuleTemplateQueryResponse.getName());
                maximumNumberOfNightsWTATemplate.setTemplateType(wtaRuleTemplateQueryResponse.getTemplateType());
                maximumNumberOfNightsWTATemplate.setDescription(wtaRuleTemplateQueryResponse.getDescription());
                maximumNumberOfNightsWTATemplate.setNightsWorked(wtaRuleTemplateQueryResponse.getNightsWorked());
                maximumNumberOfNightsWTATemplate.setBalanceType(wtaRuleTemplateQueryResponse.getBalanceType());
                maximumNumberOfNightsWTATemplate.setIntervalLength(wtaRuleTemplateQueryResponse.getIntervalLength());
                maximumNumberOfNightsWTATemplate.setIntervalUnit(wtaRuleTemplateQueryResponse.getIntervalUnit());
                maximumNumberOfNightsWTATemplate.setValidationStartDateMillis(wtaRuleTemplateQueryResponse.getValidationStartDateMillis());
                maximumNumberOfNightsWTATemplate.setDisabled(wtaRuleTemplateQueryResponse.isDisabled());

                wtaBaseRuleTemplates = save(maximumNumberOfNightsWTATemplate);
                break;
            case TEMPLATE10:
                MaximumDaysOffInPeriodWTATemplate maximumDaysOffInPeriodWTATemplate = new MaximumDaysOffInPeriodWTATemplate();
                maximumDaysOffInPeriodWTATemplate.setName(wtaRuleTemplateQueryResponse.getName());
                maximumDaysOffInPeriodWTATemplate.setTemplateType(wtaRuleTemplateQueryResponse.getTemplateType());
                maximumDaysOffInPeriodWTATemplate.setDescription(wtaRuleTemplateQueryResponse.getDescription());
                maximumDaysOffInPeriodWTATemplate.setIntervalLength(wtaRuleTemplateQueryResponse.getIntervalLength());
                maximumDaysOffInPeriodWTATemplate.setIntervalUnit(wtaRuleTemplateQueryResponse.getIntervalUnit());
                maximumDaysOffInPeriodWTATemplate.setValidationStartDateMillis(wtaRuleTemplateQueryResponse.getValidationStartDateMillis());
                maximumDaysOffInPeriodWTATemplate.setBalanceType(wtaRuleTemplateQueryResponse.getBalanceType());
                maximumDaysOffInPeriodWTATemplate.setDaysLimit(wtaRuleTemplateQueryResponse.getDaysLimit());
                maximumDaysOffInPeriodWTATemplate.setDisabled(wtaRuleTemplateQueryResponse.isDisabled());
                wtaBaseRuleTemplates = save(maximumDaysOffInPeriodWTATemplate);
                break;
            case TEMPLATE11:
                MaximumAverageScheduledTimeWTATemplate maximumAverageScheduledTimeWTATemplate = new MaximumAverageScheduledTimeWTATemplate();
                maximumAverageScheduledTimeWTATemplate.setDescription(wtaRuleTemplateQueryResponse.getDescription());
                maximumAverageScheduledTimeWTATemplate.setTemplateType(wtaRuleTemplateQueryResponse.getTemplateType());
                maximumAverageScheduledTimeWTATemplate.setUseShiftTimes(wtaRuleTemplateQueryResponse.isUseShiftTimes());
                maximumAverageScheduledTimeWTATemplate.setIntervalLength(wtaRuleTemplateQueryResponse.getIntervalLength());
                maximumAverageScheduledTimeWTATemplate.setIntervalUnit(wtaRuleTemplateQueryResponse.getIntervalUnit());
                maximumAverageScheduledTimeWTATemplate.setMaximumAvgTime(wtaRuleTemplateQueryResponse.getMaximumAvgTime());
                maximumAverageScheduledTimeWTATemplate.setBalanceType(wtaRuleTemplateQueryResponse.getBalanceType());
                maximumAverageScheduledTimeWTATemplate.setValidationStartDateMillis(wtaRuleTemplateQueryResponse.getValidationStartDateMillis());
                maximumAverageScheduledTimeWTATemplate.setBalanceAdjustment(wtaRuleTemplateQueryResponse.isBalanceAdjustment());
                maximumAverageScheduledTimeWTATemplate.setName(wtaRuleTemplateQueryResponse.getName());
                maximumAverageScheduledTimeWTATemplate.setDisabled(wtaRuleTemplateQueryResponse.isDisabled());
                wtaBaseRuleTemplates = save(maximumAverageScheduledTimeWTATemplate);
                break;
            case TEMPLATE12:
                MaximumVetoPerPeriodWTATemplate maximumVetoPerPeriodWTATemplate = new MaximumVetoPerPeriodWTATemplate();
                maximumVetoPerPeriodWTATemplate.setName(wtaRuleTemplateQueryResponse.getName());
                maximumVetoPerPeriodWTATemplate.setTemplateType(wtaRuleTemplateQueryResponse.getTemplateType());
                maximumVetoPerPeriodWTATemplate.setDescription(wtaRuleTemplateQueryResponse.getDescription());
                maximumVetoPerPeriodWTATemplate.setMaximumVetoPercentage(wtaRuleTemplateQueryResponse.getMaximumVetoPercentage());
                maximumVetoPerPeriodWTATemplate.setDisabled(wtaRuleTemplateQueryResponse.isDisabled());

                wtaBaseRuleTemplates = save(maximumVetoPerPeriodWTATemplate);
                break;
            case TEMPLATE13:
                NumberOfWeekendShiftInPeriodWTATemplate numberOfWeekendShiftInPeriodWTATemplate = new NumberOfWeekendShiftInPeriodWTATemplate();
                numberOfWeekendShiftInPeriodWTATemplate.setName(wtaRuleTemplateQueryResponse.getName());
                numberOfWeekendShiftInPeriodWTATemplate.setTemplateType(wtaRuleTemplateQueryResponse.getTemplateType());
                numberOfWeekendShiftInPeriodWTATemplate.setDescription(wtaRuleTemplateQueryResponse.getDescription());
                numberOfWeekendShiftInPeriodWTATemplate.setNumberShiftsPerPeriod(wtaRuleTemplateQueryResponse.getNumberShiftsPerPeriod());
                numberOfWeekendShiftInPeriodWTATemplate.setNumberOfWeeks(wtaRuleTemplateQueryResponse.getNumberOfWeeks());
                numberOfWeekendShiftInPeriodWTATemplate.setFromDayOfWeek(wtaRuleTemplateQueryResponse.getFromDayOfWeek());
                numberOfWeekendShiftInPeriodWTATemplate.setFromTime(wtaRuleTemplateQueryResponse.getFromTime());
                numberOfWeekendShiftInPeriodWTATemplate.setToTime(wtaRuleTemplateQueryResponse.getToTime());
                numberOfWeekendShiftInPeriodWTATemplate.setToDayOfWeek(wtaRuleTemplateQueryResponse.getToDayOfWeek());
                numberOfWeekendShiftInPeriodWTATemplate.setProportional(wtaRuleTemplateQueryResponse.isProportional());
                numberOfWeekendShiftInPeriodWTATemplate.setDisabled(wtaRuleTemplateQueryResponse.isDisabled());

                wtaBaseRuleTemplates = save(numberOfWeekendShiftInPeriodWTATemplate);
                break;
            case TEMPLATE14:
                CareDayCheckWTATemplate careDayCheckWTATemplate = new CareDayCheckWTATemplate();
                careDayCheckWTATemplate.setName(wtaRuleTemplateQueryResponse.getName());
                careDayCheckWTATemplate.setTemplateType(wtaRuleTemplateQueryResponse.getTemplateType());
                careDayCheckWTATemplate.setDescription(wtaRuleTemplateQueryResponse.getDescription());
                careDayCheckWTATemplate.setIntervalLength(wtaRuleTemplateQueryResponse.getIntervalLength());
                careDayCheckWTATemplate.setIntervalUnit(wtaRuleTemplateQueryResponse.getIntervalUnit());
                careDayCheckWTATemplate.setDaysLimit(wtaRuleTemplateQueryResponse.getDaysLimit());
                careDayCheckWTATemplate.setValidationStartDateMillis(wtaRuleTemplateQueryResponse.getValidationStartDateMillis());
                careDayCheckWTATemplate.setDisabled(wtaRuleTemplateQueryResponse.isDisabled());

                wtaBaseRuleTemplates = save(careDayCheckWTATemplate);
                break;
            case TEMPLATE15:
                MinimumDailyRestingTimeWTATemplate minimumDailyRestingTimeWTATemplate = new MinimumDailyRestingTimeWTATemplate();
                minimumDailyRestingTimeWTATemplate.setName(wtaRuleTemplateQueryResponse.getName());
                minimumDailyRestingTimeWTATemplate.setTemplateType(wtaRuleTemplateQueryResponse.getTemplateType());
                minimumDailyRestingTimeWTATemplate.setDescription(wtaRuleTemplateQueryResponse.getDescription());
                minimumDailyRestingTimeWTATemplate.setContinuousDayRestHours(wtaRuleTemplateQueryResponse.getContinuousDayRestHours());
                minimumDailyRestingTimeWTATemplate.setDisabled(wtaRuleTemplateQueryResponse.isDisabled());
                wtaBaseRuleTemplates = save(minimumDailyRestingTimeWTATemplate);
                break;
            case TEMPLATE16:
                MinimumDurationBetweenShiftWTATemplate minimumDurationBetweenShiftWTATemplate = new MinimumDurationBetweenShiftWTATemplate();
                minimumDurationBetweenShiftWTATemplate.setName(wtaRuleTemplateQueryResponse.getName());
                minimumDurationBetweenShiftWTATemplate.setTemplateType(wtaRuleTemplateQueryResponse.getTemplateType());
                minimumDurationBetweenShiftWTATemplate.setDescription(wtaRuleTemplateQueryResponse.getDescription());
                minimumDurationBetweenShiftWTATemplate.setBalanceType(wtaRuleTemplateQueryResponse.getBalanceType());
                minimumDurationBetweenShiftWTATemplate.setMinimumDurationBetweenShifts(wtaRuleTemplateQueryResponse.getMinimumDurationBetweenShifts());
                minimumDurationBetweenShiftWTATemplate.setDisabled(wtaRuleTemplateQueryResponse.isDisabled());

                wtaBaseRuleTemplates = save(minimumDurationBetweenShiftWTATemplate);
                break;
            case TEMPLATE17:
                MinimumWeeklyRestPeriodWTATemplate minimumWeeklyRestPeriodWTATemplate = new MinimumWeeklyRestPeriodWTATemplate();
                minimumWeeklyRestPeriodWTATemplate.setName(wtaRuleTemplateQueryResponse.getName());
                minimumWeeklyRestPeriodWTATemplate.setTemplateType(wtaRuleTemplateQueryResponse.getTemplateType());
                minimumWeeklyRestPeriodWTATemplate.setDescription(wtaRuleTemplateQueryResponse.getDescription());
                minimumWeeklyRestPeriodWTATemplate.setContinuousWeekRest(wtaRuleTemplateQueryResponse.getContinuousWeekRest());
                wtaBaseRuleTemplates = save(minimumWeeklyRestPeriodWTATemplate);
                break;
            case TEMPLATE18:
                ShortestAndAverageDailyRestWTATemplate shortestAndAverageDailyRestWTATemplate = new ShortestAndAverageDailyRestWTATemplate();
                shortestAndAverageDailyRestWTATemplate.setName(wtaRuleTemplateQueryResponse.getName());
                shortestAndAverageDailyRestWTATemplate.setTemplateType(wtaRuleTemplateQueryResponse.getTemplateType());
                shortestAndAverageDailyRestWTATemplate.setDescription(wtaRuleTemplateQueryResponse.getDescription());
                shortestAndAverageDailyRestWTATemplate.setBalanceType(wtaRuleTemplateQueryResponse.getBalanceType());
                shortestAndAverageDailyRestWTATemplate.setIntervalLength(wtaRuleTemplateQueryResponse.getIntervalLength());
                shortestAndAverageDailyRestWTATemplate.setIntervalUnit(wtaRuleTemplateQueryResponse.getIntervalUnit());
                shortestAndAverageDailyRestWTATemplate.setValidationStartDateMillis(wtaRuleTemplateQueryResponse.getValidationStartDateMillis());
                shortestAndAverageDailyRestWTATemplate.setContinuousDayRestHours(wtaRuleTemplateQueryResponse.getContinuousDayRestHours());
                shortestAndAverageDailyRestWTATemplate.setAverageRest(wtaRuleTemplateQueryResponse.getAverageRest());
                shortestAndAverageDailyRestWTATemplate.setShiftAffiliation(wtaRuleTemplateQueryResponse.getShiftAffiliation());
                wtaBaseRuleTemplates = save(shortestAndAverageDailyRestWTATemplate);
                break;
            case TEMPLATE19:
                MaximumShiftsInIntervalWTATemplate maximumShiftsInIntervalWTATemplate = new MaximumShiftsInIntervalWTATemplate();
                maximumShiftsInIntervalWTATemplate.setName(wtaRuleTemplateQueryResponse.getName());
                maximumShiftsInIntervalWTATemplate.setTemplateType(wtaRuleTemplateQueryResponse.getTemplateType());
                maximumShiftsInIntervalWTATemplate.setDescription(wtaRuleTemplateQueryResponse.getDescription());
                maximumShiftsInIntervalWTATemplate.setBalanceType(wtaRuleTemplateQueryResponse.getBalanceType());
                maximumShiftsInIntervalWTATemplate.setIntervalLength(wtaRuleTemplateQueryResponse.getIntervalLength());
                maximumShiftsInIntervalWTATemplate.setIntervalUnit(wtaRuleTemplateQueryResponse.getIntervalUnit());
                maximumShiftsInIntervalWTATemplate.setValidationStartDateMillis(wtaRuleTemplateQueryResponse.getValidationStartDateMillis());
                maximumShiftsInIntervalWTATemplate.setShiftsLimit(wtaRuleTemplateQueryResponse.getShiftsLimit());
                maximumShiftsInIntervalWTATemplate.setOnlyCompositeShifts(wtaRuleTemplateQueryResponse.isOnlyCompositeShifts());
                wtaBaseRuleTemplates = maximumShiftsInIntervalWTATemplate;
                break;
            case TEMPLATE20:
                MaximumSeniorDaysInYearWTATemplate maximumSeniorDaysInYearWTATemplate = new MaximumSeniorDaysInYearWTATemplate();
                maximumSeniorDaysInYearWTATemplate.setName(wtaRuleTemplateQueryResponse.getName());
                maximumSeniorDaysInYearWTATemplate.setTemplateType(wtaRuleTemplateQueryResponse.getTemplateType());
                maximumSeniorDaysInYearWTATemplate.setDescription(wtaRuleTemplateQueryResponse.getDescription());
                maximumSeniorDaysInYearWTATemplate.setIntervalLength(wtaRuleTemplateQueryResponse.getIntervalLength());
                maximumSeniorDaysInYearWTATemplate.setIntervalUnit(wtaRuleTemplateQueryResponse.getIntervalUnit());
                maximumSeniorDaysInYearWTATemplate.setValidationStartDateMillis(wtaRuleTemplateQueryResponse.getValidationStartDateMillis());
                maximumSeniorDaysInYearWTATemplate.setDaysLimit(wtaRuleTemplateQueryResponse.getDaysLimit());
                maximumSeniorDaysInYearWTATemplate.setActivityCode(wtaRuleTemplateQueryResponse.getActivityCode());

                wtaBaseRuleTemplates = maximumSeniorDaysInYearWTATemplate;
                break;
            default:
                throw new DataNotFoundByIdException("Invalid TEMPLATE");


        }
        RuleTemplateCategory ruleTemplateCategory = new RuleTemplateCategory(wtaRuleTemplateQueryResponse.getRuleTemplateCategory().getName(),
                wtaRuleTemplateQueryResponse.getRuleTemplateCategory().getDescription(), false);
        List<RuleTemplate> wtaBaseRuleTemplateList = new ArrayList<>();
        wtaBaseRuleTemplateList.add(wtaBaseRuleTemplates);
        ruleTemplateCategory.setRuleTemplates(wtaBaseRuleTemplateList);
        save(ruleTemplateCategory);
        return wtaBaseRuleTemplateList.get(0);
    }

<<<<<<< HEAD:kairos-user/src/main/java/com/kairos/service/position/PositionService.java
    private void preparePosition(Position oldPosition, PositionDTO positionDTO) {
=======
    private void preparePosition(UnitEmploymentPosition oldUnitEmploymentPosition, UnitEmploymentPositionDTO unitEmploymentPositionDTO) {
        if (!oldUnitEmploymentPosition.getExpertise().getId().equals(unitEmploymentPositionDTO.getExpertiseId())) {
            WTAWithRuleTemplateDTO wtaWithRuleTemplateDTO = workingTimeAgreementGraphRepository.getWTAByExpertiseAndCountry(unitEmploymentPositionDTO.getExpertiseId());
>>>>>>> b503068... changed position to UEP:kairos-user/src/main/java/com/kairos/service/position/UnitEmploymentPositionService.java

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
=======
            oldUnitEmploymentPosition.setExpertise(wtaWithRuleTemplateDTO.getExpertise());
        }


        if (!oldUnitEmploymentPosition.getPositionCode().getId().equals(unitEmploymentPositionDTO.getPositionNameId())) {
            PositionCode positionCode = positionCodeGraphRepository.findOne(unitEmploymentPositionDTO.getPositionNameId());
            if (!Optional.ofNullable(positionCode).isPresent()) {
                throw new DataNotFoundByIdException("PositionName Cannot be null" + unitEmploymentPositionDTO.getPositionNameId());
            }
            oldUnitEmploymentPosition.setPositionCode(positionCode);
>>>>>>> b503068... changed position to UEP:kairos-user/src/main/java/com/kairos/service/position/UnitEmploymentPositionService.java
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
<<<<<<< HEAD:kairos-user/src/main/java/com/kairos/service/position/PositionService.java
    public List<PositionQueryResult> getAllPositionByStaff(long id, long unitEmploymentId, long staffId, String
            type) {
=======
    public List<UnitEmploymentPositionQueryResult> getAlllUnitEmploymentPositionsOfStaff(long id, long unitEmploymentId, long staffId, String type) {
>>>>>>> b503068... changed position to UEP:kairos-user/src/main/java/com/kairos/service/position/UnitEmploymentPositionService.java

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
