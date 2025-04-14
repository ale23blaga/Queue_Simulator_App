package org.example.GUI;

import org.example.DataAccess.LogWriter;

public class Main {
    public static void main(String[] args) {
        SimulationFrame frame = new SimulationFrame();
        LogWriter.setFrame(frame);
    }
}