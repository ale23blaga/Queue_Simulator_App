package org.example.BusinessLogic;

import org.example.GUI.SimulationFrame;
import org.example.Model.Server;
import org.example.Model.SimulationClock;
import org.example.Model.Task;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

import static java.lang.Math.max;

public class SimulationManager implements Runnable {
    //private final CyclicBarrier barrier;

    // UI input
    private final int timeLimit;
    private final int numberOfClients;
    private final int numberOfServers;
    private final int maxServiceTime;
    private final int minServiceTime;
    private final int maxArrivalTime;
    private final int minArrivalTime;
    private final SelectionPolicy selectionPolicy;

    // Core components
    private final SimulationFrame frame;
    private final Scheduler scheduler;
    private final List<Task> generatedTasks = new ArrayList<>();

    // Stats
    private int peakHour = -1;
    private double averageWaitingTime = 0.0;
    private double averageServiceTime = 0.0;
    private int maxConcurrentClients = 0;

    public SimulationManager(int numberOfClients, int numberOfServers, int maxSimulationTime, int minArrivalTime, int maxArrivalTime, int minServiceTime, int maxServiceTime, SelectionPolicy strategy, SimulationFrame frame)
    {
        this.numberOfClients = numberOfClients;
        this.numberOfServers = numberOfServers;
        this.timeLimit = maxSimulationTime;
        this.minArrivalTime = max(minArrivalTime, 1);
        this.maxArrivalTime = maxArrivalTime;
        this.minServiceTime = max(minServiceTime, 1);
        this.maxServiceTime = maxServiceTime;
        this.selectionPolicy = strategy;
        this.frame = frame;

        //this.barrier = new CyclicBarrier(numberOfServers + 1); // +1 pt main thread
        this.scheduler = new Scheduler(numberOfServers);
        this.scheduler.changeStrategy(strategy);
        generateRandomTasks();
    }

    private void generateRandomTasks() {
        Random rand = new Random();
        generatedTasks.clear();
        int minA = Math.min(minArrivalTime, maxArrivalTime);
        int maxA = Math.max(minArrivalTime, maxArrivalTime);
        int minS = Math.min(minServiceTime, maxServiceTime);
        int maxS = Math.max(minServiceTime, maxServiceTime);

        for (int i = 1; i <= numberOfClients; i++) {
            int arrivalTime = rand.nextInt(maxA - minA + 1) + minA;
            int serviceTime = rand.nextInt(maxS - minS + 1) + minS;
            generatedTasks.add(new Task(i, arrivalTime, serviceTime));
        }
        generatedTasks.sort(Comparator.comparingInt(Task::getArrivalTime));
    }

    @Override
    public void run() {
        LogWriter.setFrame(frame);
        LogWriter.start();
        SimulationClock.reset();

        generateRandomTasks();
        int currentTime = 0;

        while (currentTime < timeLimit) {
            System.out.println("Simulation tick: " + currentTime);

            List<Task> toDispatch = new ArrayList<>();
            for (Task task : generatedTasks) {
                if (task.getArrivalTime() == currentTime) {
                    toDispatch.add(task);
                }
            }

            for (Task task : toDispatch) {
                scheduler.dispatchTask(task);
                generatedTasks.remove(task);
            }

            logSimulationState(currentTime);
            updatePeakHour(currentTime);

            SimulationClock.advance();
            currentTime++;

            try {
                Thread.sleep(1000); // 1 sec
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        calculateStats();
        frame.showStats(averageWaitingTime, peakHour, averageServiceTime);
        scheduler.shutdown();
        LogWriter.close();
    }



    private void calculateStats() {
        int totalWaitTime = 0;
        int totalServiceTime = 0;
        int totalFinished = 0;
        int totalTasks = 0;

        for (Server server : scheduler.getServers()) {
            for (Task task : server.getAllTasks()) { // include both queue and finished
                totalTasks++;

                if (task.getStartServiceTime() > 0) {
                    totalWaitTime += task.getStartServiceTime() - task.getArrivalTime();
                } else {
                    // if not started, assume it's still waiting
                    totalWaitTime += timeLimit - task.getArrivalTime();
                }

                if (task.getFinishTime() > 0) {
                    totalServiceTime += task.getServiceTime();
                    totalFinished++;
                }
            }
        }

        if (totalTasks > 0) {
            averageWaitingTime = (double) totalWaitTime / totalTasks;
        }

        if (totalFinished > 0) {
            averageServiceTime = (double) totalServiceTime / totalFinished;
        }
    }


    private void updatePeakHour(int currentTime) {
        int concurrent = 0;
        for (Server server : scheduler.getServers()) {
            concurrent += server.getQueueSize();
        }
        if (concurrent > maxConcurrentClients) {
            maxConcurrentClients = concurrent;
            peakHour = currentTime;
        }
    }

    private void logSimulationState(int currentTime) {
        LogWriter.log("Time: " + currentTime);

        // Waiting clients
        StringBuilder waiting = new StringBuilder("Waiting clients: ");
        for (Task task : generatedTasks) {
            if (task.getArrivalTime() >= currentTime) {
                waiting.append("(")
                        .append(task.getId()).append(", ")
                        .append(task.getArrivalTime()).append(", ")
                        .append(task.getServiceTime()).append(")");
            }
        }
        LogWriter.log(waiting.toString());

        // Queues
        int queueNumber = 1;
        for (Server server : scheduler.getServers()) {
            StringBuilder queueState = new StringBuilder("Queue" + queueNumber + " :");
            Task current = server.getCurrentTask();
            if (current == null && server.getTasks().length == 0) {
                queueState.append("closed");
            } else {
                if (current != null) {
                    queueState.append(" (")
                            .append(current.getId()).append(", ")
                            .append(current.getArrivalTime()).append(", ")
                            .append(current.getRemainingServiceTime()).append(")");
                }
                for (Task t : server.getTasks()) {
                    if (current == null || t.getId() != current.getId()) {
                        queueState.append(" (")
                                .append(t.getId()).append(", ")
                                .append(t.getArrivalTime()).append(", ")
                                .append(t.getRemainingServiceTime()).append(")");
                    }
                }
            }
            LogWriter.log(queueState.toString());
            queueNumber++;
        }

        LogWriter.log("");
        frame.log("");
    }

    public Scheduler getScheduler() {
        return scheduler;
    }
}
