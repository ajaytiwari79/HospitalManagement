package com.kairos.persistence.repository.master_data.data_category_element;

import com.kairos.persistence.model.data_inventory.processing_activity.RelatedDataSubject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RelatedDataSubjectRepository extends JpaRepository<RelatedDataSubject , Long> {
}
