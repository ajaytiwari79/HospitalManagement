package com.kairos.service.aggregator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.dto.activity.client_exception.ClientExceptionCount;
import com.kairos.persistence.model.client_aggregator.ClientAggregator;
import com.kairos.persistence.model.client_aggregator.FourWeekFrequency;
import com.kairos.persistence.model.client_exception.ClientException;
import com.kairos.persistence.model.task.Task;
import com.kairos.persistence.model.task.UnhandledTaskCount;
import com.kairos.persistence.model.task_demand.TaskDemand;
import com.kairos.persistence.model.task_demand.TaskDemandVisit;
import com.kairos.persistence.repository.client_aggregator.ClientAggregatorMongoRepository;
import com.kairos.persistence.repository.client_exception.ClientExceptionMongoRepository;
import com.kairos.persistence.repository.common.CustomAggregationOperation;
import com.kairos.persistence.repository.task_type.TaskDemandMongoRepository;
import com.kairos.persistence.repository.task_type.TaskMongoRepository;
import com.kairos.rest_client.ClientRestClient;
import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.rest_client.IntegrationRestClient;
import com.kairos.rest_client.SchedulerRestClient;
import com.kairos.service.MongoBaseService;
import com.kairos.service.client_exception.ClientExceptionService;
import com.kairos.service.fls_visitour.schedule.Scheduler;
import com.kairos.service.planner.RandomDateGeneratorService;
import com.kairos.service.task_type.TaskDynamicReportService;
import com.kairos.dto.user.client.Client;
import com.kairos.dto.user.client.ClientExceptionCountWrapper;
import com.kairos.dto.user.client.ClientOrganizationIds;
import com.kairos.dto.user.staff.client.ClientAggregatorDTO;
import com.kairos.utils.ApplicationUtil;
import com.kairos.commons.utils.DateUtils;
import com.kairos.utils.functional_interface.PerformCalculation;
import de.tourenserver.CallInfoRec;
import org.bson.Document;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstants.CITIZEN_ID;
import static com.kairos.persistence.model.constants.TaskConstants.MONOGDB_QUERY_RECORD_LIMIT;
import static java.time.ZoneId.systemDefault;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * Created by oodles on 7/7/17.
 */
@Transactional
@Service
public class AggregatorService extends MongoBaseService {

    private static final Logger logger = LoggerFactory.getLogger(AggregatorService.class);

    @Inject
    private Scheduler scheduler;
    @Inject
    private GenericIntegrationService genericIntegrationService;
    @Inject
    private ClientAggregatorMongoRepository clientAggregatorMongoRepository;
    @Inject
    private MongoTemplate mongoTemplate;
    @Inject
    private TaskDynamicReportService taskDynamicReportService;
    @Inject
    private ClientExceptionMongoRepository clientExceptionMongoRepository;
    @Inject
    private ClientExceptionService clientExceptionService;
    @Inject
    private TaskMongoRepository taskMongoRepository;
    @Inject
    private SchedulerRestClient schedulerRestClient;
    @Inject
    private TaskDemandMongoRepository taskDemandMongoRepository;
    @Inject
    private ClientRestClient clientRestClient;
    @Inject
    private RandomDateGeneratorService randomDateGeneratorService;

    private final Integer distance = 5000;

    private final Integer drivingTime = 10;

    private final Integer mostDistance = 5000;

    private final Integer mostDuration = 40;

    //TODO we need to check job data creation issue, temporary stop the service
   // @Scheduled(initialDelay = 100000, fixedDelay = 4000000)
    public void aggregator() {
        //Please don't change the sequence
        logger.info("Aggregator Job Starts on time---> " + DateUtils.getDate());
        try {
            List<Long> organizations = schedulerRestClient.getAllOrganizationIds();
            logger.info("Aggregator Job Starts on time---> " + clientRestClient.getCitizenIdsByUnitIds(organizations));
            for (Long organizationId : organizations) {
                aggregatorOneWeek(organizationId);
                aggregatorTwoWeek(organizationId);
                aggregatorThreeWeek(organizationId);
                aggregatorFourWeek(organizationId);
                sendDynamicReports(organizationId);

            }
            logger.info("Aggregator Job Ends on time---> " + DateUtils.getDate());
        } catch (Exception e) {
            logger.warn("Exception while aggregating Citizen data {} ", e);
        }


    }


    private void aggregatorOneWeek(Long organizationId) {

        List<Map> taskIdsGroupByCitizen = getTasksGroupByCitizen(0, 7, organizationId);
        for (Map map : taskIdsGroupByCitizen) {
            logger.debug("taskIds map for one week: " + map);
            List<Map<String, Object>> taskIds = (List<Map<String, Object>>) map.get("taskIds");
            Long  citizenId = Long.valueOf(map.get("_id").toString());


            ClientAggregator clientAggregator = clientAggregatorMongoRepository.findByUnitIdAndCitizenId(organizationId, citizenId);
            if (clientAggregator == null) clientAggregator = new ClientAggregator();
            Map<String, String> flsCredentials =  genericIntegrationService.getFLS_Credentials(organizationId);
            ClientAggregatorDTO clientAggregatorDTO = new ClientAggregatorDTO();
            if(!flsCredentials.get("flsDefaultUrl").equals("")) {
                clientAggregatorDTO = saveClientAggregator( taskIds, flsCredentials);
            }


            clientAggregator.setUnitId(organizationId);
            clientAggregator.setCitizenId(citizenId);
            clientAggregator.setEscalationOneWeekCount(clientAggregatorDTO.getEscalationCount());
            clientAggregator.setLongDrivingOneWeekCount(clientAggregatorDTO.getLongDrivingCount());
            clientAggregator.setMostDrivenOneWeekCount(clientAggregatorDTO.getMostDrivenCount());
            clientAggregator.setEscalationOneWeekTasks(clientAggregatorDTO.getEscalationTasks());
            clientAggregator.setMostDrivenOneWeekTasks(clientAggregatorDTO.getMostDrivenTasks());
            clientAggregator.setLongDrivingOneWeekTasks(clientAggregatorDTO.getLongDrivingTasks());
            clientAggregator.setWaitingOneWeekCount(clientAggregatorDTO.getWaitingCount());
            clientAggregator.setWaitingOneWeekTasks(clientAggregatorDTO.getWaitingTasks());
            clientAggregator.setPlannedStatusOneWeekCount(clientAggregatorDTO.getPlannedStatusCount());
            clientAggregator.setPlannedStatusOneWeekTasks(clientAggregatorDTO.getPlannedStatusTasks());
            clientAggregator.setTotalPlannedProblemsOneWeekCount(clientAggregatorDTO.getEscalationCount() + clientAggregatorDTO.getLongDrivingCount() + clientAggregatorDTO.getMostDrivenCount()+ clientAggregatorDTO.getPlannedStatusCount() + clientAggregatorDTO.getWaitingCount());
            clientAggregator.setTotalPlannedProblemsOneWeekTasks(ApplicationUtil.removingDuplicatesFromOneList(clientAggregatorDTO.getTotalPlannedProblemsTasks()));
            clientAggregator.setEscalationTwoWeekCount(clientAggregator.getEscalationOneWeekCount());
            clientAggregator.setLongDrivingTwoWeekCount(clientAggregator.getLongDrivingOneWeekCount());
            clientAggregator.setMostDrivenTwoWeekCount(clientAggregator.getMostDrivenOneWeekCount());
            clientAggregator.setWaitingTwoWeekCount(clientAggregator.getWaitingOneWeekCount());
            clientAggregator.setPlannedStatusTwoWeekCount(clientAggregator.getPlannedStatusOneWeekCount());
            clientAggregator.setTotalPlannedProblemsTwoWeekCount(clientAggregator.getTotalPlannedProblemsOneWeekCount());
            clientAggregator.setEscalationTwoWeekTasks(clientAggregatorDTO.getEscalationTasks());
            clientAggregator.setLongDrivingTwoWeekTasks(clientAggregatorDTO.getLongDrivingTasks());
            clientAggregator.setMostDrivenTwoWeekTasks(clientAggregatorDTO.getMostDrivenTasks());
            clientAggregator.setPlannedStatusTwoWeekTasks(clientAggregatorDTO.getPlannedStatusTasks());
            clientAggregator.setWaitingTwoWeekTasks(clientAggregatorDTO.getWaitingTasks());
            clientAggregator.setTotalPlannedProblemsTwoWeekTasks(clientAggregator.getTotalPlannedProblemsOneWeekTasks());
            clientAggregator.setEscalationThreeWeekCount(clientAggregator.getEscalationOneWeekCount());
            clientAggregator.setLongDrivingThreeWeekCount(clientAggregator.getLongDrivingOneWeekCount());
            clientAggregator.setMostDrivenThreeWeekCount(clientAggregator.getMostDrivenOneWeekCount());
            clientAggregator.setWaitingThreeWeekCount(clientAggregator.getWaitingOneWeekCount());
            clientAggregator.setPlannedStatusThreeWeekCount(clientAggregator.getPlannedStatusOneWeekCount());
            clientAggregator.setTotalPlannedProblemsThreeWeekCount(clientAggregator.getTotalPlannedProblemsOneWeekCount());
            clientAggregator.setEscalationThreeWeekTasks(clientAggregatorDTO.getEscalationTasks());
            clientAggregator.setLongDrivingThreeWeekTasks(clientAggregatorDTO.getLongDrivingTasks());
            clientAggregator.setMostDrivenThreeWeekTasks(clientAggregatorDTO.getMostDrivenTasks());
            clientAggregator.setPlannedStatusThreeWeekTasks(clientAggregatorDTO.getPlannedStatusTasks());
            clientAggregator.setWaitingThreeWeekTasks(clientAggregatorDTO.getWaitingTasks());
            clientAggregator.setTotalPlannedProblemsThreeWeekTasks(clientAggregator.getTotalPlannedProblemsOneWeekTasks());
            clientAggregator.setEscalationFourWeekCount(clientAggregator.getEscalationOneWeekCount());
            clientAggregator.setLongDrivingFourWeekCount(clientAggregator.getLongDrivingOneWeekCount());
            clientAggregator.setMostDrivenFourWeekCount(clientAggregator.getMostDrivenOneWeekCount());
            clientAggregator.setWaitingFourWeekCount(clientAggregator.getWaitingOneWeekCount());
            clientAggregator.setPlannedStatusFourWeekCount(clientAggregator.getPlannedStatusOneWeekCount());
            clientAggregator.setTotalPlannedProblemsFourWeekCount(clientAggregator.getTotalPlannedProblemsOneWeekCount());
            clientAggregator.setEscalationFourWeekTasks(clientAggregatorDTO.getEscalationTasks());
            clientAggregator.setLongDrivingFourWeekTasks(clientAggregatorDTO.getLongDrivingTasks());
            clientAggregator.setMostDrivenFourWeekTasks(clientAggregatorDTO.getMostDrivenTasks());
            clientAggregator.setPlannedStatusFourWeekTasks(clientAggregatorDTO.getPlannedStatusTasks());
            clientAggregator.setWaitingFourWeekTasks(clientAggregatorDTO.getWaitingTasks());
            clientAggregator.setTotalPlannedProblemsFourWeekTasks(clientAggregator.getTotalPlannedProblemsOneWeekTasks());
            save(clientAggregator);


        }


    }


    private void aggregatorTwoWeek(Long organizationId) {
        List<Map> taskIdsGroupByCitizen = getTasksGroupByCitizen(7, 14, organizationId);
        for (Map map : taskIdsGroupByCitizen) {
            logger.debug("taskIds map for two week: " + map);
            List<Map<String, Object>> taskIds = (List<Map<String, Object>>) map.get("taskIds");
            Long  citizenId = Long.valueOf(map.get("_id").toString());

            ClientAggregator clientAggregator = clientAggregatorMongoRepository.findByUnitIdAndCitizenId(organizationId, citizenId);
            if (clientAggregator == null) clientAggregator = new ClientAggregator();
            Map<String, String> flsCredentials =  genericIntegrationService.getFLS_Credentials(organizationId);
            ClientAggregatorDTO clientAggregatorDTO = new ClientAggregatorDTO();
            if(!flsCredentials.get("flsDefaultUrl").equals("")) {
                clientAggregatorDTO = saveClientAggregator( taskIds, flsCredentials);
            }

            clientAggregator.setEscalationTwoWeekCount(clientAggregatorDTO.getEscalationCount() + clientAggregator.getEscalationOneWeekCount());
            clientAggregator.setLongDrivingTwoWeekCount(clientAggregatorDTO.getLongDrivingCount() + clientAggregator.getLongDrivingOneWeekCount());
            clientAggregator.setMostDrivenTwoWeekCount(clientAggregatorDTO.getMostDrivenCount() + clientAggregator.getMostDrivenOneWeekCount());
            clientAggregator.setWaitingTwoWeekCount(clientAggregatorDTO.getWaitingCount() + clientAggregator.getWaitingOneWeekCount());
            clientAggregator.setPlannedStatusTwoWeekCount(clientAggregatorDTO.getPlannedStatusCount() + clientAggregator.getPlannedStatusTwoWeekCount());
            clientAggregator.setTotalPlannedProblemsTwoWeekCount(clientAggregator.getWaitingTwoWeekCount() + clientAggregator.getLongDrivingTwoWeekCount() + clientAggregator.getMostDrivenTwoWeekCount() + clientAggregator.getEscalationTwoWeekCount() + clientAggregator.getPlannedStatusTwoWeekCount());
            clientAggregator.setWaitingTwoWeekTasks(ApplicationUtil.removingDuplicates(clientAggregatorDTO.getWaitingTasks(), clientAggregator.getWaitingOneWeekTasks()));
            clientAggregator.setEscalationTwoWeekTasks(ApplicationUtil.removingDuplicates(clientAggregatorDTO.getEscalationTasks(), clientAggregator.getEscalationOneWeekTasks()));
            clientAggregator.setLongDrivingTwoWeekTasks(ApplicationUtil.removingDuplicates(clientAggregatorDTO.getLongDrivingTasks(), clientAggregator.getLongDrivingOneWeekTasks()));
            clientAggregator.setMostDrivenTwoWeekTasks(ApplicationUtil.removingDuplicates(clientAggregatorDTO.getMostDrivenTasks(), clientAggregator.getMostDrivenOneWeekTasks()));
            clientAggregator.setPlannedStatusTwoWeekTasks(ApplicationUtil.removingDuplicates(clientAggregatorDTO.getPlannedStatusTasks(), clientAggregator.getPlannedStatusOneWeekTasks()));
            clientAggregator.setTotalPlannedProblemsTwoWeekTasks(ApplicationUtil.removingDuplicates(clientAggregatorDTO.getTotalPlannedProblemsTasks(), clientAggregator.getTotalPlannedProblemsOneWeekTasks()));
            clientAggregator.setEscalationThreeWeekCount(clientAggregator.getEscalationTwoWeekCount());
            clientAggregator.setLongDrivingThreeWeekCount(clientAggregator.getLongDrivingTwoWeekCount());
            clientAggregator.setMostDrivenThreeWeekCount(clientAggregator.getMostDrivenTwoWeekCount());
            clientAggregator.setWaitingThreeWeekCount(clientAggregator.getWaitingTwoWeekCount());
            clientAggregator.setPlannedStatusThreeWeekCount(clientAggregator.getPlannedStatusTwoWeekCount());
            clientAggregator.setTotalPlannedProblemsThreeWeekCount(clientAggregator.getTotalPlannedProblemsTwoWeekCount());
            clientAggregator.setEscalationThreeWeekTasks(clientAggregator.getEscalationTwoWeekTasks());
            clientAggregator.setLongDrivingThreeWeekTasks(clientAggregator.getLongDrivingTwoWeekTasks());
            clientAggregator.setMostDrivenThreeWeekTasks(clientAggregator.getMostDrivenTwoWeekTasks());
            clientAggregator.setPlannedStatusThreeWeekTasks(clientAggregator.getPlannedStatusTwoWeekTasks());
            clientAggregator.setTotalPlannedProblemsThreeWeekTasks(clientAggregator.getTotalPlannedProblemsTwoWeekTasks());
            clientAggregator.setEscalationFourWeekCount(clientAggregator.getEscalationTwoWeekCount());
            clientAggregator.setLongDrivingFourWeekCount(clientAggregator.getLongDrivingTwoWeekCount());
            clientAggregator.setMostDrivenFourWeekCount(clientAggregator.getMostDrivenTwoWeekCount());
            clientAggregator.setWaitingFourWeekCount(clientAggregator.getWaitingTwoWeekCount());
            clientAggregator.setPlannedStatusFourWeekCount(clientAggregator.getPlannedStatusTwoWeekCount());
            clientAggregator.setTotalPlannedProblemsFourWeekCount(clientAggregator.getTotalPlannedProblemsTwoWeekCount());
            clientAggregator.setEscalationFourWeekTasks(clientAggregator.getEscalationTwoWeekTasks());
            clientAggregator.setLongDrivingFourWeekTasks(clientAggregator.getLongDrivingTwoWeekTasks());
            clientAggregator.setMostDrivenFourWeekTasks(clientAggregator.getMostDrivenTwoWeekTasks());
            clientAggregator.setPlannedStatusFourWeekTasks(clientAggregator.getPlannedStatusTwoWeekTasks());
            clientAggregator.setTotalPlannedProblemsFourWeekTasks(clientAggregator.getTotalPlannedProblemsTwoWeekTasks());
            save(clientAggregator);


        }

    }

    private void aggregatorThreeWeek(Long organizationId) {
        List<Map> taskIdsGroupByCitizen = getTasksGroupByCitizen(14, 21, organizationId);
        for (Map map : taskIdsGroupByCitizen) {
            logger.debug("taskIds map for three weeks: " + map);
            List<Map<String, Object>> taskIds = (List<Map<String, Object>>) map.get("taskIds");
            Long  citizenId = Long.valueOf(map.get("_id").toString());

            ClientAggregator clientAggregator = clientAggregatorMongoRepository.findByUnitIdAndCitizenId(organizationId, citizenId);
            if (clientAggregator == null) clientAggregator = new ClientAggregator();
            Map<String, String> flsCredentials =  genericIntegrationService.getFLS_Credentials(organizationId);
            ClientAggregatorDTO clientAggregatorDTO = new ClientAggregatorDTO();
            if(!flsCredentials.get("flsDefaultUrl").equals("")) {
                clientAggregatorDTO = saveClientAggregator( taskIds, flsCredentials);
            }

            clientAggregator.setEscalationThreeWeekCount(clientAggregatorDTO.getEscalationCount() + clientAggregator.getEscalationTwoWeekCount());
            clientAggregator.setLongDrivingThreeWeekCount(clientAggregatorDTO.getLongDrivingCount() + clientAggregator.getLongDrivingTwoWeekCount());
            clientAggregator.setMostDrivenThreeWeekCount(clientAggregatorDTO.getMostDrivenCount() + clientAggregator.getMostDrivenTwoWeekCount());
            clientAggregator.setWaitingThreeWeekCount(clientAggregatorDTO.getWaitingCount() + clientAggregator.getWaitingTwoWeekCount());
            clientAggregator.setPlannedStatusThreeWeekCount(clientAggregatorDTO.getPlannedStatusCount() + clientAggregator.getPlannedStatusTwoWeekCount());
            clientAggregator.setTotalPlannedProblemsThreeWeekCount(clientAggregator.getWaitingThreeWeekCount() + clientAggregator.getLongDrivingThreeWeekCount() + clientAggregator.getMostDrivenThreeWeekCount() + clientAggregator.getEscalationThreeWeekCount() + clientAggregator.getPlannedStatusThreeWeekCount());
            clientAggregator.setEscalationThreeWeekTasks(ApplicationUtil.removingDuplicates(clientAggregatorDTO.getEscalationTasks(), clientAggregator.getEscalationTwoWeekTasks()));
            clientAggregator.setLongDrivingThreeWeekTasks(ApplicationUtil.removingDuplicates(clientAggregatorDTO.getLongDrivingTasks(), clientAggregator.getLongDrivingTwoWeekTasks()));
            clientAggregator.setMostDrivenThreeWeekTasks(ApplicationUtil.removingDuplicates(clientAggregatorDTO.getMostDrivenTasks(), clientAggregator.getMostDrivenTwoWeekTasks()));
            clientAggregator.setPlannedStatusThreeWeekTasks(ApplicationUtil.removingDuplicates(clientAggregatorDTO.getPlannedStatusTasks(), clientAggregator.getPlannedStatusTwoWeekTasks()));
            clientAggregator.setWaitingThreeWeekTasks(ApplicationUtil.removingDuplicates(clientAggregatorDTO.getWaitingTasks(), clientAggregator.getWaitingTwoWeekTasks()));
            clientAggregator.setTotalPlannedProblemsThreeWeekTasks(ApplicationUtil.removingDuplicates(clientAggregatorDTO.getTotalPlannedProblemsTasks(), clientAggregator.getTotalPlannedProblemsTwoWeekTasks()));
            clientAggregator.setEscalationFourWeekCount(clientAggregator.getEscalationThreeWeekCount());
            clientAggregator.setLongDrivingFourWeekCount(clientAggregator.getLongDrivingThreeWeekCount());
            clientAggregator.setMostDrivenFourWeekCount(clientAggregator.getMostDrivenThreeWeekCount());
            clientAggregator.setWaitingFourWeekCount(clientAggregator.getWaitingThreeWeekCount());
            clientAggregator.setPlannedStatusFourWeekCount(clientAggregator.getPlannedStatusThreeWeekCount());
            clientAggregator.setTotalPlannedProblemsFourWeekCount(clientAggregator.getWaitingFourWeekCount() + clientAggregator.getLongDrivingFourWeekCount() + clientAggregator.getMostDrivenFourWeekCount() + clientAggregator.getEscalationFourWeekCount() + clientAggregator.getPlannedStatusFourWeekCount());
            clientAggregator.setEscalationFourWeekTasks(clientAggregator.getEscalationThreeWeekTasks());
            clientAggregator.setLongDrivingFourWeekTasks(clientAggregator.getLongDrivingThreeWeekTasks());
            clientAggregator.setMostDrivenFourWeekTasks(clientAggregator.getMostDrivenThreeWeekTasks());
            clientAggregator.setPlannedStatusFourWeekTasks(clientAggregator.getPlannedStatusThreeWeekTasks());
            clientAggregator.setTotalPlannedProblemsFourWeekTasks(clientAggregator.getTotalPlannedProblemsThreeWeekTasks());
            save(clientAggregator);

        }

    }





    private void aggregatorFourWeek(Long organizationId) {
        List<Map> taskIdsGroupByCitizen = getTasksGroupByCitizen(21, 28, organizationId);
        for (Map map : taskIdsGroupByCitizen) {
            logger.debug("taskIds map for four week: " + map);
            List<Map<String, Object>> taskIds = (List<Map<String, Object>>) map.get("taskIds");
            Long  citizenId = Long.valueOf(map.get("_id").toString());
            ClientAggregator clientAggregator = clientAggregatorMongoRepository.findByUnitIdAndCitizenId(organizationId, citizenId);
            if (clientAggregator == null) clientAggregator = new ClientAggregator();
            Map<String, String> flsCredentials =  genericIntegrationService.getFLS_Credentials(organizationId);
            ClientAggregatorDTO clientAggregatorDTO = new ClientAggregatorDTO();
            if(!flsCredentials.get("flsDefaultUrl").equals("")) {
                 clientAggregatorDTO = saveClientAggregator( taskIds, flsCredentials);
            }
            clientAggregator = saveAggregatorFourWeek(clientAggregator, clientAggregatorDTO);
            save(clientAggregator);

        }

    }

    private ClientAggregator saveAggregatorFourWeek(ClientAggregator clientAggregator, ClientAggregatorDTO clientAggregatorDTO){
        clientAggregator.setEscalationFourWeekCount(clientAggregatorDTO.getEscalationCount() + clientAggregator.getEscalationThreeWeekCount());
        clientAggregator.setLongDrivingFourWeekCount(clientAggregatorDTO.getLongDrivingCount() + clientAggregator.getLongDrivingThreeWeekCount());
        clientAggregator.setMostDrivenFourWeekCount(clientAggregatorDTO.getMostDrivenCount() + clientAggregator.getMostDrivenThreeWeekCount());
        clientAggregator.setWaitingFourWeekCount(clientAggregatorDTO.getWaitingCount() + clientAggregator.getWaitingThreeWeekCount());
        clientAggregator.setPlannedStatusFourWeekCount(clientAggregatorDTO.getPlannedStatusCount() + clientAggregator.getPlannedStatusThreeWeekCount());
        clientAggregator.setTotalPlannedProblemsFourWeekCount(clientAggregator.getWaitingFourWeekCount() + clientAggregator.getLongDrivingFourWeekCount() + clientAggregator.getMostDrivenFourWeekCount() + clientAggregator.getEscalationFourWeekCount() + clientAggregator.getPlannedStatusFourWeekCount());
        clientAggregator.setEscalationFourWeekTasks(ApplicationUtil.removingDuplicates(clientAggregatorDTO.getEscalationTasks(), clientAggregator.getEscalationThreeWeekTasks()));
        clientAggregator.setLongDrivingFourWeekTasks(ApplicationUtil.removingDuplicates(clientAggregatorDTO.getLongDrivingTasks(), clientAggregator.getLongDrivingThreeWeekTasks()));
        clientAggregator.setMostDrivenFourWeekTasks(ApplicationUtil.removingDuplicates(clientAggregatorDTO.getMostDrivenTasks(), clientAggregator.getMostDrivenThreeWeekTasks()));
        clientAggregator.setPlannedStatusFourWeekTasks(ApplicationUtil.removingDuplicates(clientAggregatorDTO.getPlannedStatusTasks(), clientAggregator.getPlannedStatusThreeWeekTasks()));
        clientAggregator.setWaitingFourWeekTasks(ApplicationUtil.removingDuplicates(clientAggregatorDTO.getWaitingTasks(), clientAggregator.getWaitingThreeWeekTasks()));
        clientAggregator.setTotalPlannedProblemsFourWeekTasks(ApplicationUtil.removingDuplicates(clientAggregatorDTO.getTotalPlannedProblemsTasks(), clientAggregator.getTotalPlannedProblemsThreeWeekTasks()));
        return  clientAggregator;

    }

    private ClientAggregatorDTO saveClientAggregator(List<Map<String, Object>> taskIds, Map<String, String> flsCredentials) {
        int longDrivingCount = 0;
        List<BigInteger> longDrivingTasks = new ArrayList<>();
        int mostDrivenCount = 0;
        List<BigInteger> mostDrivenTasks = new ArrayList<>();
        int escalationCount = 0;
        List<BigInteger> escalationTasks = new ArrayList<>();
        int waitingCount = 0;
        int plannedStatusCount = 0;
        List<BigInteger> waitingTasks = new ArrayList<>();
        List<BigInteger> plannedStatusTasks = new ArrayList<>();
        List<BigInteger> totalPlannedProblemsTasks = new ArrayList<>();
        for (Map<String, Object> taskMap : taskIds) {
            CallInfoRec callInfoRec = getCallInfo(taskMap, flsCredentials);
            if (callInfoRec.getFMExtID().equals("")) {
                escalationCount += 1;
                escalationTasks.add(BigInteger.valueOf(Long.valueOf(taskMap.get("taskId").toString())));
                totalPlannedProblemsTasks.addAll(escalationTasks);
            } else {
                if (callInfoRec.getDrivingTime() >= drivingTime) {
                    longDrivingCount += 1;
                    longDrivingTasks.add(BigInteger.valueOf(Long.valueOf(taskMap.get("taskId").toString())));
                    totalPlannedProblemsTasks.addAll(longDrivingTasks);
                }

                if (callInfoRec.getDistance() >= mostDistance) {
                    mostDrivenCount += 1;
                    mostDrivenTasks.add(BigInteger.valueOf(Long.valueOf(taskMap.get("taskId").toString())));
                    totalPlannedProblemsTasks.addAll(mostDrivenTasks);
                }

                if ((callInfoRec.getDuration() >= mostDuration) && (callInfoRec.getDistance() >= distance)) {
                    plannedStatusCount += 1;
                    plannedStatusTasks.add(BigInteger.valueOf(Long.valueOf(taskMap.get("taskId").toString())));
                    totalPlannedProblemsTasks.addAll(plannedStatusTasks);
                }

                Map<String, Object> taskMap1 = getPreviousCallByStaffId(callInfoRec, 14);
                if (taskMap1 != null) {
                    Date date = (Date) taskMap1.get("timeTo");
                    DateTime previousTaskTime = new DateTime(date).toDateTime(DateTimeZone.UTC).plusMinutes(callInfoRec.getDrivingTime());
                    DateTime currentTaskTime = new DateTime(callInfoRec.getArrival().toGregorianCalendar().getTime());
                    if (currentTaskTime.toDate().getTime() - previousTaskTime.toDate().getTime() != 0) {
                        waitingCount += 1;
                        waitingTasks.add(BigInteger.valueOf(Long.valueOf(taskMap.get("taskId").toString())));
                        totalPlannedProblemsTasks.addAll(waitingTasks);
                    }
                }
            }

        }

        ClientAggregatorDTO clientAggregatorDTO = new ClientAggregatorDTO(longDrivingCount, longDrivingTasks, mostDrivenCount, mostDrivenTasks, escalationCount, escalationTasks, waitingCount, plannedStatusCount, waitingTasks, plannedStatusTasks, totalPlannedProblemsTasks);

        return clientAggregatorDTO;

    }

    private List<Map> getTasksGroupByCitizen(Integer fromDays, Integer toDays, Long organizationId) {
        LocalDate upcomingMonday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        LocalDate fromWeekLater = upcomingMonday.plusDays(fromDays);
        LocalDate toWeekLater = upcomingMonday.plusDays(toDays);
        Date fromDate = Date.from(fromWeekLater.atStartOfDay(systemDefault()).toInstant());
        Date toDate = Date.from(toWeekLater.atStartOfDay(systemDefault()).toInstant());
        Criteria criteria = Criteria.where("dateFrom").gte(fromDate).and("dateTo").lte(toDate).and("isDeleted").is(false).and("taskOriginator").is("PRE_PLANNING")
                .and("taskStatus").ne("CANCELLED").and("relatedTaskId").exists(false).and("visitourId").ne(null).and("unitId").is(organizationId);
        String group = "{'$group':{'_id':'$citizenId', 'taskIds':{'$push':{'taskId':'$_id', 'visitourId':'$visitourId'}}}}";
        Document groupObject =Document.parse(group);
        Aggregation aggregation = newAggregation(
                match(criteria),
                new CustomAggregationOperation(groupObject)
        );
        logger.debug("Task Aggregator Query: " + aggregation.toString());

        // Result
        AggregationResults<Map> finalResult = mongoTemplate.aggregate(aggregation, Task.class, Map.class);
        return finalResult.getMappedResults();
    }

    private CallInfoRec getCallInfo(Map<String, Object> taskMap, Map<String, String> flsCredentials) {
        Map<String, Object> callInfoMetaData = new HashMap<>();
        callInfoMetaData.put("extID", taskMap.get("taskId").toString());
        callInfoMetaData.put("vtid", Integer.valueOf(taskMap.get("visitourId").toString()));

        return scheduler.getCallInfo(callInfoMetaData, flsCredentials);
    }

    private Map getPreviousCallByStaffId(CallInfoRec callInfoRec, Integer fromDays) {
        List<Long> staffIds = new ArrayList<>();
        LocalDate upcomingMonday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        LocalDate fromDaysLater = upcomingMonday.plusDays(fromDays);
        Date fromDate = Date.from(fromDaysLater.atStartOfDay(systemDefault()).toInstant());
        DateTime dateTime = new DateTime(fromDate);
        fromDate = dateTime.plusHours(5).plusMinutes(30).toDate();
        List<String> staffs = Arrays.asList(callInfoRec.getFMExtID().split("\\s*,\\s*"));
        for (String staff : staffs) {
            staffIds.add(Long.valueOf(staff));
        }
        Criteria criteria = Criteria.where("timeFrom").gte(fromDate).and("timeTo").lte(callInfoRec.getArrival().toGregorianCalendar().getTime()).and("isDeleted").is(false)
                .and("taskStatus").ne("CANCELLED").and("relatedTaskId").exists(false).and("assignedStaffIds").in(staffIds).and("visitourId").ne(null);
        Aggregation aggregation = newAggregation(
                match(criteria),
                sort(Sort.Direction.DESC, "timeTo")

        );
        logger.debug("Task Aggregator Query: " + aggregation.toString());

        // Result
        AggregationResults<Map> finalResult = mongoTemplate.aggregate(aggregation, Task.class, Map.class);
        if (finalResult.getMappedResults().isEmpty()) return null;
        return finalResult.getMappedResults().get(0);
    }

    private List<ClientAggregator> getCitizenDynamicData(Long organizationId) {

        return clientAggregatorMongoRepository.findAllByUnitId(organizationId);
    }

    private void sendDynamicReports(Long organizationId) {
        logger.info("creating citizen count and pushing to frontend");
        List<ClientAggregator> citizenDataGroupByUnit = getCitizenDynamicData(organizationId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("citizenDataList", citizenDataGroupByUnit);
        taskDynamicReportService.sendCitizenDynamicReports(organizationId, jsonObject);




    }

    @Scheduled(cron = "0 30 0 * * ?")
    public void updateExceptionCount() {
        logger.debug("Cron job running for calculate exception count");
        LocalDate localDate = (LocalDate.now().getDayOfWeek().equals(DayOfWeek.MONDAY)) ? LocalDate.now() : DateUtils.getDateForPreviousDay(LocalDate.now(), DayOfWeek.MONDAY);
        LocalDate fourWeekLater = localDate.plusDays(28);
        Date monday = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date fourWeekSunday = Date.from(fourWeekLater.atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<ClientException> clientExceptions = clientExceptionMongoRepository.getExceptionBetweenDates(monday, fourWeekSunday,new Sort(Sort.DEFAULT_DIRECTION,CITIZEN_ID));
        List<Long> citizenIds = clientExceptions.stream().map(clientException -> clientException.getClientId()).collect(Collectors.toList());
        List<ClientAggregator> clientAggregators = clientAggregatorMongoRepository.findByCitizenIdIn(citizenIds, new Sort(Sort.DEFAULT_DIRECTION, CITIZEN_ID));
        FourWeekFrequency fourWeekFrequency = FourWeekFrequency.getInstance();
        long citizenId = -1;
        List<Long> citizenUnitData = new ArrayList<>();
        for(ClientException clientException : clientExceptions){
            Optional<ClientAggregator> searchedObject = clientAggregators.stream().filter(clientAggregator1 -> clientAggregator1.getCitizenId() == clientException.getClientId()
                    && clientAggregator1.getUnitId() == clientException.getUnitId()).findFirst();

            ClientAggregator clientAggregator = (searchedObject.isPresent()) ? searchedObject.get() : new ClientAggregator(clientException.getUnitId(), clientException.getClientId());
            List<ClientExceptionCount> clientExceptionCounts = clientAggregator.getClientExceptionCounts();
            Optional<ClientExceptionCount> isClientExceptionCountExist = clientExceptionCounts.stream().filter(clientExceptionCount1 -> clientExceptionCount1.getExceptionTypeId().equals(clientException.getExceptionTypeId())).findFirst();
            ClientExceptionCount clientExceptionCount = (isClientExceptionCountExist.isPresent()) ? isClientExceptionCountExist.get() : new ClientExceptionCount(clientException.getExceptionTypeId());
            PerformCalculation performCalculation = (n) -> n + 1;
            if (citizenId == clientException.getClientId()) {
                if (!citizenUnitData.contains(clientException.getUnitId())) {
                    clientExceptionCount.resetValues();
                    citizenUnitData.add(clientException.getUnitId());
                }
            } else {
                citizenUnitData.clear();
                citizenUnitData.add(clientException.getUnitId());
                clientExceptionCount.resetValues();
                citizenId = clientException.getClientId();
            }

            clientExceptionService.updateCountInException(clientException, clientExceptionCount, performCalculation, fourWeekFrequency);
            if (!isClientExceptionCountExist.isPresent()) {
                clientExceptionCounts.add(clientExceptionCount);
            }
            clientAggregator.setClientExceptionCounts(clientExceptionCounts);

            if (!searchedObject.isPresent()) {
                clientAggregators.add(clientAggregator);
            }
        }
        save(clientAggregators);
    }

    @Scheduled(cron = "0 10 0 * * ?")
    public void updateUnhandledTask() {
        long startTime = System.currentTimeMillis();
        logger.info("Cron job starts to calculate unhndled tasks of citizen");
        LocalDate localDate = (LocalDate.now().getDayOfWeek().equals(DayOfWeek.MONDAY)) ? LocalDate.now() : DateUtils.getDateForPreviousDay(LocalDate.now(), DayOfWeek.MONDAY);
        LocalDate fourWeekLater = localDate.plusDays(28);
        Date monday = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date fourWeekSunday = Date.from(fourWeekLater.atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<Task> tasks = taskMongoRepository.getUnhandledTaskBetweenDates(monday, fourWeekSunday, new Sort(Sort.DEFAULT_DIRECTION, CITIZEN_ID));

        //TODO citizen ids repeating in list, need to use aggregation here
        List<Long> citizenIds = tasks.stream().map(task -> task.getCitizenId()).collect(Collectors.toList());
        List<ClientAggregator> citizenAggregators = clientAggregatorMongoRepository.findByCitizenIdIn(citizenIds, new Sort(Sort.DEFAULT_DIRECTION, CITIZEN_ID));
        FourWeekFrequency fourWeekFrequency = FourWeekFrequency.getInstance();
        List<Long> citizenUnitData = new ArrayList<>();
        long citizenId = -1;
        for (Task task : tasks) {
            Optional<ClientAggregator> searchedObject = citizenAggregators.stream().filter(clientAggregator1 -> clientAggregator1.getCitizenId() == task.getCitizenId()
                    && clientAggregator1.getUnitId() == task.getUnitId()).findFirst();
            ClientAggregator citizenAggregator = (searchedObject.isPresent()) ? searchedObject.get() : new ClientAggregator(task.getUnitId(), task.getCitizenId());
            UnhandledTaskCount unhandledTaskCount = (citizenAggregator.getUnhandledTaskCount() == null) ? new UnhandledTaskCount() : citizenAggregator.getUnhandledTaskCount();
            if (citizenId == task.getCitizenId()) {
                if (!citizenUnitData.contains(task.getUnitId())) {
                    unhandledTaskCount.resetValues();
                    citizenUnitData.add(task.getUnitId());
                }
            } else {
                citizenUnitData.clear();
                citizenUnitData.add(task.getUnitId());
                unhandledTaskCount.resetValues();
                citizenId = task.getCitizenId();
            }
            PerformCalculation performCalculation = (n) -> n + 1;
            updateTaskCount(unhandledTaskCount, task, fourWeekFrequency, performCalculation);
            citizenAggregator.setUnhandledTaskCount(unhandledTaskCount);

            if (!searchedObject.isPresent()) {
                citizenAggregators.add(citizenAggregator);
            }
        }
        save(citizenAggregators);
        logger.debug("Cron job finished after:: " + (System.currentTimeMillis() - startTime) + " ms");
    }

    private void updateTaskCount(UnhandledTaskCount unhandledTaskCount, Task task, FourWeekFrequency fourWeekFrequency, PerformCalculation performCalculation) {
        Set<BigInteger> taskIds;
        LocalDateTime taskStartTime = LocalDateTime.ofInstant(task.getTimeFrom().toInstant(), ZoneId.systemDefault());
        if (DateUtils.isTaskOnOrBetweenDates(taskStartTime,fourWeekFrequency.getStartOfDay(),fourWeekFrequency.getEndOfDay())) {
            unhandledTaskCount.setUnhandledTasksTodayCount(performCalculation.performCalculation(unhandledTaskCount.getUnhandledTasksTodayCount()));
            taskIds = unhandledTaskCount.getUnhandledTodayTasks();
            taskIds.add(task.getId());
            unhandledTaskCount.setUnhandledTodayTasks(taskIds);
        }
        if (DateUtils.isTaskOnOrBetweenDates(taskStartTime,fourWeekFrequency.getStartOfTomorrow(),fourWeekFrequency.getEndOfTomorrow())) {
            unhandledTaskCount.setUnhandledTasksTomorrowCount(performCalculation.performCalculation(unhandledTaskCount.getUnhandledTasksTomorrowCount()));
            taskIds = unhandledTaskCount.getUnhandledTomorrowTasks();
            taskIds.add(task.getId());
            unhandledTaskCount.setUnhandledTomorrowTasks(taskIds);
        }
        if (DateUtils.isTaskOnOrBetweenDates(taskStartTime,fourWeekFrequency.getStartOfDayAfterTomorrow(),fourWeekFrequency.getEndOfDayAfterTomorrow())) {
            unhandledTaskCount.setUnhandledTasksDayAfterTomorrowCount(performCalculation.performCalculation(unhandledTaskCount.getUnhandledTasksDayAfterTomorrowCount()));
            taskIds = unhandledTaskCount.getUnhandledDayAfterTomorrowTasks();
            taskIds.add(task.getId());
            unhandledTaskCount.setUnhandledDayAfterTomorrowTasks(taskIds);
        }
        if (DateUtils.isTaskOnOrBetweenDates(taskStartTime,fourWeekFrequency.getStartOfWeek(),fourWeekFrequency.getEndOfWeek())) {
            unhandledTaskCount.setUnhandledTasksOneWeekCount(performCalculation.performCalculation(unhandledTaskCount.getUnhandledTasksOneWeekCount()));
            taskIds = unhandledTaskCount.getUnhandledOneWeekTasks();
            taskIds.add(task.getId());
            unhandledTaskCount.setUnhandledOneWeekTasks(taskIds);
        }
        if (DateUtils.isTaskOnOrBetweenDates(taskStartTime,fourWeekFrequency.getStartOfWeek(),fourWeekFrequency.getEndOfSecondWeek())) {
            unhandledTaskCount.setUnhandledTasksTwoWeekCount(performCalculation.performCalculation(unhandledTaskCount.getUnhandledTasksTwoWeekCount()));
            taskIds = unhandledTaskCount.getUnhandledTwoWeekTasks();
            taskIds.add(task.getId());
            unhandledTaskCount.setUnhandledTwoWeekTasks(taskIds);
        }
        if (DateUtils.isTaskOnOrBetweenDates(taskStartTime,fourWeekFrequency.getStartOfWeek(),fourWeekFrequency.getEndOfThirdWeek())) {
            unhandledTaskCount.setUnhandledTasksThreeWeekCount(performCalculation.performCalculation(unhandledTaskCount.getUnhandledTasksThreeWeekCount()));
            taskIds = unhandledTaskCount.getUnhandledThreeWeekTasks();
            taskIds.add(task.getId());
            unhandledTaskCount.setUnhandledThreeWeekTasks(taskIds);
        }
        if (DateUtils.isTaskOnOrBetweenDates(taskStartTime,fourWeekFrequency.getStartOfWeek(),fourWeekFrequency.getEndOfThirdWeek())) {
            unhandledTaskCount.setUnhandledTasksFourWeekCount(performCalculation.performCalculation(unhandledTaskCount.getUnhandledTasksFourWeekCount()));
            taskIds = unhandledTaskCount.getUnhandledFourWeekTasks();
            taskIds.add(task.getId());
            unhandledTaskCount.setUnhandledFourWeekTasks(taskIds);
        }
    }

    @Scheduled(initialDelay = 100000, fixedDelay = 1000000)
    public void countCitizenTaskDemandsHoursAndTasks(){

        List<Long> organizations = schedulerRestClient.getAllOrganizationIds();
        List<ClientOrganizationIds> clientOrganizationIdsList = clientRestClient.getCitizenIdsByUnitIds(organizations);
        for(ClientOrganizationIds clientOrganizationIds : clientOrganizationIdsList ){
            ClientAggregator clientAggregator = clientAggregatorMongoRepository.findByUnitIdAndCitizenId(clientOrganizationIds.getOrganizationId(), clientOrganizationIds.getCitizenId());
            if(!Optional.ofNullable(clientAggregator).isPresent()) clientAggregator = new ClientAggregator();
            Map<String, Object> citizenWeeklyDemandData = calculateWeeklyDemands(clientOrganizationIds.getCitizenId(), clientOrganizationIds.getOrganizationId());
            Map<String, Object> citizenMonthlyDemandData = calculateMonthlyDemands(clientOrganizationIds.getCitizenId(), clientOrganizationIds.getOrganizationId());
            Map<String, Object> citizenDailyDemandData = calculateDailyDemands(clientOrganizationIds.getCitizenId(), clientOrganizationIds.getOrganizationId());
            Long visitatedMinutes = (Long) citizenWeeklyDemandData.get("minutes") + (Long) citizenMonthlyDemandData.get("minutes") + (Long) citizenDailyDemandData.get("minutes");
            Float visitatedTasks = (Float) citizenWeeklyDemandData.get("tasks") + (Float) citizenMonthlyDemandData.get("tasks")  + (Float) citizenDailyDemandData.get("tasks");
            Duration weekDuration = Duration.ofMinutes(visitatedMinutes);
            Duration monthDuration = Duration.ofMinutes(visitatedMinutes*4);
            clientAggregator.setVisitatedHoursPerWeek(weekDuration.toHours());
            clientAggregator.setVisitatedMinutesPerWeek(weekDuration.toMinutes()-Duration.ofHours(weekDuration.toHours()).toMinutes());
            clientAggregator.setVisitatedTasksPerWeek(visitatedTasks);
            clientAggregator.setVisitatedHoursPerMonth(monthDuration.toHours());
            clientAggregator.setVisitatedMinutesPerMonth(monthDuration.toMinutes()-Duration.ofHours(monthDuration.toHours()).toMinutes());
            clientAggregator.setVisitatedTasksPerMonth(visitatedTasks*4);
            clientAggregator.setCitizenId(clientOrganizationIds.getCitizenId());
            clientAggregator.setUnitId(clientOrganizationIds.getOrganizationId());
            save(clientAggregator);
        }


    }


    private Map<String, Object> calculateWeeklyDemands(Long clientId, Long unitId){
        long weekDayMinutes = 0;
        float weekDayTasks = 0;
        long weekEndMinutes = 0;
        float weekEndTasks = 0;
        Map<String, Object> citizenDemandData = new HashMap<String, Object>();
        List<TaskDemand> taskDemands = taskDemandMongoRepository.findAllByCitizenIdAndUnitIdAndRecurrencePattern(clientId,unitId, TaskDemand.RecurrencePattern.WEEKLY);
        for(TaskDemand taskDemand : taskDemands){
            int weekDayFrequency = 0;
            int weekEndFrequency = 0;
            long weekDayTaskDemandMinutes = 0;
            float weekDayTaskDemandTasks = 0;
            long weekEndTaskDemandMinutes = 0;
            float weekEndTaskDemandTasks = 0;
            if (taskDemand.getWeekdayFrequency() != null) {
                switch (taskDemand.getWeekdayFrequency()) {
                    case ONE_WEEK:
                        weekDayFrequency = 1;
                        break;
                    case TWO_WEEK:
                        weekDayFrequency = 2;
                        break;
                    case THREE_WEEK:
                        weekDayFrequency = 3;
                        break;
                    case FOUR_WEEK:
                        weekDayFrequency = 4;
                        break;
                }
                for (TaskDemandVisit taskDemandVisit : taskDemand.getWeekdayVisits()) {
                    weekDayTaskDemandMinutes += taskDemandVisit.getVisitCount() * taskDemandVisit.getVisitDuration();
                    weekDayTaskDemandTasks += taskDemandVisit.getVisitCount();
                }
                if(weekDayTaskDemandMinutes != 0)  weekDayTaskDemandMinutes =  Math.round(weekDayTaskDemandMinutes / weekDayFrequency) ;
                if(weekDayTaskDemandTasks != 0) weekDayTaskDemandTasks = BigDecimal.valueOf(weekDayTaskDemandTasks / weekDayFrequency).setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();
                weekDayMinutes += weekDayTaskDemandMinutes;
                weekDayTasks += weekDayTaskDemandTasks;

            }
            if (taskDemand.getWeekendFrequency() != null){
                switch (taskDemand.getWeekendFrequency()){
                    case ONE_WEEK: weekEndFrequency = 1;
                        break;
                    case TWO_WEEK: weekEndFrequency = 2;
                        break;
                    case THREE_WEEK: weekEndFrequency = 3;
                        break;
                    case FOUR_WEEK: weekEndFrequency = 4;
                        break;
                }
                for(TaskDemandVisit  taskDemandVisit : taskDemand.getWeekendVisits()){
                    weekEndTaskDemandMinutes += taskDemandVisit.getVisitCount()*taskDemandVisit.getVisitDuration();
                    weekEndTaskDemandTasks += taskDemandVisit.getVisitCount() ;
                }
                if(weekEndTaskDemandMinutes != 0)  weekEndTaskDemandMinutes = Math.round(weekEndTaskDemandMinutes/weekEndFrequency);
                if(weekEndTaskDemandTasks != 0) weekEndTaskDemandTasks = BigDecimal.valueOf(weekEndTaskDemandTasks/weekEndFrequency).setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();
                weekEndMinutes += weekEndTaskDemandMinutes;
                weekEndTasks += weekEndTaskDemandTasks;
            }
        }

        citizenDemandData.put("minutes", weekDayMinutes+weekEndMinutes);
        citizenDemandData.put("tasks", weekEndTasks+weekDayTasks);
        return citizenDemandData;
    }

    private Map<String, Object> calculateMonthlyDemands(Long clientId, Long unitId){
        long monthDayMinutes = 0;
        float monthDayTasks = 0;
        Map<String, Object> citizenDemandData = new HashMap<String, Object>();
        List<TaskDemand> taskDemands = taskDemandMongoRepository.findAllByCitizenIdAndUnitIdAndRecurrencePattern(clientId,unitId, TaskDemand.RecurrencePattern.MONTHLY);
        for(TaskDemand taskDemand : taskDemands){
            int weekDayFrequency = 0;
            int weekEndFrequency = 0;
            long monthDayTaskDemandMinutes = 0;
            float monthDayTaskDemandTasks = 0;
            if (taskDemand.getMonthlyFrequency() != null) {

                for (TaskDemandVisit taskDemandVisit : taskDemand.getWeekdayVisits()) {
                    //First case of Monthly frequency i.e, every ## weekday of every ## month
                    if(taskDemand.getMonthlyFrequency().getWeekdayCount() > 0){
                        monthDayTaskDemandMinutes += taskDemand.getMonthlyFrequency().getWeekdayCount() * taskDemandVisit.getVisitDuration();
                        monthDayTaskDemandTasks += taskDemand.getMonthlyFrequency().getWeekdayCount();

                            //Second case of Monthly frequency i.e, every Monday of every ## month
                    }else if(taskDemand.getMonthlyFrequency().getWeekdayCount() == 0 && taskDemand.getMonthlyFrequency().getDayOfWeek() != null && taskDemand.getMonthlyFrequency().getWeekOfMonth() ==null){
                        monthDayTaskDemandMinutes += 4 * taskDemandVisit.getVisitDuration(); // As we are considering month as 4 week, if it is repeating every Monday of month, so it occurs once in a week.
                        monthDayTaskDemandTasks += 4 ;

                        //Third case of Monthly frequency i.e, every First Monday of every ## month
                    } else if(taskDemand.getMonthlyFrequency().getWeekdayCount() == 0 && taskDemand.getMonthlyFrequency().getDayOfWeek() != null && taskDemand.getMonthlyFrequency().getWeekOfMonth() !=null){
                        monthDayTaskDemandMinutes += 1 * taskDemandVisit.getVisitDuration(); // As we are considering month as 4 week, if it is repeating First Monday of month, so it occurs 1/4 in a week.
                        monthDayTaskDemandTasks += 1;
                    }

                }
                if(monthDayTaskDemandMinutes != 0)  monthDayTaskDemandMinutes =  Math.round(monthDayTaskDemandMinutes / (taskDemand.getMonthlyFrequency().getMonthFrequency()*4)) ;
                if(monthDayTaskDemandTasks != 0) monthDayTaskDemandTasks =  BigDecimal.valueOf( monthDayTaskDemandTasks / (taskDemand.getMonthlyFrequency().getMonthFrequency()*4) ).setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();
                monthDayMinutes += monthDayTaskDemandMinutes;
                monthDayTasks += monthDayTaskDemandTasks;
            }

        }
        citizenDemandData.put("minutes", monthDayMinutes);
        citizenDemandData.put("tasks", monthDayTasks);
        return citizenDemandData;
    }

    public List<ClientExceptionCountWrapper> getClientAggregateData(long unitId){
        long sizeOfClientAggregateRecords = clientAggregatorMongoRepository.getCountOfAggregateData(unitId);
        int skip = 0;
        List<ClientAggregator> clientAggregators = new ArrayList<>((int)sizeOfClientAggregateRecords);
        if(sizeOfClientAggregateRecords > MONOGDB_QUERY_RECORD_LIMIT){
            do {
                clientAggregators.addAll(clientAggregatorMongoRepository.getAggregateDataByUnit(unitId,skip,MONOGDB_QUERY_RECORD_LIMIT));
                skip+= sizeOfClientAggregateRecords;
            } while (skip <= sizeOfClientAggregateRecords);
        } else {
            clientAggregators.addAll(clientAggregatorMongoRepository.getAggregateDataByUnit(unitId,skip,MONOGDB_QUERY_RECORD_LIMIT));
        }
        List<Long> citizenIds = clientAggregators.stream().map(clientAggregator -> clientAggregator.getCitizenId()).collect(Collectors.toList());
        List<Client> clients = clientRestClient.getCitizensByIdsInList(citizenIds);
        List<ClientExceptionCountWrapper> clientExceptionCountWrappers = new ArrayList<>();

        int clientAggregatorPos = 0;
        ObjectMapper objectMapper = new ObjectMapper();
        for (Client client : clients){
            ClientAggregator clientAggregator = clientAggregators.get(clientAggregatorPos);
            if(Optional.ofNullable(clientAggregator).isPresent() && clientAggregator.getCitizenId() == client.getId()){
                ClientExceptionCountWrapper clientExceptionCountWrapper = objectMapper.convertValue(client,ClientExceptionCountWrapper.class);
                clientExceptionCountWrapper.setClientExceptionCounts(clientAggregator.getClientExceptionCounts());
                clientExceptionCountWrappers.add(clientExceptionCountWrapper);
            }
            clientAggregatorPos++;
        }
        return clientExceptionCountWrappers;

    }




    private Map<String, Object> calculateDailyDemands(Long clientId, Long unitId){
        long minutes = 0;
        float tasks = 0;

        Map<String, Object> citizenDemandData = new HashMap<String, Object>();
        List<TaskDemand> taskDemands = taskDemandMongoRepository.findAllByCitizenIdAndUnitIdAndRecurrencePattern(clientId,unitId, TaskDemand.RecurrencePattern.DAILY);
        for(TaskDemand taskDemand : taskDemands){
            int tasksCount = randomDateGeneratorService.countRandomDatesForDailyPattern(taskDemand);
            long taskDemandMinutes = 0;
            float taskDemandTasks = 0;
            for (TaskDemandVisit taskDemandVisit : taskDemand.getWeekdayVisits()) {
                taskDemandMinutes += taskDemandVisit.getVisitDuration();
                taskDemandTasks += 1;
            }

            minutes +=  Math.round((tasksCount*taskDemandMinutes) / 4) ;
            tasks +=  BigDecimal.valueOf(( tasksCount*taskDemandTasks) / 4 ).setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();

        }

        citizenDemandData.put("minutes", minutes);
        citizenDemandData.put("tasks", tasks);
        return citizenDemandData;
    }


}