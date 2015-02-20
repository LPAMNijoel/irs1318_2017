package org.usfirst.frc.team1318.robot.Intake;

import org.usfirst.frc.team1318.robot.TuningConstants;
import org.usfirst.frc.team1318.robot.Common.IController;
import org.usfirst.frc.team1318.robot.Common.IDriver;

/* buttons 
 * up
 * down
 * toggle up and down
 * press and hold for in
 * press and hold for out
 * */

public class IntakeController implements IController
{
    private final IntakeComponent intake;
    private IDriver driver;
    private double motorSpeed;

    // state variables
    // true = outward(motor)/extending(piston), false = inward/retracting
    private Boolean solenoidState;
    //    private Boolean solenoidRightState;
    private boolean prevHoldButton;

    public IntakeController(IDriver operator, IntakeComponent intake)
    {
        this.intake = intake;
        this.driver = operator;
        this.motorSpeed = 0.0;
        this.solenoidState = null;
        //        this.solenoidRightState = null;
        this.prevHoldButton = false;
    }

    @Override
    public void setDriver(IDriver driver)
    {
        this.driver = driver;
    }

    @Override
    public void update()
    {
        // gets joystick input and translates it into wanted motor state
        if (this.driver.getIntakeForwardButton())
        {
            this.motorSpeed = TuningConstants.INTAKE_MOTOR_SPEED;
        }
        else if (this.driver.getIntakeBackwardButton())
        {
            this.motorSpeed = -TuningConstants.INTAKE_MOTOR_SPEED;
        }
        else
        {
            this.motorSpeed = 0;
        }

        if (this.driver.getIntakeDownHoldButton())
        {
            this.solenoidState = false;
            //            this.solenoidRightState = false;
            this.prevHoldButton = true;
        }
        else if (prevHoldButton)
        {
            this.solenoidState = true;
            //            this.solenoidRightState = true;
            this.prevHoldButton = false;
        }

        // gets joystick input and translates it into wanted solenoid state
        // Up means retract, down means extend
        if (this.driver.getIntakeUpButton())
        {
            this.solenoidState = false;
            //            this.solenoidRightState = false;
        }

        if (this.driver.getIntakeDownButton())
        {
            this.solenoidState = true;
            //            this.solenoidRightState = true;
        }

        //        if (this.operator.getIntakeLeftExtendOverride())
        //        {
        //            this.solenoidState = true;
        //        }
        //
        //        if (this.operator.getIntakeLeftRetractOverride())
        //        {
        //            this.solenoidState = false;
        //        }
        //
        //        if (this.operator.getIntakeRightExtendOverride())
        //        {
        //            this.solenoidRightState = true;
        //        }
        //
        //        if (this.operator.getIntakeRightRetractOverride())
        //        {
        //            this.solenoidRightState = false;
        //        }

        // sets IntakeComponent using solenoid and motor states
        if (this.solenoidState != null)
        {
            this.intake.setLeftIntake(this.solenoidState);
        }
        //        if (this.solenoidRightState != null)
        //        {
        //            this.intake.setRightIntake(this.solenoidRightState);
        //        }
        this.intake.setIntakeMotorSpeed(this.motorSpeed);
    }

    public void stop()
    {
        this.intake.setIntakeMotorSpeed(0);
    }
}
