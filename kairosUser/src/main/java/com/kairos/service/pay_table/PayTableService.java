package com.kairos.service.pay_table;


import com.kairos.custom_exception.ActionNotPermittedException;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DataNotMatchedException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.country.FunctionDTO;
import com.kairos.persistence.model.user.pay_group_area.PayGroupArea;
import com.kairos.persistence.model.user.pay_group_area.PayGroupAreaQueryResult;
import com.kairos.persistence.model.user.pay_table.*;
import com.kairos.persistence.repository.organization.OrganizationTypeGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.FunctionGraphRepository;
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
import org.joda.time.Days;
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

    @Inject
    private FunctionGraphRepository functionGraphRepository;

    private Logger logger = LoggerFactory.getLogger(PayTableService.class);


    public PayTableResponseWrapper getPayTablesByOrganizationLevel(Long countryId, Long organizationLevelId, Long startDate) {
        Level level = countryGraphRepository.getLevel(countryId, organizationLevelId);
        if (!Optional.ofNullable(level).isPresent()) {
            throw new DataNotFoundByIdException("Invalid level in country");
        }

        List<PayGroupAreaQueryResult> payGroupAreaQueryResults = payGroupAreaGraphRepository.getPayGroupAreaByOrganizationLevelId(organizationLevelId);
        List<FunctionDTO> functions = functionGraphRepository.getFunctionsByOrganizationLevel(organizationLevelId);
        List<PayTableResponse> payTableQueryResults = payTableGraphRepository.findActivePayTableByOrganizationLevel(organizationLevelId, startDate);
        PayTableResponse result = null;
        if (payTableQueryResults.size() > 1) {
            // multiple payTables are found NOW need to filter by date
            for (PayTableResponse currentPayTable : payTableQueryResults) {
                if (Optional.ofNullable(currentPayTable.getEndDateMillis()).isPresent() &&
                        (new DateTime(currentPayTable.getEndDateMillis()).isAfter(new DateTime(startDate)) || new DateTime(currentPayTable.getEndDateMillis()).isEqual(new DateTime(startDate)))
                        && (new DateTime(currentPayTable.getStartDateMillis()).isBefore(new DateTime(startDate)) || new DateTime(currentPayTable.getStartDateMillis()).isEqual(new DateTime(startDate)))) {
                    result = currentPayTable;
                    break;
                }
                if (!Optional.ofNullable(currentPayTable.getEndDateMillis()).isPresent() &&
                        (new DateTime(currentPayTable.getStartDateMillis()).isBefore(new DateTime(startDate)) || new DateTime(currentPayTable.getStartDateMillis()).isEqual(new DateTime(startDate)))) {
                    result = currentPayTable;
                    break;
                }
            }
        } else if (payTableQueryResults.size() == 1)
            result = payTableQueryResults.get(0);

        PayTableResponseWrapper payTableResponseWrapper = new PayTableResponseWrapper(payGroupAreaQueryResults, result, functions);
        return payTableResponseWrapper;

    }

    public List<OrganizationLevelPayTableDTO> getOrganizationLevelWisePayTables(Long countryId) {
        Country country = countryGraphRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            throw new DataNotFoundByIdException("Invalid country");
        }
        List<OrganizationLevelPayTableDTO> payTables = payTableGraphRepository.getOrganizationLevelWisePayTables(countryId);
        return payTables;
    }

    public PayTableResponse createPayTable(Long countryId, PayTableDTO payTableDTO) {
        logger.info(payTableDTO.toString());
        Level level = countryGraphRepository.getLevel(countryId, payTableDTO.getLevelId());
        if (!Optional.ofNullable(level).isPresent()) {
            throw new DataNotFoundByIdException("Invalid level id " + payTableDTO.getLevelId());
        }

       Boolean isAlreadyExists = payTableGraphRepository.
                checkPayTableNameAlreadyExitsByName(countryId, -1L, "(?i)" + payTableDTO.getName().trim());
         if(isAlreadyExists) {
            throw new DuplicateDataException("Name " + payTableDTO.getName()  + " already Exist in country " + countryId);
        }
        PayTableResponse payTableToValidate = payTableGraphRepository.findPayTableByOrganizationLevel(payTableDTO.getLevelId(), -1L);
        // if any payTable is found then only validate
        if (Optional.ofNullable(payTableToValidate).isPresent())
            validatePayLevel(payTableToValidate, payTableDTO.getStartDateMillis(), payTableDTO.getEndDateMillis());
        PayTable payTable = new PayTable(payTableDTO.getName().trim(), payTableDTO.getShortName(), payTableDTO.getDescription(), level, payTableDTO.getStartDateMillis(), payTableDTO.getEndDateMillis(), payTableDTO.getPaymentUnit(), true);
        save(payTable);
        PayTableResponse payTableResponse = new PayTableResponse(payTable.getName(), payTable.getShortName(), payTable.getDescription(), payTable.getStartDateMillis().getTime(), (payTable.getEndDateMillis() != null) ? payTable.getEndDateMillis().getTime() : null, payTable.isPublished(), payTable.getPaymentUnit(), payTable.isEditable());
        payTableResponse.setId(payTable.getId());
        payTableResponse.setLevel(level);
        return payTableResponse;
    }


    private void validatePayLevel(PayTableResponse payTableToValidate, Date startDateMillis, Date endDateMillis) {
        if (payTableToValidate.getEndDateMillis() != null) {
            logger.info("new  startDate{}", new DateTime(startDateMillis).toLocalDate() + "  End date " + new DateTime(payTableToValidate.getEndDateMillis()).toLocalDate());
            Days days = Days.daysBetween(new DateTime(startDateMillis).toLocalDate(), new DateTime(payTableToValidate.getEndDateMillis()).toLocalDate());
            logger.info("difference in days" + days);
            if (days.getDays() != -1) {
                throw new ActionNotPermittedException("The next allowed start day is " + new DateTime(payTableToValidate.getEndDateMillis() + ONE_DAY));
            }
        } else {
            throw new ActionNotPermittedException("Already a payTable is active for the date.Please set end Date on pay table. " + payTableToValidate.getName() + " " + payTableToValidate.getId());
        }
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
    public PayTableResponse updatePayTable(Long countryId, Long payTableId, PayTableUpdateDTO payTableDTO) {
        PayTable payTable = payTableGraphRepository.findOne(payTableId);
        if (!Optional.ofNullable(payTable).isPresent()) {
            throw new DataNotFoundByIdException("Invalid pay grade id");
        }
        // if  name or short name is changes then only we are checking its name existance
        // skipped to current pay table it is checking weather any other payTable has same name or short name in current organization level
        if (!payTableDTO.getName().trim().equalsIgnoreCase(payTable.getName())) {
            Boolean isAlreadyExists = payTableGraphRepository.
                    checkPayTableNameAlreadyExitsByName(countryId, payTableId, "(?i)" + payTableDTO.getName().trim());
            if (isAlreadyExists) {
                throw new DuplicateDataException("Name " + payTableDTO.getName() + " already Exist in country " + countryId);
            }
        }
        PayTableResponse payTableToValidate = payTableGraphRepository.findPayTableByOrganizationLevel(payTableDTO.getLevelId(), -1L);

        validatePayLevel(payTable, payTableToValidate, payTableDTO);

        payTable.setName(payTableDTO.getName().trim());
        payTable.setShortName(payTableDTO.getShortName());
        payTable.setDescription(payTableDTO.getDescription());
        payTable.setPaymentUnit(payTableDTO.getPaymentUnit());
        prepareDates(payTable, payTableDTO);
        save(payTable);
        PayTableResponse payTableResponse = new PayTableResponse(payTable.getName(), payTable.getShortName(), payTable.getDescription(), payTable.getStartDateMillis().getTime(), payTable.getEndDateMillis() != null ? payTable.getEndDateMillis().getTime() : null, payTable.isPublished(), payTable.getPaymentUnit(), payTable.isEditable());
        payTableResponse.setId(payTable.getId());
        return payTableResponse;
    }

    private void validatePayLevel(PayTable payTableFromDatabase, PayTableResponse payTableToValidate, PayTableUpdateDTO payTableDTO) {
        // user is updating any mid table so in this table we wont allow to edit the dates
        if (!payTableToValidate.getId().equals(payTableFromDatabase.getId())) {
            logger.info("new  startDate{}" + payTableFromDatabase.getStartDateMillis() + "   " + payTableDTO.getStartDateMillis()
                    + " " + payTableFromDatabase.getEndDateMillis() + "  " + payTableDTO.getEndDateMillis());
            if (!payTableFromDatabase.getStartDateMillis().equals(payTableDTO.getStartDateMillis()) || !payTableFromDatabase.getEndDateMillis().equals(payTableDTO.getEndDateMillis())) {
                throw new ActionNotPermittedException("Change in dates to this payTable is not allowed: " + payTableDTO.getName().trim());
            }
        } else if (!payTableFromDatabase.getStartDateMillis().equals(payTableDTO.getStartDateMillis())) {
            throw new ActionNotPermittedException("Start Date is not Editable " + payTableToValidate.getName());
        }
    }

    public List<PayGradeResponse> addPayGradeInPayTable(Long payTableId, PayGradeDTO payGradeDTO) {
        PayTable payTable = payTableGraphRepository.findOne(payTableId);
        if (!Optional.ofNullable(payTable).isPresent()) {
            throw new DataNotFoundByIdException("Invalid pay grade id");
        }
        Boolean isAlreadyExists = payTableGraphRepository.checkPayGradeLevelAlreadyExists(payTableId, payGradeDTO.getPayGradeLevel());
        if (isAlreadyExists) {
            throw new DuplicateDataException("Pay grade level " + payGradeDTO.getPayGradeLevel() + " already exists");
        }
        // payTable is not published
        List<PayGradeResponse> payGradesData = new ArrayList<>();
        if (!payTable.isPublished()) {
            payGradesData.add(addPayGradeInCurrentPayTable(payTable, payGradeDTO));
        } else {
            payGradesData = createCopyOfPayTableAndAddPayGrade(payTable, payGradeDTO);
        }
        return payGradesData;

    }

    private List<PayGradeResponse> createCopyOfPayTableAndAddPayGrade(PayTable payTable, PayGradeDTO payGradeDTO) {
        List<PayGradeResponse> payGradeResponses = new ArrayList<>();
        PayTable copiedPayTable = new PayTable();
        BeanUtils.copyProperties(payTable, copiedPayTable);
        copiedPayTable.setId(null);
        copiedPayTable.setPayTable(payTable);
        copiedPayTable.setPayGrades(null);
        payGradeResponses.add(addPayGradeInCurrentPayTable(copiedPayTable, payGradeDTO));
        payTable.setHasTempCopy(true);
        copiedPayTable.setPublished(false);
        save(copiedPayTable);
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
            PayGradeResponse payGradeResponse =
                    new PayGradeResponse(copiedPayTable.getId(), newPayGrade.getPayGradeLevel(), newPayGrade.getId(), getPayGradeResponse(payGradePayGroupAreaRelationShips), newPayGrade.isPublished());
            payGradeResponses.add(payGradeResponse);
        }
        // Adding new Grade in PayTable
        copiedPayTable.setPayGrades(payGradesObjects);
        save(copiedPayTable);

        return payGradeResponses;
    }


    public PayGradeResponse addPayGradeInCurrentPayTable(PayTable payTable, PayGradeDTO payGradeDTO) {
        List<Long> payGroupAreasId = payGradeDTO.getPayGroupAreas().stream().map(PayGroupAreaDTO::getPayGroupAreaId).collect(Collectors.toList());

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

            for (int i = 0; i < payGradeDTO.getPayGroupAreas().size(); i++) {

                if (payGradeDTO.getPayGroupAreas().get(i).getPayGroupAreaId().equals(currentPayGroupArea.getId())) {
                    PayGradePayGroupAreaRelationShip payGradePayGroupAreaRelationShip
                            = new PayGradePayGroupAreaRelationShip(payGrade, currentPayGroupArea, payGradeDTO.getPayGroupAreas().get(i).getPayGroupAreaAmount());
                    payGradePayGroupAreaRelationShips.add(payGradePayGroupAreaRelationShip);
                }

            }
        });
        payTableRelationShipGraphRepository.saveAll(payGradePayGroupAreaRelationShips);
        return new PayGradeResponse(payTable.getId(), payGrade.getPayGradeLevel(), payGrade.getId(), getPayGradeResponse(payGradePayGroupAreaRelationShips), payGrade.isPublished());
    }

    private List<PayGroupAreaDTO> getPayGradeResponse(List<PayGradePayGroupAreaRelationShip> payGradePayGroupAreaRelationShips) {
        List<PayGroupAreaDTO> payGradeMatrices = new ArrayList<>();
        payGradePayGroupAreaRelationShips.forEach(currentPayGroupArea -> {
            PayGroupAreaDTO payGroupAreaDTO =
                    new PayGroupAreaDTO(currentPayGroupArea.getPayGroupArea().getId(), currentPayGroupArea.getPayGroupAreaAmount(), currentPayGroupArea.getId());
            payGradeMatrices.add(payGroupAreaDTO);
        });
        return payGradeMatrices;
    }

    public List<PayGradeResponse> getPayGradesByPayTableId(Long payTableId) {
        PayTable payTable = payTableGraphRepository.findOne(payTableId);
        if (!Optional.ofNullable(payTable).isPresent() || payTable.isDeleted()) {
            throw new DataNotFoundByIdException("Invalid pay table id");
        }
        return payTableGraphRepository.getPayGradesByPayTableId(payTableId);
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
            payGradeGraphRepository.removeAllPayGroupAreasFromPayGradeAndDeletePayGrade(payGradeId);
        return found;
    }

    public List<PayGradeResponse> removePayTable(Long payTableId) {
        PayTable payTable = payTableGraphRepository.findOne(payTableId);
        if (!Optional.ofNullable(payTable).isPresent() || payTable.isDeleted()) {
            throw new DataNotFoundByIdException("Invalid pay table id");
        }
        if (payTable.isPublished()) {
            throw new ActionNotPermittedException("PayTable is already published so can't remove");
        }
        Long parentPayTableId = payTableGraphRepository.getParentPayTableByPayTableId(payTableId);
        List<PayGradeResponse> payGrades = new ArrayList<>();
        if (Optional.ofNullable(parentPayTableId).isPresent()) {
            payGrades = payTableGraphRepository.getPayGradesByPayTableId(parentPayTableId);
        }
        payTable.setPayTable(null);
        payTable.setDeleted(true);
        save(payTable);

        return payGrades;
    }

    private List<PayGradeResponse> updatePayGradeInUnpublishedPayTable(PayTable payTable, PayGradeDTO payGradeDTO, PayGrade payGrade) {
        List<PayGradeResponse> payGradeResponses = new ArrayList<>();
        Set<Long> payGroupAreaIds = payGradeDTO.getPayGroupAreas().stream().map(PayGroupAreaDTO::getPayGroupAreaId).collect(Collectors.toSet());
        List<PayGroupArea> payGroupAreas = payGroupAreaGraphRepository.findAllById(payGroupAreaIds);
        // removing all previous Ids
        payGradeGraphRepository.removeAllPayGroupAreasFromPayGrade(payGrade.getId());
        List<PayGradePayGroupAreaRelationShip> payGradePayGroupAreaRelationShips = new ArrayList<>();
        for (PayGroupAreaDTO currentPayGroup : payGradeDTO.getPayGroupAreas()) {
            PayGroupArea currentPayGroupArea = payGroupAreas.stream().filter(payGroupArea -> payGroupArea.getId().equals(currentPayGroup.getPayGroupAreaId())).findFirst().get();
            PayGradePayGroupAreaRelationShip payGradePayGroupAreaRelationShip
                    = new PayGradePayGroupAreaRelationShip(payGrade, currentPayGroupArea, currentPayGroup.getPayGroupAreaAmount());
            payTableRelationShipGraphRepository.save(payGradePayGroupAreaRelationShip);
            payGradePayGroupAreaRelationShips.add(payGradePayGroupAreaRelationShip);
            currentPayGroup.setId(payGradePayGroupAreaRelationShip.getId());
        }
        PayGradeResponse payGradeResponse =
                new PayGradeResponse(payTable.getId(), payGrade.getPayGradeLevel(), payGrade.getId(), getPayGradeResponse(payGradePayGroupAreaRelationShips), payGrade.isPublished());
        payGradeResponses.add(payGradeResponse);
        return payGradeResponses;
    }

    public List<PayGradeResponse> updatePayGradeInPayTable(Long payTableId, Long payGradeId, PayGradeDTO payGradeDTO) {
        PayTable payTable = payTableGraphRepository.findOne(payTableId);
        if (!Optional.ofNullable(payTable).isPresent() || payTable.isDeleted()) {
            throw new DataNotFoundByIdException("Invalid pay table id");
        }
        PayGrade payGrade = null;
        for (PayGrade currentPayGrade : payTable.getPayGrades()) {
            if (currentPayGrade.getId().equals(payGradeId)) {
                payGrade = currentPayGrade;
                break;
            }
        }
        if (!Optional.ofNullable(payGrade).isPresent()) {
            throw new DataNotFoundByIdException("Invalid pay grade id" + payGradeId);
        }
        List<PayGradeResponse> payGradeResponses = new ArrayList<>();
        // user is updating in a unpublished payTable
        payGradeResponses = (!payTable.isPublished()) ? updatePayGradeInUnpublishedPayTable(payTable, payGradeDTO, payGrade) :
                updatePayGradeInPublishedPayTable(payTable, payGradeDTO, payGradeId);

        return payGradeResponses;
    }

    private void copyBasicDetailOfPayTable(PayTable payTable, PayTable newPayTable) {
        BeanUtils.copyProperties(payTable, newPayTable);
        newPayTable.setId(null);
        newPayTable.setPayTable(payTable);
        newPayTable.setPayGrades(null);
        newPayTable.setPublished(false);
        payTable.setHasTempCopy(true);
        newPayTable.setHasTempCopy(false);
        save(newPayTable);

    }

    private List<PayGradeResponse> updatePayGradeInPublishedPayTable(PayTable payTable, PayGradeDTO payGradeDTO, Long payGradeId) {
        List<PayGradeResponse> payGradeResponses = new ArrayList<>();
        List<PayGrade> payGradesObjects = new ArrayList<>();
        // creating a new PayTable
        PayTable payTableByMapper = new PayTable();
        BeanUtils.copyProperties(payTable, payTableByMapper);
        payTableByMapper.setId(null);
        payTableByMapper.setPayTable(payTable);
        payTableByMapper.setPayGrades(null);
        payTableByMapper.setPublished(false);
        payTable.setHasTempCopy(true);
        payTable.setEditable(false);
        payTableByMapper.setHasTempCopy(false);
        save(payTableByMapper);
        for (PayGrade currentPayGrade : payTable.getPayGrades()) {
            PayGrade newPayGrade = new PayGrade(currentPayGrade.getPayGradeLevel(), false);
            List<PayGradePayGroupAreaRelationShip> payGradePayGroupAreaRelationShips = new ArrayList<>();

            if (payGradeDTO.getPayGradeId().equals(currentPayGrade.getId())) {
                // user has changed the value in  this pay Grade area of payTable
                List<Long> payGroupAreasId = payGradeDTO.getPayGroupAreas().stream().map(PayGroupAreaDTO::getPayGroupAreaId).collect(Collectors.toList());
                List<PayGroupArea> payGroupAreas = payGroupAreaGraphRepository.findAllById(payGroupAreasId);

                for (PayGroupAreaDTO currentPayGroupArea : payGradeDTO.getPayGroupAreas()) {
                    PayGroupArea payGroupArea = payGroupAreas.stream().filter(payGroupArea1 -> payGroupArea1.getId().equals(currentPayGroupArea.getPayGroupAreaId())).findFirst().get();
                    PayGradePayGroupAreaRelationShip payGradePayGroupAreaRelationShip
                            = new PayGradePayGroupAreaRelationShip(newPayGrade, payGroupArea, currentPayGroupArea.getPayGroupAreaAmount());
                    payGradePayGroupAreaRelationShips.add(payGradePayGroupAreaRelationShip);

                }
            } else {
                HashSet<PayTableMatrixQueryResult> payTableMatrix = payGradeGraphRepository.getPayGradeMatrixByPayGradeId(currentPayGrade.getId());
                payTableMatrix.forEach(currentObj -> {
                    PayGradePayGroupAreaRelationShip payGradePayGroupAreaRelationShip
                            = new PayGradePayGroupAreaRelationShip(newPayGrade, new PayGroupArea(currentObj.getPayGroupAreaId(), currentObj.getPayGroupAreaName()), currentObj.getPayGroupAreaAmount());
                    payGradePayGroupAreaRelationShips.add(payGradePayGroupAreaRelationShip);
                });
            }
            payTableRelationShipGraphRepository.saveAll(payGradePayGroupAreaRelationShips);
            payGradesObjects.add(newPayGrade);
            PayGradeResponse payGradeResponse =
                    new PayGradeResponse(payTableByMapper.getId(), newPayGrade.getPayGradeLevel(), newPayGrade.getId(), getPayGradeResponse(payGradePayGroupAreaRelationShips), newPayGrade.isPublished());
            payGradeResponses.add(payGradeResponse);

        }
        payTableByMapper.setPayGrades(payGradesObjects);
        save(payTableByMapper);
        return payGradeResponses;
    }

    public List<PayTable> publishPayTable(Long payTableId, Long publishedDateMillis) {
        PayTable payTable = payTableGraphRepository.findOne(payTableId);
        if (!Optional.ofNullable(payTable).isPresent() || payTable.isDeleted()) {
            throw new DataNotFoundByIdException("Invalid pay table id");
        }
        if (payTable.isPublished()) {
            throw new ActionNotPermittedException("PayTable is already published");
        }
        List<PayTable> response = new ArrayList<>();

        PayTable parentPayTable = payTableGraphRepository.getPermanentPayTableByPayTableId(payTableId);
        logger.debug(new DateTime(payTable.getStartDateMillis()).toLocalDate() + "----" + (new DateTime(publishedDateMillis).toLocalDate()));
        if (Optional.ofNullable(parentPayTable).isPresent()) {
            payTableGraphRepository.changeStateOfRelationShip(parentPayTable.getId(), publishedDateMillis - ONE_DAY);
            parentPayTable.setEndDateMillis(new Date(publishedDateMillis - ONE_DAY));
            parentPayTable.setHasTempCopy(false);
            parentPayTable.setPayTable(null);
            response.add(parentPayTable);

        } else if (!new DateTime(payTable.getStartDateMillis()).toLocalDate().equals(new DateTime(publishedDateMillis).toLocalDate())) {
            throw new ActionNotPermittedException("Can be only published on same start date");
        }
        payTable.setPayTable(null);
        payTable.setPublished(true);
        payTable.setStartDateMillis(new Date(publishedDateMillis));
        payTable.getPayGrades().forEach(currentPayGrade -> currentPayGrade.setPublished(true));
        save(payTable);
        response.add(payTable);

        return response;
    }
}
