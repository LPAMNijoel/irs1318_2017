package org.usfirst.frc.team1318.robot.driver.controltasks;

import org.usfirst.frc.team1318.robot.TuningConstants;
import org.usfirst.frc.team1318.robot.common.PIDHandler;
import org.usfirst.frc.team1318.robot.common.wpilibmocks.ITimer;
import org.usfirst.frc.team1318.robot.driver.IControlTask;
import org.usfirst.frc.team1318.robot.driver.Operation;
import org.usfirst.frc.team1318.robot.vision.VisionManager;

/**
 * Task that turns the robot a certain amount clockwise or counterclockwise in-place based on vision center
 * 
 * @author William
 */
public class VisionCenteringTask extends ControlTaskBase implements IControlTask
{
    private PIDHandler turnPidHandler;

    protected VisionManager visionManager;
    private Double centeredTime;

    /**
    * Initializes a new VisionCenteringTask
    */
    public VisionCenteringTask()
    {
        this.turnPidHandler = null;
        this.centeredTime = null;
    }

    /**
     * Begin the current task
     */
    @Override
    public void begin()
    {
        this.visionManager = this.getInjector().getInstance(VisionManager.class);
        this.turnPidHandler = new PIDHandler(0.065, 0.0, 0.0, 0.0, 1.0, -0.3, 0.3, this.getInjector().getInstance(ITimer.class));
    }

    /**
     * Run an iteration of the current task and apply any control changes
     */
    @Override
    public void update()
    {
        this.setDigitalOperationState(Operation.DriveTrainUsePositionalMode, false);

        Double currentMeasuredAngle = this.visionManager.getMeasuredAngle();
        Double currentDesiredAngle = this.visionManager.getDesiredAngle();
        if (currentMeasuredAngle != null && currentDesiredAngle != null)
        {
            this.setAnalogOperationState(
                Operation.DriveTrainTurn,
                -this.turnPidHandler.calculatePosition(currentDesiredAngle, currentMeasuredAngle));
        }
    }

    /**
     * Cancel the current task and clear control changes
     */
    @Override
    public void stop()
    {
        this.setDigitalOperationState(Operation.DriveTrainUsePositionalMode, false);
        this.setAnalogOperationState(Operation.DriveTrainTurn, 0.0);
    }

    /**
     * End the current task and reset control changes appropriately
     */
    @Override
    public void end()
    {
        this.setDigitalOperationState(Operation.DriveTrainUsePositionalMode, false);
        this.setAnalogOperationState(Operation.DriveTrainTurn, 0.0);
    }

    /**
     * Checks whether this task has completed, or whether it should continue being processed
     * @return true if we should continue onto the next task, otherwise false (to keep processing this task)
     */
    @Override
    public boolean hasCompleted()
    {
        Double currentMeasuredAngle = this.visionManager.getMeasuredAngle();
        Double currentDesiredAngle = this.visionManager.getDesiredAngle();
        if (currentMeasuredAngle == null || currentDesiredAngle == null)
        {
            return false;
        }

        double centerAngleDifference = Math.abs(currentMeasuredAngle - currentDesiredAngle);

        if (centerAngleDifference > TuningConstants.MAX_VISION_CENTERING_RANGE_DEGREES)
        {
            return false;
        }

        ITimer timer = this.getInjector().getInstance(ITimer.class);
        if (this.centeredTime == null)
        {
            this.centeredTime = timer.get();
            return false;
        }
        else if (timer.get() - this.centeredTime < 1.0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    public boolean shouldCancel()
    {
        return this.visionManager.getCenter() == null;
    }
}
