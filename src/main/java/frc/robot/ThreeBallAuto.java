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
public class ThreeBallAuto implements AutoStateMachines{
    public enum AutoStates {
        IDLE,
        DELAY,
        START,
        ALIGN, //Align to power port
        SHOOT, // Shoot 3 balls
        TURN_TO_GYRO, // Turn to gyro 0
        FORWARD, //Move forward off the line
        DONE
    }
    NetworkTable portalTapeTargetTable;
    int counter;
    double pastGyro;
    AutoStates autoStates;
    //Shooter shooter;
    //DriveController driveController;
    //DriveController.MoveParameters mP;
    //double targetShooterRPM, shooterRPMTolerance;
    double queuingBeltSpeed;
    boolean useGyro;
    double angleOffset;
    double gyroTolerance, gyroAngleDesired;
    double encoderPos;
    double lastEncoderPos;

    Shooter shooter;

    public ThreeBallAuto(Shooter shooter){
        autoStates = AutoStates.IDLE;
        //shooter = new Shooter(7, 8, 11, 9);
        counter = 0;
        portalTapeTargetTable = Robot.ntInst.getTable("Retroreflective Tape Target");
        //mP = driveController.new MoveParameters();
        // targetShooterRPM = SmartDashboard.getNumber("Shooter RPM Desired", 0);
        //shooterRPMTolerance = SmartDashboard.getNumber("Shooter RPM Tolerance Desired", 0);
        shooter.setTargetShooterRPMTolerance(SmartDashboard.getNumber("Shooter RPM Tolerance Desired", 0));
        queuingBeltSpeed = SmartDashboard.getNumber("Queuing Belt Speed", 0.5);
        gyroTolerance = SmartDashboard.getNumber("Gyro Tolerance" , 1);
        
        useGyro = true;

        this.shooter = shooter;
    }

    @Override
    public void update(MoveParameters mP){
        encoderPos = Robot.driveController.encoderPos;
        pastGyro = Robot.cleanGyro;
        switch(autoStates){
            case IDLE:
                
            break;

            case DELAY:
                counter++;
                if(counter >= 75){
                    autoStates = AutoStates.START;
                }

            break;

            case START:
                shooter.setHoodSetpoint(-1400);
                shooter.setTargetShooterRPM(3600);
                //targetShooterRPM = 3600;

                counter = 0;
                if(portalTapeTargetTable.getEntry("Retroreflective Target Found").getBoolean(false)){
                    angleOffset = portalTapeTargetTable.getEntry("X Angle").getDouble(0);
                    portalTapeTargetTable.getEntry("gyro").setDouble(Robot.rawGyro);
                    angleOffset += Robot.rawGyro;
                    gyroAngleDesired = angleOffset; 
                    autoStates = AutoStates.ALIGN;
                }
            break;
            
            case ALIGN:
                //mP.currentState = DriveState.POWERPORTALIGNMENT;
                mP.angle = angleOffset;
                mP.currentState = DriveState.TURN_TO_GYRO;
                // targetShooterRPM = SmartDashboard.getNumber("Shooter RPM Desired", 0);
                //shooterRPMTolerance = SmartDashboard.getNumber("Shooter RPM Tolerance Desired", 0);
                shooter.setTargetShooterRPMTolerance(SmartDashboard.getNumber("Shooter RPM Tolerance Desired", 0));
                queuingBeltSpeed = SmartDashboard.getNumber("Queuing Belt Speed", 0.5);
                gyroTolerance = SmartDashboard.getNumber("Gyro Tolerance" , 5);
                Robot.shooter.shootAll(queuingBeltSpeed, useGyro, gyroAngleDesired, gyroTolerance);

                autoStates = AutoStates.SHOOT;
            break;

            case SHOOT:
                mP.angle = angleOffset;
                mP.currentState = DriveState.TURN_TO_GYRO;
                if(Robot.shooter.getShooterStatus() == false){
                    autoStates = AutoStates.TURN_TO_GYRO;
                }
            break;

            case TURN_TO_GYRO:
                //want to code a gyro to 0 case in drive controller but don't know how to. want to call it in this case
                mP.angle = 0;
                mP.currentState = DriveState.TURN_TO_GYRO;
                if (Math.abs(Robot.cleanGyro) <= gyroTolerance) {
                    counter++;
                } else {
                    counter = 0;
                }

                if (counter >= 10) {
                    autoStates = AutoStates.FORWARD;
                    counter = 0;
                }
                lastEncoderPos = encoderPos;
            break;

            case FORWARD:
                //go 2 - 5 inches forward or something
                mP.currentState = DriveState.GYROLOCK;
                mP.forward = 0.2;//-0.2;
                if(Math.abs((encoderPos - lastEncoderPos)) / Constants.encoderTicksToFeet>=4){
                    autoStates = AutoStates.DONE;
                }
            break;

            case DONE:
                counter = 0;
                mP.currentState = DriveState.NONE;
                autoStates = AutoStates.IDLE;
            break;
        }
        SmartDashboard.putString("Auto State", autoStates.toString());
    }
    @Override
    public void activate(){
        counter = 0;
        autoStates = AutoStates.DELAY;
    }

    public void reset(){
        autoStates = AutoStates.IDLE;
        Robot.shooter.reset();
    }
}
