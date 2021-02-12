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
    ArrayList <Waypoint> waypoints = new ArrayList<Waypoint>(); 

    @Override
    public void update(MoveParameters mP) {
        currentEncoderPos = Robot.driveController.encoderPos - originalEncoderPos;
        mP.currentState = DriveState.MANUAL;
        mP.turn = 0;

        //TODO: (Phase 1) Can this be done less often?
        Waypoint cw = waypoints.get(waypointCounter);

        //TODO: (Phase 1) make this work when driving forward or backward
        if(currentEncoderPos < cw.encoderValue){
            //TODO: (Phase 1) make this not crash at the end of the list        
            waypointCounter++;
            cw = waypoints.get(waypointCounter);
            originalEncoderPos = Robot.driveController.encoderPos;
            if(waypointCounter >= waypoints.size()){
                mP.forward = 0;
                mP.turn = 0;
            }
        }

        mP.forward = cw.forward;
        mP.turn = cw.turn;
        
        SmartDashboard.putNumber("Current Encoder Value", currentEncoderPos);
        SmartDashboard.putNumber("Original Encoder Value", originalEncoderPos);
        SmartDashboard.putNumber("Original Original Encoder Value", Robot.driveController.encoderPos);
    }

    @Override
    public void activate() {
        originalEncoderPos = Robot.driveController.encoderPos;
        SmartDashboard.putNumber("Active Original Encoder pos !", originalEncoderPos);
    }

    @Override
    public void reset() {
        waypointCounter = 0;
        //TODO: (Phase 1) Make it simpler to enter distances
        //TODO: (Phase 1) Add Stop/Done Waypoint
        waypoints.add(new Waypoint(0.3, 0.0, 5 * Constants.falconEncoderTicksToFeet));
        waypoints.add(new Waypoint(0.2, -0.05, 4 * Constants.falconEncoderTicksToFeet));
        waypoints.add(new Waypoint(0.3, 0, 2 * Constants.falconEncoderTicksToFeet));
    }

    @Override
    public void LogHeader() {

    }

    @Override
    public void LogData() {

    }

}