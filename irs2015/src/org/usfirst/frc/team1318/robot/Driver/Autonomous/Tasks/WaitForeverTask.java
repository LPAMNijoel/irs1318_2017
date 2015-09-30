package org.usfirst.frc.team1318.robot.Driver.Autonomous.Tasks;

import org.usfirst.frc.team1318.robot.Driver.ControlTaskBase;
import org.usfirst.frc.team1318.robot.Driver.IControlTask;

/**
 * Autonomous task that waits forever.  This task can be used as a way to give up if a certain condition isn't met (by pairing it with an AnyTask)
 * 
 */
public class WaitForeverTask extends ControlTaskBase implements IControlTask
{
    /**
     * Initializes a new WaitForeverTask
     */
    public WaitForeverTask()
    {
    }

    /**
     * Begin the current task
     */
    @Override
    public void begin()
    {
    }

    /**
     * Run an iteration of the current task and apply any control changes
     */
    @Override
    public void update()
    {
    }

    /**
     * Cancel the current task and clear control changes
     */
    @Override
    public void stop()
    {
    }

    /**
     * End the current task and reset control changes appropriately
     */
    @Override
    public void end()
    {
    }

    /**
     * Checks whether this task has completed, or whether it should continue being processed
     * @return true if we should continue onto the next task, otherwise false (to keep processing this task)
     */
    @Override
    public boolean hasCompleted()
    {
        return false;
    }
}
