package org.example.BusinessLogic;

import org.example.Model.Server;
import org.example.Model.Task;

import java.util.List;

public class ShortestQueueStrategy  implements Strategy{


    @Override
    public void addTask(List<Server> servers, Task task) {
        Server minQueue = servers.get(0);
        for (Server server : servers) {
            if (server.getQueueSize() < minQueue.getQueueSize()) {
                minQueue = server;
            }
        }
        minQueue.addTask(task);
    }
}
