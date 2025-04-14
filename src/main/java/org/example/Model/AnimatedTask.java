package org.example.Model;

public class AnimatedTask {
    private final Task task;
    private int x, y;
    private int queueX = 0, queueY = 0;
    private boolean inQueue = false;
    private final int speed = 30;

    public AnimatedTask(Task task, int index) {
        this.task = task;
        this.x = 1100;
        this.queueX = this.x;
        this.y = 10 + index * 10;
        this.queueY = this.y;
    }

    public void moveTowardQueue() {
        int dx = queueX - x;
        int dy = queueY - y;

        if (Math.abs(dx) > speed)
            x += speed * Integer.signum(dx);
        else
            x = queueX;

        if (Math.abs(dy) > speed)
            y += speed * Integer.signum(dy);
        else
            y = queueY;
    }


    public void setQueuePos(int serverIndex, int positionInQueue) {
        this.queueX = 150 + positionInQueue * 50;
        this.queueY = 100 + serverIndex * 120;
    }

    public void setInQueue(boolean inQueue) {
        this.inQueue = inQueue;
    }

    public boolean isInQueue() {
        return inQueue;
    }

    public Task getTask() {
        return task;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
