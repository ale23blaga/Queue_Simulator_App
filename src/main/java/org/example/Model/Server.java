package org.example.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;



public class Server implements Runnable{
    private BlockingQueue<Task> tasks;
    private AtomicInteger waitingPeriod; // Cat de mult dureaza coada in acel moment
    private List<Task> handledTasks = new ArrayList<>();
    private Task currentTask = null; // The task that is currently processed
    private volatile boolean running = true; // ca sa le sincronizam cu simulation manager

    public Server() {
        //intializare queue and waiting Period
        this.tasks = new LinkedBlockingQueue<>();
        this.waitingPeriod = new AtomicInteger(0);
    }

    public void addTask(int id, int arrivalTime, int serviceTime){
        Task task = new Task(id, arrivalTime, serviceTime);
        tasks.add(task);
        waitingPeriod.addAndGet(task.getServiceTime());
    }

    public void addTask(Task task){
        tasks.add(task);
        //The total waiting period is greater
        waitingPeriod.addAndGet(task.getServiceTime());
    }

    public void stop(){
        running = false;
        Thread.currentThread().interrupt(); // Forced stop if sleeping
    }

    public void start(){
        running = true;
    }

    public boolean isRunning() {
        return running;
    }

    public void run() {
        while(true){
            try {
                //Take task from queue
                //Daca folosesc pool returneaza null cand e gol, asa asteapta pana apare ceva
                Task task = tasks.take();
                handledTasks.add(task);
                currentTask = task;
                int currentTime = SimulationClock.getCurrentTime();

                task.setStartServiceTime(currentTime);
                task.setFinishTime(currentTime + task.getServiceTime());

                for( int i = 0; i < task.getServiceTime(); i++ ){
                    Thread.sleep(1000);
                    task.decreaseRemainingServiceTime();
                }
                currentTask = null;
                //The task is finished, the total waiting time for the queue should be reduced
                waitingPeriod.addAndGet(-task.getServiceTime());
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;

            }
        }
    }


    public Task[] getTasks(){
        return tasks.toArray(new Task[tasks.size()]);
    }

    public int getQueueSize() {
        return tasks.size();
    }

    public int getWaitingPeriod() {
        return waitingPeriod.get();
    }

    public Task getCurrentTask() {
        return currentTask;
    }
}
