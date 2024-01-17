package me.riefolo.controller;

import me.riefolo.model.XML;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class XML_Controller {
    public XML_Controller(File xmlFile) throws ParserConfigurationException, IOException, SAXException {
        xml = new XML(xmlFile);
    }

    private XML xml;
}
