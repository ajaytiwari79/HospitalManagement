package com.kairos.service.data_import;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by prabjot on 7/2/17.
 */
@Transactional
@Service
public class DataImportService {
/*
    private static final Logger logger = Logger.getLogger(DataImportService.class);

    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private OrganizationServiceRepository organizationServiceRepository;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private OrganizationServiceService organizationServiceService;
    @Inject
    private TaskTypeMongoRepository taskTypeMongoRepository;
    @Inject
    private TaskTypeService taskTypeService;
    @Inject
    private ClientGraphRepository clientGraphRepository;
    @Inject
    TaskDemandService taskDemandService;
    @Inject
    private ClientOrganizationRelationGraphRepository clientOrganizationRelationGraphRepository;
    @Inject
    private TimeSlotService timeSlotService;
    @Inject
    private TaskDemandMongoRepository taskDemandMongoRepository;
    @Inject
    private AddressVerificationService addressVerificationService;
    @Inject
    private ZipCodeGraphRepository zipCodeGraphRepository;
    @Inject
    private RegionService regionService;
    @Inject
    private ClientOrganizationRelationGraphRepository relationGraphRepository;

    @Inject
    MongoSequenceRepository mongoSequenceRepository;



    public void importDataFromExcel(MultipartFile multipartFile) {

        logger.info("Data importing from excel file,file size::" + multipartFile.getSize());

        List<Country> countries = countryGraphRepository.findByName("Denmark");
        Country country = null;
        if(countries.isEmpty()){
            return;
        }
        country = countries.get(0);


        try {

            long startTime = System.currentTimeMillis();
            InputStream inputStream = multipartFile.getInputStream();

            //Create Workbook instance holding reference to .xlsx file
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

            //Get first/desired sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(2);
            System.out.println("name of excel sheet" + sheet.getSheetName());

            //Iterate through each rows one by one
            Iterator<Row> rowIterator = sheet.iterator();
            com.kairos.user.domain.domains.organization_service.OrganizationService organizationService = null;
            Long subServiceId = null;
            String name = null;
            com.kairos.user.domain.domains.organization_service.OrganizationService subService = null;
            String organizationName = null;
            String cprNumber = null;
            String clientName = null;
            String age = null;
            Map<String,Map<String,Object>> reqDetailsForTaskDemand = null;
            TaskType taskType = null;
            String timeSlot = null;
            Client client = null;
            boolean weekDay = false;
            Map<String,Object> taskTypeDetails = null;
            Set<String> taskTypeIds = new HashSet<>();
            Row row = null;

            AddressDTO addressDTO;
            ContactAddress contactAddress = null;
            boolean addToUnverifiedHouse = false;
            while (rowIterator.hasNext()) {
                row = rowIterator.next();
                //For each row, iterate through all the columns
                Iterator<Cell> cellIterator = row.cellIterator();
                Cell cell;
                String street = "";
                String hnr = "";
                int zipCode = 0;
                String city = "";
                while (cellIterator.hasNext() && row.getRowNum() >= 1) {
                    cell = cellIterator.next();
//                    cell.setCellType(Cell.CELL_TYPE_STRING);

                    if(cprNumber == null){
                        reqDetailsForTaskDemand = new HashMap<>();
                    }

                    *//*if (cell.getColumnIndex() == 0) {

                        cell.setCellType(Cell.CELL_TYPE_STRING);
                        //to create task demand

                        String currentValue = cell.getStringCellValue().trim();
                        if(currentValue.length() == 9){
                            currentValue = "0" + currentValue;
                        }
                        if(cprNumber != null && !cprNumber.equalsIgnoreCase(currentValue)){

                            client = clientGraphRepository.findByCPRNumber(cprNumber);
                            Organization organization = organizationGraphRepository.findByName(organizationName);
                            if(organization != null){
                                for(String taskTypeId : taskTypeIds){

                                    Map<String,Object> taskDemandData = reqDetailsForTaskDemand.get(taskTypeId);

                                    ObjectMapper objectMapper = new ObjectMapper();
                                    TaskDemand taskDemand = objectMapper.convertValue(taskDemandData,TaskDemand.class);
                                    List<Date> dates = (List<Date>) taskDemandData.get("dates");
                                    Collections.sort(dates);
                                    taskDemand.setTaskTypeId(new BigInteger(taskTypeId));

                                    taskDemand.setCitizenId(client.getId());
                                    SortedSet<String> weeks = (SortedSet<String>) taskDemandData.get("weeks");
                                    String firstRecoded = weeks.first();
                                    String lastRecoded = weeks.last();

                                    String firstRecoredValues[] = firstRecoded.split("-");
                                    String lastRecoredValues[] = lastRecoded.split("-");

                                    if(firstRecoded.length() >1){
                                        LocalDateTime startDate = DateUtil.getMondayFromWeek(Integer.parseInt(firstRecoredValues[1]),Integer.parseInt(firstRecoredValues[0]));
                                        LocalDateTime endDate = DateUtil.getSundayFromWeek(Integer.parseInt(lastRecoredValues[1]),Integer.parseInt(lastRecoredValues[0]));
                                        taskDemand.setStartDate(Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant()));
                                        taskDemand.setEndDate(Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant()));
                                    }
                                    taskDemand.setUnitId(organization.getId());
                                    taskDemand.setWeekdayFrequency(TaskDemand.WeekFrequency.FOUR_WEEK);
                                    taskDemand.setWeekendFrequency(TaskDemand.WeekFrequency.FOUR_WEEK);
                                    taskDemand.setStaffCount(1);
                                    taskDemand.setPriority(2);
                                    taskDemand.setWeekdaySupplierId(organization.getId());
                                    taskDemand.setWeekendSupplierId(organization.getId());
                                    if(taskDemand.getWeekendVisits()!=null){
                                        for (TaskDemandVisit taskDemandVisit : taskDemand.getWeekendVisits()) {
                                            taskDemandVisit.setId(mongoSequenceRepository.nextSequence());
                                        }
                                    }

                                    if(taskDemand.getWeekdayVisits() != null){
                                        for (TaskDemandVisit taskDemandVisit : taskDemand.getWeekdayVisits()) {
                                            taskDemandVisit.setId(mongoSequenceRepository.nextSequence());
                                        }
                                    }
                                    taskDemandService.save(taskDemand);
                                }
                            }
                            reqDetailsForTaskDemand = new HashMap<>();
                            taskTypeIds.clear();
                        }
                        cprNumber = cell.getStringCellValue().trim();
                        if (cprNumber.length()==9){
                            cprNumber = "0"+cprNumber;
                        }
                    }*//*

                   *//* if (cell.getColumnIndex() == 1) {
                        clientName = cell.getStringCellValue();
                    }

                    if (cell.getColumnIndex() == 2) {
                        cell.setCellType(Cell.CELL_TYPE_STRING);
                        age = cell.getStringCellValue();
                    }*//*

                    //organization service
                    if (cell.getColumnIndex() == 3) {
                        organizationService = new com.kairos.user.domain.domains.organization_service.OrganizationService();
                        organizationService.setName(cell.getStringCellValue());
                        organizationService = organizationServiceService.createOrganizationService(country.getId(), organizationService);
                        name = cell.getStringCellValue();
                    }

                    if (cell.getColumnIndex() == 4) {
                        organizationName = cell.getStringCellValue();
                    }

                    //organization sub service
                    if (cell.getColumnIndex() == 11) {
                        if (organizationService == null) {
                            organizationService = organizationServiceRepository.checkDuplicateService(country.getId(), name);
                        }
                        subService = new com.kairos.user.domain.domains.organization_service.OrganizationService();
                        subService.setName(cell.getStringCellValue());
                        organizationServiceService.addSubService(organizationService.getId(), subService);
                        name = cell.getStringCellValue();

                    }


                    //task types
                    if (cell.getColumnIndex() == 12) {
                        subService = organizationServiceRepository.checkDuplicateSubService(organizationService.getId(), name);
                        taskType = taskTypeMongoRepository.findByTitleAndSubServiceId(cell.getStringCellValue(), subService.getId());
                        if (taskType == null) {
                            taskType = new TaskType();
                            taskType.setTitle(cell.getStringCellValue());
                            taskType.setSubServiceId(subService.getId());
                            taskTypeService.save(taskType);
                        }
                        taskTypeIds.add(taskType.getId().toString());
                        Organization organization = organizationGraphRepository.findByName(organizationName);
                        if (organization != null) {
                            organizationServiceService.updateServiceToOrganization(organization.getId(), subService.getId(), true,ORGANIZATION);

                            System.out.println("count" + taskTypeMongoRepository.findBySubServiceIdAndOrganizationIdAndIsEnabled(subService.getId(),organization.getId(),true));
                            if(taskTypeMongoRepository.findBySubServiceIdAndOrganizationIdAndIsEnabled(subService.getId(),organization.getId(),true).isEmpty()){
                                taskTypeService.linkTaskTypesWithOrg(taskType.getId().toString(), organization.getId(), subService.getId());
                            }
                           *//* client = clientGraphRepository.findByCPRNumber(cprNumber);
                            if (client == null) {
                                String lastName = clientName.substring(clientName.indexOf(" "),clientName.length());
                                String firstName = clientName.substring(0,clientName.indexOf(" "));
                                client = new Client();
                                client.setFirstName(firstName.trim());
                                client.setLastName(lastName.trim());
                                client.setCprNumber(cprNumber);
                                clientGraphRepository.save(client);
                                ClientOrganizationRelation clientOrganizationRelation = new ClientOrganizationRelation();
                                clientOrganizationRelation.setOrganizationTypeHierarchy(organization);
                                clientOrganizationRelation.setClient(client);
                                clientOrganizationRelationGraphRepository.save(clientOrganizationRelation);

                            } else {
                                int count = relationGraphRepository.checkClientOrganizationRelationship(client.getId(),organization.getId());
                                if(count == 0){
                                    ClientOrganizationRelation clientOrganizationRelation = new ClientOrganizationRelation();
                                    clientOrganizationRelation.setOrganizationTypeHierarchy(organization);
                                    clientOrganizationRelation.setClient(client);
                                    clientOrganizationRelationGraphRepository.save(clientOrganizationRelation);
                                }
                            }

                            contactAddress = clientGraphRepository.findOne(client.getId()).getContactAddress();

                            if(contactAddress == null){

                                Cell houseCell = row.getCell(64);
                                Cell zipCodeCell  =  row.getCell(66);
                                Cell cityCell = row.getCell(67);

                                houseCell.setCellType(Cell.CELL_TYPE_STRING);
                                cityCell.setCellType(Cell.CELL_TYPE_STRING);
                                zipCodeCell.setCellType(Cell.CELL_TYPE_STRING);



                                //create address

                                String zipString = zipCodeCell.getStringCellValue();
                                zipString  = zipString.split("\\.", 2)[0];
                                zipCode =Integer.valueOf(zipString);
                                city = cityCell.getStringCellValue();

                                String data = houseCell.getStringCellValue();
                                List<String> strings = Arrays.asList(data.split(","));
                                String addressOnly = strings.get(0);
                                logger.info("Address: "+addressOnly);

                                String[] addressData = addressOnly.split("\\s");

                                for (int i = 0;i<=addressData.length-1;i++) {
                                    if (i==0){
                                        street = addressData[i];
                                        continue;
                                    }
                                    else {
                                        hnr = hnr+" "+addressData[i];
                                    }
                                }
                                street = street.trim();
                                hnr = hnr.trim();
                                logger.info("Street: "+street);
                                logger.info("HNR: "+hnr);
                                addressDTO = new AddressDTO();
                                addressDTO.setCity(city);
                                addressDTO.setHouseNumber(hnr);
                                addressDTO.setZipCodeValue(zipCode);
                                addressDTO.setStreet(street);

                                Map<String,Object> result = addressVerificationService.verifyAddressSheet(addressDTO);
                                Integer geoCodeStatus = (Integer) result.get("statusCode");
                                boolean saveAddress;

                                if(geoCodeStatus == 9){
                                    saveAddress = true;
                                } else if(geoCodeStatus == 1 || geoCodeStatus == 2 || geoCodeStatus == 7){
                                    saveAddress = false;
                                } else if(geoCodeStatus == 3 || geoCodeStatus == 4 || geoCodeStatus == 8){
                                    saveAddress = true;
                                } else if(geoCodeStatus == 0){
                                    saveAddress = false;
                                } else {
                                    saveAddress = false;
                                }

                                // Save Client Address
                                if (saveAddress){
                                    logger.info("Saving Address");
                                    contactAddress = new ContactAddress();
                                    contactAddress.setLongitude(Float.valueOf(String.valueOf(result.get("xCoordinates"))));
                                    contactAddress.setLatitude(Float.valueOf(String.valueOf(result.get("yCoordinates"))));
                                    contactAddress.setHouseNumber(addressDTO.getHouseNumber());

                                    ZipCode zipCodeDb = zipCodeGraphRepository.findByZipCode(addressDTO.getZipCodeValue());
                                    if (zipCodeDb == null) {
                                        logger.info("ZipCode Not Found returning null");
                                        continue;
                                    }
                                    logger.info("ZipCode found: "+zipCodeDb.getName());

                                    Map<String, Object> geographyData = regionService.getAllZipCodesData(zipCodeDb.getId());
                                    if (geographyData==null){
                                        logger.info("Geography  not found with zipcodeId: "+zipCodeDb.getId());
                                        continue;
                                    }
                                    logger.info("Geography Data: "+geographyData);

                                    // Geography Data
                                    contactAddress.setMunicipalityName(String.valueOf(geographyData.get("municipality")));
                                    contactAddress.setProvince(String.valueOf(geographyData.get("province")));
                                    contactAddress.setRegionName(String.valueOf(geographyData.get("region")));
                                    contactAddress.setZipCode(zipCodeDb);
                                    contactAddress.setCity(zipCodeDb.getName());


                                    // Native Details
                                    contactAddress.setStreet(addressDTO.getStreet());
                                    contactAddress.setHouseNumber(addressDTO.getHouseNumber());
                                    contactAddress.setFloorNumber(addressDTO.getFloorNumber());

                                    addressVerificationService.saveAndUpdateClientAddress(client,contactAddress,HAS_HOME_ADDRESS);
                                }
                            }*//*
                        }
                    }

                    //create task demand
                   *//* if(cell.getColumnIndex() == 18){

                        taskTypeDetails = reqDetailsForTaskDemand.get(taskType.getId().toString());

                        if(taskTypeDetails == null){
                            taskTypeDetails = new HashMap<>();
                            reqDetailsForTaskDemand.put(taskType.getId().toString(),taskTypeDetails);
                        }
                    }*//*

                    *//*if(cell.getColumnIndex() == 19){
                        List<Date> dates = (List<Date>) taskTypeDetails.get("dates");
                        if(taskTypeDetails.get("dates") == null){
                            dates = new ArrayList<>();
                            dates.add(cell.getDateCellValue());
                            taskTypeDetails.put("dates",dates);
                        } else {
                            dates.add(cell.getDateCellValue());
                        }
                    }*//*

                    *//*if(cell.getColumnIndex() == 22){
                        if(taskTypeDetails.get("weekend") == null){
                            taskTypeDetails.put("weekend",0);
                        }
                        if(taskTypeDetails.get("weekday") == null){
                            taskTypeDetails.put("weekday",0);
                        }
                        if("Ja".equalsIgnoreCase(cell.getStringCellValue())){
                            int weekendFrequency = (int) taskTypeDetails.get("weekend");
                            taskTypeDetails.put("weekend",weekendFrequency++);
                            weekDay = false;
                        } else {
                            int weekdayFrequency = (int) taskTypeDetails.get("weekday");
                            taskTypeDetails.put("weekday",weekdayFrequency++);
                            weekDay = true;
                        }
                    }*//*

                    *//*if(cell.getColumnIndex() == 23){

                        Organization organization = organizationGraphRepository.findByName(organizationName);
                        if(organization != null){
                            Map<String,Object> timeSlotData= timeSlotService.getTimeSlots(organization.getId());
                            List<Map<String,Object>> timeSlots = (List<Map<String,Object>>) timeSlotData.get("timeSlots");

                            List<Map<String,Object>> visits;
                            if(weekDay){
                                visits = (List<Map<String,Object>>) taskTypeDetails.get("weekdayVisits");
                                if(visits == null){
                                    visits = new ArrayList<>();
                                    taskTypeDetails.put("weekdayVisits",visits);
                                }
                            } else {
                                visits = (List<Map<String,Object>>) taskTypeDetails.get("weekendVisits");
                                if(visits == null){
                                    visits = new ArrayList<>();
                                    taskTypeDetails.put("weekendVisits",visits);
                                }
                                taskTypeDetails.put("weekendVisits",visits);
                            }

                            timeSlot = cell.getStringCellValue();

                            if("Dagvagt".equalsIgnoreCase(timeSlot)){
                                int visitCount;
                                if(weekDay){
                                    Long timeSlotId = null;
                                    String timeSlotName = null;
                                    for(Map<String,Object> map : timeSlots){
                                        if("day".equalsIgnoreCase((String) map.get("name"))){
                                            timeSlotId = (long) map.get("id");
                                            timeSlotName = (String) map.get("name");
                                        }
                                    }


                                    if(visits.isEmpty()){
                                        Cell duration = row.getCell(27);
                                        duration.setCellType(Cell.CELL_TYPE_STRING);
                                        Map<String,Object> weekDayVisit = new HashMap<>();
                                        weekDayVisit.put("timeSlotId",timeSlotId);
                                        weekDayVisit.put("visitCount",1);
                                        weekDayVisit.put("timeSlotName",timeSlotName);
                                        Double dur = Double.parseDouble(duration.getStringCellValue());
                                        weekDayVisit.put("visitDuration",dur.intValue());
                                        visits.add(weekDayVisit);
                                        taskTypeDetails.put("weekdayVisits",visits);
                                    } else {
                                        boolean isExist = false;
                                        for(Map<String,Object> weekDayVisit : visits){
                                            if(weekDayVisit.get("timeSlotId").equals(timeSlotId)){
                                                visitCount = (int) weekDayVisit.get("visitCount");
                                                weekDayVisit.put("visitCount",visitCount+1);
                                                isExist = true;
                                            }
                                        }
                                        if(!isExist){
                                            Cell duration = row.getCell(27);
                                            duration.setCellType(Cell.CELL_TYPE_STRING);
                                            Map<String,Object> weekDayVisit = new HashMap<>();
                                            weekDayVisit.put("timeSlotId",timeSlotId);
                                            weekDayVisit.put("visitCount",1);
                                            weekDayVisit.put("timeSlotName",timeSlotName);
                                            Double dur = Double.parseDouble(duration.getStringCellValue());
                                            weekDayVisit.put("visitDuration",dur.intValue());
                                            visits.add(weekDayVisit);
                                            taskTypeDetails.put("weekdayVisits",visits);
                                        }
                                        taskTypeDetails.put("weekdayVisits",visits);
                                    }

                                } else {
                                    Long timeSlotId = null;
                                    String timeSlotName = null;
                                    for(Map<String,Object> map : timeSlots){
                                        if("day".equalsIgnoreCase((String) map.get("name"))){
                                            timeSlotId = (long) map.get("id");
                                            timeSlotName = (String) map.get("name");
                                        }
                                    }

                                    if(visits.isEmpty()){
                                        Cell duration = row.getCell(27);
                                        duration.setCellType(Cell.CELL_TYPE_STRING);
                                        Map<String,Object> weekendVisit = new HashMap<>();
                                        weekendVisit.put("timeSlotId",timeSlotId);
                                        weekendVisit.put("visitCount",1);
                                        weekendVisit.put("timeSlotName",timeSlotName);
                                        Double dur = Double.parseDouble(duration.getStringCellValue());
                                        weekendVisit.put("visitDuration",dur.intValue());
                                        visits.add(weekendVisit);
                                        taskTypeDetails.put("weekendVisits",visits);
                                    } else {
                                        boolean isExist = false;
                                        for(Map<String,Object> weekDayVisit : visits){
                                            if(weekDayVisit.get("timeSlotId").equals(timeSlotId)){
                                                visitCount = (int) weekDayVisit.get("visitCount");
                                                weekDayVisit.put("visitCount",visitCount+1);
                                                isExist = true;
                                            }
                                        }
                                        if(!isExist){
                                            Cell duration = row.getCell(27);
                                            duration.setCellType(Cell.CELL_TYPE_STRING);
                                            Map<String,Object> weekDayVisit = new HashMap<>();
                                            weekDayVisit.put("timeSlotId",timeSlotId);
                                            weekDayVisit.put("visitCount",1);
                                            weekDayVisit.put("timeSlotName",timeSlotName);
                                            Double dur = Double.parseDouble(duration.getStringCellValue());
                                            weekDayVisit.put("visitDuration",dur.intValue());
                                            visits.add(weekDayVisit);
                                        }
                                        taskTypeDetails.put("weekendVisits",visits);
                                    }
                                }
                            }  else if("Aftenvagt".equalsIgnoreCase(timeSlot)){
                                int visitCount;
                                if(weekDay){
                                    Long timeSlotId = null;
                                    String timeSlotName = null;
                                    for(Map<String,Object> map : timeSlots){
                                        if("evening".equalsIgnoreCase((String) map.get("name"))){
                                            timeSlotId = (long) map.get("id");
                                            timeSlotName = (String) map.get("name");
                                        }
                                    }

                                    if(visits.isEmpty()){
                                        Cell duration = row.getCell(27);
                                        duration.setCellType(Cell.CELL_TYPE_STRING);
                                        Map<String,Object> weekDayVisit = new HashMap<>();
                                        weekDayVisit.put("timeSlotId",timeSlotId);
                                        weekDayVisit.put("visitCount",1);
                                        weekDayVisit.put("timeSlotName",timeSlotName);
                                        Double dur = Double.parseDouble(duration.getStringCellValue());
                                        weekDayVisit.put("visitDuration",dur.intValue());
                                        visits.add(weekDayVisit);
                                        taskTypeDetails.put("weekdayVisits",visits);
                                    } else {
                                        boolean isExist = false;
                                        for(Map<String,Object> weekDayVisit : visits){
                                            if(weekDayVisit.get("timeSlotId").equals(timeSlotId)){
                                                visitCount = (int) weekDayVisit.get("visitCount");
                                                weekDayVisit.put("visitCount",visitCount+1);
                                                isExist = true;
                                            }
                                        }
                                        if(!isExist){
                                            Cell duration = row.getCell(27);
                                            duration.setCellType(Cell.CELL_TYPE_STRING);
                                            Map<String,Object> weekDayVisit = new HashMap<>();
                                            weekDayVisit.put("timeSlotId",timeSlotId);
                                            weekDayVisit.put("visitCount",1);
                                            weekDayVisit.put("timeSlotName",timeSlotName);
                                            Double dur = Double.parseDouble(duration.getStringCellValue());
                                            weekDayVisit.put("visitDuration",dur.intValue());
                                            visits.add(weekDayVisit);
                                        }
                                        taskTypeDetails.put("weekdayVisits",visits);
                                    }

                                } else {
                                    Long timeSlotId = null;
                                    String timeSlotName = null;
                                    for(Map<String,Object> map : timeSlots){
                                        if("evening".equalsIgnoreCase((String) map.get("name"))){
                                            timeSlotId = (long) map.get("id");
                                            timeSlotName = (String) map.get("name");
                                        }
                                    }

                                    if(visits.isEmpty()){
                                        Cell duration = row.getCell(27);
                                        duration.setCellType(Cell.CELL_TYPE_STRING);
                                        Map<String,Object> weekDayVisit = new HashMap<>();
                                        weekDayVisit.put("timeSlotId",timeSlotId);
                                        weekDayVisit.put("visitCount",1);
                                        weekDayVisit.put("timeSlotName",timeSlotName);
                                        Double dur = Double.parseDouble(duration.getStringCellValue());
                                        weekDayVisit.put("visitDuration",dur.intValue());
                                        visits.add(weekDayVisit);
                                        taskTypeDetails.put("weekendVisits",visits);
                                    } else {
                                        boolean isExist = false;
                                        for(Map<String,Object> weekDayVisit : visits){
                                            if(weekDayVisit.get("timeSlotId").equals(timeSlotId)){
                                                visitCount = (int) weekDayVisit.get("visitCount");
                                                weekDayVisit.put("visitCount",visitCount+1);
                                                isExist = true;
                                            }
                                        }
                                        if(!isExist){
                                            Cell duration = row.getCell(27);
                                            duration.setCellType(Cell.CELL_TYPE_STRING);
                                            Map<String,Object> weekDayVisit = new HashMap<>();
                                            weekDayVisit.put("timeSlotId",timeSlotId);
                                            weekDayVisit.put("visitCount",1);
                                            weekDayVisit.put("timeSlotName",timeSlotName);
                                            Double dur = Double.parseDouble(duration.getStringCellValue());
                                            weekDayVisit.put("visitDuration",dur.intValue());
                                            visits.add(weekDayVisit);
                                        }
                                        taskTypeDetails.put("weekendVisits",visits);
                                    }
                                }
                            } else if("Nattevagt".equalsIgnoreCase(timeSlot)){
                                int visitCount;
                                if(weekDay){
                                    Long timeSlotId = null;
                                    String timeSlotName = null;
                                    for(Map<String,Object> map : timeSlots){
                                        if("night".equalsIgnoreCase((String) map.get("name"))){
                                            timeSlotId = (long) map.get("id");
                                            timeSlotName = (String) map.get("name");
                                        }
                                    }

                                    if(visits.isEmpty()){
                                        Cell duration = row.getCell(27);
                                        duration.setCellType(Cell.CELL_TYPE_STRING);
                                        Map<String,Object> weekDayVisit = new HashMap<>();
                                        weekDayVisit.put("timeSlotId",timeSlotId);
                                        weekDayVisit.put("visitCount",1);
                                        weekDayVisit.put("timeSlotName",timeSlotName);
                                        Double dur = Double.parseDouble(duration.getStringCellValue());
                                        weekDayVisit.put("visitDuration",dur.intValue());
                                        visits.add(weekDayVisit);
                                        taskTypeDetails.put("weekdayVisits",visits);
                                    } else {
                                        boolean isExist = false;
                                        for(Map<String,Object> weekDayVisit : visits){
                                            if(weekDayVisit.get("timeSlotId").equals(timeSlotId)){
                                                visitCount = (int) weekDayVisit.get("visitCount");
                                                weekDayVisit.put("visitCount",visitCount+1);
                                                isExist = true;
                                            }
                                        }
                                        if(!isExist){
                                            Cell duration = row.getCell(27);
                                            duration.setCellType(Cell.CELL_TYPE_STRING);
                                            Map<String,Object> weekDayVisit = new HashMap<>();
                                            weekDayVisit.put("timeSlotId",timeSlotId);
                                            weekDayVisit.put("visitCount",1);
                                            weekDayVisit.put("timeSlotName",timeSlotName);
                                            Double dur = Double.parseDouble(duration.getStringCellValue());
                                            weekDayVisit.put("visitDuration",dur.intValue());
                                            visits.add(weekDayVisit);
                                        }
                                        taskTypeDetails.put("weekdayVisits",visits);
                                    }

                                } else {
                                    Long timeSlotId = null;
                                    String timeSlotName = null;
                                    for(Map<String,Object> map : timeSlots){
                                        if("night".equalsIgnoreCase((String) map.get("name"))){
                                            timeSlotId = (long) map.get("id");
                                            timeSlotName = (String) map.get("name");
                                        }
                                    }

                                    if(visits.isEmpty()){
                                        Cell duration = row.getCell(27);
                                        duration.setCellType(Cell.CELL_TYPE_STRING);
                                        Map<String,Object> weekDayVisit = new HashMap<>();
                                        weekDayVisit.put("timeSlotId",timeSlotId);
                                        weekDayVisit.put("visitCount",1);
                                        weekDayVisit.put("timeSlotName",timeSlotName);
                                        Double dur = Double.parseDouble(duration.getStringCellValue());
                                        weekDayVisit.put("visitDuration",dur.intValue());
                                        visits.add(weekDayVisit);
                                        taskTypeDetails.put("weekendVisits",visits);
                                    } else {
                                        boolean isExist = false;
                                        for(Map<String,Object> weekDayVisit : visits){
                                            if(weekDayVisit.get("timeSlotId").equals(timeSlotId)){
                                                visitCount = (int) weekDayVisit.get("visitCount");
                                                weekDayVisit.put("visitCount",visitCount+1);
                                                isExist = true;
                                            }
                                        }
                                        if(!isExist){
                                            Cell duration = row.getCell(27);
                                            duration.setCellType(Cell.CELL_TYPE_STRING);
                                            Map<String,Object> weekDayVisit = new HashMap<>();
                                            weekDayVisit.put("timeSlotId",timeSlotId);
                                            weekDayVisit.put("visitCount",1);
                                            weekDayVisit.put("timeSlotName",timeSlotName);
                                            Double dur = Double.parseDouble(duration.getStringCellValue());
                                            weekDayVisit.put("visitDuration",dur.intValue());
                                            visits.add(weekDayVisit);
                                        }
                                        taskTypeDetails.put("weekendVisits",visits);
                                    }
                                }
                            }
                        }

                    }*//*

                   *//* if(cell.getColumnIndex() == 30){
                        SortedSet<String> weeks = (SortedSet<String>) taskTypeDetails.get("weeks");
                        if(taskTypeDetails.get("weeks") == null){
                            weeks = new TreeSet<>();
                            taskTypeDetails.put("weeks",weeks);
                        }
                        weeks.add(cell.getStringCellValue());
                    }*//*

                }

            }
            *//*client = clientGraphRepository.findByCPRNumber(cprNumber);
            Organization organization = organizationGraphRepository.findByName(organizationName);
            if(organization != null){
                for(String taskTypeId : taskTypeIds){

                    Map<String,Object> taskDemandData = reqDetailsForTaskDemand.get(taskTypeId);

                    ObjectMapper objectMapper = new ObjectMapper();
                    TaskDemand taskDemand = objectMapper.convertValue(taskDemandData,TaskDemand.class);
                    List<Date> dates = (List<Date>) taskDemandData.get("dates");
                    Collections.sort(dates);
                    taskDemand.setTaskTypeId(new BigInteger(taskTypeId));

                    taskDemand.setCitizenId(client.getId());
                    SortedSet<String> weeks = (SortedSet<String>) taskDemandData.get("weeks");
                    String firstRecoded = weeks.first();
                    String lastRecoded = weeks.last();

                    String firstRecoredValues[] = firstRecoded.split("-");
                    String lastRecoredValues[] = lastRecoded.split("-");

                    if(firstRecoded.length() >1){
                        LocalDateTime startDate = DateUtil.getMondayFromWeek(Integer.parseInt(firstRecoredValues[1]),Integer.parseInt(firstRecoredValues[0]));
                        LocalDateTime endDate = DateUtil.getSundayFromWeek(Integer.parseInt(lastRecoredValues[1]),Integer.parseInt(lastRecoredValues[0]));
                        taskDemand.setStartDate(Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant()));
                        taskDemand.setEndDate(Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant()));
                    }
                    taskDemand.setUnitId(organization.getId());
                    taskDemand.setWeekdayFrequency(TaskDemand.WeekFrequency.FOUR_WEEK);
                    taskDemand.setWeekendFrequency(TaskDemand.WeekFrequency.FOUR_WEEK);
                    taskDemand.setStaffCount(1);
                    taskDemand.setPriority(2);
                    taskDemand.setWeekdaySupplierId(organization.getId());
                    taskDemand.setWeekendSupplierId(organization.getId());
                    if(taskDemand.getWeekendVisits()!=null){
                        for (TaskDemandVisit taskDemandVisit : taskDemand.getWeekendVisits()) {
                            taskDemandVisit.setId(mongoSequenceRepository.nextSequence());
                        }
                    }

                    if(taskDemand.getWeekdayVisits() != null){
                        for (TaskDemandVisit taskDemandVisit : taskDemand.getWeekdayVisits()) {
                            taskDemandVisit.setId(mongoSequenceRepository.nextSequence());
                        }
                    }
                    taskDemandService.save(taskDemand);
                }
            }*//*
            inputStream.close();
            long endTime = System.currentTimeMillis();
            System.out.println(endTime - startTime + "ms");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    *//**
     * @author prabjot
     * to update preferred time of visitation demand from excel sheet
     *//*
    public void updatePreferredTimeOfDemand(MultipartFile multipartFile){

        try{
            InputStream inputStream = multipartFile.getInputStream();

            //Create Workbook instance holding refrence to .xlsx file
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

            //Get first/desired sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(2);
            System.out.println("name of excel sheet" + sheet.getSheetName());

            //Iterate through each rows one by one
            Iterator<Row> rowIterator = sheet.iterator();
            Row row;
            String cprNumber = null;
            Cell cell;
            OrganizationService subService;
            OrganizationService organizationService;
            Map<String,Map<String,Object>> reqDetailsForTaskDemand = null;
            Map<String,Object> taskTypeDetails = null;
            TaskType taskType;
            boolean weekDay;
            Set<String> taskTypeIds = new HashSet<>();
            while (rowIterator.hasNext()){

                row = rowIterator.next();
                if(row.getRowNum() > 1){


                    if(cprNumber == null){
                        reqDetailsForTaskDemand = new HashMap<>();
                    }

                    //to get cpr number cell
                    cell = row.getCell(0);
                    cell.setCellType(Cell.CELL_TYPE_STRING);

                    if(cprNumber != null && !cprNumber.equals(cell.getStringCellValue())){

                        if(cprNumber.length() == 9){
                            cprNumber = "0" + cprNumber;
                        }
                        Client client = clientGraphRepository.findByCprNumber(cprNumber);
                        if(client != null){
                            for(String taskTypeId : taskTypeIds){
                                TaskDemand taskDemand = taskDemandMongoRepository.findByCitizenIdAndTaskTypeId(client.getId(),new BigInteger(taskTypeId));
                                System.out.println("task demand" + taskDemand);
                            }

                        }

                    }

                    cprNumber = cell.getStringCellValue();

                    cell = row.getCell(3);
                    String organizationServiceName = "(?i)" + cell.getStringCellValue();

                    cell = row.getCell(11);
                    String subServiceName = "(?i)" + cell.getStringCellValue();

                    logger.info("finding subservice with service name--" + organizationServiceName + "  subservicename  " +subServiceName);

                    subService = organizationServiceRepository.checkDuplicateSubServiceByName(organizationServiceName,subServiceName);

                    System.out.println("sub service" + subService);

                    cell = row.getCell(12);
                    taskType = taskTypeMongoRepository.findByTitleAndSubServiceId(cell.getStringCellValue(), subService.getId());
                    taskTypeIds.add(taskType.getId().toString());
                    System.out.println("task type -->" + taskType.getTitle());

                    taskTypeDetails = reqDetailsForTaskDemand.get(taskType.getId().toString());

                    if(taskTypeDetails == null){
                        taskTypeDetails = new HashMap<>();
                        reqDetailsForTaskDemand.put(taskType.getId().toString(),taskTypeDetails);
                    }

                    cell = row.getCell(22);


                    if("Ja".equalsIgnoreCase(cell.getStringCellValue())){
                        weekDay = false;
                    } else {
                        weekDay = true;
                    }

                    List<Map<String,Object>> weekDayVisits = (List<Map<String,Object>>) taskTypeDetails.get("weekDayVisits");
                    List<Map<String,Object>> weekEndVisits = (List<Map<String,Object>>) taskTypeDetails.get("weekEndVisits");

                    if(weekDayVisits == null){
                        weekDayVisits = new ArrayList<>();
                        taskTypeDetails.put("weekDayVisits",weekDayVisits);
                    }

                    if(weekEndVisits == null){
                        weekEndVisits = new ArrayList<>();
                        taskTypeDetails.put("weekEndVisits",weekEndVisits);
                    }

                    cell = row.getCell(23);
                    String timeSlot = cell.getStringCellValue();

                    cell = row.getCell(20);
                    cell.setCellType(Cell.CELL_TYPE_STRING);

                    if("Dagvagt".equals(timeSlot)){
                        if(weekDay){
                            if(weekDayVisits.isEmpty()){
                                Map<String,Object> map = new HashMap<>();
                                map.put("day",timeSlot);
                                Set<String> time = new HashSet<>();
                                time.add(cell.getStringCellValue());
                                map.put("time",time);
                                weekDayVisits.add(map);
                            } else {
                                boolean isExist = false;
                                for(Map<String,Object> weekDayVisit : weekDayVisits){
                                    if(weekDayVisit.get("day").equals("Dagvagt")){
                                       Set<String> time = (Set<String>) weekDayVisit.get("time");
                                        time.add(cell.getStringCellValue());
                                        isExist = true;
                                    }
                                }
                                if(!isExist){
                                    Map<String,Object> map = new HashMap<>();
                                    map.put("day",timeSlot);
                                    Set<String> time = new HashSet<>();
                                    time.add(cell.getStringCellValue());
                                    map.put("time",time);
                                    weekDayVisits.add(map);
                                }
                            }
                        } else {
                            if(weekEndVisits.isEmpty()){
                                Map<String,Object> map = new HashMap<>();
                                map.put("day",timeSlot);
                                Set<String> time = new HashSet<>();
                                time.add(cell.getStringCellValue());
                                map.put("time",time);
                                weekEndVisits.add(map);
                            } else {
                                boolean isExist = false;
                                for(Map<String,Object> weekEndVisit : weekEndVisits){
                                    if(weekEndVisit.get("day").equals("Dagvagt")){
                                        Set<String> time = (Set<String>) weekEndVisit.get("time");
                                        time.add(cell.getStringCellValue());
                                        isExist = true;
                                    }
                                }
                                if(!isExist){
                                    Map<String,Object> map = new HashMap<>();
                                    map.put("day",timeSlot);
                                    Set<String> time = new HashSet<>();
                                    time.add(cell.getStringCellValue());
                                    map.put("time",time);
                                    weekEndVisits.add(map);
                                }
                            }
                        }


                    } else if("Aftenvagt".equals(timeSlot)){
                        if(weekDay){
                            if(weekDayVisits.isEmpty()){
                                Map<String,Object> map = new HashMap<>();
                                map.put("day",timeSlot);
                                Set<String> time = new HashSet<>();
                                time.add(cell.getStringCellValue());
                                map.put("time",time);
                                weekDayVisits.add(map);
                            } else {
                                boolean isExist = false;
                                for(Map<String,Object> weekDayVisit : weekDayVisits){
                                    if(weekDayVisit.get("day").equals("Aftenvagt")){
                                        Set<String> time = (Set<String>) weekDayVisit.get("time");
                                        time.add(cell.getStringCellValue());
                                        isExist = true;
                                    }
                                }
                                if(!isExist){
                                    Map<String,Object> map = new HashMap<>();
                                    map.put("day",timeSlot);
                                    Set<String> time = new HashSet<>();
                                    time.add(cell.getStringCellValue());
                                    map.put("time",time);
                                    weekDayVisits.add(map);
                                }

                            }
                        } else {
                            if(weekEndVisits.isEmpty()){
                                Map<String,Object> map = new HashMap<>();
                                map.put("day",timeSlot);
                                Set<String> time = new HashSet<>();
                                time.add(cell.getStringCellValue());
                                map.put("time",time);
                            } else {
                                boolean isExist = false;
                                for(Map<String,Object> weekEndVisit : weekEndVisits){
                                    if(weekEndVisit.get("day").equals("Aftenvagt")){
                                        Set<String> time = (Set<String>) weekEndVisit.get("time");
                                        time.add(cell.getStringCellValue());
                                        isExist = true;

                                    }
                                }

                                if(!isExist){
                                    Map<String,Object> map = new HashMap<>();
                                    map.put("day",timeSlot);
                                    Set<String> time = new HashSet<>();
                                    time.add(cell.getStringCellValue());
                                    map.put("time",time);
                                    weekEndVisits.add(map);
                                }
                            }
                        }


                    } else if("Nattevagt".equals(timeSlot)){
                        if(weekDay){
                            if(weekDayVisits.isEmpty()){
                                Map<String,Object> map = new HashMap<>();
                                map.put("day",timeSlot);
                                Set<String> time = new HashSet<>();
                                time.add(cell.getStringCellValue());
                                map.put("time",time);
                                weekDayVisits.add(map);
                            } else {
                                boolean isExist = false;
                                for(Map<String,Object> weekDayVisit : weekDayVisits){
                                    if(weekDayVisit.get("day").equals("Nattevagt")){
                                        Set<String> time = (Set<String>) weekDayVisit.get("time");
                                        time.add(cell.getStringCellValue());
                                        isExist = true;
                                    }
                                }
                                if(!isExist){
                                    Map<String,Object> map = new HashMap<>();
                                    map.put("day",timeSlot);
                                    Set<String> time = new HashSet<>();
                                    time.add(cell.getStringCellValue());
                                    map.put("time",time);
                                    weekDayVisits.add(map);
                                }

                            }
                        } else {
                            if(weekEndVisits.isEmpty()){
                                Map<String,Object> map = new HashMap<>();
                                map.put("day",timeSlot);
                                Set<String> time = new HashSet<>();
                                time.add(cell.getStringCellValue());
                                map.put("time",time);
                                weekEndVisits.add(map);
                            } else {
                                boolean isExist = false;
                                for(Map<String,Object> weekEndVisit : weekEndVisits){
                                    if(weekEndVisit.get("day").equals("Nattevagt")){
                                        Set<String> time = (Set<String>) weekEndVisit.get("time");
                                        time.add(cell.getStringCellValue());
                                        isExist = true;
                                    }
                                }

                                if(!isExist){
                                    Map<String,Object> map = new HashMap<>();
                                    map.put("day",timeSlot);
                                    Set<String> time = new HashSet<>();
                                    time.add(cell.getStringCellValue());
                                    map.put("time",time);
                                    weekEndVisits.add(map);
                                }
                            }
                        }
                    }
                }
            }

            if(cprNumber.length() == 9){
                cprNumber = "0" + cprNumber;
            }
            Client client = clientGraphRepository.findByCprNumber(cprNumber);
            if(client != null){
                for(String taskTypeId : taskTypeIds){
                    TaskDemand taskDemand = taskDemandMongoRepository.findByCitizenIdAndTaskTypeId(client.getId(),new BigInteger(taskTypeId));

                    Map<String,Object> taskDemandData = reqDetailsForTaskDemand.get(taskTypeId);
                    if(taskDemand.getWeekdayVisits() != null){
                        for(TaskDemandVisit taskDemandVisit : taskDemand.getWeekdayVisits()){
                            List<Map<String,Object>> weekdayVisit = (List<Map<String,Object>>) taskDemandData.get("weekDayVisits");


                            for(Map<String,Object> map : weekdayVisit){
                                if(taskDemandVisit.getTimeSlotName().equals("Day") && map.get("day").equals("Dagvagt")){
                                    Set<String> timeValues = (Set<String>) map.get("time");
                                    Double sum = 0d;
                                    for(String time : timeValues){
                                        int timeInNumber = Integer.valueOf(time);
                                        sum = sum + (timeInNumber/100*60);
                                    }
                                    double average = sum/timeValues.size();
                                    String timeInHour = average/60 + "";
                                    String[] timeParts= timeInHour.split("\\.");
                                    if(timeParts.length > 1){
                                        String hour = timeParts[0];
                                        String minute = timeParts[1];
                                        taskDemandVisit.setPreferredHour(hour);
                                        taskDemandVisit.setPreferredMinute(minute);
                                        taskDemandVisit.setPreferredTime(hour + ":" + minute);
                                    }
                                } else if(taskDemandVisit.getTimeSlotName().equals("Evening") && map.get("day").equals("Aftenvagt")){

                                    Set<String> timeValues = (Set<String>) map.get("time");
                                    Double sum = 0d;
                                    for(String time : timeValues){
                                        int timeInNumber = Integer.valueOf(time);
                                        sum = sum + (timeInNumber/100*60);
                                    }
                                    double average = sum/timeValues.size();
                                    String timeInHour = average/60 + "";
                                    String[] timeParts= timeInHour.split("\\.");
                                    if(timeParts.length > 1){
                                        String hour = timeParts[0];
                                        String minute = timeParts[1];
                                        taskDemandVisit.setPreferredHour(hour);
                                        taskDemandVisit.setPreferredMinute(minute);
                                        taskDemandVisit.setPreferredTime(hour + ":" + minute);
                                    }

                                } else if(taskDemandVisit.getTimeSlotName().equals("Night") && map.get("day").equals("Nattevagt")){

                                    Set<String> timeValues = (Set<String>) map.get("time");
                                    Double sum = 0d;
                                    for(String time : timeValues){
                                        int timeInNumber = Integer.valueOf(time);
                                        sum = sum + (timeInNumber/100*60);
                                    }
                                    double average = sum/timeValues.size();
                                    String timeInHour = average/60 + "";
                                    String[] timeParts= timeInHour.split("\\.");
                                    if(timeParts.length > 1){
                                        String hour = timeParts[0];
                                        String minute = timeParts[1];
                                        taskDemandVisit.setPreferredHour(hour);
                                        taskDemandVisit.setPreferredMinute(minute);
                                        taskDemandVisit.setPreferredTime(hour + ":" + minute);
                                    }

                                }
                            }

                        }
                    }

                    if(taskDemand.getWeekendVisits() != null){
                        for(TaskDemandVisit taskDemandVisit : taskDemand.getWeekendVisits()){
                            List<Map<String,Object>> weekendVisit = (List<Map<String,Object>>) taskDemandData.get("weekEndVisits");


                            for(Map<String,Object> map : weekendVisit){
                                if(taskDemandVisit.getTimeSlotName().equals("Day") && map.get("day").equals("Dagvagt")){
                                    Set<String> timeValues = (Set<String>) map.get("time");
                                    Double sum = 0d;
                                    for(String time : timeValues){
                                        int timeInNumber = Integer.valueOf(time);
                                        sum = sum + (timeInNumber/100*60);
                                    }
                                    double average = sum/timeValues.size();
                                    String timeInHour = average/60 + "";
                                    String[] timeParts= timeInHour.split("\\.");
                                    if(timeParts.length > 1){
                                        String hour = timeParts[0];
                                        String minute = timeParts[1];
                                        taskDemandVisit.setPreferredHour(hour);
                                        taskDemandVisit.setPreferredMinute(minute);
                                        taskDemandVisit.setPreferredTime(hour + ":" + minute);
                                    }
                                } else if(taskDemandVisit.getTimeSlotName().equals("Evening") && map.get("day").equals("Aftenvagt")){

                                    Set<String> timeValues = (Set<String>) map.get("time");
                                    Double sum = 0d;
                                    for(String time : timeValues){
                                        int timeInNumber = Integer.valueOf(time);
                                        sum = sum + (timeInNumber/100*60);
                                    }
                                    double average = sum/timeValues.size();
                                    String timeInHour = average/60 + "";
                                    String[] timeParts= timeInHour.split("\\.");
                                    if(timeParts.length > 1){
                                        String hour = timeParts[0];
                                        String minute = timeParts[1];
                                        taskDemandVisit.setPreferredHour(hour);
                                        taskDemandVisit.setPreferredMinute(minute);
                                        taskDemandVisit.setPreferredTime(hour + ":" + minute);
                                    }

                                } else if(taskDemandVisit.getTimeSlotName().equals("Night") && map.get("day").equals("Nattevagt")){

                                    Set<String> timeValues = (Set<String>) map.get("time");
                                    Double sum = 0d;
                                    for(String time : timeValues){
                                        int timeInNumber = Integer.valueOf(time);
                                        sum = sum + (timeInNumber/100*60);
                                    }
                                    double average = sum/timeValues.size();
                                    String timeInHour = average/60 + "";
                                    String[] timeParts= timeInHour.split("\\.");
                                    if(timeParts.length > 1){
                                        String hour = timeParts[0];
                                        String minute = timeParts[1];
                                        taskDemandVisit.setPreferredHour(hour);
                                        taskDemandVisit.setPreferredMinute(minute);
                                        taskDemandVisit.setPreferredTime(hour + ":" + minute);
                                    }

                                }
                            }

                        }
                    }
                    taskDemandService.save(taskDemand);


                }

            }


        } catch (Exception e){
            e.printStackTrace();
        }


    }*/
}
