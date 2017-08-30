package com.kairos.controller.client;

import com.kairos.client.dto.ClientExceptionDTO;
import com.kairos.client.dto.TaskDemandRequestWrapper;
import com.kairos.persistence.model.organization.AddressDTO;
import com.kairos.persistence.model.organization.team.Team;
import com.kairos.persistence.model.user.client.*;
import com.kairos.persistence.model.user.staff.StaffClientData;
import com.kairos.service.client.ClientAddressService;
import com.kairos.service.client.ClientBatchService;
import com.kairos.service.client.ClientExtendedService;
import com.kairos.service.client.ClientService;
import com.kairos.service.mail.MailService;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;


/**
 * Client Controller
 * Performs CRUD operations by passing data clientService
 */

@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL + "/client")
@Api(API_ORGANIZATION_UNIT_URL + "/client")
public class ClientController {

    @Inject
    private ClientService clientService;
    @Inject
    private ClientAddressService clientAddressService;
    @Inject
    private ClientBatchService clientBatchService;
    @Inject
    private ClientExtendedService clientExtendedService;
    @Inject
    private MailService mailService;
    private final Logger logger = Logger.getLogger(this.getClass());


    /**
     * * Get a Client by Id
     *
     * @param id
     * @return
     */
    @ApiOperation("Get a Client by Id")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getClientById(@PathVariable Long id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clientService.getCitizenById(id));
    }


    /**
     * Delete a Client
     *
     * @param id
     */
    @ApiOperation("Delete a Client by Id")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    void deleteClientById(@PathVariable Long id) {
        clientService.delete(id);
    }


    // -----------------------------------TABS ENDPOINTS--------------------------------------//



    @ApiOperation("Create Client with CPR")
    @RequestMapping(value = "/new", method = RequestMethod.POST)
    ResponseEntity<Map<String, Object>> createClientWithCPRInformation(@PathVariable Long unitId, @RequestBody ClientMinimumDTO client) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clientService.createCitizen(client, unitId));
    }

    // General tab
    @ApiOperation("Update General Information for a Client")
    @RequestMapping(value = "/{clientId}/general", method = RequestMethod.PUT)
    ResponseEntity<Map<String, Object>> updateClientGeneralInformation(@RequestBody @Validated ClientPersonalDto client) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clientService.setGeneralDetails(client));
    }

    @ApiOperation("Get General Information for a Client")
    @RequestMapping(value = "/{clientId}/general", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getClientGeneralInformation(@PathVariable long clientId, @PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clientService.retrieveGeneralDetails(clientId, unitId));
    }


    //People in household
    @ApiOperation("Add People In HouseHold")
    @RequestMapping(value = "/{clientId}/household", method = RequestMethod.PUT)
    ResponseEntity<Map<String, Object>> updateClientHouseholdList(@RequestBody ClientMinimumDTO client, @PathVariable long unitId, @PathVariable long clientId) throws CloneNotSupportedException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clientService.addHouseholdToClient(client, unitId, clientId));
    }

    @ApiOperation("Get People In HouseHold")
    @RequestMapping(value = "/{clientId}/household", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> updateClientHouseholdList(@PathVariable long clientId) {
        List<Map<String,Object>> createdHouseHold = clientService.getPeopleInHousehold(clientId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, createdHouseHold);
    }


    // NextToKin
    @ApiOperation("update NextToKin")
    @RequestMapping(value = "/{clientId}/nextToKin", method = RequestMethod.PUT)
    ResponseEntity<Map<String, Object>> updateNextToKin(@RequestBody NextToKinDTO client, @PathVariable long unitId, @PathVariable long clientId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clientExtendedService.updateNextToKin(client, unitId, clientId));
    }


    // Transport Tab
    @ApiOperation("Get Transport Information")
    @RequestMapping(value = "/{clientId}/transportation", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getTransportInformation(@PathVariable long clientId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clientExtendedService.getTransportationDetails(clientId));
    }

    @ApiOperation("Update Transport Information")
    @RequestMapping(value = "/{clientId}/transportation", method = RequestMethod.PUT)
    ResponseEntity<Map<String, Object>> updateTransportInformation(@RequestBody Client client) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clientExtendedService.setTransportationDetails(client));
    }


    // AddressDTO tab
    @ApiOperation("Get AddressDTO Information")
    @RequestMapping(value = "/{clientId}/address", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getClientAddressInformation(@PathVariable long clientId, @PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clientAddressService.getAddressDetails(clientId, unitId));
    }

    @ApiOperation("Add Client AddressDTO with type as specified")
    @RequestMapping(value = "/{clientId}/address", method = RequestMethod.POST)
    ResponseEntity<Map<String, Object>> addClientAddress(@PathVariable long unitId, @PathVariable long clientId,
                                                         @RequestBody AddressDTO address, @RequestParam String addressType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clientAddressService.saveAddress(address, clientId, addressType, unitId));
    }

    @ApiOperation("Update Client AddressDTO with type as specified")
    @RequestMapping(value = "/{clientId}/address/{addressId}", method = RequestMethod.PUT)
    ResponseEntity<Map<String, Object>> updateClientAddress(@PathVariable long unitId, @PathVariable long clientId, @PathVariable long addressId,
                                                            @RequestBody AddressDTO address, @RequestParam String addressType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clientAddressService.updateAddress(unitId, clientId, addressId, address, addressType));
    }

    @ApiOperation("Delete Client AddressDTO")
    @RequestMapping(value = "/{clientId}/contactAddress/{addressId}", method = RequestMethod.DELETE)
    ResponseEntity<Map<String, Object>> deleteClientAddress(@PathVariable long addressId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clientAddressService.removeClientAddress(addressId));

    }

    // Update Address longitude and latitude
    @ApiOperation("Update Client Address Coordinates")
    @RequestMapping(value = "/{clientId}/contactAddress/coordinates", method = RequestMethod.PUT)
    ResponseEntity<Map<String, Object>> updateClientAddress(@PathVariable long clientId, @RequestBody AddressDTO address) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clientAddressService.updateAddressCoordinates(address));
    }


    // Client Relative Information
    @ApiOperation("Update a Relative Information")
    @RequestMapping(value = "/{clientId}/relative/{relativeId}", method = RequestMethod.PUT)
    ResponseEntity<Map<String, Object>> updateClientRelativeInformation(@PathVariable long clientId, @PathVariable long relativeId, @RequestBody Map<String, Object> clientRelativeRelation) {
        ClientRelativeRelation relation = clientExtendedService.setRelativeDetails(clientRelativeRelation, clientId, relativeId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, relation);
    }

    @ApiOperation("Create a Relative Information")
    @RequestMapping(value = "/{clientId}/relative", method = RequestMethod.POST)
    ResponseEntity<Map<String, Object>> saveClientRelativeInformation(@PathVariable long clientId, @RequestBody Map<String, Object> clientRelativeRelation) {
        ClientRelativeRelation relation = clientExtendedService.setNewRelativeDetails(clientRelativeRelation, clientId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, relation);
    }

    @ApiOperation("Get  Relative Information")
    @RequestMapping(value = "/{clientId}/relative", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getClientRelativeInformation(@PathVariable long clientId) {
        try {
            List<Map<String, Object>> relativeList = clientExtendedService.getRelativeDetails(clientId);
            if (relativeList != null) {
                return ResponseHandler.generateResponse(HttpStatus.OK, true, relativeList);
            }
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
        } catch (NullPointerException e) {
            logger.info("Null Pointer Exception occurred with message\n " + e.getCause());
            return ResponseHandler.generateResponse(HttpStatus.NOT_FOUND, false, null);
        } catch (Exception e) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, e);
        }
    }


    // Access to Location
    @ApiOperation("Get AccessLocation Information")
    @RequestMapping(value = "/{clientId}/accessLocation", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getClientAccessLocationInformation(@PathVariable long clientId) {
        List<Object> accessToLocationList = clientAddressService.getAccessLocationDetails(clientId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessToLocationList);
    }

    @ApiOperation("Update Access to Location Information")
    @RequestMapping(value = "/{clientId}/accessLocation", method = RequestMethod.PUT)
    ResponseEntity<Map<String, Object>> updateClientAccessLocationInformation(@RequestBody AccessToLocation contactAddress) {
        AccessToLocation currentContactAddress = clientAddressService.setAccessLocationDetails(contactAddress);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, currentContactAddress);
    }

    @ApiOperation("Add Access to Location Information")
    @RequestMapping(value = "/{clientId}/address/{contactAddressId}/accessLocation", method = RequestMethod.POST)
    ResponseEntity<Map<String, Object>> addClientAccessLocationInformation(@RequestBody AccessToLocation contactAddress, @PathVariable long contactAddressId) {
        AccessToLocation currentContactAddress = clientAddressService.addAccessLocationDetails(contactAddress, contactAddressId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, currentContactAddress);
    }

    @RequestMapping(value = "/{clientId}/accessLocation/image", method = RequestMethod.POST)
    @ApiOperation("upload client picture")
    public ResponseEntity<Map<String, Object>> uploadAccessToLocation(@PathVariable long clientId, @RequestParam("file") MultipartFile file) {
        if (file != null && file.getSize() == 0) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, Collections.EMPTY_MAP);
        }
        String fileName = clientExtendedService.uploadAccessToLocationImage(clientId, file);
        if (fileName == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, Collections.EMPTY_MAP);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, fileName);
    }


    // Organization Tab
    // Get units
    @ApiOperation("Get Organization Units serving citizen")
    @RequestMapping(value = "/{clientId}/organization", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getClientOrganizationInformation(@PathVariable long clientId, @PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                clientService.getUnitData(clientId, unitId));
    }

    //anil maurya this endpoints have dependency of task micro service
    // Organization tab
    // Get Services
    @RequestMapping(value = "/{clientId}/organization/{organizationId1}/service",method = RequestMethod.GET)
    @ApiOperation("Get citizen services")
    private ResponseEntity<Map<String, Object>> getClientTaskData(@PathVariable long organizationId,@PathVariable long organizationId1, @PathVariable long clientId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clientService.getClientServiceData(clientId, organizationId1));

    }

  //anil m2 moved this endpoints in task controller
   /* // Organization tab
    // Get Task
    @RequestMapping(value = "/{clientId}/service/{serviceId}/task")
    @ApiOperation("Get citizen services")
    private ResponseEntity<Map<String, Object>> getClientTaskServiceTasks(@PathVariable long clientId, @PathVariable long serviceId, @PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clientService.getClientTasksByService(clientId, serviceId, unitId));
    }*/


    // Medical Tab
    @ApiOperation("Update Medical Information")
    @RequestMapping(value = "/{medicalId}/medical", method = RequestMethod.PUT)
    ResponseEntity<Map<String, Object>> updateClientMedicalInformation(@PathVariable long clientId, @RequestBody ClientDoctor clientDoctor) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clientExtendedService.setMedicalDetails(clientId, clientDoctor));
    }

    @ApiOperation("Get Medical Information")
    @RequestMapping(value = "/{clientId}/medical", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getClientMedicalInformation(@PathVariable long clientId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clientExtendedService.getMedicalDetails(clientId));
    }

    @ApiOperation("Update Medical Diagnose Information")
    @RequestMapping(value = "/{clientId}/{medicalId}/medical/diagnose", method = RequestMethod.PUT)
    ResponseEntity<Map<String, Object>> updateClientMedicalDiagnoseInformation(@PathVariable long clientId, @RequestBody ClientDiagnose clientDiagnose) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clientExtendedService.addDiagnoseToMedicalInformation(clientId, clientDiagnose));
    }

    @ApiOperation("Delete Medical Diagnose Information")
    @RequestMapping(value = "/{clientId}/medical/diagnose/{diagnoseId}", method = RequestMethod.DELETE)
    ResponseEntity<Map<String, Object>> deleteClientMedicalDiagnoseInformation(@PathVariable long diagnoseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clientExtendedService.deleteMedicalDiagnose(diagnoseId));
    }


    // Client Health Information
    @ApiOperation("Update Health Information: Add Allergy information")
    @RequestMapping(value = "/{clientId}/health", method = RequestMethod.PUT)
    ResponseEntity<Map<String, Object>> addClientHealthInformation(@PathVariable long clientId, @RequestBody ClientAllergies clientAllergies) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clientExtendedService.setHealthDetails(clientAllergies, clientId));
    }

    @ApiOperation("Update (Allergy) Information")
    @RequestMapping(value = "/{clientId}/health/allergy", method = RequestMethod.PUT)
    ResponseEntity<Map<String, Object>> updateClientHealthInformation(@RequestBody ClientAllergies clientAllergies) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clientExtendedService.updateClientAllergy(clientAllergies));
    }

    @ApiOperation("Get Health Information")
    @RequestMapping(value = "/{clientId}/health", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getClientHealthInformation(@PathVariable long clientId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clientExtendedService.getHealthDetails(clientId));
    }


    //  Client Social Media Information
    @ApiOperation("Update Social Media Information")
    @RequestMapping(value = "/{clientId}/socialMedia", method = RequestMethod.PUT)
    ResponseEntity<Map<String, Object>> updateClientSocialMediaInformation(@PathVariable long clientId, @RequestBody @Validated ContactDetailSocialDTO socialMediaMap) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clientExtendedService.setSocialMediaDetails(clientId, socialMediaMap));
    }


    //Client Preference Data based On teamID
    @ApiOperation(value = "Get Staff in Team with attributes")
    @RequestMapping(value = "/{clientId}/staff/team/{teamID}", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getStaffOfTeam(@PathVariable long teamID, @PathVariable long clientId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                clientService.getAllUsers(teamID, clientId));
    }


    //Prefer Staff
    @ApiOperation("Add Client Preferred Staff")
    @RequestMapping(value = "/{clientId}/staff/preferred", method = RequestMethod.PUT)
    ResponseEntity<Map<String, Object>> addPreferredStaff(@PathVariable long clientId, @RequestBody List<StaffClientData> data) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clientService.setClientStaffPreferredRelations(data));
    }

    //Restrict Staff
    @ApiOperation("Add Client Restricted Staff")
    @RequestMapping(value = "/{clientId}/staff/restricted", method = RequestMethod.PUT)
    ResponseEntity<Map<String, Object>> addRestrictedStaff(@PathVariable long clientId, @RequestBody List<StaffClientData> data) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clientService.setClientStaffForbidRelations(data));
    }

    // Clear Staff
    @ApiOperation("Add Client Neutral Staff")
    @RequestMapping(value = "/{clientId}/staff/neutral", method = RequestMethod.PUT)
    ResponseEntity<Map<String, Object>> addNeutralStaff(@PathVariable long clientId, @RequestBody List<StaffClientData> data) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clientService.setClientStaffNoneRelations(data));
    }


    //Prefer Staff
    @ApiOperation("Get Client Preferred Staff")
    @RequestMapping(value = "/{clientId}/staff/preferred", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getPreferredStaff(@PathVariable long clientId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clientService.getClientStaffPreferredRelations(clientId));
    }

    //Restrict Staff
    @ApiOperation("Get Client Restricted Staff")
    @RequestMapping(value = "/{clientId}/staff/restricted", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getRestrictedStaff(@PathVariable long clientId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clientService.getClientStaffForbidRelations(clientId));
    }

    //----------_Bulk data fetch------------
    //Prefer Staff
    @ApiOperation("Get Client Preferred Staff")
    @RequestMapping(value = "/staff/preferred", method = RequestMethod.POST)
    ResponseEntity<Map<String, Object>> getPreferredStaffBulk(@RequestBody List<Long> clientIds) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clientService.getClientStaffPreferredRelationsBulk(clientIds));
    }

    //Restrict Staff
    @ApiOperation("Get Client Restricted Staff")
    @RequestMapping(value = "/staff/restricted", method = RequestMethod.POST)
    ResponseEntity<Map<String, Object>> getRestrictedStaffBulk(@RequestBody List<Long> clientIds) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clientService.getClientStaffForbidRelationsBulk(clientIds));
    }


    // ----Team
    @ApiOperation("Add Client Restricted Team")
    @RequestMapping(value = "/{clientId}/team/restricted", method = RequestMethod.PUT)
    ResponseEntity<Map<String, Object>> setRestrictedTeam(@PathVariable long clientId, @RequestBody Map<String, Long[]> teamIds) {
        List<Team> restrictedTeamList = clientService.setRestrictedTeam(clientId, teamIds);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, restrictedTeamList);
    }


    // Client Profile Picture
    @RequestMapping(value = "/{clientId}/image", method = RequestMethod.POST)
    @ApiOperation("upload client picture")
    public ResponseEntity<Map<String, Object>> uploadPortrait(@PathVariable long clientId, @RequestParam("file") MultipartFile file) {
        if (file != null && file.getSize() == 0) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, Collections.EMPTY_MAP);
        }
        String fileName = clientExtendedService.uploadPortrait(clientId, file);
        if (fileName == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, Collections.EMPTY_MAP);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, fileName);
    }

    @RequestMapping(value = "/{clientId}/image", method = RequestMethod.DELETE)
    @ApiOperation("remove client picture")
    public ResponseEntity<Map<String, Object>> removePortrait(@PathVariable long clientId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clientExtendedService.deleteImage(clientId));

    }


    /**
     * @param map
     * @return
     * @author prabjot
     */
    @RequestMapping(value = "/{clientId}/contact_person", method = RequestMethod.POST)
    @ApiOperation("assign contact person to citizen")
    public ResponseEntity<Map<String, Object>> saveContactPersonForClient(@PathVariable long clientId, @RequestBody Map<String, Object> map) {
        Long staffId = Long.valueOf((String) map.get("staffId"));
        if (clientService.saveContactPersonForClient(clientId, staffId)) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, false);
    }


    // Mark Client Dead
    @RequestMapping(method = RequestMethod.DELETE, value = "/{clientId}/dead")
    @ApiOperation("Delete task exception")
    public ResponseEntity<Map<String, Object>> markClientAsDead(@PathVariable Long clientId) throws ParseException, CloneNotSupportedException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                clientService.markClientAsDead(clientId));
    }


    // Check Mail
    @RequestMapping(method = RequestMethod.GET, value = "/test")
    @ApiOperation("Delete task exception")
    public ResponseEntity<Map<String, Object>> checkinEmail() throws ParseException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                mailService.sendPlainMail("mohit.sharma@oodlestechnologies.com", "test", "test"));
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/basic_info")
    @ApiOperation("update client from excel sheet")
    private ResponseEntity<Map<String, Object>> updateClientFromExcel(@RequestParam("file")  MultipartFile multipartFile){
        clientBatchService.updateClientFromExcel(multipartFile);
        return ResponseHandler.generateResponse(HttpStatus.OK, true,true);
    }

    /**
     * @auther anil maurya
     * this endpoint is called from task micro service
     * @param citizenId
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{citizenId}/getClientStaffInfo")
    @ApiOperation("get client and staff info")
    private ResponseEntity<Map<String, Object>> getStaffClientInfo(@PathVariable Long citizenId, OAuth2Authentication user){
        return ResponseHandler.generateResponse(HttpStatus.OK, true,clientService.getStaffClientInfo(citizenId, user.getUserAuthentication().getPrincipal().toString()));
    }


    /**
     * @auther anil maurya
     * this endpoint is called from task micro service
     * @param citizenId
     * @param staffId
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{citizenId}/{staffId}")
    @ApiOperation("get client and staff info")
    private ResponseEntity<Map<String, Object>> getStaffCitizenHouseholds(@PathVariable Long citizenId,@PathVariable Long staffId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true,clientService.getStaffAndCitizenHouseholds(citizenId,staffId));
    }



     /** @auther anil maurya
     * this endpoint is called from task micro service
     * @param citizenId

     * @return
     */

    @RequestMapping(method = RequestMethod.GET, value = "/{citizenId}")
    @ApiOperation("get client and staff info")
    private ResponseEntity<Map<String, Object>> getCitizenDetails(@PathVariable Long citizenId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true,clientService.getCitizenDetails(citizenId));
    }



    /**
     * @auther anil maurya
     * this endpoint is called from task micro service
     * @param taskDemandWrapper

     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/getClientInfo")
    @ApiOperation("get client and staff info")
    private ResponseEntity<Map<String, Object>> getClientDetailsForTaskDemandVisit(@RequestBody TaskDemandRequestWrapper taskDemandWrapper){
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                clientService.getClientDetailsForTaskDemandVisit(taskDemandWrapper));
    }


    /**
     * called this endpoints from task micro service
     * @param citizenId
     * @param unitId
     * @param authToken
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/generateIndividualTask/{citizenId}/{unitId}/{authToken}")
    @ApiOperation("get client and staff info")
    private ResponseEntity<Map<String, Object>> generateIndividualTask(@PathVariable Long  citizenId
            ,@PathVariable Long  unitId, String authToken){
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                clientService.generateIndividualTask(authToken,unitId,citizenId));
    }



    @ApiOperation(value = "Get Organization Clients with min details")
    @RequestMapping(value = "/unit/{unitId}/client", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getOrganizationClients(@PathVariable Long organizationId, @PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                clientService.getOrganizationClients(unitId));
    }


    @RequestMapping(value = "/{accessToLocationId}/accessLocation/image", method = RequestMethod.DELETE)
    @ApiOperation("delete  access to location image")
    public ResponseEntity<Map<String, Object>> deleteAccessToLocationImage(@PathVariable long accessToLocationId) {
        clientExtendedService.removeAccessToLocationImage(accessToLocationId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }



    // Organization tab
    // Get Services
    @RequestMapping(value = "/{clientId}/organization/{organizationId1}/service")
    @ApiOperation("Get citizen services")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getClientTaskData(@PathVariable long organizationId1, @PathVariable long clientId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clientService.getClientServiceData(clientId, organizationId1));

    }


    /**
     * @auther anil maurya
     * this endpoint is call from ClientExceptionRestClient in task micro service
     * @param clientExceptionDto
     * @param unitId
     * @param clientId
     * @return
     */
    @RequestMapping(value = "/{clientId}/updateClientTempAddress")
    @ApiOperation("updateClientTempAddress")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> changeLocationUpdateClientAddress(@RequestBody ClientExceptionDTO clientExceptionDto,@PathVariable Long unitId, @PathVariable Long clientId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clientService.changeLocationUpdateClientAddress(clientExceptionDto,clientId, unitId));

    }


}
