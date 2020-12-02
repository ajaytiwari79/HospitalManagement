package com.kairos.service.region;

import com.kairos.dto.TranslationInfo;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.user.region.*;
import com.kairos.persistence.repository.user.client.ContactAddressGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.region.MunicipalityGraphRepository;
import com.kairos.persistence.repository.user.region.ProvinceGraphRepository;
import com.kairos.persistence.repository.user.region.RegionGraphRepository;
import com.kairos.persistence.repository.user.region.ZipCodeGraphRepository;
import com.kairos.service.country.CountryService;
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
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static com.kairos.constants.UserMessagesConstants.MESSAGE_ZIPCODE_NOTFOUND;

/**
 * Created by prabjot on 12/12/16.
 */
@Service
@Transactional
public class RegionService {

    @Inject
    private RegionGraphRepository regionGraphRepository;

    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private CountryService countryService;

    @Inject
    private MunicipalityGraphRepository municipalityGraphRepository;
    @Inject
    private ZipCodeGraphRepository zipCodeGraphRepository;

    @Inject
    private ProvinceGraphRepository provinceGraphRepository;

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

    public List<RegionQueryResult> getRegionByCountryId(Long countryId) {
        List<RegionQueryResult> data = regionGraphRepository.findAllRegionsByCountryId(countryId);
        return data;
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
            exceptionService.dataNotFoundByIdException(MESSAGE_ZIPCODE_NOTFOUND);

        }
        HashMap<String,Object> responseData = new HashMap<>();
        responseData.put("geographyData", FormatUtil.formatNeoResponse(regionGraphRepository.getGeographicTreeData(zipcodeId)));

        return responseData;
    }

    public Object batchProcessGeographyExcelSheet(MultipartFile multipartFile, Long countryId) {
        try {
            Iterator<Row> rowIterator = getRowIterator(multipartFile);
            Hashtable<String,ZipCode> zipCodeHashtable = new Hashtable<>();
            Hashtable<String,Municipality> municipalityHashtable = new Hashtable<>();
            Hashtable<String,Region> regionHashtable = new Hashtable<>();
            Hashtable<String,Province> provinceHashtable = new Hashtable<>();
            int regionCount=0, provinceCount= 0,municipalityCount= 0,cityCount = 0,zipCodeCount = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (validateEmptyRow(row)) return Collections.emptyList();
                if (row.getRowNum() <= 1) continue;
                Cell provinceCell = row.getCell(6);
                Cell regionCodeCell = row.getCell(0);
                Cell regionNameCell = row.getCell(1);
                Cell municipalityCodeCell = row.getCell(2);
                Cell municipalityNameCell = row.getCell(3);
                Cell cityNameCell = row.getCell(5);
                Cell zipCodeCell = row.getCell(4);
                setCellType(provinceCell, regionCodeCell, regionNameCell, municipalityCodeCell, municipalityNameCell, cityNameCell, zipCodeCell);
                regionCount = setRegionDetails(countryId, regionHashtable, regionCount, regionCodeCell, regionNameCell);
                provinceCount = setProvinceDetails(regionHashtable, provinceHashtable, provinceCount, provinceCell, regionCodeCell);
                municipalityCount = setMunicipalityDetails(municipalityHashtable, provinceHashtable, municipalityCount, provinceCell, municipalityCodeCell, municipalityNameCell);
                zipCodeCount = setZipDetails(zipCodeHashtable, municipalityHashtable, zipCodeCount, municipalityCodeCell, cityNameCell, zipCodeCell);
            }
        }
        catch (Exception ignored){ }
        return FormatUtil.formatNeoResponse(zipCodeGraphRepository.getAllZipCodeByCountryId(countryId));
    }

    private boolean validateEmptyRow(Row row) {
        if (row.getCell(0) == null || row.getCell(0).toString().isEmpty()) {
            return true;
        }
        return false;
    }

    private Iterator<Row> getRowIterator(MultipartFile multipartFile) throws IOException {
        // Prepare Sheet Stream
        InputStream inputStream = multipartFile.getInputStream();
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = workbook.getSheetAt(0);
        return sheet.iterator();
    }

    private void setCellType(Cell provinceCell, Cell regionCodeCell, Cell regionNameCell, Cell municipalityCodeCell, Cell municipalityNameCell, Cell cityNameCell, Cell zipCodeCell) {
        provinceCell.setCellType(Cell.CELL_TYPE_STRING);
        regionCodeCell.setCellType(Cell.CELL_TYPE_STRING);
        regionNameCell.setCellType(Cell.CELL_TYPE_STRING);
        municipalityCodeCell.setCellType(Cell.CELL_TYPE_STRING);
        municipalityNameCell.setCellType(Cell.CELL_TYPE_STRING);
        cityNameCell.setCellType(Cell.CELL_TYPE_STRING);
        zipCodeCell.setCellType(Cell.CELL_TYPE_STRING);
    }

    private void printInfo(int regionCount, int provinceCount, int municipalityCount, int cityCount, int zipCodeCount) {
        logger.info("--------------------------------");
        logger.info("No. of Province: {}",provinceCount);
        logger.info("No. of Region: {}",regionCount);
        logger.info("No. of Municipality: {}",municipalityCount);
        logger.info("No. of City: {}",cityCount);
        logger.info("No. of ZipCode: {}",zipCodeCount);
        long endTime = System.currentTimeMillis();
        logger.info("total time taken by sheet processing logic--->{} ms", (endTime-System.currentTimeMillis()) );
    }

    private int setZipDetails(Hashtable<String, ZipCode> zipCodeHashtable, Hashtable<String, Municipality> municipalityHashtable, int zipCodeCount, Cell municipalityCodeCell, Cell cityNameCell, Cell zipCodeCell) {
        ZipCode zipCode;
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
        return zipCodeCount;
    }

    private int setMunicipalityDetails(Hashtable<String, Municipality> municipalityHashtable, Hashtable<String, Province> provinceHashtable, int municipalityCount, Cell provinceCell, Cell municipalityCodeCell, Cell municipalityNameCell) {
        Municipality municipality;
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
        return municipalityCount;
    }

    private int setProvinceDetails(Hashtable<String, Region> regionHashtable, Hashtable<String, Province> provinceHashtable, int provinceCount, Cell provinceCell, Cell regionCodeCell) {
        Province province;
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
        return provinceCount;
    }

    private int setRegionDetails(Long countryId, Hashtable<String, Region> regionHashtable, int regionCount, Cell regionCodeCell, Cell regionNameCell) {
        Region region;
        Country country=countryService.findById(countryId);
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
        return regionCount;
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
                logger.info("zip code null of contact address  {}" , contactAddress.getId());
            }

        });
        return true;
    }
}
