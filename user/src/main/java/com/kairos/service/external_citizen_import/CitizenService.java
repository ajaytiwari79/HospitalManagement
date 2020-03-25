package com.kairos.service.external_citizen_import;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.utils.CommonsExceptionUtil;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.task.order.OrderGrant;
import com.kairos.dto.user.patient.PatientRelative;
import com.kairos.dto.user.patient.PatientWrapper;
import com.kairos.dto.user.staff.AvailableContacts;
import com.kairos.dto.user.staff.ColumnResource;
import com.kairos.dto.user.staff.ImportShiftDTO;
import com.kairos.dto.user.staff.RelativeContacts;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.client.Client;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.Unit;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetail;
import com.kairos.persistence.model.system_setting.SystemLanguage;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.OrganizationServiceRepository;
import com.kairos.persistence.repository.organization.UnitGraphRepository;
import com.kairos.persistence.repository.organization.time_slot.TimeSlotGraphRepository;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.client.ClientGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.staff.PositionGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.rest_client.TaskDemandRestClient;
import com.kairos.rest_client.TaskServiceRestClient;
import com.kairos.service.client.ExternalClientService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.service.organization.OrganizationServiceService;
import com.kairos.service.organization.TimeSlotService;
import com.kairos.service.staff.StaffCreationService;
import com.kairos.service.staff.StaffService;
import com.kairos.service.system_setting.SystemLanguageService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.util.*;

import static com.kairos.commons.utils.ObjectMapperUtils.jsonStringToObject;
import static com.kairos.constants.UserMessagesConstants.MESSAGE_CITIZEN_STAFF_ALREADYEXIST;
import static com.kairos.constants.UserMessagesConstants.MESSAGE_ORGANIZATION_ID_NOTFOUND;


/**
 * Created by oodles on 25/4/17.
 */
@Transactional
@Service
public class CitizenService {

    private static final Logger logger = LoggerFactory.getLogger(CitizenService.class);
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer";
    public static final String KMD_NEXUS_EXTERNAL_ID = "kmdNexusExternalId";
    public static final String PARAMETERS = "parameters";
    public static final String PATHWAY_TYPE_ID = "pathwayTypeId";
    public static final String CHILDREN = "children";
    public static final String GRANT_ID = "grantId";
    public static final String SUPPLIER = "supplier";

    @Inject
    private ClientGraphRepository clientGraphRepository;

    @Inject
    private CountryGraphRepository countryGraphRepository;

    @Inject
    private OrganizationServiceService organizationServiceService;

    @Inject
    private OrganizationServiceRepository organizationServiceRepository;

    @Inject
    private TimeSlotGraphRepository timeSlotGraphRepository;

    @Inject
    private UnitGraphRepository unitGraphRepository;

    @Inject
    private OrganizationService organizationService;
    @Inject
    private ExternalClientService clientService;

    @Inject
    private AuthService authService;

    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private UserGraphRepository userGraphRepository;
    @Inject
    private StaffService staffService;
    @Inject
    private PositionGraphRepository positionGraphRepository;
    @Autowired
    TaskServiceRestClient taskServiceRestClient;
    @Autowired
    TaskDemandRestClient taskDemandRestClient;
    @Inject
    private TimeSlotService timeSlotService;
    @Inject
    private StaffCreationService staffCreationService;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private SystemLanguageService systemLanguageService;

    /**
     * This method is used to import Citizen from KMD Nexus.
     *
     * @return
     */
    public String getCitizensFromKMD(Long unitId) {
        try {
            Unit unit = unitGraphRepository.findOne(unitId);
            RestTemplate loginTemplate = new RestTemplate();
            HttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
            HttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
            loginTemplate.getMessageConverters().add(formHttpMessageConverter);
            loginTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            loginTemplate.getMessageConverters().add(stringHttpMessageConverter);
            HttpHeaders headers = new HttpHeaders();
            headers.add(AUTHORIZATION, BEARER + " " + AppConstants.KMD_NEXUS_ACCESS_TOKEN);
            HttpEntity<String> headersElements = new HttpEntity<>(PARAMETERS, headers);
            ResponseEntity<String> filterResponse = loginTemplate.exchange(AppConstants.KMD_NEXUS_PATIENT_FILTER, HttpMethod.GET, headersElements, String.class);
            JSONObject citizensByFilter = new JSONObject(filterResponse.getBody());
            JSONArray pages = citizensByFilter.getJSONArray("pages");
            for (int j = 0; j < pages.length(); j++) {
                String filterUrl = pages.getJSONObject(j).getJSONObject("_links").getJSONObject("patientData").getString("href");
                ResponseEntity<String> patientListResponse = loginTemplate.exchange(filterUrl, HttpMethod.GET, headersElements, String.class);
                JSONArray patients = new JSONArray(patientListResponse.getBody());
                JSONObject patientsObject = new JSONObject();
                patientsObject.put("patientWrappers", patients);
                createCitizenFromExternalService(unit, loginTemplate, headersElements, patients);
            }
            return "success";
        } catch (Exception exception) {
            return exception.getMessage();
        }
    }

    private void createCitizenFromExternalService(Unit unit, RestTemplate loginTemplate, HttpEntity<String> headersElements, JSONArray patients) {
        for (int k = 0; k < patients.length(); k++) {
            String patientUrl = patients.getJSONObject(k).getJSONObject("_links").getJSONObject("self").getString("href");
            ResponseEntity<String> patientResponse = loginTemplate.exchange(patientUrl, HttpMethod.GET, headersElements, String.class);
            PatientWrapper patientWrapper = jsonStringToObject(patientResponse.getBody().toString(), PatientWrapper.class);
            clientService.createCitizenFromExternalService(patientWrapper, unit.getId());
        }
    }


    /**
     * This method is used to create Citizen's next to kin details imported from KMD Nexus
     */
    public void getCitizensRelativeContact() {
        try {
            List<Map<String, Object>> citizenListObject = clientGraphRepository.findAllCitizensFromKMD();
            List<Map<String, Object>> citizens = new ArrayList<>();
            for (Map<String, Object> map : citizenListObject) {
                citizens.add((Map<String, Object>) map.get("data"));
            }
            RestTemplate loginTemplate = new RestTemplate();
            HttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
            HttpMessageConverter stringHttpMessageConverterNew = new StringHttpMessageConverter();
            loginTemplate.getMessageConverters().add(formHttpMessageConverter);
            loginTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            loginTemplate.getMessageConverters().add(stringHttpMessageConverterNew);
            HttpHeaders headers = new HttpHeaders();
            headers.add(AUTHORIZATION, BEARER + " " + AppConstants.KMD_NEXUS_ACCESS_TOKEN);
            HttpEntity<String> headersElements = new HttpEntity<>(PARAMETERS, headers);
            Client client = null;
            for (Map<String, Object> map : citizens) {
                logger.info("citizen----kmdNexusExternalId------> {}", map.get(KMD_NEXUS_EXTERNAL_ID));
                client = clientGraphRepository.findByKmdNexusExternalId(map.get(KMD_NEXUS_EXTERNAL_ID).toString());
                logger.info("client-------------> {}", client);
                ResponseEntity<String> responseEntity = loginTemplate.exchange(String.format(AppConstants.KMD_NEXUS_PATIENT_RELATIVE_CONTACT, map.get(KMD_NEXUS_EXTERNAL_ID).toString()), HttpMethod.GET, headersElements, String.class);
                AvailableContacts availableContacts = jsonStringToObject(responseEntity.getBody(), AvailableContacts.class);
                processRelativeContacts(loginTemplate, headersElements, client, availableContacts);
            }
        } catch (Exception exception) {
            logger.warn("exception while adding citizen relative data---->{} ", exception.getMessage());
        }
    }

    private void processRelativeContacts(RestTemplate loginTemplate, HttpEntity<String> headersElements, Client client, AvailableContacts availableContacts) {
        for (RelativeContacts relativeContacts : availableContacts.getRelativeContacts()) {
            String relativeContactUrl = relativeContacts.get_links().getSelf().getHref();
            ResponseEntity<String> relativeContactResponse = loginTemplate.exchange(relativeContactUrl, HttpMethod.GET, headersElements, String.class);
            Unit unit = unitGraphRepository.findByName(AppConstants.KMD_NEXUS_ORGANIZATION);
            PatientRelative patientRelative = jsonStringToObject(relativeContactResponse.getBody(), PatientRelative.class);
            clientService.addClientRelativeDetailsFromExternalService(patientRelative, client, unit.getId());
        }
    }


    public void getShifts(Long filterId, Long unitId) {
        RestTemplate loginTemplate = new RestTemplate();
        HttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
        HttpMessageConverter stringHttpMessageConverterNew = new StringHttpMessageConverter();
        loginTemplate.getMessageConverters().add(formHttpMessageConverter);
        loginTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        loginTemplate.getMessageConverters().add(stringHttpMessageConverterNew);

        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION, BEARER + " " + AppConstants.KMD_NEXUS_ACCESS_TOKEN);
        Map map = new HashMap<String, String>();
        map.put("Content-Type", "application/json");
        HttpEntity<String> headersElements = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = loginTemplate.exchange(String.format(AppConstants.KMD_NEXUS_CALENDAR_STAFFS_SHIFT_FILTER, filterId), HttpMethod.POST, headersElements, String.class);
        JSONObject jsonObject = new JSONObject(responseEntity.getBody());
        ColumnResource columnResource = jsonStringToObject(jsonObject.get("columnResource").toString(), ColumnResource.class);
        for (ImportShiftDTO shift : columnResource.getShifts()) {
            String staffExternalId = shift.getEventResource().getResourceId();
            staffExternalId = staffExternalId.substring(staffExternalId.indexOf("PROFESSIONAL:") + 13);
            logger.info("Staff External Id----> {}", staffExternalId);
            ResponseEntity<String> staffResponseEntity = loginTemplate.exchange(String.format(AppConstants.KMD_NEXUS_STAFFS_DETAILS, staffExternalId), HttpMethod.GET, headersElements, String.class);
            StaffPersonalDetail staffDTO = jsonStringToObject(staffResponseEntity.getBody(), StaffPersonalDetail.class);
            Staff staff = createStaffFromKMD(unitId, staffDTO);
            taskServiceRestClient.createTaskFromKMD(staff.getId(), shift, unitId);
            logger.info("staff DTO---------> {}", staffDTO.getLastName());

        }
    }

    public Staff createStaffFromKMD(long unitId, StaffPersonalDetail payload) {
        Staff staff = staffGraphRepository.findByKmdExternalId(payload.getId()).orElse(new Staff());
        Organization organization = organizationGraphRepository.findById(unitId).orElseThrow(() -> new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_ORGANIZATION_ID_NOTFOUND, unitId)));
        staff.setFirstName(payload.getFirstName());
        staff.setLastName(payload.getLastName());
        staff.setCurrentStatus(payload.getCurrentStatus());
        staff.setEmail(payload.getContactDetail().getPrivateEmail());
        staff.setKmdExternalId(payload.getId());
        User user = userGraphRepository.findByKmdExternalId(payload.getId()).orElse(new User());
        SystemLanguage systemLanguage = systemLanguageService.getDefaultSystemLanguageForUnit(unitId);
        user.setUserLanguage(systemLanguage);
        user.setEmail(staff.getEmail());
        user.setFirstName(staff.getFirstName());
        user.setLastName(staff.getLastName());
        userGraphRepository.save(user);
        Staff alreadyExistStaff = staffGraphRepository.getByUser(user.getId());
        if (alreadyExistStaff != null)
            exceptionService.dataNotFoundByIdException(MESSAGE_CITIZEN_STAFF_ALREADYEXIST);
        staff = staffCreationService.updateStaffDetailsOnCreationOfStaff(user, staff, Long.valueOf("1162"), organization);
        if ((staff.getId() == null) || (positionGraphRepository.findPosition(organization.getId(), staff.getId()) == null)) {
            positionGraphRepository.createPositions(organization.getId(), Arrays.asList(staff.getId()), organization.getId());
        }
        return staffGraphRepository.save(staff);
    }

    public void getTimeSlots(Long unitId) {
        RestTemplate loginTemplate = new RestTemplate();
        HttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
        HttpMessageConverter stringHttpMessageConverterNew = new StringHttpMessageConverter();
        loginTemplate.getMessageConverters().add(formHttpMessageConverter);
        loginTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        loginTemplate.getMessageConverters().add(stringHttpMessageConverterNew);

        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION, BEARER + " " + AppConstants.KMD_NEXUS_ACCESS_TOKEN);
        Map map = new HashMap<String, String>();
        map.put("Content-Type", "application/json");
        HttpEntity<String> headersElements = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = loginTemplate.exchange(String.format(AppConstants.KMD_NEXUS_GET_TIME_SLOTS), HttpMethod.GET, headersElements, String.class);
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray(responseEntity.getBody());
        jsonObject.put("kmdTimeSlotDTOList", jsonArray);
        timeSlotService.updateTimeSlotType(unitId, false);
    }


}
