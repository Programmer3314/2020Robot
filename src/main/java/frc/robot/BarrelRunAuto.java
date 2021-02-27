/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

public class BarrelRunAuto extends WaypointAuto{

    @Override
    public void setWaypoints() {

        Waypoint.scale = 1.0;

        waypoints.add(new WaypointDistance(0.7, 0, 8.75)); //FORWARD
        waypoints.add(new WaypointGyro(0.3 * 2.0, 0.14 * 2.0, 337)); //FORWARD CIRCLE TURN RIGHT
        waypoints.add(new WaypointDistance(0.7, 0, 6)); //FORWARD

        waypoints.add(new WaypointGyro(0.3 * 1.8, -0.13 * 1.8, -288 + 360)); //FORWARD CIRCLE TURN LEFT
        waypoints.add(new WaypointDistance(0.7, 0, 5.0)); //FORWARD
        waypoints.add(new WaypointDistance(0.5, 0, 1.5)); //FORWARD (SLOW DOWN)

        waypoints.add(new WaypointGyro(0.3 * 2.0 , -0.15 * 2.0, -495 + 360)); //FORWARD CIRCLE TURN LEFT
        waypoints.add(new WaypointGyro(0.4 * 2.0, 0.05 * 2.0, -540 + 360)); //ADJUSTMENT TURN RIGHT
        
        waypoints.add(new WaypointDistance(1.0, 0, 16.5)); //FORWARD 
        waypoints.add(new WaypointDistance(0, 0, 0.0)); //STOP

        
        // waypoints.add(new WaypointDistance(0.6, 0, 8.75)); //FORWARD
        // waypoints.add(new WaypointGyro(0.3 * 1.2, 0.13 * 1.2, 340)); //FORWARD CIRCLE TURN RIGHT
        // waypoints.add(new WaypointDistance(0.6, 0, 6.5)); //FORWARD
        // waypoints.add(new WaypointGyro(0.3 * 1.2, -0.14 * 1.2, -290 + 360)); //FORWARD CIRCLE TURN LEFT
        // waypoints.add(new WaypointDistance(0.6, 0, 5.75)); //FORWARD
        // waypoints.add(new WaypointGyro(0.3 * 1.2 , -0.15 * 1.2, -500 + 360)); //FORWARD CIRCLE TURN LEFT
        // waypoints.add(new WaypointGyro(0.4, -0.05, -532 + 360)); //ADJUSTMENT TURN LEFT
        // waypoints.add(new WaypointDistance(0.6, 0, 16.5)); //FORWARD 
        // waypoints.add(new WaypointDistance(0, 0, 0.0)); //STOP


        // waypoints.add(new WaypointDistance(0.3, -0.15, 5.75)); //FORWARD CIRCLE TURN LEFT
        // waypoints.add(new WaypointDistance(0.3, 0, 6.5)); //FORWARD
        // waypoints.add(new WaypointDistance(0.3, -0.15, 3.5)); //FORWARD CIRCLE TURN LEFT
        // waypoints.add(new WaypointDistance(0.4, -0.05, 2.5)); //ADJUSTMENT TURN LEFT
        // waypoints.add(new WaypointDistance(0.4, 0, 15)); //FORWARD 
        // waypoints.add(new WaypointDistance(0, 0, 0.0)); //STOP

    }

}