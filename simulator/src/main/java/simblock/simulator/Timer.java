/*
 * Copyright 2019 Distributed Systems Group
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package simblock.simulator;

import simblock.task.Task;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;


/**
 * The type Timer schedules the execution of simulation tasks stored in a Future Event List (FEL)
 * . Each {@link Task}
 * can be scheduled for execution. Tasks that have been run get removed from the FEL.
 */
public class Timer {

  /**
   * A sorted queue of scheduled tasks.
   */
  private final PriorityQueue<ScheduledTask> taskQueue = new PriorityQueue<>();
  public static Timer SimulationTimer = null;

  public static void InitTimer() {
    SimulationTimer = new Timer();
  }

  public static Timer getSimulationTimer()
  {
    if(SimulationTimer==null)
    {
      throw new NullPointerException("simulation timer singleton is not initialized");
    }
    return SimulationTimer;
  }

  public PriorityQueue<ScheduledTask> getTasks(){
    return taskQueue;
  }

  /**
   * A map containing a mapping of all tasks to their ScheduledTask counterparts. When
   * executed, the key - value
   * pair is to be removed from the mapping.
   */
  //TODO a bit redundant since Task is again stored in ScheduledTask. Is there a better approach?
  private static final Map<Task, ScheduledTask> taskMap = new HashMap<>();
  /**
   * Simulation time in milliseconds. This is NOT wall clock.
   */
  //TODO is it milliseconds?
  private static long clock = 0L;

  /**
   * Represents a {@link Task} that is scheduled to be executed.
   */
  public static class ScheduledTask implements Comparable<ScheduledTask> {
    private final Task task;
    private final long scheduledTime;

    /**
     * Instantiates a new ScheduledTask.
     *
     * @param task          - the task to be executed
     * @param scheduledTime - the simulation time at which the task is to be executed
     */
    public ScheduledTask(Task task, long scheduledTime) {
      this.task = task;
      this.scheduledTime = scheduledTime;
    }

    /**
     * Gets the task.
     *
     * @return the {@link Task} instance
     */
    private Task getTask() {
      return this.task;
    }

    /**
     * Gets the scheduled time at which the task is to be executed.
     *
     * @return the scheduled time
     */
    private long getScheduledTime() {
      return this.scheduledTime;
    }

    /**
     * Compares the two scheduled tasks.
     *
     * @param o other task
     * @return 1 if self is executed later, 0 if concurrent and -1 if self is to be executed before.
     */
    public int compareTo(ScheduledTask o) {
      if (this.equals(o)) {
        return 0;
      }
      int order = Long.signum(this.scheduledTime - o.scheduledTime);
      if (order != 0) {
        return order;
      }
      order = System.identityHashCode(this) - System.identityHashCode(o);
      return order;
    }
  }

  private static void updateClock(long newTime){
    Timer.clock = newTime;
  }

  /**
   * Get current time in milliseconds.
   *
   * @return the time
   */
  public static long getClock() {
    return clock;
  }

  /**
   * Runs a {@link ScheduledTask}.
   */
  public void runFirstNextTask() {
    // If there are any tasks
    if (taskQueue.size() > 0) {
      // Get the next ScheduledTask
      ScheduledTask currentScheduledTask = taskQueue.poll();
      Task currentTask = currentScheduledTask.getTask();
      Timer.updateClock(currentScheduledTask.getScheduledTime());
      // Remove the task from the mapping of all tasks
      taskMap.remove(currentTask, currentScheduledTask);
      // Execute
      currentTask.run();
    }
  }

  /**
   * Remove task from the mapping of all tasks and from the execution queue.
   *
   * @param task the task to be removed
   */
  public void removeTask(Task task) {
    if (taskMap.containsKey(task)) {
      ScheduledTask scheduledTask = taskMap.get(task);
      taskQueue.remove(scheduledTask);
      taskMap.remove(task, scheduledTask);
    }
  }

  /**
   * Get the {@link Task} from the execution queue to be executed next.
   *
   * @return the task from the queue or null if task queue is empty.
   */
  public Task getTask() {
    if (taskQueue.size() > 0) {
      ScheduledTask currentTask = taskQueue.peek();
      return currentTask.getTask();
    } else {
      return null;
    }
  }

  /**
   * Schedule task to be executed at the current time incremented by the task duration.
   *
   * @param task the task
   */
  public void putTask(Task task){
    if(taskMap.containsKey(task)){
      System.err.println("Can't insert same task to the task queue multiple times");
      return;
    }
    ScheduledTask scheduledTask = new ScheduledTask(task, clock + task.getInterval());
    taskMap.put(task, scheduledTask);
    taskQueue.add(scheduledTask);
  }

  /**
   * Schedule task to be executed at the provided absolute timestamp.
   *
   * @param task the task
   * @param time the time in milliseconds
   */
  @SuppressWarnings("unused")
  public void putTaskAbsoluteTime(Task task, long time) {
    if(taskMap.containsKey(task)){
      System.err.println("Can't insert same task to the task queue multiple times");
      return;
    }
    ScheduledTask scheduledTask = new ScheduledTask(task, time);
    taskMap.put(task, scheduledTask);
    taskQueue.add(scheduledTask);
  }


}
