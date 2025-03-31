package org.example.BusinessLogic;

import org.example.Model.Server;
import org.example.Model.Task;

import java.util.List;

public class Scheduler {
    private List<Server> servers;
    private int maxNoServers;
    private int maxTaskPerServer;
    private Strategy strategy;

    public void changeStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public void dispatchTask(Task task){

    }
}
