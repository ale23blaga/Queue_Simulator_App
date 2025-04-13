package org.example.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {
    private final BlockingQueue<Task> tasks = new LinkedBlockingQueue<>();
    private final AtomicInteger waitingPeriod = new AtomicInteger(0);
    private final int id;
    private Task currentTask = null;
    private boolean running = true;
    private int currentTick = 0;
    private List<Task> allTasks = new ArrayList<>(); //Finished, current, and waiting tasks

    public Server(int id) {
        this.id = id;
    }

    public void addTask(Task task) {
        tasks.add(task);
        allTasks.add(task); // for average wait and service time calculations
        task.setServerId(id);
        waitingPeriod.addAndGet(task.getServiceTime());
    }

    public int getQueueSize() {
        int size =  tasks.size();
        if (currentTask != null) {
            size += 1;
        }
        return size;
    }
    public List<Task> getAllTasks(){
        return allTasks;
    }

    public Task[] getTasks() {
        return tasks.toArray(new Task[0]);
    }

    public Task getCurrentTask() {
        return currentTask;
    }

    public int getId(){
        return id;
    }

    public int getWaitingPeriod(){
        return waitingPeriod.get();
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            SimulationClock.waitForTick(currentTick);

            try {
                synchronized (this) {
                    if (currentTask == null && !tasks.isEmpty()) {
                        currentTask = tasks.take();
                        currentTask.setStartServiceTime(SimulationClock.getCurrentTime());
                    }

                    if (currentTask != null) {
                        currentTask.decreaseRemainingServiceTime();
                        System.out.println("Task " + currentTask.getId() + " decremented to " + currentTask.getRemainingServiceTime());
                        waitingPeriod.decrementAndGet(); // -1 pe tick

                        if (currentTask.getRemainingServiceTime() <= 0) {
                            currentTask.setFinishTime(SimulationClock.getCurrentTime());

                            currentTask = null;
                        }
                    }
                }

                currentTick++;

            } catch (Exception e) {
                System.err.println("Server " + id + " error: " + e.getMessage());
                break;
            }
        }
    }
}
