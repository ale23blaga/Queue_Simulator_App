package org.example.BusinessLogic;

import org.example.Model.Server;
import org.example.Model.Task;

import java.util.List;

public class ShortestQueueStrategy  implements Strategy{


    @Override
    public void addTask(List<Server> servers, Task task) {
        Server minQueueServer = servers.get(0);
        for (Server server : servers) {
            if (server.getQueueSize() < minQueueServer.getQueueSize()) {
                minQueueServer = server;
            }
        }
        System.out.println("Assigned Task " + task.getId() + " to Server " + minQueueServer.getId() + " [Shortest Queue]");
        minQueueServer.addTask(task);
    }
}
