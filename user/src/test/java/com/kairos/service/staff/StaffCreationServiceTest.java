package com.kairos.service.staff;

import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.staff.staff.StaffCreationDTO;
import com.kairos.dto.user.staff.staff.StaffDTO;
import com.kairos.enums.Gender;
import com.kairos.enums.OrganizationLevel;
import com.kairos.enums.StaffStatusEnum;
import com.kairos.persistence.model.access_permission.AccessGroup;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.account_type.AccountType;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.system_setting.SystemLanguage;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.ZipCode;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.system_setting.SystemLanguageGraphRepository;
import com.kairos.persistence.repository.user.access_permission.AccessGroupRepository;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.client.ContactAddressGraphRepository;
import com.kairos.persistence.repository.user.staff.PositionGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.integration.ActivityIntegrationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

//@RunWith(MockitoJUnitRunner.class)
public class StaffCreationServiceTest {

   /* @InjectMocks
    StaffCreationService staffCreationService;

    @Mock
    StaffAddressService staffAddressService ;

    @Mock
    ExceptionService exceptionService;

    @Mock
    private OrganizationGraphRepository organizationGraphRepository;

    @Mock
    StaffGraphRepository staffGraphRepository;

    @Mock
    UserGraphRepository userGraphRepository;

    @Mock
    SystemLanguageGraphRepository systemLanguageGraphRepository;

    @Mock
    ContactAddressGraphRepository contactAddressGraphRepository;

    @Mock
    StaffService staffService;

    @Mock
    AccessGroupRepository  accessGroupRepository;

    @Mock
    PositionGraphRepository positionGraphRepository;

    static StaffCreationDTO staffCreationDTO;

    static StaffDTO staffDTO;

    static User user = new User();

    static SystemLanguage systemLanguage = new SystemLanguage();

    static ContactAddress contactAddress = new ContactAddress();
    AccessGroup accessGroup = new AccessGroup();

    @Mock
    ActivityIntegrationService activityIntegrationService ;



    //static Organization organization ;

    List<Long> skills = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        skills.add(1L);
        skills.add(2L);


    }

    @Test
    public void createStaffFromWeb() {

        ContactAddress contactAddress = new ContactAddress();

        ZipCode zipCode = new ZipCode();
        zipCode.setEnable(true);
        zipCode.setName("India");
        zipCode.setZipCode(45454);

        Municipality municipality = new Municipality();
        municipality.setCode("111");
        municipality.setEnable(true);
        municipality.setName("NCR");

        contactAddress.setId(1L);
        //ContactAddress contactAddress = new ContactAddress();
        contactAddress.setCity("gurgaon");
        contactAddress.setStreet("sohna road");
        contactAddress.setZipCode(zipCode);
        contactAddress.setHouseNumber("163");
        contactAddress.setLongitude(1.0f);
        contactAddress.setLatitude(1.0f);
        contactAddress.setFloorNumber(1);
        contactAddress.setMunicipality(municipality);
        contactAddress.setCountry("India");
        contactAddress.setRegionName("gurgaon");
        contactAddress.setRegionCode("101");
        contactAddress.setProvince("province");
        contactAddress.setPrimary(true);

        ContactAddress contactAddressNew =new ContactAddress();


        Organization organization = new Organization();
        organization.setId(1L);
        organization.setOrganizationLevel(OrganizationLevel.COUNTRY);
        organization.setParentOrganization(false);
        organization.setContactAddress(contactAddress);


        when(organizationGraphRepository.findOne(1L)).thenReturn(organization);
        when(organizationGraphRepository.getParentOrganizationOfCityLevel(1L)).thenReturn(organization);


         staffCreationDTO = new StaffCreationDTO("testUser","user","2103903761","testFamilyName","testUser@gmail.com", Gender.MALE,"testUserName",1L,1L);

        when(staffGraphRepository.findStaffByEmailInOrganization(staffCreationDTO.getPrivateEmail(), 1L)).thenReturn(null);

          staffDTO = new StaffDTO(1L,"testUser","user", new BigInteger("1234567890"),"testFamilyName",
                "testUser@gmail.com",
                123456789,"buisness@gmail.com",123456789, DateUtils.asDate(LocalDate.of(2007, 11, 15)),1L,1L,skills,1L
                ,1L, StaffStatusEnum.ACTIVE);
   // staffDTO = staffCreationService.createStaffFromWeb(1L,staffCreationDTO);
       // Assert.assertEquals(staffDTO,staffDTO);

        user.setUserName("testUser@gmail.com");


        when(userGraphRepository.findByEmail(user.getEmail())).thenReturn(user);

        systemLanguage.setActive(true);
        systemLanguage.setCode("454");
        when(systemLanguageGraphRepository.getSystemLanguageOfCountry(1L)).thenReturn(systemLanguage);


        when(contactAddressGraphRepository.findOne(1L)).thenReturn(contactAddress);
        when(staffAddressService.getStaffContactAddressByOrganizationAddress(organization)).thenReturn(contactAddress);


        Country country = new Country();
        country.setCode("1000");
        country.setEnabled(false);


        List<AccountType> accountTypes = new ArrayList<>();
        AccountType accountType = new AccountType();
        accountType.setCountry(country);
        accountType.setName("India");


        accessGroup.setId(1L);
        accessGroup.setAllowedDayTypes(true);
        accessGroup.setDescription("testing");
        accessGroup.setEnabled(true);
        accessGroup.setName("testing");
        accessGroup.setRole(AccessGroupRole.STAFF);
        accessGroup.setTypeOfTaskGiver(false);
        accessGroup.setAllowedDayTypes(false);
        accessGroup.setAccountType(accountTypes);
        accessGroup.setEndDate( LocalDate.of( 2019 , 06 , 25 ));



        //staff.setFirstName(staffDTO.getFirstName());
        //doNothing().when(staffCreationService).createEmployment(null,null,null,null,null,false);
        when(accessGroupRepository.findOne(accessGroup.getId())).thenReturn(accessGroup);
         //when(staffCreationService.createStaffFromWeb(1L,staffCreationDTO)).thenReturn(staffDTO);
        StaffDTO staffDTOResult = staffCreationService.createStaffFromWeb(1L,staffCreationDTO);
        Assert.assertEquals(staffDTO,staffDTOResult);


    }*/
}