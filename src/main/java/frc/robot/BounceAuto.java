/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

public class BounceAuto extends WaypointAuto{

    @Override
    public void setWaypoints() {

        Waypoint.scale = 1.0;

        waypoints.add(new WaypointDistance(0.7, 0, 1.5)); //FORWARD
        waypoints.add(new WaypointGyro(0.3 * 2.0, -0.1 * 2.0, -45 + 8)); //QUARTER TURN FORWARD LEFT
        waypoints.add(new WaypointGyro(0.3 * 1.6, -0.1 * 1.6, -90 + 8)); //QUARTER TURN FORWARD LEFT (SLOW DOWN)
        waypoints.add(new WaypointDistance(0.4, 0, 0.6)); //FORWARD TO CONE
        waypoints.add(new WaypointGyro(-0.3 * 2.0, -0.08 * 2.0, -105)); //SMALL TURN BACK RIGHT //1.4
        waypoints.add(new WaypointDistance(-0.7, 0, -4.5)); //BACKWARD 

        waypoints.add(new WaypointGyro(-0.3 * 2.0, -0.14 * 2.0, -246 + 8)); //SEMI TURN BACK RIGHT
        //waypoints.add(new WaypointGyro(-0.3 * 1.2, -0.13 * 1.2, -260 + 8)); //SEMI TURN BACK RIGHT (SLOW DOWN)
        waypoints.add(new WaypointDistance(-0.7, 0, -5.0)); //BACKWARD TO CONE
        waypoints.add(new WaypointDistance(-0.4, 0, -1.0)); //BACKWARD TO CONE (SLOW DOWN)
        waypoints.add(new WaypointDistance(0.4, 0, 0.5)); //FORWARD (START UP)
        waypoints.add(new WaypointDistance(0.7, 0, 4.0)); //FORWARD

        waypoints.add(new WaypointGyro(0.3 * 2.0, -0.1 * 2.0, -360 + 8)); //QUARTER TURN LEFT
        //waypoints.add(new WaypointDistance(0.6, 0, 0.25)); //FORWARD
        waypoints.add(new WaypointGyro(0.3 * 2.0, -0.1 * 2.0, -445 + 8)); //QUARTER TURN LEFT
        waypoints.add(new WaypointDistance(0.7, 0, 4)); //FORWARD TO CONE
        waypoints.add(new WaypointDistance(0.4, 0, 1)); //FORWARD TO CONE (SLOW DOWN)

        waypoints.add(new WaypointDistance(-0.7, 0, -1)); //BACKWARD
        waypoints.add(new WaypointGyro(-0.3 * 2.0, -0.1 * 2.0, -510 + 8)); //QUARTER TURN BACK LEFT
        waypoints.add(new WaypointGyro(-0.3 * 1.6, -0.1 * 1.6, -540 + 8)); //QUARTER TURN BACK LEFT (SLOW DOWN)
        waypoints.add(new WaypointDistance(-0.4, 0, -1)); //BACKWARD
        waypoints.add(new WaypointDistance(0, 0, 0.0)); //STOP


        // waypoints.add(new WaypointDistance(0.4, 0, 2.0)); //FORWARD
        // waypoints.add(new WaypointGyro(0.3, -0.1, -90 + 8)); //QUARTER TURN FORWARD LEFT
        // waypoints.add(new WaypointDistance(0.4, 0, 0.8)); //FORWARD TO CONE
        // waypoints.add(new WaypointGyro(-0.3, -0.07, -115)); //SMALL TURN BACK RIGHT
        // waypoints.add(new WaypointDistance(-0.4, 0, -5.0)); //BACKWARD        
        // waypoints.add(new WaypointGyro(-0.3, -0.13, -266 + 8)); //SEMI TURN BACK RIGHT
        // waypoints.add(new WaypointDistance(-0.4, 0, -6.0)); //FORWARD TO CONE
        // waypoints.add(new WaypointDistance(0.4, 0, 5)); //FORWARD
        // waypoints.add(new WaypointGyro(0.3, -0.1, -360 + 8)); //QUARTER TURN LEFT
        // waypoints.add(new WaypointDistance(0.4, 0, 0.5)); //FORWARD
        // waypoints.add(new WaypointGyro(0.3, -0.1, -450 + 8)); //QUARTER TURN LEFT
        // waypoints.add(new WaypointDistance(0.4, 0, 5.0)); //FORWARD TO CONE
        // waypoints.add(new WaypointGyro(-0.3, -0.1, -540 + 8)); //QUARTER TURN BACK LEFT
        // waypoints.add(new WaypointDistance(-0.4, 0, -1)); //FORWARD
        // waypoints.add(new WaypointDistance(0, 0, 0.0)); //STOP


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

}