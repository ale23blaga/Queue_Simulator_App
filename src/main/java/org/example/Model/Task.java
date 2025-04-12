package org.example.Model;

public class Task {
    private int id;
    private int arrivalTime;
    private int serviceTime;
    private volatile int  remainingServiceTime;
    private int startServiceTime;
    private int finishTime;

    public Task(int id, int arrivalTime, int serviceTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
        this.remainingServiceTime = serviceTime;

    }

    public int getId() {
        return id;
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
        }
    }
}
