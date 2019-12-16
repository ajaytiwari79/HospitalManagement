package com.kairos.persistence.repository.custom_repository;

import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Collection;

@Repository
public interface CommonRepository {

    <T, ID extends Serializable> Collection<T> findByIds(Class<T> type, Collection<ID> ids, int depth);
}
