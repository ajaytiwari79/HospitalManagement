package com.kairos.service.scheduler;

import com.kairos.config.env.EnvConfig;
import com.kairos.dto.scheduler.KairosSchedulerExecutorDTO;
import com.kairos.dto.scheduler.KairosSchedulerLogsDTO;
import com.kairos.enums.scheduler.Result;
import com.kairos.dto.scheduler.kafka.producer.KafkaProducer;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.commons.utils.DateUtils;
import com.kairos.utils.external_plateform_shift.Transstatus;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;

import static com.kairos.constants.AppConstants.*;
import static com.kairos.constants.AppConstants.API_KMD_CARE_URL;
import static com.kairos.constants.AppConstants.IMPORT_KMD_TIME_SLOTS;

@Service
public class IntegrationJobsExecutorService {

    @Inject
    private EnvConfig envConfig;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    private static Logger logger = LoggerFactory.getLogger(IntegrationJobsExecutorService.class);

    @Inject
    private KafkaProducer kafkaProducer;

    public void runJob(KairosSchedulerExecutorDTO job) {

        String plainClientCredentials = "cluster:cluster";
        String base64ClientCredentials = new String(Base64.encodeBase64(plainClientCredentials.getBytes()));
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
        headers.add("Authorization", "Basic " + base64ClientCredentials);
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        String importShiftStatusXMLURI = envConfig.

                getCarteServerHost()+KETTLE_TRANS_STATUS;
        //   String startDate = DateFormatUtils.format(DateUtil.getCurrentDate(), "yyyy-MM-dd");
        //     String endDate = DateFormatUtils.format(DateUtil.addWeeksInDate(DateUtil.getCurrentDate(), 5), "yyyy-MM-dd");
        //  String startDate = DateFormatUtils.format(controlPanel.getStartDate(), "yyyy-MM-dd");
        //  String endDate = DateFormatUtils.format(controlPanel.getEndDate(), "yyyy-MM-dd");
        Long workplaceId = Long.valueOf(String.valueOf("15"));
        if(job.getUnitId() != null){
            Organization organization = organizationGraphRepository.findOne(job.getUnitId());
            if(organization.getExternalId() != null) workplaceId = Long.valueOf(organization.getExternalId());
        }
        String importShiftURI = "";
        int weeks = 35;
        String uniqueKey = job.getIntegrationSettingsDTO().getUniqueKey();
        logger.info("uniqueKey----> "+uniqueKey);
        RestTemplate restTemplate = new RestTemplate();
        switch(uniqueKey){
            case IMPORT_TIMECARE_SHIFTS:
                logger.info("!!===============Hit to carte server from Kairos==============!!");
                importShiftURI = envConfig.getCarteServerHost()+KETTLE_EXECUTE_TRANS+IMPORT_TIMECARE_SHIFTS_PATH+"&intWorkPlaceId="+workplaceId+"&weeks="+weeks+"&jobId="+job.getId();
                logger.info("importShiftURI----> "+importShiftURI);
               // Date started = DateUtil.getCurrentDate();
                LocalDateTime started = LocalDateTime.now();
                ResponseEntity<String> importResult = restTemplate.exchange(importShiftURI, HttpMethod.GET, entity, String.class);
                if (importResult.getStatusCodeValue() == 200) {
                    ResponseEntity<String> resultStatusXml = restTemplate.exchange(importShiftStatusXMLURI, HttpMethod.GET, entity, String.class);
                    LocalDateTime stopped = LocalDateTime.now();
                    try {
                        JAXBContext jaxbContext = JAXBContext.newInstance(Transstatus.class);
                        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                        StringReader reader = new StringReader(resultStatusXml.getBody());
                        Transstatus transstatus = (Transstatus) jaxbUnmarshaller.unmarshal(reader);
                        logger.info("trans status---> " + transstatus.getId());
                        String loggingString = StringEscapeUtils.escapeHtml4(transstatus.getLogging_string());
                        loggingString = loggingString.substring(loggingString.indexOf("[CDATA[")+7,loggingString.indexOf("]]&gt"));
                        byte[] bytes = Base64.decodeBase64(loggingString);
                        GZIPInputStream zi = null;
                        String unzipped;
                        try {
                            zi = new GZIPInputStream(new ByteArrayInputStream(bytes));
                            unzipped = IOUtils.toString(zi);
                        } finally {
                            IOUtils.closeQuietly(zi);
                        }




                        KairosSchedulerLogsDTO logs = new KairosSchedulerLogsDTO(Result.SUCCESS,unzipped,job.getId(),job.getUnitId(),DateUtils.getMillisFromLocalDateTime(started),DateUtils.getMillisFromLocalDateTime(stopped),job.getJobSubType());
                        if(transstatus.getResult().getNr_errors() > 0) {
                            logs.setResult(Result.ERROR);
                        }
                        kafkaProducer.pushToSchedulerLogsQueue(logs);

                    } catch (JAXBException exception) {
                        logger.info("trans status---exception > " + exception);
                    }
                    catch(IOException exception){
                        logger.info("exception while logging job details-- > " + exception);
                    }

                }
                break;
            case IMPORT_KMD_CITIZEN:
                importShiftURI = envConfig.getServerHost()+KMD_CARE_CITIZEN_URL+job.getUnitId();
                restTemplate.exchange(importShiftURI, HttpMethod.GET, entity, String.class);
                break;
            case IMPORT_KMD_CITIZEN_NEXT_TO_KIN:
                importShiftURI = envConfig.getServerHost()+API_KMD_CARE_CITIZEN_RELATIVE_DATA;
                restTemplate.exchange(importShiftURI, HttpMethod.GET, entity, String.class);
                break;
            case IMPORT_KMD_CITIZEN_GRANTS:
                importShiftURI = envConfig.getServerHost()+API_KMD_CARE_CITIZEN_GRANTS;
                restTemplate.exchange(importShiftURI, HttpMethod.GET, entity, String.class);
                break;
            case IMPORT_KMD_STAFF_AND_WORKING_HOURS:
                importShiftURI=envConfig.getServerHost()+API_KMD_CARE_URL+job.getUnitId()+"/getShifts/"+job.getFilterId();
                restTemplate.exchange(importShiftURI, HttpMethod.GET, entity, String.class);
                break;
            case IMPORT_KMD_TASKS:
                importShiftURI=envConfig.getServerHost()+API_KMD_CARE_URL+job.getUnitId()+"/getTasks/"+job.getFilterId();
                restTemplate.exchange(importShiftURI, HttpMethod.GET, entity, String.class);
                break;
            case IMPORT_KMD_TIME_SLOTS:
                importShiftURI=envConfig.getServerHost()+API_KMD_CARE_URL+job.getUnitId()+"/getTimeSlots";
                restTemplate.exchange(importShiftURI, HttpMethod.GET, entity, String.class);
                break;

        }

    }

}
