package com.kairos.service.resources;

import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.persistence.model.user.resources.ResourceWrapper;
import com.kairos.persistence.model.user.resources.VehicleLocation;
import com.kairos.persistence.repository.user.resources.VehicleLocationRepository;
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

    public VehicleLocation createVehicleLocation(VehicleLocation vehicleLocation){

        return vehicleLocationRepository.save(vehicleLocation);
    }

    public VehicleLocation updateVehicleLocation(VehicleLocation vehicleLocation, Long vehicleLocationId){

        VehicleLocation existingVehicleLocation = vehicleLocationRepository.findOne(vehicleLocationId);
        if (Optional.ofNullable(existingVehicleLocation).isPresent()) {
            existingVehicleLocation.setName(vehicleLocation.getName());
            existingVehicleLocation.setDescription(vehicleLocation.getDescription());
            return vehicleLocationRepository.save(existingVehicleLocation);
        }
        throw new DataNotFoundByIdException("Vehicle Location not found by id");
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
        }
        throw new DataNotFoundByIdException("Vehicle Location not found by id");
    }

    public List<VehicleLocation> getAllVehicleLocations() {
        return vehicleLocationRepository.findAll();
    }
}
