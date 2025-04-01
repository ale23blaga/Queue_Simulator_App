package org.example.Model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;



public class Server implements Runnable{
    private BlockingQueue<Task> tasks;
    private AtomicInteger waitingPeriod;

    public Server() {
        //intializare queue and waiting Period
        this.tasks = new LinkedBlockingQueue<>();
        this.waitingPeriod = new AtomicInteger(0);
    }

    public void addTask(int id, int arrivalTime, int serviceTime){
        Task task = new Task(id, arrivalTime, serviceTime);
        tasks.add(task);
    }

    public void run() {
        while(true){
            //take new task from queue
            Task task = tasks.poll();
            //stop the thread for a time equal with the task's processing time
            try {
                Thread.sleep(task.getServiceTime());
            } catch (InterruptedException e) {
                e.printStackTrace();
                return; //????

            } finally {
                //increment the waiting period
                waitingPeriod.addAndGet(task.getServiceTime());
            }
        }
    }

    public Task[] getTasks(){
        return tasks.toArray(new Task[tasks.size()]);
    }
}
