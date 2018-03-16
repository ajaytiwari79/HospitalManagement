package com.kairos.service.pay_table;

import com.kairos.custom_exception.ActionNotPermittedException;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DataNotMatchedException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.pay_group_area.PayGroupArea;
import com.kairos.persistence.model.user.pay_group_area.PayGroupAreaQueryResult;
import com.kairos.persistence.model.user.pay_table.*;
import com.kairos.persistence.repository.organization.OrganizationTypeGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.pay_group_area.PayGroupAreaGraphRepository;
import com.kairos.persistence.repository.user.pay_table.PayTableGraphRepository;
import com.kairos.persistence.model.user.pay_table.OrganizationLevelPayTableDTO;
import com.kairos.persistence.repository.user.pay_table.PayTableRelationShipGraphRepository;
import com.kairos.response.dto.web.pay_table.PayGradeDTO;
import com.kairos.response.dto.web.pay_table.PayGradeMatrixDTO;
import com.kairos.response.dto.web.pay_table.PayTableDTO;
import com.kairos.response.dto.web.pay_table.PayTableResponseWrapper;
import com.kairos.service.UserBaseService;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by prabjot on 26/12/17.
 */
@Service
public class PayTableService extends UserBaseService {

    @Inject
    private PayTableGraphRepository payTableGraphRepository;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private OrganizationTypeGraphRepository organizationTypeGraphRepository;
    @Inject
    private ExpertiseGraphRepository expertiseGraphRepository;

    @Inject
    private PayGroupAreaGraphRepository payGroupAreaGraphRepository;

    @Inject
    private PayTableRelationShipGraphRepository payTableRelationShipGraphRepository;

    private Logger logger = LoggerFactory.getLogger(PayTableService.class);

    private PayTable findById(Long payLevelId) {
        PayTable payTable = payTableGraphRepository.findOne(payLevelId);
        if (payTable == null) {
            throw new DataNotFoundByIdException("Invalid pay level id");
        }
        return payTable;
    }

    private PayLevelDTO getPayLevelResponse(PayTable payTable, PayLevelDTO payLevelDTO) {
        payLevelDTO.setId(payTable.getId());
        return payLevelDTO;
    }


    public PayTableResponseWrapper getPayTablesByOrganizationLevel(Long countryId, Long organizationLevelId) {
        Level level = countryGraphRepository.getLevel(countryId, organizationLevelId);
        if (!Optional.ofNullable(level).isPresent()) {
            throw new DataNotFoundByIdException("Invalid level in country");
        }
        List<PayGroupAreaQueryResult> payGroupAreas = payGroupAreaGraphRepository.getPayGroupAreaByOrganizationLevelId(organizationLevelId);
        List<PayTableQueryResult> payTables = payTableGraphRepository.findPayTableByOrganizationLevel(organizationLevelId, -1L);
        PayTableResponseWrapper responseWrapper = new PayTableResponseWrapper(payGroupAreas, payTables);
        return responseWrapper;

    }

    public List<OrganizationLevelPayTableDTO> getOrganizationLevelWisePayTables(Long countryId) {
        Country country = countryGraphRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            throw new DataNotFoundByIdException("Invalid country");
        }
        List<OrganizationLevelPayTableDTO> payTables = payTableGraphRepository.getOrganizationLevelWisePayTables(countryId);
        return payTables;
    }

    public PayTableQueryResult createPayLevel(Long countryId, PayTableDTO payTableDTO) {
        logger.info(payTableDTO.toString());
        Level level = countryGraphRepository.getLevel(countryId, payTableDTO.getLevelId());
        if (!Optional.ofNullable(level).isPresent()) {
            throw new DataNotFoundByIdException("Invalid level id " + payTableDTO.getLevelId());
        }

        Boolean isAlreadyExists = payTableGraphRepository.
                checkPayTableNameAlreadyExitsByNameOrShortName(countryId, -1L, "(?i)" + payTableDTO.getName().trim(),
                        "(?i)" + payTableDTO.getShortName().trim());
        if (isAlreadyExists) {
            throw new DuplicateDataException("Name " + payTableDTO.getName() + "or short Name " + payTableDTO.getShortName() + " already Exist in country " + countryId);
        }

        PayTable payTable = new PayTable(payTableDTO.getName().trim(), payTableDTO.getShortName().trim(), payTableDTO.getDescription(), level, payTableDTO.getStartDateMillis(), payTableDTO.getEndDateMillis());


        List<PayTableQueryResult> payTables = payTableGraphRepository.findPayTableByOrganizationLevel(payTableDTO.getLevelId(), -1L);
        validatePayLevel(payTables, payTableDTO);
        save(payTable);

        PayTableQueryResult payTableQueryResult = new PayTableQueryResult(payTable.getName(), payTable.getShortName(), payTable.getDescription(), payTable.getStartDateMillis().getTime(), payTable.getEndDateMillis());
        payTableQueryResult.setId(payTable.getId());
        payTableQueryResult.setLevel(level);
        return payTableQueryResult;
    }

    private void validatePayLevel(List<PayTableQueryResult> payTables, PayTableDTO payLevelDTO) {

        payTables.forEach(payTableToValidate -> {
            logger.info(payTableToValidate.toString());
            if (payTableToValidate.getEndDateMillis() != null) {
                if (new DateTime(payLevelDTO.getStartDateMillis()).isBefore(new DateTime(payTableToValidate.getEndDateMillis()))) {
                    throw new ActionNotPermittedException("overlap date range" + new DateTime(payLevelDTO.getStartDateMillis()) + " ON " + (new DateTime(payTableToValidate.getEndDateMillis())));
                }
                if (payLevelDTO.getEndDateMillis() != null) {
                    Interval previousInterval = new Interval(payTableToValidate.getStartDateMillis(), payTableToValidate.getEndDateMillis().getTime());
                    Interval interval = new Interval(payLevelDTO.getStartDateMillis().getTime(), payLevelDTO.getEndDateMillis().getTime());
                    if (previousInterval.overlaps(interval))
                        throw new ActionNotPermittedException("overlap date range");
                } else {
                    logger.info("new  EndDate {}", new DateTime(payLevelDTO.getStartDateMillis()) + "  End date " + (new DateTime(payTableToValidate.getEndDateMillis())));
                    if (new DateTime(payLevelDTO.getStartDateMillis()).isBefore(new DateTime(payTableToValidate.getEndDateMillis()))) {
                        throw new ActionNotPermittedException("overlap date range" + new DateTime(payLevelDTO.getStartDateMillis()).toDate() + " --> " + new DateTime(payTableToValidate.getEndDateMillis()).toDate());
                    }
                }
            } else {
                if (payLevelDTO.getEndDateMillis() != null) {
                    if (new DateTime(payLevelDTO.getEndDateMillis()).isAfter(new DateTime(payTableToValidate.getStartDateMillis()))) {
                        throw new ActionNotPermittedException("overlap date range " + new DateTime(payLevelDTO.getEndDateMillis()).toDate()
                                + " --> " + new DateTime(payTableToValidate.getStartDateMillis()).toDate());
                    }
                } else {
                    throw new ActionNotPermittedException("overlap date range " + new DateTime(payTableToValidate.getStartDateMillis()) + " to " + new DateTime(payLevelDTO.getStartDateMillis()));
                }
            }
        });

    }

    private PayTable validatePayLevelMetaData(Long countryId, PayLevelDTO payLevelDTO) {
        Country country = countryGraphRepository.findOne(countryId);
        if (country == null) {
            throw new InternalError("Invalid countryId");
        }
        OrganizationType organizationType = organizationTypeGraphRepository.findOne(payLevelDTO.getOrganizationTypeId());
        if (organizationType == null) {
            throw new InternalError("Invalid Organization type id ");
        }
        Expertise expertise = expertiseGraphRepository.findOne(payLevelDTO.getExpertiseId());
        if (expertise == null) {
            throw new InternalError("Invalid expertise id ");
        }

        DateTime startDateAsJodaDate = new DateTime(payLevelDTO.getStartDate()).withHourOfDay(0).withMinuteOfHour(0).
                withSecondOfMinute(0).withMillisOfSecond(0);

        PayTable payTable = new PayTable();

        if (Optional.ofNullable(payLevelDTO.getLevelId()).isPresent()) {
            Level level = organizationTypeGraphRepository.getLevel(payLevelDTO.getOrganizationTypeId(), payLevelDTO.getLevelId());
            if (level == null) {
                throw new InternalError("Invalid level id");
            }
            payTable.setLevel(level);
        }

        if (payLevelDTO.getEndDate() != null) {
            DateTime endDateAsJodaDate = new DateTime(payLevelDTO.getEndDate()).withHourOfDay(0).withMinuteOfHour(0).
                    withSecondOfMinute(0).withMillisOfSecond(0);
            payTable.setEndDateMillis(endDateAsJodaDate.toDate());
        }
        return payTable;
    }

    public PayTableDTO updatePayLevel(Long payLevelId, PayTableDTO payTableDTO) {
/*

        PayTable payTable = findById(payLevelId);
        DateTime startDateToUpdate = new DateTime(payLevelDTO.getStartDateMillis()).withHourOfDay(0).withMinuteOfHour(0).
                withSecondOfMinute(0).withMillisOfSecond(0);
        DateTime payLevelDate = new DateTime(payTable.getStartDateMillis()).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
        DateTime currentDate = new DateTime().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
        if (startDateToUpdate.compareTo(payLevelDate) != 0 &&
                payLevelDate.compareTo(currentDate) < 0) {
            throw new InternalError("Start date can't be update");
        }
        DateTime startDateAsJodaDate = new DateTime(payLevelDTO.getStartDateMillis()).withHourOfDay(0).withMinuteOfHour(0).
                withSecondOfMinute(0).withMillisOfSecond(0);
        payTable.setStartDateMillis(startDateAsJodaDate.toDate());
        if (payLevelDTO.getEndDateMillis() != null) {
            payTable.setEndDateMillis(payLevelDTO.getEndDateMillis());
        }
        payTable.setName(payLevelDTO.getName());
        save(payTable);
*/
        return payTableDTO;
    }

    public PayGradeQueryResult addPayGradeInPayTable(Long payTableId, PayGradeDTO payGradeDTO) {
        PayTable payTable = payTableGraphRepository.findOne(payTableId);
        if (!Optional.ofNullable(payTable).isPresent()) {
            throw new DataNotFoundByIdException("Invalid pay level id");
        }
        Boolean isAlreadyExists = payTableGraphRepository.checkPayGradeLevelAlreadyExists(payTableId, payGradeDTO.getPayGradeLevel());
        if (isAlreadyExists) {
            throw new DuplicateDataException("Pay grade level " + payGradeDTO.getPayGradeLevel() + " already exists");
        }
        PayGrade payGrade = new PayGrade(payGradeDTO.getPayGradeLevel());
        if (Optional.ofNullable(payTable.getPayGrades()).isPresent()) {
            payTable.getPayGrades().add(payGrade);
        } else {
            List<PayGrade> payGrades = new ArrayList<>();
            payGrades.add(payGrade);
            payTable.setPayGrades(payGrades);
        }
        save(payTable);

        List<Long> payGroupAreasId = payGradeDTO.getPayGrades().stream().map(PayGradeMatrixDTO::getPayGroupAreaId).collect(Collectors.toList());

        List<PayGroupArea> payGroupAreas = payGroupAreaGraphRepository.findAllById(payGroupAreasId);

        if (payGroupAreas.size() != payGroupAreasId.size()) {
            throw new DataNotMatchedException("unable to get all municipality");
        }
        List<PayGradePayGroupAreaRelationShip> payGradePayGroupAreaRelationShips = new ArrayList<>();
        // contains all

        payGroupAreas.forEach(currentPayGroupArea -> {

            for (int i = 0; i < payGradeDTO.getPayGrades().size(); i++) {

                if (payGradeDTO.getPayGrades().get(i).getPayGroupAreaId().equals(currentPayGroupArea.getId())) {
                    PayGradePayGroupAreaRelationShip payGradePayGroupAreaRelationShip
                            = new PayGradePayGroupAreaRelationShip(payGrade, currentPayGroupArea, payGradeDTO.getPayGrades().get(i).getPayGradeValue());
                    payGradePayGroupAreaRelationShips.add(payGradePayGroupAreaRelationShip);
                }

            }
        });
        payTableRelationShipGraphRepository.saveAll(payGradePayGroupAreaRelationShips);

        PayGradeQueryResult payGradeQueryResult =
                new PayGradeQueryResult(payTableId,payGrade.getPayGradeLevel(),payGrade.getId(),getPayGradeResponse(payGradePayGroupAreaRelationShips));
        return payGradeQueryResult;
    }

    private List<PayGradeMatrixDTO> getPayGradeResponse(List<PayGradePayGroupAreaRelationShip> payGradePayGroupAreaRelationShips) {
        List<PayGradeMatrixDTO> payGradeMatrices = new ArrayList<>();
        payGradePayGroupAreaRelationShips.forEach(currentPayGroupArea -> {
            PayGradeMatrixDTO payGradeMatrixDTO =
                    new PayGradeMatrixDTO(currentPayGroupArea.getPayGroupArea().getId(), currentPayGroupArea.getPayGradeValue(), currentPayGroupArea.getId());
            payGradeMatrices.add(payGradeMatrixDTO);
        });
        return payGradeMatrices;
    }
}
