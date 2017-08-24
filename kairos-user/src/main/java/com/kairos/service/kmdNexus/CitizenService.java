package com.kairos.service.kmdNexus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.kairos.client.CitizenServiceRestClient;
import com.kairos.service.organization.OrganizationService;
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

import com.kairos.constants.AppConstants;
import com.kairos.util.JsonUtils;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.client.Client;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.OrganizationServiceRepository;
import com.kairos.persistence.repository.organization.TimeSlotGraphRepository;
import com.kairos.persistence.repository.user.client.ClientGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.response.dto.web.AvailableContacts;
import com.kairos.response.dto.web.CitizenSupplier;
import com.kairos.response.dto.web.CurrentElements;
import com.kairos.response.dto.web.OrderGrant;
import com.kairos.response.dto.web.PatientGrant;
import com.kairos.response.dto.web.PatientRelative;
import com.kairos.response.dto.web.PatientWrapper;
import com.kairos.response.dto.web.RelativeContacts;
import com.kairos.response.dto.web.RepetitionType;
import com.kairos.service.client.ExternalClientService;
import com.kairos.service.organization.OrganizationServiceService;

import static com.kairos.constants.AppConstants.*;


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
    @Autowired
    CitizenServiceRestClient citizenServiceRestClient;

    /**
     * This method is used to import Citizen from KMD Nexus.
     * @return
     */
    public String getCitizensFromKMD() {
        try {
            Organization organization = organizationGraphRepository.findByName(AppConstants.KMD_NEXUS_ORGANIZATION);
            RestTemplate loginTemplate = new RestTemplate();
            HttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
            HttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
            loginTemplate.getMessageConverters().add(formHttpMessageConverter);
            loginTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            loginTemplate.getMessageConverters().add(stringHttpMessageConverter);
            logger.info("organization response----------> "+organization);
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
                    logger.info("patientWrapper------------> " + patientWrapper.getId());
                    //   if(Integer.valueOf(patientWrapper.getId()) != 7) continue;
                    clientService.createCitizenFromKmd(patientWrapper, organization.getId());
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
        com.kairos.persistence.model.organization.OrganizationService service = null;
        com.kairos.persistence.model.organization.OrganizationService subService = null;
        Client client = null;

        for (Map<String, Object> map : citizens) {
            authService.dokmdAuth();
            logger.info("citizen----kmdNexusExternalId------> " + map.get("kmdNexusExternalId"));
            //  if(map.get("kmdNexusExternalId").equals("7") == false) continue;
            client = clientGraphRepository.findByKmdNexusExternalId(map.get("kmdNexusExternalId").toString());
            Organization organization = (Organization) clientGraphRepository.getClientOrganizationIdList(client.getId()).get(0);

            //  logger.info("timeSlots------------> "+timeSlots);
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
                            logger.info("services--1-----> "+patientPathway.get("name").toString());
                            service = organizationServiceRepository.checkDuplicateService(country.getId(), patientPathway.get("name").toString());
                            logger.info("service--2-----> "+service);
                            if (service == null) {
                                service = new com.kairos.persistence.model.organization.OrganizationService();
                                service.setName(patientPathway.get("name").toString());
                                service = organizationServiceService.createOrganizationService(country.getId(), service);
                            }
                            JSONArray pathwayChildren = patientPathway.getJSONArray("children");
                            boolean hasSubService = false;
                            if (pathwayChildren.length() > 0) {
                                for (int pC = 0; pC < pathwayChildren.length(); pC++) {
                                    JSONObject subPatientPathway = pathwayChildren.getJSONObject(pC);
                                    if (subPatientPathway.get("type").equals("patientPathwayReference") == true) {
                                        hasSubService = true;
                                        subService = organizationServiceRepository.checkDuplicateSubServiceWithSpecialCharacters(service.getId(), subPatientPathway.get("name").toString());
                                        if (subService == null) {
                                            subService = new com.kairos.persistence.model.organization.OrganizationService();
                                            subService.setName(subPatientPathway.get("name").toString());
                                            organizationServiceService.addSubService(service.getId(), subService);
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
    public void getGrantsFromSubPatientPathway(JSONObject subPatientPathway, RestTemplate loginTemplate, HttpEntity<String> headersElements, Organization organization, Client client, com.kairos.persistence.model.organization.OrganizationService service, com.kairos.persistence.model.organization.OrganizationService subService) {
        logger.info("subPatientPathway-------type--------> " + subPatientPathway.get("type"));
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
    private JSONObject getGrantObject(int grantId, RestTemplate loginTemplate, HttpEntity<String> headersElements,
                                     Organization organization, Client client,
                                     com.kairos.persistence.model.organization.OrganizationService service, com.kairos.persistence.model.organization.OrganizationService subService) {
        List grantTypes = new ArrayList();

        grantTypes.add("Serviceloven §83, stk. 1");
        grantTypes.add("Servicelov §83, stk. 1");
        grantTypes.add("Serviceloven §83, stk. 2");
        grantTypes.add("Serviceloven §83, stk 2");
        grantTypes.add("Sundhedsloven §138");
        grantTypes.add("Sundhedsloven §140");
        JSONObject grantObject = new JSONObject();
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
        RepetitionType shiftRepetition = (RepetitionType) grantObject.get("weekDayShifts");

        String pattern = (String) grantObject.get("grantPattern");
        if (grantTypes.contains(grantObject.get("grantTypeNameSection")) && (shiftRepetition.getShifts().size() > 0)) {
            CitizenSupplier citizenSupplier = (CitizenSupplier) grantObject.get("supplier");
            Organization supplier = organizationGraphRepository.findByKmdExternalId(citizenSupplier.getId());

            if (supplier == null) {
                supplier = new Organization(citizenSupplier.getName());
                supplier.setCountry(organization.getCountry());
                supplier.setKmdExternalId(citizenSupplier.getId());
            }
            if (citizenSupplier.getType().equals("external")) {
                // organizationService.createOrganization(supplier, null);
            } else if (citizenSupplier.getType().equals("organization")) {
                if (organizationService.checkDuplicationOrganizationRelation(organization.getId(), supplier.getId()) == 0)
                    organizationService.createOrganization(supplier, organization.getId());
            }
            Integer weekDayCount = Integer.valueOf(grantObject.get("grantWeekDays").toString());
            Integer weekEndCount = Integer.valueOf(grantObject.get("grantWeekEnds").toString());
            String visitDuration = grantObject.get("visitatedDuration").toString();


            if (subService == null) {
                subService = organizationServiceRepository.checkDuplicateSubServiceWithSpecialCharacters(service.getId(), service.getName());
                if (subService == null) {
                    subService = new com.kairos.persistence.model.organization.OrganizationService();
                    subService.setName(service.getName());
                    organizationServiceService.addSubService(service.getId(), subService);
                }
            }

            if (organization != null) {
                organizationServiceService.updateServiceToOrganization(organization.getId(), subService.getId(), true, ORGANIZATION);

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
                    Client nextToKin = client.getNextToKin();
                }
                for (RelativeContacts relativeContacts : availableContacts.getRelativeContacts()) {
                    String relativeContactUrl = relativeContacts.get_links().getSelf().getHref();
                    ResponseEntity<String> relativeContactResponse = loginTemplate.exchange(relativeContactUrl, HttpMethod.GET, headersElements, String.class);
                    Organization organization = organizationGraphRepository.findByName(AppConstants.KMD_NEXUS_ORGANIZATION);
                    PatientRelative patientRelative = JsonUtils.toObject(relativeContactResponse.getBody(), PatientRelative.class);
                    clientService.addClientRelativeDetailsFromKmd(patientRelative, client, organization.getId());
                }
            }
            return "Success";
        }catch (Exception exception){
            logger.warn("exception while adding citizen relative data----> "+exception.getMessage());
            return exception.getMessage();

        }

    }

   }
