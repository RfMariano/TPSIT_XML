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
            } catch (ParserConfigurationException | IOException | SAXException e) {
                infoLabel.setText("Il file selezionato potrebbe non contenere del codice XML valido");
                infoLabel.setVisible(true);
            }
        }
    }

    private final JFrame frame;
    private JLabel infoLabel;
    private XML_Controller controller;
}