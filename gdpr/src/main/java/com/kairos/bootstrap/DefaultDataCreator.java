package com.kairos.bootstrap;

import com.kairos.persistence.model.clause_tag.ClauseTag;
import com.kairos.persistence.repository.clause_tag.ClauseTagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Optional;

@Component
public class DefaultDataCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDataCreator.class);

    @Inject
    ClauseTagRepository clauseTagRepository;

    @PostConstruct
    private void createDefaultClauseTag(){
        ClauseTag defaultClauseTag = clauseTagRepository.findDefaultTag();
        if (Optional.ofNullable(defaultClauseTag).isPresent()) {
            LOGGER.info("Default clause tag is already created.");
        }else{
            defaultClauseTag = new ClauseTag("NONE", true);
            clauseTagRepository.save(defaultClauseTag);
            LOGGER.info("Default clause tag is created successfully.");
        }

    }
}
