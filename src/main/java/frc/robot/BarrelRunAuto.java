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
        currentEncoderPos = Robot.driveController.encoderPos - originalEncoderPos;
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

        SmartDashboard.putNumber("Current Encoder Value", Robot.driveController.encoderPos);
        SmartDashboard.putNumber("Original Encoder Value", originalEncoderPos);
        SmartDashboard.putNumber("Original Original Encoder Value", Robot.driveController.encoderPos);
        SmartDashboard.putNumber("Waypoint Index Position: ", waypointCounter);
    }

    @Override
    public void activate() {
        //Robot.driveController.resetEncoderVal();
        waypoints.get(0).init();
        originalEncoderPos = Robot.driveController.encoderPos;

        SmartDashboard.putNumber("Activate encoderPos: ", originalEncoderPos);
    }

    @Override
    public void reset() {
        waypointCounter = 0;

        waypoints.add(new WaypointDistance(0.6, 0, 9)); //FORWARD
        waypoints.add(new WaypointGyro(0.3 * 1.2, 0.13 * 1.2, 340)); //FORWARD CIRCLE TURN RIGHT
        waypoints.add(new WaypointDistance(0.6, 0, 6.5)); //FORWARD
        waypoints.add(new WaypointGyro(0.3 * 1.2, -0.14 * 1.2, -290 + 360)); //FORWARD CIRCLE TURN LEFT
        waypoints.add(new WaypointDistance(0.6, 0, 5.75)); //FORWARD
        waypoints.add(new WaypointGyro(0.3 * 1.2 , -0.15 * 1.2, -500 + 360)); //FORWARD CIRCLE TURN LEFT
        waypoints.add(new WaypointGyro(0.4, -0.05, -532 + 360)); //ADJUSTMENT TURN LEFT
        waypoints.add(new WaypointDistance(0.6, 0, 16.5)); //FORWARD 
        waypoints.add(new WaypointDistance(0, 0, 0.0)); //STOP


        // waypoints.add(new WaypointDistance(0.3, -0.15, 5.75)); //FORWARD CIRCLE TURN LEFT
        // waypoints.add(new WaypointDistance(0.3, 0, 6.5)); //FORWARD
        // waypoints.add(new WaypointDistance(0.3, -0.15, 3.5)); //FORWARD CIRCLE TURN LEFT
        // waypoints.add(new WaypointDistance(0.4, -0.05, 2.5)); //ADJUSTMENT TURN LEFT
        // waypoints.add(new WaypointDistance(0.4, 0, 15)); //FORWARD 
        // waypoints.add(new WaypointDistance(0, 0, 0.0)); //STOP

    }

    @Override
    public void LogHeader() {

    }

    @Override
    public void LogData() {

    }

}