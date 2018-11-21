package com.planner.service.config;

import com.kairos.dto.planner.solverconfig.SolverConfigDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.List;

@Service
public class XmlConfigService {

    private static Logger logger = LoggerFactory.getLogger(XmlConfigService.class);

    public InputStream getXmlConfigStream(SolverConfigDTO solverConfigDTO, String xmlPath){
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        Document doc = null;
        try {
            docBuilder = docFactory.newDocumentBuilder();
            doc = docBuilder.parse(new File(xmlPath));
            Node terminationTag = doc.getElementsByTagName("secondsSpentLimit").item(0);
            //TODO pradeep need Refactor
            //terminationTag.setTextContent(solverConfigDTO.getTerminationTime().toString());
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            PrintWriter out = new PrintWriter("/media/pradeep/bak/jsi.xml");
            out.write(writer.toString());
            out.close();
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);
            InputStream stream = null;//new ByteArrayInputStream(writer.toString().getBytes(StandardCharsets.UTF_8.name()));
            stream = new FileInputStream(new File(xmlPath));
            return stream;
        } catch ( IOException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void putElementsInXml(Document baseConfig, List<String> validDrls, String solverConfigDrlParentTag) {
        Element element=(Element)baseConfig.getElementsByTagName(solverConfigDrlParentTag).item(0);
        for(String validDrl:validDrls){
            Element drl=baseConfig.createElement("scoreDrl");
            drl.setTextContent(validDrl);
            element.appendChild(drl);
        }
    }
}
