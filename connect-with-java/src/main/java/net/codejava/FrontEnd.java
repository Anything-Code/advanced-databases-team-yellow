package net.codejava;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static com.teamdev.jxbrowser.engine.RenderingMode.HARDWARE_ACCELERATED;

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.view.swing.BrowserView;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.view.swing.BrowserView;

import javax.swing.*;
import java.awt.*;

public final class FrontEnd {
	
	TrueWayApi trueWayApi;
    JPanel textPanel, panelForTextFields, completionPanel, rmPanel;
    JLabel titleLabel, cityLabel, zipLabel, streetLabel, nrLabel, userLabel, passLabel, nearLabel;
    JTextField cityField, zipField, streetField, nrField, nearField;
    JButton searchButton, addNearby;
    Neo4jDBConnect neo4jClient;
    MongoDBConnect mongoDB;
	
	FrontEnd(Neo4jDBConnect client, MongoDBConnect mongoClient){
		neo4jClient = client;
		mongoDB = mongoClient;
		trueWayApi  = new TrueWayApi(mongoDB);
		
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("Emergency Application");

        frame.setContentPane(this.createContentPane());
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(750, 500);
        frame.setVisible(true);
        
		
	}
	
	public JPanel createContentPane() {
		// We create a bottom JPanel to place everything on.
        JPanel totalGUI = new JPanel();
        totalGUI.setLayout(null);

        titleLabel = new JLabel("Add Emergency");
        titleLabel.setLocation(0,0);
        titleLabel.setSize(290, 30);
        titleLabel.setHorizontalAlignment(0);
        totalGUI.add(titleLabel);

        // Creation of a Panel to contain the JLabels
        textPanel = new JPanel();
        textPanel.setLayout(null);
        textPanel.setLocation(10, 35);
        textPanel.setSize(70, 200);
        totalGUI.add(textPanel);

        // Addresse Labels
        cityLabel = makeText(0,"City",cityLabel);
        textPanel.add(cityLabel);

        zipLabel = makeText(40,"Zip",zipLabel);
        textPanel.add(zipLabel);

        streetLabel = makeText(80,"Street",streetLabel);
        textPanel.add(streetLabel);

        nrLabel = makeText(120,"Nr",nrLabel);
        textPanel.add(nrLabel);
        
        // TextFields Panel Container
        panelForTextFields = new JPanel();
        panelForTextFields.setLayout(null);
        panelForTextFields.setLocation(110, 40);
        panelForTextFields.setSize(100, 300);
        totalGUI.add(panelForTextFields);

        // Addresse inputs
        cityField = textBox(0, cityField);
        panelForTextFields.add(cityField);

        zipField = textBox(40, zipField);
        panelForTextFields.add(zipField);
        
        streetField = textBox(80, streetField);
        panelForTextFields.add(streetField);
        
        nrField = textBox(120, nrField);
        panelForTextFields.add(nrField);
        
        searchButton = new JButton("Search");
        searchButton.setBounds(0,165,95,30);
        searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					searchAction(e);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}});
        panelForTextFields.add(searchButton);
        
        rmPanel = new JPanel();
        rmPanel.setLayout(null);
        rmPanel.setLocation(230, 40);
        rmPanel.setSize(120, 300);
        totalGUI.add(rmPanel);
        
        nearLabel = makeText(0, "Nearby", nearLabel);
        rmPanel.add(nearLabel);
        
        nearField = textBox(40, nearField);
        rmPanel.add(nearField);
        
        String[] optionsToChoose = {"Code Adam", "Code Blue", "Code Brown", "Code Clear", "Code Gray", "Code Orange",
        		"Code Pink", "Code Red", "Code Silver", "Code White", "Code Violet", "Code Green", "Code Black",
        		"External triage", "Internal triage", "Rapid response team"};

        JComboBox<String> jComboBox = new JComboBox<>(optionsToChoose);
        jComboBox.setBounds(0, 95, 95, 30);
        rmPanel.add(jComboBox);
        totalGUI.add(rmPanel);

        totalGUI.setOpaque(true);    
        return totalGUI;
		
	}
	
	private void searchAction(ActionEvent e) throws Exception {
		EmergencyReport emergencyTest = new EmergencyReport(mongoDB, neo4jClient, "Code Adam", cityField.getText(), zipField.getText(), streetField.getText(), nrField.getText());
		String Location = "";
		
		if(emergencyTest.completedAdress()) {
			System.out.println("Went here");
			Location = neo4jClient.fetchGPSfromKnowAdresse(emergencyTest.myId);
			Location = Location.replace("\"", "");
		}
		else {
			Location = emergencyTest.getCityAndZip().replace("\"", "");
			System.out.println("Its near");
			System.out.println(Location + nearField.getText());
			trueWayApi.makeTrueWayRequest(Location, nearField.getText(), emergencyTest.myId);
		}
		System.out.println(Location);
		
		PassToMap passMap = new PassToMap(mongoDB);
		passMap.makeData();
		
		String url_open ="file:///C:/Users/Emil/eclipse-workspace/ConnectDBUsingJava/src/main/java/web/map.html";
		java.awt.Desktop.getDesktop().browse(java.net.URI.create(url_open));
	}
	
	private JLabel makeText(int y, String text, JLabel genLabel) {
        genLabel = new JLabel(text);
        genLabel.setLocation(0, y);
        genLabel.setSize(70, 40);
        genLabel.setHorizontalAlignment(4);
		
		return genLabel;
	}
	
	private JTextField textBox(int y, JTextField genTextField) {
		genTextField = new JTextField(8);
		genTextField.setLocation(0, y);
		genTextField.setSize(100, 30);
		
		return genTextField;
	}
	
}
