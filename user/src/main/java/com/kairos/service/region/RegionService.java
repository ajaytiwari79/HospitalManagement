package com.kairos.service.region;

import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.Province;
import com.kairos.persistence.model.user.region.Region;
import com.kairos.persistence.model.user.region.ZipCode;
import com.kairos.persistence.repository.user.client.ContactAddressGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.region.MunicipalityGraphRepository;
import com.kairos.persistence.repository.user.region.ProvinceGraphRepository;
import com.kairos.persistence.repository.user.region.RegionGraphRepository;
import com.kairos.persistence.repository.user.region.ZipCodeGraphRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.FormatUtil;
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
import java.io.InputStream;
import java.util.*;

/**
 * Created by prabjot on 12/12/16.
 */
@Service
@Transactional
public class RegionService {

    @Inject
    RegionGraphRepository regionGraphRepository;

    @Inject
    CountryGraphRepository countryGraphRepository;


    @Inject
    MunicipalityGraphRepository municipalityGraphRepository;
    @Inject
    ZipCodeGraphRepository zipCodeGraphRepository;

    @Inject
    ProvinceGraphRepository provinceGraphRepository;

    @Inject
    private ContactAddressGraphRepository contactAddressGraphRepository;

    @Inject
    private ExceptionService exceptionService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private List<ZipCode> allZipCodes;


    public Map<String, Object> createRegionOfCountry(Long countryId, Region region) {
        Country country = countryGraphRepository.findOne(countryId);
        if (country!=null){
            region.setCountry(country);
            regionGraphRepository.save(region);
            return region.retrieveDetails();
        }
        return null;
    }

    public Map<String, Object> updateRegionById(Region region) {
        Region currentRegion = regionGraphRepository.findOne(region.getId());
        if (currentRegion!=null){
            currentRegion.setName(region.getName());
            currentRegion.setGeoFence(region.getGeoFence());
            currentRegion.setCode(region.getCode());
            currentRegion.setLatitude(region.getLatitude());
            currentRegion.setLongitude(region.getLongitude());
            regionGraphRepository.save(currentRegion);
            return currentRegion.retrieveDetails();
        }
        return null;
    }

    public boolean deleteRegion(long regionId) {
        Region region = regionGraphRepository.findOne(regionId);
        if (region == null) {
            return false;
        }
        region.setEnable(false);
        regionGraphRepository.save(region);
        return true;
    }

    public List<Object> getRegionByCountryId(Long countryId) {
        List<Object> response = new ArrayList<>();
        List<Map<String,Object>> data = regionGraphRepository.findAllRegionsByCountryId(countryId);

        return formatNeoResponse(response, data);

    }

    public List<Object> formatNeoResponse(List<Object> response, List<Map<String, Object>> data) {
        for (Map<String,Object> map: data) {
            Object o = map.get("result");
            response.add(o);
        }
        return response;
    }

    public Region getRegionById(Long id) {
        return regionGraphRepository.findOne(id);
    }


    public List<ZipCode> getZipCodes(){
        return  zipCodeGraphRepository.findAll();
    }

    public List<Municipality> getMunicipalityByZipCode(long zipCodeId){
        return  zipCodeGraphRepository.findMunicipByZipCode(zipCodeId);
    }

    public List<Region> getRegionByMunicipalityId(long municipalityId){
        return  regionGraphRepository.findRegionByMunicipalityId(municipalityId);
    }




    public List<Object> getAllRegionData() {
        List<Map<String,Object >> regionList = new ArrayList<>();
        List<Object> fullRegionList = new ArrayList<>();

        regionList = regionGraphRepository.getAllRegionWithMunicipalities();
        if (regionList!=null){
            return formatNeoResponse(fullRegionList, regionList);
        }
        return null;
    }


    public List<ZipCode> getAllZipCodes() {
        return zipCodeGraphRepository.findAll();
    }


    public Map<String,Object> getAllZipCodesData(long zipcodeId) {
        ZipCode zipCode = zipCodeGraphRepository.findOne(zipcodeId);
        if(zipCode == null){
            exceptionService.dataNotFoundByIdException("message.zipCode.notFound");

        }
        HashMap<String,Object> responseData = new HashMap<>();
        responseData.put("geographyData", FormatUtil.formatNeoResponse(regionGraphRepository.getGeographicTreeData(zipcodeId)));

        return responseData;
    }

    public Object batchProcessGeographyExcelSheet(MultipartFile multipartFile, Long countryId) {
        long startTime = System.currentTimeMillis();
        Country country = countryGraphRepository.findOne(countryId);
        List<Region> response = new ArrayList<>();
        if (country==null){
            exceptionService.dataNotFoundByIdException("message.country.id.notFound",countryId);

        }

        try {

            // Prepare Sheet Stream
            InputStream inputStream = multipartFile.getInputStream();
            if (inputStream==null){
                logger.info("Steam is null");
                return null;
            }
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if(!rowIterator.hasNext()){
                exceptionService.internalServerError("error.xssfsheet.noMoreRow",1);

            }

            Region region;
            Province province;
            Municipality municipality;
            ZipCode zipCode;

            Hashtable<String,ZipCode> zipCodeHashtable = new Hashtable<>();
            Hashtable<String,Municipality> municipalityHashtable = new Hashtable<>();
            Hashtable<String,Region> regionHashtable = new Hashtable<>();
            Hashtable<String,Province> provinceHashtable = new Hashtable<>();



            int regionCount=0;
            int provinceCount= 0;
            int municipalityCount= 0;
            int cityCount = 0;
            int zipCodeCount = 0;

            while (rowIterator.hasNext()) {

                Row row = rowIterator.next();
                if (row.getCell(0) == null || row.getCell(0).toString().isEmpty()) {
                    logger.info("No more rows");
                    return response;
                }
                if (row.getRowNum() <= 1) {
                    logger.info("Header Row found");
                    continue;
                }

                // Initialize Cells
                Cell provinceCell = row.getCell(6);
                Cell regionCodeCell = row.getCell(0);
                Cell regionNameCell = row.getCell(1);
                Cell municipalityCodeCell = row.getCell(2);
                Cell municipalityNameCell = row.getCell(3);
                Cell cityNameCell = row.getCell(5);
                Cell zipCodeCell = row.getCell(4);


                // Set Cell type
                provinceCell.setCellType(Cell.CELL_TYPE_STRING);
                regionCodeCell.setCellType(Cell.CELL_TYPE_STRING);
                regionNameCell.setCellType(Cell.CELL_TYPE_STRING);
                municipalityCodeCell.setCellType(Cell.CELL_TYPE_STRING);
                municipalityNameCell.setCellType(Cell.CELL_TYPE_STRING);
                cityNameCell.setCellType(Cell.CELL_TYPE_STRING);
                zipCodeCell.setCellType(Cell.CELL_TYPE_STRING);


                if(!regionHashtable.containsKey(regionCodeCell.getStringCellValue())){
                    region = regionGraphRepository.findByCode(regionCodeCell.getStringCellValue().trim());
                    if(region == null){
                        region = new Region();
                        region.setCountry(country);
                        region.setName(regionNameCell.getStringCellValue().trim());
                        region.setCode(regionCodeCell.getStringCellValue());
                        regionGraphRepository.save(region);
                        regionCount++;
                    }
                    regionHashtable.put(regionCodeCell.getStringCellValue(),region);
                }

                if(!provinceHashtable.containsKey(provinceCell.getStringCellValue())){
                    province = provinceGraphRepository.findByName(provinceCell.getStringCellValue().trim());
                    if(province == null){
                        province = new Province();
                        province.setName(provinceCell.getStringCellValue().trim());
                        provinceCount++;
                    }
                    province.setRegion(regionHashtable.get(regionCodeCell.getStringCellValue()));
                    provinceGraphRepository.save(province);
                    provinceHashtable.put(provinceCell.getStringCellValue(),province);
                }


                if(!municipalityHashtable.containsKey(municipalityCodeCell.getStringCellValue())){
                    municipality = municipalityGraphRepository.findByCode(municipalityCodeCell.getStringCellValue().trim());
                    if(municipality == null){
                        municipality = new Municipality();
                        municipality.setName(municipalityNameCell.getStringCellValue().trim());
                        municipality.setCode(municipalityCodeCell.getStringCellValue());
                    }
                    municipality.setProvince(provinceHashtable.get(provinceCell.getStringCellValue()));
                    municipalityGraphRepository.save(municipality);
                    municipalityHashtable.put(municipalityCodeCell.getStringCellValue(),municipality);
                    municipalityCount++;
                }

                if(!zipCodeHashtable.containsKey(zipCodeCell.getStringCellValue())){
                    zipCode = zipCodeGraphRepository.findByZipCode(Integer.valueOf(zipCodeCell.getStringCellValue().trim()));
                    if(zipCode == null){
                        zipCode = new ZipCode();
                        zipCode.setZipCode(Integer.parseInt(zipCodeCell.getStringCellValue()));
                        zipCode.setName(cityNameCell.getStringCellValue().trim());
                        zipCodeCount++;
                    }
                    List<Municipality> municipalities = zipCode.getMunicipalities();
                    municipalities.add(municipalityHashtable.get(municipalityCodeCell.getStringCellValue()));
                    zipCode.setMunicipalities(municipalities);
                    zipCodeGraphRepository.save(zipCode);
                    zipCodeHashtable.put(zipCodeCell.getStringCellValue(),zipCode);
                } else {
                    zipCode = zipCodeHashtable.get(zipCodeCell.getStringCellValue());
                    List<Municipality> municipalities = zipCode.getMunicipalities();
                    municipalities.add(municipalityHashtable.get(municipalityCodeCell.getStringCellValue()));
                    zipCode.setMunicipalities(municipalities);
                    zipCodeGraphRepository.save(zipCode);
                }






                /*if (!regionLastCode.contains(regionCodeCell.getStringCellValue())) {
                    region = regionGraphRepository.findByCode(regionCodeCell.getStringCellValue());
                    if (region == null) {
                        logger.info("Unique Region found: " + regionCodeCell.getStringCellValue());
                        region = new Region();
                        region.setCountry(country);
                        region.setName(regionNameCell.getStringCellValue().trim());
                        region.setCode(regionCodeCell.getStringCellValue());
                        regionLastCode.add(regionCodeCell.getStringCellValue());
                        regionGraphRepository.save(region);
                        regionCount++;
                    } else {
                        logger.info("Existing Region found: " + regionCodeCell.getStringCellValue());
                    }
                }

                if (!provinceLastValue.contains(provinceCell.getStringCellValue())) {
                    province = provinceGraphRepository.findByName(provinceCell.getStringCellValue());
                    if (province != null) {
                        logger.info("Existing Province found: " + provinceCell.getStringCellValue());

                        province.setRegion(region);
                    } else {
                        logger.info("Unique Province found: " + provinceCell.getStringCellValue());
                        province = new Province();
                        province.setName(provinceCell.getStringCellValue().trim());
                        provinceLastValue.add(provinceCell.getStringCellValue());
                        province.setRegion(region);
                        provinceGraphRepository.save(province);
                        provinceCount++;

                    }
                }

                if (!municipalityLastCode.contains(municipalityCodeCell.getStringCellValue())) {
                    municipality = municipalityGraphRepository.findByCode(municipalityCodeCell.getStringCellValue());

                    if (municipality != null) {
                        logger.info("Existing Municipality found: " + zipCodeCell.getStringCellValue());

                        municipality.setProvince(province);
                    } else {
                        logger.info("Unique Municipality found: " + municipalityCodeCell.getStringCellValue());
                        province = provinceGraphRepository.findByName(provinceCell.getStringCellValue());
                        municipality = new Municipality();
                        municipality.setName(municipalityNameCell.getStringCellValue().trim());
                        municipality.setCode(municipalityCodeCell.getStringCellValue());

                        municipalityLastCode.add(municipalityCodeCell.getStringCellValue());
                        municipality.setProvince(province);
                        municipalityGraphRepository.save(municipality);
                        municipalityCount++;
                    }


                }

                if (!zipCodeLastValue.contains(zipCodeCell.getStringCellValue())) {
                    logger.info("Unique ZipCode found: " + zipCodeCell.getStringCellValue());
                    municipality = municipalityGraphRepository.findByCode(municipalityCodeCell.getStringCellValue());
                    zipCode = new ZipCode();
                    zipCode.setZipCode(Integer.parseInt(zipCodeCell.getStringCellValue()));
                    zipCode.setName(cityNameCell.getStringCellValue().trim());
                    zipCodeLastValue.add(zipCodeCell.getStringCellValue());
                    zipCode.setMunicipality(municipality);
                    zipCodeGraphRepository.save(zipCode);

                    zipCodeCount++;
                } else {
                    zipCode = zipCodeGraphRepository.findByZipCode(Integer.parseInt(zipCodeCell.getStringCellValue()));
                    if (zipCode != null) {
                        logger.info("Existing ZipCode found: " + zipCodeCell.getStringCellValue());
                        zipCode.setMunicipality(municipality);
                    }
                    zipCode.setMunicipality(municipality);
                }*/
            }

            logger.info("--------------------------------");
            logger.info("No. of Province: "+provinceCount);
            logger.info("No. of Region: "+regionCount);
            logger.info("No. of Municipality: "+municipalityCount);
            logger.info("No. of City: "+cityCount);
            logger.info("No. of ZipCode: "+zipCodeCount);

            long endTime = System.currentTimeMillis();
            logger.info("total time taken by sheet processing logic--->" + (endTime-startTime) + " ms");


        }
        catch (Exception e){
            logger.info("Exception: "+e.toString());
            e.printStackTrace();
        }

        return FormatUtil.formatNeoResponse(zipCodeGraphRepository.getAllZipCodeByCountryId(countryId));
    }

    public boolean setMunicipalityInContactAddress(){
        Iterable<ContactAddress> contactAddresses = contactAddressGraphRepository.findAll(1);
        contactAddresses.forEach(contactAddress -> {
            ZipCode zipCode = contactAddress.getZipCode();
            if(zipCode != null){
                Municipality municipality = municipalityGraphRepository.getMunicipalityByZipCodeId(zipCode.getId());
                contactAddress.setMunicipality(municipality);
                contactAddressGraphRepository.save(contactAddress);
            } else {
                logger.info("zip code null of contact address  " + contactAddress.getId());
            }

        });
        return true;
    }
}
