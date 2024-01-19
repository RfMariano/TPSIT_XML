package me.riefolo.controller;

import me.riefolo.model.Pair;
import me.riefolo.model.XML;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class XML_Controller {
    public XML_Controller(File xmlFile) throws ParserConfigurationException, IOException, SAXException {
        xml = new XML(xmlFile);
        elements = getAll();
    }

    public String[] getStringElements() {
        String[] stringElements = new String[elements.size()];
        for (int i = 0; i < elements.size(); i++) {
            stringElements[i] = elements.get(i).getFirst();
        }
        return stringElements;
    }

    public ArrayList<Pair<String, Element>> getDescendants(Element element) {
        ArrayList<Pair<String, Element>> descendants = new ArrayList<>();
        getDescendants(element, 0, descendants);
        return descendants;
    }

    private void getDescendants(Element element, int depth, ArrayList<Pair<String, Element>> descendants) {
        NodeList childNodes = element.getChildNodes();
        descendants.add(new Pair<>("  ".repeat(depth) + element.getNodeName() + (childNodes.getLength() == 1 ? ": " + element.getTextContent().trim() : ""), element));

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);

            if (childNode instanceof Element) {
                getDescendants((Element) childNode, depth + 1, descendants);
            }
        }
    }

    public ArrayList<Pair<String, Element>> getAll() {
        return getDescendants(xml.getRootElement());
    }

    public void addElement(int parentElementIndex, String elementTagName) {
        Element newElement = xml.makeNewElement(elementTagName);
        elements.get(parentElementIndex).getSecond().appendChild(newElement);
        // TODO add the new element in the right position inside the elements arraylist
        // elements.add(parentElementIndex, new Pair<>(elementTagName, newElement));
        xml.saveToFile();
        elements = getAll();
    }

    private final XML xml;
    private ArrayList<Pair<String, Element>> elements;
}
