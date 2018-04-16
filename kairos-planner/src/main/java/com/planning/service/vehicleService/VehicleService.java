package com.planning.service.vehicleService;

import com.planning.domain.vehicle.PlanningVehicle;
import com.planning.domain.vehicle.VehicleAvailability;
import com.planning.enums.ShiftType;
import com.planning.enums.VehicleType;
import com.planning.repository.vehicleRepository.VehicleRepository;
import com.planning.responseDto.vehicle.OptaVehicleAvailabality;
import com.planning.responseDto.vehicle.OptaVehicleDTO;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VehicleService {

    private static Logger logger = LoggerFactory.getLogger(VehicleService.class);

    @Autowired
    private VehicleRepository vehicleRepository;

    public void saveList(List<PlanningVehicle> planningVehicles) {
        vehicleRepository.saveList(planningVehicles);
    }

    public void save(PlanningVehicle planningVehicle) {
        vehicleRepository.save(planningVehicle);
    }

    public PlanningVehicle findOne(String id) {
        if (StringUtils.isEmpty(id)) return null;
        return vehicleRepository.findOne(id);
    }

    public List<PlanningVehicle> findByIds(List<Long> ids) {
        return (List<PlanningVehicle>) vehicleRepository.findAllByIds(ids);
    }

    public List<OptaVehicleDTO> saveVehicles(List<OptaVehicleDTO> optaVehicleDTOS) {
        List<OptaVehicleDTO> updatedOptaVehicleDtos = new ArrayList<>();
        for (OptaVehicleDTO optaVehicleDto : optaVehicleDTOS) {
            PlanningVehicle planningVehicle = null;
            if (optaVehicleDto.getKairosId() != null && optaVehicleDto.getUnitId() != null) {
                planningVehicle = vehicleRepository.findByExternalId(optaVehicleDto.getKairosId(), optaVehicleDto.getUnitId(), PlanningVehicle.class);
            }
            if (planningVehicle == null) planningVehicle = new PlanningVehicle();
            planningVehicle.setRange(optaVehicleDto.getRange());
            //planningVehicle.setSkills(getSkillIds(optaVehicleDto.getSkills()));
            planningVehicle.setSpeed(optaVehicleDto.getSpeed());
            planningVehicle.setUnitId(optaVehicleDto.getUnitId());
            planningVehicle.setExternalId(optaVehicleDto.getKairosId());
            if (optaVehicleDto.getUnAvailabalities() != null) {
                List<String> vehiclesUnAvailabilities = saveVehiclesAvailabilities(optaVehicleDto.getUnAvailabalities());
                planningVehicle.setAvailabilities(vehiclesUnAvailabilities);
            }
            planningVehicle.setCostPerKM(optaVehicleDto.getCostPerKM());
            planningVehicle.setCapacity(optaVehicleDto.getCapacity());
            planningVehicle.setFuelLimitation(optaVehicleDto.getFuelLimitation());
            planningVehicle.setModelDescription(optaVehicleDto.getModelDescription());
            planningVehicle.setNumber(optaVehicleDto.getNumber());
            planningVehicle.setRegistrationNumber(optaVehicleDto.getRegistrationNumber());
            if (optaVehicleDto.getVehicleType() != null) {
                planningVehicle.setVehicleType(VehicleType.valueOf(optaVehicleDto.getVehicleType()));
            }
            planningVehicle = (PlanningVehicle) vehicleRepository.save(planningVehicle);
            optaVehicleDto.setOptaPlannerId(planningVehicle.getId());
            updatedOptaVehicleDtos.add(optaVehicleDto);
        }
        return updatedOptaVehicleDtos;
    }


    private List<String> saveVehiclesAvailabilities(List<OptaVehicleAvailabality> optaVehicleAvailabalities) {
        List<String> vehicleAvailabilityIds = new ArrayList<>();
        for (OptaVehicleAvailabality optaVehicleAvailability : optaVehicleAvailabalities) {
            VehicleAvailability vehicleAvailability = new VehicleAvailability();
            vehicleAvailability.setFromDate(optaVehicleAvailability.getFromDate());
            vehicleAvailability.setToDate(optaVehicleAvailability.getToDate());
            vehicleAvailability.setShiftType(ShiftType.valueOf(optaVehicleAvailability.getShiftType()));
            vehicleAvailability = vehicleRepository.save(vehicleAvailability);
            vehicleAvailabilityIds.add(vehicleAvailability.getId());
        }
        return vehicleAvailabilityIds;
    }

    public boolean exits(Long externalId, Long unitId) {
        return vehicleRepository.exist(externalId, unitId);
    }

    public boolean deleteByObject(OptaVehicleDTO optaVehicleDTO) {
        return vehicleRepository.deleteByExternalId(optaVehicleDTO.getKairosId(),optaVehicleDTO.getUnitId(),PlanningVehicle.class);
    }

    public void deleteList(List<PlanningVehicle> planningVehicles) {
        vehicleRepository.deleteList(planningVehicles);
    }

    public void delete(Long id) {
        vehicleRepository.deleteById(id, PlanningVehicle.class);
    }

    public OptaVehicleDTO updateVehicle(OptaVehicleDTO optaVehicleDTO) {
        if (optaVehicleDTO.getKairosId() != null && optaVehicleDTO.getUnitId() != null) {
            PlanningVehicle planningVehicle = vehicleRepository.findByExternalId(optaVehicleDTO.getKairosId(), optaVehicleDTO.getUnitId(), PlanningVehicle.class);
            if (planningVehicle != null) {
                planningVehicle.setRange(optaVehicleDTO.getRange());
                //planningVehicle.setSkills(optaVehicleDTO.getOptaSkills());
                planningVehicle.setSpeed(optaVehicleDTO.getSpeed());
                planningVehicle.setVehicleType(VehicleType.valueOf(optaVehicleDTO.getVehicleType()));
                planningVehicle = (PlanningVehicle) vehicleRepository.save(planningVehicle);
                optaVehicleDTO.setOptaPlannerId(planningVehicle.getId());
            }
        }
        return optaVehicleDTO;
    }

    public List<OptaVehicleDTO> updateList(List<OptaVehicleDTO> optaVehicleDTOS) {
        List<OptaVehicleDTO> updatedOptaVehicleDtos = new ArrayList<>();
        for (OptaVehicleDTO optaVehicleDTO : optaVehicleDTOS) {
            if (optaVehicleDTO.getKairosId() != null && optaVehicleDTO.getUnitId() != null) {
                PlanningVehicle planningVehicle = vehicleRepository.findByExternalId(optaVehicleDTO.getKairosId(), optaVehicleDTO.getUnitId(), PlanningVehicle.class);
                if (planningVehicle != null) {
                    planningVehicle.setRange(optaVehicleDTO.getRange());
                    //planningVehicle.setSkills(optaVehicleDTO.getOptaSkills());
                    planningVehicle.setSpeed(optaVehicleDTO.getSpeed());
                    planningVehicle.setVehicleType(VehicleType.valueOf(optaVehicleDTO.getVehicleType()));
                    planningVehicle = (PlanningVehicle) vehicleRepository.save(planningVehicle);
                    optaVehicleDTO.setOptaPlannerId(planningVehicle.getId());
                }
            }
            updatedOptaVehicleDtos.add(optaVehicleDTO);
        }
        return updatedOptaVehicleDtos;
    }

}
