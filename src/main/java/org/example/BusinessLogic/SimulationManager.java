package org.example.BusinessLogic;

import org.example.GUI.SimulationFrame;
import org.example.Model.Server;
import org.example.Model.SimulationClock;
import org.example.Model.Task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static java.lang.Math.max;

public class SimulationManager implements Runnable{
    //data read from UI
    public int timeLimit; //maximum processing time - read from UI
    public int maxServiceTime;
    public int minServiceTime;
    public int numberOfServers;
    public int numberOfClients;
    public int minArrivalTime;
    public int maxArrivalTime;
    public SelectionPolicy selectionPolicy = SelectionPolicy.SHORTEST_QUEUE;

    //entitiy responsible with queue management and client distribution
    private Scheduler scheduler;
    //frame for displaying simulation
    private SimulationFrame frame;
    //pool of tasks (client shopping in the store)
    private List<Task> generatedTasks; // sau tasks li se mai zice

    //Stats
    private int peakHour = -1;
    private double averageWaitingTime = 0.0;
    public double averageServiceTime = 0.0;
    private int maxConcomitentClients = 0;


    public SimulationManager(int numberOfClients, int numberOfServers, int maxSimulationTime, int minArrivalTime,
                         int  maxArrivalTime, int  minServiceTime, int maxServiceTime, SelectionPolicy strategy, SimulationFrame frame  )
    {
        this.numberOfClients = numberOfClients;
        this.numberOfServers = numberOfServers;
        this.maxServiceTime = maxServiceTime;
        this.minServiceTime = max(minServiceTime, 1); //asuring at least one sec of service
        this.maxArrivalTime = maxArrivalTime;
        this.minArrivalTime = max(minArrivalTime, 1);
        this.timeLimit = maxSimulationTime;
        this.frame = frame;
        this.selectionPolicy = strategy;

        this.scheduler = new Scheduler(numberOfServers, 1);
        this.generatedTasks = new ArrayList<>();
        generateRandomTasks();


    }

    //Generating the tasks according to the data input
    public void generateRandomTasks() {
       Random random = new Random();
       generatedTasks.clear();

       for (int i = 1; i <= numberOfClients; i++){
           int arrivalTime= random.nextInt(maxArrivalTime - minArrivalTime + 1) + minArrivalTime;
           int serviceTime = random.nextInt(maxServiceTime - minServiceTime + 1) + minServiceTime;
           Task task = new Task(i, arrivalTime, serviceTime);
           generatedTasks.add(task);
       }

       // Sort by arrival time for easier retrivial when adding to queues
        generatedTasks.sort(Comparator.comparingInt(Task::getArrivalTime));
    }

    public List<Task>  getTasks() {
        return generatedTasks;
    }

    @Override
    public void run() {
        LogWriter.setFrame(frame);
        LogWriter.start(); // Start logging
        SimulationClock.reset();

        while (SimulationClock.getCurrentTime() < timeLimit) {
            int currentTime = SimulationClock.getCurrentTime();

            //Thread uri
            //Working with threads
            List<Task> toDispatch = new ArrayList<>();
            for (Task t : generatedTasks) {
                if (t.getArrivalTime() == currentTime) {
                    toDispatch.add(t);
                }
            }
            for (Task t : toDispatch) {
                scheduler.dispatchTask(t);
                generatedTasks.remove(t);
            }

            //frame.updateUIState(currentTime, scheduler.getServers()); // optional method to update GUI

            //Logging
            synchronized (LogWriter.class){

                LogWriter.log("Time: " + currentTime);

                //Waiting clients
                String waiting = "Waiting clients: ";
                for (Task task : generatedTasks) {
                    if (task.getArrivalTime() < currentTime)
                        continue;
                    waiting += "(" + task.getId() + ", " + task.getArrivalTime() + ", " + task.getServiceTime() + ")";
                }
                LogWriter.log(waiting.trim());

                //Queues
                int queueIndex = 1;
                for (Server server: scheduler.getServers()) {
                    String queueState = "Queue" + queueIndex + " :";
                    Task[] tasks = server.getTasks();
                    if (server.getCurrentTask() == null && tasks.length == 0) {
                        queueState += ("closed");
                    }
                    else {
                        if (server.getCurrentTask() != null) {
                            Task currentTask = server.getCurrentTask();
                            queueState += " (" + currentTask.getId() + ", " + currentTask.getArrivalTime() + ", " + currentTask.getRemainingServiceTime() +")";
                        }
                        for (Task task : tasks) {
                            queueState += " (" + task.getId() + ", " + task.getArrivalTime() + ", " + task.getRemainingServiceTime() + ")";
                        }

                    }
                    LogWriter.log(queueState.trim());
                    //frame.log(queueState.trim());
                    queueIndex++;
                }
                LogWriter.log("\n"); // new line
                frame.log("\n");
            }
            //Peak hour calculations
            peakHourCalculation(currentTime);

            SimulationClock.tick();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        avarageWaitingAndServiceTimeCalculation();

        //Optional
        scheduler.shutdown();
        LogWriter.close();
    }

    public void avarageWaitingAndServiceTimeCalculation() {
        //Average Wait
        int totalWait = 0;
        int totalServiceTime = 0;
        int totalFinishedTasks = 0;
        int totalTasks = 0;

        for (Server server : scheduler.getServers()) {
            for(Task task : server.getTasks()) {
                if (task.getFinishTime() != 0) // Task-ul e finalizat
                {
                    totalWait += task.getFinishTime() - task.getArrivalTime();
                    totalServiceTime += task.getServiceTime();
                    totalTasks++;
                    totalFinishedTasks++;
                }
                else
                {
                    totalWait += timeLimit - task.getArrivalTime();
                    totalTasks++;
                }
            }
        }
        averageWaitingTime = (double) totalWait / totalTasks;
        averageServiceTime = (double) totalServiceTime / totalFinishedTasks;
    }

    public void peakHourCalculation(int currentTime) {
        int currentConcomitentClients = 0;
        for (Server server : scheduler.getServers()) {
            currentConcomitentClients += server.getQueueSize();
        }

        if (currentConcomitentClients > maxConcomitentClients){
            maxConcomitentClients = currentConcomitentClients;
            peakHour = currentTime;
        }
    }

    public Scheduler getScheduler() {
        return scheduler;
    }
}
