package org.usfirst.frc.team1318.robot.driver.controltasks;

import org.usfirst.frc.team1318.robot.driver.IControlTask;
import org.usfirst.frc.team1318.robot.driver.Operation;

public class ShooterFeedTask extends TimedTask implements IControlTask
{

    protected ShooterFeedTask(double duration)
    {
        super(duration);
    }

    @Override
    public void begin()
    {
        this.setDigitalOperationState(Operation.ShooterFeed, true);
    }

    @Override
    public void update()
    {
        this.setDigitalOperationState(Operation.ShooterFeed, true);
    }

    @Override
    public void stop()
    {
        super.stop();

        this.setDigitalOperationState(Operation.ShooterFeed, false);
    }

    @Override
    public void end()
    {
        super.end();

        this.setDigitalOperationState(Operation.ShooterFeed, false);
    }
}
