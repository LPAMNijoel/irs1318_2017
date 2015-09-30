package org.usfirst.frc.team1318.robot.Driver;

import java.util.Map;

import org.usfirst.frc.team1318.robot.Driver.States.AnalogOperationState;
import org.usfirst.frc.team1318.robot.Driver.States.DigitalOperationState;
import org.usfirst.frc.team1318.robot.Driver.States.OperationState;

public abstract class ControlTaskBase implements IControlTask
{
    private Map<Operation, OperationState> operationStateMap;

    /**
     * Initialize the task with the mapping of operations to states
     * @param operationStateMap indicating the mapping of an operation to its current state
     */
    @Override
    public void initialize(Map<Operation, OperationState> operationStateMap)
    {
        this.operationStateMap = operationStateMap;
    }

    /**
     * Begin the current task.
     */
    public abstract void begin();

    /**
     * Run an iteration of the current task.
     */
    public abstract void update();

    /**
     * Stops the current task gracefully (but unexpectedly).
     */
    public abstract void stop();

    /**
     * Ends the current task, called when it (or a master task) has completed.
     */
    public abstract void end();

    /**
     * Checks whether this task has completed, or whether it should continue being processed.
     * @return true if we should continue onto the next task, otherwise false (to keep processing this task)
     */
    public abstract boolean hasCompleted();

    /**
     * Sets the interrupt for the operation state for a given analog operation to the provided value 
     * @param operation to set the interrupt state for
     * @param value to set as the interrupt
     */
    protected void setAnalogOperationState(Operation operation, double value)
    {
        OperationState operationState = this.operationStateMap.get(operation);
        ((AnalogOperationState)operationState).setInterruptState(value);
    }

    /**
     * Sets the interrupt for the operation state for a given digital operation to the provided value 
     * @param operation to set the interrupt state for
     * @param value to set as the interrupt
     */
    protected void setDigitalOperationState(Operation operation, boolean value)
    {
        OperationState operationState = this.operationStateMap.get(operation);
        ((DigitalOperationState)operationState).setInterruptState(value);
    }
}
