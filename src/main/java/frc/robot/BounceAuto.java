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

public class BounceAuto implements AutoStateMachines{
    int waypointCounter;
    double originalEncoderPos;
    double currentEncoderPos;
    double gyroAngle;

    ArrayList <Waypoint> waypoints = new ArrayList<Waypoint>(); 

    @Override
    public void update(MoveParameters mP) {
        currentEncoderPos = Robot.driveController.encoderPos - originalEncoderPos;
        gyroAngle = Robot.cleanGyro;
        mP.currentState = DriveState.MANUAL;
        mP.turn = 0;

        //TODO: (Phase 1) Can this be done less often?
        Waypoint cw = waypoints.get(waypointCounter);

        if(waypointCounter < waypoints.size() - 1){
            if(cw.isComplete()){
                waypointCounter++;
                cw = waypoints.get(waypointCounter);
                Robot.driveController.resetEncoderVal();
            }
        }

        cw.update(mP);

        SmartDashboard.putNumber("Current Encoder Value", currentEncoderPos);
        SmartDashboard.putNumber("Original Encoder Value", originalEncoderPos);
        SmartDashboard.putNumber("Original Original Encoder Value", Robot.driveController.encoderPos);
        SmartDashboard.putNumber("Waypoint Index Position: ", waypointCounter);
    }

    @Override
    public void activate() {
        originalEncoderPos = Robot.driveController.encoderPos;
        SmartDashboard.putNumber("Active Original Encoder pos !", originalEncoderPos);
    }

    @Override
    public void reset() {
        waypointCounter = 0;
        Robot.driveController.resetEncoderVal();

        waypoints.add(new WaypointGyro(0.2, -0.1, -82)); //FORWARD RIGHT
        waypoints.add(new WaypointDistance(0, 0, 0.0)); //STOP

        //scale = 1.1
        // waypoints.add(new WaypointDistance(0.3, 0, 2.0)); //FORWARD
        // waypoints.add(new WaypointDistance(0.3, -0.1, 3.1)); //QUARTER TURN FORWARD LEFT
        // waypoints.add(new WaypointDistance(0.3, 0, 0.8)); //FORWARD TO CONE
        // waypoints.add(new WaypointDistance(-0.3, -0.07, -2.8)); //SMALL TURN BACK RIGHT
        // waypoints.add(new WaypointDistance(-0.3, 0, -5.0)); //BACKWARD        
        // waypoints.add(new WaypointDistance(-0.3, -0.12, -9.5)); //SEMI TURN BACK RIGHT
        // waypoints.add(new WaypointDistance(-0.3, 0, -6.0)); //FORWARD TO CONE
        // waypoints.add(new WaypointDistance(0.3, 0, 4.6)); //FORWARD
        // waypoints.add(new WaypointDistance(0.3, -0.1, 3.42)); //QUARTER TURN LEFT
        // waypoints.add(new WaypointDistance(0.3, 0, 0.5)); //FORWARD
        // waypoints.add(new WaypointDistance(0.3, -0.1, 3.42)); //QUARTER TURN LEFT
        // waypoints.add(new WaypointDistance(0.3, 0, 5.0)); //FORWARD TO CONE
        // waypoints.add(new WaypointDistance(-0.3, -0.1, -6.5)); //QUARTER TURN BACK LEFT
        // waypoints.add(new WaypointDistance(-0.3, 0, -1.5)); //FORWARD
        // waypoints.add(new WaypointDistance(0, 0, 0.0)); //STOP
    }

    @Override
    public void LogHeader() {

    }

    @Override
    public void LogData() {

    }

}