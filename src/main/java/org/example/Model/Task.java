package org.example.Model;

import java.util.Objects;

public class Task {
    private int id;
    private int arrivalTime;
    private int serviceTime;
    private volatile int  remainingServiceTime;
    private int startServiceTime = -1;
    private int finishTime = -1;
    private boolean dispatched;
    private int serverId = -1;

    public Task(int id, int arrivalTime, int serviceTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
        this.remainingServiceTime = serviceTime;
        this.dispatched = false;
    }

    public int getId() {
        return id;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public int getServerId(){
        return serverId;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getServiceTime() {
        return serviceTime;
    }

    public void setStartServiceTime(int startServiceTime) {
        this.startServiceTime = startServiceTime;
    }

    public void setFinishTime(int finishTime) {
        this.finishTime = finishTime;
    }

    public int getFinishTime(){
        return finishTime;
    }

    public int getRemainingServiceTime() {
        return remainingServiceTime;
    }

    public void decreaseRemainingServiceTime(){
        if (remainingServiceTime > 0){
            remainingServiceTime--;
            //System.out.println("Task " + id + " decremented to " + remainingServiceTime);
        }
    }

    public boolean isDispatched() {
        return dispatched;
    }

    public void setDispatched(boolean dispatched) {
        this.dispatched = dispatched;
    }

    public int getStartServiceTime() {
        return startServiceTime;
    }
}
