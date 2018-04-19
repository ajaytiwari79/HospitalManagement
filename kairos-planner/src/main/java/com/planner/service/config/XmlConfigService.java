package com.planner.service.config;

import com.planner.responseDto.config.SolverConfigDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;

@Service
public class XmlConfigService {

    private static Logger logger = LoggerFactory.getLogger(XmlConfigService.class);

    public InputStream getXmlConfigStream(SolverConfigDTO solverConfigDTO,String xmlPath){
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        Document doc = null;
        try {
           /* docBuilder = docFactory.newDocumentBuilder();
            doc = docBuilder.parse(new File(xmlPath));
            Node terminationTag = doc.getElementsByTagName("secondsSpentLimit").item(0);
            terminationTag.setTextContent(solverConfigDTO.getTerminationTime().toString());
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            PrintWriter out = new PrintWriter("/media/pradeep/bak/jsi.xml");
            out.write(writer.toString());
            out.close();
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);*/
            InputStream stream = null;//new ByteArrayInputStream(writer.toString().getBytes(StandardCharsets.UTF_8.name()));
            stream = new FileInputStream(new File(xmlPath));
            return stream;
        } catch ( IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
