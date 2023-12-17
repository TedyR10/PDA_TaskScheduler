**Name: Theodor-Ioan Rolea**

**Group: 333CA**

# HW 2 APD - Task Scheduler

### Overview:

* This project implements a task scheduler for a datacenter using Java Threads. This project's 
  main classes consist of `MyHost` and `MyDispatcher`. The system involves hosts that execute tasks
  and a dispatcher responsible for assigning tasks to hosts based on various scheduling algorithms.
  I will give a brief description of how the program logic works, but more in-depth comments are
  found throughout the code.
***

## Classes

### 1. MyHost

`MyHost` is a class representing a computing host in the system.
It features the following key functionalities:

- **Task Execution:** Executes tasks based on priority and preemption conditions.
- **Task Queue Management:** Uses a priority blocking queue to manage tasks based on their priority.
- **Synchronization:** Utilizes locks for thread safety during task assignment and execution.

Program logic:

- The host is initially awaiting a task. Once a task is added, it starts running that task in a
simulated manner inside a while loop, simulating time spent on the CPU. If there are no preemptions,
the task will simply finish its work and will be marked as finished and the next task in queue will
be executed.
- If a higher priority task than the one currently running is added and the currently 
  running task is preemptible, stop the currently running task, update its work left and 
  substitute it with the one that has a higher priority. These operations are done inside a
  synchronized lock to ensure thread safety.
- The host shuts down when a shutdown signal is received, halting all operations.

### 2. MyDispatcher

`MyDispatcher` is responsible for assigning tasks to hosts based
on different scheduling algorithms:

- **Round-Robin Scheduling:** Tasks are assigned to hosts in a circular order.
- **Shortest Queue Scheduling:** Tasks are assigned to the host with the shortest task queue.
- **Size Interval Task Assignment:** Tasks are assigned to hosts based on their type
(short, medium, long).
- **Least Work Left Scheduling:** Tasks are assigned to the host with the least amount of work left.

***

## Final thoughts:

* This project was challenging from the synchronization standpoint, because I had a couple of tasks
that kept failing because of the preemption aspect. I really enjoyed grinding through those 
hardships and I am glad it all worked out in the end. The labs really helped in solving a lot of
the problems I encountered.