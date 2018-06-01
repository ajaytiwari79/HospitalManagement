package com.kairos.controller;

import com.kairos.service.WorkspaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.jcr.RepositoryException;
import javax.security.auth.login.LoginException;

@RestController
public class WorkSpaceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkSpaceController.class);

    @Autowired
    private WorkspaceService workspaceService;

    @RequestMapping(value = "/newWorkspace" ,method = RequestMethod.POST)
    public String newWorkSpace( @RequestParam String workspace) throws RepositoryException, LoginException {
        return  workspaceService.newWorkSpace(workspace);

    }
}
