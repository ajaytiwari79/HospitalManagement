package com.kairos.service.resources;

import com.kairos.persistence.model.user.resources.VehicleLocation;
import com.kairos.persistence.model.user.resources.VehicleLocationDTO;
import com.kairos.persistence.repository.user.resources.VehicleLocationRepository;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * Created by oodles on 13/12/17.
 */
@Service
@Transactional
public class VehicleLocationService {

    @Inject
    private VehicleLocationRepository vehicleLocationRepository;

    @Inject
    private ExceptionService exceptionService;

    public VehicleLocationDTO createVehicleLocation(VehicleLocationDTO vehicleLocationDTO) {
        Boolean vehicleLocationExistByName = vehicleLocationRepository.vehicleLocationExistByName("(?i)" + vehicleLocationDTO.getName(), -1L);
        if (vehicleLocationExistByName) {
            exceptionService.duplicateDataException("message.VehicleLocation.name.exist");
        }
        VehicleLocation vehicleLocation = new VehicleLocation(vehicleLocationDTO.getName(), vehicleLocationDTO.getDescription());
        vehicleLocationRepository.save(vehicleLocation);
        vehicleLocationDTO.setId(vehicleLocation.getId());
        return vehicleLocationDTO;
    }

    public VehicleLocationDTO updateVehicleLocation(VehicleLocationDTO vehicleLocationDTO) {
        Boolean vehicleLocationExistByName = vehicleLocationRepository.vehicleLocationExistByName("(?i)" + vehicleLocationDTO.getName(), vehicleLocationDTO.getId());
        if (vehicleLocationExistByName) {
            exceptionService.duplicateDataException("message.VehicleLocation.name.exist");
        }
        VehicleLocation existingVehicleLocation = vehicleLocationRepository.findOne(vehicleLocationDTO.getId());
        if (Optional.ofNullable(existingVehicleLocation).isPresent()) {
            existingVehicleLocation.setName(vehicleLocationDTO.getName());
            existingVehicleLocation.setDescription(vehicleLocationDTO.getDescription());
            vehicleLocationRepository.save(existingVehicleLocation);
        } else {
            exceptionService.dataNotFoundByIdException("message.vehiclelocationservices.id.notFound");

        }
        return vehicleLocationDTO;
    }

    /**
     * Safe-Delete a VehicleLocation by id
     *
     * @param vehicleLocationId
     */
    public boolean deleteVehicleLocation(Long vehicleLocationId) {
        VehicleLocation vehicleLocation = vehicleLocationRepository.findOne(vehicleLocationId);
        if (Optional.ofNullable(vehicleLocation).isPresent()) {
            vehicleLocation.setEnabled(false);
            return vehicleLocationRepository.save(vehicleLocation) != null;
        } else {
            exceptionService.dataNotFoundByIdException("message.vehiclelocationservices.id.notFound");
        }
        return false;
    }

    public List<VehicleLocationDTO> getAllVehicleLocations() {
        return vehicleLocationRepository.getVehicleLocation();
    }
}
