package com.kairos.service.kmdNexus;

import com.kairos.activity.task.order.OrderGrant;
import com.kairos.constants.AppConstants;
import com.kairos.enums.OrganizationLevel;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.client.Client;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.system_setting.SystemLanguage;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.OrganizationServiceRepository;
import com.kairos.persistence.repository.organization.time_slot.TimeSlotGraphRepository;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.client.ClientGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.staff.EmploymentGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.rest_client.TaskDemandRestClient;
import com.kairos.rest_client.TaskServiceRestClient;
import com.kairos.service.client.ExternalClientService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.service.organization.OrganizationServiceService;
import com.kairos.service.organization.TimeSlotService;
import com.kairos.service.staff.StaffService;
import com.kairos.service.system_setting.SystemLanguageService;
import com.kairos.user.organization.ImportTimeSlotListDTO;
import com.kairos.user.patient.CurrentElements;
import com.kairos.user.patient.PatientGrant;
import com.kairos.user.patient.PatientRelative;
import com.kairos.user.patient.PatientWrapper;
import com.kairos.user.staff.AvailableContacts;
import com.kairos.user.staff.ColumnResource;
import com.kairos.user.staff.ImportShiftDTO;
import com.kairos.user.staff.RelativeContacts;
import com.kairos.user.staff.client.CitizenSupplier;
import com.kairos.user.staff.staff.StaffDTO;
import com.kairos.user.visitation.RepetitionType;
import com.kairos.util.JsonUtils;
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

import static com.kairos.constants.AppConstants.ORGANIZATION;


/**
 * Created by oodles on 25/4/17.
 */
@Transactional
@Service
public class CitizenService {

    private static final Logger logger = LoggerFactory.getLogger(CitizenService.class);

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
    private OrganizationGraphRepository organizationGraphRepository;

    @Inject
    private OrganizationService organizationService;
    @Inject
    private ExternalClientService clientService;

    @Inject
    private AuthService authService;

    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject
    private UserGraphRepository userGraphRepository;
    @Inject
    private StaffService staffService;
    @Inject
    private EmploymentGraphRepository employmentGraphRepository;
    @Autowired
    TaskServiceRestClient taskServiceRestClient;
    @Autowired
    TaskDemandRestClient taskDemandRestClient;
    @Inject
    private TimeSlotService timeSlotService;

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
            Organization organization = organizationGraphRepository.findOne(unitId);
            RestTemplate loginTemplate = new RestTemplate();
            HttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
            HttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
            loginTemplate.getMessageConverters().add(formHttpMessageConverter);
            loginTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            loginTemplate.getMessageConverters().add(stringHttpMessageConverter);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + AppConstants.KMD_NEXUS_ACCESS_TOKEN);
            //headers.setContentType(MediaType.APPLICATION_JSON);


            HttpEntity<String> headersElements = new HttpEntity<String>("parameters", headers);

            //    ResponseEntity<String> responseEntity = loginTemplate.exchange(AppConstants.KMD_NEXUS_PATIENT_PREFERENCE, HttpMethod.GET, headersElements, String.class);

            //    JSONObject preferences = new JSONObject(responseEntity.getBody());
            //    JSONArray citizenList = preferences.getJSONArray("CITIZEN_LIST");

            //  for (int i = 0; i < 1; i++) {
            //    Long id = citizenList.getJSONObject(i).getLong("id");
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
                    PatientWrapper patientWrapper = JsonUtils.toObject(patientResponse.getBody().toString(), PatientWrapper.class);
                    //   if(Integer.valueOf(patientWrapper.getId()) != 7) continue;
                    clientService.createCitizenFromExternalService(patientWrapper, organization.getId());
                }

            }

            //   }
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
        Country country = null;
        if (countries.isEmpty()) {
            return;
        }
        country = countries.get(0);
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
        headers.add("Authorization", "Bearer " + AppConstants.KMD_NEXUS_ACCESS_TOKEN);
        //headers.setContentType(MediaType.APPLICATION_JSON);


        HttpEntity<String> headersElements = new HttpEntity<String>("parameters", headers);
        com.kairos.persistence.model.organization.services.OrganizationService service = null;
        com.kairos.persistence.model.organization.services.OrganizationService subService = null;
        Client client = null;

        for (Map<String, Object> map : citizens) {
            authService.dokmdAuth();

            client = clientGraphRepository.findByKmdNexusExternalId(map.get("kmdNexusExternalId").toString());
            Organization organization = (Organization) clientGraphRepository.getClientOrganizationIdList(client.getId()).get(0);

            ResponseEntity<String> responseEntity = loginTemplate.exchange(String.format(AppConstants.KMD_NEXUS_PATIENT_PATHWAY_PREFERENCE, map.get("kmdNexusExternalId").toString()), HttpMethod.GET, headersElements, String.class);
            JSONObject patientPathwayObject = new JSONObject(responseEntity.getBody());
            JSONArray citizenPathwayArray = patientPathwayObject.getJSONArray("CITIZEN_PATHWAY");
            for (int cp = 0; cp < citizenPathwayArray.length(); cp++) {
                int pathwayFilterId = citizenPathwayArray.getJSONObject(cp).getInt("id");
                if (pathwayFilterId != 413) continue;
                try {
                    ResponseEntity<String> pathwayPreferenceResponseEntity = loginTemplate.exchange(String.format(AppConstants.KMD_NEXUS_PATIENT_PATHWAY, map.get("kmdNexusExternalId").toString()) + pathwayFilterId, HttpMethod.GET, headersElements, String.class);
                    JSONArray patientPathways = new JSONArray(pathwayPreferenceResponseEntity.getBody());
                    for (int i = 0; i < patientPathways.length(); i++) {

                        JSONObject patientPathway = patientPathways.getJSONObject(i);

                        if (patientPathway.get("type").equals("patientPathwayReference") == true) {
                            service = organizationServiceRepository.findByKmdExternalId(patientPathway.get("pathwayTypeId").toString());
                            if (service == null) {
                                service = new com.kairos.persistence.model.organization.services.OrganizationService();
                                service.setName(patientPathway.get("name").toString());
                                Optional serviceOptional = Optional.ofNullable(patientPathway.get("pathwayTypeId"));
                                if(serviceOptional.isPresent()) service.setKmdExternalId(patientPathway.get("pathwayTypeId").toString());
                                service.setImported(true);
                                service = organizationServiceService.saveImportedServices(service);
                            }
                            JSONArray pathwayChildren = patientPathway.getJSONArray("children");
                            boolean hasSubService = false;
                            if (pathwayChildren.length() > 0) {
                                for (int pC = 0; pC < pathwayChildren.length(); pC++) {
                                    JSONObject subPatientPathway = pathwayChildren.getJSONObject(pC);
                                    if (subPatientPathway.get("type").equals("patientPathwayReference") == true) {
                                        hasSubService = true;
                                        subService = organizationServiceRepository.findByKmdExternalId(subPatientPathway.get("pathwayTypeId").toString());
                                        if (subService == null) {
                                            subService = new com.kairos.persistence.model.organization.services.OrganizationService();
                                            subService.setName(subPatientPathway.get("name").toString());
                                            Optional subServiceOptional = Optional.ofNullable(subPatientPathway.get("pathwayTypeId"));
                                            if(subServiceOptional.isPresent()) subService.setKmdExternalId(subPatientPathway.get("pathwayTypeId").toString());
                                            subService.setImported(true);
                                            subService = organizationServiceService.addSubService(service.getId(), subService);
                                            organizationServiceService.updateServiceToOrganization(organization.getId(), subService.getId(), true, ORGANIZATION);
                                        }
                                        JSONArray subPatientPathwayChildren = subPatientPathway.getJSONArray("children");
                                        for (int sPPC = 0; sPPC < subPatientPathwayChildren.length(); sPPC++) {
                                            JSONObject subSubPatientPathway = subPatientPathwayChildren.getJSONObject(sPPC);
                                            getGrantsFromSubPatientPathway(subSubPatientPathway, loginTemplate, headersElements, organization, client, service, subService);
                                        }
                                    }
                                    getGrantsFromSubPatientPathway(subPatientPathway, loginTemplate, headersElements, organization, client, service, subService);

                                }

                            }
                        }

                    }
                } catch (Exception exception) {
                    logger.warn("Exception occurs while importing grants from  KMD -> "+exception.getMessage());

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
     * @param organization
     * @param client
     * @param service
     * @param subService
     */
    public void getGrantsFromSubPatientPathway(JSONObject subPatientPathway, RestTemplate loginTemplate, HttpEntity<String> headersElements, Organization organization, Client client, com.kairos.persistence.model.organization.services.OrganizationService service, com.kairos.persistence.model.organization.services.OrganizationService subService) {

        if (subPatientPathway.get("type").equals("orderReference") == true) {
            // logger.info("subPatientPathway----------> "+subPatientPathway.get("name"));
            JSONArray grantOrderChildren = subPatientPathway.getJSONArray("children");
            for (int gC = 0; gC < grantOrderChildren.length(); gC++) {
                int grantOrderId = grantOrderChildren.getJSONObject(gC).getInt("grantId");
                ResponseEntity<String> grantOrderResponse = loginTemplate.exchange(AppConstants.KMD_NEXUS_PATIENT_ORDER_GRANTS + grantOrderId, HttpMethod.GET, headersElements, String.class);
                OrderGrant grantOrderObject = JsonUtils.toObject(grantOrderResponse.getBody(), OrderGrant.class);
                getGrantObject(grantOrderObject.getOriginatorId(), loginTemplate, headersElements, organization, client, service, subService);

            }

        }
        if (subPatientPathway.get("type").equals("basketReference") == true) {
            JSONArray grantChildren = subPatientPathway.getJSONArray("children");
            for (int gC = 0; gC < grantChildren.length(); gC++) {
                int grantId = grantChildren.getJSONObject(gC).getInt("grantId");
                getGrantObject(grantId, loginTemplate, headersElements, organization, client, service, subService);

            }
        }

    }

    /**
     * This method is used to fetch details of Demands and it's services and subServices from KMD.
     * @param grantId
     * @param loginTemplate
     * @param headersElements
     * @param organization
     * @param client
     * @param service
     * @param subService
     * @return
     */
    private Map<String, Object> getGrantObject(int grantId, RestTemplate loginTemplate, HttpEntity<String> headersElements,
                                               Organization organization, Client client,
                                               com.kairos.persistence.model.organization.services.OrganizationService service, com.kairos.persistence.model.organization.services.OrganizationService subService) {
        List grantTypes = new ArrayList();

        grantTypes.add("Serviceloven §83, stk. 1");
        grantTypes.add("Servicelov §83, stk. 1");
        grantTypes.add("Serviceloven §83, stk. 2");
        grantTypes.add("Serviceloven §83, stk 2");
        grantTypes.add("Sundhedsloven §138");
        grantTypes.add("Sundhedsloven §140");
        Map<String, Object> grantObject = new HashMap<>();
        logger.info("grant url----------> " + AppConstants.KMD_NEXUS_PATIENT_GRANTS + grantId);
        ResponseEntity<String> grantResponse = loginTemplate.exchange(AppConstants.KMD_NEXUS_PATIENT_GRANTS + grantId, HttpMethod.GET, headersElements, String.class);
        PatientGrant patientGrant = JsonUtils.toObject(grantResponse.getBody(), PatientGrant.class);
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
                case "supplier":
                    grantObject.put("supplier", currentElements.getSupplier());
                    break;
                case "description":
                    grantObject.put("description", currentElements.getText());
                    break;
            }
        }
        grantObject.put("grantName", patientGrant.getName());
        grantObject.put("grantId",grantId);
        grantObject.put("clientId",client.getId());
        grantObject.put("organizationId",organization.getId());
        RepetitionType shiftRepetition = (RepetitionType) grantObject.get("weekDayShifts");

        String pattern = (String) grantObject.get("grantPattern");
        if (grantTypes.contains(grantObject.get("grantTypeNameSection")) && (shiftRepetition.getShifts().size() > 0)) {
            CitizenSupplier citizenSupplier = (CitizenSupplier) grantObject.get("supplier");
            Organization supplier = organizationGraphRepository.findByKmdExternalId(citizenSupplier.getId());
            Optional supplierOptional = Optional.ofNullable(supplier);
            if (!supplierOptional.isPresent()) {
                supplier = new Organization(citizenSupplier.getName());
                supplier.setCountry(organization.getCountry());
                supplier.setKmdExternalId(citizenSupplier.getId());
            }
            if (citizenSupplier.getType().equals("external")) {
                 //organizationService.createOrganization(supplier, null);
            } else if (citizenSupplier.getType().equals("organization")) {
                if (organizationService.checkDuplicationOrganizationRelation(organization.getId(), supplier.getId()) == 0)
                   supplier = organizationService.createOrganization(supplier, organization.getId(), false);
            }

            if (supplierOptional.isPresent()) grantObject.put("supplierId",supplier.getId());
            Integer weekDayCount = Integer.valueOf(grantObject.get("grantWeekDays").toString());
            Integer weekEndCount = Integer.valueOf(grantObject.get("grantWeekEnds").toString());
            String visitDuration = grantObject.get("visitatedDuration").toString();


            if (subService == null) {
                subService = organizationServiceRepository.checkDuplicateSubServiceWithSpecialCharacters(service.getId(), "Sub "+service.getName());
                logger.info("subservice create wigth same name----> "+subService);
                if (subService == null) {
                    subService = new com.kairos.persistence.model.organization.services.OrganizationService();
                    subService.setName("Sub "+service.getName());
                    subService.setKmdExternalId(service.getKmdExternalId());
                    subService.setImported(true);
                    subService = organizationServiceService.addSubService(service.getId(), subService);
                    organizationServiceService.updateServiceToOrganization(organization.getId(), subService.getId(), true, ORGANIZATION);
                }
            }

            if (organization != null) {

                Map<String,Object> response = taskDemandRestClient.createGrants( subService.getId(), organization.getId(), grantObject);
                logger.info("response-------------------> "+response);
            }


        }
            return grantObject;

    }

    /**
     * This method is used to create Citizen's next to kin details imported from KMD Nexus
     */
    public String getCitizensRelativeContact() {
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
            headers.add("Authorization", "Bearer " + AppConstants.KMD_NEXUS_ACCESS_TOKEN);
            HttpEntity<String> headersElements = new HttpEntity<String>("parameters", headers);
            Client client = null;

            for (Map<String, Object> map : citizens) {
                logger.info("citizen----kmdNexusExternalId------> " + map.get("kmdNexusExternalId"));
                client = clientGraphRepository.findByKmdNexusExternalId(map.get("kmdNexusExternalId").toString());
                logger.info("client-------------> "+client);
                ResponseEntity<String> responseEntity = loginTemplate.exchange(String.format(AppConstants.KMD_NEXUS_PATIENT_RELATIVE_CONTACT, map.get("kmdNexusExternalId").toString()), HttpMethod.GET, headersElements, String.class);
                AvailableContacts availableContacts = JsonUtils.toObject(responseEntity.getBody(), AvailableContacts.class);
                if (availableContacts.getRelativeContacts().size() == 0) {
                    //Client nextToKin = client.getNextToKin();
                }
                for (RelativeContacts relativeContacts : availableContacts.getRelativeContacts()) {
                    String relativeContactUrl = relativeContacts.get_links().getSelf().getHref();
                    ResponseEntity<String> relativeContactResponse = loginTemplate.exchange(relativeContactUrl, HttpMethod.GET, headersElements, String.class);
                    Organization organization = organizationGraphRepository.findByName(AppConstants.KMD_NEXUS_ORGANIZATION);
                    PatientRelative patientRelative = JsonUtils.toObject(relativeContactResponse.getBody(), PatientRelative.class);
                    clientService.addClientRelativeDetailsFromExternalService(patientRelative, client, organization.getId());
                }
            }
            return "Success";
        }catch (Exception exception){
            logger.warn("exception while adding citizen relative data----> "+exception.getMessage());
            return exception.getMessage();

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
        headers.add("Authorization", "Bearer " + AppConstants.KMD_NEXUS_ACCESS_TOKEN);
        //   headers.add("Content-Type" , "application/json");
        Map map = new HashMap<String, String>();
        map.put("Content-Type", "application/json");
        //   headers.setAll(map);
        HttpEntity<String> headersElements = new HttpEntity<String>(headers);
        ResponseEntity<String> responseEntity = loginTemplate.exchange(String.format(AppConstants.KMD_NEXUS_CALENDAR_STAFFS_SHIFT_FILTER, filterId), HttpMethod.POST, headersElements, String.class);
        JSONObject jsonObject = new JSONObject(responseEntity.getBody());
        ColumnResource columnResource = JsonUtils.toObject(jsonObject.get("columnResource").toString(), ColumnResource.class);
        for(ImportShiftDTO shift : columnResource.getShifts()){
            String staffExternalId = shift.getEventResource().getResourceId();
            staffExternalId = staffExternalId.substring(staffExternalId.indexOf("PROFESSIONAL:")+13);
            logger.info("Staff External Id----> "+staffExternalId);
            ResponseEntity<String> staffResponseEntity = loginTemplate.exchange(String.format(AppConstants.KMD_NEXUS_STAFFS_DETAILS, staffExternalId), HttpMethod.GET, headersElements, String.class);
            StaffDTO staffDTO = JsonUtils.toObject(staffResponseEntity.getBody(), StaffDTO.class);
            Staff staff = createStaffFromKMD(unitId, staffDTO);
            taskServiceRestClient.createTaskFromKMD(staff.getId(),shift,unitId);
            //anil m2 move this method in task micro service
            //createTaskFromKMD(staff, shift, unitId);
            logger.info("staff DTO---------> "+staffDTO.getLastName());

        }
        //logger.info("result---------> "+columnResource.getShifts().size());
    }

    public Staff createStaffFromKMD(long unitId, StaffDTO payload) {
        Staff staff = staffGraphRepository.findByKmdExternalId(payload.getId());
        if(staff == null) staff = new Staff();
        Organization unit = organizationGraphRepository.findOne(unitId);
        if (unit == null)
            exceptionService.dataNotFoundByIdException("message.organization.id.notFound",unitId);

        staff.setFirstName(payload.getFirstName());
        staff.setLastName(payload.getLastName());
        staff.setCurrentStatus(payload.getCurrentStatus());
        staff.setEmail(payload.getPrimaryEmailAddress());
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
                exceptionService.dataNotFoundByIdException("message.citizen.staff.alreadyexist");

            staff = staffService.createStaffObject(user, staff, Long.valueOf("1162"), unit);
        }

        Organization parent = null;
        if (!unit.isParentOrganization() && OrganizationLevel.CITY.equals(unit.getOrganizationLevel())) {
            parent = organizationGraphRepository.getParentOrganizationOfCityLevel(unit.getId());

        } else if (!unit.isParentOrganization() && OrganizationLevel.COUNTRY.equals(unit.getOrganizationLevel())) {
            parent = organizationGraphRepository.getParentOfOrganization(unit.getId());
        }

        if (parent == null) {
            if((staff.getId() == null) || (employmentGraphRepository.findEmployment(unit.getId(), staff.getId()) == null)){
                employmentGraphRepository.createEmployments(unit.getId(), Arrays.asList(staff.getId()), unit.getId());
            }

        } else {
            if((staff.getId() == null) || (employmentGraphRepository.findEmployment(parent.getId(), staff.getId()) == null)) {
                employmentGraphRepository.createEmployments(parent.getId(), Arrays.asList(staff.getId()), unit.getId());
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
        headers.add("Authorization", "Bearer " + AppConstants.KMD_NEXUS_ACCESS_TOKEN);
        //   headers.add("Content-Type" , "application/json");
        Map map = new HashMap<String, String>();
        map.put("Content-Type", "application/json");
        //   headers.setAll(map);
        HttpEntity<String> headersElements = new HttpEntity<String>(headers);
        ResponseEntity<String> responseEntity = loginTemplate.exchange(String.format(AppConstants.KMD_NEXUS_GET_TIME_SLOTS), HttpMethod.GET, headersElements, String.class);
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray(responseEntity.getBody());
        jsonObject.put("kmdTimeSlotDTOList", jsonArray);
        ImportTimeSlotListDTO importTimeSlotListDTO = JsonUtils.toObject(jsonObject.toString(), ImportTimeSlotListDTO.class);
        Organization unit = organizationGraphRepository.findOne(unitId);
        importTimeSlotListDTO.getImportTimeSlotDTOList().forEach(kmdTimeSlotDTO -> {
            //timeSlotService.importTimeSlotsFromKMD( unit,  kmdTimeSlotDTO);
        });
        timeSlotService.updateTimeSlotType(unitId,false);
    }



}
