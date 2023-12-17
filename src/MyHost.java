// Copyright Theodor-Ioan Rolea 333CA 2023
import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

import static java.lang.Math.round;

public class MyHost extends Host {
    // Task currently running on the host
    private Task runningTask = null;

    // Flags for managing host state
    private boolean contextSwitch = false;
    private boolean finished = false;
    private boolean shutdown = false;

    // Timestamp when the currently running task started
    private long runningTaskStartTime = 0;

    // Total work left to be done on the host
    private long workLeft = 0;

    // Priority queue for managing tasks based on priority
    PriorityBlockingQueue<Task> prioQueue = new PriorityBlockingQueue<>(
            10, new Comparator<Task>() {
        public int compare(Task a, Task b) {
            // Comparator for sorting tasks in descending order of priority
            return b.getPriority() - a.getPriority();
        }
    });

    // Lock for synchronization
    private final Object lock = new Object();

    @Override
    public void run() {
        while (!shutdown) {
            synchronized (lock) {

                // If no task is currently running, wait for a task to be added
                if (runningTask == null) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }

                // Check if the current task has finished or a context switch is needed
                if ((finished || contextSwitch) && runningTask != null) {

                    // Perform actions based on task completion or context switch
                    if (finished) {
                        runningTask.finish();
                        finished = false;
                    }

                    if (contextSwitch) {
                        runningTask.setLeft(runningTask.getLeft() -
                                (System.currentTimeMillis() - runningTaskStartTime));
                        prioQueue.put(runningTask);
                    }

                    // Check if there are more tasks in the queue
                    if (prioQueue.peek() != null) {
                        runningTask = prioQueue.poll();
                        runningTaskStartTime = System.currentTimeMillis();
                    } else {
                        runningTask = null;
                    }
                    contextSwitch = false;
                }
            }

            // Execute the currently running task
            if (runningTask != null) {
                long startTime = System.currentTimeMillis();
                long timeLeft = runningTask.getLeft();

                // Simulate task execution time
                while (System.currentTimeMillis() - startTime < timeLeft) {
                    // Check for a higher priority task that can preempt the current task
                    if (!prioQueue.isEmpty() &&
                            prioQueue.peek().getPriority() > runningTask.getPriority() &&
                            runningTask.isPreemptible()) {
                        contextSwitch = true;
                        break;
                    }
                }

                // Mark task as finished if the while loop is executed
                finished = true;
            }
        }
    }

    @Override
    public void addTask(Task task) {
        synchronized (lock) {

            // If no task is currently running and the queue is empty, start the task
            if (runningTask == null && prioQueue.isEmpty()) {
                runningTask = task;
                runningTaskStartTime = System.currentTimeMillis();
            } else {
                // Otherwise, add the task to the priority queue
                prioQueue.put(task);
            }

            // Notify the host thread to resume execution
            lock.notify();
        }
    }

    @Override
    public int getQueueSize() {
        // Get the size of the task queue
        int size = prioQueue.size();

        // If a task is currently running, increment the size
        if (runningTask != null)
            size += 1;

        return size;
    }

    @Override
    public long getWorkLeft() {
        // Calculate the total work left to be done on the host
        workLeft = 0;

        // Sum up the work left for tasks in the queue
        for (Task task : prioQueue) {
            workLeft += task.getLeft();
        }

        // If a task is currently running, add its remaining work
        if (runningTask != null) {
            workLeft += runningTask.getLeft() - (System.currentTimeMillis() - runningTaskStartTime);
        }

        // Convert work left to seconds and round the result
        return round((float) workLeft / 1000);
    }

    @Override
    public void shutdown() {
        // Shutdown the host
        synchronized (lock) {
            shutdown = true;
            lock.notify();
        }
    }
}