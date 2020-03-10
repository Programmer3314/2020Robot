/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.DriveController.DriveState;
import frc.robot.DriveController.MoveParameters;

/**
 * Add your docs here.
 */
public class FiveBallAutoTrench implements AutoStateMachines{
    public enum AutoStates{
        IDLE,
        DELAY,
        START,
        SHOOT,
        TURN_PERPENDICULAR_TO_ALLIANCE_STATION_WALL,
        DRIVE_BACKWARDS_AND_BALLCHASE_1,
        DRIVE_BACKWARDS_AND_BALLCHASE_2,
        DRIVE_FORWARD,
        CALCULATE_GYRO,
        ALIGN_2,
        SHOOT_2,
        DONE
        
    }
    NetworkTable portalTapeTargetTable;
    public static AutoStates autoStates = AutoStates.IDLE;
    double targetShooterRPM, shooterRPMTolerance;
    double queuingBeltSpeed;
    int counter;
    double encoderPos;
    double lastEncoderPos;  
    double gyroTolerance, gyroAngleDesired;
    boolean useGyro;
    double angleOffset;

    public FiveBallAutoTrench(){
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
                Robot.shooter.setHoodSetpoint(-1450);
                Robot.shooter.setTargetShooterRPM(3600);

                Robot.shooter.setTargetShooterRPMTolerance(50);
                queuingBeltSpeed = Constants.queuingBeltSpeed;
                gyroTolerance = 2;

                Robot.shooter.autoCounter = 3;
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

                if (Math.abs(mP.angle - Robot.cleanGyro) <= gyroTolerance) {
                    counter++;
                } else {
                    counter = 0;
                }

                if (counter >= 10) {
                    Robot.shooter.groundIntakeAll();
                    mP.currentState = DriveState.NONE;
                    autoStates = AutoStates.DRIVE_BACKWARDS_AND_BALLCHASE_1;
                    lastEncoderPos = encoderPos;
                    counter = 0;
                }

                // lastEncoderPos = encoderPos;
            break;

            case DRIVE_BACKWARDS_AND_BALLCHASE_1:
                counter++;

                if(counter >= 10){
                mP.currentState = DriveState.BALLCHASE;
                // mP.forward = -0.18;//-0.175;//0.175;

                mP.forward = -0.3;//-0.26;//-0.175;//0.175;

                // if(Math.abs((encoderPos - lastEncoderPos)) / Constants.encoderTicksToFeet >= 15.0){
                //     Robot.shooter.abortIntake();``   `````
                //     // Robot.shooter.prepareShooter();
                //     mP.currentState = DriveState.NONE;
                //     autoStates = AutoStates.DRIVE_FORWARD_2;
                //     lastEncoderPos = encoderPos;
                // }

                if(Math.abs((encoderPos - lastEncoderPos)) / Constants.encoderTicksToFeet >= 5.0){
                    autoStates = AutoStates.DRIVE_BACKWARDS_AND_BALLCHASE_2;
                    lastEncoderPos = encoderPos;
                }
                }
            break;

            case DRIVE_BACKWARDS_AND_BALLCHASE_2:
                mP.forward = -0.1625;//-0.165;//-0.17;//-0.18;

                if(Math.abs((encoderPos - lastEncoderPos)) / Constants.encoderTicksToFeet >= 6.5){
                    Robot.shooter.abortIntake();
                    // Robot.shooter.prepareShooter();
                    mP.currentState = DriveState.NONE;
                    autoStates = AutoStates.DRIVE_FORWARD;
                    lastEncoderPos = encoderPos;
                }

            break;

            case DRIVE_FORWARD:
                mP.currentState = DriveState.POWERPORTALIGNMENT;
                mP.forward = 0.22;//0.325;//0.3;//0.2//-0.2;

                Robot.shooter.setHoodSetpoint(-1450);
                Robot.shooter.setTargetShooterRPM(3600);
                Robot.shooter.prepareShooter();

                if(Math.abs((encoderPos - lastEncoderPos)) / Constants.encoderTicksToFeet >= 2.5){
                    mP.forward = 0.0;
                    mP.currentState = DriveState.NONE;//changed
                    counter = 0;
                    autoStates = AutoStates.CALCULATE_GYRO;
                }
            break;

            case CALCULATE_GYRO:
                // mP.currentState = DriveState.NONE;
                counter++;

                if(portalTapeTargetTable.getEntry("Retroreflective Target Found").getBoolean(false)){
                    if(counter >= 20){
                    angleOffset = portalTapeTargetTable.getEntry("X Angle").getDouble(0);
                    portalTapeTargetTable.getEntry("gyro").setDouble(Robot.rawGyro);
                    angleOffset += Robot.rawGyro;
                    gyroAngleDesired = angleOffset; 
 
                    autoStates = AutoStates.ALIGN_2;
                    }
                }
            break;

            case ALIGN_2:

                useGyro = true;
                //mP.angle = angleOffset;
                mP.currentState = DriveState.POWERPORTALIGNMENT;
                
                Robot.shooter.setTargetShooterRPMTolerance(50);
                queuingBeltSpeed = Constants.queuingBeltSpeed;
                gyroTolerance = 2.0;//1.75;

                // if(Math.abs(DriveController.angleOffset - Robot.cleanGyro) <= gyroTolerance){
                //     Robot.shooter.shootAll(queuingBeltSpeed, useGyro, DriveController.angleOffset, gyroTolerance);
                //     autoStates = AutoStates.SHOOT_2;
                // }

                // 
                // and move on. The next state will wait on Shooter which will wait 
                // on the gyro.
                //if(Math.abs(Robot.cleanGyro - gyroAngleDesired) <= gyroTolerance){
                    Robot.shooter.autoCounter = 3;
                    Robot.shooter.shootAll(queuingBeltSpeed, useGyro, gyroAngleDesired, gyroTolerance);
                    autoStates = AutoStates.SHOOT_2;
                //}

            break;
            
            case SHOOT_2:
                if(!Robot.shooter.getShooterStatus()){
                    autoStates = AutoStates.DONE;
                }
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

    public void LogHeader() {
        Logger.Header("autoStates,");
    }

    public void LogData() {
        Logger.singleEnum(autoStates);
    }
}
