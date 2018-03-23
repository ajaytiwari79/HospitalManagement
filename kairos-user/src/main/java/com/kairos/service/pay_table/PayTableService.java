package com.kairos.service.pay_table;

import com.kairos.custom_exception.ActionNotPermittedException;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DataNotMatchedException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.pay_group_area.PayGroupArea;
import com.kairos.persistence.model.user.pay_group_area.PayGroupAreaQueryResult;
import com.kairos.persistence.model.user.pay_table.*;
import com.kairos.persistence.repository.organization.OrganizationTypeGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.pay_group_area.PayGroupAreaGraphRepository;
import com.kairos.persistence.repository.user.pay_table.PayGradeGraphRepository;
import com.kairos.persistence.repository.user.pay_table.PayTableGraphRepository;
import com.kairos.persistence.model.user.pay_table.OrganizationLevelPayTableDTO;
import com.kairos.persistence.repository.user.pay_table.PayTableRelationShipGraphRepository;
import com.kairos.response.dto.web.pay_table.*;
import com.kairos.service.UserBaseService;
import com.kairos.util.DateUtil;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static javax.management.timer.Timer.ONE_DAY;

/**
 * Created by prabjot on 26/12/17.
 */
@Service
public class PayTableService extends UserBaseService {

    @Inject
    private PayTableGraphRepository payTableGraphRepository;
    @Inject
    private PayGradeGraphRepository payGradeGraphRepository;
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

    public PayTableQueryResult createPayTable(Long countryId, PayTableDTO payTableDTO) {
        logger.info(payTableDTO.toString());
        Level level = countryGraphRepository.getLevel(countryId, payTableDTO.getLevelId());
        if (!Optional.ofNullable(level).isPresent()) {
            throw new DataNotFoundByIdException("Invalid grade id " + payTableDTO.getLevelId());
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

        PayTableQueryResult payTableQueryResult = new PayTableQueryResult(payTable.getName(), payTable.getShortName(), payTable.getDescription(), payTable.getStartDateMillis().getTime(), payTable.getEndDateMillis(), payTable.isPublished());
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

    private void prepareDates(PayTable payTable, PayTableUpdateDTO payTableDTO) {
        if (!payTable.getStartDateMillis().equals(payTableDTO.getStartDateMillis())) {
            // The start date is modified Now We need to compare is it less than today
            if (new DateTime(payTableDTO.getStartDateMillis()).isBefore(new DateTime(DateUtil.getCurrentDate()))) {
                throw new ActionNotPermittedException("Start Date Cant be less than current date");
            }
            payTable.setStartDateMillis(payTableDTO.getStartDateMillis());
        }
        // End date
        // If already end date was set  but now no value so we are removing
        if (!Optional.ofNullable(payTableDTO.getEndDateMillis()).isPresent() && Optional.ofNullable(payTable.getEndDateMillis()).isPresent()) {
            payTable.setEndDateMillis(null);

        }

        // If already not present now its present    Previous its absent
        else if (!Optional.ofNullable(payTable.getEndDateMillis()).isPresent() && Optional.ofNullable(payTableDTO.getEndDateMillis()).isPresent()) {
            if (new DateTime(payTableDTO.getEndDateMillis()).isBefore(new DateTime(DateUtil.getCurrentDate()))) {
                throw new ActionNotPermittedException("end Date Cant be less than current date");
            }
            payTable.setEndDateMillis(payTableDTO.getEndDateMillis());
        }

        // If already present and still present // NOw checking are they same or different
        else if (Optional.ofNullable(payTable.getEndDateMillis()).isPresent() && Optional.ofNullable(payTableDTO.getEndDateMillis()).isPresent()) {
            if (!payTable.getEndDateMillis().equals(payTableDTO.getEndDateMillis())) {//The end date is modified Now We need to compare is it less than today
                if (new DateTime(payTableDTO.getEndDateMillis()).isBefore(new DateTime(DateUtil.getCurrentDate()))) {
                    throw new ActionNotPermittedException("end Date Cant be less than current date");
                }
                payTable.setEndDateMillis(payTableDTO.getEndDateMillis());
            }
        }

    }

    // update basic detail of pay Table we are not making any copy as this time  as this does not impact any calculation
    public PayTableQueryResult updatePayTable(Long countryId, Long payTableId, PayTableUpdateDTO payTableDTO) {
        PayTable payTable = payTableGraphRepository.findOne(payTableId);
        if (!Optional.ofNullable(payTable).isPresent()) {
            throw new DataNotFoundByIdException("Invalid pay grade id");
        }
        // if  name or short name is changes then only we are checking its name existance
        // skipped to current pay table it is checking weather any other payTable has same name or short name in current organization level
        if (!payTableDTO.getName().trim().equalsIgnoreCase(payTable.getName()) || !payTableDTO.getShortName().trim().equalsIgnoreCase(payTable.getShortName())) {
            Boolean isAlreadyExists = payTableGraphRepository.
                    checkPayTableNameAlreadyExitsByNameOrShortName(countryId, payTableId, "(?i)" + payTableDTO.getName().trim(),
                            "(?i)" + payTableDTO.getShortName().trim());
            if (isAlreadyExists) {
                throw new DuplicateDataException("Name " + payTableDTO.getName() + "or short Name " + payTableDTO.getShortName() + " already Exist in country " + countryId);
            }
        }
        payTable.setName(payTableDTO.getName().trim());
        payTable.setShortName(payTableDTO.getShortName().trim());
        payTable.setDescription(payTableDTO.getDescription());
        prepareDates(payTable, payTableDTO);
        save(payTable);
        PayTableQueryResult payTableQueryResult = new PayTableQueryResult(payTable.getName(), payTable.getShortName(), payTable.getDescription(), payTable.getStartDateMillis().getTime(), payTable.getEndDateMillis(), payTable.isPublished());
        payTableQueryResult.setId(payTable.getId());
        return payTableQueryResult;
    }

    public List<PayGradeQueryResult> addPayGradeInPayTable(Long payTableId, PayGradeDTO payGradeDTO) {
        PayTable payTable = payTableGraphRepository.findOne(payTableId);
        if (!Optional.ofNullable(payTable).isPresent()) {
            throw new DataNotFoundByIdException("Invalid pay grade id");
        }
        Boolean isAlreadyExists = payTableGraphRepository.checkPayGradeLevelAlreadyExists(payTableId, payGradeDTO.getPayGradeLevel());
        if (isAlreadyExists) {
            throw new DuplicateDataException("Pay grade level " + payGradeDTO.getPayGradeLevel() + " already exists");
        }
        // payTable is not published
        List<PayGradeQueryResult> payGradesData = new ArrayList<>();
        if (!payTable.isPublished()) {
            payGradesData.add(addPayGradeInCurrentPayTable(payTable, payGradeDTO));
        } else {
            payGradesData = createCopyOfPayTableAndAddPayGrade(payTable, payGradeDTO);
        }
        return payGradesData;

    }

    private List<PayGradeQueryResult> createCopyOfPayTableAndAddPayGrade(PayTable payTable, PayGradeDTO payGradeDTO) {
        List<PayGradeQueryResult> payGradeQueryResults = new ArrayList<>();

        PayTable payTableByMapper = new PayTable();
        BeanUtils.copyProperties(payTable, payTableByMapper);
        payTableByMapper.setId(null);

        payTableByMapper.setPayTable(payTable);
        payTableByMapper.setPayGrades(null);
        payGradeQueryResults.add(addPayGradeInCurrentPayTable(payTableByMapper, payGradeDTO));
        payTable.setHasTempCopy(true);
        payTableByMapper.setPublished(false);
        save(payTableByMapper);
        // copying all previous and then adding in pay Table as well.
        List<PayGrade> payGradesObjects = new ArrayList<>();

        for (PayGrade currentPayGrade : payTable.getPayGrades()) {
            PayGrade newPayGrade = new PayGrade(currentPayGrade.getPayGradeLevel(), false);
            List<PayGradePayGroupAreaRelationShip> payGradePayGroupAreaRelationShips = new ArrayList<>();
            HashSet<PayTableMatrixQueryResult> payTableMatrix = payGradeGraphRepository.getPayGradeMatrixByPayGradeId(currentPayGrade.getId());
            payTableMatrix.forEach(currentObj -> {
                PayGradePayGroupAreaRelationShip payGradePayGroupAreaRelationShip
                        = new PayGradePayGroupAreaRelationShip(newPayGrade, new PayGroupArea(currentObj.getPayGroupAreaId(), currentObj.getPayGroupAreaName()), currentObj.getPayGroupAreaAmount());
                payGradePayGroupAreaRelationShips.add(payGradePayGroupAreaRelationShip);
            });
            payTableRelationShipGraphRepository.saveAll(payGradePayGroupAreaRelationShips);
            payGradesObjects.add(newPayGrade);
            PayGradeQueryResult payGradeQueryResult =
                    new PayGradeQueryResult(payTableByMapper.getId(), newPayGrade.getPayGradeLevel(), newPayGrade.getId(), getPayGradeResponse(payGradePayGroupAreaRelationShips), newPayGrade.isPublished());
            payGradeQueryResults.add(payGradeQueryResult);
        }
        payTableByMapper.setPayGrades(payGradesObjects);
        save(payTableByMapper);
        // Adding new Grade in PayTable

        return payGradeQueryResults;
    }


    public PayGradeQueryResult addPayGradeInCurrentPayTable(PayTable payTable, PayGradeDTO payGradeDTO) {
        List<Long> payGroupAreasId = payGradeDTO.getPayTableMatrix().stream().map(PayTableMatrixDTO::getPayGroupAreaId).collect(Collectors.toList());

        List<PayGroupArea> payGroupAreas = payGroupAreaGraphRepository.findAllById(payGroupAreasId);
        if (payGroupAreas.size() != payGroupAreasId.size()) {
            throw new DataNotMatchedException("unable to get all payGroup areas ");
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

        List<PayGradePayGroupAreaRelationShip> payGradePayGroupAreaRelationShips = new ArrayList<>();
        // contains all

        payGroupAreas.forEach(currentPayGroupArea -> {

            for (int i = 0; i < payGradeDTO.getPayTableMatrix().size(); i++) {

                if (payGradeDTO.getPayTableMatrix().get(i).getPayGroupAreaId().equals(currentPayGroupArea.getId())) {
                    PayGradePayGroupAreaRelationShip payGradePayGroupAreaRelationShip
                            = new PayGradePayGroupAreaRelationShip(payGrade, currentPayGroupArea, payGradeDTO.getPayTableMatrix().get(i).getPayGroupAreaAmount());
                    payGradePayGroupAreaRelationShips.add(payGradePayGroupAreaRelationShip);
                }

            }
        });
        payTableRelationShipGraphRepository.saveAll(payGradePayGroupAreaRelationShips);

        return new PayGradeQueryResult(payTable.getId(), payGrade.getPayGradeLevel(), payGrade.getId(), getPayGradeResponse(payGradePayGroupAreaRelationShips), payGrade.isPublished());

    }

    private List<PayTableMatrixDTO> getPayGradeResponse(List<PayGradePayGroupAreaRelationShip> payGradePayGroupAreaRelationShips) {
        List<PayTableMatrixDTO> payGradeMatrices = new ArrayList<>();
        payGradePayGroupAreaRelationShips.forEach(currentPayGroupArea -> {
            PayTableMatrixDTO payTableMatrixDTO =
                    new PayTableMatrixDTO(currentPayGroupArea.getPayGroupArea().getId(), currentPayGroupArea.getPayGroupAreaAmount(), currentPayGroupArea.getId());
            payGradeMatrices.add(payTableMatrixDTO);
        });
        return payGradeMatrices;
    }

    public List<PayGradeQueryResult> getPayGridsByPayTableId(Long payTableId) {
        PayTable payTable = payTableGraphRepository.findOne(payTableId);
        if (!Optional.ofNullable(payTable).isPresent() || payTable.isDeleted()) {
            throw new DataNotFoundByIdException("Invalid pay table id");
        }
        return payTableGraphRepository.getPayGridsByPayTableId(payTableId);
    }

    public boolean removePayGradeInPayTable(Long payTableId, Long payGradeId) {
        PayTable payTable = payTableGraphRepository.findOne(payTableId);
        if (!Optional.ofNullable(payTable).isPresent() || payTable.isDeleted()) {
            throw new DataNotFoundByIdException("Invalid pay table id");
        }
        if (payTable.isPublished()) {
            throw new ActionNotPermittedException("PayTable is already published so cant remove");
        }
        if (!Optional.ofNullable(payTable.getPayGrades()).isPresent() || payTable.getPayGrades().isEmpty()) {
            throw new DataNotFoundByIdException("Invalid pay grade id");
        }
        boolean found = false;
        for (PayGrade currentPayGrade : payTable.getPayGrades()) {
            if (currentPayGrade.getId().equals(payGradeId)) {
                found = true;
                break;
            }
        }
        if (found)
            payGradeGraphRepository.removeAllPayGroupAreasFromPayGrade(payGradeId);
        return found;
    }

    public boolean removePayTable(Long payTableId) {
        PayTable payTable = payTableGraphRepository.findOne(payTableId);
        if (!Optional.ofNullable(payTable).isPresent() || payTable.isDeleted()) {
            throw new DataNotFoundByIdException("Invalid pay table id");
        }
        if (payTable.isPublished()) {
            throw new ActionNotPermittedException("PayTable is already published so can't remove");
        }
        payTable.setDeleted(true);
        save(payTable);
        return true;
    }

    public PayGradeDTO updatePayGradeInPayTable(Long payTableId, Long payGradeId, PayGradeDTO payGradeDTO) {
        PayTable payTable = payTableGraphRepository.findOne(payTableId);
        if (!Optional.ofNullable(payTable).isPresent() || payTable.isDeleted()) {
            throw new DataNotFoundByIdException("Invalid pay table id");
        }
        // user is updating in a unpublished payTable
        if (!payTable.isPublished()) {
            List<Long> relationshipIds = payGradeDTO.getPayTableMatrix().stream().map(PayTableMatrixDTO::getId).collect(Collectors.toList());
            Iterable<PayGradePayGroupAreaRelationShip> payGradeData = payTableRelationShipGraphRepository.findAllById(relationshipIds);
            for (PayTableMatrixDTO currentPayTable : payGradeDTO.getPayTableMatrix()) {
                // Rare case
                if (currentPayTable.getId() == null) { // this is a new pay group area which is added after creation and now user is setting value on that
                    PayGroupArea payGroupArea = payGroupAreaGraphRepository.findOne(currentPayTable.getPayGroupAreaId());
                    PayGrade payGrade = null;
                    for (PayGrade currentPayGrade : payTable.getPayGrades()) {
                        if (currentPayGrade.getId().equals(payGradeId)) {
                            payGrade = currentPayGrade;
                            break;
                        }
                    }
                    PayGradePayGroupAreaRelationShip payGradePayGroupAreaRelationShip
                            = new PayGradePayGroupAreaRelationShip(payGrade, payGroupArea, currentPayTable.getPayGroupAreaAmount());
                    payTableRelationShipGraphRepository.save(payGradePayGroupAreaRelationShip);
                    currentPayTable.setId(payGradePayGroupAreaRelationShip.getId());
                } else {
                    for (PayGradePayGroupAreaRelationShip currentPayGradeData : payGradeData) {
                        if (currentPayGradeData.getId().equals(currentPayTable.getId()) && !currentPayGradeData.getPayGroupAreaAmount().equals(currentPayTable.getPayGroupAreaAmount())) {
                            logger.info("user has changed Amount {} from {}", currentPayTable.getPayGroupAreaAmount(), currentPayGradeData.getPayGroupAreaAmount());
                            // The state was published and the amount is changed so we are now adding/checking weather any already relation exist or not
                            currentPayGradeData.setPayGroupAreaAmount(currentPayTable.getPayGroupAreaAmount());
                        }
                    }
                }
            }
            payTableRelationShipGraphRepository.saveAll(payGradeData);
        } else {
            List<PayGradeQueryResult> payGradeQueryResults = new ArrayList<>();
            List<PayGrade> payGradesObjects = new ArrayList<>();

            // creating a new PayTable
            PayTable payTableByMapper = new PayTable();
            BeanUtils.copyProperties(payTable, payTableByMapper);
            payTableByMapper.setId(null);
            payTableByMapper.setPayTable(payTable);
            payTableByMapper.setPayGrades(null);
            payTableByMapper.setPublished(false);
            payTable.setHasTempCopy(true);
            save(payTableByMapper);
            for (PayGrade currentPayGrade : payTable.getPayGrades()) {
                PayGrade newPayGrade = new PayGrade(currentPayGrade.getPayGradeLevel(), false);
                List<PayGradePayGroupAreaRelationShip> payGradePayGroupAreaRelationShips = new ArrayList<>();
                if (payGradeDTO.getPayGradeId().equals(currentPayGrade.getId())) {
                    // user has changed the value in  this
                    for (PayTableMatrixDTO matrix : payGradeDTO.getPayTableMatrix()) {
                        PayGradePayGroupAreaRelationShip payGradePayGroupAreaRelationShip
                                = new PayGradePayGroupAreaRelationShip(newPayGrade, new PayGroupArea(matrix.getPayGroupAreaId()), matrix.getPayGroupAreaAmount());
                        payGradePayGroupAreaRelationShips.add(payGradePayGroupAreaRelationShip);

                        payTableRelationShipGraphRepository.saveAll(payGradePayGroupAreaRelationShips);
                        payGradesObjects.add(newPayGrade);
                        PayGradeQueryResult payGradeQueryResult =
                                new PayGradeQueryResult(payTableByMapper.getId(), newPayGrade.getPayGradeLevel(), newPayGrade.getId(), getPayGradeResponse(payGradePayGroupAreaRelationShips), newPayGrade.isPublished());
                        payGradeQueryResults.add(payGradeQueryResult);

                    }
                } else {
                    HashSet<PayTableMatrixQueryResult> payTableMatrix = payGradeGraphRepository.getPayGradeMatrixByPayGradeId(currentPayGrade.getId());
                    payTableMatrix.forEach(currentObj -> {
                        PayGradePayGroupAreaRelationShip payGradePayGroupAreaRelationShip
                                = new PayGradePayGroupAreaRelationShip(newPayGrade, new PayGroupArea(currentObj.getPayGroupAreaId(), currentObj.getPayGroupAreaName()), currentObj.getPayGroupAreaAmount());
                        payGradePayGroupAreaRelationShips.add(payGradePayGroupAreaRelationShip);
                    });
                    payTableRelationShipGraphRepository.saveAll(payGradePayGroupAreaRelationShips);
                    payGradesObjects.add(newPayGrade);
                    PayGradeQueryResult payGradeQueryResult =
                            new PayGradeQueryResult(payTableByMapper.getId(), newPayGrade.getPayGradeLevel(), newPayGrade.getId(), getPayGradeResponse(payGradePayGroupAreaRelationShips), newPayGrade.isPublished());
                    payGradeQueryResults.add(payGradeQueryResult);
                }
            }
            payTableByMapper.setPayGrades(payGradesObjects);
            save(payTableByMapper);
        }
        return payGradeDTO;
    }

    public List<PayTable> publishPayTable(Long payTableId, Long publishedDateMillis) {
        PayTable payTable = payTableGraphRepository.findOne(payTableId);
        if (!Optional.ofNullable(payTable).isPresent() || payTable.isDeleted()) {
            throw new DataNotFoundByIdException("Invalid pay table id");
        }
        if (payTable.isPublished()) {
            throw new ActionNotPermittedException("PayTable is already published");
        }

        payTable.setPublished(true);
        payTable.setStartDateMillis(new Date(publishedDateMillis));
        for (PayGrade currentPayGrade : payTable.getPayGrades()) {
            currentPayGrade.setPublished(true);
        }
        save(payTable);
        List<PayTable> response = new ArrayList<>();
        response.add(payTable);
        PayTable parentPayTable = payTableGraphRepository.getPermanentPayTableByPayTableId(payTableId);
        if (Optional.ofNullable(parentPayTable).isPresent()) {
            payTableGraphRepository.changeStateOfRelationShip(parentPayTable.getId(), publishedDateMillis - ONE_DAY);
            parentPayTable.setEndDateMillis(new Date(publishedDateMillis - ONE_DAY));
            parentPayTable.setHasTempCopy(false);
            payTable.setPublished(true);
            parentPayTable.setPayTable(null);
            response.add(parentPayTable);

        }

        return response;

    }
}
