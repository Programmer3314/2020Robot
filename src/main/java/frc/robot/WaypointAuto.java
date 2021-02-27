/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.util.ArrayList;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.DriveController.DriveState;
import frc.robot.DriveController.MoveParameters;

public abstract class WaypointAuto implements AutoStateMachines{
    int waypointCounter;
    double originalEncoderPos;
    double currentEncoderPos;
    double gyroAngle;
    long start;
    long finish;

    ArrayList <Waypoint> waypoints = new ArrayList<Waypoint>(); 

    @Override
    public void update(MoveParameters mP) {
        currentEncoderPos = Robot.driveController.encoderPos - originalEncoderPos;
        gyroAngle = Robot.rawGyro;
        mP.currentState = DriveState.MANUAL;
        mP.turn = 0;

        //TODO: (Phase 1) Can this be done less often?
        Waypoint cw = waypoints.get(waypointCounter);

        if(waypointCounter < waypoints.size() - 1){
            if(cw.isComplete()){
                waypointCounter++;
                cw = waypoints.get(waypointCounter);
                //Robot.driveController.resetEncoderVal();
                cw.init();
            }
        }

        cw.update(mP);

        if(waypointCounter == waypoints.size() - 2){
            finish = System.currentTimeMillis();
            SmartDashboard.putNumber("Finished Time:", (finish - start) / 1000.0);
        }

        SmartDashboard.putNumber("Elapsed Time:", (finish - start) / 1000.0);
        SmartDashboard.putNumber("mP Forward: ", mP.forward);
        SmartDashboard.putNumber("mP Turn: ", mP.turn);
        SmartDashboard.putNumber("Current Encoder Value", currentEncoderPos);
        SmartDashboard.putNumber("Original Encoder Value", originalEncoderPos);
        SmartDashboard.putNumber("Original Original Encoder Value", Robot.driveController.encoderPos);
        SmartDashboard.putNumber("Waypoint Index Position: ", waypointCounter);
    }

    @Override
    public void activate() {
        waypoints.get(0).init();
        originalEncoderPos = Robot.driveController.encoderPos;
        SmartDashboard.putNumber("Active Original Encoder pos !", originalEncoderPos);
    }

    @Override
    public void reset() {
        waypointCounter = 0;
        Waypoint.scale = 1.0;
        start = System.currentTimeMillis();
        setWaypoints();
        //Robot.driveController.resetEncoderVal();
    }

    public abstract void setWaypoints();

    @Override
    public void LogHeader() {

    }

    @Override
    public void LogData() {

    }

}