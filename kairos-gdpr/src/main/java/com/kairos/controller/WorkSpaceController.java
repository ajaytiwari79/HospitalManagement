package com.kairos.controller;

import com.kairos.service.WorkspaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.jcr.RepositoryException;
import javax.security.auth.login.LoginException;

@RestController
public class WorkSpaceController {






@Autowired
    private WorkspaceService workspaceService;

@RequestMapping(value = "/newWorkspace" ,method = RequestMethod.POST)
public String newWorkSpace( @RequestParam String workspace) throws RepositoryException, LoginException {

return  workspaceService.newWorkSpace(workspace);


}






}
