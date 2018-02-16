package com.kairos.service.unitEmploymentPosition;

import com.kairos.custom_exception.ActionNotPermittedException;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.agreement.cta.CostTimeAgreement;
import com.kairos.persistence.model.user.agreement.wta.WTADTO;
import com.kairos.persistence.model.user.agreement.wta.WTAResponseDTO;
import com.kairos.persistence.model.user.agreement.wta.WorkingTimeAgreement;
import com.kairos.persistence.model.user.client.ClientMinimumDTO;
import com.kairos.persistence.model.user.country.EmploymentType;

import com.kairos.persistence.model.user.expertise.Expertise;

import com.kairos.persistence.model.user.position.PositionCode;
import com.kairos.persistence.model.user.unitEmploymentPosition.PositionCtaWtaQueryResult;

import com.kairos.persistence.model.user.unitEmploymentPosition.UnitEmploymentPosition;

import com.kairos.persistence.model.user.unitEmploymentPosition.UnitEmploymentPositionQueryResult;

import com.kairos.persistence.model.user.staff.Staff;
import com.kairos.persistence.model.user.staff.TimeCareEmploymentDTO;
import com.kairos.persistence.model.user.staff.UnitEmployment;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.agreement.cta.CollectiveTimeAgreementGraphRepository;
import com.kairos.persistence.repository.user.agreement.wta.WorkingTimeAgreementGraphRepository;
import com.kairos.persistence.repository.user.client.ClientGraphRepository;
import com.kairos.persistence.repository.user.country.EmploymentTypeGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;

import com.kairos.persistence.repository.user.positionCode.PositionCodeGraphRepository;
import com.kairos.persistence.repository.user.unitEmploymentPosition.UnitEmploymentPositionGraphRepository;

import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.persistence.repository.user.staff.UnitEmploymentGraphRepository;
import com.kairos.response.dto.web.UnitEmploymentPositionDTO;
import com.kairos.response.dto.web.PositionWrapper;
import com.kairos.service.UserBaseService;
import com.kairos.service.agreement.wta.WTAService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.service.positionCode.PositionCodeService;
import com.kairos.service.staff.StaffService;
import com.kairos.util.DateConverter;
import org.apache.commons.collections.list.SetUniqueList;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by pawanmandhan on 26/7/17.
 */


@Transactional
@Service

public class UnitEmploymentPositionService extends UserBaseService {
    private final Logger logger = LoggerFactory.getLogger(UnitEmploymentPositionService.class);

    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject
    private UnitEmploymentPositionGraphRepository unitEmploymentPositionGraphRepository;
    @Inject
    private PositionCodeGraphRepository positionCodeGraphRepository;
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
    private PositionCodeService positionCodeService;
    @Inject
    private WTAService wtaService;
    @Inject
    private ClientGraphRepository clientGraphRepository;


    public UnitEmploymentPositionQueryResult createUnitEmploymentPosition(Long id, String type, UnitEmploymentPositionDTO unitEmploymentPositionDTO, Boolean createFromTimeCare) {
        Organization organization = organizationService.getOrganizationDetail(id, type);
        Organization parentOrganization;
        UnitEmployment unitEmployment;
        PositionCode positionCode = null;
        if (!organization.isParentOrganization()) {
            parentOrganization = organizationService.getParentOfOrganization(organization.getId());
            unitEmployment = unitEmploymentGraphRepository.checkUnitEmploymentOfStaff(parentOrganization.getId(), organization.getId(), unitEmploymentPositionDTO.getStaffId());
            positionCode = positionCodeGraphRepository.getPositionCodeByUnitIdAndId(parentOrganization.getId(), unitEmploymentPositionDTO.getPositionCodeId());
        } else {
            unitEmployment = unitEmploymentGraphRepository.checkUnitEmploymentOfStaff(organization.getId(), unitEmploymentPositionDTO.getStaffId());
            positionCode = positionCodeGraphRepository.getPositionCodeByUnitIdAndId(organization.getId(), unitEmploymentPositionDTO.getPositionCodeId());
        }
        if (!Optional.ofNullable(unitEmployment).isPresent()) {
            logger.info("Unable to get Unit employment of this staff ,{} in organization,{}", unitEmploymentPositionDTO.getStaffId(), organization.getId());
            throw new DataNotFoundByIdException("unable to create position of staff");
        }

        if (!Optional.ofNullable(positionCode).isPresent()) {
            throw new DataNotFoundByIdException("position Name does not exist in unit " + unitEmploymentPositionDTO.getPositionCodeId());
        }
        List<UnitEmploymentPosition> oldUnitEmploymentPositions = unitEmploymentPositionGraphRepository.getAllUEPByExpertise(unitEmploymentPositionDTO.getExpertiseId(), unitEmployment.getId());
        validateUnitEmploymentPositionWithExpertise(oldUnitEmploymentPositions, unitEmploymentPositionDTO);
        UnitEmploymentPosition unitEmploymentPosition = preparePosition(unitEmploymentPositionDTO, organization, id, createFromTimeCare);

        unitEmploymentPosition.setPositionCode(positionCode);

        List<UnitEmploymentPosition> unitEmploymentPositions = unitEmployment.getUnitEmploymentPositions();

        unitEmploymentPositions.add(unitEmploymentPosition);
        unitEmployment.setUnitEmploymentPositions(unitEmploymentPositions);
        save(unitEmployment);
        UnitEmploymentPositionQueryResult unitEmploymentPositionQueryResult = unitEmploymentPosition.getBasicDetails();
        return unitEmploymentPositionQueryResult;
    }

    public boolean validateUnitEmploymentPositionWithExpertise(List<UnitEmploymentPosition> unitEmploymentPositions, UnitEmploymentPositionDTO unitEmploymentPositionDTO) {

        Long newUEPStartDateMillis = unitEmploymentPositionDTO.getStartDateMillis();
        Long newUEPEndDateMillis = (unitEmploymentPositionDTO.getEndDateMillis() != null) ? unitEmploymentPositionDTO.getEndDateMillis() : null;
        unitEmploymentPositions.forEach(unitEmploymentPosition -> {
            // if null date is set
            if (unitEmploymentPosition.getEndDateMillis() != null) {
                logger.info("new UEP start {} " + new DateTime(newUEPStartDateMillis).toLocalDate() + " new UEP End date " + (new DateTime(newUEPEndDateMillis)).toLocalDate(),
                        " current Employment  " + new DateTime(unitEmploymentPosition.getStartDateMillis()).toLocalDate() + " unitEmployment End date   " + (new DateTime(unitEmploymentPosition.getEndDateMillis())).toLocalDate());

                if (new DateTime(newUEPStartDateMillis).isBefore(new DateTime(unitEmploymentPosition.getEndDateMillis()))) {
                    throw new ActionNotPermittedException("Already a unit employment position is active with same expertise on this period(End date overlap with start Date)" + new DateTime(newUEPEndDateMillis).toDate() + " --> " + new DateTime(unitEmploymentPosition.getStartDateMillis()).toDate());
                }
                if (newUEPEndDateMillis != null) {
                    Interval previousInterval = new Interval(unitEmploymentPosition.getStartDateMillis(), unitEmploymentPosition.getEndDateMillis());
                    Interval interval = new Interval(newUEPStartDateMillis, newUEPEndDateMillis);
                    logger.info(" Interval of CURRENT UEP " + previousInterval + " Interval of going to create  " + interval);
                    if (previousInterval.overlaps(interval))
                        throw new ActionNotPermittedException("Already a unit employment position is active with same expertise on this period(End date overlap with start Date)");

                } else {
                    logger.info("new UEP EndDate {}", new DateTime(newUEPEndDateMillis) + " unitEmployment End date " + (new DateTime(unitEmploymentPosition.getEndDateMillis())));
                    if (new DateTime(newUEPEndDateMillis).isBefore(new DateTime(unitEmploymentPosition.getEndDateMillis()))) {
                        throw new ActionNotPermittedException("Already a unit employment position is active with same expertise on this period(End date overlap with start Date)." + new DateTime(newUEPEndDateMillis).toDate() + " --> " + new DateTime(unitEmploymentPosition.getEndDateMillis()).toDate());
                    }
                }
            } else {
                // unitEmploymentEnd date is null
                if (newUEPEndDateMillis != null) {
                    logger.info("new UEP EndDate " + new DateTime(newUEPEndDateMillis) + " running  UEP Start date " + (new DateTime(unitEmploymentPosition.getStartDateMillis())));
                    if (new DateTime(newUEPEndDateMillis).isAfter(new DateTime(unitEmploymentPosition.getStartDateMillis()))) {
                        throw new ActionNotPermittedException("Already a unit employment position is active with same expertise on this period(End date overlap with start Date)" + new DateTime(newUEPEndDateMillis).toDate() + " --> " + new DateTime(unitEmploymentPosition.getStartDateMillis()).toDate());
                    }
                } else {
                    logger.info("new UEP start date " + new DateTime(newUEPStartDateMillis) + " new UEP End date ", new DateTime(newUEPEndDateMillis));
                    throw new ActionNotPermittedException("Already a unit employment position is active with same expertise on this period.");
                }
            }
        });
        return true;
    }


    public PositionWrapper updateUnitEmploymentPosition(long unitEmploymentPositionId, UnitEmploymentPositionDTO unitEmploymentPositionDTO) {

        List<ClientMinimumDTO> clientMinimumDTO = clientGraphRepository.getCitizenListForThisContactPerson(unitEmploymentPositionDTO.getStaffId());
        if (clientMinimumDTO.size() > 0) {
            return new PositionWrapper(clientMinimumDTO);
        }

        UnitEmploymentPosition oldUnitEmploymentPosition = unitEmploymentPositionGraphRepository.findOne(unitEmploymentPositionId);
        if (!Optional.ofNullable(oldUnitEmploymentPosition).isPresent()) {
            throw new DataNotFoundByIdException("Invalid positionId id " + unitEmploymentPositionId + " while updating the position");
        }
        // findEmployment by UnitEmployment Id
        Long unitEmploymentId = unitEmploymentPositionGraphRepository.findEmploymentByUnitEmploymentPosition(unitEmploymentPositionId);

        List<UnitEmploymentPosition> oldUnitEmploymentPositions;
        oldUnitEmploymentPositions = unitEmploymentPositionGraphRepository.getAllUEPByExpertiseExcludingCurrent(unitEmploymentPositionDTO.getExpertiseId(), unitEmploymentId, unitEmploymentPositionId);
        validateUnitEmploymentPositionWithExpertise(oldUnitEmploymentPositions, unitEmploymentPositionDTO);

        preparePosition(oldUnitEmploymentPosition, unitEmploymentPositionDTO);
        save(oldUnitEmploymentPosition);
        return new PositionWrapper(oldUnitEmploymentPosition);

    }

    public boolean removePosition(long positionId) {
        UnitEmploymentPosition unitEmploymentPosition = unitEmploymentPositionGraphRepository.findOne(positionId);
        if (!Optional.ofNullable(unitEmploymentPosition).isPresent()) {
            return false;
        }
        unitEmploymentPosition.setDeleted(true);
        save(unitEmploymentPosition);
        return true;
    }


    public UnitEmploymentPosition getUnitEmploymentPosition(long positionId) {
        return unitEmploymentPositionGraphRepository.findOne(positionId);
    }

    /*
    * Created by vipul
    * 4-august-17
    * used to get all positions based on unitEmployment
    *

    public List<UnitEmploymentPositionQueryResult> getAllUnitEmploymentPositions(long unitEmploymentId) {
        Organization organization = organizationService.getOrganizationDetail(id, type);
        Organization parentOrganization;
        UnitEmployment unitEmployment;
        PositionCode positionCode = null;
        if (!organization.isParentOrganization()) {
            parentOrganization = organizationService.getParentOfOrganization(organization.getId());
            unitEmployment = unitEmploymentGraphRepository.checkUnitEmploymentOfStaff(parentOrganization.getId(), organization.getId(), unitEmploymentPositionDTO.getStaffId());
            positionCode = positionCodeGraphRepository.getPositionCodeByUnitIdAndId(parentOrganization.getId(), unitEmploymentPositionDTO.getPositionCodeId());
        } else {
            unitEmployment = unitEmploymentGraphRepository.checkUnitEmploymentOfStaff(organization.getId(), unitEmploymentPositionDTO.getStaffId());
            positionCode = positionCodeGraphRepository.getPositionCodeByUnitIdAndId(organization.getId(), unitEmploymentPositionDTO.getPositionCodeId());
        }
        if (!Optional.ofNullable(unitEmployment).isPresent()) {
            logger.info("Unable to get Unit employment of this staff ,{} in organization,{}", unitEmploymentPositionDTO.getStaffId(), organization.getId());
            throw new DataNotFoundByIdException("unable to create positionCode of staff");
        }


        if (unitEmployment == null) {
            throw new DataNotFoundByIdException("Invalid UnitEmployment id" + unitEmploymentId);
        }
        return unitEmploymentPositionGraphRepository.findAllUnitEmploymentPositions(unitEmploymentId);

    }
 */
    private UnitEmploymentPosition preparePosition(UnitEmploymentPositionDTO unitEmploymentPositionDTO, Organization organization, Long unitId,Boolean createFromTimeCare) {
        UnitEmploymentPosition unitEmploymentPosition = new UnitEmploymentPosition();

        Optional<WorkingTimeAgreement> wta = workingTimeAgreementGraphRepository.findById(unitEmploymentPositionDTO.getWtaId());
        if (!wta.isPresent()) {
            throw new DataNotFoundByIdException("Invalid wta id ");
        }
        unitEmploymentPosition.setWorkingTimeAgreement(wta.get());


        CostTimeAgreement cta = (unitEmploymentPositionDTO.getCtaId() == null) ? null :
                costTimeAgreementGraphRepository.findOne(unitEmploymentPositionDTO.getCtaId());
        if (cta != null) {
            unitEmploymentPosition.setCta(cta);
        }

        Optional<Expertise> expertise = expertiseGraphRepository.findById(unitEmploymentPositionDTO.getExpertiseId());
        if (!expertise.isPresent()) {
            throw new DataNotFoundByIdException("Invalid expertise id");
        }
        unitEmploymentPosition.setExpertise(expertise.get());

        EmploymentType employmentType = organizationGraphRepository.getEmploymentTypeByOrganizationAndEmploymentId(organization.getId(), unitEmploymentPositionDTO.getEmploymentTypeId(), false);
        if (!Optional.ofNullable(employmentType).isPresent()) {
            throw new DataNotFoundByIdException("Employment Type does not exist in unit " + employmentType.getId() + " AND " + unitEmploymentPositionDTO.getEmploymentTypeId());
        }
        unitEmploymentPosition.setEmploymentType(employmentType);


        Staff staff = staffGraphRepository.findOne(unitEmploymentPositionDTO.getStaffId());
        if (!Optional.ofNullable(staff).isPresent()) {
            throw new DataNotFoundByIdException("Invalid Staff Id" + unitEmploymentPositionDTO.getStaffId());
        }
        unitEmploymentPosition.setStaff(staff);

        // UEP can be created for past dates from time care
        if (!createFromTimeCare && unitEmploymentPositionDTO.getStartDateMillis() < System.currentTimeMillis()) {
            throw new ActionNotPermittedException("Start date can't be less than current Date ");
        }
        unitEmploymentPosition.setStartDateMillis(unitEmploymentPositionDTO.getStartDateMillis());


        if (Optional.ofNullable(unitEmploymentPositionDTO.getEndDateMillis()).isPresent()) {
            if (unitEmploymentPositionDTO.getStartDateMillis() > unitEmploymentPositionDTO.getEndDateMillis()) {
                throw new ActionNotPermittedException("Start date can't be less than End Date ");

            }
            unitEmploymentPosition.setEndDateMillis(unitEmploymentPositionDTO.getEndDateMillis());
        }
        unitEmploymentPosition.setTotalWeeklyMinutes(unitEmploymentPositionDTO.getTotalWeeklyMinutes() + (unitEmploymentPositionDTO.getTotalWeeklyHours() * 60));
        unitEmploymentPosition.setAvgDailyWorkingHours(unitEmploymentPositionDTO.getAvgDailyWorkingHours());
        unitEmploymentPosition.setHourlyWages(unitEmploymentPositionDTO.getHourlyWages());
        unitEmploymentPosition.setSalary(unitEmploymentPositionDTO.getSalary());
        unitEmploymentPosition.setWorkingDaysInWeek(unitEmploymentPositionDTO.getWorkingDaysInWeek());

        return unitEmploymentPosition;
    }


    private void preparePosition(UnitEmploymentPosition oldUnitEmploymentPosition, UnitEmploymentPositionDTO unitEmploymentPositionDTO) {

        CostTimeAgreement cta = (unitEmploymentPositionDTO.getCtaId() == null) ? null :
                costTimeAgreementGraphRepository.findOne(unitEmploymentPositionDTO.getCtaId());
        if (cta != null) {
            oldUnitEmploymentPosition.setCta(cta);
        }
        if (!oldUnitEmploymentPosition.getExpertise().getId().equals(unitEmploymentPositionDTO.getExpertiseId())) {
            Expertise expertise = expertiseGraphRepository.findOne(unitEmploymentPositionDTO.getExpertiseId());
            if (!Optional.ofNullable(expertise).isPresent()) {
                throw new DataNotFoundByIdException("Invalid expertise id");
            }
            oldUnitEmploymentPosition.setExpertise(expertise);
        }
        if (!oldUnitEmploymentPosition.getPositionCode().getId().equals(unitEmploymentPositionDTO.getPositionCodeId())) {
            PositionCode positionCode = positionCodeGraphRepository.findOne(unitEmploymentPositionDTO.getPositionCodeId());
            if (!Optional.ofNullable(positionCode).isPresent()) {
                throw new DataNotFoundByIdException("PositionCode Cannot be null" + unitEmploymentPositionDTO.getPositionCodeId());
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

        if (Optional.ofNullable(unitEmploymentPositionDTO.getEndDateMillis()).isPresent()) {

            if (unitEmploymentPositionDTO.getStartDateMillis() > unitEmploymentPositionDTO.getEndDateMillis()) {
                throw new ActionNotPermittedException("Start date can't be less than End Date ");
            }
            oldUnitEmploymentPosition.setEndDateMillis(unitEmploymentPositionDTO.getEndDateMillis());
        }
        oldUnitEmploymentPosition.setStartDateMillis(unitEmploymentPositionDTO.getStartDateMillis());


        oldUnitEmploymentPosition.setWorkingDaysInWeek(unitEmploymentPositionDTO.getWorkingDaysInWeek());
        oldUnitEmploymentPosition.setTotalWeeklyMinutes(unitEmploymentPositionDTO.getTotalWeeklyMinutes() + (unitEmploymentPositionDTO.getTotalWeeklyHours() * 60));
        oldUnitEmploymentPosition.setAvgDailyWorkingHours(unitEmploymentPositionDTO.getAvgDailyWorkingHours());
        oldUnitEmploymentPosition.setHourlyWages(unitEmploymentPositionDTO.getHourlyWages());
        oldUnitEmploymentPosition.setSalary(unitEmploymentPositionDTO.getSalary());


    }

    /*
     * @auth vipul
     * used to get all positions of organization n buy organization and staff Id
     * */
    public List<UnitEmploymentPositionQueryResult> getAllUnitEmploymentPositionsOfStaff(long id, long staffId, String type) {
        Staff staff = staffGraphRepository.findOne(staffId);
        if (!Optional.ofNullable(staff).isPresent()) {
            throw new DataNotFoundByIdException("Invalid Staff Id" + staffId);
        }

        Organization organization = organizationService.getOrganizationDetail(id, type);
        Organization parentOrganization;
        UnitEmployment unitEmployment;
        if (!organization.isParentOrganization()) {
            parentOrganization = organizationService.getParentOfOrganization(organization.getId());
            unitEmployment = unitEmploymentGraphRepository.checkUnitEmploymentOfStaff(parentOrganization.getId(), organization.getId(), staffId);
        } else {
            unitEmployment = unitEmploymentGraphRepository.checkUnitEmploymentOfStaff(organization.getId(), staffId);
        }
        if (!Optional.ofNullable(unitEmployment).isPresent()) {
            logger.info("Unable to get Unit employment of this staff ,{} in organization,{}", staffId, organization.getId());
            throw new DataNotFoundByIdException("unable to create position of staff");
        }

        return unitEmploymentPositionGraphRepository.getAllUnitEmploymentPositionByStaff(unitEmployment.getId(), staffId);
    }

    public PositionCtaWtaQueryResult getCtaAndWtaByExpertiseId(Long unitId, Long expertiseId) {
        PositionCtaWtaQueryResult positionCtaWtaQueryResult = new PositionCtaWtaQueryResult();
        positionCtaWtaQueryResult.setCta(unitEmploymentPositionGraphRepository.getCtaByExpertise(unitId, expertiseId));
        positionCtaWtaQueryResult.setWta(unitEmploymentPositionGraphRepository.getWtaByExpertise(unitId, expertiseId));
        return positionCtaWtaQueryResult;
    }

    public UnitEmploymentPositionQueryResult updateUnitEmploymentPositionWTA(Long unitId, Long unitEmploymentPositionId, Long wtaId, WTADTO updateDTO) {
        UnitEmploymentPosition unitEmploymentPosition = unitEmploymentPositionGraphRepository.findOne(unitEmploymentPositionId);
        if (!Optional.ofNullable(unitEmploymentPosition).isPresent()) {
            throw new DataNotFoundByIdException("Invalid unit Employment Position id" + unitEmploymentPositionId);
        }
        WorkingTimeAgreement oldWta = workingTimeAgreementGraphRepository.findOne(wtaId, 2);
        if (!Optional.ofNullable(oldWta).isPresent()) {
            logger.info("wta not found while updating unit Employment Position for staff %d", unitEmploymentPositionId);
            throw new DataNotFoundByIdException("Invalid wtaId  " + wtaId);
        }

        WorkingTimeAgreement newWta = new WorkingTimeAgreement();

        if (oldWta.getExpertise().getId() != updateDTO.getExpertiseId()) {
            logger.info("Expertise cant be changed :", wtaId);
            throw new ActionNotPermittedException("Expertise can't be changed");
        }
        logger.info(updateDTO.getName());
        newWta = wtaService.copyWta(oldWta, updateDTO);
        newWta.setExpertise(oldWta.getExpertise());
        newWta.setParentWTA(oldWta);
        newWta.setDisabled(false);
        unitEmploymentPosition.setWorkingTimeAgreement(newWta);
        save(unitEmploymentPosition);
        UnitEmploymentPositionQueryResult unitEmploymentPositionQueryResult = unitEmploymentPosition.getBasicDetails();
        newWta.setParentWTA(oldWta.basicDetails());

        newWta.setExpertise(newWta.getExpertise().retrieveBasicDetails());
        unitEmploymentPositionQueryResult.setWorkingTimeAgreement(newWta);
        return unitEmploymentPositionQueryResult;
    }

    public WTAResponseDTO getUnitEmploymentPositionWTA(Long unitId, Long unitEmploymentPositionId) {
        UnitEmploymentPosition unitEmploymentPosition = unitEmploymentPositionGraphRepository.findOne(unitEmploymentPositionId);
        if (!Optional.ofNullable(unitEmploymentPosition).isPresent() || unitEmploymentPosition.isDeleted() == true) {
            throw new DataNotFoundByIdException("Invalid unit Employment Position id" + unitEmploymentPositionId);
        }
        WTAResponseDTO workingTimeAgreement = workingTimeAgreementGraphRepository.findWtaByUnitEmploymentPosition(unitEmploymentPositionId);
        return workingTimeAgreement;
    }

    public UnitEmploymentPositionDTO convertTimeCareEmploymentDTOIntoUnitEmploymentDTO(TimeCareEmploymentDTO timeCareEmploymentDTO,  Long expertiseId, Long staffId, Long employmentTypeId, Long positionCodeId, Long wtaId, Long ctaId){
        Long startDateMillis = DateConverter.convertInUTCTimestamp(timeCareEmploymentDTO.getStartDate());
        Long endDateMillis = null;
        if(!timeCareEmploymentDTO.getEndDate().equals("0001-01-01T00:00:00")){
            endDateMillis = DateConverter.convertInUTCTimestamp(timeCareEmploymentDTO.getEndDate());
        }
        UnitEmploymentPositionDTO unitEmploymentPositionDTO = new UnitEmploymentPositionDTO(positionCodeId, expertiseId, startDateMillis, endDateMillis, Integer.parseInt(timeCareEmploymentDTO.getWeeklyHours()), employmentTypeId, staffId, wtaId, ctaId);
        return unitEmploymentPositionDTO;
    }

    public boolean addEmploymentToUnitByExternalId(List<TimeCareEmploymentDTO> timeCareEmploymentDTOs, String unitExternalId , Long expertiseId){
        Organization organization = organizationGraphRepository.findByExternalId(unitExternalId);
        if (organization == null) {
            throw new DataNotFoundByIdException("Invalid organization external id" + unitExternalId);
        }
        Organization parentOrganization = organizationService.fetchParentOrganization(organization.getId());

        Long countryId = organizationService.getCountryIdOfOrganization(parentOrganization.getId());
        EmploymentType employmentType = employmentTypeGraphRepository.getOneEmploymentTypeByCountryId(countryId, false);

        Expertise expertise = null;
        if(expertiseId == null){
            expertise = expertiseGraphRepository.getOneDefaultExpertiseByCountry(countryId);
        } else {
            expertise = expertiseGraphRepository.getExpertiesOfCountry(countryId, expertiseId);
        }

        if (expertise == null) {
            throw new DataNotFoundByIdException("NO expertise found or Invalid expertise Id : "+expertiseId);
        }

        WorkingTimeAgreement wta = unitEmploymentPositionGraphRepository.getOneDefaultWTA(organization.getId(), expertise.getId());
        CostTimeAgreement cta = unitEmploymentPositionGraphRepository.getOneDefaultCTA(organization.getId(), expertise.getId());
        if (wta == null) {
            throw new DataNotFoundByIdException("NO WTA found for organization : "+organization.getId());
        }
        if (cta == null) {
            throw new DataNotFoundByIdException("NO CTA found for organization : "+organization.getId());
        }

        PositionCode positionCode = positionCodeGraphRepository.getOneDefaultPositionCodeByUnitId(parentOrganization.getId());
        if(positionCode == null){
            throw new DataNotFoundByIdException("NO Position code exist in organization : "+parentOrganization.getId());
        }
        for ( TimeCareEmploymentDTO timeCareEmploymentDTO : timeCareEmploymentDTOs) {
            Staff staff = staffGraphRepository.findStaffByExternalId(timeCareEmploymentDTO.getPersonID(), organization.getId());
            if(staff == null){
                throw new DataNotFoundByIdException("NO staff exist with External Id : " + timeCareEmploymentDTO.getPersonID());
            }
            UnitEmploymentPositionDTO unitEmploymentPosition = convertTimeCareEmploymentDTOIntoUnitEmploymentDTO(timeCareEmploymentDTO, expertise.getId(), staff.getId(), employmentType.getId(), positionCode.getId(), wta.getId(), cta.getId());
            createUnitEmploymentPosition(organization.getId(), "Organization", unitEmploymentPosition, true);
        }
        return true;
    }

    public boolean importAllEmploymentsFromTimeCare(List<TimeCareEmploymentDTO> timeCareEmploymentsDTOs, Long expertiseId) {

        // To prepare list of organization's external Id
        Set<String> listOfWorkPlaceIds = new HashSet<>();
        for (TimeCareEmploymentDTO timeCareStaffDTO : timeCareEmploymentsDTOs) {
            listOfWorkPlaceIds.add(timeCareStaffDTO.getWorkPlaceID());
        }

        for ( String workPlaceId : listOfWorkPlaceIds) {
            List<TimeCareEmploymentDTO> timeCareEmploymentsByWorkPlace = timeCareEmploymentsDTOs.stream().filter(timeCareEmploymentDTO -> timeCareEmploymentDTO.getWorkPlaceID().equals(workPlaceId)).
                    collect(Collectors.toList());
            addEmploymentToUnitByExternalId(timeCareEmploymentsByWorkPlace, workPlaceId, expertiseId);
        }

        return true;
    }
}
