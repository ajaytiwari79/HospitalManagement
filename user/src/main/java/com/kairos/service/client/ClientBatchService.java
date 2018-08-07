package com.kairos.service.client;

import com.kairos.config.env.EnvConfig;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.user.organization.AddressDTO;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.client.Client;
import com.kairos.persistence.model.client.relationships.ClientOrganizationRelation;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.ZipCode;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.client.ClientGraphRepository;
import com.kairos.persistence.repository.user.client.ContactAddressGraphRepository;
import com.kairos.persistence.repository.user.region.MunicipalityGraphRepository;
import com.kairos.persistence.repository.user.region.RegionGraphRepository;
import com.kairos.persistence.repository.user.region.ZipCodeGraphRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.util.CPRUtil;
import com.kairos.util.DateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static com.kairos.constants.AppConstants.KAIROS;
import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_HOME_ADDRESS;


/**
 * Created by oodles on 22/5/17.
 */
@Service
@Transactional
public class ClientBatchService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Inject
    private ClientGraphRepository clientGraphRepository;

    @Inject
    private UserGraphRepository userGraphRepository;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private ClientService clientService;
    @Inject
    private AddressVerificationService addressVerificationService;
    @Inject
    private ClientOrganizationRelationService relationService;
    @Inject
    private ZipCodeGraphRepository zipCodeGraphRepository;
    @Inject
    private MunicipalityGraphRepository municipalityGraphRepository;
    @Inject
    private RegionGraphRepository regionGraphRepository;
    @Inject
    private ContactAddressGraphRepository contactAddressGraphRepository;
    @Inject
    EnvConfig envConfig;
    @Inject
    private ExceptionService exceptionService;

    public void updateClientFromExcel(MultipartFile multipartFile) {

        int clientUpdated = 0;

        try {

            InputStream stream = multipartFile.getInputStream();
            //Get the workbook instance for XLS file
            XSSFWorkbook workbook = new XSSFWorkbook(stream);
            //Get first sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (!rowIterator.hasNext()) {
                exceptionService.internalServerError("error.xssfsheet.noMoreRow", 1);

            }

            User client;
            Cell cell;
            Row row;
            long clientId;
            String firstName;
            String lastName;
            while (rowIterator.hasNext()) {
                row = rowIterator.next();
                if (row.getRowNum() > 1) {
                    cell = row.getCell(0);
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    clientId = Long.valueOf(cell.getStringCellValue());

                    client = clientGraphRepository.getUserByClientId(clientId);
                    if (client != null) {
                        cell = row.getCell(4);
                        firstName = cell.getStringCellValue();
                        cell = row.getCell(5);
                        lastName = cell.getStringCellValue();

                        client.setFirstName(firstName);
                        client.setLastName(lastName);
                        userGraphRepository.save(client);
                        clientUpdated++;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("total client updated  " + clientUpdated);
    }

    public List<Map<String, Object>> batchAddClientsToDatabase(MultipartFile multipartFile, long unitId) {
        Organization currentOrganization = organizationGraphRepository.findOne(unitId);
        if (currentOrganization == null) {
            return null;
        }
        logger.info("Organization found is : " + currentOrganization.getName());
        List<Map<String, Object>> clientList = new ArrayList<>();
        List<Map<String, Object>> houseUnverifiedClient = new ArrayList<>();
        InputStream stream;
        int counter = 0;
        int newClient = 0;
        int existingClient = 0;
        int newAddress = 0;
        int existingAddress = 0;
        int relationCreated = 0;
        try {
            stream = multipartFile.getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(stream);
            XSSFSheet sheet = workbook.getSheetAt(2);
            Iterator<Row> rowIterator = sheet.iterator();

            if (!rowIterator.hasNext()) {
                exceptionService.internalServerError("error.xssfsheet.noMoreRow", 2);

            }


            AddressDTO addressDTO;
            ContactAddress contactAddress = null;
            ClientOrganizationRelation relation;
            String lastCpr = "";

            boolean createClient;
            boolean addToUnverifiedHouse = false;

            while (rowIterator.hasNext()) {
                Client client;
                User user;
                boolean connectToOrganization = false;

                Row row = rowIterator.next();
                if (row.getCell(0) == null || row.getCell(0).toString().isEmpty()) {
                    logger.info("No more rows");
                    return clientList;
                }
                if (row.getRowNum() <= 1) {
                    continue;
                }

                String firstName = "";
                StringBuilder lastName = new StringBuilder();
                String cpr;

                String street = "";
                StringBuilder hnr = new StringBuilder();
                int zipCode = 0;
                String city = "";

                // Client info cells
                Cell cprCell = row.getCell(0);
                Cell nameCell = row.getCell(1);


                // Address Info cells
                Cell houseCell = row.getCell(64);
                Cell zipCodeCell = row.getCell(66);
                Cell cityCell = row.getCell(67);


                // Setting cell type
                nameCell.setCellType(Cell.CELL_TYPE_STRING);
                cprCell.setCellType(Cell.CELL_TYPE_STRING);

                houseCell.setCellType(Cell.CELL_TYPE_STRING);
                cityCell.setCellType(Cell.CELL_TYPE_STRING);
                zipCodeCell.setCellType(Cell.CELL_TYPE_STRING);


                // Check if cellData is repeating
                cpr = cprCell.getStringCellValue();
                if (lastCpr.equals(cpr)) {
                    continue;
                }
                lastCpr = cpr;


                // Process Client info
                String[] values = nameCell.getStringCellValue().split("\\s");
                for (int i = 0; i <= values.length - 1; i++) {
                    if (i == 0) {
                        firstName = values[i];
                    } else {
                        lastName.append(" ").append(values[i]);
                    }
                }

                logger.info("First Name: " + firstName);
                logger.info("Last Name: " + lastName);

                cpr = cprCell.getStringCellValue();
                if (cpr.length() == 9) {
                    cpr = "0" + cpr;
                }


                logger.info("CPR: " + cpr);


                // Check if Client already exist in database with CPR number
                user = userGraphRepository.findUserByCprNumber(cpr);
                if (!Optional.ofNullable(user).isPresent()) {
                    user = new User(firstName, lastName.toString(), cpr, CPRUtil.fetchDateOfBirthFromCPR(cpr));
                    client = new Client();
                    client.setUser(user);
                    client.setProfilePic(clientService.generateAgeAndGenderFromCPR(user));
                    logger.info("user not found in Database Creating new: " + user.getFirstName());
                    createClient = true;
                    newClient++;
                } else {
                    logger.info("user found in Database Using Existing : " + user.getFirstName());
                    createClient = false;
                    existingClient++;
                    client = clientGraphRepository.getClientByUserId(user.getId());
                    client.setProfilePic(clientService.generateAgeAndGenderFromCPR(user));
                    contactAddress = clientGraphRepository.findOne(client.getId()).getHomeAddress();
                }


                if (Objects.isNull(contactAddress)) {
                    contactAddress = new ContactAddress();
                    newAddress++;
                } else {
                    logger.info("Existing address found: " + contactAddress.getId() + " 1. " + contactAddress.getStreet1());
                    existingAddress++;
                }


                // Now Parse Address Information
                String zipString = zipCodeCell.getStringCellValue();
                zipString = zipString.split("\\.", 2)[0];
                zipCode = Integer.valueOf(zipString);
                city = cityCell.getStringCellValue();

                String data = houseCell.getStringCellValue();
                List<String> strings = Arrays.asList(data.split(","));
                String addressOnly = strings.get(0);
                logger.info("Address: " + addressOnly);

                String[] addressData = addressOnly.split("\\s");

                for (int i = 0; i <= addressData.length - 1; i++) {
                    if (i == 0) {
                        street = addressData[i];
                        continue;
                    } else {
                        hnr.append(" ").append(addressData[i]);
                    }
                }
                street = street.trim();
                hnr = new StringBuilder(hnr.toString().trim());
                logger.info("Street: " + street);
                logger.info("HNR: " + hnr);

                addressDTO = new AddressDTO();
                addressDTO.setCity(city);
                addressDTO.setHouseNumber(hnr.toString());
                addressDTO.setZipCodeValue(zipCode);
                addressDTO.setStreet1(street);

                Map<String, Object> result = addressVerificationService.verifyAddressSheet(addressDTO, unitId);
                Integer geoCodeStatus = (Integer) result.get("statusCode");
                boolean saveAddress;

                if (geoCodeStatus == 9) {
                    saveAddress = true;
                } else if (geoCodeStatus == 1 || geoCodeStatus == 2 || geoCodeStatus == 7) {
                    saveAddress = false;
                } else if (geoCodeStatus == 3 || geoCodeStatus == 4 || geoCodeStatus == 8) {
                    addToUnverifiedHouse = true;
                    saveAddress = true;
                } else if (geoCodeStatus == 0) {
                    saveAddress = false;
                } else {
                    saveAddress = false;
                }


                // Creating client
                if (createClient) {
                    if (user.getEmail() == null) {
                        logger.info("Creating email with CPR");
                        String email = user.getCprNumber() + KAIROS;
                        user.setEmail(email);
                        user.setUserName(email);
                    }
                    clientGraphRepository.save(client);
                }


                // Unverified  House Number Clients
                if (addToUnverifiedHouse) {
                    Map<String, Object> map = new HashMap<>();
                    map.put(client.getId().toString(), user.getFirstName() + " " + user.getLastName());
                    houseUnverifiedClient.add(map);
                    logger.info("Adding to Unverified Address List ");
                }


                // Create Client Organization Relation
                int count = relationService.checkClientOrganizationRelation(client.getId(), currentOrganization.getId());

                if (count == 0) {
                    logger.info("Client not connected to organization: " + currentOrganization.getName());
                    connectToOrganization = true;
                    relationCreated++;
                }

                // Save Client Address
                ZipCode zipCodeDb = null;
                if (saveAddress) {
                    logger.info("Saving Address");

                    contactAddress.setLongitude(Float.valueOf(String.valueOf(result.get("xCoordinates"))));
                    contactAddress.setLatitude(Float.valueOf(String.valueOf(result.get("yCoordinates"))));
                    contactAddress.setHouseNumber(addressDTO.getHouseNumber());

                    zipCodeDb = zipCodeGraphRepository.findByZipCode(addressDTO.getZipCodeValue());
                    if (zipCodeDb == null) {
                        exceptionService.dataNotFoundByIdException("message.zipCode.notFound");

                    }
                    Municipality municipality = municipalityGraphRepository.getMunicipalityByZipCodeId(zipCodeDb.getId());
                    if (municipality == null) {
                        exceptionService.dataNotFoundByIdException("message.municipality.notFound");

                    }


                    Map<String, Object> geographyData = regionGraphRepository.getGeographicData(municipality.getId());
                    if (geographyData == null) {
                        logger.info("Geography  not found with zipcodeId: " + municipality.getId());
                        exceptionService.dataNotFoundByIdException("message.geographyData.notFound", municipality.getId());

                    }
                    logger.info("Geography Data: " + geographyData);


                    // Geography Data
                    contactAddress.setMunicipality(municipality);
                    contactAddress.setProvince(String.valueOf(geographyData.get("provinceName")));
                    contactAddress.setCountry(String.valueOf(geographyData.get("countryName")));
                    contactAddress.setRegionName(String.valueOf(geographyData.get("regionName")));
                    contactAddress.setZipCode(zipCodeDb);
                    contactAddress.setCity(zipCodeDb.getName());


                    // Native Details
                    contactAddress.setStreet1(addressDTO.getStreet1());
                    contactAddress.setHouseNumber(addressDTO.getHouseNumber());
                    contactAddress.setFloorNumber(addressDTO.getFloorNumber());
                    contactAddressGraphRepository.save(contactAddress);

                    addressVerificationService.saveAndUpdateClientAddress(client, contactAddress, HAS_HOME_ADDRESS);
                }

                if (connectToOrganization) {
                    if (client != null) {
                        logger.info("Creating relationship : " + client.getId());
                        relation = new ClientOrganizationRelation(client, currentOrganization, DateUtil.getCurrentDate().getTime());
                        relationService.createRelation(relation);

                        Map<String, Object> clientInfo = new HashMap<>();
                        clientInfo.put("name", user.getFirstName() + " " + user.getLastName());
                        clientInfo.put("gender", user.getGender());
                        clientInfo.put("age", user.getAge());
                        clientInfo.put("emailId", user.getEmail());
                        clientInfo.put("id", client.getId());
                        clientInfo.put("drivingDistance", "");
                        clientInfo.put("joiningDate", relation.getJoinDate());
                        clientInfo.put("emergencyNo", "");
                        if (contactAddress != null) {
                            clientInfo.put("city", contactAddress.getCity());
                            clientInfo.put("zipcode", (zipCodeDb == null) ? null : zipCodeDb.getZipCode());
                            clientInfo.put("address", contactAddress.getHouseNumber() + ", " + contactAddress.getStreet1());
                        } else {
                            clientInfo.put("city", "");
                        }
                        clientList.add(clientInfo);
                    }
                }


                logger.info("Count: " + counter);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("counter: " + counter);
        logger.info("newClient: " + newClient);
        logger.info("existingClient: " + existingClient);
        logger.info("addressAdded: " + newAddress);
        logger.info("existingAddress: " + existingAddress);
        Map<String, Object> clientStats = new HashMap<>();
        clientStats.put("Added Client", clientList.size());

        houseUnverifiedClient.add(clientStats);
        logger.info("-----------------House Not Verified-----------------------");
        return clientList;
    }

}
