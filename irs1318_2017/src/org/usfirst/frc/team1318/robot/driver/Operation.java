package org.usfirst.frc.team1318.robot.driver;

public enum Operation
{
    // DriveTrain operations:
    DriveTrainMoveForward,
    DriveTrainTurn,
    DriveTrainSimpleMode,
    DriveTrainUsePositionalMode,
    DriveTrainLeftPosition,
    DriveTrainRightPosition,
    DriveTrainSwapFrontOrientation,
    
    // intake
    IntakeExtendArm,
    
    SetMotorSpeed,

    // Other general operations:
    EnablePID,
    DisablePID,
}
