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
import java.util.Optional;

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
        StringBuilder attributes = new StringBuilder("[");
        Optional.ofNullable(element.getAttributes().item(0)).ifPresent(attributes::append);
        for (int i=1; i<element.getAttributes().getLength(); i++) {
            attributes.append(";").append(element.getAttributes().item(i));
        }
        descendants.add(new Pair<>("  ".repeat(depth) + element.getNodeName() + (attributes.toString().equals("[") ? "" : attributes+"]") + (childNodes.getLength() == 1 ? ": " + element.getTextContent().trim() : ""), element));

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

    private int getElementDepth(int index) {
        String element = elements.get(index).getFirst();
        return element.length() - element.replaceFirst("^\\s+", "").length();
    }

    public void addElement(int parentElementIndex, String elementTagName) {
        Element newElement = xml.makeNewElement(elementTagName);
        elements.get(parentElementIndex).getSecond().appendChild(newElement);
        elements.add(parentElementIndex+1, new Pair<>(" ".repeat(getElementDepth(parentElementIndex) + 2) + elementTagName, newElement));
        xml.saveToFile();
    }

    public int removeElement(int elementIndex) {
        xml.removeElement(elements.get(elementIndex).getSecond());
        int elementDepth = getElementDepth(elementIndex);
        int removed = 0;
        do {
            elements.remove(elementIndex);
            removed++;
        } while (getElementDepth(elementIndex) > elementDepth);
        xml.saveToFile();
        return removed;
    }

    public void setText(int elementIndex, String text) {
        elements.get(elementIndex).getSecond().setTextContent(text);
        xml.saveToFile();
    }

    public boolean hasChildren(int elementIndex) {
        return elements.get(elementIndex).getSecond().getChildNodes().getLength() > 1;
    }

    public boolean setAttributes(int elementIndex, String attributes) {
        try {
            Element element = elements.get(elementIndex).getSecond();
            while (element.getAttributes().getLength() > 0) {
                elements.get(elementIndex).getSecond().removeAttribute(element.getAttributes().item(0).toString().split("=")[0]);
            }
            if (!attributes.isEmpty()) {
                for (String attribute : attributes.split(";")) {
                    element.setAttribute(removeDoubleQuotes(attribute.split("=")[0]), removeDoubleQuotes(attribute.split("=")[1]));
                }
            }
            xml.saveToFile();
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
        return true;
    }

    private String removeDoubleQuotes(String text) {
        if (text.startsWith("\"") && text.endsWith("\""))
            return text.substring(1, text.length() - 1);
        return text;
    }

    private final XML xml;

    private final ArrayList<Pair<String, Element>> elements;
}
