package org.example.Model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private BlockingQueue<Task> tasks;
    private AtomicInteger waitingPeriod;

    public void addTask(int id, int arrivalTime, int serviceTime){
        Task task = new Task(id, arrivalTime, serviceTime);
        tasks.add(task);
    }
}
