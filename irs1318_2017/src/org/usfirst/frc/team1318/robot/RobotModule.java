package org.usfirst.frc.team1318.robot;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import org.usfirst.frc.team1318.robot.climber.ClimberController;
import org.usfirst.frc.team1318.robot.common.IController;
import org.usfirst.frc.team1318.robot.common.IDashboardLogger;
import org.usfirst.frc.team1318.robot.common.SmartDashboardLogger;
import org.usfirst.frc.team1318.robot.common.wpilibmocks.AnalogInputWrapper;
import org.usfirst.frc.team1318.robot.common.wpilibmocks.CANTalonControlMode;
import org.usfirst.frc.team1318.robot.common.wpilibmocks.CANTalonWrapper;
import org.usfirst.frc.team1318.robot.common.wpilibmocks.CompressorWrapper;
import org.usfirst.frc.team1318.robot.common.wpilibmocks.DigitalInputWrapper;
import org.usfirst.frc.team1318.robot.common.wpilibmocks.DoubleSolenoidWrapper;
import org.usfirst.frc.team1318.robot.common.wpilibmocks.EncoderWrapper;
import org.usfirst.frc.team1318.robot.common.wpilibmocks.IAnalogInput;
import org.usfirst.frc.team1318.robot.common.wpilibmocks.ICANTalon;
import org.usfirst.frc.team1318.robot.common.wpilibmocks.ICompressor;
import org.usfirst.frc.team1318.robot.common.wpilibmocks.IDigitalInput;
import org.usfirst.frc.team1318.robot.common.wpilibmocks.IDoubleSolenoid;
import org.usfirst.frc.team1318.robot.common.wpilibmocks.IEncoder;
import org.usfirst.frc.team1318.robot.common.wpilibmocks.IJoystick;
import org.usfirst.frc.team1318.robot.common.wpilibmocks.IMotor;
import org.usfirst.frc.team1318.robot.common.wpilibmocks.IPowerDistributionPanel;
import org.usfirst.frc.team1318.robot.common.wpilibmocks.IRelay;
import org.usfirst.frc.team1318.robot.common.wpilibmocks.ISolenoid;
import org.usfirst.frc.team1318.robot.common.wpilibmocks.ITimer;
import org.usfirst.frc.team1318.robot.common.wpilibmocks.JoystickWrapper;
import org.usfirst.frc.team1318.robot.common.wpilibmocks.PowerDistributionPanelWrapper;
import org.usfirst.frc.team1318.robot.common.wpilibmocks.RelayDirection;
import org.usfirst.frc.team1318.robot.common.wpilibmocks.RelayWrapper;
import org.usfirst.frc.team1318.robot.common.wpilibmocks.SolenoidWrapper;
import org.usfirst.frc.team1318.robot.common.wpilibmocks.TalonWrapper;
import org.usfirst.frc.team1318.robot.common.wpilibmocks.TimerWrapper;
import org.usfirst.frc.team1318.robot.common.wpilibmocks.VictorWrapper;
import org.usfirst.frc.team1318.robot.compressor.CompressorController;
import org.usfirst.frc.team1318.robot.driver.ButtonMap;
import org.usfirst.frc.team1318.robot.driver.IButtonMap;
import org.usfirst.frc.team1318.robot.drivetrain.DriveTrainController;
import org.usfirst.frc.team1318.robot.general.PositionManager;
import org.usfirst.frc.team1318.robot.general.PowerManager;
import org.usfirst.frc.team1318.robot.intake.IntakeController;
import org.usfirst.frc.team1318.robot.shooter.ShooterController;
import org.usfirst.frc.team1318.robot.vision.VisionManager;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;

public class RobotModule extends AbstractModule
{
    @Override
    protected void configure()
    {
    }

    @Singleton
    @Provides
    public IDashboardLogger getLogger()
    {
        IDashboardLogger logger = new SmartDashboardLogger();
//        try
//        {
//            String fileName = String.format("/home/lvuser/%1$d.csv", Calendar.getInstance().getTime().getTime());
//            IDashboardLogger csvLogger = new CSVLogger(fileName, new String[] { "r.time", "shooter.power", "vision.mAngle", "vision.dist" });
//            logger = new MultiLogger(logger, csvLogger);
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }

        return logger;
    }

    @Singleton
    @Provides
    public IButtonMap getButtonMap()
    {
        return new ButtonMap();
    }

    @Singleton
    @Provides
    public ITimer getTimer()
    {
        return new TimerWrapper();
    }

    @Singleton
    @Provides
    public ControllerManager getControllerManager(Injector injector)
    {
        List<IController> controllerList = new ArrayList<>();
        controllerList.add(injector.getInstance(PowerManager.class));
        controllerList.add(injector.getInstance(PositionManager.class));
        controllerList.add(injector.getInstance(VisionManager.class));
        controllerList.add(injector.getInstance(CompressorController.class));
        controllerList.add(injector.getInstance(DriveTrainController.class));
        controllerList.add(injector.getInstance(ClimberController.class));
        controllerList.add(injector.getInstance(IntakeController.class));
        controllerList.add(injector.getInstance(ShooterController.class));
        return new ControllerManager(controllerList);
    }

    @Singleton
    @Provides
    @Named("USER_DRIVER_JOYSTICK")
    public IJoystick getDriverJoystick()
    {
        return new JoystickWrapper(ElectronicsConstants.JOYSTICK_DRIVER_PORT);
    }

    @Singleton
    @Provides
    @Named("USER_CODRIVER_JOYSTICK")
    public IJoystick getCoDriverJoystick()
    {
        return new JoystickWrapper(ElectronicsConstants.JOYSTICK_CO_DRIVER_PORT);
    }

    @Singleton
    @Provides
    public ICompressor getCompressor()
    {
        return new CompressorWrapper(ElectronicsConstants.PCM_B_MODULE);
    }

    @Singleton
    @Provides
    public IPowerDistributionPanel getPowerManagerPdp()
    {
        return new PowerDistributionPanelWrapper();
    }

    @Singleton
    @Provides
    @Named("VISION_GEAR_LIGHT")
    public ISolenoid getVisionGearLight()
    {
        SolenoidWrapper gearLight = new SolenoidWrapper(
            ElectronicsConstants.PCM_B_MODULE,
            ElectronicsConstants.VISION_GEAR_LIGHT_CHANNEL);

        return gearLight;
    }

    @Singleton
    @Provides
    @Named("VISION_SHOOTER_LIGHT")
    public ISolenoid getVisionShooterLight()
    {
        SolenoidWrapper shooterLight = new SolenoidWrapper(
            ElectronicsConstants.PCM_B_MODULE,
            ElectronicsConstants.VISION_SHOOTER_LIGHT_CHANNEL);

        return shooterLight;
    }

    @Singleton
    @Provides
    @Named("DRIVETRAIN_LEFTMOTOR")
    public IMotor getDriveTrainLeftMotor()
    {
        return new VictorWrapper(ElectronicsConstants.DRIVETRAIN_LEFT_TALON_CHANNEL);
    }

    @Singleton
    @Provides
    @Named("DRIVETRAIN_RIGHTMOTOR")
    public IMotor getDriveTrainRightMotor()
    {
        return new VictorWrapper(ElectronicsConstants.DRIVETRAIN_RIGHT_TALON_CHANNEL);
    }

    @Singleton
    @Provides
    @Named("DRIVETRAIN_LEFTENCODER")
    public IEncoder getDriveTrainLeftEncoder()
    {
        EncoderWrapper encoder = new EncoderWrapper(
            ElectronicsConstants.DRIVETRAIN_LEFT_ENCODER_CHANNEL_A,
            ElectronicsConstants.DRIVETRAIN_LEFT_ENCODER_CHANNEL_B);

        encoder.setDistancePerPulse(HardwareConstants.DRIVETRAIN_LEFT_PULSE_DISTANCE);

        return encoder;
    }

    @Singleton
    @Provides
    @Named("DRIVETRAIN_RIGHTENCODER")
    public IEncoder getDriveTrainRightEncoder()
    {
        EncoderWrapper encoder = new EncoderWrapper(
            ElectronicsConstants.DRIVETRAIN_RIGHT_ENCODER_CHANNEL_A,
            ElectronicsConstants.DRIVETRAIN_RIGHT_ENCODER_CHANNEL_B);

        encoder.setDistancePerPulse(HardwareConstants.DRIVETRAIN_RIGHT_PULSE_DISTANCE);

        return encoder;
    }

    @Singleton
    @Provides
    @Named("INTAKE_MOTOR")
    public IMotor getIntakeMotor()
    {
        TalonWrapper intake = new TalonWrapper(
            ElectronicsConstants.INTAKE_MOTOR_CHANNEL);
        return intake;
    }

    @Singleton
    @Provides
    @Named("INTAKE_ARM_SOLENOID")
    public IDoubleSolenoid getIntakeExtender()
    {
        DoubleSolenoidWrapper intakeExtender = new DoubleSolenoidWrapper(
            ElectronicsConstants.INTAKE_EXTENDER_SOLENOID_CHANNEL_A,
            ElectronicsConstants.INTAKE_EXTENDER_SOLENOID_CHANNEL_B);
        return intakeExtender;
    }

    @Singleton
    @Provides
    @Named("INTAKE_CONVEYOR_SOLENOID")
    public IDoubleSolenoid getIntakeGearExtender()
    {
        DoubleSolenoidWrapper intakeConveyorExtender = new DoubleSolenoidWrapper(
            ElectronicsConstants.INTAKE_CONVEYOR_EXTENDER_SOLENOID_CHANNEL_A,
            ElectronicsConstants.INTAKE_CONVEYOR_EXTENDER_SOLENOID_CHANNEL_B);
        return intakeConveyorExtender;
    }

    @Singleton
    @Provides
    @Named("INTAKE_GEAR_THROUGH_BEAM_SENSOR")
    public IAnalogInput getThroughBeamSensor()
    {
        AnalogInputWrapper throughBeamSensor = new AnalogInputWrapper(
            ElectronicsConstants.INTAKE_GEAR_THROUGH_BEAM_SENSOR_CHANNEL);
        return throughBeamSensor;
    }

    @Singleton
    @Provides
    @Named("INTAKE_GEAR_INDICATOR_LIGHT")
    public ISolenoid getGearIndicator()
    {
        SolenoidWrapper gearLight = new SolenoidWrapper(
            ElectronicsConstants.PCM_B_MODULE,
            ElectronicsConstants.INTAKE_GEAR_INDICATOR_CHANNEL);
        return gearLight;
    }
    
    @Singleton
    @Provides
    @Named("INTAKE_MOUTH_SOLENOID")
    public IDoubleSolenoid getIntakeMouthExtender()
    {
        DoubleSolenoidWrapper intakeMouthExtender = new DoubleSolenoidWrapper(
            ElectronicsConstants.PCM_B_MODULE,
            ElectronicsConstants.INTAKE_MOUTH_EXTENDER_CHANNEL_A,
            ElectronicsConstants.INTAKE_MOUTH_EXTENDER_CHANNEL_B);
        return intakeMouthExtender;
    }

    @Singleton
    @Provides
    @Named("CLIMBER_MOTOR")
    public IMotor getCimberClimber()
    {
        TalonWrapper climber = new TalonWrapper(
            ElectronicsConstants.CLIMBER_MOTOR_CHANNEL);
        return climber;
    }

    @Singleton
    @Provides
    @Named("SHOOTER_HOOD")
    public IDoubleSolenoid getShooterHood()
    {
        DoubleSolenoidWrapper hood = new DoubleSolenoidWrapper(
            ElectronicsConstants.SHOOTER_HOOD_CHANNEL_A,
            ElectronicsConstants.SHOOTER_HOOD_CHANNEL_B);

        return hood;
    }

    @Singleton
    @Provides
    @Named("SHOOTER_FEEDER")
    public IMotor getShooterFeeder()
    {
        TalonWrapper feeder = new TalonWrapper(
            ElectronicsConstants.SHOOTER_FEEDER_CHANNEL);

        return feeder;
    }

    @Singleton
    @Provides
    @Named("SHOOTER_READY_LIGHT")
    public ISolenoid getShooterReadyLight()
    {
        SolenoidWrapper readyLight = new SolenoidWrapper(
            ElectronicsConstants.PCM_B_MODULE,
            ElectronicsConstants.SHOOTER_READY_LIGHT_CHANNEL);

        return readyLight;
    }

    @Singleton
    @Provides
    @Named("SHOOTER_TARGETING_LIGHT")
    public IRelay getShooterTargetingLight()
    {
        RelayWrapper targetingLight = new RelayWrapper(
            ElectronicsConstants.SHOOTER_TARGETING_LIGHT_CHANNEL,
            RelayDirection.kForward);

        return targetingLight;
    }

    @Singleton
    @Provides
    @Named("SHOOTER_SHOOTER")
    public ICANTalon getOneMotorMotor()
    {
        CANTalonWrapper master = new CANTalonWrapper(ElectronicsConstants.SHOOTER_MASTER_MOTOR_CHANNEL);
        master.enableBrakeMode(false);
        master.reverseSensor(false);

        CANTalonWrapper follower = new CANTalonWrapper(ElectronicsConstants.SHOOTER_FOLLOWER_MOTOR_CHANNEL);
        follower.enableBrakeMode(false);
        follower.reverseOutput(true);
        follower.changeControlMode(CANTalonControlMode.Follower);
        follower.set(ElectronicsConstants.SHOOTER_MASTER_MOTOR_CHANNEL);

        if (TuningConstants.SHOOTER_USE_CAN_PID)
        {
            master.changeControlMode(CANTalonControlMode.Speed);
            master.setPIDF(
                TuningConstants.SHOOTER_CAN_PID_KP,
                TuningConstants.SHOOTER_CAN_PID_KI,
                TuningConstants.SHOOTER_CAN_PID_KD,
                TuningConstants.SHOOTER_CAN_PID_KF);
        }
        else
        {
            master.changeControlMode(CANTalonControlMode.PercentVbus);
        }

        return master;
    }

    @Singleton
    @Provides
    @Named("AUTO_SIDE_OF_FIELD")
    public IDigitalInput getAutoSideOfField()
    {
        return new DigitalInputWrapper(ElectronicsConstants.AUTO_SIDE_OF_FIELD_CHANNEL);
    }

    @Singleton
    @Provides
    @Named("AUTO_DIP_SWITCH_A")
    public IDigitalInput getAutoDipSwitchA()
    {
        return new DigitalInputWrapper(ElectronicsConstants.AUTO_DIP_SWITCH_A_CHANNEL);
    }

    @Singleton
    @Provides
    @Named("AUTO_DIP_SWITCH_B")
    public IDigitalInput getAutoDipSwitchB()
    {
        return new DigitalInputWrapper(ElectronicsConstants.AUTO_DIP_SWITCH_B_CHANNEL);
    }
}
