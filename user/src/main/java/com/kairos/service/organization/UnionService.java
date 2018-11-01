package com.kairos.service.organization;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.user.organization.*;
import com.kairos.dto.user.organization.union.*;
import com.kairos.dto.user.staff.client.ContactAddressDTO;
import com.kairos.enums.UnionState;
import com.kairos.persistence.model.address.MunicipalityQueryResult;
import com.kairos.persistence.model.address.ZipCodeMunicipalityQueryResult;
import com.kairos.persistence.model.address.ZipCodeSectorQueryResult;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationQueryResult;
import com.kairos.persistence.model.organization.union.*;
import com.kairos.persistence.model.query_wrapper.OrganizationCreationData;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.ZipCode;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.union.LocationGraphRepository;
import com.kairos.persistence.repository.organization.union.SectorGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.region.MunicipalityGraphRepository;
import com.kairos.persistence.repository.user.region.RegionGraphRepository;
import com.kairos.persistence.repository.user.region.ZipCodeGraphRepository;
import com.kairos.service.access_permisson.AccessGroupService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.DateUtil;
import com.kairos.utils.FormatUtil;
import io.jsonwebtoken.lang.Assert;
import org.apache.commons.collections.CollectionUtils;
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
        if(locationGraphRepository.existsByName(locationDTO.getName())) {
            exceptionService.duplicateDataException("message.location.name.alreadyexists",locationDTO.getName());
        }
        Organization union = organizationGraphRepository.findByIdAndUnionTrueAndIsEnableTrue(unionId);
        if(!Optional.ofNullable(union).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.expertise.unionId.notFound",unionId);
        }

        ContactAddress address = null;
        if(Optional.ofNullable(locationDTO.getAddress()).isPresent()) {
             address = getAddress(locationDTO.getAddress());
        }
        Location location = new Location(locationDTO.getName(),address);
        union.getLocations().add(location);
        organizationGraphRepository.save(union);
        locationGraphRepository.save(location);
        locationDTO.setId(location.getId());


        return locationDTO;
    }
    public LocationDTO updateLocation(LocationDTO locationDTO, Long unionId, Long locationId) {
        List<LocationQueryResult> locationqueryResults = locationGraphRepository.findByIdOrNameAndDeletedFalse(locationId,locationDTO.getName());

        if(!locationqueryResults.get(0).getUnionId().equals(unionId)) {
            exceptionService.invalidRequestException("message.unionId.invalid",unionId);
        }
        if(CollectionUtils.isEmpty(locationqueryResults)||!locationqueryResults.get(0).getLocation().getId().equals(locationId)) {
            exceptionService.dataNotFoundByIdException("message.location.not.found",locationId);
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
        Long zipCodeIdDb = locationqueryResults.get(0).getZipCodeId();
        Long municipalityIdDb= locationqueryResults.get(0).getMunicipalityId();
        ZipCode zipCode = null;
        Municipality municipality = null;


        if(Optional.ofNullable(locationDTO.getAddress()).isPresent()) {
            location.setAddress(new ContactAddress(locationDTO.getAddress().getHouseNumber(), locationDTO.getAddress().getProvince(), locationDTO.getAddress().getStreet(),
                    locationDTO.getAddress().getCity(), locationDTO.getAddress().getRegionName()));
            if (Optional.ofNullable(locationqueryResults.get(0).getAddressId()).isPresent()) {
                location.getAddress().setId(locationqueryResults.get(0).getAddressId());
            }

            if (Optional.ofNullable(locationDTO.getAddress().getZipCodeId()).isPresent()) {
                zipCode = zipCodeGraphRepository.findByIdDeletedFalse(locationDTO.getAddress().getZipCodeId());
                if (!Optional.ofNullable(zipCode).isPresent()) {
                    exceptionService.dataNotFoundByIdException("message.zipCode.notFound");
                }
                location.getAddress().setZipCode(zipCode);
                if (zipCodeUpdated) {
                    zipCodeGraphRepository.deleteAddressZipcodeRelation(addressIdDb, zipCodeIdDb);
                }
            }
            if (Optional.ofNullable(locationDTO.getAddress().getMunicipalityId()).isPresent()) {
                municipality = municipalityGraphRepository.findByZipCodeIdandIdDeletedFalse(locationDTO.getAddress().getMunicipalityId(), locationDTO.getAddress().getZipCodeId());
                if (!Optional.ofNullable(municipality).isPresent()) {
                    exceptionService.dataNotFoundByIdException("message.municipality.notFound");
                }
                location.getAddress().setMunicipality(municipality);
                if (municipalityUpdated) {
                    municipalityGraphRepository.deleteAddressMunicipalityRelation(addressIdDb, municipalityIdDb);
                }
            }
        }

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

                address = getAddress(unionData.getMainAddress());
            }

            UnionState state = UnionState.DRAFT;
            if(publish) {
                validateAddress(unionData.getMainAddress());
                state= UnionState.PUBLISHED;
            }
            List<Sector> sectors = null;
            if(!CollectionUtils.isEmpty(unionData.getSectorIds())) {
                 sectors = sectorGraphRepository.findSectorsById(unionData.getSectorIds());
            }


            Organization union = new Organization(unionData.getName(),sectors,address, state,country,true);

            organizationGraphRepository.save(union);

            unionData.setId(union.getId());
        return unionData;
    }

    public UnionDTO updateUnion(UnionDTO unionData, long countryId,Long unionId, boolean publish) {

        Country country = countryGraphRepository.findOne(countryId);
        if (country == null) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound", countryId);
        }

        List<UnionCompleteQueryResult> unionCompleteQueryResults = organizationGraphRepository.getUnionCompleteById(unionId,unionData.getName());

        if(CollectionUtils.isEmpty(unionCompleteQueryResults)||(unionCompleteQueryResults.size()==1&&!unionCompleteQueryResults.get(0).getUnion().getId().equals(unionId))) {
            exceptionService.dataNotFoundByIdException("message.union.not.found",unionId);
        }
        else if(unionCompleteQueryResults.size()>1) {
            exceptionService.dataNotFoundByIdException("message.union.name.alreadyexists",unionData.getName());
        }
        Organization union = unionCompleteQueryResults.get(0).getUnion();
        if(!publish&&union.getState().equals(UnionState.PUBLISHED.toString())) {
            exceptionService.invalidRequestException("message.publish.union.unpublish");
        }
        UnionState state;
        if(publish) {
            validateAddress(unionData.getMainAddress());
            union.setState(UnionState.PUBLISHED);
        }
        Set<Long> sectorIdsDb = unionCompleteQueryResults.get(0).getSectors().stream().map(sector -> sector.getId()).collect(Collectors.toSet());
        List<Long> sectorIDsCreated = new ArrayList<>(unionData.getSectorIds());
        List<Long> sectorIdsToBeDeleted = new ArrayList<Long>(sectorIdsDb);
        sectorIDsCreated.removeAll(sectorIdsDb);
        sectorIdsToBeDeleted.removeAll(unionData.getSectorIds());
        if(!sectorIdsToBeDeleted.isEmpty()) {
            organizationGraphRepository.deleteUnionSectorRelationShip(sectorIdsToBeDeleted,unionId);
        }
        if(! sectorIDsCreated.isEmpty()) {
            organizationGraphRepository.createUnionSectorRelationShip(sectorIDsCreated,unionId);
        }

        ContactAddress address = null;
        ZipCode zipCode;
        Municipality municipality;
        boolean zipCodeUpdated = false;
        boolean municipalityUpdated = false;
        UnionCompleteQueryResult unionCompleteQueryResult = unionCompleteQueryResults.get(0);

        if(!Optional.ofNullable(unionData.getMainAddress()).isPresent()&&publish){
            exceptionService.invalidRequestException("message.publish.address.missing");

        }else if(Optional.ofNullable(unionData.getMainAddress()).isPresent()){
            address = new ContactAddress(unionData.getMainAddress().getHouseNumber(), unionData.getMainAddress().getProvince(),unionData.getMainAddress().getStreet(),
                    unionData.getMainAddress().getCity(),unionData.getMainAddress().getRegionName());
            if(Optional.ofNullable(unionCompleteQueryResult.getAddress()).isPresent()) {
                address.setId(unionCompleteQueryResult.getAddress().getId());
            }
            if(Optional.ofNullable(unionCompleteQueryResult.getZipCode()).isPresent()) {
                zipCodeUpdated = !unionCompleteQueryResult.getZipCode().getId().equals(unionData.getMainAddress().getZipCodeId());
            }
            if(Optional.ofNullable(unionCompleteQueryResult.getMunicipality()).isPresent()) {
                municipalityUpdated = !unionCompleteQueryResult.getMunicipality().getId().equals(unionData.getMainAddress().getMunicipalityId());
            }

            if(Optional.ofNullable(unionData.getMainAddress().getZipCodeId()).isPresent()) {
                zipCode = zipCodeGraphRepository.findByIdDeletedFalse(unionData.getMainAddress().getZipCodeId());
                if (!Optional.ofNullable(zipCode).isPresent()) {
                    exceptionService.dataNotFoundByIdException("message.zipCode.notFound");
                }
                address.setZipCode(zipCode);
                if(zipCodeUpdated) {
                    zipCodeGraphRepository.deleteAddressZipcodeRelation(unionCompleteQueryResult.getAddress().getId(),unionCompleteQueryResult.getZipCode().getId());
                }
            }
            if(Optional.ofNullable(unionData.getMainAddress().getMunicipalityId()).isPresent()) {
                municipality = municipalityGraphRepository.findByZipCodeIdandIdDeletedFalse(unionData.getMainAddress().getMunicipalityId(),
                        unionData.getMainAddress().getZipCodeId());
                if (!Optional.ofNullable(municipality).isPresent()) {
                    exceptionService.dataNotFoundByIdException("message.municipality.notFound");
                }
                address.setMunicipality(municipality);
                if(municipalityUpdated) {
                    municipalityGraphRepository.deleteAddressMunicipalityRelation(unionCompleteQueryResult.getAddress().getId(),unionCompleteQueryResult.getMunicipality().getId());
                }
            }
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
        Assert.notNull(addressDTO.getHouseNumber(),"meessage.houseNumber.mull");
        Assert.notNull(addressDTO.getProvince(),"meessage.province.mull");
        Assert.notNull(addressDTO.getStreet(),"meessage.street.mull");
        Assert.notNull(addressDTO.getCity(),"meessage.city.mull");
        Assert.notNull(addressDTO.getRegionName(),"meessage.region.mull");
        Assert.notNull(addressDTO.getZipCodeId(),"message.zipCodeId.null");
        Assert.notNull(addressDTO.getMunicipalityId(),"message.municipality.null");
        return true;
    }
   /* public ContactAddress updateAddress(ContactAddress address, ContactAddressDTO addressDTO, ZipCode zipCode, Municipality municipality) {

        boolean municipalityUpdated;
        boolean zipCodeUpdated;
        if(Optional.ofNullable(municipality).isPresent()) {
            municipalityUpdated= !municipality.getId().equals(addressDTO.getMunicipalityId());
        }

        if(Optional.ofNullable(zipCode).isPresent()) {
            zipCodeUpdated = !zipCode.getId().equals(addressDTO.getZipCodeId());
        }
        Long addressIdDb = address.getId();
        Long zipCodeIdDb = zipCode.getId();
        Long municipalityIdDb= municipality.getId();;
        ZipCodeMunicipalityQueryResult zipCodeMunicipalityQueryResult = null;
        ContactAddress updatedAddress = new ContactAddress(addressDTO.getHouseNumber(), addressDTO.getProvince(),addressDTO.getStreet(),
                addressDTO.getCity(),addressDTO.getRegionName());
        updatedAddress.setId(address.getId());

        if(zipCodeUpdated||municipalityUpdated) {
            zipCodeMunicipalityQueryResult = zipCodeGraphRepository.getZipCodeAndMunicipalityById(addressDTO.getZipCodeId(),
                    addressDTO.getMunicipalityId());
            if(!Optional.ofNullable(zipCodeMunicipalityQueryResult).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.zipCodemunicipality.notFound");
            }
        }
        if(zipCodeUpdated&&municipalityUpdated) {

            zipCodeGraphRepository.deleteAddressAndZipCodeMunicipalityRelation(addressIdDb,zipCodeIdDb,municipalityIdDb);
            updatedAddress.setZipCode(zipCodeMunicipalityQueryResult.getZipCode());
            updatedAddress.setMunicipality(zipCodeMunicipalityQueryResult.getMunicipality());
        }
        else if(zipCodeUpdated) {

            zipCodeGraphRepository.deleteAddressZipcodeRelation(addressIdDb,zipCodeIdDb);
            updatedAddress.setZipCode(zipCodeMunicipalityQueryResult.getZipCode());

        }
        else if(municipalityUpdated) {
            zipCodeGraphRepository.deleteAddressMunicipalityRelation(addressIdDb,municipalityIdDb);
            updatedAddress.setMunicipality(zipCodeMunicipalityQueryResult.getMunicipality());
        }
        return updatedAddress;
    }*/


    public ContactAddress getAddress(ContactAddressDTO addressDTO) {

        ContactAddress contactAddress = new ContactAddress(addressDTO.getHouseNumber(),
                addressDTO.getProvince(),addressDTO.getStreet(),addressDTO.getCity(),addressDTO.getRegionName());

        if(Optional.ofNullable(addressDTO.getZipCodeId()).isPresent()) {
            ZipCode zipCode = zipCodeGraphRepository.findByIdDeletedFalse(addressDTO.getZipCodeId());
            if(!Optional.ofNullable(zipCode).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.zipCode.notFound");
            }
            contactAddress.setZipCode(zipCode);
        }
        if(Optional.ofNullable(addressDTO.getMunicipalityId()).isPresent()) {
            Municipality municipality = municipalityGraphRepository.findByIdDeletedFalse(addressDTO.getZipCodeId());
            if(!Optional.ofNullable(municipality).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.municipality.notFound");
            }
            contactAddress.setMunicipality(municipality);
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

        List<ZipCodeDTO> zipCodes = ObjectMapperUtils.copyPropertiesOfListByMapper(zipCodesSectors.getZipCodes(),ZipCodeDTO.class);
        List<SectorDTO> sectors = ObjectMapperUtils.copyPropertiesOfListByMapper(zipCodesSectors.getSectors(),SectorDTO.class);
        UnionGlobalDataDTO globalDataDTO = new UnionGlobalDataDTO(zipCodes,sectors);

        Map<Long,LocationDataQueryResult> locationDataMap = locationDataObjects.stream().collect(Collectors.toMap(LocationDataQueryResult::getLocationId,
                locationDataQueryResult -> locationDataQueryResult));
        List<UnionDataDTO> unionDataDTOS = new ArrayList<UnionDataDTO>();
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
            unionDataDTO.setLocations(locationDTOS);


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

}
