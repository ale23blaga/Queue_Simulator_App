package org.example.Model;

public class AnimatedTask {
    private final Task task;
    private int x, y;
    private int queueX = 0, queueY = 0;
    private boolean active = false;
    private boolean inQueue = false;
    private final int SPEED = 30;

    public AnimatedTask(Task task, int index) {
        this.task = task;
        this.x = 1100;
        this.y = 10 + index * 20;
    }

    public void moveTowardQueue() {
        int dx = queueX - x;
        int dy = queueY - y;

        if (Math.abs(dx) > SPEED)
            x += SPEED * Integer.signum(dx);
        else
            x = queueX;

        if (Math.abs(dy) > SPEED)
            y += SPEED * Integer.signum(dy);
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
