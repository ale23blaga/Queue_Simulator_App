package org.example.BusinessLogic;

import org.example.Model.Server;
import org.example.Model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Scheduler {
    private List<Server> servers;
    private int maxNoServers;
    private int maxTaskPerServer;
    private Strategy strategy;
    private ExecutorService executor;

    public Scheduler(int maxNoServers, int maxTaskPerServer) {
        this.maxNoServers = maxNoServers;
        this.maxTaskPerServer = maxTaskPerServer;
        strategy = new ShortestQueueStrategy();

        servers = new ArrayList<Server>();
        executor = Executors.newFixedThreadPool(maxNoServers);


        for (int i = 1; i <= maxNoServers; i++) { //for maxNoServers
            Server server = new Server();
            servers.add(server);
            //Thread pool
            executor.execute(server);
        }
    }

    public void dispatchTask(Task task){
        strategy.addTask(servers, task);

    }

    public List<Server> getServers(){
        return servers;
    }

    public void changeStrategy(SelectionPolicy policy) { //Select the other strategy
        if (policy == SelectionPolicy.SHORTEST_QUEUE) {
            strategy = new  ShortestQueueStrategy();
        }
        else
        if (policy == SelectionPolicy.SHORTEST_TIME){
            strategy = new  TimeStrategy();
        }
    }

    public void shutdown() {
        executor.shutdown();
    }
}
