package com.kairos.service;


import org.springframework.stereotype.Service;

import javax.jcr.*;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;
import java.io.*;
import java.util.Calendar;

@Service
public class DocumentVersioningService {


    public Node importFile(Node folderNode, File file, Session session) throws RepositoryException, IOException {

        Node fileNode = folderNode.addNode(file.getName(), "nt:file");
        ValueFactory valueFactory = session.getValueFactory();
        Binary binary = valueFactory.createBinary(new FileInputStream(file));
        Node resNode = fileNode.addNode("jcr:content", "nt:resource");
        resNode.setProperty("jcr:data", binary);
        resNode.setProperty("jcr:lastModified", Calendar.getInstance().getTimeInMillis());
        return fileNode;
    }


    public void VersioningBasic(Node parentNode, Session session, Binary binary) throws RepositoryException {

        Node versioningNode = parentNode.addNode("childversioning", "nt:unstructured");
        versioningNode.addMixin("mix:versionable");

        versioningNode.setProperty("jcr:data", binary);
        versioningNode.save();
        session.save();
        Version firstversion = versioningNode.checkin();

        Node child = parentNode.getNode("childversioning");
        child.checkout();
        child.setProperty("jcr:data", binary);
        session.save();
        Version second = child.checkin();
        Node child2 = parentNode.getNode("childversioning");
        child2.checkout();
        child2.setProperty("jcr:data", binary);
        session.save();

        Version third = child2.checkin();

        VersionHistory versionHistory = versioningNode.getVersionHistory();

        for (VersionIterator it = versionHistory.getAllVersions(); it.hasNext(); ) {
            Version version = (Version) it.next();
            Node node = version.getFrozenNode();
            System.out.println("node name" + node.getProperty("jcr:data").toString());


        }

    }


    public void readNodeFile(Node node, Session session) throws RepositoryException, IOException {
        Node contentnode = node.getNode("jcr:content");
        String name = node.getName();
        InputStream inputStream = contentnode.getProperty("jcr:data").getBinary().getStream();
        StringBuffer file = new StringBuffer();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String s = bufferedReader.readLine();

        while (s != null) {

            file.append(s);
            s = bufferedReader.readLine();
            System.err.println(s);

        }

        System.err.println("file++++++++++" + file);

        contentnode.getProperty("jcr:data").getBinary().dispose();
/*
FileInputStream fileInputStream=new FileInputStream((File) binary);
byte[] buffer=new byte[1000];
int nRead=0;
int total=0;
while ((nRead=fileInputStream.read(buffer))!=-1)
    {

        System.out.println(new String(buffer));
        total += nRead;
getAllVersions
    }

*/


    }


}
