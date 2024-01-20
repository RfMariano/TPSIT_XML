package me.riefolo.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

public class XML {
    public XML(File xmlFile) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        xmlDocument = builder.parse(xmlFile);
        filePath = xmlFile.getPath();
        xmlDocument.getDocumentElement().normalize();
    }

    public Element getRootElement() {
        return xmlDocument.getDocumentElement();
    }

    public Element makeNewElement(String tagName) {
        return xmlDocument.createElement(tagName);
    }

    public void saveToFile() {
        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = transFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        }
        StringWriter buffer = new StringWriter();
        try {
            transformer.transform(new DOMSource(getRootElement()),
                    new StreamResult(buffer));
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
        String xmlCode = buffer.toString();

        try {
            FileWriter myWriter = new FileWriter(filePath);
            myWriter.write(xmlCode);
            myWriter.close();
        } catch (IOException e) {
            System.err.println("Errore nella scrittura del file");
        }
    }

    public void removeElement(Element element) {
        element.getParentNode().removeChild(element);
    }

    private final Document xmlDocument;
    private final String filePath;
}
