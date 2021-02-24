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

public class SlalomAuto implements AutoStateMachines{
    int waypointCounter;
    double originalEncoderPos;
    double currentEncoderPos;
    double gyroAngle;

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
        //Robot.driveController.resetEncoderVal();

        waypoints.add(new WaypointDistance(0.4, 0, 2)); //FORWARD
        waypoints.add(new WaypointGyro(0.3, -0.1, -55)); //TURN LEFT
        waypoints.add(new WaypointDistance(0.4, 0, 1.25)); //FORWARD
        waypoints.add(new WaypointGyro(0.3 * 1.6, 0.0575 * 1.6, 50)); //TURN RIGHT
        //// waypoints.add(new WaypointDistance(0.4, 0, 6.5)); //FORWARD
        //// waypoints.add(new WaypointGyro(0.3, 0.1, 50)); //TURN RIGHT
        waypoints.add(new WaypointDistance(0.4, 0, 4)); //FORWARD
        waypoints.add(new WaypointGyro(0.3, -0.13, -225)); //FULL CIRCLE TURN LEFT
        waypoints.add(new WaypointDistance(0.4, 0, 2)); //FORWARD
        waypoints.add(new WaypointGyro(0.3, 0.1, -180)); //TURN RIGHT
        waypoints.add(new WaypointDistance(0.4, 0, 8.5)); //FORWARD
        waypoints.add(new WaypointGyro(0.3, 0.1, -130)); // TURN RIGHT
        waypoints.add(new WaypointDistance(0.4, 0, 3)); //FORWARD
        waypoints.add(new WaypointGyro(0.3, -0.1, -180 + 8)); //TURN LEFT
        waypoints.add(new WaypointDistance(0, 0, 0)); //STOP


        // //scale = 1.1
        // waypoints.add(new WaypointDistance(0.4, 0, 2)); //FORWARD
        // waypoints.add(new WaypointGyro(0.3, -0.1, -50)); //TURN LEFT
        // waypoints.add(new WaypointDistance(0.4, 0, 1.25)); //FORWARD
        // waypoints.add(new WaypointGyro(0.3, 0.1, -8)); //TURN RIGHT
        // waypoints.add(new WaypointDistance(0.4, 0, 6.5)); //FORWARD
        // waypoints.add(new WaypointGyro(0.3, 0.1, 50)); //TURN RIGHT
        // waypoints.add(new WaypointDistance(0.4, 0, 2)); //FORWARD
        // waypoints.add(new WaypointGyro(0.3, -0.13, -225)); //FULL CIRCLE TURN LEFT
        // waypoints.add(new WaypointDistance(0.4, 0, 2)); //FORWARD
        // waypoints.add(new WaypointGyro(0.3, 0.1, -180)); //TURN RIGHT
        // waypoints.add(new WaypointDistance(0.4, 0, 8.5)); //FORWARD
        // waypoints.add(new WaypointGyro(0.3, 0.1, -130)); // TURN RIGHT
        // waypoints.add(new WaypointDistance(0.4, 0, 3)); //FORWARD
        // waypoints.add(new WaypointGyro(0.3, -0.1, -180 + 8)); //TURN LEFT
        // waypoints.add(new WaypointDistance(0, 0, 0)); //STOP

    }

    @Override
    public void LogHeader() {

    }

    @Override
    public void LogData() {

    }

}