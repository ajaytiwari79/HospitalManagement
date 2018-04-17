package com.planning.repository.customRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class CustomRepository extends BaseRepository{
    private static Logger log= LoggerFactory.getLogger(BaseRepository.class);
}
