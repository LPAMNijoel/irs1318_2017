package org.usfirst.frc.team1318.robot.Autonomous.Tasks;

import java.util.Arrays;
import java.util.List;

import org.usfirst.frc.team1318.robot.Autonomous.AutonomousControlData;
import org.usfirst.frc.team1318.robot.Autonomous.IAutonomousTask;

/**
 * Autonomous task that holds multiple other tasks and executes them in parallel until certain conditions
 * are met.
 * 
 * AnyTask - a task that continues processing all of the provided tasks until any of them is ready to continue
 * AllTask - a task that continues processing all of the provided tasks until all of them are ready to continue
 * 
 * @author Will
 *
 */
public class ConcurrentTask implements IAutonomousTask
{
    private final boolean anyTask;
    private final List<IAutonomousTask> tasks;

    /**
     * Initializes a new ConcurrentTask
     * @param anyTask indicates that we want to use AnyTask semantics as opposed to AllTask semantics
     * @param tasks to run
     */
    private ConcurrentTask(boolean anyTask, IAutonomousTask... tasks)
    {
        this.anyTask = anyTask;
        this.tasks = Arrays.asList(tasks);
    }

    /**
     * Create a task that continues processing all of the provided tasks until any of them is ready to continue
     * @param tasks to run
     * @return a task that runs all of the provided tasks until all of them are ready to continue
     */
    public static IAutonomousTask AnyTasks(IAutonomousTask... tasks)
    {
        return new ConcurrentTask(true, tasks);
    }

    /**
     * Create a task that continues processing all of the provided tasks until all of them are ready to continue
     * @param tasks to run
     * @return a task that runs all of the provided tasks until one of them is ready to continue
     */
    public static IAutonomousTask AllTasks(IAutonomousTask... tasks)
    {
        return new ConcurrentTask(false, tasks);
    }

    /**
     * Begin the current task
     */
    @Override
    public void begin()
    {
        for (IAutonomousTask task : this.tasks)
        {
            task.begin();
        }
    }

    /**
     * Run an iteration of the current task and apply any control changes 
     * @param data to which we should apply updated settings
     */
    @Override
    public void update(AutonomousControlData data)
    {
        for (IAutonomousTask task : this.tasks)
        {
            task.update(data);
        }
    }

    /**
     * Cancel the current task and clear control changes
     * @param data to which we should clear any updated control settings
     */
    @Override
    public void cancel(AutonomousControlData data)
    {
        for (IAutonomousTask task : this.tasks)
        {
            task.cancel(data);
        }
    }

    /**
     * End the current task and reset control changes appropriately
     * @param data to which we should apply updated settings
     */
    @Override
    public void end(AutonomousControlData data)
    {
        for (IAutonomousTask task : this.tasks)
        {
            task.end(data);
        }
    }

    /**
     * Checks whether this task has completed, or whether it should continue being processed
     * @return true if we should continue onto the next task, otherwise false (to keep processing this task)
     */
    @Override
    public boolean hasCompleted()
    {
        for (IAutonomousTask task : this.tasks)
        {
            boolean taskHasCompleted = task.hasCompleted();

            // for AnyTask tasks, return that we're completed (true) if any of them have completed (true).
            if (this.anyTask && taskHasCompleted)
            {
                return true;
            }

            // for AllTask tasks, return that we haven't completed (false) if any hasn't completed (false).
            if (!this.anyTask && !taskHasCompleted)
            {
                return false;
            }
        }

        // AnyTasks return false when none of them are true.  AllTasks return true when none of them are false.
        return !this.anyTask;
    }
}
