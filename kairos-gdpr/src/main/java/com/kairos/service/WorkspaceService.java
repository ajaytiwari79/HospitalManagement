package com.kairos.service;


import com.kairos.ExceptionHandler.WorkSpaceExistException;

import org.apache.jackrabbit.api.JackrabbitSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jcr.*;
import javax.security.auth.login.LoginException;

@Service
public class WorkspaceService {


    @Autowired
    private Repository repository;



    public String newWorkSpace(String newworkspace) throws RepositoryException, LoginException {

        System.err.println("+++");
        JackrabbitSession session = (JackrabbitSession)repository.login(new SimpleCredentials("bobby", "admin".toCharArray()));
System.err.println("+++"+session);

        Workspace workspace = session.getWorkspace();
        String[] workspaces = workspace.getAccessibleWorkspaceNames();
        int flag = -1;
        for (String name : workspaces) {
            if (name.equals(newworkspace)) {
                flag = 1;
            }
        }
        if (flag == 1) {
           throw  new WorkSpaceExistException("Workspace with same name Already Exist");
        } else {
            workspace.createWorkspace(newworkspace);

        }
        return newworkspace;
    }


}
