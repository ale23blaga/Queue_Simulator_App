package org.example.BusinessLogic;

import org.example.Model.Server;
import org.example.Model.Task;

import java.util.ArrayList;
import java.util.List;

public class Scheduler {
    private List<Server> servers;
    private int maxNoServers;
    private int maxTaskPerServer;
    private Strategy strategy;

    public Scheduler(int maxNoServers, int maxTaskPerServer) {
        //for maxNoServers
        // -- create server object
        // -- create thread with the object
        strategy = new ShortestQueueStrategy();
        servers = new ArrayList<Server>();

        for (int i = 1; i < maxNoServers; i++) { //for maxNoServers
            Server server = new Server();
            servers.add(server);
            Thread serversThread = new Thread(server); //create new thread with the object
        }
    }

    public void changeStrategy(SelectionPolicy policy) {
        //apply strategy patter to instantiate the strategy
        //strategy corresponding to policy
        if (policy == SelectionPolicy.SHORTEST_QUEUE) {
            strategy = new  ShortestQueueStrategy();
        }
        if (policy == SelectionPolicy.SHORTEST_TIME){
            strategy = new  TimeStrategy();
        }
    }

    public void dispatchTask(Task task){
        //call the strategy addTask method
        //this method sends task to the queue


    }

    public List<Server> getServers(){
        return servers;
    }
}
