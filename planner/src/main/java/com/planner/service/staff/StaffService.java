package com.planner.service.staff;
import com.planner.domain.staff.Staff;
import com.planner.repository.staff.StaffRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class StaffService {
    private static Logger log= LoggerFactory.getLogger(StaffService.class);

    @Inject
    private com.planner.repository.staffRepository.StaffRepository staffRepository;

    public String test()
    {
       return  staffRepository.findStaffNameById(0L);
    }
    /* @Autowired
    private StaffRepository staffRepository;
    public void createStaff(Long unitId, StaffBasicDetailsDTO staffDTO) {
        Staff staff = new Staff(staffDTO.getFirstName(),staffDTO.getLastName(),staffDTO.getSkills());
        staffRepository.save(staff);
    }

    public void createStaff(Long unitId, List<StaffBasicDetailsDTO> staffDTOs) {
        List<Staff> staffList= new ArrayList<>();
        for(StaffBasicDetailsDTO staffBasicDetailsDTO:staffDTOs){
            Staff staff = new Staff(staffBasicDetailsDTO.getFirstName(),staffBasicDetailsDTO.getLastName(),staffBasicDetailsDTO.getSkills());
            staffList.add(staff);
        }
        staffRepository.saveAll(staffList);
    }

    public void updateStaff(Long staffKairosId, Long unitId, StaffBasicDetailsDTO staffDTO) {
        Staff staff=staffRepository.findByKairosId(BigInteger.valueOf(staffKairosId)).get();
        staff.setSkills(staffDTO.getSkills());
        staffRepository.save(staff);

    }*/

}
