package com.kairos.persistance.repository.data_inventory.processing_activity;

import com.kairos.persistance.model.data_inventory.processing_activity.ProcessingActivity;

public interface CustomProcessingActivityRepository {

    ProcessingActivity findByName(Long countryid, Long organizationId, String name);
}
