package org.example.Model;

public class SimulationClock {
    private static volatile int currentTime = 0;

    public static synchronized void tick() {
        currentTime++;
    }

    public static synchronized int getCurrentTime() {
        return currentTime;
    }

    public static synchronized void reset(){
        currentTime = 0;
    }
}
