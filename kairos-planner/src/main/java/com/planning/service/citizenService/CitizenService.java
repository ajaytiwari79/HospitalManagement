package com.planning.service.citizenService;


import com.planning.domain.citizen.PlanningCitizen;
import com.planning.domain.location.PlanningLocation;
import com.planning.domain.staff.PlanningStaff;
import com.planning.repository.citizenRepository.CitizenRepository;
import com.planning.repository.staffRepository.StaffRepository;
import com.planning.responseDto.citizenDto.OptaCitizenDTO;
import com.planning.service.locationService.LocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CitizenService {

    private static Logger log= LoggerFactory.getLogger(CitizenService.class);

    @Autowired
    private CitizenRepository citizenRepository;
    @Autowired
    private LocationService locationService;
    @Autowired private StaffRepository staffRepository;

    public void saveList(List<PlanningCitizen> planningCitizens){
        citizenRepository.saveList(planningCitizens);
    }

    public void save(PlanningCitizen planningCitizen){
        citizenRepository.save(planningCitizen);
    }

    public PlanningCitizen findOne(String id){
        return (PlanningCitizen) citizenRepository.findById(id,PlanningCitizen.class);
    }

    public List<PlanningCitizen> findByIds(List<String> ids){
        return (List<PlanningCitizen>) citizenRepository.findByIds(ids,PlanningCitizen.class);
    }

    public List<OptaCitizenDTO> saveCitizens(List<OptaCitizenDTO> optaCitizenDTOS){
        List<OptaCitizenDTO> updatedOptaCitizenDtos = new ArrayList<>();
        for (OptaCitizenDTO optaCitizenDTO : optaCitizenDTOS) {
            PlanningCitizen planningCitizen = null;
            if(optaCitizenDTO.getKairosId()!=null){
                planningCitizen = citizenRepository.findByExternalId(optaCitizenDTO.getKairosId(),optaCitizenDTO.getUnitId(),PlanningCitizen.class);
            }
            if (planningCitizen == null) {
                planningCitizen = new PlanningCitizen();
            }
            planningCitizen.setExternalId(optaCitizenDTO.getKairosId());
            planningCitizen.setFirstName(optaCitizenDTO.getFirstName());
            planningCitizen.setLastName(optaCitizenDTO.getLastName());
            /*PlanningLocation planningLocation = locationService.getLocationByLatLong(optaCitizenDTO.getAddress().getLatitude(), optaCitizenDTO.getAddress().getLongitude());
            if (planningLocation != null) planningCitizen.setLocationId(planningLocation.getId());
            else {
                planningLocation = locationService.saveLocation(optaCitizenDTO.getAddress());
                planningCitizen.setLocationId(planningLocation.getId());
            }*/
            planningCitizen.setUnitId(optaCitizenDTO.getUnitId());
            //planningCitizen.setForbidenStaff(optaCitizenDTO.getOptaForbidenStaff());
            //planningCitizen.setPreferedStaff(optaCitizenDTO.getOptaPreferedStaff());
            planningCitizen = (PlanningCitizen) citizenRepository.save(planningCitizen);
            optaCitizenDTO.setOptaPlannerId(planningCitizen.getId());
            updatedOptaCitizenDtos.add(optaCitizenDTO);
        }
        return updatedOptaCitizenDtos;
    }

    public boolean exits(Long externalId,Long unitId){
        return citizenRepository.exist(externalId,unitId);
    }

    public boolean deleteByObject(OptaCitizenDTO optaCitizenDTO){
        return citizenRepository.deleteByExternalId(optaCitizenDTO.getKairosId(),optaCitizenDTO.getUnitId(),PlanningCitizen.class);
    }

    public void deleteList(List<PlanningCitizen> planningCitizens){
        citizenRepository.deleteByObject(planningCitizens);
    }

    public OptaCitizenDTO updateCitizen(OptaCitizenDTO optaCitizenDTO){
        PlanningCitizen planningCitizen = findOne(optaCitizenDTO.getOptaPlannerId());
        if(planningCitizen==null)planningCitizen = new PlanningCitizen();
        planningCitizen.setFirstName(optaCitizenDTO.getFirstName());
        planningCitizen.setLastName(optaCitizenDTO.getLastName());
        /*PlanningLocation planningLocation = locationService.getLocationByLatLong(optaCitizenDTO.getAddress().getLatitude(), optaCitizenDTO.getAddress().getLongitude());
        if(planningLocation!=null)planningCitizen.setLocationId(planningLocation.getId());
        else{
            planningLocation = locationService.saveLocation(optaCitizenDTO.getAddress());
            planningCitizen.setLocationId(planningLocation.getId());
        }*/
        planningCitizen.setExternalId(optaCitizenDTO.getKairosId());
        planningCitizen.setUnitId(optaCitizenDTO.getUnitId());
        planningCitizen.setForbidenStaff(getStaffIds(optaCitizenDTO.getForbidenStaff(),optaCitizenDTO.getUnitId()));
        planningCitizen.setPreferedStaff(getStaffIds(optaCitizenDTO.getPreferedStaff(),optaCitizenDTO.getUnitId()));
        planningCitizen = (PlanningCitizen) citizenRepository.save(planningCitizen);
        optaCitizenDTO.setOptaPlannerId(planningCitizen.getId());
        return optaCitizenDTO;
    }

    public List<String> getStaffIds(List<Long> staffExternalIds,Long unitId){
        List<String> staffIds = new ArrayList<>(staffExternalIds.size());
        for (Long staffExternalId:staffExternalIds) {
            PlanningStaff planningStaff = staffRepository.findByExternalId(staffExternalId,unitId,PlanningStaff.class);
            staffIds.add(planningStaff.getId());
        }
        return staffIds;
    }

    public List<OptaCitizenDTO> updateList(List<OptaCitizenDTO> optaCitizenDTOS){
        List<OptaCitizenDTO> updatedOptaCitizenDTOS = new ArrayList<>();
        for (OptaCitizenDTO optaCitizenDTO : optaCitizenDTOS) {
            PlanningCitizen planningCitizen = citizenRepository.findByExternalId(optaCitizenDTO.getKairosId(),optaCitizenDTO.getUnitId(),PlanningCitizen.class);
            if(planningCitizen==null)planningCitizen = new PlanningCitizen();
            planningCitizen.setFirstName(optaCitizenDTO.getFirstName());
            planningCitizen.setLastName(optaCitizenDTO.getLastName());
          /*  PlanningLocation planningLocation = locationService.getLocationByLatLong(optaCitizenDTO.getAddress().getLatitude(), optaCitizenDTO.getAddress().getLongitude());
            if(planningLocation!=null)planningCitizen.setLocationId(planningLocation.getId());
            else{
                planningLocation = locationService.saveLocation(optaCitizenDTO.getAddress());
                planningCitizen.setLocationId(planningLocation.getId());
            }
          */  planningCitizen.setUnitId(optaCitizenDTO.getUnitId());
            planningCitizen.setForbidenStaff(getStaffIds(optaCitizenDTO.getForbidenStaff(),optaCitizenDTO.getUnitId()));
            planningCitizen.setPreferedStaff(getStaffIds(optaCitizenDTO.getPreferedStaff(),optaCitizenDTO.getUnitId()));
            planningCitizen = (PlanningCitizen) citizenRepository.save(planningCitizen);
            optaCitizenDTO.setOptaPlannerId(planningCitizen.getId());
            updatedOptaCitizenDTOS.add(optaCitizenDTO);
        }
        return updatedOptaCitizenDTOS;
    }

    public List<PlanningCitizen> getAllByUnitId(long unitId){
        return citizenRepository.getAllByUnitId(unitId,PlanningCitizen.class);
    }
}
