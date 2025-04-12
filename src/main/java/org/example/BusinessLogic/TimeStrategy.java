package org.example.BusinessLogic;

import org.example.Model.Server;
import org.example.Model.Task;

import java.util.List;

public class TimeStrategy implements Strategy {

    @Override
    public void addTask(List<Server> servers, Task task) {
        Server minTimeServer = servers.get(0);
        for (Server server : servers) {
            if (server.getWaitingPeriod() < minTimeServer.getWaitingPeriod()) {
                minTimeServer = server;
            }
        }
        minTimeServer.addTask(task);
    }
}
