package com.kairos.controller;

import com.kairos.service.DocumentVersioningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import javax.jcr.*;
import java.io.File;
import java.io.IOException;

@RestController
public class DocumentController {


    @Autowired
    DocumentVersioningService documentVersioningService;


    @Autowired
    Repository repository;


    @Transactional
    @RequestMapping(value = "/version", method = RequestMethod.POST)
    public void VersioningList() throws RepositoryException, IOException {


        File file = new File("/home/bobby/Documents/Kairos/abc.txt");
        file.createNewFile();
        Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()),"default");
        try {
            Node parentnode = session.getRootNode();
            Node node = parentnode.addNode("hello");
            node.setProperty("string", "asd");
            Node filenode = documentVersioningService.importFile(parentnode, file, session);
                 session.save();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {


            session.logout();

        }


    }


    @GetMapping("/versionHistory")
    public void versionHistory() throws RepositoryException, IOException {


        Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()),"default");
        Node node = session.getRootNode();
        NodeIterator nodeIterator = node.getNodes();
        while (nodeIterator.hasNext()) {
            Node node1 = (Node) nodeIterator.next();
            if (node1.getName().equals("abc.txt")) {
                documentVersioningService.readNodeFile(node1, session);
            }
            PropertyIterator property = node1.getProperties();

        }
        session.logout();


    }

}
