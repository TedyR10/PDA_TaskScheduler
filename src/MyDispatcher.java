// Copyright Theodor-Ioan Rolea 333CA 2023

import java.util.List;

public class MyDispatcher extends Dispatcher {
    // Number of hosts in the system
    public int n;

    // Current ID for round-robin scheduling
    public int id = -1;

    public MyDispatcher(SchedulingAlgorithm algorithm, List<Host> hosts) {
        super(algorithm, hosts);
        n = hosts.size();
    }

    @Override
    public void addTask(Task task) {
        if (algorithm == SchedulingAlgorithm.ROUND_ROBIN) {
            // Round-robin scheduling: assign task to hosts in a circular order
            hosts.get((++id) % n).addTask(task);
        } else if (algorithm == SchedulingAlgorithm.SHORTEST_QUEUE) {
            // Shortest queue scheduling: assign task to the host with the shortest queue
            float shortestQueue = Float.MAX_VALUE;
            int nodeToSend = -1;
            for (int i = 0; i < hosts.size(); i++) {
                if (hosts.get(i).getQueueSize() < shortestQueue) {
                    shortestQueue = hosts.get(i).getQueueSize();
                    nodeToSend = i;
                }
            }
            hosts.get(nodeToSend).addTask(task);
        } else if (algorithm == SchedulingAlgorithm.SIZE_INTERVAL_TASK_ASSIGNMENT) {
            // Size interval task assignment: assign tasks to hosts based on task type
            if (task.getType() == TaskType.SHORT) {
                hosts.get(0).addTask(task);
            } else if (task.getType() == TaskType.MEDIUM) {
                hosts.get(1).addTask(task);
            } else {
                hosts.get(2).addTask(task);
            }
        } else {
            // Default scheduling: assign task to the host with the least work left
            float leastWorkLeft = Float.MAX_VALUE;
            int nodeToSend = -1;
            for (int i = 0; i < hosts.size(); i++) {
                if (hosts.get(i).getWorkLeft() < leastWorkLeft) {
                    leastWorkLeft = hosts.get(i).getWorkLeft();
                    nodeToSend = i;
                }
            }
            hosts.get(nodeToSend).addTask(task);
        }
    }
}
