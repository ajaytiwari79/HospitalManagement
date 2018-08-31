package com.planner.service.shift_planning;

import com.planner.repository.shift_planning.UserNeo4jRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class UserNeo4jService {
    @Inject
    private UserNeo4jRepository userNeo4jRepository;
}
