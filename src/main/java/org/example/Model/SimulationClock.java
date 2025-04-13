package org.example.Model;

public class SimulationClock {
    private static int currentTime = 0;

    public static synchronized void waitForTick(int expectedTick) {
        while (currentTime < expectedTick) {
            try {
                SimulationClock.class.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public static synchronized void advance() {
        currentTime++;
        System.out.println("Simulation tick: " + currentTime);
        SimulationClock.class.notifyAll();
    }

    public static synchronized int getCurrentTime() {
        return currentTime;
    }

    public static synchronized void reset() {
        currentTime = 0;
    }
}
