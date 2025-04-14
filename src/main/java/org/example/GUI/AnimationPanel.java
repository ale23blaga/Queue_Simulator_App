package org.example.GUI;

import org.example.Model.AnimatedTask;
import org.example.Model.Task;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AnimationPanel extends JPanel {
    private final List<AnimatedTask> animatedTasks = new ArrayList<>();
    private final int serverCount;
    private final int serverSpacing = 120;

    public AnimationPanel(int serverCount) {
        this.serverCount = serverCount;
        setPreferredSize(new Dimension(1400, 800));
        setBackground(Color.gray);

        Timer timer = new Timer(30, e-> {
            for (AnimatedTask task : animatedTasks) {
                if (task.isInQueue())
                {
                    task.moveTowardQueue();
                }
                else {
                    task.moveTowardQueue();
                }
            }
            repaint();
        });
        timer.start();
    }

    public void addAnimatedTask(AnimatedTask animatedTask) {
        animatedTasks.add(animatedTask);
    }

    public void recalculateQueuePosition() {
        List<AnimatedTask>[] serverQueues = new ArrayList[serverCount];
        for (int i = 0; i < serverCount; i++) {
            serverQueues[i] = new ArrayList<>();
        }

        for (AnimatedTask task : animatedTasks) {
            if (task.isInQueue()) {
                int serverId = task.getTask().getServerId();
                if (serverId > 0 && serverId <= serverCount) {
                    serverQueues[serverId - 1].add(task);
                }
            }
        }

        for (int server = 0; server < serverCount; server++) {
            List<AnimatedTask> queue = serverQueues[server];

            for (int i = 0; i < queue.size(); i++) {
                AnimatedTask task = queue.get(i);

                int xQueue = 150 + i * 35;
                int yQueue = 100 + server * serverSpacing;
                task.setQueuePos(xQueue, yQueue);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        //Draw servers
        for (int i = 0; i < serverCount; i++) {
            int y = 210 + i * serverSpacing;
            g.setColor(Color.black);
            g.drawRect(100, y, 40, 40);
            g.drawString("S" + (i + 1), 105, y + 25);
        }

        //Draw tasks
        for (AnimatedTask animatedTask : animatedTasks) {
            Task task = animatedTask.getTask();
            if (task.getFinishTime() > -1) {
                continue;
                //g.setColor(Color.gray); // finished
            } else if (task.getStartServiceTime() > -1) {
                g.setColor(Color.green); // in service
            } else if (animatedTask.isInQueue()) {
                g.setColor(Color.blue); // waiting in queue
            } else {

                g.setColor(Color.magenta); // waiting to be assigned
            }
            //repaint (1000, 1, 200, 800);
            g.fillOval(animatedTask.getX(), animatedTask.getY(), 30, 30);
            g.setColor(Color.WHITE);
            g.drawString(String.valueOf(task.getId()), animatedTask.getX(), animatedTask.getY());
        }
    }


}
