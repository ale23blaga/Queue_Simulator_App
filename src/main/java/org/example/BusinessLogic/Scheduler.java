package org.example.BusinessLogic;

import org.example.Model.Server;
import org.example.Model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Scheduler {
    private final List<Server> servers;
    private  Strategy strategy;
    private final ExecutorService executor;

    public Scheduler(int numberOfServers) {
        servers = new ArrayList<>();
        strategy = new ShortestQueueStrategy();
        executor = Executors.newFixedThreadPool(numberOfServers);


        for (int i = 1; i <= numberOfServers; i++) {
            Server server = new Server(i);
            servers.add(server);
            executor.execute(server);
        }
    }

    public void dispatchTask(Task task) {
        strategy.addTask(servers, task);
    }

    public List<Server> getServers() {
        return servers;
    }

    public void shutdown() {
        for (Server server : servers) {
            server.stop();
        }
        executor.shutdown();
    }

    public void changeStrategy(SelectionPolicy policy){
        if (policy == SelectionPolicy.SHORTEST_QUEUE)
            strategy = new ShortestQueueStrategy();
        else if (policy == SelectionPolicy.SHORTEST_TIME)
            strategy = new TimeStrategy();
    }
}
