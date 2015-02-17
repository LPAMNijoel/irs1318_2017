package org.usfirst.frc.team1318.robot.Autonomous.Tasks;

import org.usfirst.frc.team1318.robot.Autonomous.AutonomousControlData;
import org.usfirst.frc.team1318.robot.Autonomous.IAutonomousTask;
import org.usfirst.frc.team1318.robot.Elevator.ElevatorComponent;

import edu.wpi.first.wpilibj.Timer;

/**
 * CollectToteTask:
 * 
 * This task:
 * 1. Runs the intake until it notices that the through beam sensor has been broken, then
 * 2. Stops the intake and tells the elevator to go to the bottom (move to bottom) until the bottom limit switch is hit, then 
 * 3. Tells the elevator to go up to the 3rd level
 * 
 * @author Will
 *
 */
public class CollectToteTask implements IAutonomousTask
{
    private static final double DELAY_TIME = 0.1;

    private ElevatorComponent elevatorComponent;
    private Timer timer;

    private Double startWait;
    private boolean hasDetectedThroughBeamBroken;
    private boolean hasHitBottomLimitSwitch;

    public CollectToteTask(ElevatorComponent elevatorComponent)
    {
        this.elevatorComponent = elevatorComponent;
        this.timer = new Timer();

        this.startWait = null;
        this.hasDetectedThroughBeamBroken = false;
        this.hasHitBottomLimitSwitch = false;
    }

    @Override
    public void begin()
    {
        this.hasDetectedThroughBeamBroken = false;
        this.hasHitBottomLimitSwitch = false;
        this.timer.start();
        this.startWait = null;
    }

    @Override
    public void update(AutonomousControlData data)
    {
        if (!this.hasDetectedThroughBeamBroken)
        {
            if (this.elevatorComponent.getThroughBeamBroken())
            {
                this.hasDetectedThroughBeamBroken = true;
            }
        }
        else if (!this.hasHitBottomLimitSwitch)
        {
            if (this.elevatorComponent.getBottomLimitSwitchValue())
            {
                this.hasHitBottomLimitSwitch = true;

                this.startWait = this.timer.get();
            }
        }

        data.setIntakeForwardState(!this.hasDetectedThroughBeamBroken);
        data.setElevatorMoveToBottomState(this.hasDetectedThroughBeamBroken && !this.hasHitBottomLimitSwitch);
        data.setElevatorMoveTo3Totes(this.hasHitBottomLimitSwitch);
    }

    @Override
    public void cancel(AutonomousControlData data)
    {
        data.setIntakeForwardState(false);
        data.setElevatorMoveToBottomState(false);
        data.setElevatorMoveTo3Totes(false);
    }

    @Override
    public void end(AutonomousControlData data)
    {
        data.setIntakeForwardState(false);
        data.setElevatorMoveToBottomState(false);
        data.setElevatorMoveTo3Totes(false);
    }

    @Override
    public boolean shouldContinueProcessingTask()
    {
        if (this.startWait == null)
        {
            return true;
        }

        return this.hasDetectedThroughBeamBroken
            && this.hasHitBottomLimitSwitch
            && this.timer.get() < this.startWait + CollectToteTask.DELAY_TIME;
    }
}
