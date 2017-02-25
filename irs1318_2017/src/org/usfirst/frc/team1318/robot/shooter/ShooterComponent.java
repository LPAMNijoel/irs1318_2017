package org.usfirst.frc.team1318.robot.shooter;

import org.usfirst.frc.team1318.robot.common.IDashboardLogger;
import org.usfirst.frc.team1318.robot.common.wpilibmocks.DoubleSolenoidValue;
import org.usfirst.frc.team1318.robot.common.wpilibmocks.IDoubleSolenoid;
import org.usfirst.frc.team1318.robot.common.wpilibmocks.IEncoder;
import org.usfirst.frc.team1318.robot.common.wpilibmocks.IMotor;
import org.usfirst.frc.team1318.robot.common.wpilibmocks.IRelay;
import org.usfirst.frc.team1318.robot.common.wpilibmocks.ISolenoid;
import org.usfirst.frc.team1318.robot.common.wpilibmocks.ITimer;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class ShooterComponent
{
    private static final String LogName = "shooter";

    private final IDashboardLogger logger;
    private final IDoubleSolenoid hood;
    private final IMotor feeder;
    private final ISolenoid readyLight;
    private final IRelay targetingLight;
    private final IMotor shooter;
    private final IEncoder encoder;

    private final ITimer timer;

    private double prevTime;
    private int prevTicks;
    private double prevVelocity;

    @Inject
    public ShooterComponent(
        IDashboardLogger logger,
        ITimer timer,
        @Named("SHOOTER_HOOD") IDoubleSolenoid hood,
        @Named("SHOOTER_FEEDER") IMotor feeder,
        //@Named("SHOOTER_READY_LIGHT") ISolenoid readyLight,
        //@Named("SHOOTER_TARGETING_LIGHT") IRelay targetingLight,
        @Named("SHOOTER_SHOOTER") IMotor shooter,
        @Named("SHOOTER_ENCODER") IEncoder encoder)
    {
        this.logger = logger;

        this.hood = hood;
        this.feeder = feeder;
        this.readyLight = null; //readyLight;
        this.targetingLight = null; //targetingLight;
        this.shooter = shooter;
        this.encoder = encoder;

        this.timer = timer;

        this.prevTime = this.timer.get();
        this.prevTicks = 0;
        this.prevVelocity = 0.0;
    }

    public void setShooterPower(double power)
    {
        //        if (TuningConstants.SHOOTER_USE_CAN_PID)
        //        {
        //            if (power == 0.0)
        //            {
        //                this.shooter.changeControlMode(CANTalonControlMode.Voltage);
        //            }
        //            else
        //            {
        //                this.shooter.changeControlMode(CANTalonControlMode.Speed);
        //            }
        //        }

        this.shooter.set(power);
        this.logger.logNumber(ShooterComponent.LogName, "power", power);
    }

    public void setFeederPower(double power)
    {
        this.logger.logNumber(ShooterComponent.LogName, "feederPower", power);
        this.feeder.set(-power); // motor installed backwards
    }

    public int getShooterTicks()
    {
        int ticks = this.encoder.get(); // this.shooter.getTicks();
        this.logger.logNumber(ShooterComponent.LogName, "ticks", ticks);
        return ticks;
    }

    public double getShooterSpeed()
    {
        int currTicks = this.encoder.get();
        double currTime = this.timer.get();
        double dt = currTime - this.prevTime;
        if (dt >= 0.01)
        {
            this.prevTime = currTime;

            // calculate change in ticks since our last measurement
            double deltaX = currTicks - this.prevTicks;
            double timeRatio = 0.02 / dt;

            this.prevTicks = currTicks;
            this.prevVelocity = deltaX * timeRatio;
        }

        double speed = this.prevVelocity; // = this.encoder.getRate(); // this.shooter.getSpeed();
        this.logger.logNumber(ShooterComponent.LogName, "speed", speed);
        return speed;
    }

    public void extendOrRetract(boolean extend)
    {
        this.logger.logString(ShooterComponent.LogName, "hood", extend ? "extend" : "retract");
        if (extend)
        {
            this.hood.set(DoubleSolenoidValue.kForward);
        }
        else
        {
            this.hood.set(DoubleSolenoidValue.kReverse);
        }
    }

    public void setReadyLight(boolean on)
    {
        //this.readyLight.set(on);
    }

    public void setTargetingLight(boolean on)
    {
        //this.targetingLight.set(on ? RelayValue.kForward : RelayValue.kOff);
    }

    public void stop()
    {
        this.hood.set(DoubleSolenoidValue.kOff);
        this.feeder.set(0.0);
        //this.readyLight.set(false);
        //this.targetingLight.set(RelayValue.kOff);
        this.shooter.set(0.0);

        this.encoder.reset();
        this.prevTicks = 0;
        this.prevTime = 0.0;
        this.prevVelocity = 0.0;
    }
}
