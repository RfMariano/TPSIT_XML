package me.riefolo.view;

import com.formdev.flatlaf.FlatDarkLaf;
import me.riefolo.controller.XML_Controller;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.border.BevelBorder;
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

        JPanel buttonsPanel = getButtonsPanel();
        frame.add(buttonsPanel, BorderLayout.EAST);

        model = new DefaultListModel<>();
        xmlList = new JList<>(model);
        scrollPane = new JScrollPane(xmlList);
        scrollPane.setVisible(false);
        frame.add(scrollPane, BorderLayout.WEST);
    }

    private JPanel getButtonsPanel() {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        buttonsPanel.setAlignmentX(JPanel.CENTER_ALIGNMENT);
        buttonsPanel.setAlignmentY(JPanel.CENTER_ALIGNMENT);
        JButton addElement = new JButton("Aggiungi elemento");
        addElement.addActionListener(e -> {
            if (xmlList == null) {
                JOptionPane.showMessageDialog(frame, "Devi prima aprire un file xml");
            } else if (xmlList.getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(frame, "Devi prima selezionare un elemento");
            } else
            popupInput = JOptionPane.showInputDialog(frame,
                    "Quale dev'essere il nome del tag?", null);
            addElement();
        });
        buttonsPanel.add(addElement);
        return buttonsPanel;
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

                model.clear();
                for (String element : controller.getStringElements()) {
                    model.addElement(element);
                }
                scrollPane.setVisible(true);
                xmlList.repaint();
            } catch (ParserConfigurationException | IOException | SAXException e) {
                infoLabel.setText("Il file selezionato potrebbe non contenere del codice XML valido");
                infoLabel.setVisible(true);
            }
        }
    }

    private void addElement() {
        if (popupInput == null || popupInput.isEmpty()) return;
        controller.addElement(xmlList.getSelectedIndex(), popupInput);
        model.add(xmlList.getSelectedIndex()+1, popupInput);
        popupInput = null;
    }

    private final JFrame frame;
    private JLabel infoLabel;
    private JList<String> xmlList;
    private JScrollPane scrollPane;
    private XML_Controller controller;
    private String popupInput;
    private DefaultListModel<String> model;
}
