package me.riefolo.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class XML {
    public XML(File xmlFile) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        xmlDocument = builder.parse(xmlFile);
        xmlDocument.getDocumentElement().normalize();
    }

    public NodeList getElementsByTagName(String tagName) {
        return xmlDocument.getElementsByTagName(tagName);
    }

    public Node getElementById(String id) {
        return xmlDocument.getElementById(id);
    }

    public void addElement(Element parentElement, Element element) {
        parentElement.appendChild(element);
    }

    private Document xmlDocument;
}
