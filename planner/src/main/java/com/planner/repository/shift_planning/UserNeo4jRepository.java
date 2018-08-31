package com.planner.repository.shift_planning;

import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

/**
 * @author mohit
 */
@Repository
public class UserNeo4jRepository {

    @Inject
    private Session session;
}
