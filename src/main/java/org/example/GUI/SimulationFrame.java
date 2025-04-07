package org.example.GUI;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class SimulationFrame extends JFrame {
    private JTextField minProcessingField;
    private JTextField maxProcessingField;
    private JTextField numberOfClientsField;
    private JTextField numberOfServersField;
    private JTextField timeLimitField;
    private JComboBox<String> policyComboBox;
    private JButton startButton;

    private JTextArea logArea;

    private JLabel averageWaitLabel;
    private JLabel peakHourLabel;

    private PrintWriter logWriter;

    public SimulationFrame() {
        this.setTitle("Queue Simulation");
        this.setSize(700, 600);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        // Top inputs
        JPanel inputPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Simulation Settings"));

        minProcessingField = new JTextField("2");
        maxProcessingField = new JTextField("5");
        numberOfClientsField = new JTextField("100");
        numberOfServersField = new JTextField("3");
        timeLimitField = new JTextField("100");

        policyComboBox = new JComboBox<>(new String[]{"SHORTEST_QUEUE", "SHORTEST_TIME"});
        startButton = new JButton("Start Simulation");

        inputPanel.add(new JLabel("Min Processing Time:"));
        inputPanel.add(minProcessingField);
        inputPanel.add(new JLabel("Max Processing Time:"));
        inputPanel.add(maxProcessingField);
        inputPanel.add(new JLabel("Number of Clients:"));
        inputPanel.add(numberOfClientsField);
        inputPanel.add(new JLabel("Number of Servers:"));
        inputPanel.add(numberOfServersField);
        inputPanel.add(new JLabel("Time Limit:"));
        inputPanel.add(timeLimitField);
        inputPanel.add(new JLabel("Selection Policy:"));
        inputPanel.add(policyComboBox);

        this.add(inputPanel, BorderLayout.NORTH);

        // Center log area
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Simulation Log"));
        this.add(scrollPane, BorderLayout.CENTER);

        // Bottom stats
        JPanel statsPanel = new JPanel(new GridLayout(1, 2));
        averageWaitLabel = new JLabel("Average Waiting Time: N/A");
        peakHourLabel = new JLabel("Peak Hour: N/A");
        statsPanel.add(averageWaitLabel);
        statsPanel.add(peakHourLabel);
        statsPanel.setBorder(BorderFactory.createTitledBorder("Statistics"));
        this.add(statsPanel, BorderLayout.SOUTH);

        this.setVisible(true);

        // Initialize log file
        try {
            logWriter = new PrintWriter(new FileWriter("simulation_log.txt", false));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Getters for input values
    public int getMinProcessingTime() {
        return Integer.parseInt(minProcessingField.getText());
    }

    public int getMaxProcessingTime() {
        return Integer.parseInt(maxProcessingField.getText());
    }

    public int getNumberOfClients() {
        return Integer.parseInt(numberOfClientsField.getText());
    }

    public int getNumberOfServers() {
        return Integer.parseInt(numberOfServersField.getText());
    }

    public int getTimeLimit() {
        return Integer.parseInt(timeLimitField.getText());
    }

    public String getSelectedPolicy() {
        return (String) policyComboBox.getSelectedItem();
    }

    public JButton getStartButton() {
        return startButton;
    }

    // Logging methods
    public void log(String text) {
        logArea.append(text + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
        if (logWriter != null) {
            logWriter.println(text);
            logWriter.flush();
        }
    }

    public void setAverageWaitingTime(double value) {
        averageWaitLabel.setText("Average Waiting Time: " + String.format("%.2f", value));
    }

    public void setPeakHour(int time) {
        peakHourLabel.setText("Peak Hour: " + time);
    }

    public void closeLogFile() {
        if (logWriter != null) {
            logWriter.close();
        }
    }
}
