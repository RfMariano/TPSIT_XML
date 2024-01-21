package me.riefolo.view;

import com.formdev.flatlaf.FlatDarkLaf;
import me.riefolo.controller.XML_Controller;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class XML_Editor extends JFrame {
    public XML_Editor() {
        FlatDarkLaf.setup();
        frame = new JFrame();

        initComponents();
        frame.setVisible(true);
    }

    private void initComponents() {
        frame.setSize(500,500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setMenu();

        infoLabel = new JLabel("Apri un file per poterlo editare");
        infoLabel.setHorizontalAlignment(JLabel.CENTER);
        infoLabel.setVerticalAlignment(JLabel.CENTER);
        frame.add(infoLabel, BorderLayout.CENTER);

        buttonsPanel = getButtonsPanel();
        frame.add(buttonsPanel, BorderLayout.EAST);

        model = new DefaultListModel<>();
        xmlList = new JList<>(model);
        scrollPane = new JScrollPane(xmlList);
        scrollPane.setVisible(false);
        frame.add(scrollPane, BorderLayout.WEST);
    }

    private JPanel getButtonsPanel() {
        JPanel buttonsPanel = new JPanel();

        buttonsPanel.setVisible(false);
        // buttonsPanel.setBorder(new BevelBorder(BevelBorder.LOWERED)); // DEBUG ONLY
        buttonsPanel.setPreferredSize(new Dimension(frame.getWidth()/2, frame.getHeight()));

        buttonsPanel.setAlignmentX(JPanel.CENTER_ALIGNMENT);
        buttonsPanel.setAlignmentY(JPanel.CENTER_ALIGNMENT);

        JButton addElementButton = getAddElementButton();
        buttonsPanel.add(addElementButton);

        JButton removeElementButton = getRemoveElementButton();
        buttonsPanel.add(removeElementButton);

        JButton modifyTextButton = getModifyTextButton();
        buttonsPanel.add(modifyTextButton);

        JButton modifyAttributesButton = getModifyAttributesButton();
        buttonsPanel.add(modifyAttributesButton);

        return buttonsPanel;
    }

    private JButton getRemoveElementButton() {
        JButton removeElementButton = new JButton("Rimuovi elemento");
        removeElementButton.addActionListener(e -> {
            if (xmlList == null)
                JOptionPane.showMessageDialog(frame, "Devi prima aprire un file xml");
            else if (xmlList.getSelectedIndex() == -1)
                JOptionPane.showMessageDialog(frame, "Devi prima selezionare un elemento");
            else
                removeElement();
        });
        return removeElementButton;
    }

    private JButton getAddElementButton() {
        JButton addElementButton = new JButton("Aggiungi elemento");
        addElementButton.addActionListener(e -> {
            if (xmlList == null)
                JOptionPane.showMessageDialog(frame, "Devi prima aprire un file xml");
            else if (xmlList.getSelectedIndex() == -1)
                JOptionPane.showMessageDialog(frame, "Devi prima selezionare un elemento");
            else
                popupInput = JOptionPane.showInputDialog(frame,
                        "Quale dev'essere il nome del tag?", null);
            addElement();
        });
        return addElementButton;
    }

    private JButton getModifyTextButton() {
        JButton modifyTextButton = new JButton("Modifica testo");
        modifyTextButton.addActionListener(e -> {
            if (xmlList == null)
                JOptionPane.showMessageDialog(frame, "Devi prima aprire un file xml");
            else if (xmlList.getSelectedIndex() == -1)
                JOptionPane.showMessageDialog(frame, "Devi prima selezionare un elemento");
            else if (controller.hasChildren(xmlList.getSelectedIndex()))
                JOptionPane.showMessageDialog(frame, "I tag con figli non possono contenere testo");
            else {
                String elementText = model.getElementAt(xmlList.getSelectedIndex());
                popupInput = JOptionPane.showInputDialog(frame,
                        "Inserisci il testo da inserire all'interno del tag", elementText.contains(":") ? elementText.substring(elementText.indexOf(':')+2) : "");
            }
            modifyText();
        });
        return modifyTextButton;
    }

    private JButton getModifyAttributesButton() {
        JButton modifyAttributesButton = new JButton("Modifica attributi");
        modifyAttributesButton.addActionListener(e -> {
            if (xmlList == null)
                JOptionPane.showMessageDialog(frame, "Devi prima aprire un file xml");
            else if (xmlList.getSelectedIndex() == -1)
                JOptionPane.showMessageDialog(frame, "Devi prima selezionare un elemento");
            else {
                String elementText = model.getElementAt(xmlList.getSelectedIndex());
                popupInput = JOptionPane.showInputDialog(frame,
                        "Inserisci il valore degli attributi", elementText.contains("[") ? elementText.substring(elementText.indexOf('[')+1, elementText.indexOf(']')) : "");
            }
            modifyAttributes();
        });
        return modifyAttributesButton;
    }

    private void setMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem openMenuItem = new JMenuItem("Apri");
        openMenuItem.addActionListener(e -> openFile());
        fileMenu.add(openMenuItem);

        menuBar.add(fileMenu);
        frame.setJMenuBar(menuBar);
    }

    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File xmlFile = fileChooser.getSelectedFile();
            try {
                controller = new XML_Controller(xmlFile);
                infoLabel.setVisible(false);
                buttonsPanel.setVisible(true);

                model.clear();
                for (String element : controller.getStringElements()) {
                    model.addElement(element);
                }
                scrollPane.setVisible(true);
                xmlList.repaint();
            } catch (ParserConfigurationException | IOException | SAXException e) {
                infoLabel.setText("Il file selezionato potrebbe non contenere del codice XML valido");
                infoLabel.setVisible(true);
                buttonsPanel.setVisible(false);
            }
        }
    }

    private void addElement() {
        if (popupInput == null) return;
        if (popupInput.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Nessun contenuto inserito");
        }
        controller.addElement(xmlList.getSelectedIndex(), popupInput);
        model.add(xmlList.getSelectedIndex()+1, controller.getStringElements()[xmlList.getSelectedIndex()+1]);
        popupInput = null;
    }

    private void removeElement() {
        int removed = controller.removeElement(xmlList.getSelectedIndex());
        model.removeRange(xmlList.getSelectedIndex(), xmlList.getSelectedIndex()+removed-1);
    }

    private void modifyText() {
        if (popupInput == null) return;
        controller.setText(xmlList.getSelectedIndex(), popupInput);
        String element = model.getElementAt(xmlList.getSelectedIndex());
        if (popupInput.isEmpty() && element.contains(":"))
            model.setElementAt(element.substring(0, element.indexOf(':')), xmlList.getSelectedIndex());
        else if (!popupInput.isEmpty())
            model.setElementAt((element.contains(":") ? element.substring(0, element.indexOf(':')) : element) + ": " + popupInput, xmlList.getSelectedIndex());
        popupInput = null;
    }

    private void modifyAttributes() {
        if (popupInput == null) return;
        if (!controller.setAttributes(xmlList.getSelectedIndex(), popupInput)) {
            JOptionPane.showMessageDialog(frame, "Il contenuto inserito non è valido");
            return;
        }
        String element = model.getElementAt(xmlList.getSelectedIndex());
        if (popupInput.isEmpty() && element.contains("["))
            model.setElementAt(element.substring(0, element.indexOf('[')) + element.substring(element.indexOf(']')+1), xmlList.getSelectedIndex());
        else if (!popupInput.isEmpty()) {
            String firstPart;
            if (element.contains("[")) firstPart = element.substring(0, element.indexOf('['));
            else if (element.contains(":")) firstPart = element.substring(0, element.indexOf(':'));
            else firstPart = element;
            model.setElementAt(firstPart + "[" + popupInput + "]" + element.substring(element.indexOf(":")), xmlList.getSelectedIndex());
        }
        popupInput = null;
    }

    private final JFrame frame;
    private JLabel infoLabel;
    private JList<String> xmlList;
    private JScrollPane scrollPane;
    private XML_Controller controller;
    private String popupInput;
    private DefaultListModel<String> model;
    private JPanel buttonsPanel;
}
