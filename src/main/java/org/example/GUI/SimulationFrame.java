package org.example.GUI;

import org.example.BusinessLogic.SelectionPolicy;
import org.example.BusinessLogic.SimulationManager;
import org.example.Model.Server;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class SimulationFrame extends JFrame {

    private JTextField numberOfClientsField;
    private JTextField numberOfServersField;
    private JTextField maxSimulationTimeField;
    private JTextField minArrivalTimeField, maxArrivalTimeField;
    private JTextField minServiceTimeField, maxServiceTimeField;
    private JComboBox<String> strategybox;
    private JButton startButton;

    private JTextArea logArea;

    private JLabel averageWaitLabel;
    private JLabel peakHourLabel;
    private JLabel averageServiceLabel;

    //Animatie

    public SimulationFrame() {
        this.setTitle("Queue Simulation");
        this.setSize(1000, 800);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        // Top inputs
        JPanel inputPanel = new JPanel(new GridLayout(5, 4, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Simulation Settings"));

        numberOfClientsField = new JTextField("100");
        numberOfServersField = new JTextField("4");
        maxSimulationTimeField = new JTextField("100");
        minArrivalTimeField = new JTextField("1");
        maxArrivalTimeField = new JTextField("80");
        minServiceTimeField = new JTextField("2");
        maxServiceTimeField = new JTextField("10");

        strategybox = new JComboBox<>(new String[]{"SHORTEST_QUEUE", "SHORTEST_TIME"});
        startButton = new JButton("Start Simulation");

        inputPanel.add(new JLabel("Number of Clients"));
        inputPanel.add(numberOfClientsField);
        inputPanel.add(new JLabel("Number of Servers"));
        inputPanel.add(numberOfServersField);
        inputPanel.add(new JLabel("Max Simulation Time"));
        inputPanel.add(maxSimulationTimeField);
        inputPanel.add(new JLabel("Strategy"));
        inputPanel.add(strategybox);
        inputPanel.add(new JLabel("Min Arrival Time"));
        inputPanel.add(minArrivalTimeField);
        inputPanel.add(new JLabel("Max Arrival Time"));
        inputPanel.add(maxArrivalTimeField);
        inputPanel.add(new JLabel("Min Service Time"));
        inputPanel.add(minServiceTimeField);
        inputPanel.add(new JLabel("Max Service Time"));
        inputPanel.add(maxServiceTimeField);
        inputPanel.add(startButton);

        startButton.addActionListener(e -> startSimulation());
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
        averageServiceLabel = new JLabel("Average Service Time: N/A");
        statsPanel.add(averageWaitLabel);
        statsPanel.add(peakHourLabel);
        statsPanel.add(averageServiceLabel);
        statsPanel.setBorder(BorderFactory.createTitledBorder("Statistics"));
        this.add(statsPanel, BorderLayout.SOUTH);


        this.setVisible(true);
    }

    public void startSimulation() {
        int numberOfClients = Integer.parseInt(numberOfClientsField.getText());
        int numberOfServers = Integer.parseInt(numberOfServersField.getText());
        int maxSimulationTime = Integer.parseInt(maxSimulationTimeField.getText());
        int minArrivalTime = Integer.parseInt(minArrivalTimeField.getText());
        int maxArrivalTime = Integer.parseInt(maxArrivalTimeField.getText());
        int minServiceTime = Integer.parseInt(minServiceTimeField.getText());
        int maxServiceTime = Integer.parseInt(maxServiceTimeField.getText());

        SelectionPolicy strategy = SelectionPolicy.valueOf(strategybox.getSelectedItem().toString());

        //Animation frame
        AnimationFrame animationFrame = new AnimationFrame(numberOfServers);

        SimulationManager sim =  new SimulationManager(numberOfClients, numberOfServers, maxSimulationTime,
                minArrivalTime, maxArrivalTime, minServiceTime, maxServiceTime, strategy, this, animationFrame.getAnimationPanel());

        Thread simulationThread = new Thread(sim);
        simulationThread.start();
    }

    // Logging methods
    public void log(String text) {
        logArea.append(text + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public void setAverageWaitingTime(double value) {
        averageWaitLabel.setText("Average Waiting Time: " + String.format("%.2f", value));
    }

    public void setPeakHour(int time) {
        peakHourLabel.setText("Peak Hour: " + time);
    }

    public void setAverageServiceTime(double time) {
        averageServiceLabel.setText("Average Service Time: " + String.format("%.2f", time));
    }

    public void showStats(double averageWaitingTime, int peakHour, double averageServiceTime){
        setAverageWaitingTime(averageWaitingTime);
        setPeakHour(peakHour);
        setAverageServiceTime(averageServiceTime);
        //log("Average Waiting Time: " + String.format("%.2f", averageWaitingTime));
        //log("Peak Hour: " + peakHour);
        //log("Average Service Time: " + String.format("%.2f", averageServiceTime));
    }
}
