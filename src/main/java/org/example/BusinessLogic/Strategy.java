package org.example.BusinessLogic;

import org.example.Model.Server;
import org.example.Model.Task;

import java.util.List;

public interface Strategy {
    default void addTask(List<Server> servers, Task task){}
}
