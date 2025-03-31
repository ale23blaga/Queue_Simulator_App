package org.example.BusinessLogic;

import org.example.GUI.SimulationFrame;
import org.example.Model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimulationManager {
    private Scheduler scheduler;
    private SimulationFrame frame;
    private List<Task> tasks;
    //private SelectionPolicy selectionPolicy;

    public SimulationManager() {
        scheduler = new Scheduler();
        tasks = new ArrayList<>();
        frame = new SimulationFrame();
        //selectionPolicy = new SelectionPolicy();
    }

    public void generateRandomTasks(int numberOfTasks) {
        Random random = new Random();
        tasks.clear();
        for (int i = 1;  i <= numberOfTasks; i++) {
            int arrivalTime = random.nextInt(100) + 1;
            int serviceTime = random.nextInt(10) + 1;
            Task task = new Task(i, arrivalTime, serviceTime);
            tasks.add(task);
        }
    }

    public List<Task>  getTasks() {
        return tasks;
    }

}
