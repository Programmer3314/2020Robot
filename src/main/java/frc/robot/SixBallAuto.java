
package frc.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.DriveController.DriveState;
import frc.robot.DriveController.MoveParameters;
import frc.robot.ThreeBallAuto.AutoStates;

public class SixBallAuto implements AutoStateMachines{
    public enum AutoStates{
        IDLE,
        DELAY,
        START,
        SHOOT,
        TURN_PERPENDICULAR_TO_ALLIANCE_STATION_WALL,
        DRIVE_BACKWARDS_AND_BALLCHASE,
        DRIVE_FORWARD,
        CALCULATE_GYRO,
        ALIGN_2,
        SHOOT_2,
        DONE
        
    }
    NetworkTable portalTapeTargetTable;
    AutoStates autoStates;
    double targetShooterRPM, shooterRPMTolerance;
    double queuingBeltSpeed;
    int counter;
    double encoderPos;
    double lastEncoderPos;
    double gyroTolerance, gyroAngleDesired;
    boolean useGyro;
    double angleOffset;

    public SixBallAuto(){
        autoStates = AutoStates.IDLE;
        counter = 0;

        portalTapeTargetTable = Robot.ntInst.getTable("Retroreflective Tape Target");
        shooterRPMTolerance = SmartDashboard.getNumber("Shooter RPM Tolerance Desired", 0);
        queuingBeltSpeed = SmartDashboard.getNumber("Queuing Belt Speed", 0.5);
        gyroTolerance = SmartDashboard.getNumber("Gyro Tolerance" , 1);

        useGyro = false;
    }
    @Override
    public void update(MoveParameters mP){
        encoderPos = Robot.driveController.encoderPos;
        switch(autoStates){
            case IDLE:
            break;

            case DELAY:

            break;
            
            case START:
                Robot.shooter.setHoodSetpoint(-1400);
                Robot.shooter.setTargetShooterRPM(3600);

                Robot.shooter.setTargetShooterRPMTolerance(50);
                queuingBeltSpeed = SmartDashboard.getNumber("Queuing Belt Speed", 0.5);
                gyroTolerance = SmartDashboard.getNumber("Gyro Tolerance" , 5);

                Robot.shooter.shootAll(queuingBeltSpeed, useGyro, 0, gyroTolerance);

                autoStates = AutoStates.SHOOT;
            break;
            
            case SHOOT:
                if(Robot.shooter.getShooterStatus() == false){
                    autoStates = AutoStates.TURN_PERPENDICULAR_TO_ALLIANCE_STATION_WALL;
                }
            break;
            
            case TURN_PERPENDICULAR_TO_ALLIANCE_STATION_WALL:
                mP.angle = 25;
                mP.currentState = DriveState.TURN_TO_GYRO;

                if (Math.abs(Robot.cleanGyro) <= 25) {
                    counter++;
                } else {
                    counter = 0;
                }

                if (counter >= 10) {
                    Robot.shooter.groundIntakeAll();
                    autoStates = AutoStates.DRIVE_BACKWARDS_AND_BALLCHASE;
                    counter = 0;
                }

                lastEncoderPos = encoderPos;
            break;

            case DRIVE_BACKWARDS_AND_BALLCHASE:

                mP.currentState = DriveState.BALLCHASE;
                mP.forward = 0.175;

                if(Math.abs((encoderPos - lastEncoderPos)) / Constants.encoderTicksToFeet >= 16){
                    Robot.shooter.abortIntake();
                    mP.currentState = DriveState.NONE;
                    autoStates = AutoStates.DRIVE_FORWARD;
                    lastEncoderPos = encoderPos;
                }
            break;

            case DRIVE_FORWARD:
                mP.currentState = DriveState.GYROLOCK;
                mP.forward = -0.2;

                if(Math.abs((encoderPos - lastEncoderPos)) / Constants.encoderTicksToFeet >= 5){
                    mP.forward = 0.0;
                    autoStates = AutoStates.CALCULATE_GYRO;
                }
            break;

            case CALCULATE_GYRO:
                if(portalTapeTargetTable.getEntry("Retroreflective Target Found").getBoolean(false)){
                    angleOffset = portalTapeTargetTable.getEntry("X Angle").getDouble(0);
                    portalTapeTargetTable.getEntry("gyro").setDouble(Robot.rawGyro);
                    angleOffset += Robot.rawGyro;
                    gyroAngleDesired = angleOffset; 
                    autoStates = AutoStates.ALIGN_2;
                }
            break;

            case ALIGN_2:
                Robot.shooter.setHoodSetpoint(-1500);
                Robot.shooter.setTargetShooterRPM(3600);

                useGyro = true;
                mP.angle = angleOffset;
                mP.currentState = DriveState.TURN_TO_GYRO;
                
                Robot.shooter.setTargetShooterRPMTolerance(50);
                queuingBeltSpeed = SmartDashboard.getNumber("Queuing Belt Speed", 0.5);
                gyroTolerance = SmartDashboard.getNumber("Gyro Tolerance" , 5);

                Robot.shooter.shootAll(queuingBeltSpeed, useGyro, gyroAngleDesired, gyroTolerance);

                autoStates = AutoStates.SHOOT_2;
            break;
            
            case SHOOT_2:
                autoStates = AutoStates.DONE;
            break;

            case DONE:
                useGyro = false;
                counter = 0;
            break;
        
        }
    }
    
    @Override
    public void activate(){
        counter = 0;
        autoStates = AutoStates.START;
    }

    public void reset(){
        autoStates = AutoStates.IDLE;
        Robot.shooter.reset();
    }
}
