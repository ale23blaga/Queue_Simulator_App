package org.example.BusinessLogic;

import org.example.GUI.AnimationPanel;
import org.example.GUI.SimulationFrame;
import org.example.Model.AnimatedTask;
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

    //components
    private final SimulationFrame frame;
    private final Scheduler scheduler;
    private final List<Task> generatedTasks = new ArrayList<>();

    // Stats
    private int peakHour = -1;
    private double averageWaitingTime = 0.0;
    private double averageServiceTime = 0.0;
    private int maxConcurrentClients = 0;

    //Animation
    private AnimationPanel animationPanel;
    private Map<Task, AnimatedTask> animatedTasksMap = new HashMap<>();

    public SimulationManager(int numberOfClients, int numberOfServers, int maxSimulationTime, int minArrivalTime, int maxArrivalTime, int minServiceTime, int maxServiceTime, SelectionPolicy strategy, SimulationFrame frame, AnimationPanel animationPanel)
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
        this.animationPanel = animationPanel;

        this.scheduler = new Scheduler(numberOfServers);
        this.scheduler.changeStrategy(strategy);
        generateRandomTasks();
    }

    private void generateRandomTasks() {
        Random rand = new Random();
        generatedTasks.clear();
        animatedTasksMap.clear();

        int minA = Math.min(minArrivalTime, maxArrivalTime);
        int maxA = Math.max(minArrivalTime, maxArrivalTime);
        int minS = Math.min(minServiceTime, maxServiceTime);
        int maxS = Math.max(minServiceTime, maxServiceTime);

        for (int i = 1; i <= numberOfClients; i++) {
            int arrivalTime = rand.nextInt(maxA - minA + 1) + minA;
            int serviceTime = rand.nextInt(maxS - minS + 1) + minS;
            Task task = new Task(i, arrivalTime, serviceTime);
            generatedTasks.add(task);


            AnimatedTask animatedTask = new AnimatedTask(task, i);
            animatedTasksMap.put(task, animatedTask);
            animationPanel.addAnimatedTask(animatedTask);

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
            animationTaskUpdate();
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
        logStats();
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
            for (Task task : server.getAllTasks()) { // include boht queue and finished
                totalTasks++;

                if (task.getStartServiceTime() > 0) {
                    totalWaitTime += task.getStartServiceTime() - task.getArrivalTime() - 1;
                } else {
                    // if not started assume it's still waiting
                    totalWaitTime += timeLimit - task.getArrivalTime();
                }

                if (task.getFinishTime() > 0) { //average service time for finished tasks
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

    private void logStats(){
        LogWriter.log("FINAL STATS: ");
        LogWriter.log("AVerage Waiting Time: " + String.format("%.2f", averageWaitingTime));
        LogWriter.log("Average Service Time: " + String.format("%.2f", averageServiceTime));
        LogWriter.log("Peak Hour: " + peakHour);

    }

    private void logSimulationState(int currentTime) {
        LogWriter.log("Time: " + currentTime);

        // Waiting clients
        String waiting = "Waiting clients: ";
        for (Task task : generatedTasks) {
            if (task.getArrivalTime() >= currentTime) {
                waiting +=  " (" + task.getId() + ", " + task.getArrivalTime() + ", " + task.getServiceTime() + ")";
            }
        }
        LogWriter.log(waiting);

        // Queues
        int queueNumber = 1;
        for (Server server : scheduler.getServers()) {
            String queueState = "Queue" + queueNumber + " :";
            Task current = server.getCurrentTask();
            if (current == null && server.getTasks().length == 0) {
                queueState += "closed";
            } else {
                if (current != null) {
                    queueState += " (" + current.getId() + ", " + current.getArrivalTime() + ", " + current.getRemainingServiceTime() + ")";
                }
                for (Task t : server.getTasks()) {
                    if (current == null || t.getId() != current.getId()) {
                        queueState += " (" + t.getId() + ", " + t.getArrivalTime() + ", " + t.getRemainingServiceTime() + ")";
                    }
                }
            }
            LogWriter.log(queueState);
            queueNumber++;
        }

        LogWriter.log("");
        frame.log("");
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void animationTaskUpdate()
    {
        for (Server server : scheduler.getServers()) {
            int queuePosition = 0;

            //current task
            Task current = server.getCurrentTask();
            if (current != null && current.getFinishTime() == -1) {
                AnimatedTask at = animatedTasksMap.get(current);
                if (at != null) {
                    at.setQueuePos(server.getId(), queuePosition);
                    at.setInQueue(true);
                }
                queuePosition++;
            }

            //tasks in queue
            for (Task t : server.getTasks()) {
                if (t != current && t.getFinishTime() == -1) {
                    AnimatedTask at = animatedTasksMap.get(t);
                    if (at != null) {
                        at.setQueuePos(server.getId(), queuePosition);
                        at.setInQueue(true);
                    }
                    queuePosition++;
                }
            }

        }
    }
}
