package com.kairos.service.organization;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.user.organization.*;
import com.kairos.dto.user.organization.union.*;
import com.kairos.dto.user.staff.client.ContactAddressDTO;
import com.kairos.enums.UnionState;
import com.kairos.enums.reason_code.ReasonCodeType;
import com.kairos.persistence.model.address.MunicipalityQueryResult;
import com.kairos.persistence.model.address.ZipCodeSectorQueryResult;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.reason_code.ReasonCodeResponseDTO;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationBasicResponse;
import com.kairos.persistence.model.organization.OrganizationHierarchyData;
import com.kairos.persistence.model.organization.OrganizationQueryResult;
import com.kairos.persistence.model.organization.union.*;
import com.kairos.persistence.model.query_wrapper.OrganizationCreationData;
import com.kairos.persistence.model.staff.StaffExperienceInExpertiseDTO;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.ZipCode;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.union.LocationGraphRepository;
import com.kairos.persistence.repository.organization.union.SectorGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.ReasonCodeGraphRepository;
import com.kairos.persistence.repository.user.region.MunicipalityGraphRepository;
import com.kairos.persistence.repository.user.region.RegionGraphRepository;
import com.kairos.persistence.repository.user.region.ZipCodeGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.service.access_permisson.AccessGroupService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.staff.StaffRetrievalService;
import com.kairos.utils.DateUtil;
import com.kairos.utils.FormatUtil;
import com.kairos.wrapper.StaffUnionWrapper;
import io.jsonwebtoken.lang.Assert;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by vipul on 13/2/18.
 */
@Service
@Transactional
public class UnionService {
    private final Logger logger = LoggerFactory.getLogger(UnionService.class);
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private ZipCodeGraphRepository zipCodeGraphRepository;
    @Inject
    private RegionGraphRepository regionGraphRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private AccessGroupService accessGroupService;
    @Inject
    private SectorGraphRepository sectorGraphRepository;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private LocationGraphRepository locationGraphRepository;
    @Inject
    private MunicipalityGraphRepository municipalityGraphRepository;
    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject
    private StaffRetrievalService staffRetrievalService;
    @Inject private OrganizationService organizationService;
    @Inject private ReasonCodeGraphRepository reasonCodeGraphRepository;


    public UnionQueryWrapper getAllUnionOfCountry(Long countryId) {
        UnionQueryWrapper unionQueryWrapper = new UnionQueryWrapper();

        OrganizationQueryResult organizationQueryResult = organizationGraphRepository.getAllUnionOfCountry(countryId);
        OrganizationCreationData organizationCreationData = organizationGraphRepository.getOrganizationCreationData(countryId);
        List<Map<String, Object>> zipCodes = FormatUtil.formatNeoResponse(zipCodeGraphRepository.getAllZipCodeByCountryId(countryId));
        organizationCreationData.setZipCodes(zipCodes);
        organizationCreationData.setCompanyTypes(CompanyType.getListOfCompanyType());
        organizationCreationData.setCompanyUnitTypes(CompanyUnitType.getListOfCompanyUnitType());
        organizationCreationData.setAccessGroups(accessGroupService.getCountryAccessGroupsForOrganizationCreation(countryId));
        List<Map<String, Object>> orgData = new ArrayList<>();
        for (Map<String, Object> organizationData : organizationQueryResult.getOrganizations()) {
            HashMap<String, Object> orgBasicData = new HashMap<>();
            orgBasicData.put("orgData", organizationData);
            Map<String, Object> address = (Map<String, Object>) organizationData.get("contactAddress");
            orgBasicData.put("municipalities", (address.get("zipCode") == null) ? Collections.emptyMap() : FormatUtil.formatNeoResponse(regionGraphRepository.getGeographicTreeData((long) address.get("zipCode"))));
            orgData.add(orgBasicData);
        }
        unionQueryWrapper.setGlobalData(organizationCreationData);
        unionQueryWrapper.setUnions(orgData);

        return unionQueryWrapper;
    }

    // TODO USED IN FUTURE
    public List<UnionResponseDTO> getAllUnionByOrganization(Long unitId) {
        Organization organization = organizationGraphRepository.findOne(unitId);
        if (!Optional.ofNullable(organization).isPresent() || !Optional.ofNullable(organization.getOrganizationSubTypes()).isPresent()) {
           exceptionService.dataNotFoundByIdException("message.organisation.notFound");

        }
        List<Long> organizationSubTypeIds = organization.getOrganizationSubTypes().parallelStream().map(organizationType -> organizationType.getId()).collect(Collectors.toList());
        List<UnionResponseDTO> organizationQueryResult = organizationGraphRepository.getAllUnionsByOrganizationSubType(organizationSubTypeIds);
        return organizationQueryResult;
    }



    public List<UnionResponseDTO> getAllApplicableUnionsForOrganization(Long unitId) {
        List<UnionResponseDTO> allUnions = new ArrayList<>();
        Organization organization = organizationGraphRepository.findOne(unitId);
        if (!Optional.ofNullable(organization).isPresent() || !Optional.ofNullable(organization.getOrganizationSubTypes()).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.organisation.notFound");

        }
        List<Long> organizationSubTypeIds = organization.getOrganizationSubTypes().parallelStream().map(organizationType -> organizationType.getId()).collect(Collectors.toList());

        allUnions = organizationGraphRepository.getAllUnionsByOrganizationSubType(organizationSubTypeIds);
        return allUnions;

    }

    public boolean addUnionInOrganization(Long unionId, Long organizationId, boolean joined) {
        Organization organization = organizationGraphRepository.findOne(organizationId);
        if (!Optional.ofNullable(organization).isPresent() || !Optional.ofNullable(organization.getOrganizationSubTypes()).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.organisation.notFound");

        }
        Organization union = organizationGraphRepository.findOne(unionId);
        if (!Optional.ofNullable(union).isPresent() || union.isUnion() == false || union.isEnable() == false) {
    exceptionService.dataNotFoundByIdException("message.union.id.notFound");

        }
        if (joined)
            organizationGraphRepository.addUnionInOrganization(organizationId, unionId, DateUtil.getCurrentDate().getTime());
        else
            organizationGraphRepository.removeUnionFromOrganization(organizationId, unionId, DateUtil.getCurrentDate().getTime());

        return joined;
    }

    public List<Sector> findAllSectorsByCountry(Long countryId) {
        List<Sector> sectors = sectorGraphRepository.findAllSectorsByCountryAndDeletedFalse(countryId);
        if(CollectionUtils.isEmpty(sectors)) {
            exceptionService.dataNotFoundByIdException("message.sector.notFound",countryId);
        }
        return sectors;
    }
    public SectorDTO createSector(SectorDTO sectorDto, Long countryId) {
        if(sectorGraphRepository.existsByName(sectorDto.getName())) {
            exceptionService.duplicateDataException("message.sector.alreadyexists",sectorDto.getName());
        }
        Sector sector = new Sector(sectorDto.getName());
        Country country = countryGraphRepository.findCountryById(countryId);
        if(!Optional.ofNullable(country).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound",countryId);
        }
        sector.setCountry(country);
        sectorGraphRepository.save(sector);
        sectorDto.setId(sector.getId());
        return sectorDto;
    }
    public SectorDTO updateSector(SectorDTO sectorDto, Long sectorId) {
        Sector sector = sectorGraphRepository.findSectorById(sectorId);
        if(!Optional.ofNullable(sector).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.sector.id.notFound",sectorId);
        }
        else if(sector.getName().equals(sectorDto.getName())) {
            exceptionService.duplicateDataException("message.sector.alreadyexists",sectorDto.getName());
        }
        sector.setName(sectorDto.getName());
        sectorGraphRepository.save(sector);
        sectorDto.setId(sector.getId());
        return sectorDto;
    }

    public Boolean deleteSector(Long sectorId) {
        Sector sector = sectorGraphRepository.findSectorById(sectorId);
        if(!Optional.ofNullable(sector).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.sector.id.notFound",sectorId);
        }

        sector.setDeleted(true);
        sectorGraphRepository.save(sector);
        return true;
    }

    public List<Location> findAllLocationsByUnion(Long unionId) {
        List<Location> locations = locationGraphRepository.findLocationsByUnion(unionId);
        if(CollectionUtils.isEmpty(locations)) {
            exceptionService.dataNotFoundByIdException("message.location.notFound",unionId);
        }
        return locations;
    }
    public LocationDTO createLocation(LocationDTO locationDTO, Long unionId) {
        if(locationGraphRepository.existsByName(locationDTO.getName(),unionId)) {
            exceptionService.duplicateDataException("message.location.name.alreadyexists",locationDTO.getName());
        }
        Organization union = organizationGraphRepository.findByIdAndUnionTrueAndIsEnableTrue(unionId);
        if(!Optional.ofNullable(union).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.expertise.unionId.notFound",unionId);
        }

        ContactAddress address = null;
        if(Optional.ofNullable(locationDTO.getAddress()).isPresent()) {
             address = getAddress(locationDTO.getAddress(),false,false,null,null,null);
        }
        Location location = new Location(locationDTO.getName(),address);
        union.getLocations().add(location);
        organizationGraphRepository.save(union);
        locationGraphRepository.save(location);
        locationDTO.setId(location.getId());


        return locationDTO;
    }
    public LocationDTO updateLocation(LocationDTO locationDTO, Long unionId, Long locationId) {
        List<LocationQueryResult> locationqueryResults = locationGraphRepository.findByIdOrNameAndDeletedFalse(locationId,locationDTO.getName(),unionId);

        if(CollectionUtils.isEmpty(locationqueryResults)||!locationqueryResults.get(0).getLocation().getId().equals(locationId)) {
            exceptionService.dataNotFoundByIdException("message.location.not.found",locationId);
        }
        if(!locationqueryResults.get(0).getUnionId().equals(unionId)) {
            exceptionService.invalidRequestException("message.unionId.invalid",unionId);
        }

        else if(locationqueryResults.size()>1) {
            exceptionService.dataNotFoundByIdException("message.location.name.alreadyexists",locationDTO.getName());
        }

        boolean zipCodeUpdated = false;
        boolean municipalityUpdated = false;

            if(Optional.ofNullable(locationqueryResults.get(0).getZipCodeId()).isPresent()) {
                zipCodeUpdated = !locationqueryResults.get(0).getZipCodeId().equals(locationDTO.getAddress().getZipCodeId());
            }
            if(Optional.ofNullable(locationqueryResults.get(0).getMunicipalityId()).isPresent()) {
                municipalityUpdated = !locationqueryResults.get(0).getMunicipalityId().equals(locationDTO.getAddress().getMunicipalityId());
            }


        Location location = locationqueryResults.get(0).getLocation();
        Long addressIdDb = locationqueryResults.get(0).getAddressId();

        if(Optional.ofNullable(locationDTO.getAddress()).isPresent()) {
            location.setAddress(getAddress(locationDTO.getAddress(),zipCodeUpdated,municipalityUpdated,addressIdDb,
                    locationqueryResults.get(0).getZipCodeId(),locationqueryResults.get(0).getMunicipalityId()));
        }
        location.setName(locationDTO.getName());

        locationGraphRepository.save(location);
        return locationDTO;
    }


    public boolean deleteLocation(Long locationId) {
        Location location = locationGraphRepository.findByIdAndDeletedFalse(locationId);
        if(!Optional.ofNullable(location).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.location.id.notFound",locationId);
        }

        location.setDeleted(true);
        locationGraphRepository.save(location);
        return true;
    }


    public UnionDTO createUnion(UnionDTO unionData, long countryId, boolean publish) {

        Country country = countryGraphRepository.findOne(countryId);
        if (country == null) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound", countryId);
        }

        if(organizationGraphRepository.existsByName(unionData.getName())) {
            exceptionService.duplicateDataException("message.union.name.exists", unionData.getName());

        }
        ContactAddress address = null;
            if(!Optional.ofNullable(unionData.getMainAddress()).isPresent()&&publish){
                exceptionService.invalidRequestException("message.publish.address.missing");
            }  else if(Optional.ofNullable(unionData.getMainAddress()).isPresent()){

                address = getAddress(unionData.getMainAddress(),false,false,null,null,null);
            }

            boolean boardingCompleted=false;

            if(publish) {
                validateAddress(unionData.getMainAddress());
                boardingCompleted = true;
                unionData.setState(UnionState.PUBLISHED);
            }
            List<Sector> sectors = null;
            if(!CollectionUtils.isEmpty(unionData.getSectorIds())) {
                 sectors = sectorGraphRepository.findSectorsById(unionData.getSectorIds());
            }


            Organization union = new Organization(unionData.getName(),sectors,address, boardingCompleted,country,true);

            organizationGraphRepository.save(union);

            unionData.setId(union.getId());
        return unionData;
    }

    public UnionDTO updateUnion(UnionDTO unionData, long countryId,Long unionId, boolean publish) {

        Country country = countryGraphRepository.findOne(countryId);
        if (country == null) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound", countryId);
        }

        List<UnionDataQueryResult> unionDataQueryResults = organizationGraphRepository.getUnionCompleteById(unionId,unionData.getName());

        if(CollectionUtils.isEmpty(unionDataQueryResults)||(unionDataQueryResults.size()==1&&!unionDataQueryResults.get(0).getUnion().getId().equals(unionId))) {
            exceptionService.dataNotFoundByIdException("message.union.not.found",unionId);
        }
        else if(unionDataQueryResults.size()>1) {
            exceptionService.dataNotFoundByIdException("message.union.name.alreadyexists",unionData.getName());
        }
        Organization union = unionDataQueryResults.get(0).getUnion();
        if(!publish&&union.isBoardingCompleted()) {
            exceptionService.invalidRequestException("message.publish.union.unpublish");
        }

        Set<Long> sectorIdsDb = unionDataQueryResults.get(0).getSectors().stream().map(sector -> sector.getId()).collect(Collectors.toSet());
        List<Long> sectorIDsCreated = new ArrayList<>(unionData.getSectorIds());
        List<Long> sectorIdsToBeDeleted = new ArrayList<Long>(sectorIdsDb);

        sectorIDsCreated.removeAll(sectorIdsDb);
        sectorIdsToBeDeleted.removeAll(unionData.getSectorIds());
        if(!sectorIdsToBeDeleted.isEmpty()&&!union.isBoardingCompleted()) {
            organizationGraphRepository.deleteUnionSectorRelationShip(sectorIdsToBeDeleted,unionId);
        }else if(!sectorIdsToBeDeleted.isEmpty()&&union.isBoardingCompleted()) {
            exceptionService.unsupportedOperationException("message.sector.unlinked");
        }

        if(! sectorIDsCreated.isEmpty()) {
            organizationGraphRepository.createUnionSectorRelationShip(sectorIDsCreated,unionId);
        }
        if(publish) {
            validateAddress(unionData.getMainAddress());
            union.setBoardingCompleted(true);
            unionData.setState(UnionState.PUBLISHED);
        }


        ContactAddress address = null;
        ZipCode zipCode;
        Municipality municipality;
        boolean zipCodeUpdated = false;
        boolean municipalityUpdated = false;
        UnionDataQueryResult unionDataQueryResult = unionDataQueryResults.get(0);

        if(!Optional.ofNullable(unionData.getMainAddress()).isPresent()&&publish){
            exceptionService.invalidRequestException("message.publish.address.missing");

        }else if(Optional.ofNullable(unionData.getMainAddress()).isPresent()){

        if(Optional.ofNullable(unionDataQueryResult.getZipCode()).isPresent()) {
            zipCodeUpdated = !unionDataQueryResult.getZipCode().getId().equals(unionData.getMainAddress().getZipCodeId());
        }
        if(Optional.ofNullable(unionDataQueryResult.getMunicipality()).isPresent()) {
            municipalityUpdated = !unionDataQueryResult.getMunicipality().getId().equals(unionData.getMainAddress().getMunicipalityId());
        }
        Long zipCodeIdDB = Optional.ofNullable(unionDataQueryResult.getZipCode()).isPresent()?unionDataQueryResult.getZipCode().getId():null;
        Long municipalityIdDB = Optional.ofNullable(unionDataQueryResult.getMunicipality()).isPresent()?unionDataQueryResult.getMunicipality().getId():null;
        address = getAddress(unionData.getMainAddress(),zipCodeUpdated,municipalityUpdated,Optional.ofNullable(unionDataQueryResult.getAddress()).isPresent()?
                unionDataQueryResult.getAddress().getId():null,zipCodeIdDB,municipalityIdDB);
        }

        union.setName(unionData.getName());
        union.setContactAddress(address);

        organizationGraphRepository.save(union);

        unionData.setId(union.getId());
        return unionData;
    }

//    public void getUnionsGlobalData(Long countryId) {
//
//        List<>
//
//    }



    public boolean validateAddress(ContactAddressDTO addressDTO) {
        Assert.isTrue(StringUtils.isNotEmpty(addressDTO.getHouseNumber()),exceptionService.convertMessage("message.houseNumber.null"));
        Assert.isTrue(StringUtils.isNotEmpty(addressDTO.getProvince()),exceptionService.convertMessage("message.province.null"));
        Assert.isTrue(StringUtils.isNotEmpty(addressDTO.getStreet()),exceptionService.convertMessage("message.street.null"));
        Assert.isTrue(StringUtils.isNotEmpty(addressDTO.getCity()),exceptionService.convertMessage("message.city.null"));
        Assert.isTrue(StringUtils.isNotEmpty(addressDTO.getRegionName()),exceptionService.convertMessage("message.region.null"));
        Assert.notNull(addressDTO.getZipCodeId(),exceptionService.convertMessage("message.zipCodeId.null"));
        Assert.notNull(addressDTO.getMunicipalityId(),exceptionService.convertMessage("message.municipality.null"));
        return true;
    }


    /**
     * @Author Yatharth Govil
     * @Last ModifiedBy Yatharth Govil
     * @param addressDTO
     * @param zipCodeUpdated
     * @param municipalityUpdated
     * @param addressId
     * @Description This method is used for creating address object for saving in DB
     * @return ContactAddress
     */
    public ContactAddress getAddress(ContactAddressDTO addressDTO,boolean zipCodeUpdated,boolean municipalityUpdated, Long addressId, Long oldZipCodeId,
                                     Long oldMunicipalityId) {

        ContactAddress contactAddress = new ContactAddress(addressDTO.getHouseNumber(),
                addressDTO.getProvince(),addressDTO.getStreet(),addressDTO.getCity(),addressDTO.getRegionName());

        contactAddress.setId(addressId);
        if(Optional.ofNullable(addressDTO.getZipCodeId()).isPresent()) {
            ZipCode zipCode = zipCodeGraphRepository.findByIdDeletedFalse(addressDTO.getZipCodeId());
            if(!Optional.ofNullable(zipCode).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.zipCode.notFound");
            }
            if(zipCodeUpdated) {
                zipCodeGraphRepository.deleteAddressZipcodeRelation(addressId,oldZipCodeId);
            }
            contactAddress.setZipCode(zipCode);
        }
        if(Optional.ofNullable(addressDTO.getMunicipalityId()).isPresent()) {
            Municipality municipality = municipalityGraphRepository.findByZipCodeIdandIdDeletedFalse(addressDTO.getMunicipalityId(),addressDTO.getZipCodeId());
            if(!Optional.ofNullable(municipality).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.municipality.notFound");
            }
            contactAddress.setMunicipality(municipality);
            if(municipalityUpdated) {
                municipalityGraphRepository.deleteAddressMunicipalityRelation(addressId,oldMunicipalityId);
            }
        }


        return contactAddress;
    }

    public UnionGlobalDataDTO getUnionData(Long countryId) {

        List<UnionDataQueryResult> unionDataObjects = organizationGraphRepository.getUnionData(countryId);
        List<Long> locationIds = unionDataObjects.stream().flatMap(unionDataQueryResult -> unionDataQueryResult.getLocations().stream().map(location -> location.getId())).collect(
                Collectors.toList());
        Set<Long> municipalityIds= unionDataObjects.stream().flatMap(unionDataQueryResult->unionDataQueryResult.getMunicipalities().stream().map(
                municipality->municipality.getId())).collect(Collectors.toSet());
        List<LocationDataQueryResult> locationDataObjects = locationGraphRepository.getLocationData(locationIds);
        municipalityIds.addAll(locationDataObjects.stream().flatMap(locationDataQueryResult -> locationDataQueryResult.getMunicipalities().stream().map(
                municipality -> municipality.getId())).collect(Collectors.toSet()));
        List<MunicipalityQueryResult> municipalityQueryResults = municipalityGraphRepository.findMunicipalityRegionAndProvince(municipalityIds);
        Map<Long,MunicipalityQueryResult> municipalityMap = municipalityQueryResults.stream().collect(Collectors.toMap(municipalityQueryResult->municipalityQueryResult.getMunicipality().getId(),v->v));
        ZipCodeSectorQueryResult zipCodesSectors = zipCodeGraphRepository.getZipCodesAndSectors(countryId);

        List<ZipCodeDTO> zipCodes = null;
        List<SectorDTO> sectors = null;
        if(CollectionUtils.isNotEmpty(zipCodesSectors.getZipCodes())) {
             zipCodes = ObjectMapperUtils.copyPropertiesOfListByMapper(zipCodesSectors.getZipCodes(),ZipCodeDTO.class);
        }
        if(CollectionUtils.isNotEmpty(zipCodesSectors.getSectors())) {
             sectors = ObjectMapperUtils.copyPropertiesOfListByMapper(zipCodesSectors.getSectors(),SectorDTO.class);
        }
        UnionGlobalDataDTO globalDataDTO = new UnionGlobalDataDTO(zipCodes,sectors);

        Map<Long,LocationDataQueryResult> locationDataMap = locationDataObjects.stream().collect(Collectors.toMap(LocationDataQueryResult::getLocationId,
                locationDataQueryResult -> locationDataQueryResult,(first,second)->second));
        List<UnionDataDTO> unionDataDTOS = new ArrayList<>();
        for(UnionDataQueryResult unionDataQueryResult:unionDataObjects) {

            UnionDataDTO unionDataDTO = new UnionDataDTO();
            unionDataDTO.setId(unionDataQueryResult.getUnion().getId());
            unionDataDTO.setName(unionDataQueryResult.getUnion().getName());
            unionDataDTO.setSectors(ObjectMapperUtils.copyPropertiesOfListByMapper(unionDataQueryResult.getSectors(),SectorDTO.class));
            List<LocationDTO> locationDTOS = new ArrayList<LocationDTO>();
            List<MunicipalityDTO> municipalitiesUnion;
            if(Optional.ofNullable(unionDataQueryResult.getAddress()).isPresent()) {
                ContactAddressDTO contactAddressDTOUnion = ObjectMapperUtils.copyPropertiesByMapper(unionDataQueryResult.getAddress(),ContactAddressDTO.class);
                if(Optional.ofNullable(unionDataQueryResult.getZipCode()).isPresent()) {
                    contactAddressDTOUnion.setZipCodeId(unionDataQueryResult.getZipCode().getId());
                    contactAddressDTOUnion.setZipCodeValue(unionDataQueryResult.getZipCode().getZipCode());
                    municipalitiesUnion = ObjectMapperUtils.copyPropertiesOfListByMapper(unionDataQueryResult.getMunicipalities(),MunicipalityDTO.class);
                    updateMunicipalities(municipalitiesUnion,municipalityMap);
                    unionDataDTO.setMunicipalities(municipalitiesUnion);
                }
                if(Optional.ofNullable(unionDataQueryResult.getMunicipality()).isPresent()) {
                    contactAddressDTOUnion.setMunicipalityId(unionDataQueryResult.getMunicipality().getId());
                    contactAddressDTOUnion.setMunicipalityName(unionDataQueryResult.getMunicipality().getName());
                }
                unionDataDTO.setMainAddress(contactAddressDTOUnion);

            }
            updateLocations(locationDataMap,unionDataQueryResult,municipalityMap,locationDTOS);
            unionDataDTO.setLocations(locationDTOS);
            unionDataDTO.setState(unionDataQueryResult.getUnion().isBoardingCompleted()?UnionState.PUBLISHED:UnionState.DRAFT);


            unionDataDTOS.add(unionDataDTO);
        }
        globalDataDTO.setUnions(unionDataDTOS);
        return globalDataDTO;
    }

    public void updateMunicipalities(List<MunicipalityDTO> municipalities,  Map<Long,MunicipalityQueryResult> municipalityMap) {
        for(MunicipalityDTO municipalityDTO:municipalities) {
            MunicipalityQueryResult currentMunicipality = municipalityMap.get(municipalityDTO.getId());
            RegionDTO regionDTO = new RegionDTO(currentMunicipality.getRegion().getId(),currentMunicipality.getRegion().getName());
            ProvinceDTO province = new ProvinceDTO(currentMunicipality.getProvince().getId(),currentMunicipality.getProvince().getName(),regionDTO);
            municipalityDTO.setProvince(province);
        }
    }

    public void updateLocations(Map<Long,LocationDataQueryResult> locationDataMap,UnionDataQueryResult unionDataQueryResult,Map<Long,MunicipalityQueryResult>
            municipalityMap,List<LocationDTO> locationDTOS) {


        for(Location location:unionDataQueryResult.getLocations()) {
            LocationDataQueryResult locationDataQueryResult = locationDataMap.get(location.getId());
            ContactAddressDTO contactAddressDTO = null;
            List<MunicipalityDTO> municipalitiesLocation = null;
            if(Optional.ofNullable(locationDataQueryResult.getAddress()).isPresent()) {
                contactAddressDTO = ObjectMapperUtils.copyPropertiesByMapper(locationDataQueryResult.getAddress(),ContactAddressDTO.class);
                if(Optional.ofNullable(locationDataQueryResult.getZipCode()).isPresent()) {
                    contactAddressDTO.setZipCodeId(locationDataQueryResult.getZipCode().getId());
                    contactAddressDTO.setZipCodeValue(locationDataQueryResult.getZipCode().getZipCode());
                    municipalitiesLocation = ObjectMapperUtils.copyPropertiesOfListByMapper(locationDataQueryResult.getMunicipalities(),MunicipalityDTO.class);
                    updateMunicipalities(municipalitiesLocation,municipalityMap);
                }
                if(Optional.ofNullable(locationDataQueryResult.getMunicipality()).isPresent()) {
                    contactAddressDTO.setMunicipalityId(locationDataQueryResult.getMunicipality().getId());
                    contactAddressDTO.setMunicipalityName(locationDataQueryResult.getMunicipality().getName());
                }
            }

            locationDTOS.add(new LocationDTO(location.getId(),location.getName(),contactAddressDTO,municipalitiesLocation));
        }
    }

    public StaffUnionWrapper getUnitPositionDefaultData(Long unitId, String type, Long staffId) {
        Optional<Staff> staff = staffGraphRepository.findById(staffId);
        if (!staff.isPresent()) {
            exceptionService.dataNotFoundByIdException("message.staff.unitid.notfound");

        }
        List<StaffExperienceInExpertiseDTO> staffSelectedExpertise = staffRetrievalService.getExpertiseWithExperienceByStaffIdAndUnitId(staffId, unitId);
        Organization organization = organizationService.getOrganizationDetail(unitId, type);
        if (!Optional.ofNullable(organization).isPresent() || !Optional.ofNullable(organization.getOrganizationSubTypes()).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.organization.notfound");

        }
        List<Long> organizationSubTypeIds = organization.getOrganizationSubTypes().parallelStream().map(organizationType -> organizationType.getId()).collect(Collectors.toList());
        List<UnionResponseDTO> unions = organizationGraphRepository.getAllUnionsByOrganizationSubType(organizationSubTypeIds);
        List<OrganizationBasicResponse> organizationHierarchy = new ArrayList<>();
        if (organization.isParentOrganization()) {
            organizationHierarchy = organizationGraphRepository.getOrganizationHierarchy(organization.getId());
            /*
            ////Can create Unit Position for WorkCentre only
            if (organization.isWorkcentre()) {
                OrganizationBasicResponse currentOrganization = new OrganizationBasicResponse(organization.getId(), organization.getName());
                organizationHierarchy.add(currentOrganization);
            }*/
        } else {
            OrganizationHierarchyData data = organizationGraphRepository.getChildHierarchyByChildUnit(organization.getId());
                /*
                //Can create Unit Position for WorkCentre only
                OrganizationBasicResponse parentOrganization = new OrganizationBasicResponse(data.getParent().getId(), data.getParent().getName());
                organizationHierarchy.add(parentOrganization);*/
            Iterator itr = data.getChildUnits().listIterator();
            while (itr.hasNext()) {
                Organization thisOrganization = (Organization) itr.next();
                organizationHierarchy.add(new OrganizationBasicResponse(thisOrganization.getId(), thisOrganization.getName()));
            }
        }
        List<ReasonCodeResponseDTO> reasonCodeType = reasonCodeGraphRepository.findReasonCodesByUnitIdAndReasonCodeType(organization.getId(), ReasonCodeType.EMPLOYMENT);
        return new StaffUnionWrapper(unions, organizationHierarchy, reasonCodeType, staffSelectedExpertise);
    }
}
