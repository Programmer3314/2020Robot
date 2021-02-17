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

public class BarrelRunAuto implements AutoStateMachines{
    int waypointCounter;
    double originalEncoderPos;
    double currentEncoderPos;
    double gyroAngle;

    ArrayList <Waypoint> waypoints = new ArrayList<Waypoint>(); 

    @Override
    public void update(MoveParameters mP) {
        // currentEncoderPos = Robot.driveController.encoderPos - originalEncoderPos;
        // gyroAngle = Robot.cleanGyro;
        mP.currentState = DriveState.MANUAL;
        mP.turn = 0;

        //TODO: (Phase 1) Can this be done less often?
        Waypoint cw = waypoints.get(waypointCounter);

        if(waypointCounter < waypoints.size() - 1){
            if(cw.isComplete()){
                waypointCounter++;
                cw = waypoints.get(waypointCounter);
                Robot.driveController.resetEncoderVal();
                Robot.robot.resetEncoderVal();
            }
        }

        cw.update(mP);

        SmartDashboard.putNumber("Current Encoder Value", Robot.driveController.encoderPos);
        SmartDashboard.putNumber("Original Encoder Value", originalEncoderPos);
        SmartDashboard.putNumber("Original Original Encoder Value", Robot.driveController.encoderPos);
        SmartDashboard.putNumber("Waypoint Index Position: ", waypointCounter);
    }

    @Override
    public void activate() {
        Robot.robot.resetEncoderVal();
        originalEncoderPos = Robot.driveController.encoderPos;
        SmartDashboard.putNumber("Active Original Encoder pos !", originalEncoderPos);
    }

    @Override
    public void reset() {
        waypointCounter = 0;

        waypoints.add(new WaypointDistance(0.3, -0.15, 4)); //FORWARD LEFT
        //waypoints.add(new WaypointDistance(0.5, 0, 17.5)); //FORWARD 
        waypoints.add(new WaypointDistance(0, 0, 0.0)); //STOP

    }

    @Override
    public void LogHeader() {

    }

    @Override
    public void LogData() {

    }

}