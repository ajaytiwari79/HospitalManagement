package com.planner.service.staff;
import com.kairos.persistence.model.user.staff.StaffBasicDetailsDTO;
import com.planner.domain.staff.Staff;
import com.planner.repository.staff.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigInteger;
@Service
@Transactional
public class StaffService {
    @Autowired
    private StaffRepository staffRepository;
    public void createStaff(Long unitId, StaffBasicDetailsDTO staffDTO) {
        Staff staff = new Staff(staffDTO.getFirstName(),staffDTO.getLastName(),staffDTO.getSkills());
        staffRepository.save(staff);
    }

    public void updateStaff(Long staffKairosId, Long unitId, StaffBasicDetailsDTO staffDTO) {
        Staff staff=staffRepository.findByKairosId(BigInteger.valueOf(staffKairosId)).get();
        staff.setSkills(staffDTO.getSkills());
        staffRepository.save(staff);

    }

}
