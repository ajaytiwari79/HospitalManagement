package com.kairos.service.external_citizen_import;

import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.task.order.OrderGrant;
import com.kairos.dto.user.patient.CurrentElements;
import com.kairos.dto.user.patient.PatientGrant;
import com.kairos.dto.user.patient.PatientRelative;
import com.kairos.dto.user.patient.PatientWrapper;
import com.kairos.dto.user.staff.AvailableContacts;
import com.kairos.dto.user.staff.ColumnResource;
import com.kairos.dto.user.staff.ImportShiftDTO;
import com.kairos.dto.user.staff.RelativeContacts;
import com.kairos.dto.user.staff.client.CitizenSupplier;
import com.kairos.dto.user.visitation.RepetitionType;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.client.Client;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationBuilder;
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
    @Inject private StaffCreationService staffCreationService;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private SystemLanguageService systemLanguageService;
    /**
     * This method is used to import Citizen from KMD Nexus.
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
                for (int k = 0; k < patients.length(); k++) {
                    String patientUrl = patients.getJSONObject(k).getJSONObject("_links").getJSONObject("self").getString("href");
                    ResponseEntity<String> patientResponse = loginTemplate.exchange(patientUrl, HttpMethod.GET, headersElements, String.class);
                    PatientWrapper patientWrapper = jsonStringToObject(patientResponse.getBody().toString(), PatientWrapper.class);
                    clientService.createCitizenFromExternalService(patientWrapper, unit.getId());
                }
            }
            return "success";
        } catch (Exception exception) {
            return exception.getMessage();
        }
    }


    /**
     * This method is used to import Citizen's demands from KMD.
     */
    public void getCitizenGrantsFromKMD() {
        List<Map<String, Object>> citizenListObject = clientGraphRepository.findAllCitizensFromKMD();
        List<Map<String, Object>> citizens = new ArrayList<>();
        List<Country> countries = countryGraphRepository.findByName("Denmark");
        if (countries.isEmpty()) {
            return;
        }
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


        HttpEntity<String> headersElements = new HttpEntity<String>(PARAMETERS, headers);
        com.kairos.persistence.model.organization.services.OrganizationService service = null;
        com.kairos.persistence.model.organization.services.OrganizationService subService = null;
        Client client = null;

        for (Map<String, Object> map : citizens) {
            authService.dokmdAuth();

            client = clientGraphRepository.findByKmdNexusExternalId(map.get(KMD_NEXUS_EXTERNAL_ID).toString());
            Unit unit = (Unit) clientGraphRepository.getClientOrganizationIdList(client.getId()).get(0);

            ResponseEntity<String> responseEntity = loginTemplate.exchange(String.format(AppConstants.KMD_NEXUS_PATIENT_PATHWAY_PREFERENCE, map.get(KMD_NEXUS_EXTERNAL_ID).toString()), HttpMethod.GET, headersElements, String.class);
            JSONObject patientPathwayObject = new JSONObject(responseEntity.getBody());
            JSONArray citizenPathwayArray = patientPathwayObject.getJSONArray("CITIZEN_PATHWAY");
            for (int cp = 0; cp < citizenPathwayArray.length(); cp++) {
                int pathwayFilterId = citizenPathwayArray.getJSONObject(cp).getInt("id");
                if (pathwayFilterId != 413) continue;
                try {
                    ResponseEntity<String> pathwayPreferenceResponseEntity = loginTemplate.exchange(String.format(AppConstants.KMD_NEXUS_PATIENT_PATHWAY, map.get(KMD_NEXUS_EXTERNAL_ID).toString()) + pathwayFilterId, HttpMethod.GET, headersElements, String.class);
                    JSONArray patientPathways = new JSONArray(pathwayPreferenceResponseEntity.getBody());
                    for (int i = 0; i < patientPathways.length(); i++) {

                        JSONObject patientPathway = patientPathways.getJSONObject(i);

                        if (patientPathway.get("type").equals("patientPathwayReference") == true) {
                            service = organizationServiceRepository.findByKmdExternalId(patientPathway.get(PATHWAY_TYPE_ID).toString());
                            if (service == null) {
                                service = new com.kairos.persistence.model.organization.services.OrganizationService();
                                service.setName(patientPathway.get("name").toString());
                                Optional serviceOptional = Optional.ofNullable(patientPathway.get(PATHWAY_TYPE_ID));
                                if(serviceOptional.isPresent()) service.setKmdExternalId(patientPathway.get(PATHWAY_TYPE_ID).toString());
                                service.setImported(true);
                                service = organizationServiceService.saveImportedServices(service);
                            }
                            JSONArray pathwayChildren = patientPathway.getJSONArray(CHILDREN);
                            if (pathwayChildren.length() > 0) {
                                for (int pC = 0; pC < pathwayChildren.length(); pC++) {
                                    JSONObject subPatientPathway = pathwayChildren.getJSONObject(pC);
                                    if (subPatientPathway.get("type").equals("patientPathwayReference") == true) {
                                        subService = organizationServiceRepository.findByKmdExternalId(subPatientPathway.get(PATHWAY_TYPE_ID).toString());
                                        if (subService == null) {
                                            subService = new com.kairos.persistence.model.organization.services.OrganizationService();
                                            subService.setName(subPatientPathway.get("name").toString());
                                            Optional subServiceOptional = Optional.ofNullable(subPatientPathway.get(PATHWAY_TYPE_ID));
                                            if(subServiceOptional.isPresent()) subService.setKmdExternalId(subPatientPathway.get(PATHWAY_TYPE_ID).toString());
                                            subService.setImported(true);
                                            subService = organizationServiceService.addSubService(service.getId(), subService);
                                            organizationServiceService.updateServiceToOrganization(unit.getId(), subService.getId(), true);
                                        }
                                        JSONArray subPatientPathwayChildren = subPatientPathway.getJSONArray(CHILDREN);
                                        for (int sPPC = 0; sPPC < subPatientPathwayChildren.length(); sPPC++) {
                                            JSONObject subSubPatientPathway = subPatientPathwayChildren.getJSONObject(sPPC);
                                            getGrantsFromSubPatientPathway(subSubPatientPathway, loginTemplate, headersElements, unit, client, service, subService);
                                        }
                                    }
                                    getGrantsFromSubPatientPathway(subPatientPathway, loginTemplate, headersElements, unit, client, service, subService);

                                }

                            }
                        }

                    }
                } catch (Exception exception) {
                    logger.warn("Exception occurs while importing grants from  KMD -> {}",exception.getMessage());

                }
            }

        }

    }

    /**
     * This method is used to fetch grant details from KMD pathways and sub-pathways
     * pathways and sub-pathway is pattern to stored the grants.
     * @param subPatientPathway
     * @param loginTemplate
     * @param headersElements
     * @param unit
     * @param client
     * @param service
     * @param subService
     */
    public void getGrantsFromSubPatientPathway(JSONObject subPatientPathway, RestTemplate loginTemplate, HttpEntity<String> headersElements, Unit unit, Client client, com.kairos.persistence.model.organization.services.OrganizationService service, com.kairos.persistence.model.organization.services.OrganizationService subService) {

        if (subPatientPathway.get("type").equals("orderReference")) {
            JSONArray grantOrderChildren = subPatientPathway.getJSONArray(CHILDREN);
            for (int gC = 0; gC < grantOrderChildren.length(); gC++) {
                int grantOrderId = grantOrderChildren.getJSONObject(gC).getInt(GRANT_ID);
                ResponseEntity<String> grantOrderResponse = loginTemplate.exchange(AppConstants.KMD_NEXUS_PATIENT_ORDER_GRANTS + grantOrderId, HttpMethod.GET, headersElements, String.class);
                OrderGrant grantOrderObject = jsonStringToObject(grantOrderResponse.getBody(), OrderGrant.class);
                getGrantObject(grantOrderObject.getOriginatorId(), loginTemplate, headersElements, unit, client, service, subService);

            }

        }
        if (subPatientPathway.get("type").equals("basketReference") == true) {
            JSONArray grantChildren = subPatientPathway.getJSONArray(CHILDREN);
            for (int gC = 0; gC < grantChildren.length(); gC++) {
                int grantId = grantChildren.getJSONObject(gC).getInt(GRANT_ID);
                getGrantObject(grantId, loginTemplate, headersElements, unit, client, service, subService);

            }
        }

    }

    /**
     * This method is used to fetch details of Demands and it's services and subServices from KMD.
     * @param grantId
     * @param loginTemplate
     * @param headersElements
     * @param unit
     * @param client
     * @param service
     * @param subService
     * @return
     */
    private Map<String, Object> getGrantObject(int grantId, RestTemplate loginTemplate, HttpEntity<String> headersElements,
                                               Unit unit, Client client,
                                               com.kairos.persistence.model.organization.services.OrganizationService service, com.kairos.persistence.model.organization.services.OrganizationService subService) {
        List grantTypes = new ArrayList();

        grantTypes.add("Serviceloven §83, stk. 1");
        grantTypes.add("Servicelov §83, stk. 1");
        grantTypes.add("Serviceloven §83, stk. 2");
        grantTypes.add("Serviceloven §83, stk 2");
        grantTypes.add("Sundhedsloven §138");
        grantTypes.add("Sundhedsloven §140");
        Map<String, Object> grantObject = new HashMap<>();
        logger.info("grant url----------> {}" , AppConstants.KMD_NEXUS_PATIENT_GRANTS + grantId);
        ResponseEntity<String> grantResponse = loginTemplate.exchange(AppConstants.KMD_NEXUS_PATIENT_GRANTS + grantId, HttpMethod.GET, headersElements, String.class);
        PatientGrant patientGrant = jsonStringToObject(grantResponse.getBody(), PatientGrant.class);
        List<CurrentElements> currentElementsList = patientGrant.getCurrentElements();
        for (CurrentElements currentElements : currentElementsList) {
            switch (currentElements.getType()) {

                case "paragraph":
                    grantObject.put("grantTypeNameSection", currentElements.getParagraph().getName() + " " + currentElements.getParagraph().getSection());
                    grantObject.put("grantTypeGroupName", currentElements.getParagraph().getGroup().getName());
                    break;
                case "repetition":
                    grantObject.put("grantPattern", currentElements.getPattern());
                    grantObject.put("grantCount", currentElements.getCount());
                    grantObject.put("grantWeekDays", currentElements.getNext().getWeekdays());
                    grantObject.put("grantWeekEnds", currentElements.getNext().getWeekenddays());
                    grantObject.put("weekDayShifts", currentElements.getNext().getNext());
                    grantObject.put("priority", currentElements.getPriority());
                    break;
                case "workflowApprovedDate":
                    grantObject.put("date", currentElements.getDate());
                    break;
                case "resourceCount":
                    grantObject.put("resourceCount", currentElements.getNumber());
                    break;
                case "visitatedDurationInMinutes":
                    grantObject.put("visitatedDuration", currentElements.getNumber());
                    break;
                case SUPPLIER:
                    grantObject.put(SUPPLIER, currentElements.getSupplier());
                    break;
                case "description":
                    grantObject.put("description", currentElements.getText());
                    break;
                default:
                    break;
            }
        }
        grantObject.put("grantName", patientGrant.getName());
        grantObject.put(GRANT_ID,grantId);
        grantObject.put("clientId",client.getId());
        grantObject.put("organizationId", unit.getId());
        RepetitionType shiftRepetition = (RepetitionType) grantObject.get("weekDayShifts");
        if (grantTypes.contains(grantObject.get("grantTypeNameSection")) && (shiftRepetition.getShifts().size() > 0)) {
            CitizenSupplier citizenSupplier = (CitizenSupplier) grantObject.get(SUPPLIER);
            Organization supplier = organizationGraphRepository.findByKmdExternalId(citizenSupplier.getId());
            Optional supplierOptional = Optional.ofNullable(supplier);
            if (!supplierOptional.isPresent()) {
                supplier = new OrganizationBuilder().setName(citizenSupplier.getName()).createOrganization();
                Long countryId=countryGraphRepository.getCountryIdByUnitId(unit.getId());
                supplier.setCountry(countryGraphRepository.findOne(countryId));
                supplier.setKmdExternalId(citizenSupplier.getId());
            }
             else if (citizenSupplier.getType().equals("organization") && organizationService.checkDuplicationOrganizationRelation(unit.getId(), supplier.getId()) == 0) {
                   supplier = organizationService.createOrganization(supplier,  false);
            }

            if (supplierOptional.isPresent()) grantObject.put("supplierId",supplier.getId());
            if (subService == null) {
                subService = organizationServiceRepository.checkDuplicateSubServiceWithSpecialCharacters(service.getId(), "Sub "+service.getName());
                logger.info("subservice create wigth same name----> {}",subService);
                if (subService == null) {
                    subService = new com.kairos.persistence.model.organization.services.OrganizationService();
                    subService.setName("Sub "+service.getName());
                    subService.setKmdExternalId(service.getKmdExternalId());
                    subService.setImported(true);
                    subService = organizationServiceService.addSubService(service.getId(), subService);
                    organizationServiceService.updateServiceToOrganization(unit.getId(), subService.getId(), true);
                }
            }
            Map<String,Object> response = taskDemandRestClient.createGrants( subService.getId(), unit.getId(), grantObject);
                logger.info("response------------------->{} ",response);


        }
            return grantObject;

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
                logger.info("citizen----kmdNexusExternalId------> {}" , map.get(KMD_NEXUS_EXTERNAL_ID));
                client = clientGraphRepository.findByKmdNexusExternalId(map.get(KMD_NEXUS_EXTERNAL_ID).toString());
                logger.info("client-------------> {}",client);
                ResponseEntity<String> responseEntity = loginTemplate.exchange(String.format(AppConstants.KMD_NEXUS_PATIENT_RELATIVE_CONTACT, map.get(KMD_NEXUS_EXTERNAL_ID).toString()), HttpMethod.GET, headersElements, String.class);
                AvailableContacts availableContacts = jsonStringToObject(responseEntity.getBody(), AvailableContacts.class);
                for (RelativeContacts relativeContacts : availableContacts.getRelativeContacts()) {
                    String relativeContactUrl = relativeContacts.get_links().getSelf().getHref();
                    ResponseEntity<String> relativeContactResponse = loginTemplate.exchange(relativeContactUrl, HttpMethod.GET, headersElements, String.class);
                    Unit unit = unitGraphRepository.findByName(AppConstants.KMD_NEXUS_ORGANIZATION);
                    PatientRelative patientRelative = jsonStringToObject(relativeContactResponse.getBody(), PatientRelative.class);
                    clientService.addClientRelativeDetailsFromExternalService(patientRelative, client, unit.getId());
                }
            }
        }catch (Exception exception){
            logger.warn("exception while adding citizen relative data---->{} ",exception.getMessage());
        }

    }


    public void getShifts(Long filterId, Long unitId){
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
        for(ImportShiftDTO shift : columnResource.getShifts()){
            String staffExternalId = shift.getEventResource().getResourceId();
            staffExternalId = staffExternalId.substring(staffExternalId.indexOf("PROFESSIONAL:")+13);
            logger.info("Staff External Id----> {}",staffExternalId);
            ResponseEntity<String> staffResponseEntity = loginTemplate.exchange(String.format(AppConstants.KMD_NEXUS_STAFFS_DETAILS, staffExternalId), HttpMethod.GET, headersElements, String.class);
            StaffPersonalDetail staffDTO = jsonStringToObject(staffResponseEntity.getBody(), StaffPersonalDetail.class);
            Staff staff = createStaffFromKMD(unitId, staffDTO);
            taskServiceRestClient.createTaskFromKMD(staff.getId(),shift,unitId);
            logger.info("staff DTO---------> {}",staffDTO.getLastName());

        }
    }

    public Staff createStaffFromKMD(long unitId, StaffPersonalDetail payload) {
        Staff staff = staffGraphRepository.findByKmdExternalId(payload.getId());
        if(staff == null) staff = new Staff();
        Organization organization = organizationGraphRepository.findOne(unitId);
        if (organization == null)
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_ID_NOTFOUND,unitId);

        staff.setFirstName(payload.getFirstName());
        staff.setLastName(payload.getLastName());
        staff.setCurrentStatus(payload.getCurrentStatus());
        staff.setEmail(payload.getContactDetail().getPrivateEmail());
        staff.setKmdExternalId(payload.getId());


        User user =  userGraphRepository.findByKmdExternalId(payload.getId());
        if(user == null) {
            SystemLanguage systemLanguage = systemLanguageService.getDefaultSystemLanguageForUnit(unitId);
            user = new User();
            user.setUserLanguage(systemLanguage);
        }
        user.setEmail(staff.getEmail());
        user.setFirstName(staff.getFirstName());
        user.setLastName(staff.getLastName());
        userGraphRepository.save(user);

        if (user != null) {
            Staff alreadyExistStaff = staffGraphRepository.getByUser(user.getId());
            if (alreadyExistStaff != null)
                exceptionService.dataNotFoundByIdException(MESSAGE_CITIZEN_STAFF_ALREADYEXIST);

            staff = staffCreationService.updateStaffDetailsOnCreationOfStaff(user, staff, Long.valueOf("1162"), organization);
        }

        Organization parent=organizationService.fetchParentOrganization(unitId);

        if (parent == null) {
            if((staff.getId() == null) || (positionGraphRepository.findPosition(organization.getId(), staff.getId()) == null)){
                positionGraphRepository.createPositions(organization.getId(), Arrays.asList(staff.getId()), organization.getId());
            }

        } else {
            if((staff.getId() == null) || (positionGraphRepository.findPosition(parent.getId(), staff.getId()) == null)) {
                positionGraphRepository.createPositions(parent.getId(), Arrays.asList(staff.getId()), organization.getId());
            }
        }

        logger.info("::::::::::::::::;   Saving user :::::::::::::::::: ");
        return staffGraphRepository.save(staff);
    }

    public void getTimeSlots(Long unitId){
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
        timeSlotService.updateTimeSlotType(unitId,false);
    }



}
