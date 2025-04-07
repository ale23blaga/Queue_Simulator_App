package org.example.BusinessLogic;

import org.example.GUI.SimulationFrame;
import org.example.Model.Server;
import org.example.Model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimulationManager implements Runnable{
    //data read from UI
    public int timeLimit = 100; //maximum processing time - read from UI
    public int maxProcessingTime = 10;
    public int minProcessingTime = 2;
    public int numberOfServers = 3;
    public int numberOfClients = 100;
    public SelectionPolicy selectionPolicy = SelectionPolicy.SHORTEST_QUEUE;

    //entitiy responsible with queue management and client distribution
    private Scheduler scheduler;
    //frame for displaying simulation
    private SimulationFrame frame;
    //pool of tasks (client shopping in the store)
    private List<Task> generatedTasks; // sau tasks li se mai zice

    public SimulationManager() {
        // Intializare
        // -> crate and start numberOfServers
        scheduler = new Scheduler(numberOfServers, 1);
        generatedTasks = new ArrayList<>();
        //initialize selection strategy -> e in scheduler in constructor
        generateRandomTasks();
        frame = new SimulationFrame();
    }

    public void generateRandomTasks() {
        Random random = new Random();
        generatedTasks.clear();
        int numberOfTasks = random.nextInt(1000) + 1;
        for (int i = 1;  i <= numberOfTasks; i++) {
            int arrivalTime = random.nextInt(100) + 1;
            int serviceTime = random.nextInt(maxProcessingTime - minProcessingTime) + minProcessingTime;
            Task task = new Task(i, arrivalTime, serviceTime);
            generatedTasks.add(task);
        }
    }

    public List<Task>  getTasks() {
        return generatedTasks;
    }

    @Override
    public void run() {
        //iterate generated Tasks list and pick task that have the
        //arivialTime equal with the current Time
        // - send task to queueu by calling the dispatchTask Method
        //from Scheduler
        // - delete client from list
        //update UI frame
        int currentTime = 0;
        while (currentTime < timeLimit) {
            List<Task> toRemove = new ArrayList<>();
            for (Task t : generatedTasks) {
                if (t.getArrivalTime() == currentTime) {
                    scheduler.dispatchTask(t);
                    toRemove.add(t);
                }
            }
            generatedTasks.removeAll(toRemove);

            // update UI / logs here
            System.out.println("Time: " + currentTime);
            for (Server server : scheduler.getServers()) {
                System.out.println("Server: " + server.getTasks().length + " tasks");
            }

            currentTime++;
            try {
                Thread.sleep(1000); // simulate 1 second
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //wait an interval of 1 second
    }

    //aici ajunge main-ul la final, sa stergi clasa cu main
    public static void main(String[] args){
        SimulationManager gen= new SimulationManager();
        Thread t = new Thread(gen);
        //t.start();
    }
}
