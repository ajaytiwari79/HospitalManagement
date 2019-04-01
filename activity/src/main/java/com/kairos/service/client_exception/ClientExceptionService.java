package com.kairos.service.client_exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.dto.user.client.Client;
import com.kairos.dto.user.client.ClientExceptionCountWrapper;
import com.kairos.dto.user.client.ClientTemporaryAddress;
import com.kairos.persistence.model.client_aggregator.ClientAggregator;
import com.kairos.persistence.model.client_aggregator.FourWeekFrequency;
import com.kairos.persistence.model.client_exception.ClientException;
import com.kairos.dto.activity.client_exception.ClientExceptionCount;
import com.kairos.persistence.model.client_exception.ClientExceptionDTO;
import com.kairos.persistence.model.client_exception.ClientExceptionType;
import com.kairos.persistence.model.task.Task;
import com.kairos.persistence.model.task_type.TaskType;
import com.kairos.persistence.model.task_type.TaskTypeDefination;
import com.kairos.persistence.repository.client_exception.ClientExceptionMongoRepository;
import com.kairos.persistence.repository.client_exception.ClientExceptionTypeMongoRepository;
import com.kairos.persistence.repository.common.MongoSequenceRepository;
import com.kairos.persistence.repository.task_type.TaskDemandMongoRepository;
import com.kairos.persistence.repository.task_type.TaskMongoRepository;
import com.kairos.persistence.repository.task_type.TaskTypeMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.aggregator.AggregatorService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.fls_visitour.schedule.SchedulerImpl;
import com.kairos.service.fls_visitour.schedule.TaskConverterService;
import com.kairos.service.planner.PlannerService;
import com.kairos.service.planner.TaskExceptionService;
import com.kairos.service.task_type.TaskService;
import com.kairos.rule_validator.task.TaskLocationSpecification;
import com.kairos.rule_validator.TaskSpecification;
import com.kairos.commons.utils.DateUtils;
import com.kairos.utils.functional_interface.PerformCalculation;
import com.kairos.wrapper.task.TaskGanttDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.persistence.model.constants.ClientExceptionConstant.*;
import static com.kairos.persistence.model.constants.TaskConstants.*;
import static com.kairos.commons.utils.DateUtils.MONGODB_QUERY_DATE_FORMAT;
import static com.kairos.commons.utils.DateUtils.ONLY_DATE;

/**
 * Created by oodles on 7/2/17.
 */
@Transactional
@Service
public class ClientExceptionService extends MongoBaseService {
    public static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private TaskMongoRepository taskMongoRepository;
    @Inject
    private TaskDemandMongoRepository taskDemandMongoRepository;

    @Inject
    private ClientExceptionTypeMongoRepository clientExceptionTypeMongoRepository;
    @Inject
    private SchedulerImpl scheduler;
    @Inject
    private ClientExceptionMongoRepository clientExceptionMongoRepository;
    @Inject
    private TaskService taskService;
    @Inject
    private TaskConverterService taskConverterService;
    @Inject
    MongoSequenceRepository mongoSequenceRepository;

    @Inject
    private PlannerService plannerService;
    @Inject
    UserIntegrationService userIntegrationService;
    @Inject
    private TaskExceptionService taskExceptionService;
    @Inject
    private TaskTypeMongoRepository taskTypeMongoRepository;
    @Inject
    AggregatorService aggregatorService;
    @Inject
    ExceptionService exceptionService;




    public static Date stringToDate(String stringDate) {
        DateFormat dateFormat = new SimpleDateFormat(ISO_FORMAT);
        try {
            return dateFormat.parse(stringDate);
        } catch (Exception ex) {
            return null;
        }
    }

    public Map<String, Object> createClientException(ClientExceptionDTO clientExceptionDto, long unitId, long clientId) throws ParseException {


        logger.info("client exception dto :: " + clientExceptionDto.toString());

        // Client client = clientGraphRepository.findById(clientId, 0);
        Client client = userIntegrationService.getClient(clientId);
        if (client == null) {
            exceptionService.internalError("error.client.notfound");
        }
        Optional<ClientExceptionType> clientExceptionTypeOptional = clientExceptionTypeMongoRepository.findById(new BigInteger(clientExceptionDto.getExceptionTypeId()));
        if (!clientExceptionTypeOptional.isPresent()) {
            exceptionService.internalError("error.exception.type.null");
        }
        ClientExceptionType clientExceptionType=clientExceptionTypeOptional.get();
        ClientException clientException;
        List<Task> tasksToReturn;
        List<ClientException> clientExceptions = new ArrayList<>();
        LocalDateTime fromTime = null;
        LocalDateTime toTime = null;
        LocalDateTime timeFrom;
        LocalDateTime timeTo;
        Date dateFrom;
        Date dateTo;
        Map<String, Object> map = new HashMap<>();
        List<Task> allUnhandledTasks = new ArrayList<>();
        List<ClientException> sickExceptions = null;
        switch (clientExceptionType.getValue()) {
            case SICK: {
                sickExceptions = new ArrayList<>();
                Optional<String> date = clientExceptionDto.getSelectedDates().stream().findFirst();
                if (!date.isPresent()) {
                    exceptionService.internalError("error.exception.date.notfound");
                }
                DateTime initialDate = new DateTime(date.get());
                validateSickException(initialDate, clientExceptionDto.getDaysToReview(), clientId);
                int exceptionCount = 0;
                while (exceptionCount < clientExceptionDto.getDaysToReview()) {
                    DateTime startOfDay = initialDate.withTimeAtStartOfDay();
                    DateTime endOfDay = initialDate.withTime(DAY_END_HOUR, DAY_END_MINUTE, DAY_END_SECOND, DAY_END_NANO);
                    clientException = clientExceptionMongoRepository.getSickExceptionForDate(clientId, startOfDay.toDate());
                    if (clientException == null) {
                        clientException = getClientExceptionObj(clientExceptionDto, startOfDay.toDate(), endOfDay.toDate(), clientExceptionType, clientId, unitId);
                        save(clientException);
                        sickExceptions.add(clientException);
                    }
                    clientExceptions.add(clientException);
                    initialDate = initialDate.plusDays(1);
                    exceptionCount++;
                    tasksToReturn = updateTasksInKairosAndVisitour(clientExceptionDto, unitId, clientId, startOfDay.toDate(), endOfDay.toDate(), clientException, false, null);
                    allUnhandledTasks.addAll(tasksToReturn);
                }
                break;
            }
            case IN_HOSPITAL: {
                if (clientExceptionDto.getFromTime() != null && clientExceptionDto.getToTime() != null) {
                    fromTime = LocalDateTime.ofInstant(DateUtils.convertToOnlyDate(clientExceptionDto.getFromTime(), MONGODB_QUERY_DATE_FORMAT).toInstant(), ZoneId.systemDefault());
                    toTime = LocalDateTime.ofInstant(DateUtils.convertToOnlyDate(clientExceptionDto.getToTime(), MONGODB_QUERY_DATE_FORMAT).toInstant(), ZoneId.systemDefault());
                }
                for (String selectedDate : clientExceptionDto.getSelectedDates()) {
                    if (fromTime != null && toTime != null) {
                        timeFrom = LocalDate.parse(selectedDate).atTime(fromTime.getHour(), fromTime.getMinute());
                        timeTo = LocalDate.parse(selectedDate).atTime(toTime.getHour(), toTime.getMinute());
                        dateFrom = Date.from(timeFrom.atZone(ZoneId.systemDefault()).toInstant());
                        dateTo = Date.from(timeTo.atZone(ZoneId.systemDefault()).toInstant());
                    } else {
                        dateFrom = DateUtils.convertToOnlyDate(selectedDate, ONLY_DATE);
                        dateTo = DateUtils.convertToOnlyDate(selectedDate, ONLY_DATE);
                        dateTo.setHours(DAY_END_HOUR);
                        dateTo.setMinutes(DAY_END_MINUTE);
                    }
                    validateClientException(Arrays.asList(clientId), dateFrom, dateTo, clientExceptionType.getId());
                    clientException = getClientExceptionObj(clientExceptionDto, dateFrom, dateTo, clientExceptionType, clientId, unitId);
                    save(clientException);
                    clientExceptions.add(clientException);
                    tasksToReturn = updateTasksInKairosAndVisitour(clientExceptionDto, unitId, clientId, dateFrom, dateTo, clientException, false, null);
                    allUnhandledTasks.addAll(tasksToReturn);
                }
                break;

            }
            case CHANGE_LOCATION: {

                ClientTemporaryAddress clientTemporaryAddress = userIntegrationService.updateClientTemporaryAddress(clientExceptionDto, unitId, clientId);
                return createChangeLocationException(unitId, clientId, clientExceptionDto, clientExceptionType, clientTemporaryAddress);
            }
            case DO_NOT_DISTURB: {
                if (clientExceptionDto.getFromTime() != null && clientExceptionDto.getToTime() != null) {
                    fromTime = LocalDateTime.ofInstant(DateUtils.convertToOnlyDate(clientExceptionDto.getFromTime(), MONGODB_QUERY_DATE_FORMAT).toInstant(), ZoneId.systemDefault());
                    toTime = LocalDateTime.ofInstant(DateUtils.convertToOnlyDate(clientExceptionDto.getToTime(), MONGODB_QUERY_DATE_FORMAT).toInstant(), ZoneId.systemDefault());
                }
                for (String selectedDate : clientExceptionDto.getSelectedDates()) {

                    if (fromTime != null && toTime != null) {
                        timeFrom = LocalDate.parse(selectedDate).atTime(fromTime.getHour(), fromTime.getMinute());
                        timeTo = LocalDate.parse(selectedDate).atTime(toTime.getHour(), toTime.getMinute());
                        dateFrom = Date.from(timeFrom.atZone(ZoneId.systemDefault()).toInstant());
                        dateTo = Date.from(timeTo.atZone(ZoneId.systemDefault()).toInstant());
                    } else {
                        dateFrom = DateUtils.convertToOnlyDate(selectedDate, ONLY_DATE);
                        dateTo = DateUtils.convertToOnlyDate(selectedDate, ONLY_DATE);
                        dateTo.setHours(DAY_END_HOUR);
                        dateTo.setMinutes(DAY_END_HOUR);
                    }
                    validateClientException(Arrays.asList(clientId),dateFrom,dateTo,clientExceptionType.getId());
                    clientException = getClientExceptionObj(clientExceptionDto, dateFrom, dateTo, clientExceptionType, clientId, unitId);
                    save(clientException);
                    clientExceptions.add(clientException);
                    tasksToReturn = updateTasksInKairosAndVisitour(clientExceptionDto, unitId, clientId, dateFrom, dateTo, clientException, false, null);
                    allUnhandledTasks.addAll(tasksToReturn);
                }
                break;
            }
            default:
                exceptionService.internalError("error.exception.type.notexist");
        }
        ClientAggregator clientAggregator = taskExceptionService.updateTaskCountInAggregator(allUnhandledTasks, unitId, clientId, false);
        PerformCalculation performCalculation = (n) -> n + 1;
        if (sickExceptions != null) {
            updateClientAggregator(sickExceptions, performCalculation, clientAggregator);
        } else {
            updateClientAggregator(clientExceptions, performCalculation, clientAggregator);
        }
        map.put("exceptionList", clientExceptions);
        map.put("taskList", plannerService.customizeTaskData(allUnhandledTasks));

        return map;
    }

    private Map<String,Object> createChangeLocationException(Long unitId, Long clientId, ClientExceptionDTO clientExceptionDTO,
                                                             ClientExceptionType clientExceptionType, ClientTemporaryAddress clientTemporaryAddress) throws ParseException {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(ONLY_DATE);
        List<String> exceptionDates = clientExceptionDTO.getSelectedDates();
        Collections.sort(exceptionDates, Comparator.comparing(s -> LocalDate.parse(s, dateTimeFormatter)));
        checkExceptionDateValidation(clientId, exceptionDates, clientExceptionDTO);
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        if (clientExceptionDTO.getFromTime() != null && clientExceptionDTO.getToTime() != null) {
            startTime = LocalDateTime.ofInstant(DateUtils.convertToOnlyDate(clientExceptionDTO.getFromTime(), MONGODB_QUERY_DATE_FORMAT).toInstant(), ZoneId.systemDefault());
            endTime = LocalDateTime.ofInstant(DateUtils.convertToOnlyDate(clientExceptionDTO.getToTime(), MONGODB_QUERY_DATE_FORMAT).toInstant(), ZoneId.systemDefault());
        }
        List<ClientException> clientExceptions = new ArrayList<>(exceptionDates.size());
        for (String exceptionDate : exceptionDates) {
            Date dateFrom;
            Date dateTo;
            if (StringUtils.isBlank(clientExceptionDTO.getFromTime()) && StringUtils.isBlank(clientExceptionDTO.getToTime())) {
                dateFrom = DateUtils.convertToOnlyDate(exceptionDate, ONLY_DATE);
                dateTo = DateUtils.convertToOnlyDate(exceptionDate, ONLY_DATE);
                dateTo.setHours(DAY_END_HOUR);
                dateTo.setMinutes(DAY_END_MINUTE);
            } else {
                LocalDateTime timeFrom = LocalDate.parse(exceptionDate).atTime(startTime.getHour(), startTime.getMinute());
                LocalDateTime timeTo = LocalDate.parse(exceptionDate).atTime(endTime.getHour(), endTime.getMinute());
                dateFrom = Date.from(timeFrom.atZone(ZoneId.systemDefault()).toInstant());
                dateTo = Date.from(timeTo.atZone(ZoneId.systemDefault()).toInstant());
            }
            ClientException clientException = getClientExceptionObj(clientExceptionDTO, dateFrom, dateTo, clientExceptionType, clientId, unitId);
            clientException.setHouseHoldMembers(clientExceptionDTO.getHouseHoldMembers());
            clientException.setTemporaryAddressId(clientTemporaryAddress.getId());
            clientExceptions.add(clientException);
        }
        ClientException exceptionAtFirstIndex = clientExceptions.get(0);
        ClientException exceptionAtLastIndex = clientExceptions.get(clientExceptions.size() - 1);
        clientExceptions = copyExceptionsForHouseHoldMembers(clientExceptions, clientExceptionDTO,clientId);
        save(clientExceptions);
        List<Long> houseHoldMembers = new ArrayList<>(clientExceptionDTO.getHouseHoldMembers());
        houseHoldMembers.add(clientId);
        List<Task> tasksUnderException = taskMongoRepository.getTasksBetweenExceptionDates(unitId, houseHoldMembers, exceptionAtFirstIndex.getFromTime(), exceptionAtLastIndex.getToTime());
        clientExceptions.forEach(clientException -> {
            List<Task> taskUnderThisException = tasksUnderException.stream().filter(task -> task.getCitizenId() == clientException.getClientId() && task.getTimeFrom().
                    compareTo(clientException.getFromTime()) >= 0 && task.getTimeTo().compareTo(clientException.getToTime()) <= 0).collect(Collectors.toList());
            taskUnderThisException.stream().forEach(taskUnderException -> {
                updateTaskInfo(clientExceptionDTO, taskUnderException);
                updatePriorityAndDuration(clientExceptionDTO, taskUnderException);
                updateTaskAddress(taskUnderException, clientTemporaryAddress);
            });
        });
        if(!tasksUnderException.isEmpty()){
            save(tasksUnderException);
        }
        updateAggregatorForChangeLocationException(tasksUnderException,clientExceptions,houseHoldMembers,unitId);
        Map<String,Object> response = new HashMap<>();
        response.put("exceptionList", clientExceptions.stream().filter(clientException->clientException.getClientId() == clientId).collect(Collectors.toList()));
        response.put("taskList", plannerService.customizeTaskData(tasksUnderException.stream().filter
                (task->task.getCitizenId() == clientId).collect(Collectors.toList())));
        response.put("tempAddress",clientTemporaryAddress);
        return response;

    }

    private List<ClientException> copyExceptionsForHouseHoldMembers(List<ClientException> clientExceptions, ClientExceptionDTO clientExceptionDTO,
                                                                    Long clientId) {
        List<ClientException> allExceptions = new ArrayList<>(clientExceptions);
        List<Long> houseHoldMembers = new ArrayList<>();
        houseHoldMembers.add(clientId);
        for (Long houseHoldMemberId : clientExceptionDTO.getHouseHoldMembers()) {
            for (ClientException clientException : clientExceptions) {
                ClientException exceptionForHouseHold = new ClientException();
                BeanUtils.copyProperties(clientException, exceptionForHouseHold);
                exceptionForHouseHold.setHouseHoldMembers(houseHoldMembers);
                exceptionForHouseHold.setClientId(houseHoldMemberId);
                allExceptions.add(exceptionForHouseHold);
            }
        }
        return allExceptions;
    }


    private void checkExceptionDateValidation(Long clientId, List<String> exceptionDates, ClientExceptionDTO clientExceptionDTO) throws ParseException {

        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        if (clientExceptionDTO.getFromTime() != null && clientExceptionDTO.getToTime() != null) {
            startTime = LocalDateTime.ofInstant(DateUtils.convertToOnlyDate(clientExceptionDTO.getFromTime(), MONGODB_QUERY_DATE_FORMAT).toInstant(), ZoneId.systemDefault());
            endTime = LocalDateTime.ofInstant(DateUtils.convertToOnlyDate(clientExceptionDTO.getToTime(), MONGODB_QUERY_DATE_FORMAT).toInstant(), ZoneId.systemDefault());
        }
        String startDateFromList = exceptionDates.get(0);
        String lastDateFromList = exceptionDates.get(exceptionDates.size() - 1);
        Date dateFrom;
        Date dateTo;
        if (startTime != null && endTime != null) {
            LocalDateTime timeFrom = LocalDate.parse(startDateFromList).atTime(startTime.getHour(), startTime.getMinute());
            LocalDateTime timeTo = LocalDate.parse(lastDateFromList).atTime(endTime.getHour(), endTime.getMinute());
            dateFrom = Date.from(timeFrom.atZone(ZoneId.systemDefault()).toInstant());
            dateTo = Date.from(timeTo.atZone(ZoneId.systemDefault()).toInstant());
        } else {
            dateFrom = DateUtils.convertToOnlyDate(startDateFromList, ONLY_DATE);
            dateTo = DateUtils.convertToOnlyDate(lastDateFromList, ONLY_DATE);
            dateTo.setHours(DAY_END_HOUR);
            dateTo.setMinutes(DAY_END_HOUR);
        }

        List<Long> houseHoldMembers = new ArrayList<>(clientExceptionDTO.getHouseHoldMembers());
        houseHoldMembers.add(clientId);

        validateClientException(houseHoldMembers, dateFrom, dateTo, new BigInteger(clientExceptionDTO.getExceptionTypeId()));
    }

    private void updateAggregatorForChangeLocationException(List<Task> tasks,List<ClientException> clientExceptions,List<Long> citizensId,Long unitId){
        PerformCalculation performCalculation = (n) -> n + 1;
        citizensId.forEach(citizenId->{
            List<Task> taskByCitizen = tasks.stream().filter(task -> task.getCitizenId() == citizenId).collect(Collectors.toList());
            List<ClientException> exceptionsByCitizen = clientExceptions.stream().filter(clientException-> clientException.getClientId() == citizenId)
                    .collect(Collectors.toList());
            ClientAggregator clientAggregator = taskExceptionService.updateTaskCountInAggregator(taskByCitizen, unitId, citizenId, false);
            updateClientAggregator(exceptionsByCitizen, performCalculation, clientAggregator);
        });
    }


    private List<Task> updateTasksInKairosAndVisitour(ClientExceptionDTO clientExceptionDTO, long unitId, long clientId,
                                                      Date dateFrom, Date dateTo, ClientException clientException,
                                                      boolean exceptionHandled, ClientTemporaryAddress clientTemporaryAddress) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Task> unhandledTasks = taskMongoRepository.getTasksBetweenExceptionDates(unitId, clientId, dateFrom, dateTo);
        unhandledTasks.forEach(unhandledTask -> {
            if (!unhandledTask.isSingleTask() && unhandledTask.getActualPlanningTask() == null) {
                taskService.savePreplanningStateOfTask(unhandledTask);
            }
            updateTaskInfo(clientExceptionDTO, unhandledTask);
            updatePriorityAndDuration(clientExceptionDTO, unhandledTask);
            if (!exceptionHandled)
                persistExceptionInTask(unhandledTask, clientException, objectMapper);

            boolean hasPrimaryAddress = false;
            if (Optional.ofNullable(unhandledTask.getTaskTypeId()).isPresent()) {
                TaskType taskType = taskTypeMongoRepository.findById(unhandledTask.getTaskTypeId()).get();
                TaskTypeDefination taskTypeDefination = taskType.getDefinations();
                if (Optional.ofNullable(taskTypeDefination).isPresent())
                    hasPrimaryAddress = taskTypeDefination.isHasPrimaryAddress();

            }

            if (exceptionHandled && clientTemporaryAddress != null && !hasPrimaryAddress) {
                updateTaskAddress(unhandledTask, clientTemporaryAddress);
            }
        });
        Map<String, String> flsCredentials = userIntegrationService.getFLS_Credentials(unitId);
        taskConverterService.createFlsCallFromTasks(unhandledTasks, flsCredentials);
        return unhandledTasks;
    }

    /*
    rule_validator to check presence of client required for task
    if task can be deliverd without presence of client, then task will not consider unhandled task ( unhandled task which
    effects by exception)
     //TODO we will use this method later after implement merge task functionality because now merge task can be create without task type
     */
    private boolean clientPresenceRequiredForTask(TaskType taskType,Task task){
        TaskSpecification<Task> taskLocationSpecification = new TaskLocationSpecification(taskType.isClientPresenceRequired());
        return taskLocationSpecification.isSatisfied(task);
    }

    private void updateTaskInfo(ClientExceptionDTO clientExceptionDTO, Task task) {

        if (clientExceptionDTO.getInfo1() != null) {
            task.setInfo1((task.getInfo1() == null ? clientExceptionDTO.getInfo1() : task.getInfo1() + clientExceptionDTO.getInfo1()));
        }

        if (clientExceptionDTO.getInfo2() != null) {
            task.setInfo2((task.getInfo2() == null ? clientExceptionDTO.getInfo2() : task.getInfo2() + clientExceptionDTO.getInfo2()));
        }
    }

    private void persistExceptionInTask(Task task, ClientException clientException, ObjectMapper objectMapper) {
        Task.ClientException taskException = objectMapper.convertValue(clientException, Task.ClientException.class);
        List<Task.ClientException> clientExceptionList = task.getClientExceptions();
        Iterator<Task.ClientException> clientExceptionIterator = clientExceptionList.iterator();
        while (clientExceptionIterator.hasNext()) {
            Task.ClientException clientExceptionToDelete = clientExceptionIterator.next();
            if (clientExceptionToDelete.getId().equals(clientException.getId())) {
                clientExceptionIterator.remove();
                break;
            }
        }
        clientExceptionList.add(taskException);
        task.setClientExceptions(clientExceptionList);
    }

    private void updatePriorityAndDuration(ClientExceptionDTO clientExceptionDTO, Task actualTask) {

        if (clientExceptionDTO.isUpdateTaskDuration()) {
            if (clientExceptionDTO.getNewTaskDuration() < 1) {
                exceptionService.internalError("error.task.duration");
            }
            Date timeTo = org.apache.commons.lang.time.DateUtils.addMinutes(actualTask.getTimeFrom(), clientExceptionDTO.getNewTaskDuration());
            actualTask.setTimeTo(timeTo);
            actualTask.setDuration(clientExceptionDTO.getNewTaskDuration());
        }
        if (clientExceptionDTO.isUpdateTaskPriority()) {
            actualTask.setPriority(clientExceptionDTO.getNewTaskPriority());
        }
    }


    private ClientException getClientExceptionObj(ClientExceptionDTO clientExceptionDTO, Date fromTime, Date toTime,
                                                  ClientExceptionType clientExceptionType, long clientId, long unitId) {
        ObjectMapper objectMapper = new ObjectMapper();
        ClientException clientException = objectMapper.convertValue(clientExceptionDTO, ClientException.class);

        clientException.setFromTime(fromTime);
        clientException.setToTime(toTime);
        clientException.setName(clientExceptionType.getName());
        clientException.setValue(clientExceptionType.getValue());
        clientException.setExceptionTypeId(clientExceptionType.getId());
        clientException.setClientId(clientId);
        clientException.setUnitId(unitId);
        if (clientExceptionDTO.getFromTime() == null && clientExceptionDTO.getToTime() == null) {
            clientException.setFullDay(true);
        }
        return clientException;
    }

    private void updateTaskAddress(Task actualTask, ClientTemporaryAddress clientTemporaryAddress) {
        actualTask.getAddress().setCity(clientTemporaryAddress.getCity());
        actualTask.getAddress().setCountry("DK");
        actualTask.getAddress().setZip(clientTemporaryAddress.getZipCode().getZipCode());
        actualTask.getAddress().setLatitude(String.valueOf(clientTemporaryAddress.getLatitude()));
        actualTask.getAddress().setLongitude(String.valueOf(clientTemporaryAddress.getLongitude()));
        actualTask.getAddress().setStreet(clientTemporaryAddress.getStreet1());
        actualTask.getAddress().setHouseNumber(clientTemporaryAddress.getHouseNumber());
        actualTask.setLocationChanged(true);

    }

    public ClientExceptionType createTaskExceptionType(ClientExceptionType clientExceptionType) {
        return save(clientExceptionType);

    }

    public ClientExceptionType updateTaskExceptionType(ClientExceptionType clientExceptionType) {
        Optional<ClientExceptionType> clientExceptionTypeOptional = clientExceptionTypeMongoRepository.findById(clientExceptionType.getId());
        if (!clientExceptionTypeOptional.isPresent()) {
            return null;
        }
        ClientExceptionType type=clientExceptionTypeOptional.get();
        type.setName(clientExceptionType.getName());
        type.setDescription(clientExceptionType.getDescription());
        type.setValue(clientExceptionType.getValue());
        return save(type);
    }


    public List<ClientExceptionType> getTaskExceptionType() {
        logger.info("Service Hit");
        List<ClientExceptionType> data = clientExceptionTypeMongoRepository.findAll();
        logger.info("Getting List size: " + data.size());
        return data;

    }


    public boolean deleteTaskExceptionType(String id) {
        Optional<ClientExceptionType> clientExceptionTypeOptional = clientExceptionTypeMongoRepository.findById(new BigInteger(id));
        if (!clientExceptionTypeOptional.isPresent()) {
            return false;
        }
        clientExceptionTypeOptional.get().setEnabled(false);
        save(clientExceptionTypeOptional.get());
        return true;
    }


    public List<TaskGanttDTO> deleteClientException(BigInteger exceptionId, long unitId) {
        Optional<ClientException> clientExceptionOptional = clientExceptionMongoRepository.findById(exceptionId);
        if (!clientExceptionOptional.isPresent()) {
            exceptionService.internalError("error.exception.notfound");
        }
        ClientException  clientException=clientExceptionOptional.get();
        List<Task> tasksToHandle = taskMongoRepository.getTaskByException(clientException.getClientId(), unitId, exceptionId);
        if (!tasksToHandle.isEmpty()) {
            deleteExceptionFromTask(tasksToHandle, clientException.getId());
            save(tasksToHandle);
        }
        ClientAggregator clientAggregator = taskExceptionService.updateTaskCountInAggregator(tasksToHandle, unitId, clientException.getClientId(), true);
        if (clientAggregator != null) {
            PerformCalculation performCalculation = (n) -> n - 1;
            updateClientAggregator(clientException, performCalculation, clientAggregator);
            plannerService.sendAggregateDataToClient(clientAggregator, unitId);
        }
        clientExceptionMongoRepository.delete(clientException);
        return taskService.customizeTaskData(tasksToHandle);

    }

    private void deleteExceptionFromTasK(List<Task> tasksToHandle, BigInteger clientExceptionId) {

        tasksToHandle.forEach(task -> {
            Iterator<Task.ClientException> clientExceptionIterator = task.getClientExceptions().iterator();
            while (clientExceptionIterator.hasNext()) {
                Task.ClientException exceptionToDelete = clientExceptionIterator.next();
                if (exceptionToDelete.getId().equals(clientExceptionId)) {
                    clientExceptionIterator.remove();
                    break;
                }
            }
        });
    }


    public HashMap<String, Object> bulkDeleteClientException(ClientExceptionDTO exceptionDTO, long unitId) throws ParseException {

        List<Date> exceptionDatesToDelete = null;
        List<Task> allTask = new ArrayList<>();
        List<ClientException> allExceptions = new ArrayList<>();
        List<BigInteger> exceptionIdsToDelete = new ArrayList<>();
        for (String dateToParse : exceptionDTO.getSelectedDates()) {
            if (exceptionDatesToDelete == null) {
                exceptionDatesToDelete = new ArrayList<>();
            }
            DateTime startOfDay = new DateTime(DateUtils.convertToOnlyDate(dateToParse, MONGODB_QUERY_DATE_FORMAT)).withTimeAtStartOfDay();
            DateTime endOfDay = startOfDay.withTime(DAY_END_HOUR, DAY_END_MINUTE, DAY_END_SECOND, DAY_END_NANO);
            List<ClientException> clientExceptions = clientExceptionMongoRepository.getExceptionOfCitizenBetweenDates(exceptionDTO.getClientId(), startOfDay.toDate(), endOfDay.toDate(), unitId);
            allExceptions.addAll(clientExceptions);
            if (!clientExceptions.isEmpty()) {
                List<BigInteger> exceptionIds = clientExceptions.stream().map(clientException -> clientException.getId()).collect(Collectors.toList());
                exceptionIdsToDelete.addAll(exceptionIds);
                List<Task> tasksToHandle = taskMongoRepository.getTasksByException(clientExceptions.get(0).getClientId(), unitId, exceptionIds);
                exceptionIds.forEach(exceptionId -> {
                    deleteExceptionFromTask(tasksToHandle, exceptionId);
                });
                allTask.addAll(tasksToHandle);
            }
        }
        if (!allTask.isEmpty()) {
            save(allTask);
        }
        if (!allExceptions.isEmpty()) {
            clientExceptionMongoRepository.deleteAll(allExceptions);
        }

        // to update client aggregator object
        ClientAggregator clientAggregator = taskExceptionService.updateTaskCountInAggregator(allTask, unitId, exceptionDTO.getClientId(), true);
        if (clientAggregator != null) {
            PerformCalculation performCalculation = (n) -> n - 1;
            updateClientAggregator(allExceptions, performCalculation, clientAggregator);
            plannerService.sendAggregateDataToClient(clientAggregator, unitId);
        }
        HashMap<String, Object> response = new HashMap<>();
        response.put("exceptionIds", exceptionIdsToDelete);
        response.put("taskList", taskService.customizeTaskData(allTask));
        return response;
    }

    private Map<String, Object> prepareResponse(ClientException clientException) {
        if (Objects.isNull(clientException)) {
            return null;
        }
        Map<String, Object> data = new HashMap<>();
        data.put("id", clientException.getId());
        data.put("exceptionTypeId", clientException.getExceptionTypeId());
        data.put("value", clientException.getValue());
        data.put("name", clientException.getName());
        data.put("date", clientException.getFromTime());
        data.put("fromTime", clientException.getFromTime());
        data.put("toTime", clientException.getToTime());
        data.put("fullDay", clientException.isFullDay());
        data.put("exceptionHandled", clientException.isExceptionHandled());
        data.put("info1", clientException.getInfo1());
        data.put("info2", clientException.getInfo2());
        data.put("moveToDay", clientException.getMoveToDay());
        data.put("moveToTimeslotId", clientException.getMoveToTimeSlotId());
        data.put("newTaskDuration", clientException.getNewTaskDuration());
        data.put("newTaskPriority", clientException.getNewTaskPriority());
        data.put("temporaryAddress", clientException.getTemporaryAddressId() != null ? clientException.getTemporaryAddressId().toString() : "");
        data.put("taskStatus", clientException.getTaskStatus());
        data.put("houseHoldMembers",clientException.getHouseHoldMembers());

        return data;

    }

    public Object getClientExceptionById(BigInteger taskExceptionId) {
        Optional<ClientException> clientExceptionOptional = clientExceptionMongoRepository.findById(taskExceptionId);
        if (!clientExceptionOptional.isPresent()) {
            exceptionService.dataNotFoundByIdException("message.exception.task.notfound");
        }
        logger.info("Preparing response");
        ClientException clientException=clientExceptionOptional.get();
        return prepareResponse(clientException);
    }

    public List<ClientException> getClientExceptionOnDates(ClientExceptionDTO exceptionDTO, long unitId) throws ParseException {

        if (exceptionDTO.getSelectedDates() == null || exceptionDTO.getSelectedDates().isEmpty()) {
            return Collections.emptyList();
        }
        String firstElement = exceptionDTO.getSelectedDates().get(0);
        String lastElement = exceptionDTO.getSelectedDates().get(exceptionDTO.getSelectedDates().size() - 1);
        SimpleDateFormat executionDateFormat = new SimpleDateFormat(ONLY_DATE);
        Date startDate = executionDateFormat.parse(firstElement);
        Date endDate = executionDateFormat.parse(lastElement);

        DateTime startOfDate = new DateTime(startDate).withTimeAtStartOfDay();
        DateTime endOfDay = new DateTime(endDate).withTime(DAY_END_HOUR, DAY_END_MINUTE, DAY_END_SECOND, DAY_END_NANO);

        List<Date> selectedDates = new ArrayList<>();
        for (String date : exceptionDTO.getSelectedDates()) {
            selectedDates.add(DateUtils.convertToOnlyDate(date, ONLY_DATE));
        }
        return clientExceptionMongoRepository.getExceptionOfCitizenBetweenDates(exceptionDTO.getClientId(), startOfDate.toDate(), endOfDay.toDate(), unitId);
    }

    public Map<String, Object> updateClientExceptionById(BigInteger clientExceptionId, ClientExceptionDTO clientExceptionDto, long unitId) throws ParseException {
        Optional<ClientException> clientExceptionOptional = clientExceptionMongoRepository.findById(clientExceptionId);
        if (!clientExceptionOptional.isPresent()) {
            exceptionService.internalError("error.exception.notfound");
        }
        ClientException clientException=clientExceptionOptional.get();
        Optional<ClientExceptionType> clientExceptionTypeOptional = clientExceptionTypeMongoRepository.findById(new BigInteger(clientExceptionDto.getExceptionTypeId()));
        if (!clientExceptionTypeOptional.isPresent()) {
            exceptionService.internalError("error.exception.type");
        }
        ClientExceptionType clientExceptionType=clientExceptionTypeOptional.get();
        List<Task> tasksToReturn;
        List<ClientException> clientExceptions = null;
        List<BigInteger> exceptionIds = null;
        Map<String, Object> response = new HashMap<>();
        List<Task> allUnhandledTasks = new ArrayList<>();
        List<ClientException> newCreatedExceptions = new ArrayList<>();
        switch (clientExceptionType.getValue()) {
            case SICK: {
                List<ClientException> exceptionsToDelete = clientExceptionMongoRepository.getExceptionAfterDate
                        (clientException.getClientId(), clientException.getFromTime(), clientExceptionType.getId());
                exceptionIds = exceptionsToDelete.stream().map(exception -> exception.getId()).collect(Collectors.toList());
                taskMongoRepository.deleteExceptionsFromTasks(clientException.getClientId(), unitId, exceptionIds);
                clientExceptionMongoRepository.deleteByIdIn(exceptionIds);
                DateTime initialDate = new DateTime(clientException.getFromTime());
                initialDate = initialDate.plusDays(1);
                int exceptionCount = 0;
                int numberOfDeletedExceptions = exceptionIds.size();
                int numberOfExceptionsToCreate = 1;
                while (exceptionCount < clientExceptionDto.getDaysToReview()) {
                    DateTime startOfDay = initialDate.withTimeAtStartOfDay();
                    DateTime endOfDay = initialDate.withTime(DAY_END_HOUR, DAY_END_MINUTE, DAY_END_SECOND, DAY_END_NANO);
                    clientException = getClientExceptionObj(clientExceptionDto, startOfDay.toDate(), endOfDay.toDate(),
                            clientExceptionType, clientException.getClientId(), unitId);
                    if (clientExceptions == null) {
                        clientExceptions = new ArrayList<>();
                    }
                    save(clientException);
                    clientExceptions.add(clientException);
                    initialDate = initialDate.plusDays(1);
                    exceptionCount++;
                    tasksToReturn = getUpdatedTasks(unitId, clientException.getClientId(), clientException,
                            clientExceptionDto, false, null);
                    allUnhandledTasks.addAll(tasksToReturn);
                    if (numberOfExceptionsToCreate > numberOfDeletedExceptions) {
                        newCreatedExceptions.add(clientException);
                    }
                    numberOfExceptionsToCreate++;
                }
                break;
            }
            case DO_NOT_DISTURB: {
                validateClientException(clientExceptionDto, clientException);
                int numberOfTaskUpdated = taskMongoRepository.deleteExceptionsFromTasks(clientException.getClientId(),
                        unitId, Arrays.asList(clientException.getId()));
                logger.debug("Task deleted by exception update::" + numberOfTaskUpdated);
                tasksToReturn = getUpdatedTasks(unitId, clientException.getClientId(), clientException,
                        clientExceptionDto, false, null);
                allUnhandledTasks.addAll(tasksToReturn);
                save(updateClientException(clientException, clientExceptionDto));
                clientExceptions = new ArrayList<>();
                clientExceptions.add(clientException);
                break;
            }
            case CHANGE_LOCATION: {
                return updateChangeLocationException(clientException,clientExceptionDto);
            }
            case IN_HOSPITAL: {
                validateClientException(clientExceptionDto, clientException);
                int numberOfTaskUpdated = taskMongoRepository.deleteExceptionsFromTasks(clientException.getClientId(), unitId, Arrays.asList(clientException.getId()));
                logger.debug("Task deleted by exception update::" + numberOfTaskUpdated);
                tasksToReturn = getUpdatedTasks(unitId, clientException.getClientId(), clientException, clientExceptionDto, false, null);
                allUnhandledTasks.addAll(tasksToReturn);
                save(updateClientException(clientException, clientExceptionDto));
                clientExceptions = new ArrayList<>();
                clientExceptions.add(clientException);
                break;
            }
            default:
                exceptionService.internalError("error.exception.type");

        }
        ClientAggregator clientAggregator = taskExceptionService.updateTaskCountInAggregator(allUnhandledTasks, unitId, clientException.getClientId(), false);
        if (newCreatedExceptions != null) {
            updateClientAggregator(newCreatedExceptions, (n) -> n + 1, clientAggregator);
        }
        response.put("exceptionList", clientExceptions);
        response.put("deletedExceptions", exceptionIds);
        response.put("taskList", plannerService.customizeTaskData(allUnhandledTasks));
        return response;

    }

    private Map<String, Object> updateChangeLocationException(ClientException exceptionOfCitizen,ClientExceptionDTO clientExceptionDTO) throws ParseException {
        List<ClientException> exceptionsOfHouseHoldMembers = getExceptionsOfHouseHoldMembers(exceptionOfCitizen,clientExceptionDTO);
        exceptionsOfHouseHoldMembers.add(exceptionOfCitizen);
        List<BigInteger> exceptionIds = exceptionsOfHouseHoldMembers.stream().map(clientException-> clientException.getId()).collect(Collectors.toList());
        exceptionIds.add(exceptionOfCitizen.getId());
        List<Long> houseHoldMembers = new ArrayList<>(clientExceptionDTO.getHouseHoldMembers());
        houseHoldMembers.add(exceptionOfCitizen.getClientId());
        validateTimeSlotsForException(clientExceptionDTO,exceptionOfCitizen,houseHoldMembers,exceptionIds);
        ClientTemporaryAddress clientTemporaryAddress = userIntegrationService.updateClientTemporaryAddress(clientExceptionDTO, exceptionOfCitizen.getUnitId(), exceptionOfCitizen.getClientId());
        List<Task> updatedTasks = updateTasksOnCreatingChangeLocationException(houseHoldMembers,exceptionOfCitizen,clientExceptionDTO,clientTemporaryAddress,exceptionsOfHouseHoldMembers);
        for(ClientException exceptionsOfHouseHoldMember : exceptionsOfHouseHoldMembers){
            exceptionsOfHouseHoldMember.setTemporaryAddressId(clientTemporaryAddress.getId());
            updateClientException(exceptionsOfHouseHoldMember, clientExceptionDTO);
        }
        save(exceptionsOfHouseHoldMembers);
        updateUnhandledTaskCountForChangeLocationException(updatedTasks,houseHoldMembers,exceptionOfCitizen.getUnitId());
        Map<String,Object> response = new HashMap<>();
        response.put("exceptionList",Arrays.asList(exceptionOfCitizen));
        response.put("taskList",updatedTasks.parallelStream().filter(task -> task.getCitizenId() == exceptionOfCitizen.getClientId()).collect(Collectors.toList()));
        return response;
    }

    private void updateUnhandledTaskCountForChangeLocationException(List<Task> allUnhandledTasks,List<Long> clientIds,Long unitId){
        for(Long clientId:clientIds){
            List<Task> taskOfCitizen = allUnhandledTasks.stream().filter(unhandledTask->unhandledTask.getCitizenId()==clientId).collect(Collectors.toList());
            taskExceptionService.updateTaskCountInAggregator(taskOfCitizen, unitId, clientId, false);
        }

    }

    private List<ClientException> getExceptionsOfHouseHoldMembers(ClientException exceptionOfCitizen,ClientExceptionDTO clientExceptionDTO){
        return clientExceptionMongoRepository.findExceptionByClientIdInAndExceptionTypeIdAndFromTimeAndToTime(clientExceptionDTO.getHouseHoldMembers(),
                exceptionOfCitizen.getExceptionTypeId(),exceptionOfCitizen.getFromTime(),exceptionOfCitizen.getToTime());
    }

    private void validateTimeSlotsForException(ClientExceptionDTO clientExceptionDTO,ClientException exceptionOfCitizen,
                                               List<Long> clientIds,List<BigInteger> exceptionIds) throws ParseException {
        LocalDateTime fromTime = LocalDateTime.ofInstant(DateUtils.convertToOnlyDate(clientExceptionDTO.getFromTime(), MONGODB_QUERY_DATE_FORMAT).toInstant(), ZoneId.systemDefault());
        LocalDateTime toTime = LocalDateTime.ofInstant(DateUtils.convertToOnlyDate(clientExceptionDTO.getToTime(), MONGODB_QUERY_DATE_FORMAT).toInstant(), ZoneId.systemDefault());
        Date newDateFrom = DateUtils.getDate(exceptionOfCitizen.getFromTime().getTime());
        newDateFrom.setHours(fromTime.getHour());
        newDateFrom.setMinutes(fromTime.getMinute());
        Date newDateTO = DateUtils.getDate(exceptionOfCitizen.getToTime().getTime());
        newDateTO.setHours(toTime.getHour());
        newDateTO.setMinutes(toTime.getMinute());
        validateClientException(newDateFrom,newDateTO,exceptionIds,clientIds);

    }

    private List<Task> getUpdatedTasks(long unitId, long clientId, ClientException clientException, ClientExceptionDTO clientExceptionDTO,
                                       boolean exceptionHandled, ClientTemporaryAddress clientTemporaryAddress) throws ParseException {
        Date newDateFrom = DateUtils.getDate(clientException.getFromTime().getTime());
        Date newDateTO = DateUtils.getDate(clientException.getToTime().getTime());
        if (clientException.getExceptionTypeId().equals(new BigInteger("2"))) {
            newDateFrom.setHours(DAY_START_HOUR);
            newDateFrom.setMinutes(DAY_START_MINUTE);
            newDateTO.setHours(DAY_END_HOUR);
            newDateTO.setMinutes(DAY_END_MINUTE);
        } else {
            LocalDateTime fromTime = LocalDateTime.ofInstant(DateUtils.convertToOnlyDate(clientExceptionDTO.getFromTime(), MONGODB_QUERY_DATE_FORMAT).toInstant(), ZoneId.systemDefault());
            LocalDateTime toTime = LocalDateTime.ofInstant(DateUtils.convertToOnlyDate(clientExceptionDTO.getToTime(), MONGODB_QUERY_DATE_FORMAT).toInstant(), ZoneId.systemDefault());
            newDateFrom.setHours(fromTime.getHour());
            newDateFrom.setMinutes(fromTime.getMinute());
            newDateTO.setHours(toTime.getHour());
            newDateTO.setMinutes(toTime.getMinute());
        }
        ObjectMapper objectMapper = new ObjectMapper();
        List<Task> tasksByOldDate = taskMongoRepository.getTasksBetweenExceptionDates(unitId, clientId, clientException.getFromTime(), clientException.getToTime());
        List<Task> tasksByNewDate = taskMongoRepository.getTasksBetweenExceptionDates(unitId, clientId, newDateFrom, newDateTO);
        tasksByNewDate.forEach(task -> {
            if (!task.isSingleTask() && task.getActualPlanningTask() == null) {
                taskService.savePreplanningStateOfTask(task);
            }
            updateTaskInfo(clientExceptionDTO, task);
            updatePriorityAndDuration(clientExceptionDTO, task);
            if (!exceptionHandled)
                persistExceptionInTask(task, clientException, objectMapper);
            if (exceptionHandled && clientTemporaryAddress != null) {
                updateTaskAddress(task, clientTemporaryAddress);
            }
        });
        tasksByOldDate.forEach(oldTask -> {
            long count = tasksByNewDate.stream().filter(newTask -> newTask.getId().equals(oldTask.getId())).count();
            if (count == 0) {
                deleteExceptionFromTask(oldTask, clientException);
                tasksByNewDate.add(oldTask);
            }
        });
        Map<String, String> flsCredentials = userIntegrationService.getFLS_Credentials(unitId);
        taskConverterService.createFlsCallFromTasks(tasksByNewDate, flsCredentials);
        return tasksByNewDate;
    }

    private List<Task> updateTasksOnCreatingChangeLocationException(List<Long> clientIds, ClientException exceptionOfCitizen,
                                                                    ClientExceptionDTO clientExceptionDTO, ClientTemporaryAddress clientTemporaryAddress,
                                                                    List<ClientException> clientExceptions) throws ParseException {
        Date newDateFrom = DateUtils.getDate(exceptionOfCitizen.getFromTime().getTime());
        Date newDateTO = DateUtils.getDate(exceptionOfCitizen.getToTime().getTime());
        LocalDateTime fromTime = LocalDateTime.ofInstant(DateUtils.convertToOnlyDate(clientExceptionDTO.getFromTime(), MONGODB_QUERY_DATE_FORMAT).toInstant(), ZoneId.systemDefault());
        LocalDateTime toTime = LocalDateTime.ofInstant(DateUtils.convertToOnlyDate(clientExceptionDTO.getToTime(), MONGODB_QUERY_DATE_FORMAT).toInstant(), ZoneId.systemDefault());
        newDateFrom.setHours(fromTime.getHour());
        newDateFrom.setMinutes(fromTime.getMinute());
        newDateTO.setHours(toTime.getHour());
        newDateTO.setMinutes(toTime.getMinute());
        ObjectMapper objectMapper = new ObjectMapper();
        List<Task> tasksByOldDate = taskMongoRepository.getTasksBetweenExceptionDates(exceptionOfCitizen.getUnitId(), clientIds, exceptionOfCitizen.getFromTime(), exceptionOfCitizen.getToTime());
        List<Task> tasksByNewDate = taskMongoRepository.getTasksBetweenExceptionDates(exceptionOfCitizen.getUnitId(), clientIds, newDateFrom, newDateTO);
        tasksByNewDate.forEach(task -> {
            if (!task.isSingleTask() && task.getActualPlanningTask() == null) {
                taskService.savePreplanningStateOfTask(task);
            }
            updateTaskInfo(clientExceptionDTO, task);
            updatePriorityAndDuration(clientExceptionDTO, task);
            Optional<ClientException> result = clientExceptions.stream().filter(clientException-> clientException.getClientId() == task.getCitizenId()).findFirst();
            if(result.isPresent()){
                persistExceptionInTask(task, result.get(), objectMapper);
            }
            if (clientTemporaryAddress != null) {
                updateTaskAddress(task, clientTemporaryAddress);
            }
        });
        tasksByOldDate.forEach(oldTask -> {
            long count = tasksByNewDate.stream().filter(newTask -> newTask.getId().equals(oldTask.getId())).count();
            if (count == 0) {
                Optional<ClientException> result = clientExceptions.stream().filter(clientException-> clientException.getClientId() == oldTask.getCitizenId()).findFirst();
                if(result.isPresent()){
                    deleteExceptionFromTask(oldTask, result.get());
                }
                tasksByNewDate.add(oldTask);
            }
        });
        Map<String, String> flsCredentials = userIntegrationService.getFLS_Credentials(exceptionOfCitizen.getUnitId());
        taskConverterService.createFlsCallFromTasks(tasksByNewDate, flsCredentials);
        return tasksByNewDate;
    }

    private Task deleteExceptionFromTask(Task task, ClientException clientException) {

        Iterator<Task.ClientException> clientExceptionIterator = task.getClientExceptions().iterator();
        while (clientExceptionIterator.hasNext()) {
            Task.ClientException exceptionToDelete = clientExceptionIterator.next();
            if (exceptionToDelete.getId().equals(clientException.getId())) {
                clientExceptionIterator.remove();
                break;
            }
        }
        return task;
    }

    //TODO refactor this method, need to refactor name
    private void deleteExceptionFromTask(List<Task> tasksToHandle, BigInteger clientExceptionId) {

        tasksToHandle.forEach(task -> {
            Iterator<Task.ClientException> clientExceptionIterator = task.getClientExceptions().iterator();
            while (clientExceptionIterator.hasNext()) {
                Task.ClientException exceptionToDelete = clientExceptionIterator.next();
                if (exceptionToDelete.getId().equals(clientExceptionId)) {
                    clientExceptionIterator.remove();
                    break;
                }
            }
        });
    }


    private ClientException updateClientException(ClientException clientException, ClientExceptionDTO clientExceptionDTO) throws ParseException {
        if (!GenericValidator.isBlankOrNull(clientExceptionDTO.getFromTime()) && !GenericValidator.isBlankOrNull(clientExceptionDTO.getToTime())) {
            LocalDateTime fromTime = LocalDateTime.ofInstant(DateUtils.convertToOnlyDate(clientExceptionDTO.getFromTime(), MONGODB_QUERY_DATE_FORMAT).toInstant(), ZoneId.systemDefault());
            LocalDateTime toTime = LocalDateTime.ofInstant(DateUtils.convertToOnlyDate(clientExceptionDTO.getToTime(), MONGODB_QUERY_DATE_FORMAT).toInstant(), ZoneId.systemDefault());
            clientException.getFromTime().setHours(fromTime.getHour());
            clientException.getFromTime().setMinutes(fromTime.getMinute());
            clientException.getToTime().setHours(toTime.getHour());
            clientException.getToTime().setMinutes(toTime.getMinute());
            clientException.setFullDay(false);
        } else {
            clientException.getFromTime().setHours(DAY_START_HOUR);
            clientException.getFromTime().setMinutes(DAY_START_MINUTE);
            clientException.getToTime().setHours(DAY_END_HOUR);
            clientException.getToTime().setMinutes(DAY_END_MINUTE);
            clientException.setFullDay(true);
        }
        clientException.setNewTaskDuration(String.valueOf(clientExceptionDTO.getNewTaskDuration()));
        clientException.setNewTaskPriority(String.valueOf(clientExceptionDTO.getNewTaskPriority()));
        return clientException;
    }

    public void updateTaskException(long clientId, Task actualTask) {
        List<ClientException> clientExceptions = clientExceptionMongoRepository.getExceptionBetweenTaskDates(clientId, actualTask.getTimeFrom(), actualTask.getTimeTo());
        ObjectMapper objectMapper = new ObjectMapper();
        List<Task.ClientException> exceptions = clientExceptions.stream().map(clientException -> objectMapper.convertValue(clientException, Task.ClientException.class)).collect(Collectors.toList());
        actualTask.setClientExceptions(exceptions);
    }


    private void validateClientException(List<Long> clientId, Date dateFrom, Date dateTo, BigInteger exceptionTypeId) {
        if (clientExceptionMongoRepository.isExceptionTypeExistBetweenDate(clientId, dateFrom, dateTo, exceptionTypeId)) {
            exceptionService.invalidClientException("message.exception.timeslot.create");
        }
    }

    private void validateClientException(ClientExceptionDTO clientExceptionDTO, ClientException clientException) throws ParseException {

        LocalDateTime fromTime = LocalDateTime.ofInstant(DateUtils.convertToOnlyDate(clientExceptionDTO.getFromTime(), MONGODB_QUERY_DATE_FORMAT).toInstant(), ZoneId.systemDefault());
        LocalDateTime toTime = LocalDateTime.ofInstant(DateUtils.convertToOnlyDate(clientExceptionDTO.getToTime(), MONGODB_QUERY_DATE_FORMAT).toInstant(), ZoneId.systemDefault());
        Date newDateFrom = DateUtils.getDate(clientException.getFromTime().getTime());
        newDateFrom.setHours(fromTime.getHour());
        newDateFrom.setMinutes(fromTime.getMinute());
        Date newDateTO = DateUtils.getDate(clientException.getToTime().getTime());
        newDateTO.setHours(toTime.getHour());
        newDateTO.setMinutes(toTime.getMinute());
        if (clientExceptionMongoRepository.isExceptionExistBetweenDate(clientException.getClientId(), newDateFrom, newDateTO, clientException.getId())) {
            exceptionService.invalidClientException("message.exception.timeslot.create");
        }
    }

    private void validateClientException(Date dateFrom, Date dateTo, List<BigInteger> exceptionIds,List<Long> clientIds) throws ParseException {

        if (clientExceptionMongoRepository.isExceptionExistBetweenDate(clientIds, dateFrom, dateTo, exceptionIds)) {
            exceptionService.invalidClientException("message.exception.timeslot.create");
        }
    }

    private void validateSickException(DateTime initialDate, int daysToReview, long citizenId) {

        DateTime startDate = initialDate.withTimeAtStartOfDay();
        ;
        long count = clientExceptionMongoRepository.countSickExceptionsAfterDate(citizenId, startDate.toDate());
        if (daysToReview <= count) {
            exceptionService.invalidClientException("message.exception.timeslot.create");
        }
    }

    private void updateClientAggregator(List<ClientException> clientExceptions, PerformCalculation performCalculation, ClientAggregator clientAggregator) {

        List<ClientExceptionCount> clientExceptionCounts = clientAggregator.getClientExceptionCounts();
        for (ClientException clientException : clientExceptions) {
            updateExceptionCountObj(clientException, performCalculation, clientExceptionCounts);
        }
        clientAggregator.setClientExceptionCounts(clientExceptionCounts);
        save(clientAggregator);
    }

    private void updateClientAggregator(ClientException clientException, PerformCalculation performCalculation, ClientAggregator clientAggregator) {

        if (clientAggregator == null) {
            clientAggregator = new ClientAggregator(clientException.getUnitId(), clientException.getClientId());
        }
        List<ClientExceptionCount> clientExceptionCounts = clientAggregator.getClientExceptionCounts();
        updateExceptionCountObj(clientException, performCalculation, clientExceptionCounts);
        clientAggregator.setClientExceptionCounts(clientExceptionCounts);
        save(clientAggregator);
    }


    private void updateExceptionCountObj(ClientException clientException, PerformCalculation performCalculation, List<ClientExceptionCount> clientExceptionCounts) {

        Optional<ClientExceptionCount> exceptionCount = clientExceptionCounts.stream().filter(clientExceptionCount -> clientExceptionCount.getExceptionTypeId().equals(clientException.getExceptionTypeId())).findFirst();
        ClientExceptionCount clientExceptionCount = (exceptionCount.isPresent()) ? exceptionCount.get() : new ClientExceptionCount(clientException.getExceptionTypeId());
        updateCountInException(clientException, clientExceptionCount, performCalculation, FourWeekFrequency.getInstance());
        if (!exceptionCount.isPresent()) {
            clientExceptionCounts.add(clientExceptionCount);
        }
    }


    public void updateCountInException(ClientException clientException, ClientExceptionCount clientExceptionCount, PerformCalculation performCalculation,
                                       FourWeekFrequency fourWeekFrequency) {

        LocalDateTime exceptionStartTime = LocalDateTime.ofInstant(clientException.getFromTime().toInstant(), ZoneId.systemDefault());
        if (exceptionStartTime.isEqual(fourWeekFrequency.getStartOfDay()) || (exceptionStartTime.isAfter(fourWeekFrequency.getStartOfDay()) && exceptionStartTime.isBefore(fourWeekFrequency.getEndOfDay()))) {
            clientExceptionCount.setExceptionsTodayCount(performCalculation.performCalculation(clientExceptionCount.getExceptionsTodayCount()));
        }
        if (exceptionStartTime.isEqual(fourWeekFrequency.getStartOfTomorrow()) || (exceptionStartTime.isAfter(fourWeekFrequency.getStartOfTomorrow()) && exceptionStartTime.isBefore(fourWeekFrequency.getEndOfTomorrow()))) {
            clientExceptionCount.setExceptionsTomorrowCount(performCalculation.performCalculation(clientExceptionCount.getExceptionsTomorrowCount()));
        }
        if (exceptionStartTime.isEqual(fourWeekFrequency.getStartOfDayAfterTomorrow()) || (exceptionStartTime.isAfter(fourWeekFrequency.getStartOfDayAfterTomorrow()) && exceptionStartTime.isBefore(fourWeekFrequency.getEndOfDayAfterTomorrow()))) {
            clientExceptionCount.setExceptionsDayAfterTomorrowCount(performCalculation.performCalculation(clientExceptionCount.getExceptionsDayAfterTomorrowCount()));
        }
        if (exceptionStartTime.isEqual(fourWeekFrequency.getStartOfWeek()) || (exceptionStartTime.isAfter(fourWeekFrequency.getStartOfWeek()) && exceptionStartTime.isBefore(fourWeekFrequency.getEndOfWeek()))) {
            clientExceptionCount.setExceptionsOneWeekCount(performCalculation.performCalculation(clientExceptionCount.getExceptionsOneWeekCount()));
        }
        if (exceptionStartTime.isEqual(fourWeekFrequency.getStartOfWeek()) || (exceptionStartTime.isAfter(fourWeekFrequency.getStartOfWeek()) && exceptionStartTime.isBefore(fourWeekFrequency.getEndOfSecondWeek()))) {
            clientExceptionCount.setExceptionsTwoWeekCount(performCalculation.performCalculation(clientExceptionCount.getExceptionsTwoWeekCount()));
        }
        if (exceptionStartTime.isEqual(fourWeekFrequency.getStartOfWeek()) || (exceptionStartTime.isAfter(fourWeekFrequency.getStartOfWeek()) && exceptionStartTime.isBefore(fourWeekFrequency.getEndOfThirdWeek()))) {
            clientExceptionCount.setExceptionsThreeWeekCount(performCalculation.performCalculation(clientExceptionCount.getExceptionsThreeWeekCount()));
        }
        if (exceptionStartTime.isEqual(fourWeekFrequency.getStartOfWeek()) || (exceptionStartTime.isAfter(fourWeekFrequency.getStartOfWeek()) && exceptionStartTime.isBefore(fourWeekFrequency.getEndOfFourWeek()))) {
            clientExceptionCount.setExceptionsFourWeekCount(performCalculation.performCalculation(clientExceptionCount.getExceptionsFourWeekCount()));
        }

    }

    /**
     * this method will return clients only which are having exceptions for current week
     *
     * @param unitId
     * @return
     */
    public List<ClientExceptionCountWrapper> getExceptionClients(long unitId) {
        return aggregatorService.getClientAggregateData(unitId);
    }


}
