package com.kairos.service.staff;
/*
 *Created By Pavan on 14/11/18
 *
 */

import com.kairos.commons.utils.DateUtils;
import com.kairos.enums.Gender;
import com.kairos.enums.StaffStatusEnum;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.client.ContactDetail;
import com.kairos.persistence.model.staff.SectorAndStaffExpertiseQueryResult;
import com.kairos.persistence.model.staff.StaffExperienceInExpertiseDTO;
import com.kairos.persistence.model.staff.StaffExpertiseQueryResult;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetail;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.SeniorityLevel;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.language.LanguageGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffExpertiseRelationShipGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.service.exception.ExceptionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StaffExpertiseTest {

    @Mock
    private StaffGraphRepository staffGraphRepository;
    @Mock
    private StaffExpertiseRelationShipGraphRepository staffExpertiseRelationShipGraphRepository;
    @Mock
    private ExpertiseGraphRepository expertiseGraphRepository;
    @InjectMocks
    StaffService staffService;
    @Mock
    StaffRetrievalService staffRetrievalService;
    @Mock
    ExceptionService exceptionService;
    @Mock
    LanguageGraphRepository languageGraphRepository;
    @Mock
    UserGraphRepository userGraphRepository;

     List<Long> expertiseIds= new ArrayList<>();

     StaffPersonalDetail staffPersonalDetail=new StaffPersonalDetail();
     StaffPersonalDetail staffPersonalDetailResult=new StaffPersonalDetail();
     List<SectorAndStaffExpertiseQueryResult> sectorWiseExpertise=new ArrayList<>();
     List<StaffExpertiseQueryResult> staffExpertiseQueryResults=new ArrayList<>();
     public List<Expertise> expertiseList;
     public Staff staff;

   @Before
    public void setUp(){


    }

    @Test
    public void saveStaffPersonalDetails() throws ParseException {

        expertiseList=new ArrayList<>();
        staff=new Staff();
        staff.setId(1956L);
        User user=new User();
        user.setCprNumber("1103843142");
        ContactDetail contactDetail=new ContactDetail();
        contactDetail.setId(22071L);
        contactDetail.setPrivatePhone("9876767767");
        List<Long> expertiseIds= new ArrayList<>();
        expertiseIds.add(23234L);
        expertiseIds.add(23238L);
        List<StaffExperienceInExpertiseDTO> expertiseWithExperience=new ArrayList<>();
        StaffExperienceInExpertiseDTO staffExperienceInExpertiseDTO1=new StaffExperienceInExpertiseDTO(189030L,"A EXP",23234L,24,DateUtils.asDate(LocalDate.of(2007,11,15)));
        StaffExperienceInExpertiseDTO staffExperienceInExpertiseDTO2=new StaffExperienceInExpertiseDTO(185502L,"B EXP",23238L,24,DateUtils.asDate(LocalDate.of(2007,11,15)));
        expertiseWithExperience.add(staffExperienceInExpertiseDTO1);
        expertiseWithExperience.add(staffExperienceInExpertiseDTO2);
        staffPersonalDetail.setFirstName("Jasmin");
        staffPersonalDetail.setLastName("J. Toft");
        staffPersonalDetail.setGender(Gender.MALE);
        staffPersonalDetail.setCurrentStatus(StaffStatusEnum.ACTIVE);
        staffPersonalDetail.setCprNumber("1103843142");
        staffPersonalDetail.setContactDetail(contactDetail);
        staffPersonalDetail.setExpertiseIds(expertiseIds);
        staffPersonalDetail.setFamilyName("Jasmin");
        staffPersonalDetail.setExpertiseWithExperience(expertiseWithExperience);



        Expertise expertise1=new Expertise();
        expertise1.setId(23234L);
        expertise1.setName("A EXP");

        Expertise expertise2=new Expertise();
        expertise2.setId(23238L);
        expertise2.setName("B EXP");

        List<SeniorityLevel> seniorityLevels=new ArrayList<>();
        SeniorityLevel seniorityLevel=new SeniorityLevel();
        seniorityLevel.setFrom(0);
        seniorityLevel.setTo(2);

        SeniorityLevel seniorityLevel1=new SeniorityLevel();
        seniorityLevel1.setFrom(2);
        seniorityLevel1.setTo(4);
        seniorityLevels.add(seniorityLevel);
        seniorityLevels.add(seniorityLevel1);

        expertise1.setSeniorityLevel(seniorityLevels);
        expertise2.setSeniorityLevel(seniorityLevels);
        expertiseList.add(expertise1);
        expertiseList.add(expertise2);







        SectorAndStaffExpertiseQueryResult sectorAndStaffExpertiseQueryResult=new SectorAndStaffExpertiseQueryResult();
        sectorAndStaffExpertiseQueryResult.setId(1542L);
        sectorAndStaffExpertiseQueryResult.setName("Sector1");

        StaffExpertiseQueryResult staffExpertiseQueryResult1=new StaffExpertiseQueryResult();
        staffExpertiseQueryResult1.setId(189030L);
        staffExpertiseQueryResult1.setName("A EXP");
        staffExpertiseQueryResult1.setExpertiseId(23234L);
        staffExpertiseQueryResult1.setRelevantExperienceInMonths(132);
        staffExpertiseQueryResult1.setNextSeniorityLevelInMonths(null);
        staffExpertiseQueryResult1.setSeniorityLevel(seniorityLevel);
        staffExpertiseQueryResult1.setExpertiseStartDate(DateUtils.asDate(LocalDate.of(2007,11,15)));
        staffExpertiseQueryResult1.setUnitPositionExists(true);
        staffExpertiseQueryResult1.setSeniorityLevels(seniorityLevels);


        StaffExpertiseQueryResult staffExpertiseQueryResult2=new StaffExpertiseQueryResult();
        staffExpertiseQueryResult2.setId(185502L);
        staffExpertiseQueryResult2.setName("B EXP");
        staffExpertiseQueryResult2.setExpertiseId(23238L);
        staffExpertiseQueryResult2.setRelevantExperienceInMonths(132);
        staffExpertiseQueryResult2.setNextSeniorityLevelInMonths(null);
        staffExpertiseQueryResult2.setSeniorityLevel(seniorityLevel1);
        staffExpertiseQueryResult2.setExpertiseStartDate(DateUtils.asDate(LocalDate.of(2007,11,15)));
        staffExpertiseQueryResult2.setUnitPositionExists(true);
        staffExpertiseQueryResult2.setSeniorityLevels(seniorityLevels);
        staffExpertiseQueryResults.add(staffExpertiseQueryResult1);
        staffExpertiseQueryResults.add(staffExpertiseQueryResult2);
        sectorAndStaffExpertiseQueryResult.setExpertiseWithExperience(staffExpertiseQueryResults);

        staffPersonalDetailResult.setFirstName("Jasmin");
        staffPersonalDetailResult.setLastName("J. Toft");
        staffPersonalDetailResult.setGender(Gender.MALE);
        staffPersonalDetailResult.setCurrentStatus(StaffStatusEnum.ACTIVE);
        staffPersonalDetailResult.setCprNumber("1103843142");
        staffPersonalDetailResult.setContactDetail(contactDetail);
        staffPersonalDetailResult.setExpertiseIds(expertiseIds);
        staffPersonalDetailResult.setFamilyName("Jasmin");
        staffPersonalDetailResult.setExpertiseWithExperience(expertiseWithExperience);
        sectorWiseExpertise.add(sectorAndStaffExpertiseQueryResult);
        staffPersonalDetailResult.setSectorWiseExpertise(sectorWiseExpertise);
        when(staffGraphRepository.findOne(1956l)).thenReturn(staff);
        when(expertiseGraphRepository.findAllById(expertiseIds)).thenReturn(expertiseList);
        when(userGraphRepository.getUserByStaffId(1956L)).thenReturn(user);
        when(staffExpertiseRelationShipGraphRepository.getSectorWiseExpertiseWithExperience(1956L)).thenReturn(sectorWiseExpertise);
        StaffPersonalDetail staffPersonalDetail1 = staffService.savePersonalDetail(1956L,staffPersonalDetail,0l);
        staffPersonalDetail1.setSectorWiseExpertise(sectorWiseExpertise);
        assertEquals(staffPersonalDetail1, staffPersonalDetailResult);
    }

}
